package tn.health.careservice.entities;

public enum ChronicDisease {
    DIABETE("Diabète"),
    ASTHME("Asthme"),
    HYPERTENSION("Hypertension"),
    CANCER("Cancer"),
    CARDIOPATHIE("Cardiopathie"),
    AUTRE("Autre");

    private final String description;

    ChronicDisease(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
