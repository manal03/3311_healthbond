import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * This class contains the logic for testing the daily nutrient total calculation.
 * It does not have any UI components.
 */
public class GoalGenerator {

    /**
     * This method runs the test. It fetches the daily nutrient totals for the
     * given user and prints the resulting HashMap to the console.
     *
     * @param user The UserProfile object to test with.
     */
    public Map<String,Double> getDailyTotal(UserProfile user) {
        // 1. Instantiate the class that implements the interface.
        DailyNutrientInterface nutrientTracker = new DailyNutrientTotals();

        // 2. Get the current date and format it as a "YYYY-MM-DD" string.
        String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

     

        // 3. Call the interface method to get the daily totals.
        Map<String, Double> dailyTotals = nutrientTracker.getDailyTotalsForUser(user, todayDate);
        return dailyTotals;
       
    }
    
    public Map<String,Double> getrecommendedTotal(UserProfile user){
    	RecommendationInterface recNut = new RecommendNutrients();
    	Map<String,Double> recommended = recNut.findForUser(user);
    	return recommended;
    	
    }
    
    
}
