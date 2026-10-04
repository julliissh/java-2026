import delivery.Delivery;
import delivery.ExpressDelivery;
import delivery.PickupDelivery;
import delivery.StandardDelivery;
import discount.DiscountStrategy;
import discount.NoDiscount;
import discount.PromoCodeDiscount;
import discount.ThresholdDiscount;
import exception.InsufficientStockException;
import exception.InvalidPhoneException;
import exception.InvalidPriceException;
import exception.InvalidQuantityException;
import exception.ShopException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final int MAX_PRODUCTS = 20;

    void main() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println();
            System.out.println("                 ІНТЕРНЕТ-МАГАЗИН:                ");
            System.out.println("==================================================");
            System.out.println();

            Customer customer = registerCustomer(scanner);

            Product[] products = new Product[MAX_PRODUCTS];
            int count = 0;

            while (true) {
                printMenu(customer, count);

                int choice;
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Помилка: потрібно ввести цифру від 0 до 7!");
                    scanner.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1:
                        count = addProduct(scanner, products, count);
                        break;
                    case 2:
                        showCatalog(products, count);
                        break;
                    case 3:
                        filterByBudget(scanner, products, count);
                        break;
                    case 4:
                        sortCatalog(products, count);
                        break;
                    case 5:
                        searchInCatalog(scanner, products, count);
                        break;
                    case 6:
                        checkout(scanner, customer, products, count);
                        break;
                    case 7:
                        compareDiscounts(scanner);
                        break;
                    case 0:
                        System.out.println("\nДякуємо за користування нашим інтернет-магазином! Гарного дня!");
                        return;
                    default:
                        System.out.println("Помилка: невірний пункт меню! Оберіть від 0 до 7.");
                        break;
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static Customer registerCustomer(Scanner scanner) {
        System.out.println("--- 1. ДАНІ КЛІЄНТА ---");
        System.out.print("Введіть ім'я клієнта: ");
        String customerName = scanner.nextLine().trim();

        Customer customer = null;
        while (customer == null) {
            try {
                System.out.print("Введіть номер телефону (+380... або 0...): ");
                String customerPhone = scanner.nextLine().trim();
                customer = new Customer(customerName, customerPhone);
            } catch (InvalidPhoneException e) {
                System.out.println("Помилка: " + e.getMessage() + " (введено: '" + e.getInvalidPhone() + "'). Спробуйте ще раз.\n");
            }
        }
        System.out.println("Поточний клієнт успішно зареєстрований: " + customer);
        return customer;
    }

    private static void printMenu(Customer customer, int count) {
        System.out.println();
        System.out.println("================ МЕНЮ ІНТЕРНЕТ-МАГАЗИНУ ================");
        System.out.println("Клієнт: " + customer.getName() + " (" + customer.getPhone() + ") | Товарів у каталозі: " + count);
        System.out.println("1 - Додати новий товар до каталогу");
        System.out.println("2 - Переглянути всі товари в каталозі");
        System.out.println("3 - Фільтр за бюджетом та підсумок каталогу");
        System.out.println("4 - Сортувати товари за ціною (Comparable)");
        System.out.println("5 - Пошук товару за зразком (лінійний пошук)");
        System.out.println("6 - Оформити покупку (розрахунок знижки, доставки та чек)");
        System.out.println("7 - Порівняти всі способи знижки для суми");
        System.out.println("0 - Завершити роботу");
        System.out.println("========================================================");
        System.out.print("Оберіть дію: ");
    }

    private static int addProduct(Scanner scanner, Product[] products, int count) {
        if (count >= MAX_PRODUCTS) {
            System.out.println("Помилка: каталог переповнено! Максимальна кількість товарів: " + MAX_PRODUCTS);
            return count;
        }

        System.out.println("\n--- ДОДАВАННЯ ТОВАРУ №" + (count + 1) + " ---");
        try {
            System.out.print("Назва товару: ");
            String name = scanner.nextLine().trim();

            System.out.print("Категорія: ");
            String category = scanner.nextLine().trim();

            System.out.print("Ціна (грн): ");
            double price = scanner.nextDouble();

            System.out.print("Кількість (шт): ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            products[count] = createProduct(name, category, price, quantity);
            count++;
            System.out.println("Товар успішно додано до каталогу!");

        } catch (InputMismatchException e) {
            System.out.println("Помилка: ціна та кількість повинні бути коректними числами!");
            scanner.nextLine();
        } catch (InvalidPriceException e) {
            System.out.println("Помилка: " + e.getMessage() + " (введено: " + e.getInvalidPrice() + " грн)!");
        } catch (ShopException e) {
            System.out.println("Помилка магазину: " + e.getMessage() + "!");
        }
        return count;
    }

    private static void showCatalog(Product[] products, int count) {
        System.out.println("\n--- СПИСОК ТОВАРІВ У КАТАЛОЗІ ---");
        printProducts(products, count);
    }

    private static void filterByBudget(Scanner scanner, Product[] products, int count) {
        if (count == 0) {
            System.out.println("Каталог порожній! Спочатку додайте товари.");
            return;
        }

        System.out.println("\n--- ФІЛЬТР ТОВАРІВ ЗА БЮДЖЕТОМ ТА ПІДСУМОК ---");
        double limitPrice = 0;
        while (limitPrice <= 0) {
            try {
                System.out.print("Шукати товари, дешевші за (введіть суму в грн): ");
                limitPrice = scanner.nextDouble();
                scanner.nextLine();
                if (limitPrice <= 0) {
                    throw new IllegalArgumentException("Сума повинна бути більшою за 0!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Помилка: потрібно ввести числове значення!");
                scanner.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println("Помилка: " + e.getMessage());
            }
        }

        int cheaperCount = 0;
        for (int i = 0; i < count; i++) {
            if (products[i].getPrice() < limitPrice) {
                cheaperCount++;
            }
        }
        System.out.printf("Кількість товарів, дешевших за %.2f грн: %d шт.%n", limitPrice, cheaperCount);

        int totalQuantity = 0;
        double totalPrice = 0;
        for (int i = 0; i < count; i++) {
            totalQuantity += products[i].getQuantity();
            totalPrice += products[i].getPrice() * products[i].getQuantity();
        }
        System.out.printf("Загальна кількість одиниць товару на складі: %d шт.%n", totalQuantity);
        System.out.printf("Загальна вартість усіх товарів: %.2f грн%n", totalPrice);
    }

    private static void sortCatalog(Product[] products, int count) {
        if (count == 0) {
            System.out.println("Каталог порожній! Немає чого сортувати.");
            return;
        }

        System.out.println("\n--- СОРТУВАННЯ ТОВАРІВ ЗА ЦІНОЮ (COMPARABLE) ---");
        System.out.println("Масив ДО сортування:");
        printProducts(products, count);

        Arrays.sort(products, 0, count);

        System.out.println();
        System.out.println("Масив ПІСЛЯ сортування (за зростанням ціни):");
        printProducts(products, count);
    }

    private static void searchInCatalog(Scanner scanner, Product[] products, int count) {
        if (count == 0) {
            System.out.println("Каталог порожній! Немає серед чого шукати.");
            return;
        }

        System.out.println("\n--- ЛІНІЙНИЙ ПОШУК ТОВАРУ ЗА ЗРАЗКОМ ---");
        System.out.println("Введіть дані товару-зразка для пошуку (мають збігатися всі поля):");

        Product sampleProduct = null;
        while (sampleProduct == null) {
            try {
                System.out.print("Назва: ");
                String searchName = scanner.nextLine().trim();

                System.out.print("Категорія: ");
                String searchCategory = scanner.nextLine().trim();

                System.out.print("Ціна: ");
                double searchPrice = scanner.nextDouble();

                System.out.print("Кількість: ");
                int searchQuantity = scanner.nextInt();
                scanner.nextLine();

                sampleProduct = new Product(searchName, searchCategory, searchPrice, searchQuantity);

            } catch (InputMismatchException e) {
                System.out.println("Помилка: ціна та кількість зразка мають бути числами!");
                scanner.nextLine();
            } catch (ShopException e) {
                System.out.println("Помилка зразка: " + e.getMessage() + ". Спробуйте ще раз.");
            }
        }

        int foundIndex = findProduct(products, count, sampleProduct);
        if (foundIndex != -1) {
            System.out.println();
            System.out.printf("Знайдено товар \"%s\" за індексом [%d]:%n", products[foundIndex].getName(), foundIndex);
            System.out.println(products[foundIndex]);
        } else {
            System.out.println();
            System.out.println("Товар-зразок не знайдено у списку.");
        }
    }

    private static void checkout(Scanner scanner, Customer customer, Product[] products, int count) {
        if (count == 0) {
            System.out.println("Каталог порожній! Спочатку додайте товари.");
            return;
        }

        System.out.println("\n--- ОФОРМЛЕННЯ ПОКУПКИ ТА РОЗРАХУНОК ЧЕКУ ---");
        printProducts(products, count);

        try {
            System.out.print("Введіть номер товару для покупки (1-" + count + "): ");
            int itemNumber = scanner.nextInt();
            scanner.nextLine();

            int itemIndex = itemNumber - 1;
            if (itemIndex < 0 || itemIndex >= count) {
                throw new IndexOutOfBoundsException("Товару з номером " + itemNumber + " не існує в списку!");
            }

            Product selectedProduct = products[itemIndex];
            if (!selectedProduct.isInStock()) {
                System.out.println("Товар \"" + selectedProduct.getName() + "\" закінчився на складі!");
                return;
            }
            System.out.println("Обрано: " + selectedProduct.getName() + " (в наявності: " + selectedProduct.getQuantity() + " шт.)");

            System.out.print("Введіть кількість одиниць для покупки: ");
            int buyAmount = scanner.nextInt();
            scanner.nextLine();

            System.out.println("\nОберіть спосіб доставки:");
            System.out.println("1 - Стандартна доставка (150 грн, від 1500 грн безкоштовно)");
            System.out.println("2 - Експрес-доставка (150 грн + 100 грн за швидкість)");
            System.out.println("3 - Самовивіз з магазину (безкоштовно)");
            System.out.print("Ваш вибір: ");
            int deliveryChoice = scanner.nextInt();
            scanner.nextLine();

            Delivery delivery;
            if (deliveryChoice == 1) {
                delivery = new StandardDelivery();
            } else if (deliveryChoice == 2) {
                delivery = new ExpressDelivery();
            } else if (deliveryChoice == 3) {
                delivery = new PickupDelivery();
            } else {
                throw new IllegalArgumentException("Некоректний вибір доставки! Оберіть 1, 2 або 3.");
            }

            selectedProduct.buy(buyAmount);

            Order order = new Order(customer, selectedProduct, buyAmount, new ThresholdDiscount(), delivery);

            System.out.print("\nВведіть промокод (або натисніть Enter, щоб пропустити): ");
            String promoCode = scanner.nextLine().trim();
            if (promoCode.equalsIgnoreCase(PromoCodeDiscount.PROMO_CODE)) {
                order.setDiscountStrategy(new PromoCodeDiscount());
                System.out.println("Промокод успішно застосовано! Знижка змінена на 15%.");
            } else if (!promoCode.isEmpty()) {
                System.out.println("Промокод недійсний. Застосовано стандартну програму лояльності.");
            }

            order.printReceipt();

        } catch (InputMismatchException e) {
            System.out.println("Помилка: номер товару та кількість мають бути цілими числами!");
            scanner.nextLine();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Помилка вибору: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка введення: " + e.getMessage());
        } catch (InsufficientStockException e) {
            System.out.println("Помилка наявності: " + e.getMessage() + " (запитано: " + e.getRequestedQuantity() + " шт.)!");
        } catch (InvalidQuantityException e) {
            System.out.println("Помилка кількості: " + e.getMessage() + " (введено: " + e.getInvalidQuantity() + " шт.)!");
        } catch (ShopException e) {
            System.out.println("Помилка магазину: " + e.getMessage());
        }
    }

    private static void compareDiscounts(Scanner scanner) {
        System.out.println("\n--- ПОРІВНЯННЯ СПОСОБІВ ЗНИЖКИ (ПОЛІМОРФІЗМ) ---");
        double sum = 0;
        while (sum <= 0) {
            try {
                System.out.print("Введіть суму замовлення для перевірки (грн): ");
                sum = scanner.nextDouble();
                scanner.nextLine();
                if (sum <= 0) {
                    throw new IllegalArgumentException("Сума повинна бути більшою за 0!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Помилка: потрібно ввести числове значення!");
                scanner.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println("Помилка: " + e.getMessage());
            }
        }

        DiscountStrategy[] strategies = {
            new NoDiscount(),
            new ThresholdDiscount(),
            new PromoCodeDiscount()
        };

        System.out.println("\nРезультати розрахунку для суми " + sum + " грн:");
        for (DiscountStrategy strategy : strategies) {
            System.out.println(strategy.describe(sum));
        }
    }

    private static Product createProduct(String name, String category, double price, int quantity) {
        try {
            return new Product(name, category, price, quantity);
        } catch (InvalidPriceException e) {
            System.out.println("[ЛОГ/АУДИТ]: Спроба створення товару з неприпустимою ціною: " + e.getInvalidPrice() + " грн!");
            throw e;
        }
    }

    private static void printProducts(Product[] products, int count) {
        if (count == 0) {
            System.out.println("Каталог товарів порожній.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.printf("[%d] Товар: %-19s | Категорія: %-12s | Ціна: %8.2f грн | Залишок: %3d шт.%n",
                    (i + 1), products[i].getName(), products[i].getCategory(), products[i].getPrice(), products[i].getQuantity());
        }
    }

    private static int findProduct(Product[] products, int count, Product target) {
        for (int i = 0; i < count; i++) {
            if (products[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}