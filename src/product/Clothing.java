package product;

import exception.InvalidProductDataException;
import java.util.Objects;

public final class Clothing extends Product {
    public static final String DEFAULT_MATERIAL = "Бавовна";

    private final String size;
    private final String material;

    public Clothing(String name, double price, int quantity, String size, String material) {
        super(name, price, quantity);
        if (size == null || size.trim().isEmpty()) {
            throw new InvalidProductDataException("Розмір одягу обов'язковий (наприклад, S, M, L, XL)!");
        }
        if (material == null || material.trim().isEmpty()) {
            throw new InvalidProductDataException("Матеріал одягу обов'язковий (наприклад, Бавовна, Шерсть)!");
        }
        this.size = size.trim();
        this.material = material.trim();
        System.out.printf("  [ЛОГ КОНСТРУКТОР]: Clothing -> параметри одягу (розмір: \"%s\", матеріал: \"%s\")%n", this.size, this.material);
    }

    public Clothing(String name, double price, int quantity, String size) {
        this(name, price, quantity, size, DEFAULT_MATERIAL);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Clothing clothing = (Clothing) obj;
        return Objects.equals(size, clothing.size) && Objects.equals(material, clothing.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), size, material);
    }
}
