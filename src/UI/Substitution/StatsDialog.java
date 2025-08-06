package UI.Substitution;

import models.NutrientInfo;
import models.SubstitutionRecord;
import models.UserProfile;
import services.Substitution;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsDialog {
    private final UserProfile user;
    private final JDialog dialog;

    public StatsDialog(JFrame parent, UserProfile user) {
        this.user = user;
        this.dialog = new JDialog(parent, "Substitution Stats", true);
        initUI();
        dialog.setSize(800, 420);
        dialog.setLocationRelativeTo(parent);
    }

    private void initUI() {
        String[] cols = {"Original", "Substitute", "Applied On", "Calories", "Protein (g)", "Fat (g)", "Carbs (g)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        try {
            List<SubstitutionRecord> records = new Substitution().getSubstitutionHistory(user.getUserId());
            List<Integer> subIds = records.stream()
                    .map(SubstitutionRecord::getSubstituteFoodId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Integer, Map<String, NutrientInfo>> nutrientMap =
                    new Substitution().getNutrientDataForFoods(subIds);

            for (SubstitutionRecord r : records) {
                Map<String, NutrientInfo> nutri =
                        nutrientMap.getOrDefault(r.getSubstituteFoodId(), Collections.emptyMap());

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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Failed to load stats: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(new JScrollPane(table));
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 240, 220));
        table.setGridColor(new Color(180, 200, 180));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(140, 195, 140));
    }

    public void showDialog() {
        dialog.setVisible(true);
    }
}
