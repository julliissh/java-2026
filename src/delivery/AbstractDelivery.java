package delivery;

public abstract class AbstractDelivery implements Delivery {
    protected static final double FREE_DELIVERY_THRESHOLD = 1500.0;

    protected double getBaseCost(double orderSum, double standardFee) {
        if (orderSum >= FREE_DELIVERY_THRESHOLD) {
            return 0.0;
        } else {
            return standardFee;
        }
    }
}
