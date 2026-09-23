import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println();
            System.out.println("                 ІНТЕРНЕТ-МАГАЗИН:                ");
            System.out.println("==================================================");
            System.out.println();

            // 1. ДАНІ КЛІЄНТА (перевірка на порожні поля)
            System.out.println("--- 1. ДАНІ КЛІЄНТА ---");
            Customer customer = null;
            while (customer == null) {
                try {
                    System.out.print("Введіть ім'я клієнта: ");
                    String customerName = scanner.nextLine().trim();

                    System.out.print("Введіть номер телефону: ");
                    String customerPhone = scanner.nextLine().trim();

                    customer = new Customer(customerName, customerPhone);
                } catch (EmptyNameException e) {
                    System.out.println("Помилка: " + e.getMessage() + " Спробуйте ще раз.\n");
                }
            }
            System.out.println("Поточний клієнт: " + customer);
            System.out.println();

            // 2. ФОРМУВАННЯ КОШИКА ТОВАРІВ
            System.out.println("--- 2. ФОРМУВАННЯ КОШИКА ТОВАРІВ ---");
            int count = 0;
            while (count <= 0) {
                try {
                    System.out.print("Введіть кількість товарів для додавання: ");
                    count = Integer.parseInt(scanner.nextLine().trim());

                    if (count <= 0) {
                        throw new IllegalArgumentException("Кількість товарів має бути більшою за 0!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Помилка: потрібно ввести ціле число!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Помилка: " + e.getMessage());
                }
            }

            Product[] products = new Product[count];
            for (int i = 0; i < count; i++) {
                boolean added = false;
                while (!added) {
                    System.out.println("\nВведення товару №" + (i + 1) + ":");
                    try {
                        System.out.print("Назва товару: ");
                        String name = scanner.nextLine().trim();

                        System.out.print("Категорія: ");
                        String category = scanner.nextLine().trim();

                        System.out.print("Ціна (грн): ");
                        double price = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));

                        System.out.print("Кількість (шт): ");
                        int quantity = Integer.parseInt(scanner.nextLine().trim());

                        // Створення товару через метод з re-throw
                        products[i] = createProduct(name, category, price, quantity);
                        added = true;
                        System.out.println("Товар успішно додано!");

                    } catch (NumberFormatException e) {
                        // Перехоплення нечислового введення
                        System.out.println("Помилка: ціна та кількість повинні бути коректними числами!");
                    } catch (InvalidPriceException e) {
                        // Перехоплення конкретного підкласу після re-throw
                        System.out.println("Помилка: " + e.getMessage());
                    } catch (EmptyNameException e) {
                        // Перехоплення окремої гілки виключень (порожнє поле)
                        System.out.println("Помилка: " + e.getMessage());
                    } catch (ShopException e) {
                        // Поліморфний catch: базовий клас перехоплює InvalidQuantityException
                        System.out.println("Помилка магазину: " + e.getMessage());
                    }
                }
            }

            // Виведення списку товарів (форматований вивід через toString)
            System.out.println();
            System.out.println("--- СПИСОК ТОВАРІВ У КОШИКУ КЛІЄНТА " + customer.getName() + " (" + customer.getPhone() + ") ---");
            printProducts(products);

            // 3. ПІДСУМОК: ФІЛЬТР ЗА БЮДЖЕТОМ
            System.out.println();
            System.out.println("--- ПІДСУМОК: ФІЛЬТР ТОВАРІВ ЗА БЮДЖЕТОМ ---");
            double limitPrice = 0;
            while (limitPrice <= 0) {
                try {
                    System.out.print("Шукати товари, дешевші за (введіть суму в грн): ");
                    limitPrice = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
                    if (limitPrice <= 0) {
                        throw new IllegalArgumentException("Сума повинна бути більшою за 0!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Помилка: потрібно ввести числове значення!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Помилка: " + e.getMessage());
                }
            }

            int cheaperCount = 0;
            for (Product product : products) {
                if (product.getPrice() < limitPrice) {
                    cheaperCount++;
                }
            }
            System.out.printf("Кількість товарів, дешевших за %.2f грн: %d шт.%n", limitPrice, cheaperCount);

            int totalQuantity = 0;
            for (Product product : products) {
                totalQuantity += product.getQuantity();
            }
            System.out.printf("Загальна кількість одиниць товару в кошику: %d шт.%n", totalQuantity);
            System.out.println();

            // 4. СОРТУВАННЯ ЗА ЦІНОЮ
            System.out.println("--- 3. СОРТУВАННЯ ТОВАРІВ ЗА ЦІНОЮ ---");
            System.out.println("Масив ДО сортування:");
            printProducts(products);

            bubbleSortByPrice(products);

            System.out.println();
            System.out.println("Масив ПІСЛЯ Bubble Sort (за зростанням ціни):");
            printProducts(products);
            System.out.println();

            // 5. ЛІНІЙНИЙ ПОШУК ТОВАРУ ЗА ЗРАЗКОМ
            System.out.println("--- 4. ЛІНІЙНИЙ ПОШУК ТОВАРУ ЗА ЗРАЗКОМ ---");
            System.out.println("Введіть дані товару-зразка для пошуку (мають збігатися всі поля):");

            Product sampleProduct = null;
            while (sampleProduct == null) {
                try {
                    System.out.print("Назва: ");
                    String searchName = scanner.nextLine().trim();

                    System.out.print("Категорія: ");
                    String searchCategory = scanner.nextLine().trim();

                    System.out.print("Ціна: ");
                    double searchPrice = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));

                    System.out.print("Кількість: ");
                    int searchQuantity = Integer.parseInt(scanner.nextLine().trim());

                    sampleProduct = new Product(searchName, searchCategory, searchPrice, searchQuantity);
                } catch (NumberFormatException e) {
                    System.out.println("Помилка: ціна та кількість зразка мають бути числами!");
                } catch (EmptyNameException | ShopException e) {
                    System.out.println("Помилка зразка: " + e.getMessage() + " Спробуйте ще раз.");
                }
            }

            int foundIndex = findProduct(products, sampleProduct);
            if (foundIndex != -1) {
                System.out.println();
                System.out.printf("Знайдено товар \"%s\" за індексом [%d]:%n", products[foundIndex].getName(), foundIndex);
                System.out.println(products[foundIndex]);
            } else {
                System.out.println();
                System.out.println("Товар-зразок не знайдено у списку.");
            }

            System.out.println();
            System.out.println("Роботу програми успішно завершено!");

        } catch (Exception e) {
            System.out.println("\nНепередбачена помилка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Гарантоване закриття ресурсів
            System.out.println("\n[finally]: Звільнення ресурсів, закриття Scanner.");
            scanner.close();
        }
    }

    // Метод реєстрації товару з механізмом Re-throw (Рівень 3)
    public static Product createProduct(String name, String category, double price, int quantity)
            throws ShopException, EmptyNameException {
        try {
            return new Product(name, category, price, quantity);
        } catch (InvalidPriceException e) {
            // Логування/аудит перед повторним збудженням (re-throw)
            System.out.println("[ЛОГ/АУДИТ]: Спроба створення товару з неприпустимою ціною: " + e.getInvalidPrice() + " грн!");
            throw e; // повторне збудження (re-throw) для обробки у виклику
        }
    }

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