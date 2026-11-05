package entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.Hibernate;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected BaseEntity() {
    }

    protected BaseEntity(String name) {
        setName(name);
    }

    protected BaseEntity(long id, String name) {
        setName(name);
        if (id > 0) {
            this.id = id;
        } else {
            this.id = null;
        }
    }

    public long getId() {
        if (id != null) {
            return id;
        }
        return 0L;
    }

    public Long getIdWrapper() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id > 0) {
            this.id = id;
        } else {
            this.id = null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва сутності не може бути порожньою");
        }
        this.name = name.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(obj)) return false;
        BaseEntity other = (BaseEntity) obj;
        return id != null && id > 0 && id.equals(other.getIdWrapper());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }

    @Override
    public String toString() {
        return "Сутність [id=" + getId() + "]: " + name;
    }
}
