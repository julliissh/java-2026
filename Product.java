public class Product {
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product(String name, String category, double price, int quantity) {
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
        return "Товар: " + name + " | Категорія: " + category + " | Ціна: " + price + " грн" + " | Кількість: " + quantity + " шт.";
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