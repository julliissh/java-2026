package delivery;

public class DeliveryFactory {
    public static Delivery createDelivery(int choice) {
        switch (choice) {
            case 1:
                return new StandardDelivery();
            case 2:
                return new ExpressDelivery();
            case 3:
                return new PickupDelivery();
            case 4:
                return new NovaPoshtaAdapter(new NovaPoshtaService());
            default:
                throw new IllegalArgumentException("Некоректний вибір доставки! Оберіть від 1 до 4.");
        }
    }
}
