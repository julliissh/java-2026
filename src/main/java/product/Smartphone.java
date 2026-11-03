package product;

import exception.InvalidProductDataException;

public final class Smartphone extends Electronics {
    private final int storageGb;
    private final int batteryCapacity;

    public Smartphone(long id, String name, double price, int quantity, int warrantyMonths, int storageGb, int batteryCapacity) {
        super(id, name, price, quantity, warrantyMonths);
        if (storageGb <= 0) {
            throw new InvalidProductDataException("Обсяг вбудованої пам'яті має бути більшим за 0 ГБ!");
        }
        if (batteryCapacity <= 0) {
            throw new InvalidProductDataException("Ємність акумулятора має бути більшою за 0 мАг!");
        }
        this.storageGb = storageGb;
        this.batteryCapacity = batteryCapacity;
    }

    public int getStorageGb() {
        return storageGb;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    @Override
    public String getCategory() {
        return "Смартфон";
    }

    @Override
    public String toString() {
        return super.toString() + " | Пам'ять: " + storageGb + " ГБ | Батарея: " + batteryCapacity + " мАг";
    }
}
