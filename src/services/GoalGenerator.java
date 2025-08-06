package services;

import models.DailyNutrientInterface;
import models.Goal;
import models.RecommendationInterface;
import models.UserProfile;
import utility.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class contains the logic for comparing a user's actual nutrient intake
 * with their recommended goals to generate potential new goals.
 */
public class GoalGenerator {

    public List<Goal> generateGoals(UserProfile user) {
        List<String> existingGoalLabels = getExistingGoalLabels(user);
        Map<String, Double> actualTotals = getActualTotalsForYesterday(user);
        Map<String, Double> recommendedGoals = getRecommendedGoals(user);

        List<Goal> potentialGoals = new ArrayList<>();

        if (recommendedGoals.isEmpty()) {
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Profile", "N/A", "N/A", "Could not generate goals. Please complete your profile.", 0, false));
            return potentialGoals;
        }
        if (actualTotals.isEmpty()) {
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Logging", "N/A", "N/A", "Could not generate goals. Please log your meals for yesterday.", 0, false));
            return potentialGoals;
        }

        // Protein
        double actualProtein = actualTotals.getOrDefault("PROTEIN", 0.0);
        double recommendedProtein = recommendedGoals.getOrDefault("Protein", 0.0);
        if (actualProtein < recommendedProtein * 0.8) {
            double difference = recommendedProtein - actualProtein;
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Protein", "Moderate", "Increase",
                    String.format("Increase daily protein by ~%.0f g", difference), difference, false));
        }

        // Fat
        double actualFat = actualTotals.getOrDefault("FAT (TOTAL LIPIDS)", 0.0);
        double recommendedFat = recommendedGoals.getOrDefault("Fat", 0.0);
        if (actualFat > recommendedFat * 1.2) {
            double difference = actualFat - recommendedFat;
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Fat", "Moderate", "Decrease",
                    String.format("Reduce daily fat by ~%.0f g", difference), difference, false));
        }

        // Carbs
        double actualCarbs = actualTotals.getOrDefault("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 0.0);
        double recommendedCarbs = recommendedGoals.getOrDefault("Carbs", 0.0);
        if (actualCarbs < recommendedCarbs * 0.8) {
            double difference = recommendedCarbs - actualCarbs;
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Carbohydrates", "Moderate", "Increase",
                    String.format("Increase daily carbs by ~%.0f g", difference), difference, false));
        }

        // Calories
        double actualCalories = actualTotals.getOrDefault("ENERGY (KILOCALORIES)", 0.0);
        double recommendedCalories = recommendedGoals.getOrDefault("Calories", 0.0);
        if (actualCalories < recommendedCalories - 100) {
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Calories", "Moderate", "Increase",
                    "Increase overall calorie intake.", 100, false));
        } else if (actualCalories > recommendedCalories + 100) {
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Calories", "Moderate", "Decrease",
                    "Reduce overall calorie intake.", 100, false));
        }


        double actualFiber = actualTotals.getOrDefault("FIBRE, TOTAL DIETARY", 0.0);
        double recommendedFiber = recommendedGoals.getOrDefault("Fiber", 0.0);
        if (actualFiber < recommendedFiber * 0.8) {
            double difference = recommendedFiber - actualFiber;
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "Fiber", "Moderate", "Increase",
                    String.format("Increase daily fiber intake by ~%.0f g", difference), difference, false));
        }

        if (potentialGoals.isEmpty()) {
            potentialGoals.add(new Goal(UUID.randomUUID().toString(), "General", "N/A", "N/A", "You are meeting your primary nutrient goals. Well done!", 0, false));
        }

        return potentialGoals.stream()
                .filter(goal -> !existingGoalLabels.contains(goal.getLabel()))
                .collect(Collectors.toList());
    }

    public List<String> getExistingGoalLabels(UserProfile user) {
        List<String> existingGoals = new ArrayList<>();
        String sql = "SELECT Label FROM user_goals WHERE idusers = ?";
        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                existingGoals.add(rs.getString("Label"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existingGoals;
    }

    protected Map<String, Double> getActualTotalsForYesterday(UserProfile user) {
        DailyNutrientInterface nutrientTracker = new DailyNutrientTotals();
        String yesterdayDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return nutrientTracker.getDailyTotalsForUser(user, yesterdayDate);
    }


    protected Map<String, Double> getRecommendedGoals(UserProfile user) {
        RecommendationInterface recommendationFinder = new RecommendNutrients();
        return recommendationFinder.findForUser(user);
    }
}