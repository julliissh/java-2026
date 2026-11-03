package delivery;

public class ExpressDelivery extends AbstractDelivery {
    public static final double EXPRESS_DELIVERY_EXTRA = 100.0;

    @Override
    protected double getAdditionalCost(double orderSum) {
        return EXPRESS_DELIVERY_EXTRA;
    }

    @Override
    public String getName() {
        return "Експрес-доставка";
    }
}
