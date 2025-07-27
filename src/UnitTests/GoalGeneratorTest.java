package UnitTests;

import models.Goal;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GoalGeneratorTest {

    @Test
    public void testGoalObjectCreation() {
        String id = UUID.randomUUID().toString();
        String type = "Protein";
        String intensity = "Moderate";
        String direction = "Increase";
        String label = "Increase daily protein intake by ~20g";
        double amount = 20.0;
        boolean isPercentage = false;

        Goal goal = new Goal(id, type, intensity, direction, label, amount, isPercentage);

        assertEquals(id, goal.getGoalID());
        assertEquals(type, goal.getGoalType());
        assertEquals(intensity, goal.getIntensity());
        assertEquals(direction, goal.getDirection());
        assertEquals(label, goal.getLabel());
        assertEquals(amount, goal.getAmount());
        assertFalse(goal.isPercentage());

        // Optional: test toString returns the label
        assertEquals(label, goal.toString());
    }

    @Test
    public void testGoalDecreaseCalories() {
        Goal calorieGoal = new Goal(
                UUID.randomUUID().toString(),
                "Calories",
                "Moderate",
                "Decrease",
                "Reduce daily calorie intake by ~100 kcal",
                100.0,
                false
        );

        assertEquals("Decrease", calorieGoal.getDirection());
        assertEquals("Reduce daily calorie intake by ~100 kcal", calorieGoal.getLabel());
        assertEquals(100.0, calorieGoal.getAmount());
    }
}
