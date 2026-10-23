package product;

import exception.InvalidProductDataException;

public sealed class Electronics extends Product permits Smartphone {
    public static final int DEFAULT_WARRANTY_MONTHS = 12;

    private final int warrantyMonths;

    public Electronics(long id, String name, double price, int quantity, int warrantyMonths) {
        super(id, name, price, quantity);
        if (warrantyMonths < 0) {
            throw new InvalidProductDataException("Гарантійний термін не може бути від'ємним!");
        }
        this.warrantyMonths = warrantyMonths;
    }

    public Electronics(long id, String name, double price, int quantity) {
        this(id, name, price, quantity, DEFAULT_WARRANTY_MONTHS);
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
}
