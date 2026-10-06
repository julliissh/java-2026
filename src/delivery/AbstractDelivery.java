package delivery;

public abstract class AbstractDelivery implements Delivery {
    public static final double FREE_DELIVERY_THRESHOLD = 1500.0;
    public static final double STANDARD_DELIVERY_FEE = 150.0;

    protected double getBaseCost(double orderSum) {
        if (orderSum >= FREE_DELIVERY_THRESHOLD) {
            return 0.0;
        } else {
            return STANDARD_DELIVERY_FEE;
        }
    }
}
