package UI;

import models.UserProfile;
import models.Goal;
import services.GoalGenerator;
import utility.ConnectionProvider;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GoalGeneratorUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private UserProfile user;

    private JList<Goal> goalsList;
    private DefaultListModel<Goal> listModel;
    private JButton addGoalsButton;
    private JButton backButton;
    private JLabel instructionLabel;

    private int existingGoalCount = 0;

    public GoalGeneratorUI(UserProfile user) {
        this.user = user;
        setTitle("Generate Your Nutrition Goals");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Potential Goals Based On Your Recent Activity", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        instructionLabel = new JLabel("You may select up to two goals to add.", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        topPanel.add(instructionLabel);
        add(topPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        goalsList = new JList<>(listModel);
        goalsList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        goalsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(goalsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addGoalsButton = new JButton("Add Selected Goals");
        addGoalsButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addGoalsButton.addActionListener(e -> addSelectedGoals());
        buttonPanel.add(addGoalsButton);

        backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.addActionListener(e -> {
            this.dispose();
            new MainUI(user).setVisible(true);
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        loadPotentialGoals();
    }

    private void loadPotentialGoals() {
        GoalGenerator generator = new GoalGenerator();

        // First, check how many goals the user already has.
        List<String> existingGoals = generator.getExistingGoalLabels(user);
        existingGoalCount = existingGoals.size();

        // If user already has 2 or more goals, disable adding more.
        if (existingGoalCount >= 2) {
            listModel.clear();
            listModel.addElement(new Goal(null, "Limit Reached", "N/A", "N/A", "You have already added the maximum of 2 goals.", 0, false));
            goalsList.setEnabled(false);
            addGoalsButton.setEnabled(false);
            instructionLabel.setText("No more goals can be added. Manage your existing goals first.");
            return;
        }

        List<Goal> goals = generator.generateGoals(user);
        listModel.clear();
        for (Goal goal : goals) {
            listModel.addElement(goal);
        }
    }

    private void addSelectedGoals() {
        List<Goal> selectedGoals = goalsList.getSelectedValuesList();

        if (selectedGoals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one goal from the list.");
            return;
        }

        // --- NEW VALIDATION LOGIC ---
        if (selectedGoals.size() + existingGoalCount > 2) {
            JOptionPane.showMessageDialog(this, "You can only have a maximum of two goals. You have " + existingGoalCount + " existing goal(s). Please select fewer items.", "Selection Limit Exceeded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int successCount = 0;
        for (Goal goal : selectedGoals) {
            if (saveGoalToDatabase(goal)) {
                successCount++;
            }
        }

        if (successCount > 0) {
            JOptionPane.showMessageDialog(this, successCount + " goal(s) have been saved successfully!");
            // Refresh the UI to reflect the new state (e.g., disable the add button if the limit is reached)
            loadPotentialGoals();
        }
    }

    private boolean saveGoalToDatabase(Goal goal) {
        String sql = "INSERT INTO user_goals (idusers, GoalID, GoalType, Intensity, Direction, Label, Amount, isPercentage, DateAdded) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, goal.getGoalID());
            pstmt.setString(3, goal.getGoalType());
            pstmt.setString(4, goal.getIntensity());
            pstmt.setString(5, goal.getDirection());
            pstmt.setString(6, goal.getLabel());
            pstmt.setDouble(7, goal.getAmount());
            pstmt.setBoolean(8, goal.isPercentage());
            pstmt.setDate(9, java.sql.Date.valueOf(LocalDate.now()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving goal '" + goal.getLabel() + "': " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
