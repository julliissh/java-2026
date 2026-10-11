package delivery;

public class ExpressDelivery extends AbstractDelivery {
    public static final double EXPRESS_DELIVERY_EXTRA = 100.0;

    @Override
    public double calculateCost(double orderSum) {
        return getBaseCost(orderSum) + EXPRESS_DELIVERY_EXTRA;
    }

    @Override
    public String getName() {
        return "Експрес-доставка";
    }
}
