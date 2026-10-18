package discount;

public class DiscountFactory {

    public static DiscountStrategy createDiscount(String promoCode) {
        if (PromoCodeDiscount.PROMO_CODE.equals(promoCode)) {
            return new PromoCodeDiscount();
        }
        return new ThresholdDiscount();
    }

    public static DiscountStrategy[] createAllStrategies() {
        return new DiscountStrategy[] {
            new NoDiscount(),
            new ThresholdDiscount(),
            new PromoCodeDiscount()
        };
    }
}
