package UI;

import models.NutrientInfo;
import models.UserProfile;
import services.Substitution;
import models.SubstitutionRecord;
import utility.ConnectionProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.sql.Date;
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
            originalMealOptions = Substitution.getLoggedFoodNames(user.getUserId());
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
        dialog.setSize(520, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblOrig = new JLabel("Original Food:");
        JComboBox<String> originalCombo = new JComboBox<>(originalMealOptions.toArray(new String[0]));

        JLabel lblSub = new JLabel("Substitute Food:");
        List<String> allFoods = Substitution.getAllFoodNames();
        JComboBox<String> substituteCombo = new JComboBox<>(allFoods.toArray(new String[0]));
        substituteCombo.setEditable(true);

        JTextField editor = (JTextField) substituteCombo.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = editor.getText().toLowerCase();
                List<String> filtered = allFoods.stream()
                        .filter(name -> name.toLowerCase().contains(input))
                        .collect(Collectors.toList());
                substituteCombo.setModel(new DefaultComboBoxModel<>(filtered.toArray(new String[0])));
                substituteCombo.setSelectedItem(input);
                substituteCombo.showPopup();
            }
        });

        JLabel lblRange = new JLabel("Date Range:");
        String[] rangeOptions = {"Past 7 Days", "Past 15 Days", "Past 30 Days", "Custom Range"};
        JComboBox<String> rangeCombo = new JComboBox<>(rangeOptions);

        JLabel lblStart = new JLabel("Start Date (YYYY-MM-DD):");
        JTextField startDateField = new JTextField(LocalDate.now().minusDays(7).toString());

        JLabel lblEnd = new JLabel("End Date (YYYY-MM-DD):");
        JTextField endDateField = new JTextField(LocalDate.now().toString());

        startDateField.setEnabled(false);
        endDateField.setEnabled(false);

        rangeCombo.addActionListener(e -> {
            String selected = (String) rangeCombo.getSelectedItem();
            LocalDate today = LocalDate.now();
            switch (selected) {
                case "Past 7 Days" -> {
                    startDateField.setText(today.minusDays(7).toString());
                    endDateField.setText(today.toString());
                }
                case "Past 15 Days" -> {
                    startDateField.setText(today.minusDays(15).toString());
                    endDateField.setText(today.toString());
                }
                case "Past 30 Days" -> {
                    startDateField.setText(today.minusDays(30).toString());
                    endDateField.setText(today.toString());
                }
            }
            boolean custom = selected.equals("Custom Range");
            startDateField.setEnabled(custom);
            endDateField.setEnabled(custom);
        });

        panel.add(lblOrig, gbc); gbc.gridx = 1; panel.add(originalCombo, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblSub, gbc); gbc.gridx = 1; panel.add(substituteCombo, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblRange, gbc); gbc.gridx = 1; panel.add(rangeCombo, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblStart, gbc); gbc.gridx = 1; panel.add(startDateField, gbc);
        gbc.gridx = 0; gbc.gridy++; panel.add(lblEnd, gbc); gbc.gridx = 1; panel.add(endDateField, gbc);

        JButton btnApply = new JButton("Apply Substitution");
        btnApply.setBackground(new Color(85, 170, 85));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFont(new Font("Arial", Font.BOLD, 13));

        btnApply.addActionListener(e -> {
            String orig = (String) originalCombo.getSelectedItem();
            String sub = (String) substituteCombo.getSelectedItem();
            String startStr = startDateField.getText().trim();
            String endStr = endDateField.getText().trim();

            if (orig == null || sub == null || orig.equals(sub)) {
                JOptionPane.showMessageDialog(dialog, "Please select different original and substitute foods.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate start = LocalDate.parse(startStr);
                LocalDate end = LocalDate.parse(endStr);
                if (start.isAfter(end)) {
                    JOptionPane.showMessageDialog(dialog, "Start date must be before or equal to end date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int origId = Substitution.getFoodIdByName(orig);
                int subId = Substitution.getFoodIdByName(sub);
                new Substitution().applySubstitutionToMeals(user.getUserId(), origId, subId, Date.valueOf(start), Date.valueOf(end));
                JOptionPane.showMessageDialog(dialog, "Substitution applied from " + startStr + " to " + endStr + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(btnApply, gbc);

        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void showSubstitutionHistoryDialog() {
        JDialog dialog = new JDialog(this, "Substitution History", true);
        dialog.setSize(700, 420);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Original", "Substitute", "Start", "End", "Applied On"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement stmt = con.prepareStatement("SELECT original_food_id, substitute_food_id, start_date, end_date, date_applied FROM swap_records WHERE user_id = ? ORDER BY date_applied DESC")) {

            stmt.setInt(1, user.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String orig = Substitution.getFoodNameById(rs.getInt("original_food_id"));
                    String sub = Substitution.getFoodNameById(rs.getInt("substitute_food_id"));
                    model.addRow(new Object[]{
                            orig, sub,
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getDate("date_applied")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    private void showSubstitutionStatsDialog() {
        JDialog dialog = new JDialog(this, "Substitution Stats", true);
        dialog.setSize(800, 420);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Original", "Substitute", "Applied On", "Calories", "Protein (g)", "Fat (g)", "Carbs (g)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        try {
            List<SubstitutionRecord> records = new Substitution().getSubstitutionHistory(user.getUserId());
            List<Integer> subIds = records.stream().map(SubstitutionRecord::getSubstituteFoodId).distinct().toList();
            Map<Integer, Map<String, NutrientInfo>> nutrientMap = new Substitution().getNutrientDataForFoods(subIds);

            for (SubstitutionRecord r : records) {
                Map<String, NutrientInfo> nutri = nutrientMap.getOrDefault(r.getSubstituteFoodId(), Collections.emptyMap());
                model.addRow(new Object[]{
                        Substitution.getFoodNameById(r.getOriginalFoodId()),
                        Substitution.getFoodNameById(r.getSubstituteFoodId()),
                        r.getDateApplied(),
                        nutri.getOrDefault("ENERGY (KILOCALORIES)", new NutrientInfo(0, "")).getValue(),
                        nutri.getOrDefault("PROTEIN", new NutrientInfo(0, "")).getValue(),
                        nutri.getOrDefault("FAT (TOTAL LIPIDS)", new NutrientInfo(0, "")).getValue(),
                        nutri.getOrDefault("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", new NutrientInfo(0, "")).getValue()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error loading stats: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
    }
}