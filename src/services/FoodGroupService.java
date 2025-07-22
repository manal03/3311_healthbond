package services;

import models.FoodGroupData;
import models.UserProfile;
import services.strategies.CFGAlignmentStrategy;
import services.strategies.CFG2019AlignmentStrategy;
import utility.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for food group operations using Strategy pattern
 * Manages CFG alignment calculations and food classification
 */
public class FoodGroupService {

    // Strategy instance - implements Strategy pattern for CFG calculations
    private static CFGAlignmentStrategy alignmentStrategy = new CFG2019AlignmentStrategy(); // Default to 2019

    /**
     * Set the alignment strategy (Strategy Pattern - allows switching algorithms at runtime)
     */
    public static void setAlignmentStrategy(CFGAlignmentStrategy strategy) {
        alignmentStrategy = strategy;
    }

    /**
     * Get current alignment strategy
     */
    public static CFGAlignmentStrategy getAlignmentStrategy() {
        return alignmentStrategy;
    }

    /**
     * Calculate alignment score using current strategy
     * This delegates to the current strategy implementation
     */
    public static double calculateAlignmentScore(FoodGroupData data, UserProfile user) {
        return alignmentStrategy.calculateAlignmentScore(data, user);
    }

    /**
     * Get recommendations text from current strategy
     */
    public static String getRecommendationsText() {
        return alignmentStrategy.getRecommendationsText();
    }

    /**
     * Get guideline name from current strategy
     */
    public static String getGuidelineName() {
        return alignmentStrategy.getGuidelineName();
    }

    /**
     * Get recommended proportions from current strategy
     */
    public static FoodGroupData getRecommendedProportions() {
        return alignmentStrategy.getRecommendedProportions();
    }

    /**
     * Check if current strategy includes dairy
     */
    public static boolean includesDairy() {
        return alignmentStrategy.includesDairy();
    }

    /**
     * Get user's food group data for specified time frame
     */
    public static FoodGroupData getUserFoodGroupData(UserProfile user, int days) {
        FoodGroupData data = new FoodGroupData();

        try (Connection con = ConnectionProvider.getCon()) {
            String query = """
                SELECT fc.food_group, SUM(mi.quantity) as total_quantity
                FROM meals m 
                JOIN meal_items mi ON m.idmeals = mi.meal_id 
                JOIN food_classifications fc ON mi.food_name = fc.food_name
                WHERE m.idusers = ? 
                AND m.date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
                GROUP BY fc.food_group
                """;

            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, user.getUserId());
            stmt.setInt(2, days);
            ResultSet rs = stmt.executeQuery();

            Map<String, Double> groupTotals = new HashMap<>();
            groupTotals.put("vegetables", 0.0);
            groupTotals.put("fruits", 0.0);
            groupTotals.put("grains", 0.0);
            groupTotals.put("protein", 0.0);
            groupTotals.put("dairy", 0.0);

            while (rs.next()) {
                String foodGroup = rs.getString("food_group").toLowerCase();
                double totalQuantity = rs.getDouble("total_quantity");

                if (groupTotals.containsKey(foodGroup)) {
                    groupTotals.put(foodGroup, totalQuantity);
                }
            }

            data.setVegetables(groupTotals.get("vegetables"));
            data.setFruits(groupTotals.get("fruits"));
            data.setGrains(groupTotals.get("grains"));
            data.setProtein(groupTotals.get("protein"));
            data.setDairy(groupTotals.get("dairy"));

            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            data = getSampleFoodGroupData();
        }

        return data;
    }

    /**
     * Get food group classification from database
     */
    public static String classifyFood(String foodName) {
        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT food_group FROM food_classifications WHERE food_name = ?";

            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, foodName.toLowerCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String foodGroup = rs.getString("food_group");
                stmt.close();
                return foodGroup.toLowerCase();
            }

            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Food not found in classification table
    }

    /**
     * Add a new food classification to the database
     */
    public static boolean addFoodClassification(String foodName, String foodGroup) {
        try (Connection con = ConnectionProvider.getCon()) {
            String query = "INSERT INTO food_classifications (food_name, food_group) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE food_group = ?";

            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, foodName.toLowerCase());
            stmt.setString(2, foodGroup.toLowerCase());
            stmt.setString(3, foodGroup.toLowerCase());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all food classifications from database (useful for admin/management)
     */
    public static Map<String, String> getAllFoodClassifications() {
        Map<String, String> classifications = new HashMap<>();

        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT food_name, food_group FROM food_classifications ORDER BY food_group, food_name";

            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                classifications.put(rs.getString("food_name"), rs.getString("food_group"));
            }

            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classifications;
    }

    /**
     * Get sample data for demonstration purposes
     */
    public static FoodGroupData getSampleFoodGroupData() {
        return new FoodGroupData(3.2, 1.8, 4.5, 2.1, 1.4);
    }

    /**
     * Get detailed food group breakdown as text
     */
    public static String getFoodGroupBreakdown(FoodGroupData data) {
        StringBuilder breakdown = new StringBuilder();
        breakdown.append("FOOD GROUP BREAKDOWN:\n\n");
        breakdown.append(String.format("Vegetables: %.1f servings (%.1f%%)\n",
                data.getVegetables(), data.getVegetablesPercentage()));
        breakdown.append(String.format("Fruits: %.1f servings (%.1f%%)\n",
                data.getFruits(), data.getFruitsPercentage()));
        breakdown.append(String.format("Grains: %.1f servings (%.1f%%)\n",
                data.getGrains(), data.getGrainsPercentage()));
        breakdown.append(String.format("Protein: %.1f servings (%.1f%%)\n",
                data.getProtein(), data.getProteinPercentage()));

        if (includesDairy()) {
            breakdown.append(String.format("Dairy: %.1f servings (%.1f%%)\n",
                    data.getDairy(), data.getDairyPercentage()));
        }

        breakdown.append(String.format("\nTotal Servings: %.1f\n", data.getTotalServings()));
        breakdown.append(String.format("Current Guideline: %s\n", getGuidelineName()));

        return breakdown.toString();
    }
}