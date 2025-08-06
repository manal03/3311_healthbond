package models;

import java.awt.Color;

public class NutrientSummary {
    private final String name;
    private final double dailyAverage;
    private final double recommended;
    private final String unit;
    private final double percentageOfTotal;
    private final double percentageOfRecommended;

    public NutrientSummary(String name, double dailyAverage, double recommended,
                           String unit, double percentageOfTotal) {
        this.name = name;
        this.dailyAverage = dailyAverage;
        this.recommended = recommended;
        this.unit = unit;
        this.percentageOfTotal = percentageOfTotal;
        this.percentageOfRecommended = recommended > 0 ?
                (dailyAverage / recommended) * 100 : 0;
    }

    public String getName() {
        return name;
    }

    public double getDailyAverage() {
        return dailyAverage;
    }

    public double getRecommended() {
        return recommended;
    }

    public String getUnit() {
        return unit;
    }

    public double getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public double getPercentageOfRecommended() {
        return percentageOfRecommended;
    }

    public boolean isAdequate() {
        return percentageOfRecommended >= 70 && percentageOfRecommended < 100;
    }

    public boolean isOptimal() {
        return percentageOfRecommended >= 100;
    }

    public Color getStatusColor() {
        if (isOptimal()) {
            return new Color(50, 168, 82); // Green
        } else if (isAdequate()) {
            return new Color(255, 193, 7); // Amber
        } else {
            return new Color(220, 53, 69); // Red
        }
    }

    public boolean isKeyNutrient() {
        return switch (name.toUpperCase()) {
            case "ENERGY (KILOCALORIES)", "PROTEIN", "FAT (TOTAL LIPIDS)",
                 "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FIBRE, TOTAL DIETARY" -> true;
            default -> false;
        };
    }

    public String getDisplayName() {
        return switch (name.toUpperCase()) {
            case "ENERGY (KILOCALORIES)" -> "Calories";
            case "PROTEIN" -> "Protein";
            case "FAT (TOTAL LIPIDS)" -> "Fat";
            case "CARBOHYDRATE, TOTAL (BY DIFFERENCE)" -> "Carbs";
            case "FIBRE, TOTAL DIETARY" -> "Fiber";
            default -> name.length() > 17 ? name.substring(0, 15) + "…" : name;
        };
    }
}
