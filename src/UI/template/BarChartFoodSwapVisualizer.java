package UI.template;

import models.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Implementation of food swap visualizer using bar charts
 */
public class BarChartFoodSwapVisualizer extends AbstractFoodSwapVisualizer {

    public BarChartFoodSwapVisualizer(UserProfile user, String nutrient, String timePeriod, boolean includeCFG) {
        super(user, nutrient, timePeriod, includeCFG);
    }

    @Override
    protected void drawVisualization(JPanel panel) {
        if (beforeSwapData.isEmpty() || allValuesZero(beforeSwapData, afterSwapData)) {
            JLabel noDataLabel = new JLabel("No substitution data available for selected period", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(noDataLabel, BorderLayout.CENTER);
            return;
        }

        if (includeCFGAdherence && !cfgBeforeData.isEmpty()) {
            JPanel splitPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            splitPanel.setBackground(Color.WHITE);

            JPanel nutrientChart = new BarChartPanel(beforeSwapData, afterSwapData,
                    selectedNutrient + " Comparison");
            splitPanel.add(nutrientChart);

            JPanel cfgChart = new CFGBarChartPanel(cfgBeforeData, cfgAfterData);
            splitPanel.add(cfgChart);

            panel.add(splitPanel, BorderLayout.CENTER);
        } else {
            JPanel nutrientChart = new BarChartPanel(beforeSwapData, afterSwapData,
                    selectedNutrient + " Comparison");
            panel.add(nutrientChart, BorderLayout.CENTER);
        }
    }

    private boolean allValuesZero(Map<LocalDate, Double> map1, Map<LocalDate, Double> map2) {
        for (Double value : map1.values()) {
            if (value > 0) return false;
        }
        for (Double value : map2.values()) {
            if (value > 0) return false;
        }
        return true;
    }

    /**
     * Inner class for rendering bar charts
     */
    private class BarChartPanel extends JPanel {
        private Map<LocalDate, Double> beforeData;
        private Map<LocalDate, Double> afterData;
        private String title;

        public BarChartPanel(Map<LocalDate, Double> beforeData, Map<LocalDate, Double> afterData, String title) {
            this.beforeData = beforeData;
            this.afterData = afterData;
            this.title = title;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            int margin = 40;
            int chartWidth = width - 2 * margin;
            int chartHeight = height - 2 * margin;

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString(title, margin, 20);

            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
            g2d.drawLine(margin, margin, margin, height - margin); // Y-axis

            // Calculate max value for scaling
            double maxValue = 0;
            for (Double value : beforeData.values()) {
                maxValue = Math.max(maxValue, value);
            }
            for (Double value : afterData.values()) {
                maxValue = Math.max(maxValue, value);
            }

            if (maxValue == 0) return;

            // Draw bars
            int numDates = beforeData.size();
            if (numDates == 0) return;

            int barGroupWidth = chartWidth / numDates;
            int barWidth = barGroupWidth / 3;
            int spacing = barWidth / 2;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

            int index = 0;
            for (LocalDate date : beforeData.keySet()) {
                int x = margin + index * barGroupWidth + spacing;

                // Before bar
                double beforeValue = beforeData.getOrDefault(date, 0.0);
                int beforeHeight = (int) ((beforeValue / maxValue) * chartHeight);
                g2d.setColor(new Color(255, 100, 100));
                g2d.fillRect(x, height - margin - beforeHeight, barWidth, beforeHeight);

                // After bar
                double afterValue = afterData.getOrDefault(date, 0.0);
                int afterHeight = (int) ((afterValue / maxValue) * chartHeight);
                g2d.setColor(new Color(100, 255, 100));
                g2d.fillRect(x + barWidth, height - margin - afterHeight, barWidth, afterHeight);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String dateStr = date.format(formatter);
                g2d.drawString(dateStr, x + barWidth/2, height - margin + 15);

                index++;
            }

            // Draw Y-axis labels
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                double value = (maxValue * i) / 5;
                int y = height - margin - (chartHeight * i) / 5;
                g2d.drawString(String.format("%.0f", value), 5, y + 5);
            }
        }
    }

    /**
     * Inner class for CFG adherence bar chart
     */
    private class CFGBarChartPanel extends JPanel {
        private Map<String, Double> beforeData;
        private Map<String, Double> afterData;

        public CFGBarChartPanel(Map<String, Double> beforeData, Map<String, Double> afterData) {
            this.beforeData = beforeData;
            this.afterData = afterData;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            int margin = 40;
            int chartWidth = width - 2 * margin;
            int chartHeight = height - 2 * margin;

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            String title = "Canada Food Guide Adherence";
            g2d.drawString(title, margin, 20);

            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(margin, height - margin, width - margin, height - margin);
            g2d.drawLine(margin, margin, margin, height - margin);

            double maxValue = 5.0; // Max servings is 5

            // Draw bars
            int numGroups = beforeData.size();
            int barGroupWidth = chartWidth / numGroups;
            int barWidth = barGroupWidth / 3;
            int spacing = barWidth / 2;

            int index = 0;
            for (String foodGroup : beforeData.keySet()) {
                int x = margin + index * barGroupWidth + spacing;

                // Before bar
                double beforeValue = beforeData.get(foodGroup);
                int beforeHeight = (int) ((beforeValue / maxValue) * chartHeight);
                g2d.setColor(new Color(255, 100, 100));
                g2d.fillRect(x, height - margin - beforeHeight, barWidth, beforeHeight);

                // After bar
                double afterValue = afterData.get(foodGroup);
                int afterHeight = (int) ((afterValue / maxValue) * chartHeight);
                g2d.setColor(new Color(100, 255, 100));
                g2d.fillRect(x + barWidth, height - margin - afterHeight, barWidth, afterHeight);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));

                String[] words = foodGroup.split(" & ");
                for (int i = 0; i < words.length; i++) {
                    g2d.drawString(words[i], x, height - margin + 15 + (i * 12));
                }

                index++;
            }

            // Draw Y-axis labels (servings)
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i <= 5; i++) {
                int y = height - margin - (chartHeight * i) / 5;
                g2d.drawString(String.valueOf(i), margin - 20, y + 5);
            }
            g2d.drawString("Servings", 5, margin - 10);
        }
    }
}