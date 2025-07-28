package UnitTests;

import models.UserProfile;
import UI.template.AbstractFoodSwapVisualizer;
import UI.template.BarChartFoodSwapVisualizer;
import UI.template.LineChartFoodSwapVisualizer;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class FoodSwapVisualizerTest {

    @Test
    public void testBarChartVisualizerCreation() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        AbstractFoodSwapVisualizer visualizer = new BarChartFoodSwapVisualizer(
                user, "Calories", "Last 7 days", false
        );

        assertNotNull(visualizer);

        JPanel panel = visualizer.generateVisualization();

        assertNotNull(panel);
        assertEquals(BorderLayout.class, panel.getLayout().getClass());
        assertEquals(Color.WHITE, panel.getBackground());
    }

    @Test
    public void testLineChartVisualizerCreation() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        AbstractFoodSwapVisualizer visualizer = new LineChartFoodSwapVisualizer(
                user, "Protein", "Last 30 days", true
        );

        assertNotNull(visualizer);

        JPanel panel = visualizer.generateVisualization();

        assertNotNull(panel);
        assertEquals(BorderLayout.class, panel.getLayout().getClass());
    }

    @Test
    public void testDifferentTimePeriods() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        String[] timePeriods = {"Last 7 days", "Last 14 days", "Last 30 days", "Last 3 months"};

        for (String period : timePeriods) {
            AbstractFoodSwapVisualizer visualizer = new BarChartFoodSwapVisualizer(
                    user, "Fat", period, false
            );

            JPanel panel = visualizer.generateVisualization();
            assertNotNull(panel);
        }
    }

    @Test
    public void testDifferentNutrients() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        String[] nutrients = {"Calories", "Protein", "Carbohydrates", "Fat",
                "Fiber", "Sugar", "Sodium", "Cholesterol"};

        for (String nutrient : nutrients) {
            AbstractFoodSwapVisualizer visualizer = new LineChartFoodSwapVisualizer(
                    user, nutrient, "Last 7 days", false
            );

            JPanel panel = visualizer.generateVisualization();
            assertNotNull(panel);
        }
    }

    @Test
    public void testCFGAdherenceOption() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        AbstractFoodSwapVisualizer visualizerNoCFG = new BarChartFoodSwapVisualizer(
                user, "Calories", "Last 7 days", false
        );
        JPanel panelNoCFG = visualizerNoCFG.generateVisualization();
        assertNotNull(panelNoCFG);

        AbstractFoodSwapVisualizer visualizerWithCFG = new BarChartFoodSwapVisualizer(
                user, "Calories", "Last 7 days", true
        );
        JPanel panelWithCFG = visualizerWithCFG.generateVisualization();
        assertNotNull(panelWithCFG);
    }
}