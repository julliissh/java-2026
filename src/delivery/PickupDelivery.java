package delivery;

public class PickupDelivery implements Delivery {

    @Override
    public double calculateCost(double orderSum) {
        return 0.0;
    }

    @Override
    public String getName() {
        return "Самовивіз з магазину";
    }
}
