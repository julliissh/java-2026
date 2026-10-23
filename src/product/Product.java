package product;

import entity.BaseEntity;
import exception.InsufficientStockException;
import exception.InvalidPriceException;
import exception.InvalidProductDataException;
import exception.InvalidQuantityException;

public abstract sealed class Product extends BaseEntity implements Purchasable, Displayable permits Electronics, Clothing {
    private final double price;
    private int quantity;

    protected Product(long id, String name, double price, int quantity) {
        super(id, validateName(name));
        if (price <= 0) {
            throw new InvalidPriceException("Ціна товару повинна бути більшою за 0", price);
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Кількість товару повинна бути більшою за 0", quantity);
        }

        this.price = price;
        this.quantity = quantity;
    }

    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductDataException("Назва товару не може бути порожньою!");
        }
        return name.trim();
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
        return getName() + " (" + getCategory() + ") — " + price + " грн";
    }

    @Override
    public String toString() {
        return "Товар [id=" + getId() + "]: " + getName() + " | Категорія: " + getCategory() + " | Ціна: " + price + " грн | Кількість: " + quantity + " шт.";
    }
}
