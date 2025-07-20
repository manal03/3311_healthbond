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
        this.setTitle("Health Tracker - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(116, 209, 115));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, 800, 120);
        headerPanel.setBackground(new Color(85, 170, 85));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(60, 120, 60)));

        JLabel welcomeLabel = new JLabel("Welcome to Health Tracker");
        welcomeLabel.setBounds(50, 20, 700, 50);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(welcomeLabel);

        JLabel subtitleLabel = new JLabel("Track your meals and manage your health goals");
        subtitleLabel.setBounds(50, 70, 700, 30);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(240, 248, 255));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel);

        this.add(headerPanel);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBounds(50, 160, 300, 300);
        leftPanel.setBackground(new Color(142, 182, 101));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 60), 2),
                "Get Started",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(60, 120, 60)
        ));

        JLabel newUserLabel = new JLabel("New User?");
        newUserLabel.setBounds(30, 50, 240, 30);
        newUserLabel.setFont(new Font("Arial", Font.BOLD, 18));
        newUserLabel.setForeground(new Color(60, 120, 60));
        newUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(newUserLabel);

        JLabel instructionLabel = new JLabel("<html><div style='text-align: center;'>Create your profile to start<br>tracking your health journey</div></html>");
        instructionLabel.setBounds(30, 90, 240, 60);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(instructionLabel);

        createProfileBtn = new JButton("Create A Profile");
        createProfileBtn.setBounds(50, 180, 200, 50);
        createProfileBtn.setFocusable(false);
        createProfileBtn.setFont(new Font("Arial", Font.BOLD, 14));
        createProfileBtn.setBackground(new Color(85, 170, 85));
        createProfileBtn.setForeground(Color.WHITE);
        createProfileBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        createProfileBtn.addActionListener(this);
        leftPanel.add(createProfileBtn);

        this.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBounds(400, 160, 350, 400);
        rightPanel.setBackground(new Color(142, 182, 101));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 60), 2),
                "Existing Profiles",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(60, 120, 60)
        ));

        JLabel selectLabel = new JLabel("Double-click to select a profile:");
        selectLabel.setBounds(20, 40, 310, 25);
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selectLabel.setForeground(new Color(60, 120, 60));
        rightPanel.add(selectLabel);

        listModel = new DefaultListModel<>();
        profileList = new JList<>(listModel);
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.setFont(new Font("Arial", Font.PLAIN, 14));
        profileList.setBackground(Color.WHITE);
        profileList.setSelectionBackground(new Color(85, 170, 85));
        profileList.setSelectionForeground(Color.WHITE);
        profileList.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

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
        scrollPane.setBounds(20, 75, 310, 300);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createRaisedBevelBorder());
        rightPanel.add(scrollPane);

        this.add(rightPanel);

        JPanel footerPanel = new JPanel();
        footerPanel.setBounds(0, 600, 800, 50);
        footerPanel.setBackground(new Color(85, 170, 85));
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(60, 120, 60)));

        JLabel footerLabel = new JLabel("Health Tracker v1.0 - Your Personal Health Assistant");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(240, 248, 255));
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.add(footerLabel, BorderLayout.CENTER);

        this.add(footerPanel);

        loadUserProfiles();

        this.setSize(800, 700);
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
                listModel.addElement("No profiles found - Create one to get started!");
            }

            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading profiles: " + e.getMessage());
        }
    }

    private void openMainUIForUser(String name) {
        if (name.equals("No profiles found - Create one to get started!")) {
            JOptionPane.showMessageDialog(this, "Please create a profile first.");
            return;
        }

        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT * FROM users WHERE name = ?";
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
            JOptionPane.showMessageDialog(this, "Error opening profile: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createProfileBtn) {
            this.dispose();
            CreateProfileUI newProfile = new CreateProfileUI();
            System.out.println("Profile created");
        }
    }
}