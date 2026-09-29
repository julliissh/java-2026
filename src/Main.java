import exception.InsufficientStockException;
import exception.InvalidPhoneException;
import exception.InvalidPriceException;
import exception.InvalidQuantityException;
import exception.ShopException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final int MAX_PRODUCTS = 20;
    private static final double DISCOUNT_HIGH_THRESHOLD = 5000.0;
    private static final double DISCOUNT_LOW_THRESHOLD = 2500.0;
    private static final double DISCOUNT_HIGH_RATE = 0.10; 
    private static final double DISCOUNT_LOW_RATE = 0.05; 
    private static final double FREE_DELIVERY_THRESHOLD = 1500.0;
    private static final double STANDARD_DELIVERY_FEE = 150.0;
    private static final double EXPRESS_DELIVERY_EXTRA = 100.0;

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
                    System.out.println("Помилка: потрібно ввести цифру від 0 до 6!");
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
                    case 0:
                        System.out.println("\nДякуємо за користування нашим інтернет-магазином! Гарного дня!");
                        return;
                    default:
                        System.out.println("Помилка: невірний пункт меню! Оберіть від 0 до 6.");
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
        System.out.println("4 - Сортувати товари за ціною (Bubble Sort)");
        System.out.println("5 - Пошук товару за зразком (лінійний пошук)");
        System.out.println("6 - Оформити покупку (розрахунок знижки, доставки та чек)");
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

        System.out.println("\n--- СОРТУВАННЯ ТОВАРІВ ЗА ЦІНОЮ (BUBBLE SORT) ---");
        System.out.println("Масив ДО сортування:");
        printProducts(products, count);

        bubbleSortByPrice(products, count);

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
            System.out.println("Обрано: " + selectedProduct.getName() + " (в наявності: " + selectedProduct.getQuantity() + " шт.)");

            System.out.print("Введіть кількість одиниць для покупки: ");
            int buyAmount = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Потрібна швидка експрес-доставка? (1 - так (+" + EXPRESS_DELIVERY_EXTRA + " грн), 0 - ні): ");
            int expressChoice = scanner.nextInt();
            scanner.nextLine();

            if (expressChoice != 0 && expressChoice != 1) {
                throw new IllegalArgumentException("Некоректний вибір доставки! Введіть 1 (експрес) або 0 (стандартна).");
            }
            boolean isExpress = (expressChoice == 1);

            selectedProduct.buy(buyAmount);

            double subtotal = selectedProduct.getPrice() * buyAmount;

            double discount = 0;
            if (subtotal >= DISCOUNT_HIGH_THRESHOLD) {
                discount = subtotal * DISCOUNT_HIGH_RATE;
            } else if (subtotal >= DISCOUNT_LOW_THRESHOLD) {
                discount = subtotal * DISCOUNT_LOW_RATE;
            }

            double deliveryCost;
            if ((subtotal - discount) >= FREE_DELIVERY_THRESHOLD) {
                deliveryCost = 0;
            } else {
                deliveryCost = STANDARD_DELIVERY_FEE;
            }

            String deliveryType;
            if (isExpress) {
                deliveryType = "Експрес";
                deliveryCost += EXPRESS_DELIVERY_EXTRA;
            } else {
                deliveryType = "Стандартна";
            }

            double finalTotal = subtotal - discount + deliveryCost;

            System.out.println();
            System.out.println("==================================================");
            System.out.println("                 ЧЕК ЗАМОВЛЕННЯ                   ");
            System.out.println("==================================================");
            System.out.println("Клієнт: " + customer.getName() + " (" + customer.getPhone() + ")");
            System.out.println("Товар: " + selectedProduct.getName());
            System.out.println("Категорія: " + selectedProduct.getCategory());
            System.out.printf("Ціна за од.: %.2f грн%n", selectedProduct.getPrice());
            System.out.println("Кількість: " + buyAmount + " шт.");
            System.out.println("Тип доставки: " + deliveryType);
            System.out.println("--------------------------------------------------");
            System.out.printf("Вартість товару: %.2f грн%n", subtotal);
            System.out.printf("Знижка клієнта: -%.2f грн%n", discount);
            System.out.printf("Вартість доставки: %.2f грн%n", deliveryCost);
            System.out.println("--------------------------------------------------");
            System.out.printf("РАЗОМ ДО СПЛАТИ: %.2f грн%n", finalTotal);
            System.out.println("Залишок товару на складі: " + selectedProduct.getQuantity() + " шт.");
            System.out.println("==================================================");
            System.out.println("Дякуємо за покупку в нашому інтернет-магазині!");

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

    private static void bubbleSortByPrice(Product[] products, int count) {
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (products[j].getPrice() > products[j + 1].getPrice()) {
                    Product temp = products[j];
                    products[j] = products[j + 1];
                    products[j + 1] = temp;
                }
            }
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