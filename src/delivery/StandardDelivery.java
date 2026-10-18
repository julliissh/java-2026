package delivery;

public class StandardDelivery extends AbstractDelivery {

    @Override
    protected double getAdditionalCost(double orderSum) {
        return 0.0;
    }

    @Override
    public String getName() {
        return "Стандартна доставка";
    }
}
