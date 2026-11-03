import delivery.AbstractDelivery;
import delivery.Delivery;
import delivery.DeliveryFactory;
import delivery.ExpressDelivery;
import discount.DiscountFactory;
import discount.DiscountStrategy;
import exception.InsufficientStockException;
import exception.InvalidPhoneException;
import exception.InvalidPriceException;
import exception.InvalidQuantityException;
import exception.ShopException;
import java.util.InputMismatchException;
import java.util.Scanner;
import observer.CustomerNotifier;
import observer.OrderLogger;
import product.Clothing;
import product.Displayable;
import product.Electronics;
import product.Product;
import product.ProductFactory;
import product.Purchasable;
import product.Smartphone;

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
                    System.out.println("Помилка: потрібно ввести цифру від 0 до 8!");
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
                    case 8:
                        runSystemDiagnostics();
                        break;
                    case 0:
                        System.out.println("\nДякуємо за користування нашим інтернет-магазином! Гарного дня!");
                        return;
                    default:
                        System.out.println("Помилка: невірний пункт меню! Оберіть від 0 до 8.");
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
        System.out.printf("Клієнт: %s (%s) | Товарів у каталозі: %d%n", customer.getName(), customer.getPhone(), count);
        System.out.println("1 - Додати новий товар до каталогу");
        System.out.println("2 - Переглянути всі товари в каталозі");
        System.out.println("3 - Фільтр за бюджетом та підсумок каталогу");
        System.out.println("4 - Сортувати товари за ціною (Bubble Sort)");
        System.out.println("5 - Пошук товару за зразком");
        System.out.println("6 - Оформити покупку");
        System.out.println("7 - Порівняти всі способи знижки для суми");
        System.out.println("8 - Діагностика ієрархії та типів товарів");
        System.out.println("0 - Завершити роботу");
        System.out.println("========================================================");
        System.out.print("Оберіть дію: ");
    }

    private static Product readProductFromConsole(Scanner scanner, String modeTitle) {
        System.out.printf("%n--- %s ---%n", modeTitle);
        System.out.println("Оберіть категорію товару:");
        System.out.println("1 - Одяг");
        System.out.println("2 - Електроніка");
        System.out.println("3 - Смартфон");
        System.out.print("Ваш вибір (1-3): ");

        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Назва товару: ");
        String name = scanner.nextLine().trim();

        System.out.print("Ціна (грн): ");
        double price = scanner.nextDouble();

        System.out.print("Кількість (шт): ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        switch (typeChoice) {
            case ProductFactory.TYPE_CLOTHING:
                System.out.print("Розмір одягу (наприклад, S, M, L, XL): ");
                String size = scanner.nextLine().trim();
                System.out.printf("Використати матеріал за замовчуванням (%s)? (1 - так, 2 - ні): ", Clothing.DEFAULT_MATERIAL);
                int matChoice = scanner.nextInt();
                scanner.nextLine();
                if (matChoice == 1) {
                    return ProductFactory.createClothing(name, price, quantity, size);
                } else {
                    System.out.print("Введіть назву матеріалу: ");
                    String material = scanner.nextLine().trim();
                    return ProductFactory.createClothing(name, price, quantity, size, material);
                }

            case ProductFactory.TYPE_ELECTRONICS:
                System.out.printf("Використати гарантію за замовчуванням (%d міс.)? (1 - так, 2 - ні): ", Electronics.DEFAULT_WARRANTY_MONTHS);
                int warChoice = scanner.nextInt();
                scanner.nextLine();
                if (warChoice == 1) {
                    return ProductFactory.createElectronics(name, price, quantity);
                } else {
                    System.out.print("Введіть термін гарантії (місяців): ");
                    int warranty = scanner.nextInt();
                    scanner.nextLine();
                    return ProductFactory.createElectronics(name, price, quantity, warranty);
                }

            case ProductFactory.TYPE_SMARTPHONE:
                System.out.printf("Використати гарантію за замовчуванням (%d міс.)? (1 - так, 2 - ні): ", Electronics.DEFAULT_WARRANTY_MONTHS);
                int phoneWarChoice = scanner.nextInt();
                scanner.nextLine();
                int warranty;
                if (phoneWarChoice == 1) {
                    warranty = Electronics.DEFAULT_WARRANTY_MONTHS;
                } else {
                    System.out.print("Введіть термін гарантії (місяців): ");
                    warranty = scanner.nextInt();
                    scanner.nextLine();
                }

                System.out.print("Обсяг вбудованої пам'яті (ГБ): ");
                int storage = scanner.nextInt();

                System.out.print("Ємність акумулятора (мАг): ");
                int battery = scanner.nextInt();
                scanner.nextLine();

                return ProductFactory.createSmartphone(name, price, quantity, warranty, storage, battery);

            default:
                throw new IllegalArgumentException("Невірний код категорії товару: " + typeChoice + "!");
        }
    }

    private static int addProduct(Scanner scanner, Product[] products, int count) {
        if (count >= MAX_PRODUCTS) {
            System.out.println("Помилка: каталог переповнено! Максимальна кількість товарів: " + MAX_PRODUCTS);
            return count;
        }

        try {
            Product newProduct = readProductFromConsole(scanner, "ДОДАВАННЯ ТОВАРУ №" + (count + 1));
            products[count] = newProduct;
            count++;
            System.out.println("Товар успішно додано до каталогу!");
            showOnDisplay(newProduct);
            inspectStock(newProduct);

        } catch (InputMismatchException e) {
            System.out.println("Помилка: введено некоректні дані! Очікувалось число.");
            scanner.nextLine();
        } catch (InvalidPriceException e) {
            System.out.println("Помилка ціни: " + e.getMessage() + " (введено: " + e.getInvalidPrice() + " грн)!");
        } catch (ShopException e) {
            System.out.println("Помилка магазину: " + e.getMessage() + "!");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка вхідних даних: " + e.getMessage());
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

        System.out.println("\n--- ПОШУК ТОВАРУ ЗА ЗРАЗКОМ (ПЕРЕВІРКА EQUALS) ---");
        try {
            Product sample = readProductFromConsole(scanner, "ВВЕДЕННЯ ДАНИХ ЗРАЗКА");
            int foundIndex = findProduct(products, count, sample);
            if (foundIndex != -1) {
                System.out.println();
                System.out.printf("Знайдено ідентичний товар \"%s\" за індексом [%d]:%n", products[foundIndex].getName(), foundIndex);
                System.out.println(products[foundIndex]);
            } else {
                System.out.println();
                System.out.println("Товар-зразок не знайдено в каталозі (метод equals повернув false для всіх елементів).");
            }
        } catch (InputMismatchException e) {
            System.out.println("Помилка: числові дані введено некоректно!");
            scanner.nextLine();
        } catch (ShopException | IllegalArgumentException e) {
            System.out.println("Помилка введення зразка: " + e.getMessage());
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
            System.out.printf("Введіть номер товару для покупки (1-%d): ", count);
            int itemNumber = scanner.nextInt();
            scanner.nextLine();

            int itemIndex = itemNumber - 1;
            if (itemIndex < 0 || itemIndex >= count) {
                throw new IndexOutOfBoundsException("Товару з номером " + itemNumber + " не існує в списку!");
            }

            Product selectedProduct = products[itemIndex];
            if (!selectedProduct.isInStock()) {
                System.out.printf("Товар \"%s\" закінчився на складі!%n", selectedProduct.getName());
                return;
            }
            System.out.printf("Обрано: %s (в наявності: %d шт.)%n", selectedProduct.getName(), selectedProduct.getQuantity());

            System.out.print("Введіть кількість одиниць для покупки: ");
            int buyAmount = scanner.nextInt();
            scanner.nextLine();

            System.out.println("\nОберіть спосіб доставки:");
            System.out.printf("1 - Стандартна доставка (%.0f грн, від %.0f грн безкоштовно)%n",
                    AbstractDelivery.STANDARD_DELIVERY_FEE, AbstractDelivery.FREE_DELIVERY_THRESHOLD);
            System.out.printf("2 - Експрес-доставка (%.0f грн + %.0f грн за швидкість)%n",
                    AbstractDelivery.STANDARD_DELIVERY_FEE, ExpressDelivery.EXPRESS_DELIVERY_EXTRA);
            System.out.println("3 - Самовивіз з магазину (безкоштовно)");
            System.out.println("4 - Нова Пошта (стороннє API через Adapter)");
            System.out.print("Ваш вибір (1-4): ");
            int deliveryChoice = scanner.nextInt();
            scanner.nextLine();

            Delivery delivery = DeliveryFactory.createDelivery(deliveryChoice);

            System.out.print("\nВведіть промокод (або натисніть Enter, щоб пропустити): ");
            String promoCode = scanner.nextLine().trim();
            DiscountStrategy discount = DiscountFactory.createDiscount(promoCode);
            System.out.println("Застосовано: " + discount.getName());

            Order order = new Order(customer, selectedProduct, buyAmount, discount, delivery);
            order.addObserver(new OrderLogger());
            order.addObserver(new CustomerNotifier());

            selectedProduct.buy(buyAmount);
            order.placeOrder();
            order.printReceipt();

        } catch (InputMismatchException e) {
            System.out.println("Помилка: номер товару та кількість мають бути цілими числами!");
            scanner.nextLine();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Помилка вибору: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка введення: " + e.getMessage());
        } catch (InsufficientStockException e) {
            System.out.printf("Помилка наявності: %s (запитано: %d шт., в наявності: %d шт.)!%n",
                    e.getMessage(), e.getRequestedQuantity(), e.getAvailableQuantity());
        } catch (InvalidQuantityException e) {
            System.out.printf("Помилка кількості: %s (введено: %d шт.)!%n", e.getMessage(), e.getInvalidQuantity());
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

        DiscountStrategy[] strategies = DiscountFactory.createAllStrategies();

        System.out.printf("%nРезультати розрахунку для суми %.2f грн:%n", sum);
        for (DiscountStrategy strategy : strategies) {
            System.out.println(strategy.describe(sum));
        }
    }

    private static void runSystemDiagnostics() {
        System.out.println("\n========================================================");
        System.out.println("         ДІАГНОСТИКА СИСТЕМИ ТА ІЄРАРХІЇ ТОВАРІВ         ");
        System.out.println("========================================================");

        System.out.println("\n[1] ЛАНЦЮЖОК ВИКЛИКІВ КОНСТРУКТОРІВ:");
        System.out.println("Створюємо тестовий екземпляр Smartphone:");
        Smartphone demoPhone = ProductFactory.createSmartphone("Google Pixel 8", 32000, 2, 24, 128, 4575);
        System.out.println("Створено об'єкт: " + demoPhone);
        System.out.println("Порядок виконання: Product -> Electronics -> Smartphone.");

        System.out.println("\n[2] ДОСЛІДЖЕННЯ СИМЕТРІЇ EQUALS() (GETCLASS ПРОТИ INSTANCEOF):");
        Electronics baseElectronics = ProductFactory.createElectronics("Смарт-годинник", 8000.0, 1, 12);
        Smartphone subSmartphone = ProductFactory.createSmartphone("Смарт-годинник", 8000.0, 1, 12, 64, 450);

        System.out.println("Об'єкт 1 (Electronics): " + baseElectronics);
        System.out.println("Об'єкт 2 (Smartphone): " + subSmartphone);

        boolean baseEqualsSub = baseElectronics.equals(subSmartphone);
        boolean subEqualsBase = subSmartphone.equals(baseElectronics);

        System.out.printf("  baseElectronics.equals(subSmartphone) -> %b%n", baseEqualsSub);
        System.out.printf("  subSmartphone.equals(baseElectronics) -> %b%n", subEqualsBase);

        if (baseEqualsSub == subEqualsBase) {
            System.out.println("  -> Симетрію збережено: a.equals(b) == b.equals(a) (false завдяки getClass()).");
        }

        System.out.println("\n[3] ДЕМОНСТРАЦІЯ БЕЗПЕЧНОГО ПРИВЕДЕННЯ ТИПІВ:");
        Product someProduct = ProductFactory.createClothing("Футболка", 500.0, 5, "M", "Бавовна");
        System.out.printf("Статичний тип: Product | Фактичний тип: %s%n", someProduct.getClass().getSimpleName());

        System.out.println("Спроба явного некоректного приведення (Smartphone) someProduct:");
        try {
            Smartphone badCast = (Smartphone) someProduct;
            System.out.println("Приведення вдалося: " + badCast);
        } catch (ClassCastException ex) {
            System.out.println("  -> Перехоплено ClassCastException: " + ex.getMessage());
            System.out.println("  -> Безпечний виклик через pattern matching:");
            if (someProduct instanceof Smartphone sp) {
                System.out.println("  Об'єкт є смартфоном: " + sp.getStorageGb() + " ГБ");
            } else {
                System.out.printf("  Об'єкт \"%s\" не є Smartphone, приведення відхилено.%n", someProduct.getName());
            }
        }
        System.out.println("========================================================\n");
    }

    private static void printProducts(Product[] products, int count) {
        if (count == 0) {
            System.out.println("Каталог товарів порожній.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.printf("[%d] %s%n", (i + 1), products[i].toString());
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

    private static void showOnDisplay(Displayable item) {
        System.out.printf("  [Вітрина (Displayable)]: %s%n", item.getDisplayInfo());
    }

    private static void inspectStock(Purchasable item) {
        String available;
        if (item.isInStock()) {
            available = "так";
        } else {
            available = "ні";
        }
        System.out.printf("  [Склад (Purchasable)]: в наявності %d шт. (доступний: %s)%n", item.getQuantity(), available);
    }
}
