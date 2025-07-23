package UI;

import models.NutrientSummary;
import models.RecommendedIntake;
import models.UserProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class NutrientVisualizerUI extends JFrame {
    private final UserProfile user;
    private JComboBox<String> timeRangeCombo;
    private JButton generateBtn;
    private JPanel chartPanel;
    private JEditorPane summaryPane;
    private JPanel statusPanel;
    private JLabel userInfoLabel;
    private JTabbedPane tabbedPane;

    public NutrientVisualizerUI(UserProfile user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Health Tracker - Nutrient Visualizer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 720);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(116, 209, 115));

        JLabel title = new JLabel("Nutrient Visualizer");
        title.setBounds(40, 30, 400, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Analyze your intake and visualize health stats");
        subtitle.setBounds(40, 60, 400, 20);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.WHITE);
        add(subtitle);

        JPanel content = new JPanel(null);
        content.setBounds(60, 100, 880, 570);
        content.setBackground(new Color(142, 182, 101));
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(content);

        userInfoLabel = new JLabel(getUserInfoText());
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userInfoLabel.setForeground(Color.WHITE);
        userInfoLabel.setBounds(20, 10, 800, 25);
        content.add(userInfoLabel);

        JLabel timeLabel = new JLabel("Time Period:");
        timeLabel.setBounds(20, 50, 100, 25);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        content.add(timeLabel);

        timeRangeCombo = new JComboBox<>(new String[]{"Last 7 days", "Last 30 days", "Last 3 months", "All time"});
        timeRangeCombo.setBounds(100, 50, 140, 25);
        content.add(timeRangeCombo);

        generateBtn = new JButton("Generate");
        generateBtn.setBounds(530, 50, 140, 30);
        generateBtn.setFont(new Font("Arial", Font.BOLD, 13));
        generateBtn.setBackground(new Color(85, 170, 85));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusable(false);
        generateBtn.addActionListener(this::generateVisualization);
        content.add(generateBtn);

        JButton helpBtn = new JButton("?");
        helpBtn.setToolTipText("What does this mean?");
        helpBtn.setBounds(680, 50, 30, 30);
        helpBtn.setFont(new Font("Arial", Font.BOLD, 14));
        helpBtn.setBackground(Color.WHITE);
        helpBtn.setFocusable(false);
        helpBtn.addActionListener(e -> showHelpDialog());
        content.add(helpBtn);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(20, 100, 830, 440);
        content.add(tabbedPane);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);

        summaryPane = new JEditorPane();
        summaryPane.setContentType("text/html");
        summaryPane.setEditable(false);
        summaryPane.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane summaryScroll = new JScrollPane(summaryPane);

        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        JScrollPane statusScroll = new JScrollPane(statusPanel);

        tabbedPane.addTab("Summary", summaryScroll);
        tabbedPane.addTab("Status", statusScroll);
        tabbedPane.addTab("Pie Chart", chartPanel);

        setVisible(true);
    }

    private String getUserInfoText() {
        int age = (int) new RecommendedIntake(user).calculateAge(user.getDob());
        return String.format("👤 User: %s | Gender: %s | Age: %d | Units: %s",
                user.getName(), user.getSex(), age, user.getUnit());
    }

    private void generateVisualization(ActionEvent e) {
        String period = (String) timeRangeCombo.getSelectedItem();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case "Last 7 days" -> startDate = endDate.minusDays(7);
            case "Last 30 days" -> startDate = endDate.minusDays(30);
            case "Last 3 months" -> startDate = endDate.minusMonths(3);
            default -> startDate = LocalDate.of(2000, 1, 1);
        }

        try {
            NutritionAnalysis analysis = new NutritionAnalysis(user);
            List<NutrientSummary> summaries = analysis.analyzeNutrients(startDate, endDate);
            if (summaries.isEmpty()) showEmptyState();
            else {
                showTextSummary(summaries);
                showPieChart(summaries);
                showStatusIndicators(summaries);

                tabbedPane.setSelectedIndex(0);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void showHelpDialog() {
        JDialog dialog = new JDialog(this, "Nutrient Visualizer Help", true);
        dialog.setSize(540, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        String html = """
    <html>
    <body style='font-family:sans-serif; padding:12px;'>
        <h2 style='color:#2E86C1;'>Nutrient Visualizer - Help</h2>
        
        <p><b>📊 Pie Chart:</b> Shows your nutrient intake split into categories like protein, fat, carbs, fiber, and others.</p>

        <p><b>📋 Summary:</b> Table showing your average daily intake, units, and percentage of total calories.</p>

        <p><b>✅ Status Panel:</b> Highlights your intake against recommended values:</p>
        <ul style='list-style-type: none; padding-left: 0;'>
            <li>🟢 <b style='color:green;'>Optimal</b> — >=100% of recommended</li>
            <li>🟡 <b style='color:orange;'>Adequate</b> — 70–99%</li>
            <li>🔴 <b style='color:red;'>Low</b> — Below 70%</li>
        </ul>

        <p><b>📅 Time Filter:</b> Use the dropdown to change time period (last 7 days, 30 days, etc.)</p>

        <hr>
        <p style='color:gray; font-size: 11px;'>Tip: Summary and Status tabs provide deeper insight beyond the pie chart.</p>
    </body>
    </html>
    """;

        JEditorPane contentPane = new JEditorPane("text/html", html);
        contentPane.setEditable(false);
        contentPane.setOpaque(false);
        contentPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        closeBtn.setFocusable(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(closeBtn);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void showEmptyState() {
        chartPanel.removeAll();
        chartPanel.add(new JLabel("⚠️ No data available for selected time period."), BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();

        summaryPane.setText("No data available.");
        statusPanel.removeAll();
        statusPanel.add(new JLabel("No data available."));
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    private void showPieChart(List<NutrientSummary> summaries) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        double otherTotal = 0;
        for (NutrientSummary summary : summaries) {
            switch (summary.getName().toUpperCase()) {
                case "ENERGY (KILOCALORIES)" -> dataset.setValue("Calories", summary.getPercentageOfTotal());
                case "CARBOHYDRATE, TOTAL (BY DIFFERENCE)" -> dataset.setValue("Carbs", summary.getPercentageOfTotal());
                case "FAT (TOTAL LIPIDS)" -> dataset.setValue("Fat", summary.getPercentageOfTotal());
                case "PROTEIN" -> dataset.setValue("Protein", summary.getPercentageOfTotal());
                case "FIBRE, TOTAL DIETARY" -> dataset.setValue("Fiber", summary.getPercentageOfTotal());
                default -> otherTotal += summary.getPercentageOfTotal();
            }
        }
        if (otherTotal > 0) dataset.setValue("Other Nutrients", otherTotal);

        JFreeChart chart = ChartFactory.createPieChart(
                "Macronutrient Distribution", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSimpleLabels(true);
        plot.setInteriorGap(0.04);

        chartPanel.removeAll();
        chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void showTextSummary(List<NutrientSummary> summaries) {
        Set<String> keyNutrients = Set.of(
                "ENERGY (KILOCALORIES)",
                "CARBOHYDRATE, TOTAL (BY DIFFERENCE)",
                "FAT (TOTAL LIPIDS)",
                "PROTEIN",
                "FIBRE, TOTAL DIETARY"
        );

        StringBuilder html = new StringBuilder();
        html.append("""
        <html><head>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                padding: 10px;
                color: #333;
                background-color: #f8fff8;
            }
            h3 {
                color: #2E86C1;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
                background-color: #ffffff;
            }
            th, td {
                border: 1px solid #d0e4d0;
                padding: 8px;
                text-align: center;
            }
            th {
                background-color: #e6f6e6;
                color: #2b4b2b;
            }
            tr:nth-child(even) {
                background-color: #f4fdf4;
            }
            b {
                color: #1F618D;
            }
        </style>
        </head><body>
    """);

        html.append("<h3>Macronutrient Summary</h3>");
        html.append("<table>");
        html.append("<tr><th>Nutrient</th><th>Amount</th><th>Unit</th><th>% of Total</th></tr>");

        double otherAmount = 0;
        double otherTotal = 0;
        String unit = "";

        for (NutrientSummary s : summaries) {
            String name = s.getName().toUpperCase();
            if (keyNutrients.contains(name)) {
                html.append("<tr>")
                        .append("<td><b>").append(cleanName(name)).append("</b></td>")
                        .append("<td>").append(String.format("%.1f", s.getDailyAverage())).append("</td>")
                        .append("<td>").append(s.getUnit()).append("</td>")
                        .append("<td>").append(String.format("%.1f%%", s.getPercentageOfTotal())).append("</td>")
                        .append("</tr>");
            } else {
                otherAmount += s.getDailyAverage();
                otherTotal += s.getPercentageOfTotal();
                unit = s.getUnit();
            }
        }

        html.append("<tr style='font-weight:bold; background-color:#ecfaec;'>")
                .append("<td>Other Nutrients</td>")
                .append("<td>").append(String.format("%.1f", otherAmount)).append("</td>")
                .append("<td>").append(unit).append("</td>")
                .append("<td>").append(String.format("%.1f%%", otherTotal)).append("</td>")
                .append("</tr>");

        html.append("</table></body></html>");

        summaryPane.setText(html.toString());
        summaryPane.setCaretPosition(0);
    }


    private void showStatusIndicators(List<NutrientSummary> summaries) {
        statusPanel.removeAll();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        Set<String> keyNutrients = Set.of(
                "ENERGY (KILOCALORIES)", "PROTEIN", "FAT (TOTAL LIPIDS)",
                "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FIBRE, TOTAL DIETARY"
        );

        for (NutrientSummary summary : summaries) {
            if (keyNutrients.contains(summary.getName())) {
                statusPanel.add(createNutrientStatusPanel(summary));
                statusPanel.add(Box.createVerticalStrut(10));
            }
        }

        statusPanel.revalidate();
        statusPanel.repaint();
    }

    private JPanel createNutrientStatusPanel(NutrientSummary summary) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(14, 14));
        dot.setBackground(summary.getStatusColor());
        dot.setOpaque(true);
        dot.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        dot.setMaximumSize(new Dimension(14, 14));

        String cleanName = cleanName(summary.getName());
        JLabel nameLabel = new JLabel("<html><b>" + cleanName + "</b></html>");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JProgressBar bar = new JProgressBar(0, 200);
        bar.setValue((int) summary.getPercentageOfRecommended());
        bar.setStringPainted(true);
        bar.setString(String.format("%.1f%%", summary.getPercentageOfRecommended()));
        bar.setForeground(summary.getStatusColor());

        JLabel amountLabel = new JLabel(String.format("%.1f / %.1f %s",
                summary.getDailyAverage(),
                summary.getRecommended(),
                summary.getUnit()));
        amountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(dot);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(nameLabel, BorderLayout.NORTH);
        centerPanel.add(bar, BorderLayout.CENTER);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(amountLabel, BorderLayout.EAST);

        return card;
    }

    private String cleanName(String name) {
        return switch (name.toUpperCase()) {
            case "ENERGY (KILOCALORIES)" -> "Calories";
            case "PROTEIN" -> "Protein";
            case "FAT (TOTAL LIPIDS)" -> "Fat";
            case "CARBOHYDRATE, TOTAL (BY DIFFERENCE)" -> "Carbs";
            case "FIBRE, TOTAL DIETARY" -> "Fiber";
            default -> name.length() > 17 ? name.substring(0, 15) + "…" : name;
        };
    }
}

