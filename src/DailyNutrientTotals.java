import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Assume UserProfile, DailyNutrientInterface, and ConnectionProvider classes exist.

/**
 * This class implements the DailyNutrientInterface. It is responsible for
 * querying the database to calculate the total nutrient breakdown for all meals
 * a user has logged on a specific day.
 */
public class DailyNutrientTotals implements DailyNutrientInterface {

    /**
     * Calculates the total nutrient breakdown for all meals a user logged on a specific date.
     *
     * @param user The UserProfile object for whom to calculate the totals.
     * @param dateString The specific date to analyze, in "YYYY-MM-DD" format.
     * @return A Map where the key is the Nutrient Name (e.g., "PROTEIN") and the
     * value is the total calculated amount for that day. Returns an empty map if no meals are found or an error occurs.
     */
    @Override
    public Map<String, Double> getDailyTotalsForUser(UserProfile user, String dateString) {
        // The HashMap to store the final results.
        Map<String, Double> dailyTotals = new HashMap<>();

        // This is the final, accurate SQL query that sums all nutrients for the day.
        String sql =
            "SELECT " +
            "    nn.NutrientName, " +
            "    SUM(ROUND((na.NutrientValue * (i.quantity / 100.0)), 2)) AS DailyTotal " +
            "FROM " +
            "    meals m " +
            "JOIN " +
            "    ingredients i ON m.idmeals = i.idmeals " +
            "JOIN " +
            "    nutrient_amount na ON i.FoodID = na.FoodID " +
            "JOIN " +
            "    nutrient_name nn ON na.NutrientID = nn.NutrientID " +
            "WHERE " +
            "    m.idusers = ? AND m.date = ? " +
            "    AND nn.NutrientName IN ( " +
            "        'PROTEIN', " +
            "        'FAT (TOTAL LIPIDS)', " +
            "        'CARBOHYDRATE, TOTAL (BY DIFFERENCE)', " +
            "        'ENERGY (KILOCALORIES)', " +
            "        'FIBRE, TOTAL DIETARY' " +
            "    ) " +
            "GROUP BY " +
            "    nn.NutrientName " +
            "ORDER BY " +
            "    nn.NutrientName;";

        // Use your ConnectionProvider to get the database connection.
        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bind the parameters to the query
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, dateString);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Loop through the results and populate the HashMap
                while (rs.next()) {
                    String nutrientName = rs.getString("NutrientName");
                    double totalAmount = rs.getDouble("DailyTotal");
                    // Store the nutrient name and its calculated total in the map
                    dailyTotals.put(nutrientName, totalAmount);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while fetching daily totals: " + e.getMessage());
            e.printStackTrace();
        }

        return dailyTotals;
    }
}
