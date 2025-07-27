package UI;

import UI.Components.BackButton;
import utility.ConnectionProvider;
import models.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditProfileUI extends JFrame implements ActionListener {
    private UserProfile user;
    private JLabel nameLabel, dobLabel, heightLabel, weightLabel, sexLabel;
    private JButton editNameBtn, editDOBBtn, editHeightBtn, editWeightBtn, editSexBtn;
    private JButton unitToggleBtn;


    public EditProfileUI(UserProfile user) {
        this.user = user;
        this.setLayout(null);
        this.setTitle("Health Tracker - Edit Profile");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(116, 209, 115));

        BackButton backButton = new BackButton(this);
        backButton.setBounds(20, 20, 80, 30);
        this.add(backButton);

        JLabel welcome = createLabel("Edit Profile - " + user.getName(), 120, 30, 400, 30, 18, true);
        JLabel subtitle = createLabel("Update your personal information", 120, 60, 300, 20, 12, false);
        this.add(welcome);
        this.add(subtitle);

        JPanel contentPanel = new JPanel(null);
        contentPanel.setBounds(100, 120, 600, 400);
        contentPanel.setBackground(new Color(142, 182, 101));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.add(contentPanel);

        JLabel titleLabel = createLabel("Current Profile Information", 20, 20, 300, 25, 14, true);
        contentPanel.add(titleLabel);

        createProfileRow(contentPanel, "Name:", user.getName(), 60, 0);
        createProfileRow(contentPanel, "Date of Birth:", user.getDob(), 100, 1);
        createProfileRow(contentPanel, "Height:", formatHeight(user.getHeight()), 140, 2);
        createProfileRow(contentPanel, "Weight:", formatWeight(user.getWeight()), 180, 3);
        createProfileRow(contentPanel, "Sex:", user.getSex(), 220, 4);
        unitToggleBtn = new JButton("Change Units");
        unitToggleBtn.setBounds(240, 300, 150, 30);
        unitToggleBtn.setFocusable(false);
        unitToggleBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        unitToggleBtn.setBackground(new Color(100, 100, 255));
        unitToggleBtn.setForeground(Color.WHITE);
        unitToggleBtn.addActionListener(this);
        contentPanel.add(unitToggleBtn);

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y, int width, int height, int fontSize, boolean bold) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void createProfileRow(JPanel parent, String labelText, String value, int yPos, int buttonIndex) {
        JLabel fieldLabel = new JLabel(labelText);
        fieldLabel.setBounds(30, yPos, 120, 25);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setBounds(160, yPos, 250, 25);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton editBtn = createEditButton(yPos);

        switch (buttonIndex) {
            case 0 -> { nameLabel = valueLabel; editNameBtn = editBtn; }
            case 1 -> { dobLabel = valueLabel; editDOBBtn = editBtn; }
            case 2 -> { heightLabel = valueLabel; editHeightBtn = editBtn; }
            case 3 -> { weightLabel = valueLabel; editWeightBtn = editBtn; }
            case 4 -> { sexLabel = valueLabel; editSexBtn = editBtn; }
        }

        parent.add(fieldLabel);
        parent.add(valueLabel);
        parent.add(editBtn);
    }

    private JButton createEditButton(int yPos) {
        JButton editBtn = new JButton("Edit");
        editBtn.setBounds(420, yPos, 70, 25);
        editBtn.setFocusable(false);
        editBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        editBtn.setBackground(new Color(85, 170, 85));
        editBtn.setForeground(Color.WHITE);
        editBtn.addActionListener(this);
        return editBtn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editNameBtn) editName();
        else if (e.getSource() == editDOBBtn) editDOB();
        else if (e.getSource() == editHeightBtn) editHeight();
        else if (e.getSource() == editWeightBtn) editWeight();
        else if (e.getSource() == editSexBtn) editSex();
        else if (e.getSource() == unitToggleBtn) toggleUnits();

    }

    private void editName() {
        String newName = JOptionPane.showInputDialog(this, "Enter new name:", user.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            try (Connection con = ConnectionProvider.getCon()) {
                String sql = "UPDATE users SET name = ? WHERE idusers = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newName);
                ps.setInt(2, user.getUserId());
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    user.setName(newName);
                    refreshProfileDisplay();
                    JOptionPane.showMessageDialog(this, "Name updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update name.");
                }
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }
    private void toggleUnits() {
        boolean currentUnit = user.isUsingImperial();
        boolean newUnit = !currentUnit;

        try (Connection con = ConnectionProvider.getCon()) {
            String sql = "UPDATE users SET unit = ? WHERE idusers = ?";
            assert con != null;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newUnit ? "imperial" : "metric");
            ps.setInt(2, user.getUserId());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                user.setUsingImperial(newUnit); // update in memory
                refreshProfileDisplay(); // refresh height/weight
                JOptionPane.showMessageDialog(this, "Units changed to " + (newUnit ? "Imperial" : "Metric") + "!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update units.");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }


    private void editDOB() {
        String newDOB = JOptionPane.showInputDialog(this, "Enter new date of birth (YYYY-MM-DD):", user.getDob());
        if (newDOB != null && !newDOB.trim().isEmpty()) {
            try (Connection con = ConnectionProvider.getCon()) {
                String sql = "UPDATE users SET dateofbirth = ? WHERE idusers = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newDOB);
                ps.setInt(2, user.getUserId());
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    user.setDob(newDOB);
                    refreshProfileDisplay();
                    JOptionPane.showMessageDialog(this, "Date of birth updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update date of birth.");
                }
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }

    private void editHeight() {
        try {
            String input = JOptionPane.showInputDialog(this, "Enter new height (cm):", user.getHeight());
            if (input != null && !input.trim().isEmpty()) {
                int newHeight = Integer.parseInt(input);
                if (newHeight > 0) {
                    try (Connection con = ConnectionProvider.getCon()) {
                        String sql = "UPDATE users SET height_cm = ? WHERE idusers = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, newHeight);
                        ps.setInt(2, user.getUserId());
                        int updated = ps.executeUpdate();
                        if (updated > 0) {
                            user.setHeight(newHeight);
                            refreshProfileDisplay();
                            JOptionPane.showMessageDialog(this, "Height updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update height.");
                        }
                        ps.close();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a height greater than 0.");
                }
            }
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for height.");
        }
    }

    private void editWeight() {
        try {
            String input = JOptionPane.showInputDialog(this, "Enter new weight (kg):", user.getWeight());
            if (input != null && !input.trim().isEmpty()) {
                int newWeight = Integer.parseInt(input);
                if (newWeight > 0) {
                    try (Connection con = ConnectionProvider.getCon()) {
                        String sql = "UPDATE users SET weight_kg = ? WHERE idusers = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, newWeight);
                        ps.setInt(2, user.getUserId());
                        int updated = ps.executeUpdate();
                        if (updated > 0) {
                            user.setWeight(newWeight);
                            refreshProfileDisplay();
                            JOptionPane.showMessageDialog(this, "Weight updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update weight.");
                        }
                        ps.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a weight greater than 0.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for weight.");
        }
    }

    private void editSex() {
        String[] genders = {"Male", "Female", "Other"};
        JComboBox<String> genderBox = new JComboBox<>(genders);
        genderBox.setSelectedItem(user.getSex());

        int result = JOptionPane.showConfirmDialog(this, genderBox, "Select new gender:", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedGender = (String) genderBox.getSelectedItem();
            if (selectedGender != null && !selectedGender.equals(user.getSex())) {
                try (Connection con = ConnectionProvider.getCon()) {
                    String sql = "UPDATE users SET sex = ? WHERE idusers = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, selectedGender);
                    ps.setInt(2, user.getUserId());

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        user.setSex(selectedGender);
                        refreshProfileDisplay();
                        JOptionPane.showMessageDialog(this, "Gender updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update gender.");
                    }
                    ps.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        }
    }

    private void refreshProfileDisplay() {
        nameLabel.setText(user.getName());
        dobLabel.setText(user.getDob());
        heightLabel.setText(formatHeight(user.getHeight()));
        weightLabel.setText(formatWeight(user.getWeight()));
        sexLabel.setText(user.getSex());
    }

    private String formatHeight(int heightCm) {
        return user.isUsingImperial() ?
                String.format("%.1f in", heightCm / 2.54) :
                heightCm + " cm";
    }

    private String formatWeight(int weightKg) {
        return user.isUsingImperial() ?
                String.format("%.1f lbs", weightKg * 2.20462) :
                weightKg + " kg";
    }
}
