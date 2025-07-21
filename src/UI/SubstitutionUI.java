package UI;

import models.UserProfile;
import services.NutrientInfo;
import services.Substitution;
import services.SubstitutionRecord;
import utility.ConnectionProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class SubstitutionUI extends JFrame {
    private final UserProfile user;
    private List<String> originalMealOptions = new ArrayList<>();

    public SubstitutionUI(UserProfile user) {
        this.user = user;
        try {
            originalMealOptions = Substitution.getLoggedFoodNames(user.getUserId()); // Cache the meal list
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load food names: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        setTitle("Meal Substitution System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(null);
        getContentPane().setBackground(new Color(116, 209, 115));

        JLabel title = new JLabel("Food Substitution Panel");
        title.setBounds(120, 30, 400, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title);

        JPanel content = new JPanel();
        content.setLayout(null);
        content.setBounds(100, 100, 600, 350);
        content.setBackground(new Color(142, 182, 101));
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(content);

        JButton btnNew = new JButton("Log New Substitution");
        styleButton(btnNew);
        btnNew.setBounds(200, 40, 200, 40);
        btnNew.addActionListener(e -> showSubstitutionDialog());
        content.add(btnNew);

        JButton btnHistory = new JButton("View Substitution History");
        styleButton(btnHistory);
        btnHistory.setBounds(200, 100, 200, 40);
        btnHistory.addActionListener(e -> showSubstitutionHistoryDialog());
        content.add(btnHistory);

        JButton btnStats = new JButton("View Substitution Stats");
        styleButton(btnStats);
        btnStats.setBounds(200, 160, 200, 40);
        btnStats.addActionListener(e -> showSubstitutionStatsDialog());
        content.add(btnStats);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(85, 170, 85));
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
    }

    private void showSubstitutionDialog() {
        JDialog dialog = new JDialog(this, "Log a Substitution", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        // Original and substitute combos
        JComboBox<String> originalCombo = new JComboBox<>(originalMealOptions.toArray(new String[0]));
        JComboBox<String> substituteCombo = new JComboBox<>(Substitution.getAllFoodNames().toArray(new String[0]));

        // Date range selection
        String[] rangeOptions = {"Past 7 Days", "Past 15 Days", "Past 30 Days", "Custom Range"};
        JComboBox<String> rangeCombo = new JComboBox<>(rangeOptions);

        // Date fields
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField(LocalDate.now().toString());

        // Default to 7 days ago
        startDateField.setText(LocalDate.now().minusDays(7).toString());
        startDateField.setEnabled(false);
        endDateField.setEnabled(false);

        // Toggle based on range selection
        rangeCombo.addActionListener(e -> {
            String selected = (String) rangeCombo.getSelectedItem();
            LocalDate today = LocalDate.now();

            switch (selected) {
                case "Past 7 Days":
                    startDateField.setText(today.minusDays(7).toString());
                    endDateField.setText(today.toString());
                    break;
                case "Past 15 Days":
                    startDateField.setText(today.minusDays(15).toString());
                    endDateField.setText(today.toString());
                    break;
                case "Past 30 Days":
                    startDateField.setText(today.minusDays(30).toString());
                    endDateField.setText(today.toString());
                    break;
                case "Custom Range":
                    // Leave fields editable; no change to values
                    break;
            }

            boolean isCustom = "Custom Range".equals(selected);
            startDateField.setEnabled(isCustom);
            endDateField.setEnabled(isCustom);
        });

        // Add components to dialog
        dialog.add(new JLabel("Original Food:"));
        dialog.add(originalCombo);
        dialog.add(new JLabel("Substitute Food:"));
        dialog.add(substituteCombo);
        dialog.add(new JLabel("Date Range:"));
        dialog.add(rangeCombo);
        dialog.add(new JLabel("Start Date (YYYY-MM-DD):"));
        dialog.add(startDateField);
        dialog.add(new JLabel("End Date (YYYY-MM-DD):"));
        dialog.add(endDateField);

        // Apply button
        JButton btnLog = new JButton("Apply Substitution");
        btnLog.addActionListener(e -> {
            String orig = (String) originalCombo.getSelectedItem();
            String sub = (String) substituteCombo.getSelectedItem();
            String startStr = startDateField.getText().trim();
            String endStr = endDateField.getText().trim();

            if (orig == null || sub == null) {
                JOptionPane.showMessageDialog(dialog, "Please select both original and substitute foods.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (orig.equals(sub)) {
                JOptionPane.showMessageDialog(dialog, "Original and substitute food cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate startLocalDate = LocalDate.parse(startStr);
                LocalDate endLocalDate = LocalDate.parse(endStr);

                if (startLocalDate.isAfter(endLocalDate)) {
                    JOptionPane.showMessageDialog(dialog, "Start date must be before or equal to end date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.sql.Date startDate = java.sql.Date.valueOf(startLocalDate);
                java.sql.Date endDate = java.sql.Date.valueOf(endLocalDate);

                int origId = Substitution.getFoodIdByName(orig);
                int subId = Substitution.getFoodIdByName(sub);

                new Substitution().applySubstitutionToMeals(user.getUserId(), origId, subId, startDate, endDate);
                JOptionPane.showMessageDialog(dialog, "Substitution applied to meals from " + startStr + " to " + endStr + ".");
                dialog.dispose();
            } catch (java.time.format.DateTimeParseException ex1) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex2) {
                JOptionPane.showMessageDialog(dialog, "Error applying substitution: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel());  // Empty for alignment
        dialog.add(btnLog);
        dialog.setVisible(true);
    }

    private void showSubstitutionHistoryDialog() {
        JDialog dialog = new JDialog(this, "Substitution History", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Original", "Substitute", "Start Date", "End Date", "Date Applied"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        String sql = "SELECT original_food_id, substitute_food_id, start_date, end_date, date_applied " +
                "FROM swap_records WHERE user_id = ? ORDER BY date_applied DESC";

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int origId = rs.getInt("original_food_id");
                    int subId = rs.getInt("substitute_food_id");

                    String origName = "Unknown";
                    String subName = "Unknown";

                    try {
                        origName = Substitution.getFoodNameById(origId);
                    } catch (SQLException e) {
                        origName = "Unknown (ID: " + origId + ")";
                    }
                    try {
                        subName = Substitution.getFoodNameById(subId);
                    } catch (SQLException e) {
                        subName = "Unknown (ID: " + subId + ")";
                    }

                    java.sql.Date startDate = rs.getDate("start_date");
                    java.sql.Date endDate = rs.getDate("end_date");
                    java.sql.Date loggedDate = rs.getDate("date_applied");

                    String from = (startDate != null) ? startDate.toString() : "N/A";
                    String to = (endDate != null) ? endDate.toString() : "N/A";
                    String logged = (loggedDate != null) ? loggedDate.toString() : "N/A";

                    model.addRow(new Object[]{origName, subName, from, to, logged});
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading substitution history: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showSubstitutionStatsDialog() {
        JDialog dialog = new JDialog(this, "Substitution Stats", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Original Food", "Substitute Food", "Date Applied", "Calories", "Protein (g)", "Fat (g)", "Carbs (g)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        try {
            List<SubstitutionRecord> records = new Substitution().getSubstitutionHistory(user.getUserId());

            List<Integer> substituteFoodIds = records.stream()
                    .map(SubstitutionRecord::getSubstituteFoodId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Integer, Map<String, NutrientInfo>> allNutrients = new Substitution().getNutrientDataForFoods(substituteFoodIds);

            for (SubstitutionRecord r : records) {
                String origName;
                String subName;
                try {
                    origName = Substitution.getFoodNameById(r.getOriginalFoodId());
                    subName = Substitution.getFoodNameById(r.getSubstituteFoodId());
                } catch (SQLException ex) {
                    origName = "Unknown";
                    subName = "Unknown";
                    ex.printStackTrace();
                }

                Map<String, NutrientInfo> nutrients = allNutrients.getOrDefault(r.getSubstituteFoodId(), Collections.emptyMap());

                float calories = nutrients.getOrDefault("ENERGY (KILOCALORIES)", new NutrientInfo(0f, "")).getValue();
                float protein = nutrients.getOrDefault("PROTEIN", new NutrientInfo(0f, "")).getValue();
                float fat = nutrients.getOrDefault("FAT (TOTAL LIPIDS)", new NutrientInfo(0f, "")).getValue();
                float carbs = nutrients.getOrDefault("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", new NutrientInfo(0f, "")).getValue();

                model.addRow(new Object[]{
                        origName, subName, r.getDateApplied().toString(),
                        calories, protein, fat, carbs
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error loading substitution stats: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}


