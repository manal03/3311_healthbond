package UI;

import utility.ConnectionProvider;
import models.UserProfile;
import models.Goal;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// This class assumes your 'Goal.java' file exists separately in your project.

public class ViewGoalsUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private UserProfile user;

    // --- UI Components ---
    private JList<Goal> goalsList;
    private DefaultListModel<Goal> listModel;
    private JButton removeGoalButton;
    private JButton backButton;

    public ViewGoalsUI(UserProfile user) {
        this.user = user;
        setTitle("Your Saved Goals");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Title Label ---
        JLabel titleLabel = new JLabel("Your Current Goals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // --- JList to display the saved goals ---
        listModel = new DefaultListModel<>();
        goalsList = new JList<>(listModel);
        goalsList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        goalsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(goalsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- Button Panel (Bottom) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // "Remove Selected Goal" Button
        removeGoalButton = new JButton("Remove Selected Goal");
        removeGoalButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        removeGoalButton.addActionListener(e -> removeSelectedGoal());
        buttonPanel.add(removeGoalButton);

        // "Back to Main Menu" Button
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

        // --- Load the user's saved goals from the database ---
        loadUserGoals();
    }

    /**
     * Fetches the user's saved goals from the 'user_goals' table and displays them in the list.
     */
    private void loadUserGoals() {
        listModel.clear(); // Clear the list before loading
        String sql = "SELECT * FROM user_goals WHERE idusers = ?";
        try (Connection conn = ConnectionProvider.getCon();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Reconstruct the Goal object from the database data
                Goal goal = new Goal(
                        rs.getString("GoalID"),
                        rs.getString("GoalType"),
                        rs.getString("Intensity"),
                        rs.getString("Direction"),
                        rs.getString("Label"),
                        rs.getDouble("Amount"),
                        rs.getBoolean("isPercentage")
                );
                listModel.addElement(goal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading goals: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes the currently selected goal from the database.
     */
    private void removeSelectedGoal() {
        Goal selectedGoal = goalsList.getSelectedValue();
        if (selectedGoal == null) {
            JOptionPane.showMessageDialog(this, "Please select a goal to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this goal?\n\"" + selectedGoal.getLabel() + "\"",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM user_goals WHERE GoalID = ?";
            try (Connection conn = ConnectionProvider.getCon();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, selectedGoal.getGoalID());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Goal removed successfully!");
                    loadUserGoals(); // Refresh the list
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing goal: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}