package UnitTests;

import models.NutrientSummary;
import models.UserProfile;
import services.NutritionAnalysis;
import utility.ConnectionProvider;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NutritionAnalysisTest {

    private int userId;
    private int foodId;
    private int nutrientId;
    private int mealId;
    private NutritionAnalysis analysis;

    // Get next food ID from DB
    private int getNextFoodId(Connection con) throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(FoodID) FROM food_name")) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    // Get next nutrient ID from DB
    private int getNextNutrientId(Connection con) throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(NutrientID) FROM nutrient_name")) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        try (Connection con = ConnectionProvider.getCon()) {
            // Insert test user and get ID
            PreparedStatement userStmt = con.prepareStatement(
                    "INSERT INTO users (dateofbirth, weight_kg, height_cm, sex, name, unit) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userStmt.setDate(1, Date.valueOf("1990-01-01"));
            userStmt.setInt(2, 70);
            userStmt.setInt(3, 175);
            userStmt.setString(4, "male");
            userStmt.setString(5, "JUnit Nutrition User");
            userStmt.setString(6, "metric");
            userStmt.executeUpdate();
            try (ResultSet rs = userStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                userId = rs.getInt(1);
            }

            // Create user profile for analysis
            UserProfile user = new UserProfile(userId, "1990-01-01", 70, 175, "male", "JUnit Nutrition User", "metric");
            analysis = new NutritionAnalysis(user);

            // Insert test food
            foodId = getNextFoodId(con);
            PreparedStatement foodStmt = con.prepareStatement("INSERT INTO food_name (FoodID, FoodDescription) VALUES (?, ?)");
            foodStmt.setInt(1, foodId);
            foodStmt.setString(2, "Test Food");
            foodStmt.executeUpdate();

            // Insert nutrient (Protein)
            nutrientId = getNextNutrientId(con);
            PreparedStatement nutrientStmt = con.prepareStatement(
                    "INSERT INTO nutrient_name (NutrientID, NutrientName, NutrientUnit) VALUES (?, ?, ?)");
            nutrientStmt.setInt(1, nutrientId);
            nutrientStmt.setString(2, "Protein");
            nutrientStmt.setString(3, "g");
            nutrientStmt.executeUpdate();

            // Insert nutrient amount (25g protein per 100g food)
            PreparedStatement amountStmt = con.prepareStatement(
                    "INSERT INTO nutrient_amount (FoodID, NutrientID, NutrientValue) VALUES (?, ?, ?)");
            amountStmt.setInt(1, foodId);
            amountStmt.setInt(2, nutrientId);
            amountStmt.setFloat(3, 25f);
            amountStmt.executeUpdate();

            // Insert meal for user
            PreparedStatement mealStmt = con.prepareStatement(
                    "INSERT INTO meals (idusers, mealType, date) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            mealStmt.setInt(1, userId);
            mealStmt.setString(2, "Lunch");
            mealStmt.setDate(3, Date.valueOf(LocalDate.now()));
            mealStmt.executeUpdate();
            try (ResultSet rs = mealStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                mealId = rs.getInt(1);
            }

            // Insert ingredient (100g of test food)
            PreparedStatement ingredientStmt = con.prepareStatement(
                    "INSERT INTO ingredients (idmeals, FoodID, quantity) VALUES (?, ?, ?)");
            ingredientStmt.setInt(1, mealId);
            ingredientStmt.setInt(2, foodId);
            ingredientStmt.setInt(3, 100);
            ingredientStmt.executeUpdate();
        }
    }

    @Test
    public void testAnalyzeNutrients_ReturnsProteinSummary() throws Exception {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();

        List<NutrientSummary> result = analysis.analyzeNutrients(start, end);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        NutrientSummary protein = result.stream()
                .filter(n -> "Protein".equalsIgnoreCase(n.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(protein);
        assertEquals("g", protein.getUnit());
        assertEquals(12.5, protein.getDailyAverage(), 0.1);
        assertTrue(protein.getPercentageOfRecommended() >= 0);
    }

    @Test
    public void testGetDailyBreakdown_IncludesProteinForToday() throws Exception {
        LocalDate day = LocalDate.now();
        Map<LocalDate, List<NutrientSummary>> breakdown = analysis.getDailyBreakdown(day.minusDays(1), day);

        assertNotNull(breakdown);
        assertTrue(breakdown.containsKey(day));

        List<NutrientSummary> daySummaries = breakdown.get(day);
        assertFalse(daySummaries.isEmpty());

        NutrientSummary protein = daySummaries.stream()
                .filter(n -> "Protein".equalsIgnoreCase(n.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(protein);
        assertEquals(25, protein.getDailyAverage(), 0.1);
        assertEquals("g", protein.getUnit());
    }

    @Test
    public void testAnalyzeNutrients_MultipleNutrients() throws Exception {
        try (Connection con = ConnectionProvider.getCon()) {
            // Add Carb nutrient
            int carbNutrientId = getNextNutrientId(con);
            PreparedStatement carbStmt = con.prepareStatement(
                    "INSERT INTO nutrient_name (NutrientID, NutrientName, NutrientUnit) VALUES (?, ?, ?)");
            carbStmt.setInt(1, carbNutrientId);
            carbStmt.setString(2, "Carbohydrate");
            carbStmt.setString(3, "g");
            carbStmt.executeUpdate();

            // Add carb amount (50g per 100g)
            PreparedStatement carbAmountStmt = con.prepareStatement(
                    "INSERT INTO nutrient_amount (FoodID, NutrientID, NutrientValue) VALUES (?, ?, ?)");
            carbAmountStmt.setInt(1, foodId);
            carbAmountStmt.setInt(2, carbNutrientId);
            carbAmountStmt.setFloat(3, 50f);
            carbAmountStmt.executeUpdate();

            // Update ingredient quantity to 100g
            PreparedStatement updateIngredientStmt = con.prepareStatement(
                    "UPDATE ingredients SET quantity = ? WHERE idmeals = ? AND FoodID = ?");
            updateIngredientStmt.setInt(1, 100);
            updateIngredientStmt.setInt(2, mealId);
            updateIngredientStmt.setInt(3, foodId);
            updateIngredientStmt.executeUpdate();

            // Run nutrient analysis
            List<NutrientSummary> summaries = analysis.analyzeNutrients(LocalDate.now(), LocalDate.now());

            NutrientSummary carbs = summaries.stream()
                    .filter(n -> "Carbohydrate".equalsIgnoreCase(n.getName()))
                    .findFirst().orElse(null);

            assertNotNull(carbs);
            assertEquals(50, carbs.getDailyAverage(), 0.1);

            // Clean up carb data
            con.createStatement().executeUpdate("DELETE FROM nutrient_amount WHERE NutrientID = " + carbNutrientId);
            con.createStatement().executeUpdate("DELETE FROM nutrient_name WHERE NutrientID = " + carbNutrientId);
        }
    }

    @Test
    public void testAnalyzeNutrients_MultipleIngredients() throws Exception {
        try (Connection con = ConnectionProvider.getCon()) {
            // Insert second food
            int secondFoodId = getNextFoodId(con);
            PreparedStatement foodStmt = con.prepareStatement(
                    "INSERT INTO food_name (FoodID, FoodDescription) VALUES (?, ?)");
            foodStmt.setInt(1, secondFoodId);
            foodStmt.setString(2, "Second Test Food");
            foodStmt.executeUpdate();

            // Add protein amount for second food (30g per 100g)
            PreparedStatement proteinAmountStmt = con.prepareStatement(
                    "INSERT INTO nutrient_amount (FoodID, NutrientID, NutrientValue) VALUES (?, ?, ?)");
            proteinAmountStmt.setInt(1, secondFoodId);
            proteinAmountStmt.setInt(2, nutrientId);
            proteinAmountStmt.setFloat(3, 30f);
            proteinAmountStmt.executeUpdate();

            // Add second ingredient to meal (50g)
            PreparedStatement ingredientStmt = con.prepareStatement(
                    "INSERT INTO ingredients (idmeals, FoodID, quantity) VALUES (?, ?, ?)");
            ingredientStmt.setInt(1, mealId);
            ingredientStmt.setInt(2, secondFoodId);
            ingredientStmt.setInt(3, 50);
            ingredientStmt.executeUpdate();

            // Analyze nutrients for the day
            List<NutrientSummary> summaries = analysis.analyzeNutrients(LocalDate.now(), LocalDate.now());
            NutrientSummary protein = summaries.stream()
                    .filter(n -> "Protein".equalsIgnoreCase(n.getName()))
                    .findFirst().orElse(null);

            assertNotNull(protein);
            // Protein total: 25g + 15g = 40g
            assertEquals(40, protein.getDailyAverage(), 0.1);

            // Clean up second food and related data
            con.createStatement().executeUpdate("DELETE FROM ingredients WHERE FoodID = " + secondFoodId);
            con.createStatement().executeUpdate("DELETE FROM nutrient_amount WHERE FoodID = " + secondFoodId);
            con.createStatement().executeUpdate("DELETE FROM food_name WHERE FoodID = " + secondFoodId);
        }
    }

    @Test
    public void testAnalyzeNutrients_NoMealsInDateRange() throws Exception {
        LocalDate start = LocalDate.now().minusYears(1);
        LocalDate end = LocalDate.now().minusYears(1).plusDays(1);

        List<NutrientSummary> summaries = analysis.analyzeNutrients(start, end);
        assertNotNull(summaries);
        assertTrue(summaries.isEmpty(), "Should be empty if no meals found");
    }

    @Test
    public void testNutrientSummary_StatusColor() {
        NutrientSummary low = new NutrientSummary("Protein", 10, 100, "g", 10);
        NutrientSummary medium = new NutrientSummary("Protein", 80, 100, "g", 80);
        NutrientSummary high = new NutrientSummary("Protein", 120, 100, "g", 120);

        assertEquals(new java.awt.Color(220, 53, 69), low.getStatusColor());    // red
        assertEquals(new java.awt.Color(255, 193, 7), medium.getStatusColor()); // amber
        assertEquals(new java.awt.Color(50, 168, 82), high.getStatusColor());   // green
    }

    @AfterEach
    public void tearDown() throws Exception {
        try (Connection con = ConnectionProvider.getCon()) {
            con.createStatement().executeUpdate("DELETE FROM ingredients WHERE idmeals = " + mealId);
            con.createStatement().executeUpdate("DELETE FROM meals WHERE idmeals = " + mealId);
            con.createStatement().executeUpdate("DELETE FROM nutrient_amount WHERE FoodID = " + foodId);
            con.createStatement().executeUpdate("DELETE FROM nutrient_name WHERE NutrientID = " + nutrientId);
            con.createStatement().executeUpdate("DELETE FROM food_name WHERE FoodID = " + foodId);
            con.createStatement().executeUpdate("DELETE FROM users WHERE idusers = " + userId);
        }
    }
}
