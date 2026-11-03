package delivery;

public abstract class AbstractDelivery implements Delivery {
    public static final double FREE_DELIVERY_THRESHOLD = 1500.0;
    public static final double STANDARD_DELIVERY_FEE = 150.0;

    @Override
    public final double calculateCost(double orderSum) {
        return getBaseCost(orderSum) + getAdditionalCost(orderSum);
    }

    protected double getBaseCost(double orderSum) {
        if (orderSum >= FREE_DELIVERY_THRESHOLD) {
            return 0.0;
        } else {
            return STANDARD_DELIVERY_FEE;
        }
    }

    protected abstract double getAdditionalCost(double orderSum);
}
