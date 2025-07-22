package models;

public class NutrientInfo {
    private final String name;
    private final float value;
    private final String unit;

    public NutrientInfo(float value, String unit) {
        this("Unknown Nutrient", value, unit);
    }

    public NutrientInfo(String name, float amount, String unit) {
        this.name = name;
        this.value = amount;
        this.unit = unit;
    }

    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }
}