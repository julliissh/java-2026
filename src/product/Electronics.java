package product;

import exception.InvalidProductDataException;
import java.util.Objects;

public sealed class Electronics extends Product permits Smartphone {
    public static final int DEFAULT_WARRANTY_MONTHS = 12;

    private final int warrantyMonths;

    public Electronics(String name, double price, int quantity, int warrantyMonths) {
        super(name, price, quantity);
        if (warrantyMonths < 0) {
            throw new InvalidProductDataException("Гарантійний термін не може бути від'ємним!");
        }
        this.warrantyMonths = warrantyMonths;
        System.out.printf("  [ЛОГ КОНСТРУКТОР]: Electronics -> встановлено гарантію: %d міс.%n", this.warrantyMonths);
    }

    public Electronics(String name, double price, int quantity) {
        this(name, price, quantity, DEFAULT_WARRANTY_MONTHS);
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public String getCategory() {
        return "Електроніка";
    }

    @Override
    public String toString() {
        return super.toString() + " | Гарантія: " + warrantyMonths + " міс.";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Electronics that = (Electronics) obj;
        return warrantyMonths == that.warrantyMonths;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), warrantyMonths);
    }
}
