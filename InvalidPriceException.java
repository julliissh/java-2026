/**
 * Виключення для некоректної ціни товару (ціна <= 0).
 * Наслідує ShopException (checked).
 * Зберігає некоректне значення для зрозумілого повідомлення про помилку.
 */
public class InvalidPriceException extends ShopException {

    private final double invalidPrice;

    public InvalidPriceException(double price) {
        super("Некоректна ціна: " + price + " грн. Ціна повинна бути > 0.");
        this.invalidPrice = price;
    }

    public double getInvalidPrice() {
        return invalidPrice;
    }
}
