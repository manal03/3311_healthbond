package services.strategies;

import models.FoodGroupData;
import models.UserProfile;

/**
 * Strategy interface for different Canada Food Guide alignment calculations
 * Implements the Strategy pattern to allow switching between CFG versions
 */
public interface CFGAlignmentStrategy {
    /**
     * Calculate alignment score between user's food data and guideline recommendations
     * @param data User's food group consumption data
     * @param user User profile for personalized recommendations
     * @return Alignment score as percentage (0-100)
     */
    double calculateAlignmentScore(FoodGroupData data, UserProfile user);

    /**
     * Get the name of this guideline version
     * @return String representation of the guideline name
     */
    String getGuidelineName();

    /**
     * Get detailed recommendations text for this guideline
     * @return Formatted text with recommendations
     */
    String getRecommendationsText();

    /**
     * Get recommended proportions for plate visualization
     * @return FoodGroupData representing ideal proportions
     */
    FoodGroupData getRecommendedProportions();

    /**
     * Get version identifier for UI display
     * @return Short version identifier
     */
    String getVersionIdentifier();

    /**
     * Check if this guideline includes dairy as a separate category
     * @return true if dairy is included, false otherwise
     */
    boolean includesDairy();
}