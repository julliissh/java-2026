package product;

import exception.InsufficientStockException;
import exception.InvalidPriceException;
import exception.InvalidProductDataException;
import exception.InvalidQuantityException;
import java.util.Objects;

public abstract sealed class Product implements Purchasable, Displayable permits Electronics, Clothing {
    private final String name;
    private final double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductDataException("Назва товару не може бути порожньою!");
        }
        if (price <= 0) {
            throw new InvalidPriceException("Ціна товару повинна бути більшою за 0", price);
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Кількість товару повинна бути більшою за 0", quantity);
        }

        this.name = name.trim();
        this.price = price;
        this.quantity = quantity;

        System.out.printf("  [ЛОГ КОНСТРУКТОР]: Product -> ініціалізовано базові поля (назва: \"%s\", ціна: %.2f грн, кількість: %d шт.)%n",
                this.name, this.price, this.quantity);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public final void buy(int amount) {
        if (amount <= 0) {
            throw new InvalidQuantityException("Кількість для покупки має бути більшою за 0", amount);
        }
        if (amount > this.quantity) {
            throw new InsufficientStockException("Недостатньо товару на складі! В наявності лише: " + this.quantity + " шт.", amount, this.quantity);
        }
        this.quantity -= amount;
    }

    public abstract String getCategory();

    @Override
    public String getDisplayInfo() {
        return name + " (" + getCategory() + ") — " + price + " грн";
    }

    @Override
    public String toString() {
        return "Товар: " + name + " | Категорія: " + getCategory() + " | Ціна: " + price + " грн | Кількість: " + quantity + " шт.";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        return Double.compare(other.price, price) == 0 &&
               Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
