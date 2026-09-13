import java.util.Scanner;

void main() {
    Scanner scanner = new Scanner(System.in);
    
    IO.println();
    IO.println("Онлайн-магазин: Оформлення замовлення");
    IO.println();

    IO.print("Введіть ім'я клієнта: ");
    String clientName = scanner.nextLine();

    IO.print("Введіть назву товару: ");
    String productTitle = scanner.nextLine();

    IO.print("Введіть категорію товару: ");
    String category = scanner.nextLine();

    IO.print("Введіть ціну товару (грн): ");
    double price = scanner.nextDouble();

    IO.print("Введіть кількість (шт): ");
    int quantity = scanner.nextInt();

    IO.print("Введіть рейтинг товару (від 1 до 5): ");
    double rating = scanner.nextDouble();

    IO.print("Потрібна експрес-доставка? (true/false): ");
    boolean isExpress = scanner.nextBoolean();

    double subtotal = price * quantity;

    double discount;
    if (subtotal >= 5000) {
        discount = subtotal * 0.10;
    } else if (subtotal >= 2500) {
        discount = subtotal * 0.05;
    } else {
        discount = 0;
    }

    double deliveryCost;
    if ((subtotal - discount) >= 1500) {
        deliveryCost = 0;
    } else {
        deliveryCost = 150;
    }

    String deliveryType;
    if (isExpress) {
        deliveryCost += 100;
        deliveryType = "Експрес (+100 грн)";
    } else {
        deliveryType = "Стандартна";
    }

    String ratingStatus;
    if (rating >= 4.5) {
        ratingStatus = "Високий рейтинг (Хіт продажу!)";
    } else if (rating >= 3) {
        ratingStatus = "Середній рейтинг (Добрий вибір)";
    } else {
        ratingStatus = "Низький рейтинг";
    }

    double finalTotal = subtotal - discount + deliveryCost;

    IO.println();
    IO.println("==================================================");
    IO.println("                 ЧЕК ЗАМОВЛЕННЯ                   ");
    IO.println("==================================================");
    IO.println("Клієнт: " + clientName);
    IO.println("Товар: " + productTitle);
    IO.println("Категорія: " + category);
    System.out.printf("Ціна за одиницю: %.2f грн%n", price);
    IO.println("Кількість: " + quantity + " шт.");
    System.out.printf("Рейтинг: %.2f / 5.00 (%s)%n", rating, ratingStatus);
    IO.println("Тип доставки: " + deliveryType);
    IO.println("--------------------------------------------------");
    System.out.printf("Вартість товарів: %.2f грн%n", subtotal);
    System.out.printf("Знижка: -%.2f грн%n", discount);
    System.out.printf("Доставка: %.2f грн%n", deliveryCost);
    IO.println("--------------------------------------------------");
    System.out.printf("РАЗОМ ДО СПЛАТИ: %.2f грн%n", finalTotal);
    IO.println("==================================================");
    IO.println("Дякуємо за покупку в нашому інтернет-магазині!");
    scanner.close();
}