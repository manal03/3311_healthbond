package models;

import java.util.Map;

public interface DailyNutrientInterface {
    Map<String, Double> getDailyTotalsForUser(UserProfile user, String dateString);
}
