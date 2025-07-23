package UI;

import models.UserProfile;
import UI.Components.BackButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private UserProfile user;
    private JButton mealEntryBtn;
    private JButton editProfileBtn;
    private JButton generateGoalBtn;
    private JButton journalViewBtn;
    private JButton substitutionBtn;
    private JButton nutrientVisualBtn;
    private JButton cfgAlignmentBtn;
    private JButton foodSwapVisualBtn;

    public MainUI(UserProfile user) {
        this.user = user;

        setTitle("HealthBond - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(116, 209, 115));
        setLayout(null);

        BackButton back = new BackButton(this);
        back.setBounds(20, 20, 80, 30);
        add(back);

        JLabel title = new JLabel("Welcome " + user.getName() + "!");
        title.setBounds(120, 30, 400, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Your health tracking dashboard");
        subtitle.setBounds(120, 60, 300, 20);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.WHITE);
        add(subtitle);

        JPanel content = new JPanel();
        content.setLayout(null);
        content.setBounds(100, 120, 600, 520);
        content.setBackground(new Color(142, 182, 101));
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        add(content);

        JLabel menuLabel = new JLabel("Navigation Menu");
        menuLabel.setBounds(20, 20, 150, 25);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 14));
        content.add(menuLabel);

        mealEntryBtn = new JButton("Enter Meal");
        mealEntryBtn.setBounds(200, 60, 200, 40);
        mealEntryBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        mealEntryBtn.setBackground(new Color(85, 170, 85));
        mealEntryBtn.setForeground(Color.WHITE);
        mealEntryBtn.setFocusable(false);
        mealEntryBtn.addActionListener(this);
        content.add(mealEntryBtn);

        editProfileBtn = new JButton("Edit Profile");
        editProfileBtn.setBounds(200, 110, 200, 40);
        editProfileBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        editProfileBtn.setBackground(new Color(85, 170, 85));
        editProfileBtn.setForeground(Color.WHITE);
        editProfileBtn.setFocusable(false);
        editProfileBtn.addActionListener(this);
        content.add(editProfileBtn);

        generateGoalBtn = new JButton("Generate Goal");
        generateGoalBtn.setBounds(200, 160, 200, 40);
        generateGoalBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        generateGoalBtn.setBackground(new Color(85, 170, 85));
        generateGoalBtn.setForeground(Color.WHITE);
        generateGoalBtn.setFocusable(false);
        generateGoalBtn.addActionListener(this);
        content.add(generateGoalBtn);

        journalViewBtn = new JButton("Journal View");
        journalViewBtn.setBounds(200, 210, 200, 40);
        journalViewBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        journalViewBtn.setBackground(new Color(85, 170, 85));
        journalViewBtn.setForeground(Color.WHITE);
        journalViewBtn.setFocusable(false);
        journalViewBtn.addActionListener(this);
        content.add(journalViewBtn);

        substitutionBtn = new JButton("Food Substitution");
        substitutionBtn.setBounds(200, 260, 200, 40);
        substitutionBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        substitutionBtn.setBackground(new Color(85, 170, 85));
        substitutionBtn.setForeground(Color.WHITE);
        substitutionBtn.setFocusable(false);
        substitutionBtn.addActionListener(this);
        content.add(substitutionBtn);

        nutrientVisualBtn = new JButton("Nutrient Visual");
        nutrientVisualBtn.setBounds(200, 310, 200, 40);
        nutrientVisualBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        nutrientVisualBtn.setBackground(new Color(85, 170, 85));
        nutrientVisualBtn.setForeground(Color.WHITE);
        nutrientVisualBtn.setFocusable(false);
        nutrientVisualBtn.addActionListener(this);
        content.add(nutrientVisualBtn);

        cfgAlignmentBtn = new JButton("CFG Alignment");
        cfgAlignmentBtn.setBounds(200, 360, 200, 40);
        cfgAlignmentBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        cfgAlignmentBtn.setBackground(new Color(85, 170, 85));
        cfgAlignmentBtn.setForeground(Color.WHITE);
        cfgAlignmentBtn.setFocusable(false);
        cfgAlignmentBtn.addActionListener(this);
        content.add(cfgAlignmentBtn);

        foodSwapVisualBtn = new JButton("Food Swap Visual");
        foodSwapVisualBtn.setBounds(200, 410, 200, 40);
        foodSwapVisualBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        foodSwapVisualBtn.setBackground(new Color(85, 170, 85));
        foodSwapVisualBtn.setForeground(Color.WHITE);
        foodSwapVisualBtn.setFocusable(false);
        foodSwapVisualBtn.addActionListener(this);
        content.add(foodSwapVisualBtn);

        JLabel infoLabel = new JLabel("User Info:");
        infoLabel.setBounds(50, 420, 100, 20);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        content.add(infoLabel);

        JLabel heightLabel = new JLabel("Height: " + user.getHeight() + " cm");
        heightLabel.setBounds(50, 440, 150, 20);
        heightLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        content.add(heightLabel);

        JLabel weightLabel = new JLabel("Weight: " + user.getWeight() + " kg");
        weightLabel.setBounds(50, 460, 150, 20);
        weightLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        content.add(weightLabel);

        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mealEntryBtn) {
            new MealLog(user);
            this.dispose();
        } else if (e.getSource() == editProfileBtn) {
            new EditProfileUI(user);
            this.dispose();
        } else if (e.getSource() == generateGoalBtn) {
            new GoalGeneratorUI(user);
            this.dispose();
        } else if (e.getSource() == journalViewBtn) {
            new JournalViewUI(user);
            this.dispose();
        } else if (e.getSource() == substitutionBtn) {
            new SubstitutionUI(user);
            this.dispose();
        } else if (e.getSource() == nutrientVisualBtn) {
            this.dispose();
        } else if (e.getSource() == cfgAlignmentBtn) {
           new CanadaFoodGuideUI(user);
            this.dispose();
        } else if (e.getSource() == foodSwapVisualBtn) {
            new FoodSwapVisualizationUI(user);
            this.dispose();
        }
    }
}