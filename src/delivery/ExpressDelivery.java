package delivery;

public class ExpressDelivery extends AbstractDelivery {
    private static final double STANDARD_DELIVERY_FEE = 150.0;
    private static final double EXPRESS_DELIVERY_EXTRA = 100.0;

    @Override
    public double calculateCost(double orderSum) {
        return getBaseCost(orderSum, STANDARD_DELIVERY_FEE) + EXPRESS_DELIVERY_EXTRA;
    }

    @Override
    public String getName() {
        return "Експрес-доставка";
    }
}
