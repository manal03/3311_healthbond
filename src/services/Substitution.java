package services;

import models.NutrientInfo;
import models.SubstitutionRecord;
import utility.ConnectionProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Substitution {

    // Applies a substitution for a food item in meals within a given date range
    public void applySubstitutionToMeals(int userId, int originalFoodId, int substituteFoodId, Date startDate, Date endDate) throws SQLException {
        try (Connection con = ConnectionProvider.getCon()) {
            // Finds all meals that contain the original food in the given range
            String sqlMeals = """
            SELECT m.idmeals, m.date
            FROM meals m
            JOIN ingredients i ON m.idmeals = i.idmeals
            WHERE m.idusers = ?
              AND i.FoodID = ?
              AND m.date BETWEEN ? AND ?
            """;

            List<MealInstance> meals = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sqlMeals)) {
                ps.setInt(1, userId);
                ps.setInt(2, originalFoodId);
                ps.setDate(3, startDate);
                ps.setDate(4, endDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int mealId = rs.getInt("idmeals");
                        Date mealDate = rs.getDate("date");
                        meals.add(new MealInstance(mealId, mealDate));
                    }
                }
            }

            // Records each substitution applied to those meals
            String insertSql = """
            INSERT INTO swap_records
            (user_id, original_food_id, substitute_food_id, start_date, end_date, date_applied)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement psInsert = con.prepareStatement(insertSql)) {
                for (MealInstance meal : meals) {
                    psInsert.setInt(1, userId);
                    psInsert.setInt(2, originalFoodId);
                    psInsert.setInt(3, substituteFoodId);

                    psInsert.setDate(4, meal.mealDate);
                    psInsert.setDate(5, meal.mealDate);
                    psInsert.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }
        }
    }

    // Helper class to hold meal ID and date
    private static class MealInstance {
        int mealId;
        Date mealDate;

        MealInstance(int mealId, Date mealDate) {
            this.mealId = mealId;
            this.mealDate = mealDate;
        }
    }

    // Returns the list of all substitution records for a given user
    public List<SubstitutionRecord> getSubstitutionHistory(int userId) throws SQLException {
        List<SubstitutionRecord> records = new ArrayList<>();

        String sql = "SELECT original_food_id, substitute_food_id, start_date, end_date, date_applied " +
                "FROM swap_records WHERE user_id = ? ORDER BY date_applied DESC";

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int originalFoodId = rs.getInt("original_food_id");
                    int substituteFoodId = rs.getInt("substitute_food_id");

                    Date startDateSql = rs.getDate("start_date");
                    LocalDate startDate = (startDateSql != null) ? startDateSql.toLocalDate() : null;

                    Date endDateSql = rs.getDate("end_date");
                    LocalDate endDate = (endDateSql != null) ? endDateSql.toLocalDate() : null;

                    Date dateAppliedSql = rs.getDate("date_applied");
                    LocalDate dateApplied = (dateAppliedSql != null) ? dateAppliedSql.toLocalDate() : null;

                    SubstitutionRecord record = new SubstitutionRecord(
                            originalFoodId, substituteFoodId, startDate, endDate, dateApplied
                    );

                    records.add(record);
                }
            }
        }

        return records;
    }

    // Retrieves nutrient data for a list of food IDs
    public Map<Integer, Map<String, NutrientInfo>> getNutrientDataForFoods(List<Integer> foodIds) throws SQLException {
        Map<Integer, Map<String, NutrientInfo>> nutrientData = new HashMap<>();

        if (foodIds.isEmpty()) return nutrientData;

        String placeholders = foodIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT na.FoodID, nn.NutrientName, na.NutrientValue, nn.NutrientUnit " +
                "FROM nutrient_amount na " +
                "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
                "WHERE na.FoodID IN (" + placeholders + ") " +
                "AND na.NutrientDateOfEntry = ( " +
                "  SELECT MAX(NutrientDateOfEntry) FROM nutrient_amount " +
                "  WHERE FoodID = na.FoodID AND NutrientID = na.NutrientID " +
                ")";

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < foodIds.size(); i++) {
                stmt.setInt(i + 1, foodIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int foodId = rs.getInt("FoodID");
                    String nutrientName = rs.getString("NutrientName");
                    float nutrientValue = rs.getFloat("NutrientValue");
                    String nutrientUnit = rs.getString("NutrientUnit");

                    nutrientData
                            .computeIfAbsent(foodId, k -> new HashMap<>())
                            .put(nutrientName, new NutrientInfo(nutrientValue, nutrientUnit));
                }
            }
        }
        return nutrientData;
    }

    // Returns names of foods logged by the user in the last 30 days
    public static List<String> getLoggedFoodNames(int userId) throws SQLException {
        List<String> foodNames = new ArrayList<>();
        String sql = "SELECT fn.FoodDescription " +
                "FROM ingredients i " +
                "JOIN meals m ON i.idmeals = m.idmeals " +
                "JOIN food_name fn ON i.FoodID = fn.FoodID " +
                "WHERE m.idusers = ? " +
                "  AND m.date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                "ORDER BY fn.FoodDescription";

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    foodNames.add(rs.getString("FoodDescription"));
                }
            }
        }
        return foodNames;
    }

    // Returns all food names in the database
    public static List<String> getAllFoodNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT FoodDescription FROM food_name ORDER BY FoodDescription";
        try (Connection c = ConnectionProvider.getCon();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet r = s.executeQuery()) {
            while (r.next()) list.add(r.getString(1));
        } catch (SQLException ignored) { }
        return list;
    }

    // Returns all foodID in the database
    public static int getFoodIdByName(String name) throws SQLException {
        String sql = "SELECT FoodID FROM food_name WHERE FoodDescription = ?";
        try (Connection c = ConnectionProvider.getCon();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, name);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) return r.getInt(1);
            }
        }
        throw new SQLException("Food not found: " + name);
    }

    // Returns the food name by ID, or "Unknown" if not found
    public static String getFoodNameById(int id) throws SQLException {
        String sql = "SELECT FoodDescription FROM food_name WHERE FoodID = ?";
        try (Connection c = ConnectionProvider.getCon();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) return r.getString(1);
            }
        }
        return "Unknown";
    }
}


