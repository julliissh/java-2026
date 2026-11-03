package product;

import entity.BaseEntity;
import exception.InsufficientStockException;
import exception.InvalidPriceException;
import exception.InvalidQuantityException;

public class Product extends BaseEntity implements Purchasable, Displayable {
    private final double price;
    private int quantity;
    private long categoryId;

    public Product(long id, String name, double price, int quantity) {
        this(id, name, price, quantity, 1L);
    }

    public Product(long id, String name, double price, int quantity, long categoryId) {
        super(id, name);
        if (price <= 0) {
            throw new InvalidPriceException("Ціна товару повинна бути більшою за 0", price);
        }
        if (quantity < 0) {
            throw new InvalidQuantityException("Кількість товару не може бути меншою за 0", quantity);
        }

        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
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

    public String getCategory() {
        return "Загальна категорія";
    }

    @Override
    public String getDisplayInfo() {
        return getName() + " (" + getCategory() + ") — " + price + " грн";
    }

    @Override
    public String toString() {
        return "Товар [id=" + getId() + "]: " + getName() + " | Категорія: " + getCategory() + " (categoryId=" + categoryId + ") | Ціна: " + price + " грн | Кількість: " + quantity + " шт.";
    }
}
