package models;

public class MealItem {
    private int foodId;
    private double quantity;

    public MealItem(int foodId, double quantity) {
        this.foodId = foodId;
        this.quantity = quantity;
    }

    public int getFoodId() {
        return foodId;
    }

    public double getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Food ID: " + foodId + ", Quantity: " + quantity;
    }
}
