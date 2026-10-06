package delivery;

public class StandardDelivery extends AbstractDelivery {

    @Override
    public double calculateCost(double orderSum) {
        return getBaseCost(orderSum);
    }

    @Override
    public String getName() {
        return "Стандартна доставка";
    }
}
