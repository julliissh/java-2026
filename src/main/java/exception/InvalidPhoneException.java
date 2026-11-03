package exception;

public class InvalidPhoneException extends ShopException {
    private final String invalidPhone;

    public InvalidPhoneException(String message, String invalidPhone) {
        super(message);
        this.invalidPhone = invalidPhone;
    }

    public String getInvalidPhone() {
        return invalidPhone;
    }
}
