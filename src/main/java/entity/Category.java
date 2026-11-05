package entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import product.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "uk_categories_name", columnNames = "name")
)
public class Category extends BaseEntity {

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(
            mappedBy = "categoryEntity",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false
    )
    private List<Product> products = new ArrayList<>();

    protected Category() {
    }

    public Category(String name, String description) {
        super(name);
        this.description = description;
    }

    public Category(long id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Товар не може бути null");
        }
        if (!products.contains(product)) {
            products.add(product);
        }
        product.setCategoryEntity(this);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            return;
        }
        products.remove(product);
        if (product.getCategoryEntity() == this) {
            product.setCategoryEntity(null);
        }
    }

    @Override
    public String toString() {
        return "Категорія [id=" + getId() + "]: " + getName() + " (" + description + ")";
    }
}
