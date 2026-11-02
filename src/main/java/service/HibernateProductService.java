package service;

import entity.Category;
import exception.EntityNotFoundException;
import jpa.JpaUtil;
import product.Product;

import java.util.Comparator;
import java.util.List;

public class HibernateProductService implements ProductService {

    private JpaUtil jpaUtil;

    public HibernateProductService() {
        this.jpaUtil = JpaUtil.getInstance();
    }

    @Override
    public Product findById(long id) {
        return jpaUtil.executeReadOnly(em -> {
            List<Product> result = em.createQuery(
                            "SELECT p FROM Product p LEFT JOIN FETCH p.categoryEntity WHERE p.id = :id",
                            Product.class
                    )
                    .setParameter("id", id)
                    .getResultList();

            if (result.isEmpty()) {
                throw new EntityNotFoundException(id);
            }
            return result.getFirst();
        });
    }

    @Override
    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Товар не може бути null");
        }

        jpaUtil.executeInTransaction(em -> {
            Category resolvedCategory = resolveCategory(em, product);
            product.setCategoryEntity(resolvedCategory);

            if (product.getIdWrapper() == null) {
                em.persist(product);
            } else {
                Product existing = em.find(Product.class, product.getId());
                if (existing == null) {
                    throw new EntityNotFoundException(product.getId());
                }
                existing.setName(product.getName());
                existing.setPriceDecimal(product.getPriceDecimal());
                existing.setQuantity(product.getQuantity());
                existing.setCategoryEntity(resolvedCategory);
            }
        });
    }

    private Category resolveCategory(jakarta.persistence.EntityManager em, Product product) {
        if (product.getCategoryEntity() != null && product.getCategoryEntity().getIdWrapper() != null) {
            long catId = product.getCategoryEntity().getId();
            Category cat = em.find(Category.class, catId);
            if (cat == null) {
                throw new EntityNotFoundException(catId);
            }
            return cat;
        }
        long fallbackCategoryId = product.getCategoryId();
        if (fallbackCategoryId > 0) {
            Category cat = em.find(Category.class, fallbackCategoryId);
            if (cat == null) {
                throw new EntityNotFoundException(fallbackCategoryId);
            }
            return cat;
        }
        throw new IllegalArgumentException("Для збереження товару необхідно вказати існуючу категорію");
    }

    @Override
    public boolean delete(long id) {
        boolean[] deleted = {false};
        jpaUtil.executeInTransaction(em -> {
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
                deleted[0] = true;
            }
        });
        return deleted[0];
    }

    public List<Product> findAll() {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery(
                                "SELECT p FROM Product p LEFT JOIN FETCH p.categoryEntity ORDER BY p.id ASC",
                                Product.class
                        )
                        .getResultList()
        );
    }

    @Override
    public List<Product> findAllSortedByName() {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery(
                                "SELECT p FROM Product p LEFT JOIN FETCH p.categoryEntity ORDER BY p.name ASC",
                                Product.class
                        )
                        .getResultList()
        );
    }

    @Override
    public List<Product> findAllSortedByNameAndByPrice() {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery(
                                "SELECT p FROM Product p LEFT JOIN FETCH p.categoryEntity ORDER BY p.name ASC, p.price ASC",
                                Product.class
                        )
                        .getResultList()
        );
    }

    @Override
    public List<Product> findAllSorted(Comparator<? super Product> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Компаратор не може бути null");
        }
        List<Product> list = findAll();
        list.sort(comparator);
        return list;
    }

    public List<Product> findByCategoryName(String categoryName) {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery(
                                "SELECT p FROM Product p JOIN FETCH p.categoryEntity c WHERE c.name = :categoryName ORDER BY p.name ASC",
                                Product.class
                        )
                        .setParameter("categoryName", categoryName)
                        .getResultList()
        );
    }

    public List<Product> findPage(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Номер сторінки та розмір мають бути більшими за 0");
        }
        int firstResult = Math.toIntExact(((long) page - 1) * size);
        return jpaUtil.executeReadOnly(em ->
                em.createQuery(
                                "SELECT p FROM Product p LEFT JOIN FETCH p.categoryEntity ORDER BY p.id ASC",
                                Product.class
                        )
                        .setFirstResult(firstResult)
                        .setMaxResults(size)
                        .getResultList()
        );
    }
}
