
import java.util.ArrayList;
import java.util.HashMap;

public class GoalGenerator {
    protected String Nutrient;
    protected double amount;
    private UserProfile user;


    public HashMap<String, Double> getRecommendedNutrients(UserProfile user){
    	
        return null; // Go through each meal nutrient data and compare the amount to the reccomended intake
        // Map the result in a hash map nutrient: difference between entered meal and recommneded actual - suggested

        // If it is negative, then suggest increasing intake for that nutrient

        // If it is positive, then suggest decreasing intake for that nutrient

        // If it is 0, then dont make any suggestion for that nutrient
    }

    public ArrayList<Goal> generateGoal(){
        return null;
        // based on the hashmap above, go through each nutrient and generate a list of potential goal objects and add it to the array and return
    }


}