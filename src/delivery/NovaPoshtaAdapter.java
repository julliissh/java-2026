package delivery;

public class NovaPoshtaAdapter implements Delivery {
    private final NovaPoshtaService novaPoshta;

    public NovaPoshtaAdapter(NovaPoshtaService novaPoshta) {
        this.novaPoshta = novaPoshta;
    }

    @Override
    public double calculateCost(double orderSum) {
        int sumInKopiyky = (int) (orderSum * 100);
        int tariffInKopiyky = novaPoshta.computeTariff(sumInKopiyky);
        return tariffInKopiyky / 100.0;
    }

    @Override
    public String getName() {
        return novaPoshta.getServiceTitle();
    }
}
