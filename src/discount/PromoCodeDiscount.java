package discount;

public class PromoCodeDiscount implements DiscountStrategy {
    public static final String PROMO_CODE = "SALE15";
    private static final double PROMO_RATE = 0.15;

    @Override
    public double calculateDiscount(double subtotal) {
        return subtotal * PROMO_RATE;
    }

    @Override
    public String getName() {
        return "Промокод " + PROMO_CODE + " (" + (int)(PROMO_RATE * 100) + "%)";
    }
}
