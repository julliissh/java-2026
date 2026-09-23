/**
 * Клас Customer представляє покупця інтернет-магазину.
 * Конструктор оголошує throws EmptyNameException — компілятор вимагає
 * обробки при кожному створенні об'єкта Customer.
 */
public class Customer {

    private String name;
    private String phone;

    /**
     * @throws EmptyNameException якщо ім'я або телефон порожні / складаються з пробілів
     */
    public Customer(String name, String phone) throws EmptyNameException {
        if (name == null || name.trim().isEmpty()) {
            throw new EmptyNameException("Ім'я клієнта");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new EmptyNameException("Номер телефону");
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