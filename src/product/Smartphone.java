package product;

import exception.InvalidProductDataException;
import java.util.Objects;

public final class Smartphone extends Electronics {
    private final int storageGb;
    private final int batteryCapacity;

    public Smartphone(String name, double price, int quantity, int warrantyMonths, int storageGb, int batteryCapacity) {
        super(name, price, quantity, warrantyMonths);
        if (storageGb <= 0) {
            throw new InvalidProductDataException("Обсяг вбудованої пам'яті має бути більшим за 0 ГБ!");
        }
        if (batteryCapacity <= 0) {
            throw new InvalidProductDataException("Ємність акумулятора має бути більшою за 0 мАг!");
        }
        this.storageGb = storageGb;
        this.batteryCapacity = batteryCapacity;
        System.out.printf("  [ЛОГ КОНСТРУКТОР]: Smartphone -> параметри смартфона (пам'ять: %d ГБ, батарея: %d мАг)%n",
                this.storageGb, this.batteryCapacity);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Smartphone that = (Smartphone) obj;
        return storageGb == that.storageGb && batteryCapacity == that.batteryCapacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), storageGb, batteryCapacity);
    }
}
