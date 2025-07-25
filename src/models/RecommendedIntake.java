package models;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class RecommendedIntake {
    private final UserProfile user;

    public RecommendedIntake(UserProfile user) {
        this.user = user;
    }

    // Calculates age from a date string.
    // Tries ISO format first, then "dd/MM/yyyy".
    // Returns 30 if both formats fail.
    public double calculateAge(String dobString) {
        try {
            return calculateAgeFromFormat(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException e) {
            try {
                return calculateAgeFromFormat(dobString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeException ex) {
                System.err.println("Unparseable date: " + dobString);
                return 30; // Default age
            }
        }
    }

    // Parses the date and calculates age in years.
    private double calculateAgeFromFormat(String dobString, DateTimeFormatter formatter) {
        LocalDate dob = LocalDate.parse(dobString, formatter);
        return (double) ChronoUnit.YEARS.between(dob, LocalDate.now());
    }

    // Returns a map of recommended daily intake values.
    public Map<String, Float> getRecommendedMap() {
        Map<String, Float> recommendedMap = new HashMap<>();

        recommendedMap.put("ENERGY (KILOCALORIES)", 2000f);
        recommendedMap.put("PROTEIN", 50f);
        recommendedMap.put("FAT (TOTAL LIPIDS)", 65f);
        recommendedMap.put("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 310f);
        recommendedMap.put("FIBRE, TOTAL DIETARY", 30f);

        return recommendedMap;
    }
}