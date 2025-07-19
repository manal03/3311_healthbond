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
        String sql = "SELECT * FROM nutrientrecommendations WHERE ? BETWEEN age_min AND age_max " +
                     "AND gender = ? AND ? BETWEEN weight_min_kg AND weight_max_kg " +
                     "AND ? BETWEEN height_min_cm AND height_max_cm;";

        try (Connection conn = ConnectionProvider.getCon()) {

            if (conn == null) {
                System.err.println("RecommendNutrients Error: Failed to get database connection.");
                return recommendations; // Return empty map
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int age;
                try {
                    LocalDate dob = LocalDate.parse(user.getDob(), DateTimeFormatter.ISO_LOCAL_DATE);
                    age = Period.between(dob, LocalDate.now()).getYears();
                } catch (DateTimeParseException e) {
                    System.err.println("DAO Error: Could not parse the date of birth: '" + user.getDob() + "'.");
                    System.err.println("Please ensure the date format is exactly YYYY-MM-DD.");
                    return recommendations; // Return empty map
                }

                pstmt.setInt(1, age);
                pstmt.setString(2, user.getSex());
                pstmt.setInt(3, user.getWeight());
                pstmt.setInt(4, user.getHeight());

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
        
        // --- NEW CODE FOR DEBUGGING ---
        // Print the contents of the map to the console before returning it.
        System.out.println("--- Debug: Contents of recommendations map ---");
        if (recommendations.isEmpty()) {
            System.out.println("Map is empty. No recommendations found.");
        } else {
            for (Map.Entry<String, Double> entry : recommendations.entrySet()) {
                System.out.printf("  Key: %-10s | Value: %.2f%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("----------------------------------------------");
        
        // Return the map, which will be populated if a record was found, or empty otherwise.
        return recommendations;
    }
}
