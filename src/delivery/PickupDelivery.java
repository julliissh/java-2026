package delivery;

public class PickupDelivery extends AbstractDelivery {

    @Override
    protected double getBaseCost(double orderSum) {
        return 0.0;
    }

    @Override
    protected double getAdditionalCost(double orderSum) {
        return 0.0;
    }

    @Override
    public String getName() {
        return "Самовивіз з магазину";
    }
}
