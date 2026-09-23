/**
 * Виключення для порожнього текстового поля (назва товару, ім'я клієнта тощо).
 * Навмисно наслідує Exception (а не ShopException) — щоб показати різні гілки
 * ієрархії виключень: catch(ShopException) не перехопить EmptyNameException.
 * Зберігає назву поля для зрозумілого повідомлення.
 */
public class EmptyNameException extends Exception {

    private final String fieldName;

    public EmptyNameException(String fieldName) {
        super("Поле \"" + fieldName + "\" не може бути порожнім.");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
