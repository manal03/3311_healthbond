package UI.template;
import models.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Concrete implementation of food swap visualizer using line charts
 */
public class LineChartFoodSwapVisualizer extends AbstractFoodSwapVisualizer {

    public LineChartFoodSwapVisualizer(UserProfile user, String nutrient, String timePeriod, boolean includeCFG) {
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

        LineChartPanel chartPanel = new LineChartPanel(beforeSwapData, afterSwapData,
                selectedNutrient + " Over Time");
        panel.add(chartPanel, BorderLayout.CENTER);

        if (includeCFGAdherence && !cfgBeforeData.isEmpty()) {
            JPanel cfgSummaryPanel = createCFGSummaryPanel();
            panel.add(cfgSummaryPanel, BorderLayout.EAST);
        }
    }

    /**
     * Check if all values are zero
     */
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
     * Create a summary panel for CFG adherence
     */
    private JPanel createCFGSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setPreferredSize(new Dimension(180, 250));

        for (String foodGroup : cfgBeforeData.keySet()) {
            JPanel groupPanel = new JPanel(new GridLayout(2, 1));
            groupPanel.setBackground(new Color(240, 240, 240));
            groupPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

            JLabel nameLabel = new JLabel(foodGroup);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 10));

            double before = cfgBeforeData.get(foodGroup);
            double after = cfgAfterData.get(foodGroup);

            JLabel changeLabel = new JLabel(String.format("%.1f → %.1f", before, after));
            changeLabel.setFont(new Font("Arial", Font.PLAIN, 10));

            groupPanel.add(nameLabel);
            groupPanel.add(changeLabel);

            panel.add(groupPanel);
        }

        return panel;
    }

    /**
     * Inner class for rendering line charts
     */
    private class LineChartPanel extends JPanel {
        private Map<LocalDate, Double> beforeData;
        private Map<LocalDate, Double> afterData;
        private String title;

        public LineChartPanel(Map<LocalDate, Double> beforeData, Map<LocalDate, Double> afterData, String title) {
            this.beforeData = beforeData;
            this.afterData = afterData;
            this.title = title;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 400));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            int margin = 50;
            int chartWidth = width - 2 * margin;
            int chartHeight = height - 2 * margin;

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString(title, margin, 25);

            g2d.setColor(Color.BLACK);
            g2d.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
            g2d.drawLine(margin, margin, margin, height - margin); // Y-axis

            // Calculate bounds
            double minValue = Double.MAX_VALUE;
            double maxValue = Double.MIN_VALUE;

            for (Double value : beforeData.values()) {
                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
            }
            for (Double value : afterData.values()) {
                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
            }

            if (maxValue == minValue) {
                maxValue = minValue + 1;
            }

            // Add some padding to the range
            double range = maxValue - minValue;
            minValue -= range * 0.1;
            maxValue += range * 0.1;

            // Get sorted dates
            java.util.List<LocalDate> sortedDates = new ArrayList<>(beforeData.keySet());
            Collections.sort(sortedDates);

            if (sortedDates.isEmpty()) return;

            // Calculate points for before data
            java.util.List<Point2D> beforePoints = new ArrayList<>();
            java.util.List<Point2D> afterPoints = new ArrayList<>();

            for (int i = 0; i < sortedDates.size(); i++) {
                LocalDate date = sortedDates.get(i);
                double x = margin + (i * chartWidth) / (sortedDates.size() - 1);

                if (beforeData.containsKey(date)) {
                    double beforeValue = beforeData.get(date);
                    double beforeY = height - margin - ((beforeValue - minValue) / (maxValue - minValue)) * chartHeight;
                    beforePoints.add(new Point2D.Double(x, beforeY));
                }

                if (afterData.containsKey(date)) {
                    double afterValue = afterData.get(date);
                    double afterY = height - margin - ((afterValue - minValue) / (maxValue - minValue)) * chartHeight;
                    afterPoints.add(new Point2D.Double(x, afterY));
                }
            }

            // Draw lines
            g2d.setStroke(new BasicStroke(2));

            // Draw before line
            if (beforePoints.size() > 1) {
                g2d.setColor(new Color(255, 100, 100));
                for (int i = 0; i < beforePoints.size() - 1; i++) {
                    Point2D p1 = beforePoints.get(i);
                    Point2D p2 = beforePoints.get(i + 1);
                    g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                }

                // Draw points
                for (Point2D point : beforePoints) {
                    g2d.fillOval((int)point.getX() - 3, (int)point.getY() - 3, 6, 6);
                }
            }

            // Draw after line
            if (afterPoints.size() > 1) {
                g2d.setColor(new Color(100, 255, 100));
                for (int i = 0; i < afterPoints.size() - 1; i++) {
                    Point2D p1 = afterPoints.get(i);
                    Point2D p2 = afterPoints.get(i + 1);
                    g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                }

                // Draw points
                for (Point2D point : afterPoints) {
                    g2d.fillOval((int)point.getX() - 3, (int)point.getY() - 3, 6, 6);
                }
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                double value = minValue + ((maxValue - minValue) * i) / 5;
                int y = height - margin - (chartHeight * i) / 5;
                g2d.drawString(String.format("%.0f", value), 10, y + 5);
            }

            // X-axis labels
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
            int labelInterval = Math.max(1, sortedDates.size() / 6);

            for (int i = 0; i < sortedDates.size(); i += labelInterval) {
                LocalDate date = sortedDates.get(i);
                double x = margin + (i * chartWidth) / (sortedDates.size() - 1);
                String dateStr = date.format(formatter);
                g2d.drawString(dateStr, (int)x - 15, height - margin + 15);
            }
        }
    }
}