/**
 * Клас Product представляє товар у каталозі / кошику покупця.
 * Конструктор оголошує throws для трьох типів кастомних виключень —
 * компілятор вимагає їх обробки при кожному створенні об'єкта Product.
 *
 * Рефакторинг toString(): String.format замість конкатенації —
 * вирівнювання колонок для зручного читання виводу (за зауваженням викладача).
 */
public class Product {

    private String name;
    private String category;
    private double price;
    private int quantity;

    /**
     * @throws EmptyNameException        якщо назва або категорія порожні
     * @throws InvalidPriceException     якщо ціна <= 0
     * @throws InvalidQuantityException  якщо кількість <= 0
     */
    public Product(String name, String category, double price, int quantity)
            throws EmptyNameException, InvalidPriceException, InvalidQuantityException {

        if (name == null || name.trim().isEmpty()) {
            throw new EmptyNameException("Назва товару");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new EmptyNameException("Категорія товару");
        }
        if (price <= 0) {
            throw new InvalidPriceException(price);
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException(quantity);
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

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("Товар: %-20s | Категорія: %-12s | Ціна: %9.2f грн | Кількість: %3d шт.",
                name, category, price, quantity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Product other = (Product) obj;
        return this.getPrice() == other.getPrice() &&
               this.getQuantity() == other.getQuantity() &&
               this.getName().equals(other.getName()) &&
               this.getCategory().equals(other.getCategory());
    }
}