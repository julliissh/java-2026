import java.util.Scanner;

public class Main {
    void main() {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("                 ІНТЕРНЕТ-МАГАЗИН:                ");
        System.out.println("==================================================");
        System.out.println();

        System.out.println("--- 1. ДАНІ КЛІЄНТА ---");
        System.out.print("Введіть ім'я клієнта: ");
        String customerName = scanner.nextLine();

        System.out.print("Введіть номер телефону: ");
        String customerPhone = scanner.nextLine();

        Customer customer = new Customer(customerName, customerPhone);
        System.out.println("Поточний клієнт: " + customer);
        System.out.println();

        System.out.println("--- 2. ФОРМУВАННЯ КОШИКА ТОВАРІВ ---");
        System.out.print("Введіть кількість товарів для додавання: ");
        int count = scanner.nextInt();
        scanner.nextLine();

        Product[] products = new Product[count];
        for (int i = 0; i < count; i++) {
            System.out.println();
            System.out.println("Введення товару №" + (i + 1) + ":");

            System.out.print("Назва товару: ");
            String name = scanner.nextLine();

            System.out.print("Категорія: ");
            String category = scanner.nextLine();

            System.out.print("Ціна (грн): ");
            double price = scanner.nextDouble();

            System.out.print("Кількість (шт): ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            products[i] = new Product(name, category, price, quantity);
        }

        System.out.println();
        System.out.println("--- СПИСОК ТОВАРІВ У КОШИКУ КЛІЄНТА " + customer.getName() + " (" + customer.getPhone() + ") ---");
        printProducts(products);

        System.out.println();
        System.out.println("--- ПІДСУМОК: ФІЛЬТР ТОВАРІВ ЗА БЮДЖЕТОМ ---");
        System.out.print("Шукати товари, дешевші за (введіть суму в грн): ");
        double limitPrice = scanner.nextDouble();
        scanner.nextLine();

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

        System.out.println("--- 3. СОРТУВАННЯ ТОВАРІВ ЗА ЦІНОЮ ---");
        System.out.println("Масив ДО сортування:");
        printProducts(products);

        bubbleSortByPrice(products);

        System.out.println();
        System.out.println("Масив ПІСЛЯ Bubble Sort (за зростанням ціни):");
        printProducts(products);
        System.out.println();

        System.out.println("--- 4. ЛІНІЙНИЙ ПОШУК ТОВАРУ ЗА ЗРАЗКОМ ---");
        System.out.println("Введіть дані товару-зразка для пошуку (мають збігатися всі поля):");

        System.out.print("Назва: ");
        String searchName = scanner.nextLine();

        System.out.print("Категорія: ");
        String searchCategory = scanner.nextLine();

        System.out.print("Ціна: ");
        double searchPrice = scanner.nextDouble();

        System.out.print("Кількість: ");
        int searchQuantity = scanner.nextInt();
        scanner.nextLine();

        Product sampleProduct = new Product(searchName, searchCategory, searchPrice, searchQuantity);

        int foundIndex = findProduct(products, sampleProduct);

        if (foundIndex != -1) {
            System.out.println();
            System.out.println("Знайдено товар \"" + products[foundIndex].getName() + "\" за індексом [" + foundIndex + "]:");
            System.out.println(products[foundIndex]);
        } else {
            System.out.println();
            System.out.println("Товар-зразок не знайдено у списку.");
        }
        System.out.println();
        System.out.println("Роботу програми успішно завершено!");
        scanner.close();
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