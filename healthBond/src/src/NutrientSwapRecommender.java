import java.sql.*;
import java.util.*;

public class NutrientSwapRecommender {
    // Enum for nutrient mappings
    public enum NutrientType {
        CALORIES("Calories", 208),
        FIBER("Fiber", 291),
        PROTEIN("Protein", 203),
        FAT("Fat", 204),
        CHOLESTEROL("Cholesterol",601),
        VITAMIN_B12("VITAMIN B-12 , ADDED", 578),
        CARBS("Carbohydrate", 205),
        ;

        private final String label;
        private final int nutrientId;

        NutrientType(String label, int nutrientId) {
            this.label = label;
            this.nutrientId = nutrientId;
        }

        public int getNutrientId() {
            return nutrientId;
        }

        public static Optional<NutrientType> fromLabel(String label) {
            return Arrays.stream(values())
                    .filter(n -> n.label.equalsIgnoreCase(label))
                    .findFirst();
        }
    }

    // Data holder for suggestions
    public static class Suggestion {
        private final String original;
        private final String replacement;
        private final double changeAmount;

        public Suggestion(String original, String replacement, double changeAmount) {
            this.original = original;
            this.replacement = replacement;
            this.changeAmount = changeAmount;
        }

        public String getOriginal() { return original; }
        public String getReplacement() { return replacement; }
        public double getChangeAmount() { return changeAmount; }
    }

    public List<Suggestion> recommendSwaps(List<SwapGoal> goals, List<MealItem> currentMeals) {
        List<Suggestion> suggestions = new ArrayList<>();

        try (Connection conn = ConnectionProvider.getCon()) {
            for (SwapGoal goal : goals) {
                Optional<NutrientType> nutrientOpt = NutrientType.fromLabel(goal.getNutrient());
                if (nutrientOpt.isEmpty()) continue;

                int nutrientId = nutrientOpt.get().getNutrientId();
                boolean increase = "INCREASE".equalsIgnoreCase(goal.getDirection());

                for (MealItem item : currentMeals) {
                    double existingVal = getNutrientValue(conn, item.getFoodId(), nutrientId);
                    if (existingVal == -1) continue;

                    int bestFoodId = findBetterFood(conn, nutrientId, increase);
                    if (bestFoodId == item.getFoodId()) continue;

                    double bestVal = getNutrientValue(conn, bestFoodId, nutrientId);
                    double currentActual = existingVal * (item.getQuantity() / 100.0);
                    double betterActual = bestVal * (item.getQuantity() / 100.0);
                    double delta = Math.abs(betterActual - currentActual);

                    if (delta < 0.01) continue;

                    String oldName = getFoodName(conn, item.getFoodId());
                    String newName = getFoodName(conn, bestFoodId);

                    suggestions.add(new Suggestion(
                            oldName + " (" + (int)item.getQuantity() + "g)",
                            newName + " (" + (int)item.getQuantity() + "g)",
                            increase ? (betterActual - currentActual) : (currentActual - betterActual)
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in NutrientSwapAdvisor: " + e.getMessage());
        }

        return suggestions;
    }

    private double getNutrientValue(Connection conn, int foodId, int nutrientId) throws SQLException {
        String sql = "SELECT nutrientvalue FROM nutrient_amount WHERE foodid = ? AND nutrientid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            ps.setInt(2, nutrientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("nutrientvalue");
            }
        }
        return -1;
    }

    private int findBetterFood(Connection conn, int nutrientId, boolean increase) throws SQLException {
        String order = increase ? "DESC" : "ASC";
        String sql = "SELECT foodid FROM nutrient_amount WHERE nutrientid = ? ORDER BY nutrientvalue " + order + " LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nutrientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("foodid");
            }
        }
        return -1;
    }

    private String getFoodName(Connection conn, int foodId) throws SQLException {
        String sql = "SELECT fooddescription FROM food_name WHERE foodid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("fooddescription");
            }
        }
        return "Food #" + foodId;
    }
}
