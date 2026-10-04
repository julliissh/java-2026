package delivery;

public class StandardDelivery extends AbstractDelivery {
    private static final double STANDARD_DELIVERY_FEE = 150.0;

    @Override
    public double calculateCost(double orderSum) {
        return getBaseCost(orderSum, STANDARD_DELIVERY_FEE);
    }

    @Override
    public String getName() {
        return "Стандартна доставка";
    }
}
