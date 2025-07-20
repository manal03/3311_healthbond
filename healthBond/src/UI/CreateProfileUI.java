package UI;

import UI.Components.BackButton;
import database.ConnectionProvider;
import models.UserProfile;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateProfileUI extends JFrame implements ActionListener {
    private JTextField nameField, genderField, heightField, weightField, dobField;
    private JComboBox<String> unitField;
    private JButton submitBtn;

    public CreateProfileUI() {
        // Frame setup
        setTitle("Health Tracker - Create Profile");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(116, 209, 115));
        setLayout(null);

        // Header Panel
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 800, 80);
        header.setBackground(new Color(85, 170, 85));
        header.setBorder(new MatteBorder(0, 0, 3, 0, new Color(60, 120, 60)));

        BackButton back = new BackButton(this);
        back.setBounds(15, 15, 90, 35);
        back.setFont(new Font("Arial", Font.BOLD, 12));
        header.add(back);

        JLabel title = new JLabel("Create Your Profile");
        title.setBounds(120, 15, 400, 35);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);

        JLabel subtitle = new JLabel("Fill in your details to get started");
        subtitle.setBounds(120, 45, 400, 25);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitle.setForeground(new Color(240, 248, 255));
        header.add(subtitle);

        add(header);

        // Form Panel
        JPanel form = new JPanel(null);
        form.setBounds(100, 120, 600, 480);
        form.setBackground(new Color(142, 182, 101));
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 60), 2),
                "Profile Details", 0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(60, 120, 60)
        ));

        int labelX = 50, fieldX = 220, y = 40, gapY = 60;
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Name
        JLabel nameLbl = new JLabel("Name:");
        nameLbl.setFont(labelFont);
        nameLbl.setBounds(labelX, y, 150, 25);
        form.add(nameLbl);
        nameField = new JTextField();
        nameField.setFont(fieldFont);
        nameField.setBounds(fieldX, y, 300, 30);
        form.add(nameField);

        // Gender
        y += gapY;
        JLabel genderLbl = new JLabel("Gender:");
        genderLbl.setFont(labelFont);
        genderLbl.setBounds(labelX, y, 150, 25);
        form.add(genderLbl);
        genderField = new JTextField();
        genderField.setFont(fieldFont);
        genderField.setBounds(fieldX, y, 100, 30);
        form.add(genderField);

        // Height
        y += gapY;
        JLabel heightLbl = new JLabel("Height (cm):");
        heightLbl.setFont(labelFont);
        heightLbl.setBounds(labelX, y, 150, 25);
        form.add(heightLbl);
        heightField = new JTextField();
        heightField.setFont(fieldFont);
        heightField.setBounds(fieldX, y, 100, 30);
        form.add(heightField);

        // Weight
        y += gapY;
        JLabel weightLbl = new JLabel("Weight (kg):");
        weightLbl.setFont(labelFont);
        weightLbl.setBounds(labelX, y, 150, 25);
        form.add(weightLbl);
        weightField = new JTextField();
        weightField.setFont(fieldFont);
        weightField.setBounds(fieldX, y, 100, 30);
        form.add(weightField);

        // DOB
        y += gapY;
        JLabel dobLbl = new JLabel("Date of Birth:");
        dobLbl.setFont(labelFont);
        dobLbl.setBounds(labelX, y, 150, 25);
        form.add(dobLbl);
        dobField = new JTextField("YYYY-MM-DD");
        dobField.setFont(fieldFont);
        dobField.setBounds(fieldX, y, 150, 30);
        form.add(dobField);

        // Unit
        y += gapY;
        JLabel unitLbl = new JLabel("Unit:");
        unitLbl.setFont(labelFont);
        unitLbl.setBounds(labelX, y, 150, 25);
        form.add(unitLbl);
        unitField = new JComboBox<>(new String[]{"metric", "imperial"});
        unitField.setFont(fieldFont);
        unitField.setBounds(fieldX, y, 120, 30);
        form.add(unitField);

        // Submit Button (moved below fields)
        submitBtn = new JButton("Submit Profile");
        submitBtn.setBounds(220, y + 80, 160, 40);
        submitBtn.setFocusable(false);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setBackground(new Color(85, 170, 85));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this);
        form.add(submitBtn);

        add(form);

        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            try (Connection con = ConnectionProvider.getCon()) {
                String name = nameField.getText().trim();
                String gender = genderField.getText().trim();
                int height = Integer.parseInt(heightField.getText().trim());
                int weight = Integer.parseInt(weightField.getText().trim());
                String dob = dobField.getText().trim();
                String unit = (String) unitField.getSelectedItem();

                String sql = "INSERT INTO users(sex, dateofbirth, height_cm, weight_kg, name, unit) VALUES(?,?,?,?,?,?)";
                PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, gender);
                stmt.setString(2, dob);
                stmt.setInt(3, height);
                stmt.setInt(4, weight);
                stmt.setString(5, name);
                stmt.setString(6, unit);
                int inserted = stmt.executeUpdate();

                if (inserted > 0) {
                    ResultSet keys = stmt.getGeneratedKeys();
                    if (keys.next()) {
                        int userId = keys.getInt(1);
                        JOptionPane.showMessageDialog(this, "Profile created successfully!");
                        UserProfile user = new UserProfile(userId, dob, weight, height, gender, name, unit);
                        new MainUI(user);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Profile creation failed.");
                }

                stmt.close();
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
