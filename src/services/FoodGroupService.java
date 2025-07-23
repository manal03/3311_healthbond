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

    public static CFGAlignmentStrategy getAlignmentStrategy() {
        return alignmentStrategy;
    }

    public static double calculateAlignmentScore(FoodGroupData data, UserProfile user) {
        return alignmentStrategy.calculateAlignmentScore(data, user);
    }

    public static String getRecommendationsText() {
        return alignmentStrategy.getRecommendationsText();
    }

    public static String getGuidelineName() {
        return alignmentStrategy.getGuidelineName();
    }

    public static FoodGroupData getRecommendedProportions() {
        return alignmentStrategy.getRecommendedProportions();
    }

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
     * Get sample data for demonstration purposes
     */
    public static FoodGroupData getSampleFoodGroupData() {
        return new FoodGroupData(3.2, 1.8, 4.5, 2.1, 1.4);
    }

}