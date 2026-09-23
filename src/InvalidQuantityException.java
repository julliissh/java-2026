/**
 * Виключення для некоректної кількості товару (кількість <= 0).
 * Наслідує ShopException (checked).
 * Зберігає некоректне значення для зрозумілого повідомлення про помилку.
 */
public class InvalidQuantityException extends ShopException {

    private final int invalidQuantity;

    public InvalidQuantityException(int quantity) {
        super("Некоректна кількість: " + quantity + " шт. Кількість повинна бути > 0.");
        this.invalidQuantity = quantity;
    }

    public int getInvalidQuantity() {
        return invalidQuantity;
    }
}
