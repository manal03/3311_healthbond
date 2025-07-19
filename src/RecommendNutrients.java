import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

// Assume your UserProfile and ConnectionProvider classes exist.

/**
 * This class implements the RecommendationInterface. It handles all the SQL
 * logic to query the nutrientrecommendations table and find the appropriate
 * daily goals for a user.
 */
public class RecommendNutrients implements RecommendationInterface {

    @Override
    public Map<String, Double> findForUser(UserProfile user) {
        // Create a map to be returned. It will be empty if no results are found.
        Map<String, Double> recommendations = new HashMap<>();

        // This is the final, correct SQL query from the Canvas.
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
                return recommendations; // Return empty map
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set the user ID for the WHERE clause
                pstmt.setInt(1, user.getUserId());

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // If a record is found, populate the map.
                    recommendations.put("Calories", rs.getDouble("recommended_calories_per_day"));
                    recommendations.put("Carbs", rs.getDouble("recommended_carbs_grams"));
                    recommendations.put("Protein", rs.getDouble("recommended_protein_grams"));
                    recommendations.put("Fat", rs.getDouble("recommended_fat_grams"));
                    recommendations.put("Fiber", rs.getDouble("recommended_fiber_grams"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while fetching recommendations: " + e.getMessage());
            e.printStackTrace();
        }

        
        
        return recommendations;
    }
}
