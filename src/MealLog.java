import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MealLog extends JFrame {
    JButton submit;
    JComboBox<String> mealType;
    JTextField dateField, ingredientField, quantityField;
    private UserProfile user;

    public MealLog(UserProfile user) {
        this.user = user;
        this.setLayout(null);
        this.setSize(700, 400);
        this.setTitle("Log a Meal");

        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        mealType = new JComboBox<>(mealTypes);
        mealType.setBounds(20, 40, 150, 30);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(20, 80, 100, 30);
        dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(20, 110, 150, 30);

        JLabel ingredientLabel = new JLabel("Ingredient:");
        ingredientLabel.setBounds(20, 150, 100, 30);
        ingredientField = new JTextField("e.g. Egg");
        ingredientField.setBounds(20, 180, 150, 30);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 220, 100, 30);
        quantityField = new JTextField("e.g. 2");
        quantityField.setBounds(20, 250, 150, 30);

        submit = new JButton("Submit");
        submit.setBounds(20, 300, 150, 30);

        submit.addActionListener(e -> {
            String selectedMeal = (String) mealType.getSelectedItem();
            String date = dateField.getText();
            String ingredient = ingredientField.getText();
            String quantity = quantityField.getText();

            try (Connection con = ConnectionProvider.getCon()) {
                int mealId = -1;

                if (!selectedMeal.equals("Snack")) {
                    // Check if meal already exists for this user and date (for Breakfast, Lunch, Dinner)
                    String checkMealSQL = "SELECT idmeals FROM meals WHERE idusers = ? AND mealType = ? AND date = ?";
                    PreparedStatement checkStmt = con.prepareStatement(checkMealSQL);
                    checkStmt.setInt(1, user.getUserId());
                    checkStmt.setString(2, selectedMeal);
                    checkStmt.setString(3, date);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        mealId = rs.getInt("idmeals");  // reuse existing
                    } else {
                        // insert new meal
                        String insertMealSQL = "INSERT INTO meals (idusers, mealType, date) VALUES (?, ?, ?)";
                        PreparedStatement insertMealStmt = con.prepareStatement(insertMealSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                        insertMealStmt.setInt(1, user.getUserId());
                        insertMealStmt.setString(2, selectedMeal);
                        insertMealStmt.setString(3, date);
                        insertMealStmt.executeUpdate();
                        ResultSet keys = insertMealStmt.getGeneratedKeys();
                        if (keys.next()) {
                            mealId = keys.getInt(1);
                        }
                        insertMealStmt.close();
                    }
                    checkStmt.close();
                } else {
                    // Always insert a new meal for snack
                    String insertMealSQL = "INSERT INTO meals (idusers, mealType, date) VALUES (?, ?, ?)";
                    PreparedStatement insertMealStmt = con.prepareStatement(insertMealSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                    insertMealStmt.setInt(1, user.getUserId());
                    insertMealStmt.setString(2, selectedMeal);
                    insertMealStmt.setString(3, date);
                    insertMealStmt.executeUpdate();
                    ResultSet keys = insertMealStmt.getGeneratedKeys();
                    if (keys.next()) {
                        mealId = keys.getInt(1);
                    }
                    insertMealStmt.close();
                }

                // Insert ingredient
                if (mealId != -1) {
                    String insertIngredientSQL = "INSERT INTO ingredients (idmeals, ingredient, quantity) VALUES (?, ?, ?)";
                    PreparedStatement ingredientStmt = con.prepareStatement(insertIngredientSQL);
                    ingredientStmt.setInt(1, mealId);
                    ingredientStmt.setString(2, ingredient);
                    ingredientStmt.setString(3, quantity);
                    ingredientStmt.executeUpdate();
                    ingredientStmt.close();

                    JOptionPane.showMessageDialog(this, "Meal and ingredient logged successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to log meal.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });



        this.add(mealType);
        this.add(dateLabel);
        this.add(dateField);
        this.add(ingredientLabel);
        this.add(ingredientField);
        this.add(quantityLabel);
        this.add(quantityField);
        this.add(submit);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
}
