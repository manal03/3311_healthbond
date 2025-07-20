package UI;

import UI.Components.BackButton;
import database.ConnectionProvider;
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

    EditProfileUI(UserProfile user) {
        this.user = user;
        this.setLayout(null);
        this.setTitle("Health Tracker - Edit Profile");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(116, 209, 115));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, 800, 80);
        headerPanel.setBackground(new Color(85, 170, 85));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(60, 120, 60)));

        BackButton backButton = new BackButton(this);
        backButton.setBounds(15, 15, 90, 35);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(backButton);

        JLabel welcome = new JLabel("Edit Profile - " + user.getName());
        welcome.setBounds(120, 10, 500, 35);
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        headerPanel.add(welcome);

        JLabel subtitle = new JLabel("Update your personal information");
        subtitle.setBounds(120, 40, 400, 25);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 13));
        subtitle.setForeground(new Color(240, 248, 255));
        headerPanel.add(subtitle);

        this.add(headerPanel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(30, 120, 740, 520);
        contentPanel.setBackground(new Color(142, 182, 101));
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 60), 2),
                "Current Profile Information",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(60, 120, 60)
        ));
        createProfileRow(contentPanel, "Name:", user.getName(), 50, 0);
        createProfileRow(contentPanel, "Date of Birth:", user.getDob(), 120, 1);
        createProfileRow(contentPanel, "Height:", user.getHeight() + " cm", 190, 2);
        createProfileRow(contentPanel, "Weight:", user.getWeight() + " kg", 260, 3);
        createProfileRow(contentPanel, "Sex:", user.getSex(), 330, 4);

        this.add(contentPanel);

        this.setSize(800, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createProfileRow(JPanel parent, String labelText, String value, int yPos, int buttonIndex) {
        JLabel fieldLabel = new JLabel(labelText);
        fieldLabel.setBounds(30, yPos, 120, 30);
        fieldLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fieldLabel.setForeground(new Color(60, 120, 60));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setBounds(160, yPos, 300, 30);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        valueLabel.setForeground(Color.BLACK);

        JButton editBtn = new JButton("Edit");
        editBtn.setBounds(480, yPos, 80, 30);
        editBtn.setFocusable(false);
        editBtn.setFont(new Font("Arial", Font.BOLD, 12));
        editBtn.setBackground(new Color(85, 170, 85));
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        editBtn.addActionListener(this);

        // Store references to labels and buttons for updates
        switch (buttonIndex) {
            case 0:
                nameLabel = valueLabel;
                editNameBtn = editBtn;
                break;
            case 1:
                dobLabel = valueLabel;
                editDOBBtn = editBtn;
                break;
            case 2:
                heightLabel = valueLabel;
                editHeightBtn = editBtn;
                break;
            case 3:
                weightLabel = valueLabel;
                editWeightBtn = editBtn;
                break;
            case 4:
                sexLabel = valueLabel;
                editSexBtn = editBtn;
                break;
        }

        parent.add(fieldLabel);
        parent.add(valueLabel);
        parent.add(editBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editNameBtn) {
            editName();
        } else if (e.getSource() == editDOBBtn) {
            editDOB();
        } else if (e.getSource() == editHeightBtn) {
            editHeight();
        } else if (e.getSource() == editWeightBtn) {
            editWeight();
        } else if (e.getSource() == editSexBtn) {
            editSex();
        }
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
                    nameLabel.setText(newName);
                    user.setName(newName);
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
                    dobLabel.setText(newDOB);
                    user.setDob(newDOB);
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
                Integer newHeight = Integer.valueOf(input);
                if (newHeight > 0) {
                    try (Connection con = ConnectionProvider.getCon()) {
                        String sql = "UPDATE users SET height_cm = ? WHERE idusers = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, newHeight);
                        ps.setInt(2, user.getUserId());
                        int updated = ps.executeUpdate();
                        if (updated > 0) {
                            heightLabel.setText(newHeight + " cm");
                            user.setHeight(newHeight);
                            JOptionPane.showMessageDialog(this, "Height updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update height.");
                        }
                        ps.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid height greater than 0.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for height.");
        }
    }

    private void editWeight() {
        try {
            String input = JOptionPane.showInputDialog(this, "Enter new weight (kg):", user.getWeight());
            if (input != null && !input.trim().isEmpty()) {
                Integer newWeight = Integer.valueOf(input);
                if (newWeight > 0) {
                    try (Connection con = ConnectionProvider.getCon()) {
                        String sql = "UPDATE users SET weight_kg = ? WHERE idusers = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, newWeight);
                        ps.setInt(2, user.getUserId());
                        int updated = ps.executeUpdate();
                        if (updated > 0) {
                            weightLabel.setText(newWeight + " kg");
                            user.setWeight(newWeight);
                            JOptionPane.showMessageDialog(this, "Weight updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update weight.");
                        }
                        ps.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid weight greater than 0.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for weight.");
        }
    }

    private void editSex() {
        String[] genders = {"Male", "Female", "Other"};
        String currentSex = user.getSex();

        JComboBox<String> genderBox = new JComboBox<>(genders);
        genderBox.setSelectedItem(currentSex);

        int result = JOptionPane.showConfirmDialog(
                this,
                genderBox,
                "Select new gender:",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedGender = (String) genderBox.getSelectedItem();
            if (selectedGender != null && !selectedGender.equals(currentSex)) {
                try (Connection con = ConnectionProvider.getCon()) {
                    String sql = "UPDATE users SET sex = ? WHERE idusers = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, selectedGender);
                    ps.setInt(2, user.getUserId());

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        sexLabel.setText(selectedGender);
                        user.setSex(selectedGender);
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
}