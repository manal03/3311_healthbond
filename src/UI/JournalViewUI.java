package UI;

import utility.ConnectionProvider;
import models.UserProfile;
import models.SwapGoal;
import services.NutrientSwapRecommender;
import models.MealItem;
import models.RecommendationInterface;


import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JournalViewUI extends JFrame {
    private final UserProfile user;
    private DefaultListModel<String> listModel;
    private JList<String> mealList;
    private ArrayList<Integer> mealIdList;
    private JButton detailsButton;
    private JButton suggestSwapButton;


    private String inferDefaultUnit(String nutrientName) {
        switch (nutrientName.toLowerCase()) {
            case "calories": return "kcal";
            case "protein":
            case "carbohydrate":
            case "carbohydrates":
            case "fat":
            case "fibre":
            case "fiber": return "g";
            case "sugars": return "g";
            default: return "units";
        }
    }

    public JournalViewUI(UserProfile user) {

        this.user = user;

        setTitle("Meal Journal - " + user.getName());
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        mealList = new JList<>(listModel);
        mealList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(mealList);
        mealIdList = new ArrayList<>();

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Meals Logged", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        suggestSwapButton = new JButton("Suggest Replacements");
        suggestSwapButton.addActionListener(e -> suggestMealSwaps());

        topPanel.add(header, BorderLayout.CENTER);
        topPanel.add(suggestSwapButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        detailsButton = new JButton("View Details");
        detailsButton.addActionListener(e -> showMealDetails());
        bottomPanel.add(detailsButton);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            this.dispose();
            new MainUI(user).setVisible(true);
        });
        bottomPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        displayMealsWithCalories();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayMealsWithCalories() {
        listModel.clear();
        mealIdList.clear();

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
            JOptionPane.showMessageDialog(this, "Error loading meals: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMealDetails() {
        int selectedIndex = mealList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meal first.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedMealId = mealIdList.get(selectedIndex);

        try (Connection con = ConnectionProvider.getCon()) {
            String query = """
            SELECT
                nn.NutrientName,
                SUM((na.NutrientValue * i.quantity * (1 - IFNULL(ra.RefuseAmount, 0) / 100)) / 100) AS totalAmount,
                nn.NutrientUnit
            FROM ingredients i
            JOIN nutrient_amount na ON i.foodID = na.FoodID
            JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID
            LEFT JOIN refuse_amount ra ON i.foodID = ra.FoodID
            WHERE i.idmeals = ? AND nn.NutrientCode IN (203, 204, 205, 291, 269, 208)
            GROUP BY nn.NutrientName, nn.NutrientUnit
            
        """;

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, selectedMealId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder nutrientInfo = new StringBuilder("Nutrient Breakdown:\n");
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String nutrientName = rs.getString("NutrientName");
                double amount = rs.getDouble("totalAmount");
                String unit = rs.getString("NutrientUnit");

                // Handle null or unknown units
                if (unit == null || unit.trim().isEmpty()) {
                    unit = inferDefaultUnit(nutrientName);
                }

                nutrientInfo.append(String.format("%s: %.1f %s%n", nutrientName, amount, unit));
            }

            if (!hasData) {
                nutrientInfo.append("No detailed nutrient information available for this meal.\n");
            }

            JOptionPane.showMessageDialog(this, nutrientInfo.toString(), "Meal Details", JOptionPane.INFORMATION_MESSAGE);
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading meal details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void suggestMealSwaps() {
        int selectedIndex = mealList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meal to get swap suggestions for.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedMealId = mealIdList.get(selectedIndex);

        List<SwapGoal> userGoals = getActiveUserSwapGoals(user);
        if (userGoals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no active nutritional goals set.", "No Active Goals", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<MealItem> mealItems = getMealItemsForMeal(selectedMealId);
        if (mealItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Could not retrieve food items for the selected meal.", "Meal Items Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NutrientSwapRecommender advisor = new NutrientSwapRecommender();
        List<NutrientSwapRecommender.Suggestion> suggestions = advisor.recommendSwaps(userGoals, mealItems);

        displaySwapSuggestions(suggestions);
    }

    private List<SwapGoal> getActiveUserSwapGoals(UserProfile user) {
        List<SwapGoal> activeGoals = new ArrayList<>();
        String sql = "SELECT GoalType, Direction FROM user_goals WHERE idusers = ?";

        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nutrient = rs.getString("GoalType");
                String direction = rs.getString("Direction");
                if (nutrient != null && direction != null) {
                    String mapped = mapGoalTypeToSwapNutrient(nutrient);
                    if (mapped != null) {
                        activeGoals.add(new SwapGoal(user.getUserId(), mapped, direction.toUpperCase(), 0, "", ""));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving active goals: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return activeGoals;
    }

    private String mapGoalTypeToSwapNutrient(String goalType) {
        switch (goalType) {
            case "Protein": return "Protein";
            case "Fat": return "Fat";
            case "Carbohydrates": return "Carbs";
            case "Calories": return "Calories";
            case "Fibre": return "Fiber"; // If your DB uses British spelling
            default: return goalType;
        }
    }

    private List<MealItem> getMealItemsForMeal(int mealId) {
        List<MealItem> mealItems = new ArrayList<>();
        String sql = "SELECT foodID, quantity FROM ingredients WHERE idmeals = ?";

        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mealId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int foodId = rs.getInt("foodID");
                double quantity = rs.getDouble("quantity");
                mealItems.add(new MealItem(foodId, quantity));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving meal items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return mealItems;
    }

    private void displaySwapSuggestions(List<NutrientSwapRecommender.Suggestion> suggestions) {
        if (suggestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suitable swap suggestions found.", "No Suggestions", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Swap Suggestions:\n\n");
        for (NutrientSwapRecommender.Suggestion suggestion : suggestions) {
            sb.append(String.format("Original: %s\n", suggestion.getOriginal()));
            sb.append(String.format("Suggested: %s\n", suggestion.getReplacement()));
            sb.append(String.format("Expected Change: %.2f\n\n", suggestion.getChangeAmount()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.ITALIC, 16));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Nutritional Swap Suggestions", JOptionPane.PLAIN_MESSAGE);
    }
}
