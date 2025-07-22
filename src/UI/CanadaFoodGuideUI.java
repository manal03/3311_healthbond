package UI;

import models.UserProfile;
import models.FoodGroupData;
import services.FoodGroupService;
import services.strategies.CFG2019AlignmentStrategy;
import services.strategies.CFG2007AlignmentStrategy;
import UI.Components.BackButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CanadaFoodGuideUI extends JFrame implements ActionListener {
    private UserProfile userAccount;
    private FoodGroupData nutritionInfo;
    private JButton detailsDisplayBtn;
    private JPanel userChartPanel, recommendedChartPanel;
    private JLabel alignmentResultLabel, currentGuideLabel;
    private JComboBox<String> durationSelector, versionSelector;
    private JPanel personalDataPanel, standardDataPanel;

    public CanadaFoodGuideUI(UserProfile user) {
        this.userAccount = user;
        this.nutritionInfo = new FoodGroupData();
        buildInterface();
        retrieveNutritionData();
    }

    private void buildInterface() {
        setTitle("Food Guide Thing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(116, 209, 115));
        setLayout(null);

        BackButton navigationBack = new BackButton(this);
        navigationBack.setBounds(20, 20, 80, 30);
        add(navigationBack);

        JLabel mainTitle = new JLabel("Canada Food Guide");
        mainTitle.setBounds(120, 30, 400, 30);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(mainTitle);

        JLabel descriptionText = new JLabel("your diet vs CFG");
        descriptionText.setBounds(120, 60, 400, 20);
        add(descriptionText);

        JPanel controlSection = new JPanel();
        controlSection.setLayout(null);
        controlSection.setBounds(50, 100, 800, 80);
        controlSection.setBackground(new Color(142, 182, 101));
        controlSection.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(controlSection);

        JLabel versionLabel = new JLabel("Version:");
        versionLabel.setBounds(20, 15, 60, 25);
        controlSection.add(versionLabel);

        String[] versions = {"CFG 2019", "CFG 2007"};
        versionSelector = new JComboBox<>(versions);
        versionSelector.setBounds(80, 15, 100, 25);
        versionSelector.addActionListener(this);
        controlSection.add(versionSelector);

        JLabel durationLabel = new JLabel("Days:");
        durationLabel.setBounds(200, 15, 40, 25);
        controlSection.add(durationLabel);

        String[] durations = {"Last 7 Days", "Last 30 Days", "Last 90 Days"};
        durationSelector = new JComboBox<>(durations);
        durationSelector.setBounds(240, 15, 120, 25);
        durationSelector.addActionListener(this);
        controlSection.add(durationSelector);

        detailsDisplayBtn = new JButton("Details");
        detailsDisplayBtn.setBounds(380, 15, 80, 25);
        detailsDisplayBtn.addActionListener(this);
        controlSection.add(detailsDisplayBtn);

        currentGuideLabel = new JLabel("Guide: " + FoodGroupService.getGuidelineName());
        currentGuideLabel.setBounds(20, 45, 300, 20);
        controlSection.add(currentGuideLabel);

        alignmentResultLabel = new JLabel("Score: ?");
        alignmentResultLabel.setBounds(500, 45, 200, 20);
        controlSection.add(alignmentResultLabel);

        // User plate
        personalDataPanel = new JPanel();
        personalDataPanel.setLayout(null);
        personalDataPanel.setBounds(50, 200, 400, 400);
        personalDataPanel.setBackground(Color.WHITE);
        personalDataPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(personalDataPanel);

        JLabel personalHeader = new JLabel("Your Plate");
        personalHeader.setBounds(150, 10, 100, 20);
        personalDataPanel.add(personalHeader);

        userChartPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g, nutritionInfo, false);
            }
        };
        userChartPanel.setBounds(50, 40, 300, 300);
        userChartPanel.setBackground(Color.WHITE);
        userChartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        personalDataPanel.add(userChartPanel);

        // CFG plate
        standardDataPanel = new JPanel();
        standardDataPanel.setLayout(null);
        standardDataPanel.setBounds(470, 200, 400, 400);
        standardDataPanel.setBackground(Color.WHITE);
        standardDataPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(standardDataPanel);

        JLabel standardHeader = new JLabel("CFG Plate");
        standardHeader.setBounds(150, 10, 100, 20);
        standardDataPanel.add(standardHeader);

        recommendedChartPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g, FoodGroupService.getRecommendedProportions(), true);
            }
        };
        recommendedChartPanel.setBounds(50, 40, 300, 300);
        recommendedChartPanel.setBackground(Color.WHITE);
        recommendedChartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        standardDataPanel.add(recommendedChartPanel);

        // legends
        addLegendWithPercent(personalDataPanel, nutritionInfo);
        addLegendWithPercent(standardDataPanel, FoodGroupService.getRecommendedProportions());

        setVisible(true);
    }

    private void drawChart(Graphics g, FoodGroupData data, boolean isStandard) {
        Graphics2D g2d = (Graphics2D) g;
        int w = 300, h = 300;
        int cx = w/2, cy = h/2, r = 120;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(cx-r, cy-r, r*2, r*2);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(cx-r, cy-r, r*2, r*2);

        if(isStandard) {
            String ver = (String) versionSelector.getSelectedItem();
            if("CFG 2007".equals(ver)) {
                g2d.setColor(Color.GREEN);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 0, 126);
                g2d.setColor(Color.ORANGE);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 126, 54);
                g2d.setColor(Color.yellow);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 180, 90);
                g2d.setColor(Color.RED);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 270, 72);
                g2d.setColor(Color.BLUE);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 342, 18);
            } else {
                g2d.setColor(Color.GREEN);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 0, 180);
                g2d.setColor(Color.yellow);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 180, 90);
                g2d.setColor(Color.RED);
                g2d.fillArc(cx-r, cy-r, r*2, r*2, 270, 90);
            }
        } else {
            double total = data.getTotalServings();
            if(total > 0) {
                double angle = 0;
                Color[] colors = {Color.GREEN, Color.ORANGE, Color.yellow, Color.RED, Color.BLUE};
                double[] amounts = {data.getVegetables(), data.getFruits(), data.getGrains(), data.getProtein(), data.getDairy()};

                for(int i = 0; i < 5; i++) {
                    if(amounts[i] > 0) {
                        double deg = (amounts[i]/total) * 360;
                        g2d.setColor(colors[i]);
                        g2d.fillArc(cx-r, cy-r, r*2, r*2, (int)angle, (int)deg);
                        angle += deg;
                    }
                }
            }
        }
    }

    private void addLegendWithPercent(JPanel panel, FoodGroupData data) {
        Component[] components = panel.getComponents();
        for(int i = components.length - 1; i >= 0; i--) {
            Component c = components[i];
            if(c instanceof JLabel && (((JLabel)c).getText().contains("%") || ((JLabel)c).getText().equals("■"))) {
                panel.remove(c);
            }
        }

        String[] labels = {"Veg", "Fruit", "Grain", "Protein", "Dairy"};
        Color[] colors = {Color.GREEN, Color.ORANGE, Color.yellow, Color.RED, Color.BLUE};
        double[] percentages = {data.getVegetablesPercentage(), data.getFruitsPercentage(),
                data.getGrainsPercentage(), data.getProteinPercentage(), data.getDairyPercentage()};

        for(int i = 0; i < 5; i++) {
            JLabel colorBox = new JLabel("■");
            colorBox.setForeground(colors[i]);
            colorBox.setBounds(10 + i*75, 350, 20, 20);
            panel.add(colorBox);

            JLabel label = new JLabel(labels[i] + ": " + (int)percentages[i] + "%");
            label.setBounds(25 + i*75, 350, 65, 20);
            label.setFont(new Font("Arial", Font.PLAIN, 9));
            panel.add(label);
        }

        panel.repaint();
    }

    private void retrieveNutritionData() {
        String duration = (String) durationSelector.getSelectedItem();
        int days = duration.contains("7") ? 7 : duration.contains("30") ? 30 : 90;
        nutritionInfo = FoodGroupService.getUserFoodGroupData(userAccount, days);
        refreshStuff();
    }

    private void refreshStuff() {
        userChartPanel.repaint();
        recommendedChartPanel.repaint();

        // Update legends with new data
        addLegendWithPercent(personalDataPanel, nutritionInfo);
        addLegendWithPercent(standardDataPanel, FoodGroupService.getRecommendedProportions());

        double score = 0;
        if(nutritionInfo.getTotalServings() > 0) {
            score = FoodGroupService.calculateAlignmentScore(nutritionInfo, userAccount);
        }
        alignmentResultLabel.setText("Score: " + (int)score + "%");

        if(score >= 80) alignmentResultLabel.setForeground(Color.GREEN);
        else if(score >= 60) alignmentResultLabel.setForeground(Color.ORANGE);
        else alignmentResultLabel.setForeground(Color.RED);
    }

    private void configureCFGApproach() {
        String ver = (String) versionSelector.getSelectedItem();
        if("CFG 2007".equals(ver)) {
            FoodGroupService.setAlignmentStrategy(new CFG2007AlignmentStrategy());
        } else {
            FoodGroupService.setAlignmentStrategy(new CFG2019AlignmentStrategy());
        }
        currentGuideLabel.setText("Guide: " + FoodGroupService.getGuidelineName());

        // Update the CFG chart legend when version changes
        addLegendWithPercent(standardDataPanel, FoodGroupService.getRecommendedProportions());
    }

    private void showDetails() {
        JDialog d = new JDialog(this, "Guide Info", true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        JTextArea ta = new JTextArea(FoodGroupService.getRecommendationsText());
        ta.setEditable(false);
        d.add(new JScrollPane(ta));
        JButton close = new JButton("OK");
        close.addActionListener(e -> d.dispose());
        JPanel p = new JPanel();
        p.add(close);
        d.add(p, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == durationSelector) {
            retrieveNutritionData();
        } else if(e.getSource() == versionSelector) {
            configureCFGApproach();
            refreshStuff();
        } else if(e.getSource() == detailsDisplayBtn) {
            showDetails();
        }
    }
}