import delivery.Delivery;
import discount.DiscountStrategy;

public class Order {
    private Customer customer;
    private Product product;
    private int amount;
    private DiscountStrategy discountStrategy;
    private Delivery delivery;

    public Order(Customer customer, Product product, int amount, DiscountStrategy discountStrategy, Delivery delivery) {
        this.customer = customer;
        this.product = product;
        this.amount = amount;
        this.discountStrategy = discountStrategy;
        this.delivery = delivery;
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public double getSubtotal() {
        return product.getPrice() * amount;
    }

    public double getDiscount() {
        double subtotal = getSubtotal();
        return discountStrategy.calculateDiscount(subtotal);
    }

    public double getDeliveryCost() {
        double subtotal = getSubtotal();
        double discount = getDiscount();
        double orderSumAfterDiscount = subtotal - discount;
        return delivery.calculateCost(orderSumAfterDiscount);
    }

    public double getTotal() {
        return getSubtotal() - getDiscount() + getDeliveryCost();
    }

    public void printReceipt() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                 ЧЕК ЗАМОВЛЕННЯ                   ");
        System.out.println("==================================================");
        System.out.printf("Клієнт: %s (%s)%n", customer.getName(), customer.getPhone());
        System.out.printf("Товар: %s%n", product.getName());
        System.out.printf("Категорія: %s%n", product.getCategory());
        System.out.printf("Ціна за од.: %.2f грн%n", product.getPrice());
        System.out.printf("Кількість: %d шт.%n", amount);
        System.out.printf("Тип знижки: %s%n", discountStrategy.getName());
        System.out.printf("Тип доставки: %s%n", delivery.getName());
        System.out.println("--------------------------------------------------");
        System.out.printf("Вартість товару: %.2f грн%n", getSubtotal());
        System.out.printf("Знижка клієнта: -%.2f грн%n", getDiscount());
        System.out.printf("Вартість доставки: %.2f грн%n", getDeliveryCost());
        System.out.println("--------------------------------------------------");
        System.out.printf("РАЗОМ ДО СПЛАТИ: %.2f грн%n", getTotal());
        System.out.printf("Залишок товару на складі: %d шт.%n", product.getQuantity());
        System.out.println("==================================================");
        System.out.println("Дякуємо за покупку в нашому інтернет-магазині!");
    }
}
