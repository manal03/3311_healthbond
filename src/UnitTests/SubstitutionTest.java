package UnitTests;

import models.SubstitutionRecord;
import services.Substitution;
import utility.ConnectionProvider;

import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubstitutionTest {

    private int userId;
    private int originalFoodId;
    private int substituteFoodId;
    private int mealId;
    private final Substitution substitution = new Substitution();

    // Get next FoodID for insertion
    private int getNextFoodId(Connection con) throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(FoodID) FROM food_name")) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        try (Connection con = ConnectionProvider.getCon()) {
            // Insert test user
            PreparedStatement userStmt = con.prepareStatement(
                    "INSERT INTO users (dateofbirth, weight_kg, height_cm, sex, name, unit) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userStmt.setDate(1, Date.valueOf("1990-01-01"));
            userStmt.setInt(2, 70);
            userStmt.setInt(3, 175);
            userStmt.setString(4, "male");
            userStmt.setString(5, "JUnit Test User");
            userStmt.setString(6, "metric");
            userStmt.executeUpdate();
            try (ResultSet userKeys = userStmt.getGeneratedKeys()) {
                if (userKeys.next()) userId = userKeys.getInt(1);
            }

            // Insert original food
            originalFoodId = getNextFoodId(con);
            PreparedStatement origFoodStmt = con.prepareStatement(
                    "INSERT INTO food_name (FoodID, FoodDescription) VALUES (?, ?)");
            origFoodStmt.setInt(1, originalFoodId);
            origFoodStmt.setString(2, "JUnit Original Food");
            origFoodStmt.executeUpdate();

            // Insert substitute food
            substituteFoodId = getNextFoodId(con);
            PreparedStatement subFoodStmt = con.prepareStatement(
                    "INSERT INTO food_name (FoodID, FoodDescription) VALUES (?, ?)");
            subFoodStmt.setInt(1, substituteFoodId);
            subFoodStmt.setString(2, "JUnit Substitute Food");
            subFoodStmt.executeUpdate();

            // Insert meal 2 days ago
            PreparedStatement mealStmt = con.prepareStatement(
                    "INSERT INTO meals (idusers, mealType, date) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            mealStmt.setInt(1, userId);
            mealStmt.setString(2, "Lunch");
            mealStmt.setDate(3, Date.valueOf(LocalDate.now().minusDays(2)));
            mealStmt.executeUpdate();
            try (ResultSet mealKeys = mealStmt.getGeneratedKeys()) {
                if (mealKeys.next()) mealId = mealKeys.getInt(1);
            }

            // Add original food to meal
            PreparedStatement ingredientStmt = con.prepareStatement(
                    "INSERT INTO ingredients (idmeals, FoodID, quantity) VALUES (?, ?, ?)");
            ingredientStmt.setInt(1, mealId);
            ingredientStmt.setInt(2, originalFoodId);
            ingredientStmt.setInt(3, 100);
            ingredientStmt.executeUpdate();
        }
    }

    @Test
    public void testApplySubstitutionToMeals() throws Exception {
        substitution.applySubstitutionToMeals(
                userId,
                originalFoodId,
                substituteFoodId,
                Date.valueOf(LocalDate.now().minusDays(7)),
                Date.valueOf(LocalDate.now())
        );

        List<SubstitutionRecord> records = substitution.getSubstitutionHistory(userId);
        assertNotNull(records);
        assertFalse(records.isEmpty());

        boolean found = records.stream().anyMatch(r ->
                r.getOriginalFoodId() == originalFoodId &&
                        r.getSubstituteFoodId() == substituteFoodId &&
                        r.getDateApplied() != null);

        assertTrue(found, "Substitution record should be found");
    }

    @Test
    public void testNoSubstitutionRecordsInitially() throws Exception {
        List<SubstitutionRecord> records = substitution.getSubstitutionHistory(userId);
        assertNotNull(records);
        assertTrue(records.isEmpty(), "No records before substitution");
    }

    @Test
    public void testSubstitutionDoesNotAffectOtherUsers() throws Exception {
        int otherUserId;
        try (Connection con = ConnectionProvider.getCon()) {
            PreparedStatement otherUserStmt = con.prepareStatement(
                    "INSERT INTO users (dateofbirth, weight_kg, height_cm, sex, name, unit) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            otherUserStmt.setDate(1, Date.valueOf("1995-05-05"));
            otherUserStmt.setInt(2, 65);
            otherUserStmt.setInt(3, 170);
            otherUserStmt.setString(4, "female");
            otherUserStmt.setString(5, "Other User");
            otherUserStmt.setString(6, "metric");
            otherUserStmt.executeUpdate();
            try (ResultSet keys = otherUserStmt.getGeneratedKeys()) {
                keys.next();
                otherUserId = keys.getInt(1);
            }
        }

        substitution.applySubstitutionToMeals(
                userId,
                originalFoodId,
                substituteFoodId,
                Date.valueOf(LocalDate.now().minusDays(7)),
                Date.valueOf(LocalDate.now())
        );

        List<SubstitutionRecord> otherRecords = substitution.getSubstitutionHistory(otherUserId);
        assertNotNull(otherRecords);
        assertTrue(otherRecords.isEmpty(), "Other user should have no substitutions");

        try (Connection con = ConnectionProvider.getCon()) {
            con.prepareStatement("DELETE FROM users WHERE idusers = " + otherUserId).executeUpdate();
        }
    }

    @Test
    public void testApplySubstitutionWithNoMeals() throws Exception {
        int noMealUserId;
        try (Connection con = ConnectionProvider.getCon()) {
            PreparedStatement noMealUserStmt = con.prepareStatement(
                    "INSERT INTO users (dateofbirth, weight_kg, height_cm, sex, name, unit) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            noMealUserStmt.setDate(1, Date.valueOf("1980-10-10"));
            noMealUserStmt.setInt(2, 80);
            noMealUserStmt.setInt(3, 180);
            noMealUserStmt.setString(4, "male");
            noMealUserStmt.setString(5, "No Meal User");
            noMealUserStmt.setString(6, "metric");
            noMealUserStmt.executeUpdate();
            try (ResultSet keys = noMealUserStmt.getGeneratedKeys()) {
                keys.next();
                noMealUserId = keys.getInt(1);
            }
        }

        // Should not create records since user has no meals
        substitution.applySubstitutionToMeals(
                noMealUserId,
                originalFoodId,
                substituteFoodId,
                Date.valueOf(LocalDate.now().minusDays(7)),
                Date.valueOf(LocalDate.now())
        );

        List<SubstitutionRecord> records = substitution.getSubstitutionHistory(noMealUserId);
        assertNotNull(records);
        assertTrue(records.isEmpty(), "No records created for user with no meals");

        try (Connection con = ConnectionProvider.getCon()) {
            con.prepareStatement("DELETE FROM users WHERE idusers = " + noMealUserId).executeUpdate();
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        try (Connection con = ConnectionProvider.getCon()) {
            con.prepareStatement("DELETE FROM swap_records WHERE user_id = " + userId).executeUpdate();
            con.prepareStatement("DELETE FROM ingredients WHERE idmeals = " + mealId).executeUpdate();
            con.prepareStatement("DELETE FROM meals WHERE idmeals = " + mealId).executeUpdate();
            con.prepareStatement("DELETE FROM food_name WHERE FoodID = " + originalFoodId).executeUpdate();
            con.prepareStatement("DELETE FROM food_name WHERE FoodID = " + substituteFoodId).executeUpdate();
            con.prepareStatement("DELETE FROM users WHERE idusers = " + userId).executeUpdate();
        }
    }
}
