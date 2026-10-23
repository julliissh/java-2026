package product;

import exception.InvalidProductDataException;

public final class Clothing extends Product {
    public static final String DEFAULT_MATERIAL = "Бавовна";

    private final String size;
    private final String material;

    public Clothing(long id, String name, double price, int quantity, String size, String material) {
        super(id, name, price, quantity);
        if (size == null || size.trim().isEmpty()) {
            throw new InvalidProductDataException("Розмір одягу обов'язковий (наприклад, S, M, L, XL)!");
        }
        if (material == null || material.trim().isEmpty()) {
            throw new InvalidProductDataException("Матеріал одягу обов'язковий (наприклад, Бавовна, Шерсть)!");
        }
        this.size = size.trim();
        this.material = material.trim();
    }

    public Clothing(long id, String name, double price, int quantity, String size) {
        this(id, name, price, quantity, size, DEFAULT_MATERIAL);
    }

    public String getSize() {
        return size;
    }

    public String getMaterial() {
        return material;
    }

    @Override
    public String getCategory() {
        return "Одяг";
    }

    @Override
    public String toString() {
        return super.toString() + " | Розмір: " + size + " | Матеріал: " + material;
    }
}
