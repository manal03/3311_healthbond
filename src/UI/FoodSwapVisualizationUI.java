package UI;

import models.UserProfile;
import UI.Components.BackButton;
import UI.template.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class FoodSwapVisualizationUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private UserProfile user;
    private JComboBox<String> visualizationTypeCombo;
    private JComboBox<String> nutrientCombo;
    private JComboBox<String> timePeriodCombo;
    private JButton generateBtn;
    private JPanel visualizationPanel;
    private JCheckBox cfgAdherenceCheck;

    private AbstractFoodSwapVisualizer currentVisualizer;

    public FoodSwapVisualizationUI(UserProfile user) {
        this.user = user;

        setTitle("Food Swap Visualization");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(116, 209, 115));
        setLayout(null);

        BackButton back = new BackButton(this);
        back.setBounds(20, 20, 80, 30);
        add(back);

        JLabel title = new JLabel("Food Swap Visualization");
        title.setBounds(120, 20, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBounds(20, 70, 300, 250);
        controlPanel.setBackground(new Color(142, 182, 101));
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel typeLabel = new JLabel("Chart Type:");
        typeLabel.setBounds(20, 20, 100, 25);
        controlPanel.add(typeLabel);

        visualizationTypeCombo = new JComboBox<>(new String[]{"Bar Chart", "Line Chart"});
        visualizationTypeCombo.setBounds(130, 20, 150, 25);
        controlPanel.add(visualizationTypeCombo);

        JLabel nutrientLabel = new JLabel("Nutrient:");
        nutrientLabel.setBounds(20, 60, 100, 25);
        controlPanel.add(nutrientLabel);

        nutrientCombo = new JComboBox<>(new String[]{
                "Calories", "Protein", "Carbohydrates", "Fat",
                "Fiber", "Sugar", "Sodium", "Cholesterol"
        });
        nutrientCombo.setBounds(130, 60, 150, 25);
        controlPanel.add(nutrientCombo);

        JLabel periodLabel = new JLabel("Time Period:");
        periodLabel.setBounds(20, 100, 100, 25);
        controlPanel.add(periodLabel);

        timePeriodCombo = new JComboBox<>(new String[]{
                "Last 7 days", "Last 14 days", "Last 30 days", "Last 3 months"
        });
        timePeriodCombo.setBounds(130, 100, 150, 25);
        controlPanel.add(timePeriodCombo);

        cfgAdherenceCheck = new JCheckBox("Show CFG Data");
        cfgAdherenceCheck.setBounds(20, 140, 200, 25);
        cfgAdherenceCheck.setBackground(new Color(142, 182, 101));
        controlPanel.add(cfgAdherenceCheck);

        generateBtn = new JButton("Generate");
        generateBtn.setBounds(20, 180, 260, 40);
        generateBtn.setBackground(new Color(85, 170, 85));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.addActionListener(this);
        controlPanel.add(generateBtn);

        add(controlPanel);

        visualizationPanel = new JPanel();
        visualizationPanel.setBackground(Color.WHITE);
        visualizationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        visualizationPanel.setBounds(340, 70, 420, 480);
        visualizationPanel.setLayout(new BorderLayout());

        JLabel placeholderLabel = new JLabel("Click Generate to view chart", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        visualizationPanel.add(placeholderLabel, BorderLayout.CENTER);

        add(visualizationPanel);

        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateBtn) {
            generateVisualization();
        }
    }

    private void generateVisualization() {
        String visualType = (String) visualizationTypeCombo.getSelectedItem();
        String nutrient = (String) nutrientCombo.getSelectedItem();
        String timePeriod = (String) timePeriodCombo.getSelectedItem();
        boolean includeCFG = cfgAdherenceCheck.isSelected();

        switch (visualType) {
            case "Bar Chart":
                currentVisualizer = new BarChartFoodSwapVisualizer(user, nutrient, timePeriod, includeCFG);
                break;
            case "Line Chart":
                currentVisualizer = new LineChartFoodSwapVisualizer(user, nutrient, timePeriod, includeCFG);
                break;
        }

        // Clear visualization, generate new one
        visualizationPanel.removeAll();
        JPanel visualization = currentVisualizer.generateVisualization();
        visualizationPanel.add(visualization, BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }
}