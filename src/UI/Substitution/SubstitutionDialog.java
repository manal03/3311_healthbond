package UI.Substitution;

import UI.Components.DateRangeSelector;
import UI.Components.FoodSelectionComboBox;
import models.UserProfile;
import services.Substitution;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class SubstitutionDialog {
    private final UserProfile user;
    private final JDialog dialog;
    private FoodSelectionComboBox originalFoodCombo;
    private FoodSelectionComboBox substituteFoodCombo;
    private DateRangeSelector dateRangeSelector;

    public SubstitutionDialog(JFrame parent, UserProfile user) {
        this.user = user;
        this.dialog = new JDialog(parent, "Log a Substitution", true);
        initUI();
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        List<String> originalFoods;
        List<String> allFoods;

        try {
            originalFoods = Substitution.getLoggedFoodNames(user.getUserId());
            allFoods = Substitution.getAllFoodNames();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog,
                    "Failed to load food lists: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            originalFoods = List.of();
            allFoods = List.of();
        }

        // Original Food Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Original Food:"), gbc);

        // Original Food ComboBox
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        originalFoodCombo = new FoodSelectionComboBox(originalFoods, null);
        panel.add(originalFoodCombo.getComponent(), gbc);

        // Substitute Food Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Substitute Food:"), gbc);

        // Substitute Food ComboBox
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        substituteFoodCombo = new FoodSelectionComboBox(allFoods, null);
        panel.add(substituteFoodCombo.getComponent(), gbc);

        // Date Range Selector Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel dateRangeLabel = new JLabel("Select Date Range");
        dateRangeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(dateRangeLabel, gbc);

        // Date Range Selector Component
        gbc.gridy = 3;
        dateRangeSelector = new DateRangeSelector();
        panel.add(dateRangeSelector.getComponent(), gbc);

        // Apply Button
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton applyBtn = new JButton("Apply Substitution");
        applyBtn.setBackground(new Color(85, 170, 85));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Arial", Font.BOLD, 14));
        applyBtn.setFocusable(false);
        applyBtn.setPreferredSize(new Dimension(200, 40));
        applyBtn.addActionListener(e -> onApply());
        panel.add(applyBtn, gbc);

        dialog.setContentPane(panel);
    }

    private void onApply() {
        String orig = originalFoodCombo.getSelectedFood();
        String sub = substituteFoodCombo.getSelectedFood();
        String startStr = dateRangeSelector.getStartDate();
        String endStr = dateRangeSelector.getEndDate();

        if (orig == null || sub == null || orig.equals(sub)) {
            JOptionPane.showMessageDialog(dialog,
                    "Please select different original and substitute foods.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate start = LocalDate.parse(startStr);
            LocalDate end = LocalDate.parse(endStr);

            if (start.isAfter(end)) {
                JOptionPane.showMessageDialog(dialog,
                        "Start date must be before or equal to end date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int origId = Substitution.getFoodIdByName(orig);
            int subId = Substitution.getFoodIdByName(sub);

            new Substitution().applySubstitutionToMeals(user.getUserId(),
                    origId, subId,
                    Date.valueOf(start), Date.valueOf(end));

            JOptionPane.showMessageDialog(dialog,
                    "Substitution applied from " + startStr + " to " + endStr + ".",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dialog.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDialog() {
        dialog.setVisible(true);
    }
}
