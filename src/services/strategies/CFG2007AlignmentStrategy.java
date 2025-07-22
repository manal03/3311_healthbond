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

    // Default serving recommendations (can be personalized based on user profile)
    private static final int DEFAULT_VEGETABLES_SERVINGS = 7;
    private static final int DEFAULT_FRUITS_SERVINGS = 2;
    private static final int DEFAULT_GRAINS_SERVINGS = 7;
    private static final int DEFAULT_PROTEIN_SERVINGS = 2;
    private static final int DEFAULT_DAIRY_SERVINGS = 2;

    @Override
    public double calculateAlignmentScore(FoodGroupData data, UserProfile user) {
        Map<String, Integer> recommended = getCFG2007Recommendations(user);

        double totalScore = 0;
        int categories = 5;

        totalScore += calculateCategoryScore(data.getVegetables(), recommended.get("vegetables"));
        totalScore += calculateCategoryScore(data.getFruits(), recommended.get("fruits"));
        totalScore += calculateCategoryScore(data.getGrains(), recommended.get("grains"));
        totalScore += calculateCategoryScore(data.getProtein(), recommended.get("protein"));
        totalScore += calculateCategoryScore(data.getDairy(), recommended.get("dairy"));

        return totalScore / categories;
    }

    /**
     * Calculate score for a single category based on actual vs recommended servings
     */
    private double calculateCategoryScore(double actual, int recommended) {
        if (recommended == 0) return 100;

        double ratio = actual / recommended;

        if (ratio >= 0.8 && ratio <= 1.2) {
            return 100;
        } else if (ratio >= 0.6 && ratio <= 1.4) {
            return 80;
        } else if (ratio >= 0.4 && ratio <= 1.6) {
            return 60;
        } else {
            return Math.max(0, 40 - Math.abs(ratio - 1.0) * 20);
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
                "Vegetables and Fruits: 7-10 servings per day\n" +
                "• Eat at least one dark green and one orange vegetable each day\n" +
                "• Choose vegetables and fruit prepared with little or no added fat, sugar or salt\n" +
                "• Have vegetables and fruit more often than juice\n\n" +
                "Grain Products: 6-8 servings per day\n" +
                "• Make at least half of your grain products whole grain each day\n" +
                "• Choose grain products that are lower in fat, sugar or salt\n\n" +
                "Milk and Alternatives: 2-3 servings per day\n" +
                "• Drink skim, 1%, or 2% milk each day\n" +
                "• Select lower fat milk alternatives\n\n" +
                "Meat and Alternatives: 2-3 servings per day\n" +
                "• Have meat alternatives such as beans, lentils and tofu often\n" +
                "• Eat at least two Food Guide Servings of fish each week\n" +
                "• Select lean meat and alternatives prepared with little or no added fat or salt\n\n" +
                "SERVING SIZE EXAMPLES:\n" +
                "• Vegetables: 125 mL (1⁄2 cup) fresh, frozen or canned vegetables\n" +
                "• Fruits: 1 medium fruit or 125 mL (1⁄2 cup) fresh, frozen or canned fruit\n" +
                "• Grains: 1 slice bread or 125 mL (1⁄2 cup) cooked rice, pasta or cereal\n" +
                "• Protein: 75 g (21⁄2 oz.) cooked fish, poultry, lean meat\n" +
                "• Dairy: 250 mL (1 cup) milk or fortified soy beverage\n\n" +
                "Your alignment score is calculated based on how closely\n" +
                "your daily servings match these recommendations.";
    }
}