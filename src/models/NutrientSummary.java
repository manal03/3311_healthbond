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

    public String getName() { return name; }
    public double getDailyAverage() { return dailyAverage; }
    public double getRecommended() { return recommended; }
    public String getUnit() { return unit; }
    public double getPercentageOfTotal() { return percentageOfTotal; }
    public double getPercentageOfRecommended() { return percentageOfRecommended; }

    // Returns a color based on how close the intake is to the recommendation
    public Color getStatusColor() {
        if (percentageOfRecommended >= 100) {
            return new Color(50, 168, 82); // Green
        } else if (percentageOfRecommended >= 70) {
            return new Color(255, 193, 7); // Amber
        } else {
            return new Color(220, 53, 69); // Red
        }
    }

}


