import entity.Category;
import exception.EntityNotFoundException;
import jdbc.DBConnection;
import product.Clothing;
import product.Electronics;
import product.Product;
import service.InMemoryCrudService;
import service.InMemoryProductService;
import service.JdbcCategoryService;
import service.JdbcProductService;
import service.ProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudDemo {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  ДЕМОНСТРАЦІЯ ЛР №10: JDBC, HikariCP, Transactions, Batch & Pagination");
        System.out.println("  Середовище: Тестова база даних PostgreSQL (shopdb)");
        System.out.println("================================================================================");

        service.DatabaseInitializer.init();
        System.out.println("-> Ініціалізація БД (init.sql) успішна!");

        JdbcCategoryService categoryService = new JdbcCategoryService();
        JdbcProductService jdbcService = new JdbcProductService();

        try {
            System.out.println("-> Підготовка тестового середовища (очищення тестових записів у shopdb)...");
            cleanupDemoData(jdbcService, categoryService);
            try {
                categoryService.delete(1);
            } catch (Exception ignored) {
            }

            demonstrateCrud(jdbcService, categoryService);
            demonstrateTransactions(jdbcService, categoryService);
            demonstrateSqlInjection(jdbcService);
            demonstrateBatchOperations(jdbcService);
            demonstratePagination(jdbcService);
            demonstratePolymorphism(jdbcService);
            demonstrateCustomerService();
        } finally {
            System.out.println("\n--- Завершення роботи ---");
            DBConnection.getInstance().close();
            System.out.println("-> Демонстраційні ресурси закрито, пул з'єднань HikariCP зупинено.");
            System.out.println("-> Дані збережено в базі shopdb для перегляду в pgAdmin.");
        }
    }

    private static void demonstrateCrud(JdbcProductService jdbcService, JdbcCategoryService categoryService) {
        System.out.println("\n=== 0. Тестування CRUD та сортування JdbcProductService (Рівні 1-2) ===");
        categoryService.save(new Category(1, "Електроніка", "Гаджети та техніка"));

        Product prod1 = new Product(101, "Клавіатура", 1200.0, 5, 1);
        Product prod2 = new Product(102, "Мишка", 650.0, 10, 1);
        jdbcService.save(prod1);
        jdbcService.save(prod2);
        System.out.println("Товарів у БД: " + jdbcService.count());
        System.out.println("Знайдено по id 101: " + jdbcService.findById(101));

        Product prod1Updated = new Product(101, "Клавіатура з підсвіткою", 1350.0, 7, 1);
        jdbcService.save(prod1Updated);
        System.out.println("Оновлено товар 101: " + jdbcService.findById(101));

        try {
            jdbcService.findById(9999);
            throw new IllegalStateException("Помилка: findById(9999) повинен був кинути EntityNotFoundException!");
        } catch (EntityNotFoundException e) {
            System.out.println("Спіймано EntityNotFoundException для id 9999: " + e.getMessage());
        }

        System.out.println("Сортування за назвою (findAllSortedByName):");
        for (Product p : jdbcService.findAllSortedByName()) {
            System.out.println("  " + p);
        }

        Product prod3 = new Product(103, "Клавіатура з підсвіткою", 950.0, 3, 1);
        jdbcService.save(prod3);
        System.out.println("Сортування за назвою та ціною (findAllSortedByNameAndByPrice):");
        for (Product p : jdbcService.findAllSortedByNameAndByPrice()) {
            System.out.println("  " + p);
        }

        boolean restrictTriggered = false;
        try {
            System.out.println("Спроба видалити категорію 1 з наявними товарами (ON DELETE RESTRICT):");
            categoryService.delete(1);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException sqlEx && "23503".equals(sqlEx.getSQLState())) {
                restrictTriggered = true;
                System.out.println("Успішно спрацювало обмеження зовнішнього ключа ON DELETE RESTRICT (SQLState: 23503):");
                System.out.println("  " + sqlEx.getMessage().trim());
            } else {
                throw new IllegalStateException("Очікувалася помилка зовнішнього ключа (SQLState 23503), але отримано: " + e.getMessage(), e);
            }
        }
        if (!restrictTriggered) {
            throw new IllegalStateException("Критична помилка: категорію 1 видалено попри наявність прив'язаних товарів! Обмеження ON DELETE RESTRICT не спрацювало.");
        }
    }

    private static void demonstrateTransactions(JdbcProductService jdbcService, JdbcCategoryService categoryService) {
        System.out.println("\n--- Демонстрація успішної транзакції ---");
        Category newCat = new Category(2, "Книги", "Література");
        List<Product> books = List.of(
                new Product(201, "Підручник з Java", 450.0, 5, 2),
                new Product(202, "Чистий код", 600.0, 4, 2)
        );
        jdbcService.createCategoryWithProducts(newCat, books);
        System.out.println("Успішна транзакція: категорію 'Книги' та товари додано в одній транзакції!");
        System.out.println("Категорій після транзакції: " + categoryService.findAll().size());

        System.out.println("\n--- Демонстрація відкату транзакції (rollback) ---");
        System.out.println("Категорій ДО невдалої транзакції: " + categoryService.findAll().size());
        System.out.println("Товарів ДО невдалої транзакції: " + jdbcService.count());

        Category badCat = new Category(3, "Одяг", "Сезонний одяг");
        List<Product> badProducts = List.of(
                new Product(401, "Куртка", 2500.0, 2, 3),
                new Product(101, "Конфліктний товар", 500.0, 1, 3)
        );

        boolean rollbackHappened = false;
        try {
            jdbcService.createCategoryWithProducts(badCat, badProducts);
        } catch (RuntimeException e) {
            rollbackHappened = true;
            System.out.println("Спіймано очікуваний виняток транзакції: " + e.getMessage());
        }

        if (!rollbackHappened) {
            throw new IllegalStateException("Критична помилка: транзакція мала впасти через конфлікт первинного ключа id=101, але виконалася успішно!");
        }

        System.out.println("Категорій ПІСЛЯ відкату (rollback): " + categoryService.findAll().size());
        System.out.println("Товарів ПІСЛЯ відкату (rollback): " + jdbcService.count());

        try {
            categoryService.findById(3);
            throw new IllegalStateException("Критична помилка: категорія 3 збереглася в БД після збою транзакції (rollback не спрацював)!");
        } catch (EntityNotFoundException e) {
            System.out.println("Rollback підтверджено: категорія 3 відсутня в БД (не збереглася)");
        }

        try {
            jdbcService.findById(401);
            throw new IllegalStateException("Критична помилка: перший товар транзакції (id=401) зберігся в БД після збою (rollback не спрацював)!");
        } catch (EntityNotFoundException e) {
            System.out.println("Rollback підтверджено: перший товар транзакції (id=401) також відсутній в БД (успішний відкат)");
        }
    }

    private static void demonstrateSqlInjection(JdbcProductService jdbcService) {
        System.out.println("\n--- Захист від SQL-ін'єкцій ---");
        String attack = "' OR '1'='1";
        System.out.println("Пошук '" + attack + "' через PreparedStatement (безпечно): " + jdbcService.findByName(attack).size() + " записів");

        System.out.println("Видалення 102 (успішне): " + jdbcService.delete(102));
        System.out.println("Видалення 102 (повторне): " + jdbcService.delete(102));
        System.out.println("Кількість після видалення: " + jdbcService.count());
    }

    private static void demonstrateBatchOperations(JdbcProductService jdbcService) {
        System.out.println("\n--- Демонстрація пакетної вставки (Batch vs Single) ---");
        List<Product> list1 = new ArrayList<>();
        List<Product> list2 = new ArrayList<>();
        List<Long> benchmarkIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            long idA = 10000 + i;
            long idB = 20000 + i;
            benchmarkIds.add(idA);
            benchmarkIds.add(idB);
            list1.add(new Product(idA, "Товар A" + i, 100.0 + i, 5, 1));
            list2.add(new Product(idB, "Товар B" + i, 100.0 + i, 5, 1));
        }

        try {
            for (long id : benchmarkIds) {
                jdbcService.delete(id);
            }

            long start = System.nanoTime();
            for (Product product : list1) {
                jdbcService.save(product);
            }
            long oneByOneMs = (System.nanoTime() - start) / 1_000_000;

            start = System.nanoTime();
            jdbcService.insertBatch(list2);
            long batchMs = (System.nanoTime() - start) / 1_000_000;

            System.out.println("По одному (1000 записів): " + oneByOneMs + " мс");
            System.out.println("Пакетно   (1000 записів): " + batchMs + " мс");
        } finally {
            for (long id : benchmarkIds) {
                jdbcService.delete(id);
            }
            System.out.println("Видалення тестових записів бенчмарку гарантовано виконано у finally.");
        }
    }

    private static void demonstratePagination(JdbcProductService jdbcService) {
        System.out.println("\n--- Демонстрація пагінації (findPage) ---");
        System.out.println("Сторінка 1 (розмір 2):");
        for (Product p : jdbcService.findPage(1, 2)) {
            System.out.println("  " + p);
        }
        System.out.println("Сторінка 2 (розмір 2):");
        for (Product p : jdbcService.findPage(2, 2)) {
            System.out.println("  " + p);
        }
    }

    private static void demonstratePolymorphism(JdbcProductService jdbcService) {
        System.out.println("\n=== 1. Поліморфізм інтерфейсу ProductService (Рівень 1) ===");
        InMemoryProductService inMemoryProductService = new InMemoryProductService();
        inMemoryProductService.save(new Clothing(1, "Футболка", 550.0, 10, "M", "Бавовна"));
        inMemoryProductService.save(new Electronics(2, "Ноутбук", 35000.0, 3, 24));
        inMemoryProductService.save(new Clothing(3, "Футболка", 450.0, 15, "L", "Поліестер"));

        System.out.println("Товарів в InMemoryProductService (ОЗП): " + inMemoryProductService.count());
        System.out.println("Товарів у JdbcProductService (БД):      " + jdbcService.count());

        System.out.println("\nВзаємозамінність через спільний інтерфейс ProductService:");
        showServicePolymorphism(jdbcService, "JdbcProductService (PostgreSQL)");
        showServicePolymorphism(inMemoryProductService, "InMemoryProductService (ОЗП)");

        System.out.println("\nСортування за назвою через ProductService.findAllSortedByName():");
        List<String> pgNames = new ArrayList<>();
        for (Product p : jdbcService.findAllSortedByName()) {
            pgNames.add(p.getName());
        }
        List<String> memNames = new ArrayList<>();
        for (Product p : inMemoryProductService.findAllSortedByName()) {
            memNames.add(p.getName());
        }
        System.out.println("  PostgreSQL: " + pgNames);
        System.out.println("  ОЗП (RAM):  " + memNames);
    }

    private static void demonstrateCustomerService() {
        System.out.println("\n=== 2. Робота клієнтського сервісу (Customer) ===");
        InMemoryCrudService<Customer> customerService = new InMemoryCrudService<>();
        customerService.save(new Customer(1, "Олена Коваль", "0991234567"));
        customerService.save(new Customer(2, "Богдан Шевченко", "0671234567"));
        System.out.println("Клієнтів збережено в пам'яті: " + customerService.count());

        List<String> customerNames = new ArrayList<>();
        for (Customer c : customerService.findAllSortedByName()) {
            customerNames.add(c.getName());
        }
        System.out.println("Клієнти, відсортовані за іменем: " + customerNames);
    }

    private static void cleanupDemoData(JdbcProductService productService, JdbcCategoryService categoryService) {
        List<Long> demoProductIds = List.of(101L, 102L, 103L, 201L, 202L, 401L);
        for (long id : demoProductIds) {
            productService.delete(id);
        }

        for (long catId : List.of(2L, 3L)) {
            try {
                categoryService.delete(catId);
            } catch (Exception e) {
                System.out.println("  [cleanup] Інформація щодо категорії " + catId + ": " + e.getMessage());
            }
        }
    }

    private static void showServicePolymorphism(ProductService s, String implementationName) {
        String first = s.findAllSortedByName().isEmpty() ? "немає" : s.findAllSortedByName().get(0).getName();
        System.out.println("-> " + implementationName + " | Перший за назвою: " + first);
    }
}
