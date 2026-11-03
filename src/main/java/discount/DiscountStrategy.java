package discount;

public interface DiscountStrategy {
    double calculateDiscount(double subtotal);
    String getName();

    default String describe(double subtotal) {
        double discount = calculateDiscount(subtotal);
        double toPay = subtotal - discount;
        return getName() + " | знижка: " + discount + " грн | до сплати: " + toPay + " грн";
    }
}
