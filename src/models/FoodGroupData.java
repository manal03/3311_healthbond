package models;

public class FoodGroupData {
    private double vegetables;
    private double fruits;
    private double grains;
    private double protein;
    private double dairy;

    // CFG 2019 recommended percentages
    public static final double CFG_VEGETABLES_PERCENT = 50.0;
    public static final double CFG_FRUITS_PERCENT = 0.0; // Included in vegetables in 2019 guide
    public static final double CFG_GRAINS_PERCENT = 25.0;
    public static final double CFG_PROTEIN_PERCENT = 25.0;
    public static final double CFG_DAIRY_PERCENT = 0.0; // Water is recommended instead


    public static final int CFG_2007_VEGETABLES_SERVINGS = 7;
    public static final int CFG_2007_FRUITS_SERVINGS = 2;
    public static final int CFG_2007_GRAINS_SERVINGS = 7;
    public static final int CFG_2007_PROTEIN_SERVINGS = 2;
    public static final int CFG_2007_DAIRY_SERVINGS = 2;

    public FoodGroupData() {
        this.vegetables = 0.0;
        this.fruits = 0.0;
        this.grains = 0.0;
        this.protein = 0.0;
        this.dairy = 0.0;
    }

    public FoodGroupData(double vegetables, double fruits, double grains, double protein, double dairy) {
        this.vegetables = vegetables;
        this.fruits = fruits;
        this.grains = grains;
        this.protein = protein;
        this.dairy = dairy;
    }

    public double getVegetables() { return vegetables; }
    public double getFruits() { return fruits; }
    public double getGrains() { return grains; }
    public double getProtein() { return protein; }
    public double getDairy() { return dairy; }

    public void setVegetables(double vegetables) { this.vegetables = vegetables; }
    public void setFruits(double fruits) { this.fruits = fruits; }
    public void setGrains(double grains) { this.grains = grains; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setDairy(double dairy) { this.dairy = dairy; }

    public double getTotalServings() {
        return vegetables + fruits + grains + protein + dairy;
    }

    public double getVegetablesPercentage() {
        double total = getTotalServings();
        return total > 0 ? (vegetables / total) * 100 : 0;
    }

    public double getFruitsPercentage() {
        double total = getTotalServings();
        return total > 0 ? (fruits / total) * 100 : 0;
    }

    public double getGrainsPercentage() {
        double total = getTotalServings();
        return total > 0 ? (grains / total) * 100 : 0;
    }

    public double getProteinPercentage() {
        double total = getTotalServings();
        return total > 0 ? (protein / total) * 100 : 0;
    }

    public double getDairyPercentage() {
        double total = getTotalServings();
        return total > 0 ? (dairy / total) * 100 : 0;
    }

    @Deprecated
    public double getCFG2019AlignmentScore() {
        double vegFruitPercent = getVegetablesPercentage() + getFruitsPercentage();
        double grainPercent = getGrainsPercentage();
        double proteinPercent = getProteinPercentage();

        double vegScore = Math.max(0, 100 - Math.abs(vegFruitPercent - CFG_VEGETABLES_PERCENT) * 2);
        double grainScore = Math.max(0, 100 - Math.abs(grainPercent - CFG_GRAINS_PERCENT) * 4);
        double proteinScore = Math.max(0, 100 - Math.abs(proteinPercent - CFG_PROTEIN_PERCENT) * 4);

        return (vegScore + grainScore + proteinScore) / 3;
    }

    @Override
    public String toString() {
        return String.format("FoodGroupData{vegetables=%.1f, fruits=%.1f, grains=%.1f, protein=%.1f, dairy=%.1f}",
                vegetables, fruits, grains, protein, dairy);
    }
}