package UI;

import database.ConnectionProvider;
import models.UserProfile;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Helper Class to store food data in the search list
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
        this.setSize(700, 550);
        this.setTitle("Log a Meal");

        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        mealType = new JComboBox<>(mealTypes);
        mealType.setBounds(20, 60, 150, 30);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(20, 80, 150, 30);
        dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(20, 110, 150, 30);

        JLabel ingredientLabel = new JLabel("Search Ingredient:");
        ingredientLabel.setBounds(20, 150, 150, 30);
        searchField = new JTextField();
        searchField.setBounds(20, 180, 300, 30);

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
        quantityField = new JTextField("100");
        quantityField.setBounds(20, 410, 150, 30);

        submit = new JButton("Add Ingredient");
        submit.setBounds(20, 460, 150, 30);
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

    private void searchForFood() {
        String searchText = searchField.getText().trim();
        if (searchText.length() < 3) {
            listModel.clear();
            return;
        }

        String[] searchWords = searchText.split("\\s+");
        StringBuilder sqlBuilder = new StringBuilder("SELECT FoodID, FoodDescription FROM food_name WHERE 1=1 ");
        for (String ignored : searchWords) {
            sqlBuilder.append("AND LOWER(FoodDescription) LIKE ? ");
        }
        sqlBuilder.append("LIMIT 30");

        List<FoodItem> foundItems = new ArrayList<>();

        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < searchWords.length; i++) {
                pstmt.setString(i + 1, "%" + searchWords[i].toLowerCase() + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                foundItems.add(new FoodItem(rs.getInt("FoodID"), rs.getString("FoodDescription")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        try (Connection con = ConnectionProvider.getCon()) {
            int mealId = getOrCreateMealId(con, (String) mealType.getSelectedItem(), dateField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            if (mealId != -1) {
                String insertIngredientSQL = "INSERT INTO ingredients (idmeals, FoodID, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement ingredientStmt = con.prepareStatement(insertIngredientSQL)) {
                    ingredientStmt.setInt(1, mealId);
                    ingredientStmt.setInt(2, selectedFood.getFoodId());
                    ingredientStmt.setInt(3, quantity);
                    ingredientStmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "'" + selectedFood + "' logged successfully!");
                searchField.setText("");
                quantityField.setText("100");
                listModel.clear();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private int getOrCreateMealId(Connection con, String mealType, String date) throws SQLException {
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
        return -1;
    }
}
