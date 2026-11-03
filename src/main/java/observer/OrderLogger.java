package observer;

public class OrderLogger implements OrderObserver {

    @Override
    public void onOrderPlaced(String customerName, String customerPhone, String productName, int amount, double total) {
        System.out.printf("[ЛОГ БУХГАЛТЕРІЇ]: Замовлення для %s (%s) на товар \"%s\" (%d шт.) на суму %.2f грн оформлено.%n",
                customerName, customerPhone, productName, amount, total);
    }
}
