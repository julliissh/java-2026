import entity.Category;
import exception.EntityNotFoundException;
import jpa.JpaUtil;
import org.hibernate.LazyInitializationException;
import org.hibernate.stat.Statistics;
import product.Product;
import service.DatabaseInitializer;
import service.HibernateCategoryService;
import service.HibernateProductService;

import java.util.List;
import java.util.Map;

public class CrudDemo {

    public static void main(String[] args) {
        DatabaseInitializer.init();

        HibernateCategoryService categoryService = new HibernateCategoryService();
        HibernateProductService productService = new HibernateProductService();

        try {
            clearDb();

            testCrud(categoryService, productService);
            testRelations();
            testJpql(productService);
            testLazy(categoryService);
            testNPlusOne(categoryService);
        } finally {
            JpaUtil.getInstance().close();
            jdbc.DBConnection.getInstance().close();
        }
    }

    private static void clearDb() {
        JpaUtil.getInstance().executeInTransaction(em -> {
            em.createQuery("DELETE FROM Product").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();
        });
    }

    private static void testCrud(HibernateCategoryService categoryService, HibernateProductService productService) {
        System.out.println("\n--- 1. Тест CRUD ---");

        Category electronics = new Category("Електроніка", "Техніка та гаджети");
        categoryService.save(electronics);

        Product keyboard = new Product("Клавіатура", 1200.0, 5, electronics);
        productService.save(keyboard);
        System.out.println("Створено: " + keyboard);

        Product loaded = productService.findById(keyboard.getId());
        System.out.println("Знайдено: " + loaded);

        loaded.setName("Клавіатура з підсвіткою");
        loaded.setPrice(1400.0);
        productService.save(loaded);
        System.out.println("Оновлено: " + productService.findById(loaded.getId()));

        System.out.println("Сортування за назвою:");
        for (Product p : productService.findAllSortedByName()) {
            System.out.println("  " + p);
        }

        productService.delete(loaded.getId());
        System.out.println("Видалено товар id=" + loaded.getId());

        try {
            productService.findById(loaded.getId());
        } catch (EntityNotFoundException e) {
            System.out.println("Спіймано очікуваний виняток: " + e.getMessage());
        }
    }

    private static void testRelations() {
        System.out.println("\n--- 2. Тест зв'язків @OneToMany / @ManyToOne та cascade ---");

        JpaUtil.getInstance().executeInTransaction(em -> {
            Category books = new Category("Книги", "Література");
            Product book1 = new Product("Java Persistence", 900.0, 10, books);
            Product book2 = new Product("Чистий код", 750.0, 8, books);

            books.addProduct(book1);
            books.addProduct(book2);

            em.persist(books);
            System.out.println("Категорію 'Книги' та товари збережено каскадно!");
        });
    }

    private static void testJpql(HibernateProductService productService) {
        System.out.println("\n--- 3. Тест JPQL та пагінації ---");

        System.out.println("Сортування за назвою та ціною:");
        for (Product p : productService.findAllSortedByNameAndByPrice()) {
            System.out.println("  " + p);
        }

        System.out.println("Пошук за категорією 'Книги':");
        for (Product p : productService.findByCategoryName("Книги")) {
            System.out.println("  " + p);
        }

        System.out.println("Пагінація (сторінка 1, розмір 1):");
        List<Product> page = productService.findPage(1, 1);
        for (Product p : page) {
            System.out.println("  " + p);
        }
    }

    private static void testLazy(HibernateCategoryService categoryService) {
        System.out.println("\n--- 4. Тест LazyInitializationException ---");
        long categoryId = categoryService.findAll().getFirst().getId();

        Category category = categoryService.findById(categoryId);
        try {
            System.out.println("Кількість товарів поза сесією: " + category.getProducts().size());
        } catch (LazyInitializationException e) {
            System.out.println("Спіймано очікуваний LazyInitializationException: " + e.getMessage());
        }

        Category fixedCategory = categoryService.findByIdWithProducts(categoryId);
        System.out.println("Виправлено через JOIN FETCH! Товарів: " + fixedCategory.getProducts().size());
    }

    private static void testNPlusOne(HibernateCategoryService categoryService) {
        System.out.println("\n--- 5. Тест проблеми N+1 ---");
        Statistics stats = JpaUtil.getInstance().getStatistics();

        stats.clear();
        Map<String, Integer> countsBefore = categoryService.countProductsPerCategoryWithNPlusOne();
        System.out.println("До виправлення (N+1): виконано SQL-запитів = " + stats.getPrepareStatementCount());

        stats.clear();
        Map<String, Integer> countsAfter = categoryService.countProductsPerCategoryWithJoinFetch();
        System.out.println("Після виправлення (JOIN FETCH): виконано SQL-запитів = " + stats.getPrepareStatementCount());
    }
}
