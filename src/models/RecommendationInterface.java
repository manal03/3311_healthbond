package models;

import java.util.Map;

public interface RecommendationInterface {
    Map<String, Double> findForUser(UserProfile user);

}