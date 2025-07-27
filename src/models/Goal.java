package models;

public class Goal {
    protected String GoalID;	//ID of goal generated to be used as primary key in db
    protected String GoalType;
    protected String Intensity;
    protected String Direction; // Increase or Decrease
    protected String Label; // Arbitrary amount to change nutrients i.e by alot
    protected double Amount; //
    protected boolean isPercentage;

    public Goal(String ID,String type, String Intensity, String Direction, String Label, double Amount, boolean isPercentage ) {
        this.GoalType = type;
        this.GoalID = ID;
        this.Intensity = Intensity;
        this.Direction = Direction;
        this.Label = Label;
        this.Amount = Amount;
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

    @Override
    public String toString() {
        return Label;
    }
}
