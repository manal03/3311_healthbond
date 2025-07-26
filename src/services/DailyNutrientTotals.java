package services;

import models.DailyNutrientInterface;
import models.UserProfile;
import utility.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Double> dailyTotals = new HashMap<>();

        String sql = """
            SELECT 
                nn.NutrientCode,
                SUM((na.NutrientValue * i.quantity * (1 - IFNULL(ra.RefuseAmount, 0) / 100)) / 100) AS DailyTotal
            FROM meals m
            JOIN ingredients i ON m.idmeals = i.idmeals
            JOIN nutrient_amount na ON i.FoodID = na.FoodID
            JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID
            LEFT JOIN refuse_amount ra ON i.FoodID = ra.FoodID
            WHERE m.idusers = ? AND m.date = ?
              AND nn.NutrientCode IN (203, 204, 205, 208, 291)
            GROUP BY nn.NutrientCode
        """;

        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, dateString);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int nutrientCode = rs.getInt("NutrientCode");
                    double totalAmount = rs.getDouble("DailyTotal");

                    String nutrientName = switch (nutrientCode) {
                        case 203 -> "PROTEIN";
                        case 204 -> "FAT (TOTAL LIPIDS)";
                        case 205 -> "CARBOHYDRATE, TOTAL (BY DIFFERENCE)";
                        case 208 -> "ENERGY (KILOCALORIES)";
                        case 291 -> "FIBRE, TOTAL DIETARY";
                        default -> "UNKNOWN";
                    };

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
