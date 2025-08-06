package UnitTests;

import models.Goal;
import models.UserProfile;
import org.junit.jupiter.api.Test;
import services.GoalGenerator;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GoalGeneratorTest {

    // Helper subclass to override nutrient data retrieval methods
    static class TestGoalGenerator extends GoalGenerator {
        private final Map<String, Double> actualTotalsMock;
        private final Map<String, Double> recommendedGoalsMock;

        public TestGoalGenerator(Map<String, Double> actualTotalsMock, Map<String, Double> recommendedGoalsMock) {
            this.actualTotalsMock = actualTotalsMock;
            this.recommendedGoalsMock = recommendedGoalsMock;
        }

        @Override
        protected Map<String, Double> getActualTotalsForYesterday(UserProfile user) {
            return actualTotalsMock;
        }

        @Override
        protected Map<String, Double> getRecommendedGoals(UserProfile user) {
            return recommendedGoalsMock;
        }
    }

    @Test
    public void testNoMeals() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        Map<String, Double> emptyTotals = Map.of();
        Map<String, Double> recommendedGoals = Map.of(
            "Calories", 2500.0,
            "Protein", 50.0,
            "Fat", 70.0,
            "Carbs", 300.0,
            "Fiber", 30.0
        );

        GoalGenerator generator = new TestGoalGenerator(emptyTotals, recommendedGoals);
        List<Goal> goals = generator.generateGoals(user);

        assertNotNull(goals);
        assertEquals(1, goals.size());
        assertEquals("Logging", goals.get(0).getGoalType());
    }

    @Test
    public void testSimulatedMeals() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        Map<String, Double> actualTotals = Map.of(
            "PROTEIN", 30.0,
            "FAT (TOTAL LIPIDS)", 90.0,
            "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 230.0,
            "ENERGY (KILOCALORIES)", 2400.0,
            "FIBRE, TOTAL DIETARY", 15.0
        );

        Map<String, Double> recommendedGoals = Map.of(
            "Calories", 2500.0,
            "Protein", 50.0,
            "Fat", 70.0,
            "Carbs", 300.0,
            "Fiber", 30.0
        );

        GoalGenerator generator = new TestGoalGenerator(actualTotals, recommendedGoals);
        List<Goal> goals = generator.generateGoals(user);

        assertNotNull(goals);
        assertFalse(goals.isEmpty());

        assertTrue(goals.stream().anyMatch(g -> g.getGoalType().equals("Protein") && g.getDirection().equals("Increase")));
        assertTrue(goals.stream().anyMatch(g -> g.getGoalType().equals("Fat") && g.getDirection().equals("Decrease")));
        assertTrue(goals.stream().anyMatch(g -> g.getGoalType().equals("Carbohydrates") && g.getDirection().equals("Increase")));
        assertTrue(goals.stream().anyMatch(g -> g.getGoalType().equals("Fiber") && g.getDirection().equals("Increase")));
    }

}
