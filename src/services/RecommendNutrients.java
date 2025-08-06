package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.RecommendationInterface;
import models.UserProfile;
import utility.ConnectionProvider;

/**
 * Implements RecommendationInterface to fetch daily nutrient goals
 * for a user based on their profile.
 */
public class RecommendNutrients implements RecommendationInterface {

    @Override
    public Map<String, Double> findForUser(UserProfile user) {
        Map<String, Double> recommendations = new HashMap<>();

        String sql = "SELECT " +
                "    nr.recommended_calories_per_day, " +
                "    nr.recommended_carbs_grams, " +
                "    nr.recommended_protein_grams, " +
                "    nr.recommended_fat_grams, " +
                "    nr.recommended_fiber_grams " +
                "FROM " +
                "    users u " +
                "JOIN " +
                "    nutrientrecommendations nr ON " +
                "        TIMESTAMPDIFF(YEAR, u.dateofbirth, CURDATE()) BETWEEN nr.age_min AND nr.age_max " +
                "        AND LOWER(u.sex) = LOWER(nr.gender) " +
                "        AND u.weight_kg BETWEEN nr.weight_min_kg AND nr.weight_max_kg " +
                "        AND u.height_cm BETWEEN nr.height_min_cm AND nr.height_max_cm " +
                "WHERE " +
                "    u.idusers = ?;";

        try (Connection conn = ConnectionProvider.getCon()) {
            if (conn == null) {
                System.err.println("RecommendNutrients Error: Failed to get database connection.");
                return recommendations; // return empty map
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, user.getUserId());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        recommendations.put("CALORIES", rs.getDouble("recommended_calories_per_day"));
                        recommendations.put("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", rs.getDouble("recommended_carbs_grams"));
                        recommendations.put("Protein", rs.getDouble("recommended_protein_grams"));
                        recommendations.put("FAT (TOTAL LIPIDS)", rs.getDouble("recommended_fat_grams"));
                        recommendations.put("FIBRE, TOTAL DIETARY", rs.getDouble("recommended_fiber_grams"));  // consistent casing
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while fetching recommendations: " + e.getMessage());
            e.printStackTrace();
        }

        return recommendations;
    }
}