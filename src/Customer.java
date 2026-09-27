import exception.InvalidPhoneException;

public class Customer {
    public static final int MIN_PHONE_LENGTH = 10;
    public static final int MAX_PHONE_LENGTH = 13;

    private String name;
    private String phone;

    public Customer(String name, String phone) throws InvalidPhoneException {
        if (phone == null || phone.length() < MIN_PHONE_LENGTH || phone.length() > MAX_PHONE_LENGTH) {
            throw new InvalidPhoneException("Номер телефону має містити від " + MIN_PHONE_LENGTH + " до " + MAX_PHONE_LENGTH + " символів", phone);
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