package product;

import entity.BaseEntity;
import entity.Category;
import exception.InsufficientStockException;
import exception.InvalidPriceException;
import exception.InvalidQuantityException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "products")
public class Product extends BaseEntity implements Purchasable, Displayable {

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryEntity;

    @Transient
    private long categoryId;

    protected Product() {
    }

    public Product(String name, double price, int quantity, Category categoryEntity) {
        super(name);
        validateAndSetPrice(price);
        validateAndSetQuantity(quantity);
        setCategoryEntity(categoryEntity);
    }

    public Product(long id, String name, double price, int quantity) {
        this(id, name, price, quantity, 1L);
    }

    public Product(long id, String name, double price, int quantity, long categoryId) {
        super(id, name);
        validateAndSetPrice(price);
        validateAndSetQuantity(quantity);
        this.categoryId = categoryId;
    }

    public Product(long id, String name, double price, int quantity, Category categoryEntity) {
        super(id, name);
        validateAndSetPrice(price);
        validateAndSetQuantity(quantity);
        setCategoryEntity(categoryEntity);
    }

    private void validateAndSetPrice(double priceValue) {
        if (priceValue <= 0) {
            throw new InvalidPriceException("Ціна товару повинна бути більшою за 0", priceValue);
        }
        this.price = BigDecimal.valueOf(priceValue).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateAndSetQuantity(int quantityValue) {
        if (quantityValue < 0) {
            throw new InvalidQuantityException("Кількість товару не може бути меншою за 0", quantityValue);
        }
        this.quantity = quantityValue;
    }

    public double getPrice() {
        if (price != null) {
            return price.doubleValue();
        }
        return 0.0;
    }

    public BigDecimal getPriceDecimal() {
        return price;
    }

    public void setPrice(double price) {
        validateAndSetPrice(price);
    }

    public void setPriceDecimal(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            double currentPrice = 0.0;
            if (price != null) {
                currentPrice = price.doubleValue();
            }
            throw new InvalidPriceException(
                    "Ціна товару повинна бути більшою за 0",
                    currentPrice
            );
        }
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        validateAndSetQuantity(quantity);
    }

    public Category getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(Category categoryEntity) {
        this.categoryEntity = categoryEntity;
        if (categoryEntity != null && categoryEntity.getId() > 0) {
            this.categoryId = categoryEntity.getId();
        }
    }

    public long getCategoryId() {
        if (categoryEntity != null && Hibernate.isInitialized(categoryEntity) && categoryEntity.getId() > 0) {
            return categoryEntity.getId();
        }
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public void buy(int amount) {
        if (amount <= 0) {
            throw new InvalidQuantityException("Кількість для покупки має бути більшою за 0", amount);
        }
        if (amount > this.quantity) {
            throw new InsufficientStockException(
                    "Недостатньо товару на складі! В наявності лише: " + this.quantity + " шт.",
                    amount,
                    this.quantity
            );
        }
        this.quantity -= amount;
    }

    public String getCategory() {
        if (categoryEntity != null && Hibernate.isInitialized(categoryEntity) && categoryEntity.getName() != null) {
            return categoryEntity.getName();
        }
        return "Загальна категорія";
    }

    @Override
    public String getDisplayInfo() {
        return getName() + " (" + getCategory() + ") — " + getPrice() + " грн";
    }

    @Override
    public String toString() {
        return "Товар [id=" + getId() + "]: " + getName()
                + " | Категорія: " + getCategory()
                + " | Ціна: " + getPrice() + " грн"
                + " | Кількість: " + quantity + " шт.";
    }
}
