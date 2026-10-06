import exception.InsufficientStockException;
import exception.InvalidPriceException;
import exception.InvalidQuantityException;

public class Product implements Purchasable, Displayable {
    private final String name;
    private final String category;
    private final double price;
    private int quantity;

    public Product(String name, String category, double price, int quantity) {
        if (price <= 0) {
            throw new InvalidPriceException("Ціна товару повинна бути більшою за 0", price);
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Кількість товару повинна бути більшою за 0", quantity);
        }

        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String getDisplayInfo() {
        return name + " (" + category + ") — " + price + " грн";
    }

    @Override
    public void buy(int amount) {
        if (amount <= 0) {
            throw new InvalidQuantityException("Кількість для покупки має бути більшою за 0", amount);
        }
        if (amount > this.quantity) {
            throw new InsufficientStockException("Недостатньо товару на складі! В наявності лише: " + this.quantity + " шт.", amount, this.quantity);
        }
        this.quantity -= amount;
    }

    @Override
    public String toString() {
        return "Товар: " + name + " | Категорія: " + category + " | Ціна: " + price + " грн | Кількість: " + quantity + " шт.";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        return this.getPrice() == other.getPrice() &&
               this.getQuantity() == other.getQuantity() &&
               this.getName().equals(other.getName()) &&
               this.getCategory().equals(other.getCategory());
    }
}