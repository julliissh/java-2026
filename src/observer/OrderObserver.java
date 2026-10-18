package observer;

public interface OrderObserver {
    void onOrderPlaced(String customerName, String customerPhone, String productName, int amount, double total);
}
