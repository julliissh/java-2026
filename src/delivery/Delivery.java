package delivery;

public interface Delivery {
    double calculateCost(double orderSum);
    String getName();
}
