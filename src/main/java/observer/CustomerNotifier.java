package observer;

public class CustomerNotifier implements OrderObserver {

    @Override
    public void onOrderPlaced(String customerName, String customerPhone, String productName, int amount, double total) {
        System.out.printf("[SMS на %s]: Дякуємо за покупку, %s! Ваше замовлення \"%s\" на суму %.2f грн успішно прийнято.%n",
                customerPhone, customerName, productName, total);
    }
}
