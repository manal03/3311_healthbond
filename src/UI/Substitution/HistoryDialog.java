package UI.Substitution;

import models.UserProfile;
import services.Substitution;
import utility.ConnectionProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class HistoryDialog {
    private final UserProfile user;
    private final JDialog dialog;

    public HistoryDialog(JFrame parent, UserProfile user) {
        this.user = user;
        this.dialog = new JDialog(parent, "Substitution History", true);
        initUI();
        dialog.setSize(700, 420);
        dialog.setLocationRelativeTo(parent);
    }

    private void initUI() {
        String[] cols = {"Original", "Substitute", "Start", "End", "Applied On"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT original_food_id, substitute_food_id, start_date, end_date, date_applied " +
                             "FROM swap_records WHERE user_id = ? ORDER BY date_applied DESC")) {

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
            JOptionPane.showMessageDialog(dialog, "Failed to load history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
