public interface Purchasable {
    void buy(int amount);
    int getQuantity();

    default boolean isInStock() {
        return getQuantity() > 0;
    }
}
