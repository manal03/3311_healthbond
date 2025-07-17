import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class FoodItem {
    private final int foodId;
    private final String foodDescription;

    public FoodItem(int foodId, String foodDescription) {
        this.foodId = foodId;
        this.foodDescription = foodDescription;
    }

    public int getFoodId() {
        return foodId;
    }

  
    @Override
    public String toString() {
        return foodDescription;
    }
}


public class MealLog extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton submit;
    private JComboBox<String> mealType;
    private JTextField dateField, quantityField;
    private UserProfile user;

   
    private JTextField searchField; 
    private JList<FoodItem> searchResultsList;
    private DefaultListModel<FoodItem> listModel;

    public MealLog(UserProfile user) {
        this.user = user;
        this.setLayout(null);
        this.setSize(700, 550); // Increased height to accommodate the search results
        this.setTitle("Log a Meal");

        // --- Standard Meal and Date setup (no changes) ---
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        mealType = new JComboBox<>(mealTypes);
        mealType.setBounds(20, 40, 150, 30);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(20, 80, 100, 30);
        dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(20, 110, 150, 30);

        
        JLabel ingredientLabel = new JLabel("Search Ingredient:");
        ingredientLabel.setBounds(20, 150, 150, 30);
        searchField = new JTextField();
        searchField.setBounds(20, 180, 300, 30);

        // This listener triggers a search every time the user types.
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchForFood(); }
            public void removeUpdate(DocumentEvent e) { searchForFood(); }
            public void changedUpdate(DocumentEvent e) { searchForFood(); }
        });


        listModel = new DefaultListModel<>();
        searchResultsList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(searchResultsList);
        listScrollPane.setBounds(20, 220, 650, 150); 
    
        JLabel quantityLabel = new JLabel("Quantity (grams):");
        quantityLabel.setBounds(20, 380, 150, 30);
        quantityField = new JTextField("e.g. 100");
        quantityField.setBounds(20, 410, 150, 30);

        submit = new JButton("Add Ingredient");
        submit.setBounds(20, 460, 150, 30);

        // --- Updated ActionListener ---
        submit.addActionListener(e -> logIngredient());

    
        this.add(mealType);
        this.add(dateLabel);
        this.add(dateField);
        this.add(ingredientLabel);
        this.add(searchField);
        this.add(listScrollPane);
        this.add(quantityLabel);
        this.add(quantityField);
        this.add(submit);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Searches the database for food items matching the text in the search field.
     */
    private void searchForFood() {
        String searchText = searchField.getText();
       
        if (searchText.trim().length() < 3) {
            listModel.clear();
            return;
        }

        String sql = "SELECT FoodID, FoodDescription FROM food_name WHERE FoodDescription LIKE ? LIMIT 20";
        List<FoodItem> foundItems = new ArrayList<>();

        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int foodId = rs.getInt("FoodID");
                String description = rs.getString("FoodDescription");
                foundItems.add(new FoodItem(foodId, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the JList with the results.
        listModel.clear();
        for (FoodItem item : foundItems) {
            listModel.addElement(item);
        }
    }


    private void logIngredient() {
        FoodItem selectedFood = searchResultsList.getSelectedValue();
        if (selectedFood == null) {
            JOptionPane.showMessageDialog(this, "Please search for and select an ingredient from the list.");
            return;
        }

        String selectedMealType = (String) mealType.getSelectedItem();
        String date = dateField.getText();
        String quantityStr = quantityField.getText();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.");
            return;
        }

        try (Connection con = ConnectionProvider.getCon()) {
            
            int mealId = getOrCreateMealId(con, selectedMealType, date);

            // --- Insert Ingredient using FoodID ---
            if (mealId != -1) {

                String insertIngredientSQL = "INSERT INTO ingredients (idmeals, FoodID, quantity) VALUES (?, ?, ?)";
                PreparedStatement ingredientStmt = con.prepareStatement(insertIngredientSQL);
                ingredientStmt.setInt(1, mealId);
                ingredientStmt.setInt(2, selectedFood.getFoodId()); // Use the selected FoodID
                ingredientStmt.setInt(3, quantity);
                ingredientStmt.executeUpdate();
                ingredientStmt.close();

                JOptionPane.showMessageDialog(this, "'" + selectedFood + "' logged successfully!");
                // Clear fields for next entry
                searchField.setText("");
                quantityField.setText("");
                listModel.clear();

            } else {
                JOptionPane.showMessageDialog(this, "Failed to log meal.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        
    }
    private int getOrCreateMealId(Connection con, String mealType, String date) throws SQLException {
        int mealId = -1;
        
        String checkMealSQL = "SELECT idmeals FROM meals WHERE idusers = ? AND mealType = ? AND date = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkMealSQL)) {
            checkStmt.setInt(1, user.getUserId());
            checkStmt.setString(2, mealType);
            checkStmt.setString(3, date);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idmeals");
            }
        }

        // If no meal was found, create a new one.
        String insertMealSQL = "INSERT INTO meals (idusers, mealType, date) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = con.prepareStatement(insertMealSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, user.getUserId());
            insertStmt.setString(2, mealType);
            insertStmt.setString(3, date);
            insertStmt.executeUpdate();
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
        return -1; // Should not happen if insertion works
    }
    
    
    
}

    /**
     * Checks if a meal exists for the user/date/type, or creates a new one.
     * @return The idmeals for the meal, or -1 on failure.
     */
    