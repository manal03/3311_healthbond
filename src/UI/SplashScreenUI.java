package UI;

import database.ConnectionProvider;
import models.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SplashScreenUI extends JFrame implements ActionListener {
    private JButton createProfileBtn;
    private JList<String> profileList;
    private DefaultListModel<String> listModel;

    public SplashScreenUI() {
        this.setLayout(null);
        this.setTitle("Health Tracker");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(116, 209, 115));

        JLabel welcomeLabel = new JLabel("HealthBond");
        welcomeLabel.setBounds(300, 30, 200, 40);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        this.add(welcomeLabel);

        JLabel subtitleLabel = new JLabel("Track your health");
        subtitleLabel.setBounds(330, 70, 140, 25);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        this.add(subtitleLabel);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBounds(50, 120, 300, 250);
        leftPanel.setBackground(new Color(142, 182, 101));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel newUserLabel = new JLabel("New User");
        newUserLabel.setBounds(110, 20, 100, 25);
        newUserLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leftPanel.add(newUserLabel);

        JLabel instructionLabel = new JLabel("Create your profile to start");
        instructionLabel.setBounds(70, 60, 200, 25);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        leftPanel.add(instructionLabel);

        createProfileBtn = new JButton("Create Profile");
        createProfileBtn.setBounds(80, 120, 140, 40);
        createProfileBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        createProfileBtn.setBackground(new Color(85, 170, 85));
        createProfileBtn.setForeground(Color.WHITE);
        createProfileBtn.addActionListener(this);
        leftPanel.add(createProfileBtn);

        this.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBounds(400, 120, 300, 350);
        rightPanel.setBackground(new Color(142, 182, 101));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel selectLabel = new JLabel("Select Profile:");
        selectLabel.setBounds(20, 20, 120, 25);
        selectLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(selectLabel);

        JLabel doubleClickLabel = new JLabel("(Double click to open)");
        doubleClickLabel.setBounds(20, 45, 150, 20);
        doubleClickLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        rightPanel.add(doubleClickLabel);

        listModel = new DefaultListModel<>();
        profileList = new JList<>(listModel);
        profileList.setFont(new Font("Arial", Font.PLAIN, 12));
        profileList.setBackground(Color.WHITE);
        profileList.setSelectionBackground(new Color(85, 170, 85));

        profileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedProfile = profileList.getSelectedValue();
                    if (selectedProfile != null) {
                        openMainUIForUser(selectedProfile);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(profileList);
        scrollPane.setBounds(20, 75, 260, 250);
        rightPanel.add(scrollPane);

        this.add(rightPanel);

        loadUserProfiles();

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    private void loadUserProfiles() {
        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT name FROM users";
            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                listModel.addElement(name);
            }

            if (listModel.isEmpty()) {
                listModel.addElement("No profiles found");
            }

            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading profiles: " + e.getMessage());
        }
    }

    private void openMainUIForUser(String name) {
        if (name.equals("No profiles found")) {
            JOptionPane.showMessageDialog(this, "Create a profile first.");
            return;
        }

        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT * FROM users WHERE name = ?";
            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("idusers");
                String gender = rs.getString("sex");
                String dob = rs.getString("dateofbirth");
                int height = rs.getInt("height_cm");
                int weight = rs.getInt("weight_kg");
                String unit = rs.getString("unit");

                UserProfile user = new UserProfile(id, dob, weight, height, gender, name, unit);
                new MainUI(user);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createProfileBtn) {
            this.dispose();
            CreateProfileUI newProfile = new CreateProfileUI();
        }
    }
}