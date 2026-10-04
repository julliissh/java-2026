package discount;

public class ThresholdDiscount implements DiscountStrategy {
    private static final double DISCOUNT_HIGH_THRESHOLD = 5000.0;
    private static final double DISCOUNT_LOW_THRESHOLD = 2500.0;
    private static final double DISCOUNT_HIGH_RATE = 0.10;
    private static final double DISCOUNT_LOW_RATE = 0.05;

    @Override
    public double calculateDiscount(double subtotal) {
        if (subtotal >= DISCOUNT_HIGH_THRESHOLD) {
            return subtotal * DISCOUNT_HIGH_RATE;
        } else if (subtotal >= DISCOUNT_LOW_THRESHOLD) {
            return subtotal * DISCOUNT_LOW_RATE;
        }
        return 0.0;
    }

    @Override
    public String getName() {
        return "Програма лояльності (знижка від суми)";
    }
}
