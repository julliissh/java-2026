import entity.BaseEntity;
import exception.InvalidPhoneException;

public class Customer extends BaseEntity {
    private static final String PHONE_REGEX = "^(?:\\+?38)?(?:\\(0\\d{2}\\)|0\\d{2})[-\\s]?\\d{3}[-\\s]?\\d{2}[-\\s]?\\d{2}$";

    private static final long FIRST_ID = 1;
    private static long nextId = FIRST_ID;

    private final String phone;

    public Customer(String name, String phone) {
        this(nextId++, name, phone);
    }

    public Customer(long id, String name, String phone) {
        super(id, name);
        if (phone == null || !phone.matches(PHONE_REGEX)) {
            throw new InvalidPhoneException("Номер телефону не відповідає формату (наприклад, +380991234567 або 0991234567)", phone);
        }
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Клієнт [id=" + getId() + "]: " + getName() + " (тел: " + phone + ")";
    }
}
