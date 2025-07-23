

public class Goal {
    protected String GoalID;
    protected String GoalType;    // e.g., "Protein", "Fat"
    protected String Intensity;   // e.g., "Moderate"
    protected String Direction;   // "Increase" or "Decrease"
    protected String Label;       // A descriptive label for the goal
    protected double Amount;      // The target amount
    protected boolean isPercentage;

    public Goal(String ID, String type, String intensity, String direction, String label, double amount, boolean isPercentage) {
        this.GoalID = ID;
        this.GoalType = type;
        this.Intensity = intensity;
        this.Direction = direction;
        this.Label = label;
        this.Amount = amount;
        this.isPercentage = isPercentage;
    }

    // Getters for all fields
    public String getGoalID() {
        return GoalID;
    }
    public String getGoalType() {
        return GoalType;
    }
    public String getIntensity() {
        return Intensity;
    }
    public String getDirection() {
        return Direction;
    }
    public String getLabel() {
        return Label;
    }
    public double getAmount() {
        return Amount;
    }
    public boolean isPercentage() {
        return isPercentage;
    }

    // A descriptive toString() method for display in the JList.
    @Override
    public String toString() {
        return Label;
    }
}