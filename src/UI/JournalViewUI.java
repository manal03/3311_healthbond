package UI;

import utility.ConnectionProvider;
import models.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class JournalViewUI extends JFrame {
    private final UserProfile user;
    private DefaultListModel<String> listModel;
    private JList<String> mealList;
    private ArrayList<Integer> mealIdList; // Track meal IDs
    private JButton detailsButton;

    public JournalViewUI(UserProfile user) {
        this.user = user;

        setTitle("Meal Journal - " + user.getName());
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        mealList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(mealList);
        mealIdList = new ArrayList<>();

        JLabel header = new JLabel("Meals Logged", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        detailsButton = new JButton("View Details");
        detailsButton.addActionListener(e -> showMealDetails());

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(detailsButton, BorderLayout.SOUTH);

        displayMealsWithCalories();
        setVisible(true);
    }

    private void displayMealsWithCalories() {
        try (Connection con = ConnectionProvider.getCon()) {
            String query = """
                SELECT 
                    m.idmeals,
                    m.date, 
                    m.mealType, 
                    SUM((na.NutrientValue * i.quantity * (1 - IFNULL(ra.RefuseAmount, 0) / 100)) / 100) AS totalCalories
                FROM meals m
                JOIN ingredients i ON m.idmeals = i.idmeals
                JOIN nutrient_amount na ON i.foodID = na.FoodID
                JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID
                LEFT JOIN refuse_amount ra ON i.foodID = ra.FoodID
                WHERE m.idUsers = ? AND nn.NutrientCode = 208
                GROUP BY m.idmeals, m.date, m.mealType
                ORDER BY m.date DESC
            """;

            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int mealId = rs.getInt("idmeals");
                String date = rs.getString("date");
                String mealType = rs.getString("mealType");
                double calories = rs.getDouble("totalCalories");

                mealIdList.add(mealId);
                listModel.addElement(date + " - " + mealType + " - " + String.format("%.1f", calories) + " kcal");
            }

            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading meals: " + e.getMessage());
        }
    }

    private void showMealDetails() {
        int selectedIndex = mealList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meal first.");
            return;
        }

        int selectedMealId = mealIdList.get(selectedIndex);

        try (Connection con = ConnectionProvider.getCon()) {
            String query = """
                SELECT 
                    nn.NutrientName,
                    SUM((na.NutrientValue * i.quantity * (1 - IFNULL(ra.RefuseAmount, 0) / 100)) / 100) AS totalAmount
                FROM ingredients i
                JOIN nutrient_amount na ON i.foodID = na.FoodID
                JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID
                LEFT JOIN refuse_amount ra ON i.foodID = ra.FoodID
                WHERE i.idmeals = ? AND nn.NutrientCode IN (203, 204, 205)
                GROUP BY nn.NutrientName
            """;

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, selectedMealId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder nutrientInfo = new StringBuilder("Nutrient Breakdown:\n");
            while (rs.next()) {
                String nutrientName = rs.getString("NutrientName");
                double amount = rs.getDouble("totalAmount");
                nutrientInfo.append(String.format("%s: %.1f g%n", nutrientName, amount));
            }

            JOptionPane.showMessageDialog(this, nutrientInfo.toString(), "Meal Details", JOptionPane.INFORMATION_MESSAGE);
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading meal details: " + e.getMessage());
        }
    }
}
