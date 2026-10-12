package discount;

public class ThresholdDiscount implements DiscountStrategy {

    @Override
    public double calculateDiscount(double subtotal) {
        DiscountTier tier = DiscountTier.getBySubtotal(subtotal);
        switch (tier) {
            case HIGH:
                return subtotal * tier.getRate();
            case LOW:
                return subtotal * tier.getRate();
            case NONE:
            default:
                return 0.0;
        }
    }

    @Override
    public String getName() {
        return "Програма лояльності (знижка від суми)";
    }
}
