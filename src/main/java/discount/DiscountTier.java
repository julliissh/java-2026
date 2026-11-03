package discount;

public enum DiscountTier {
    NONE(0.0, 0.0),
    LOW(2500.0, 0.05),
    HIGH(5000.0, 0.10);

    private final double threshold;
    private final double rate;

    DiscountTier(double threshold, double rate) {
        this.threshold = threshold;
        this.rate = rate;
    }

    public double getThreshold() {
        return threshold;
    }

    public double getRate() {
        return rate;
    }

    public static DiscountTier getBySubtotal(double subtotal) {
        if (subtotal >= HIGH.threshold) {
            return HIGH;
        } else if (subtotal >= LOW.threshold) {
            return LOW;
        }
        return NONE;
    }
}
