package UI;

import models.UserProfile;
import UI.Components.BackButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends JFrame implements ActionListener {
    private UserProfile user;
    JButton mealEntryBtn;
    JButton editProfileBtn;
    JPanel mealEntryPanel;
    JLabel mealTypeLabel;

    public MainUI(UserProfile user){
        this.user = user;
        this.setLayout(null);
        this.setTitle("Health Tracker - Main Menu");
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

        JLabel welcome = new JLabel("Welcome, " + user.getName() + "!");
        welcome.setBounds(120, 10, 500, 35);
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        headerPanel.add(welcome);


        JLabel subtitle = new JLabel("Choose an option to get started");
        subtitle.setBounds(120, 40, 400, 25);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 13));
        subtitle.setForeground(new Color(240, 248, 255));
        headerPanel.add(subtitle);


        this.add(headerPanel);


        mealEntryBtn = new JButton("Enter Meal");
        mealEntryBtn.setBounds(30, 120, 150, 60);
        mealEntryBtn.setFocusable(false);
        mealEntryBtn.addActionListener(this);

        editProfileBtn = new JButton("Edit Profile");
        editProfileBtn.setBounds(30, 200, 150, 60);
        editProfileBtn.setFocusable(false);
        editProfileBtn.addActionListener(this);

        this.add(mealEntryBtn);
        this.add(editProfileBtn);

        this.setSize(800, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mealEntryBtn){
            showMealEntryForm();
        }else if(e.getSource() == editProfileBtn){
            this.dispose();
            EditProfileUI editProfile = new EditProfileUI(user);
        }
    }

    private void addIngredientRow(JPanel container) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setPreferredSize(new Dimension(480, 40));

        JTextField ingredientField = new JTextField(15);
        JTextField quantityField = new JTextField(7);

        row.add(new JLabel("Ingredient:"));
        row.add(ingredientField);
        row.add(new JLabel("Quantity:"));
        row.add(quantityField);

        container.add(row);
        container.revalidate();
        container.repaint();
    }

    private void showMealEntryForm() {
        if(mealEntryPanel != null){
            this.remove(mealEntryPanel);
        }

        mealEntryPanel = new JPanel();
        mealEntryPanel.setLayout(null);
        mealEntryPanel.setVisible(true);
        mealEntryPanel.setBounds(200, 20, 570, 620);
        mealEntryPanel.setBackground(new Color(142, 182, 101));
        mealEntryPanel.setBorder(BorderFactory.createTitledBorder("Meal Entry Form"));

        mealTypeLabel = new JLabel("Meal Type:");
        mealTypeLabel.setBounds(20, 30, 100, 30);
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        JComboBox<String> mealTypeBox = new JComboBox<>(mealTypes);
        mealTypeBox.setBounds(20, 65, 120, 30);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(20, 110, 100, 30);
        JTextField dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(20, 145, 150, 30);

        JLabel ingredientsLabel = new JLabel("Ingredients:");
        ingredientsLabel.setBounds(20, 190, 100, 30);

        JPanel ingredientContainer = new JPanel();
        ingredientContainer.setLayout(new BoxLayout(ingredientContainer, BoxLayout.Y_AXIS));
        ingredientContainer.setBackground(new Color(142, 182, 101));

        JScrollPane scrollPane = new JScrollPane(ingredientContainer);
        scrollPane.setBounds(20, 225, 520, 250);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JButton addRowButton = new JButton("Add Ingredient");
        addRowButton.setBounds(20, 490, 140, 30);

        JButton submitButton = new JButton("Submit Meal");
        submitButton.setBounds(180, 490, 120, 30);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(320, 490, 100, 30);
        cancelButton.addActionListener(e -> {
            this.remove(mealEntryPanel);
            this.repaint();
            this.revalidate();
        });

        addIngredientRow(ingredientContainer);
        addRowButton.addActionListener(e -> addIngredientRow(ingredientContainer));

        mealEntryPanel.add(mealTypeLabel);
        mealEntryPanel.add(mealTypeBox);
        mealEntryPanel.add(dateLabel);
        mealEntryPanel.add(dateField);
        mealEntryPanel.add(ingredientsLabel);
        mealEntryPanel.add(scrollPane);
        mealEntryPanel.add(addRowButton);
        mealEntryPanel.add(submitButton);
        mealEntryPanel.add(cancelButton);

        this.add(mealEntryPanel);
        this.repaint();
        this.revalidate();
    }
}