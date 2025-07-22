package services;

import models.NutrientInfo;
import models.NutrientSummary;
import models.RecommendedIntake;
import models.UserProfile;
import utility.ConnectionProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class NutritionAnalysis {
    private final UserProfile user;
    private final RecommendedIntake recommendedIntake;

    public NutritionAnalysis(UserProfile user) {
        this.user = user;
        this.recommendedIntake = new RecommendedIntake(user);
    }

    public List<NutrientSummary> analyzeNutrients(LocalDate start, LocalDate end) throws SQLException {
        List<NutrientInfo> averages = getDailyAverages(start, end);
        Map<String, Float> recommendedMap = recommendedIntake.getRecommendedMap();

        double total = averages.stream().mapToDouble(NutrientInfo::getValue).sum();
        List<NutrientSummary> result = new ArrayList<>();

        for (NutrientInfo n : averages) {
            float recommended = recommendedMap.getOrDefault(n.getName(), 0f);
            double percentageOfTotal = total > 0 ? (n.getValue() / total) * 100 : 0;
            result.add(new NutrientSummary(
                    n.getName(),
                    n.getValue(),
                    recommended,
                    n.getUnit(),
                    percentageOfTotal
            ));
        }

        result.sort(Comparator.comparingDouble(NutrientSummary::getPercentageOfTotal).reversed());
        return result;
    }

    private List<NutrientInfo> getDailyAverages(LocalDate start, LocalDate end) throws SQLException {
        List<NutrientInfo> averages = new ArrayList<>();
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;

        try (Connection con = ConnectionProvider.getCon()) {
            String sql = """
                SELECT nn.NutrientName, nn.NutrientUnit, 
                       SUM(na.NutrientValue * i.quantity / 100) / ? AS daily_avg 
                FROM meals m 
                JOIN ingredients i ON m.idmeals = i.idmeals 
                JOIN nutrient_amount na ON i.FoodID = na.FoodID 
                JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID 
                WHERE m.idusers = ? AND m.date BETWEEN ? AND ? 
                GROUP BY nn.NutrientName, nn.NutrientUnit
            """;

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setLong(1, days);
                stmt.setInt(2, user.getUserId());
                stmt.setDate(3, java.sql.Date.valueOf(start));
                stmt.setDate(4, java.sql.Date.valueOf(end));

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("NutrientName");
                        String unit = rs.getString("NutrientUnit");
                        float avg = rs.getFloat("daily_avg");

                        averages.add(new NutrientInfo(name, avg, unit));
                    }
                }
            }
        }
        return averages;
    }
}

