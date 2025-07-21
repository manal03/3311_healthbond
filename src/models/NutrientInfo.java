package models;

public class NutrientInfo {
    private final float value;
    private final String unit;

    public NutrientInfo(float value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}