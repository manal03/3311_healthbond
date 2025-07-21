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
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateProfileUI extends JFrame implements ActionListener {
    private JTextField nameField, genderField, heightField, weightField, dobField;
    private JComboBox<String> unitField;
    private JButton submitBtn;

    public CreateProfileUI() {
        setTitle("Health Tracker - Create Profile");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(116, 209, 115));
        setLayout(null);

        BackButton back = new BackButton(this);
        back.setBounds(20, 20, 80, 30);
        add(back);

        JLabel title = new JLabel("Create Your Profile");
        title.setBounds(120, 30, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Fill in your details to get started");
        subtitle.setBounds(120, 60, 300, 20);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.WHITE);
        add(subtitle);

        JPanel form = new JPanel();
        form.setLayout(null);
        form.setBounds(150, 120, 500, 400);
        form.setBackground(new Color(142, 182, 101));
        form.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        add(form);

        JLabel formTitle = new JLabel("Profile Details");
        formTitle.setBounds(20, 20, 150, 25);
        formTitle.setFont(new Font("Arial", Font.BOLD, 14));
        form.add(formTitle);

        int labelX = 30, fieldX = 150, y = 60, gapY = 45;

        JLabel nameLbl = new JLabel("Name:");
        nameLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        nameLbl.setBounds(labelX, y, 100, 25);
        form.add(nameLbl);
        nameField = new JTextField();
        nameField.setBounds(fieldX, y, 200, 25);
        form.add(nameField);

        y += gapY;
        JLabel genderLbl = new JLabel("Gender:");
        genderLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        genderLbl.setBounds(labelX, y, 100, 25);
        form.add(genderLbl);
        genderField = new JTextField();
        genderField.setBounds(fieldX, y, 100, 25);
        form.add(genderField);

        y += gapY;
        JLabel heightLbl = new JLabel("Height (cm):");
        heightLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        heightLbl.setBounds(labelX, y, 100, 25);
        form.add(heightLbl);
        heightField = new JTextField();
        heightField.setBounds(fieldX, y, 100, 25);
        form.add(heightField);

        y += gapY;
        JLabel weightLbl = new JLabel("Weight (kg):");
        weightLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        weightLbl.setBounds(labelX, y, 100, 25);
        form.add(weightLbl);
        weightField = new JTextField();
        weightField.setBounds(fieldX, y, 100, 25);
        form.add(weightField);

        y += gapY;
        JLabel dobLbl = new JLabel("Date of Birth:");
        dobLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        dobLbl.setBounds(labelX, y, 100, 25);
        form.add(dobLbl);
        dobField = new JTextField("YYYY-MM-DD");
        dobField.setBounds(fieldX, y, 120, 25);
        form.add(dobField);

        y += gapY;
        JLabel unitLbl = new JLabel("Unit:");
        unitLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        unitLbl.setBounds(labelX, y, 100, 25);
        form.add(unitLbl);
        unitField = new JComboBox<>(new String[]{"metric", "imperial"});
        unitField.setBounds(fieldX, y, 100, 25);
        form.add(unitField);

        submitBtn = new JButton("Submit Profile");
        submitBtn.setBounds(180, y + 50, 140, 35);
        submitBtn.setFocusable(false);
        submitBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        submitBtn.setBackground(new Color(85, 170, 85));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this);
        form.add(submitBtn);

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