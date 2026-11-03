package exception;

public class InvalidQuantityException extends ShopException {
    private final int invalidQuantity;

    public InvalidQuantityException(String message, int invalidQuantity) {
        super(message);
        this.invalidQuantity = invalidQuantity;
    }

    public int getInvalidQuantity() {
        return invalidQuantity;
    }
}
