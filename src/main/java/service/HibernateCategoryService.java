package service;

import entity.Category;
import exception.EntityNotFoundException;
import jpa.JpaUtil;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HibernateCategoryService implements CRUDService<Category> {

    private JpaUtil jpaUtil;

    public HibernateCategoryService() {
        this.jpaUtil = JpaUtil.getInstance();
    }

    @Override
    public Category findById(long id) {
        return jpaUtil.executeReadOnly(em -> {
            Category category = em.find(Category.class, id);
            if (category == null) {
                throw new EntityNotFoundException(id);
            }
            return category;
        });
    }

    public Category findByIdWithProducts(long id) {
        return jpaUtil.executeReadOnly(em -> {
            List<Category> result = em.createQuery(
                            "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id",
                            Category.class
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
    public void save(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Категорія не може бути null");
        }

        jpaUtil.executeInTransaction(em -> {
            if (category.getIdWrapper() == null) {
                em.persist(category);
            } else {
                Category existing = em.find(Category.class, category.getId());
                if (existing == null) {
                    throw new EntityNotFoundException(category.getId());
                }
                existing.setName(category.getName());
                existing.setDescription(category.getDescription());
            }
        });
    }

    @Override
    public boolean delete(long id) {
        boolean[] deleted = {false};
        jpaUtil.executeInTransaction(em -> {
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
                deleted[0] = true;
            }
        });
        return deleted[0];
    }

    public void deleteOrThrow(long id) {
        jpaUtil.executeInTransaction(em -> {
            Category category = em.find(Category.class, id);
            if (category == null) {
                throw new EntityNotFoundException(id);
            }
            em.remove(category);
        });
    }

    public List<Category> findAll() {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery("SELECT c FROM Category c ORDER BY c.id ASC", Category.class)
                        .getResultList()
        );
    }

    @Override
    public List<Category> findAllSortedByName() {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery("SELECT c FROM Category c ORDER BY c.name ASC", Category.class)
                        .getResultList()
        );
    }

    @Override
    public List<Category> findAllSorted(Comparator<? super Category> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Компаратор не може бути null");
        }
        List<Category> list = findAll();
        list.sort(comparator);
        return list;
    }

    public long count() {
        return jpaUtil.executeReadOnly(em ->
                em.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                        .getSingleResult()
        );
    }

    public Map<String, Integer> countProductsPerCategoryWithNPlusOne() {
        return jpaUtil.executeReadOnly(em -> {
            List<Category> categories = em.createQuery(
                            "SELECT c FROM Category c ORDER BY c.id ASC",
                            Category.class
                    )
                    .getResultList();

            Map<String, Integer> counts = new LinkedHashMap<>();
            for (Category c : categories) {
                counts.put(c.getName(), c.getProducts().size());
            }
            return counts;
        });
    }

    public Map<String, Integer> countProductsPerCategoryWithJoinFetch() {
        return jpaUtil.executeReadOnly(em -> {
            List<Category> categories = em.createQuery(
                            "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products ORDER BY c.id ASC",
                            Category.class
                    )
                    .getResultList();

            Map<String, Integer> counts = new LinkedHashMap<>();
            for (Category c : categories) {
                counts.put(c.getName(), c.getProducts().size());
            }
            return counts;
        });
    }
}
