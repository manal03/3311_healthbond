package services.strategies;

import models.FoodGroupData;
import models.UserProfile;
import java.util.HashMap;
import java.util.Map;

/**
 * Canada Food Guide 2007 alignment strategy implementation
 * Focuses on specific serving counts for each food group
 */
public class CFG2007AlignmentStrategy implements CFGAlignmentStrategy {

    // CFG 2007 recommended plate proportions for visualization
    private static final double VEGETABLES_PERCENT = 35.0;
    private static final double FRUITS_PERCENT = 15.0;
    private static final double GRAINS_PERCENT = 25.0;
    private static final double PROTEIN_PERCENT = 20.0;
    private static final double DAIRY_PERCENT = 5.0;

    private static final int DEFAULT_VEGETABLES_SERVINGS = 5;
    private static final int DEFAULT_FRUITS_SERVINGS = 3;
    private static final int DEFAULT_GRAINS_SERVINGS = 7;
    private static final int DEFAULT_PROTEIN_SERVINGS = 2;
    private static final int DEFAULT_DAIRY_SERVINGS = 2;

    @Override
    public double calculateAlignmentScore(FoodGroupData data, UserProfile user) {
        if (data.getTotalServings() == 0) {
            return 0.0;
        }

        // For CFG 2007, we can use either serving-based or percentage-based scoring
        // Using percentage-based for consistency with CFG 2019
        double vegScore = calculateCategoryScore(data.getVegetablesPercentage(), VEGETABLES_PERCENT);
        double fruitScore = calculateCategoryScore(data.getFruitsPercentage(), FRUITS_PERCENT);
        double grainScore = calculateCategoryScore(data.getGrainsPercentage(), GRAINS_PERCENT);
        double proteinScore = calculateCategoryScore(data.getProteinPercentage(), PROTEIN_PERCENT);
        double dairyScore = calculateCategoryScore(data.getDairyPercentage(), DAIRY_PERCENT);

        // Average all five categories for CFG 2007
        return (vegScore + fruitScore + grainScore + proteinScore + dairyScore) / 5;
    }

    /**
     * Calculate score for a single category based on percentage deviation
     */
    private double calculateCategoryScore(double actual, double target) {
        double difference = Math.abs(actual - target);

        if (difference <= 5) return 100;
        if (difference <= 10) return 85;
        if (difference <= 15) return 70;
        if (difference <= 20) return 55;
        if (difference <= 25) return 40;
        if (difference<= 30) return 25;

        return Math.max(0, 10);
    }

    /**
     * Alternative scoring method based on serving counts (for future use)
     */
    private double calculateServingBasedScore(FoodGroupData data, UserProfile user, int days) {
        Map<String, Integer> recommended = getCFG2007Recommendations(user);

        double dailyVeg = data.getVegetables() / days;
        double dailyFruit = data.getFruits() / days;
        double dailyGrains = data.getGrains() / days;
        double dailyProtein = data.getProtein() / days;
        double dailyDairy = data.getDairy() / days;

        double vegScore = calculateServingScore(dailyVeg, recommended.get("vegetables"));
        double fruitScore = calculateServingScore(dailyFruit, recommended.get("fruits"));
        double grainScore = calculateServingScore(dailyGrains, recommended.get("grains"));
        double proteinScore = calculateServingScore(dailyProtein, recommended.get("protein"));
        double dairyScore = calculateServingScore(dailyDairy, recommended.get("dairy"));

        return (vegScore + fruitScore + grainScore + proteinScore + dairyScore) / 5;
    }

    /**
     * Calculate score for a single category based on actual vs recommended servings
     */
    private double calculateServingScore(double actual, int recommended) {
        if (recommended == 0) return 100;

        double ratio = actual / recommended;

        if (ratio >= 0.8 && ratio <= 1.2) {
            return 100;
        } else if (ratio >= 0.6 && ratio <= 1.4) {
            return 80;
        } else if (ratio >= 0.4 && ratio <= 1.6) {
            return 60;
        } else if (ratio >= 0.2 && ratio <= 1.8) {
            return 40;
        } else {
            return Math.max(0, 20);
        }
    }

    /**
     * Get personalized CFG 2007 recommendations based on user profile
     */
    private Map<String, Integer> getCFG2007Recommendations(UserProfile user) {
        Map<String, Integer> recommendations = new HashMap<>();

        recommendations.put("vegetables", DEFAULT_VEGETABLES_SERVINGS);
        recommendations.put("fruits", DEFAULT_FRUITS_SERVINGS);
        recommendations.put("grains", DEFAULT_GRAINS_SERVINGS);
        recommendations.put("protein", DEFAULT_PROTEIN_SERVINGS);
        recommendations.put("dairy", DEFAULT_DAIRY_SERVINGS);

        return recommendations;
    }

    @Override
    public String getGuidelineName() {
        return "Canada's Food Guide 2007";
    }

    @Override
    public String getVersionIdentifier() {
        return "CFG 2007";
    }

    @Override
    public boolean includesDairy() {
        return true;
    }

    @Override
    public FoodGroupData getRecommendedProportions() {
        return new FoodGroupData(VEGETABLES_PERCENT, FRUITS_PERCENT, GRAINS_PERCENT, PROTEIN_PERCENT, DAIRY_PERCENT);
    }

    @Override
    public String getRecommendationsText() {
        return "CANADA'S FOOD GUIDE - 2007 RECOMMENDATIONS\n\n" +
                "Daily Serving Recommendations:\n\n" +
                "Vegetables and Fruits: 7-10 servings per day\n" +
                "• Vegetables: ~5 servings (35% of plate)\n" +
                "• Fruits: ~3 servings (15% of plate)\n" +
                "• Eat at least one dark green and one orange vegetable each day\n" +
                "• Choose vegetables and fruit prepared with little or no added fat, sugar or salt\n" +
                "• Have vegetables and fruit more often than juice\n\n" +
                "Grain Products: 6-8 servings per day (25% of plate)\n" +
                "• Make at least half of your grain products whole grain each day\n" +
                "• Choose grain products that are lower in fat, sugar or salt\n\n" +
                "Meat and Alternatives: 2-3 servings per day (20% of plate)\n" +
                "• Have meat alternatives such as beans, lentils and tofu often\n" +
                "• Eat at least two Food Guide Servings of fish each week\n" +
                "• Select lean meat and alternatives prepared with little or no added fat or salt\n\n" +
                "Milk and Alternatives: 2-3 servings per day (5% of plate)\n" +
                "• Drink skim, 1%, or 2% milk each day\n" +
                "• Select lower fat milk alternatives\n\n" +
                "SCORING:\n" +
                "Your alignment score is calculated based on how closely your\n" +
                "food group proportions match the recommended percentages:\n" +
                "• Vegetables: ~35%, Fruits: ~15%, Grains: ~25%\n" +
                "• Protein: ~20%, Dairy: ~5%";
    }
}