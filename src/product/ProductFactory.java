package product;

public class ProductFactory {
    public static final int TYPE_CLOTHING = 1;
    public static final int TYPE_ELECTRONICS = 2;
    public static final int TYPE_SMARTPHONE = 3;

    private static final long FIRST_ID = 1;
    private static long nextId = FIRST_ID;

    private static long nextId() {
        return nextId++;
    }

    public static Clothing createClothing(String name, double price, int quantity, String size, String material) {
        return new Clothing(nextId(), name, price, quantity, size, material);
    }

    public static Clothing createClothing(String name, double price, int quantity, String size) {
        return new Clothing(nextId(), name, price, quantity, size);
    }

    public static Electronics createElectronics(String name, double price, int quantity, int warrantyMonths) {
        return new Electronics(nextId(), name, price, quantity, warrantyMonths);
    }

    public static Electronics createElectronics(String name, double price, int quantity) {
        return new Electronics(nextId(), name, price, quantity);
    }

    public static Smartphone createSmartphone(String name, double price, int quantity, int warrantyMonths, int storageGb, int batteryCapacity) {
        return new Smartphone(nextId(), name, price, quantity, warrantyMonths, storageGb, batteryCapacity);
    }
}
