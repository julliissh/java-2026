package discount;

public class NoDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double subtotal) {
        return 0.0;
    }

    @Override
    public String getName() {
        return "Без знижки";
    }

    @Override
    public String describe(double subtotal) {
        return getName() + " | знижка не застосовується | до сплати: " + subtotal + " грн";
    }
}
