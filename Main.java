import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    void main() {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);

        // =====================================================================
        // Рівень 1: Блок try-finally — Scanner завжди закривається через finally
        // =====================================================================
        try {
            System.out.printf("%n%50s%n", "ІНТЕРНЕТ-МАГАЗИН:");
            System.out.printf("%-50s%n%n", "=".repeat(50));

            // -----------------------------------------------------------------
            // 1. ДАНІ КЛІЄНТА
            // -----------------------------------------------------------------
            System.out.println("--- 1. ДАНІ КЛІЄНТА ---");
            Customer customer = readCustomer(scanner);
            System.out.println("Поточний клієнт: " + customer);
            System.out.println();

            // -----------------------------------------------------------------
            // 2. ФОРМУВАННЯ КОШИКА ТОВАРІВ
            // -----------------------------------------------------------------
            System.out.println("--- 2. ФОРМУВАННЯ КОШИКА ТОВАРІВ ---");
            int count = readProductCount(scanner);

            Product[] products = new Product[count];

            // Рівень 3: Зовнішній try-catch — ловить re-thrown ShopException
            try {
                for (int i = 0; i < count; i++) {
                    products[i] = readProduct(scanner, i + 1);
                }
            } catch (ShopException e) {
                // Сюди потрапляємо лише якщо readProduct кинув re-throw
                System.out.println("\n[КРИТИЧНА ПОМИЛКА] Введення товару перервано: " + e.getMessage());
                System.out.println("Програму завершено. Перезапустіть та введіть коректні дані.");
                return; // завершення роботи — finally все одно виконається
            }

            System.out.println();
            System.out.println("--- СПИСОК ТОВАРІВ У КОШИКУ КЛІЄНТА "
                    + customer.getName() + " (" + customer.getPhone() + ") ---");
            printProducts(products);

            // -----------------------------------------------------------------
            // 3. ПІДСУМОК: ФІЛЬТР ЗА БЮДЖЕТОМ
            // -----------------------------------------------------------------
            System.out.println();
            System.out.println("--- ПІДСУМОК: ФІЛЬТР ТОВАРІВ ЗА БЮДЖЕТОМ ---");
            double limitPrice = readBudgetLimit(scanner);

            int cheaperCount = 0;
            for (Product product : products) {
                if (product.getPrice() < limitPrice) {
                    cheaperCount++;
                }
            }
            System.out.println("Кількість товарів, дешевших за " + limitPrice + " грн: " + cheaperCount + " шт.");

            int totalQuantity = 0;
            for (Product product : products) {
                totalQuantity += product.getQuantity();
            }
            System.out.println("Загальна кількість одиниць товару в кошику: " + totalQuantity + " шт.");
            System.out.println();

            // -----------------------------------------------------------------
            // 4. СОРТУВАННЯ (Bubble Sort)
            // -----------------------------------------------------------------
            System.out.println("--- 3. СОРТУВАННЯ ТОВАРІВ ЗА ЦІНОЮ ---");
            System.out.println("Масив ДО сортування:");
            printProducts(products);

            bubbleSortByPrice(products);

            System.out.println();
            System.out.println("Масив ПІСЛЯ Bubble Sort (за зростанням ціни):");
            printProducts(products);
            System.out.println();

            // -----------------------------------------------------------------
            // 5. ЛІНІЙНИЙ ПОШУК ЗА ЗРАЗКОМ
            // -----------------------------------------------------------------
            System.out.println("--- 4. ЛІНІЙНИЙ ПОШУК ТОВАРУ ЗА ЗРАЗКОМ ---");
            System.out.println("Введіть дані товару-зразка для пошуку (мають збігатися всі поля):");

            Product sampleProduct = readProduct(scanner, -1); // -1 = режим зразка, не re-throw

            int foundIndex = findProduct(products, sampleProduct);

            if (foundIndex != -1) {
                System.out.println();
                System.out.printf("Знайдено товар \"%s\" за індексом [%d]:%n",
                        products[foundIndex].getName(), foundIndex);
                System.out.println(products[foundIndex]);
            } else {
                System.out.println();
                System.out.println("Товар-зразок не знайдено у списку.");
            }

            System.out.println();
            System.out.println("Роботу програми успішно завершено!");

        } catch (Exception e) {
            // Рівень 1: catch(Exception e) — ловить будь-яку непередбачену помилку
            System.out.println("\n[Непередбачена помилка] " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Рівень 1: finally — Scanner закривається ЗАВЖДИ (навіть при помилці)
            System.out.println("\n(finally) Закриття ресурсів...");
            scanner.close();
        }
    }

    // =========================================================================
    // Допоміжний метод: зчитування даних клієнта з валідацією
    // =========================================================================
    private static Customer readCustomer(Scanner scanner) throws EmptyNameException {
        Customer customer = null;
        while (customer == null) {
            try {
                System.out.print("Введіть ім'я клієнта: ");
                String customerName = scanner.nextLine();

                System.out.print("Введіть номер телефону: ");
                String customerPhone = scanner.nextLine();

                // Рівень 2: конструктор Customer кидає EmptyNameException
                customer = new Customer(customerName, customerPhone);

            } catch (EmptyNameException e) {
                // Рівень 2: перехоплюємо і просимо ввести знову
                System.out.println("  [Помилка] " + e.getMessage() + " Спробуйте ще раз.\n");
            }
        }
        return customer;
    }

    // =========================================================================
    // Допоміжний метод: зчитування кількості товарів з валідацією
    // =========================================================================
    private static int readProductCount(Scanner scanner) {
        int count = 0;
        while (count <= 0) {
            try {
                System.out.print("Введіть кількість товарів для додавання: ");
                count = scanner.nextInt();
                scanner.nextLine();
                // Рівень 1: власна перевірка через IllegalArgumentException
                if (count <= 0) {
                    throw new IllegalArgumentException("Кількість має бути > 0, отримано: " + count);
                }
            } catch (InputMismatchException e) {
                // Рівень 1: введено не число
                System.out.println("  [Помилка] Введіть ціле число.");
                scanner.nextLine(); // очищення буфера
                count = 0;
            } catch (IllegalArgumentException e) {
                // Рівень 1: число є, але некоректне
                System.out.println("  [Помилка] " + e.getMessage());
                count = 0;
            }
        }
        return count;
    }

    // =========================================================================
    // Допоміжний метод: зчитування бюджетного ліміту з валідацією
    // =========================================================================
    private static double readBudgetLimit(Scanner scanner) {
        double limit = -1;
        while (limit <= 0) {
            try {
                System.out.print("Шукати товари, дешевші за (введіть суму в грн): ");
                limit = scanner.nextDouble();
                scanner.nextLine();
                if (limit <= 0) {
                    throw new IllegalArgumentException("Сума має бути > 0, отримано: " + limit);
                }
            } catch (InputMismatchException e) {
                System.out.println("  [Помилка] Введіть числове значення.");
                scanner.nextLine();
                limit = -1;
            } catch (IllegalArgumentException e) {
                System.out.println("  [Помилка] " + e.getMessage());
                limit = -1;
            }
        }
        return limit;
    }

    // =========================================================================
    // Допоміжний метод: зчитування одного товару з валідацією
    //
    // productNumber > 0  → режим додавання: при InvalidPrice/InvalidQuantity
    //                      виконується re-throw (летить у зовнішній catch)
    // productNumber == -1 → режим зразка: повторюємо введення без re-throw
    // =========================================================================
    private static Product readProduct(Scanner scanner, int productNumber)
            throws ShopException {

        String label = (productNumber > 0) ? "Введення товару №" + productNumber + ":" : "Введення товару-зразка:";
        Product product = null;

        while (product == null) {
            System.out.println();
            System.out.println(label);

            // Рівень 1: зчитування рядків — помилки малоймовірні, але захищаємо
            System.out.print("Назва товару: ");
            String name = scanner.nextLine();

            System.out.print("Категорія: ");
            String category = scanner.nextLine();

            // Рівень 1: зчитування числових полів з InputMismatchException
            double price = readDouble(scanner, "Ціна (грн): ");
            int quantity = readInt(scanner, "Кількість (шт): ");

            // Рівень 2 + 3: кілька catch для одного try
            try {
                product = new Product(name, category, price, quantity);

            } catch (InvalidPriceException e) {
                // Рівень 3: RE-THROW конкретного підкласу
                // При додаванні товару — логуємо і кидаємо далі (зупиняє процес для демонстрації re-throw)
                // При введенні зразка — просто повідомляємо і повторюємо
                if (productNumber > 0) {
                    System.out.println("  [LOG] Помилка ціни товару (re-throw): " + e.getMessage());
                    throw e; // re-throw → летить у зовнішній catch(ShopException e) у main()
                } else {
                    System.out.println("  [Помилка] " + e.getMessage() + " Спробуйте ще раз.");
                }

            } catch (EmptyNameException e) {
                // Рівень 2+3: EmptyNameException — окрема гілка ієрархії (не ShopException)
                // catch(ShopException) не спіймав би це — тому тут окремий catch
                System.out.println("  [Помилка] " + e.getMessage() + " Спробуйте ще раз.");

            } catch (ShopException e) {
                // Рівень 3: Базовий тип ієрархії — ловить інші підкласи ShopException
                // (зокрема InvalidQuantityException, демонструючи поліморфне перехоплення суперкласом)
                System.out.println("  [Помилка магазину (ShopException)] " + e.getMessage() + " Спробуйте ще раз.");

            } catch (Exception e) {
                // Рівень 1: catch(Exception e) — непередбачувана помилка
                System.out.println("  [Непередбачена помилка] " + e.getMessage());
                e.printStackTrace();
            }
        }
        return product;
    }

    // =========================================================================
    // Допоміжні методи: безпечне зчитування чисел (Рівень 1)
    // =========================================================================
    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  [Помилка] Введіть числове значення (наприклад: 450.0).");
                scanner.nextLine();
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  [Помилка] Введіть ціле число (наприклад: 2).");
                scanner.nextLine();
            }
        }
    }

    // =========================================================================
    // Допоміжні методи: вивід, сортування, пошук (з ЛР1/ЛР2)
    // =========================================================================
    public static void printProducts(Product[] products) {
        for (Product product : products) {
            System.out.println(product);
        }
    }

    public static void bubbleSortByPrice(Product[] products) {
        for (int i = 0; i < products.length - 1; i++) {
            for (int j = 0; j < products.length - 1 - i; j++) {
                if (products[j].getPrice() > products[j + 1].getPrice()) {
                    Product temp = products[j];
                    products[j] = products[j + 1];
                    products[j + 1] = temp;
                }
            }
        }
    }

    public static int findProduct(Product[] products, Product target) {
        for (int i = 0; i < products.length; i++) {
            if (products[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}