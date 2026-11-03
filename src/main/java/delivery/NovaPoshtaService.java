package delivery;

public class NovaPoshtaService {

    public int computeTariff(int valueInKopiyky) {
        return 8000 + (valueInKopiyky / 100);
    }

    public String getServiceTitle() {
        return "Нова Пошта (стороннє API)";
    }
}
