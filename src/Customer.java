import exception.InvalidPhoneException;

public class Customer {
    private static final String PHONE_REGEX = "^(?:\\+?38)?(?:\\(0\\d{2}\\)|0\\d{2})[-\\s]?\\d{3}[-\\s]?\\d{2}[-\\s]?\\d{2}$";

    private String name;
    private String phone;

    public Customer(String name, String phone) {
        if (phone == null || !phone.matches(PHONE_REGEX)) {
            throw new InvalidPhoneException("Номер телефону не відповідає формату (наприклад, +380991234567 або 0991234567)", phone);
        }
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Клієнт: " + name + " (тел: " + phone + ")";
    }
}