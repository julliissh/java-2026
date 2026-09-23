/**
 * Базовий клас для всіх доменних виключень інтернет-магазину.
 * Наслідує checked Exception — компілятор вимагає обробки (try-catch або throws).
 * Від цього класу наслідуються InvalidPriceException та InvalidQuantityException.
 */
public class ShopException extends Exception {

    public ShopException(String message) {
        super(message);
    }
}
