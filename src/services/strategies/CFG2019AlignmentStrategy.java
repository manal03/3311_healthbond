package services.strategies;

import models.FoodGroupData;
import models.UserProfile;

/**
 * Canada Food Guide 2019 alignment strategy implementation
 * Focuses on plate proportions: 50% vegetables/fruits, 25% grains, 25% protein
 */
public class CFG2019AlignmentStrategy implements CFGAlignmentStrategy {

    // CFG 2019 recommended plate proportions
    private static final double VEGETABLES_FRUITS_PERCENT = 50.0;
    private static final double GRAINS_PERCENT = 25.0;
    private static final double PROTEIN_PERCENT = 25.0;

    @Override
    public double calculateAlignmentScore(FoodGroupData data, UserProfile user) {
        if (data.getTotalServings() == 0) {
            return 0.0;
        }

        // CFG 2019: 50% vegetables/fruits, 25% grains, 25% protein
        double vegFruitPercent = data.getVegetablesPercentage() + data.getFruitsPercentage();
        double grainPercent = data.getGrainsPercentage();
        double proteinPercent = data.getProteinPercentage();

        double vegScore = calculateCategoryScore(vegFruitPercent, VEGETABLES_FRUITS_PERCENT);
        double grainScore = calculateCategoryScore(grainPercent, GRAINS_PERCENT);
        double proteinScore = calculateCategoryScore(proteinPercent, PROTEIN_PERCENT);

        return (vegScore + grainScore + proteinScore) / 3;
    }

    /**
     * Calculate score for a single category based on deviation from target
     */
    private double calculateCategoryScore(double actual, double target) {
        double deviation = Math.abs(actual - target);
        if (deviation <= 5) return 100;
        if (deviation <= 10) return 90;
        if (deviation <= 15) return 75;
        if (deviation <= 25) return 50;

        return Math.max(0, 25 - deviation);
    }

    @Override
    public String getGuidelineName() {
        return "Canada's Food Guide 2019";
    }

    @Override
    public String getVersionIdentifier() {
        return "CFG 2019";
    }

    @Override
    public boolean includesDairy() {
        return false;
    }

    @Override
    public FoodGroupData getRecommendedProportions() {
        return new FoodGroupData(50.0, 0.0, GRAINS_PERCENT, PROTEIN_PERCENT, 0.0);
    }

    @Override
    public String getRecommendationsText() {
        return "CANADA'S FOOD GUIDE - 2019 RECOMMENDATIONS\n\n" +
                "Make half your plate vegetables and fruits:\n" +
                "• Choose vegetables and fruits more often than juice\n" +
                "• Choose a variety of vegetables and fruits\n" +
                "• Frozen or canned vegetables and fruits are nutritious too\n\n" +
                "Make one quarter of your plate whole grain foods:\n" +
                "• Choose whole grain foods like brown rice, quinoa, oats\n" +
                "• Choose whole grain bread, pasta, and cereals\n\n" +
                "Make one quarter of your plate protein foods:\n" +
                "• Choose protein foods that come from plants more often\n" +
                "• Include fish, shellfish, eggs, poultry, lean red meat\n" +
                "• Choose lower sodium options\n\n" +
                "Make water your drink of choice:\n" +
                "• Replace sugary drinks with water\n" +
                "• Use unflavoured milk in cereal, coffee, or tea\n\n" +
                "CHANGES FROM 2007 GUIDE:\n" +
                "• Simplified from 4 food groups to 3 main categories\n" +
                "• Emphasis on plant-based proteins\n" +
                "• Water instead of dairy as beverage recommendation\n" +
                "• Focus on eating patterns rather than specific servings\n\n" +
                "Your alignment score is calculated based on how closely\n" +
                "your plate proportions match these recommendations.";
    }
}