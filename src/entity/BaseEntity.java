package entity;

public abstract class BaseEntity {
    private final long id;
    private final String name;

    protected BaseEntity(long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва сутності не може бути порожньою");
        }
        this.id = id;
        this.name = name.trim();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity other = (BaseEntity) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
