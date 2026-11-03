package entity;

public class Category extends BaseEntity {
    private final String description;

    public Category(long id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Категорія [id=" + getId() + "]: " + getName() + " (" + description + ")";
    }
}
