package UI.template;

import models.UserProfile;
import models.SubstitutionRecord;
import models.NutrientInfo;
import models.FoodGroupData;
import services.FoodGroupService;
import services.Substitution;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Abstract base class implementing Template Method Pattern for food swap visualizations
 */
public abstract class AbstractFoodSwapVisualizer {
    protected UserProfile user;
    protected String selectedNutrient;
    protected String timePeriod;
    protected boolean includeCFGAdherence;

    protected Map<LocalDate, Double> beforeSwapData;
    protected Map<LocalDate, Double> afterSwapData;
    protected Map<String, Double> cfgBeforeData;
    protected Map<String, Double> cfgAfterData;

    protected Substitution substitutionService;

    public AbstractFoodSwapVisualizer(UserProfile user, String nutrient, String timePeriod, boolean includeCFG) {
        this.user = user;
        this.selectedNutrient = nutrient;
        this.timePeriod = timePeriod;
        this.includeCFGAdherence = includeCFG;

        beforeSwapData = new TreeMap<>();
        afterSwapData = new TreeMap<>();
        cfgBeforeData = new HashMap<>();
        cfgAfterData = new HashMap<>();

        this.substitutionService = new Substitution();
    }

    /**
     * Template method defining the algorithm for generating visualizations
     */
    public final JPanel generateVisualization() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(endDate);
        List<SubstitutionRecord> records = fetchSubstitutionRecords();
        processNutrientData(records, startDate, endDate);

        if (includeCFGAdherence) {
            processCFGAdherenceData(startDate, endDate);
        }

        JPanel panel = createVisualizationPanel();
        drawVisualization(panel);
        addLegendAndSummary(panel);

        return panel;
    }

    /**
     * Calculate start date based on selected time period
     */
    protected LocalDate calculateStartDate(LocalDate endDate) {
        switch (timePeriod) {
            case "Last 7 days":
                return endDate.minusDays(7);
            case "Last 14 days":
                return endDate.minusDays(14);
            case "Last 30 days":
                return endDate.minusDays(30);
            case "Last 3 months":
                return endDate.minusMonths(3);
            default:
                return endDate.minusDays(7);
        }
    }

    /**
     * Fetch substitution records for the user
     */
    protected List<SubstitutionRecord> fetchSubstitutionRecords() {
        try {
            List<SubstitutionRecord> allRecords = substitutionService.getSubstitutionHistory(user.getUserId());
            LocalDate startDate = calculateStartDate(LocalDate.now());
            LocalDate endDate = LocalDate.now();
            List<SubstitutionRecord> filteredRecords = new ArrayList<>();
            for (SubstitutionRecord record : allRecords) {
                LocalDate dateApplied = record.getDateApplied();
                if (dateApplied != null && !dateApplied.isBefore(startDate) && !dateApplied.isAfter(endDate)) {
                    filteredRecords.add(record);
                }
            }
            return filteredRecords;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Process nutrient data from substitution records
     */
    protected void processNutrientData(List<SubstitutionRecord> records, LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            beforeSwapData.put(currentDate, 0.0);
            afterSwapData.put(currentDate, 0.0);
            currentDate = currentDate.plusDays(1);
        }

        if (!records.isEmpty()) {
            Set<Integer> foodIds = new HashSet<>();
            for (SubstitutionRecord record : records) {
                foodIds.add(record.getOriginalFoodId());
                foodIds.add(record.getSubstituteFoodId());
            }

            try {
                Map<Integer, Map<String, NutrientInfo>> nutrientData =
                        substitutionService.getNutrientDataForFoods(new ArrayList<>(foodIds));

                for (SubstitutionRecord record : records) {
                    LocalDate date = record.getDateApplied();
                    if (date == null) continue;

                    Map<String, NutrientInfo> originalNutrients = nutrientData.get(record.getOriginalFoodId());
                    Map<String, NutrientInfo> substitutedNutrients = nutrientData.get(record.getSubstituteFoodId());

                    if (originalNutrients != null && substitutedNutrients != null) {
                        double originalValue = getNutrientValue(originalNutrients, selectedNutrient);
                        double substitutedValue = getNutrientValue(substitutedNutrients, selectedNutrient);

                        beforeSwapData.merge(date, originalValue, Double::sum);
                        afterSwapData.merge(date, substitutedValue, Double::sum);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get nutrient value from nutrient map
     */
    protected double getNutrientValue(Map<String, NutrientInfo> nutrients, String nutrientName) {
        NutrientInfo nutrient = nutrients.get(nutrientName);
        if (nutrient != null) {
            return nutrient.getValue();
        }

        String nutrientLower = nutrientName.toLowerCase();
        for (Map.Entry<String, NutrientInfo> entry : nutrients.entrySet()) {
            String name = entry.getKey().toLowerCase();
            if (name.contains(nutrientLower) || nutrientLower.contains(name)) {
                return entry.getValue().getValue();
            }
        }

        switch (nutrientName) {
            case "Calories":
                return getNutrientValue(nutrients, "Energy");
            case "Carbohydrates":
                return getNutrientValue(nutrients, "Carbohydrate");
            case "Fat":
                return getNutrientValue(nutrients, "Total lipid");
            case "Fiber":
                return getNutrientValue(nutrients, "Fiber, total dietary");
            case "Sugar":
                return getNutrientValue(nutrients, "Sugars, total");
            default:
                return 0.0;
        }
    }

    /**
     * Process Canada Food Guide adherence data
     */
    protected void processCFGAdherenceData(LocalDate startDate, LocalDate endDate) {
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        FoodGroupData currentData = FoodGroupService.getUserFoodGroupData(user, days);

        // For before data
        cfgBeforeData.put("Vegetables & Fruits", currentData.getVegetables() + currentData.getFruits());
        cfgBeforeData.put("Grains", currentData.getGrains());
        cfgBeforeData.put("Protein", currentData.getProtein());

        if (FoodGroupService.includesDairy()) {
            cfgBeforeData.put("Dairy", currentData.getDairy());
        }

        // For after data
        FoodGroupData recommended = FoodGroupService.getRecommendedProportions();

        double vegFruitImproved = cfgBeforeData.get("Vegetables & Fruits") +
                (recommended.getVegetables() + recommended.getFruits() - cfgBeforeData.get("Vegetables & Fruits")) * 0.3;
        double grainsImproved = cfgBeforeData.get("Grains") +
                (recommended.getGrains() - cfgBeforeData.get("Grains")) * 0.3;
        double proteinImproved = cfgBeforeData.get("Protein") +
                (recommended.getProtein() - cfgBeforeData.get("Protein")) * 0.3;

        cfgAfterData.put("Vegetables & Fruits", vegFruitImproved);
        cfgAfterData.put("Grains", grainsImproved);
        cfgAfterData.put("Protein", proteinImproved);

        if (FoodGroupService.includesDairy()) {
            double dairyImproved = cfgBeforeData.get("Dairy") +
                    (recommended.getDairy() - cfgBeforeData.get("Dairy")) * 0.3;
            cfgAfterData.put("Dairy", dairyImproved);
        }
    }

    protected JPanel createVisualizationPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    protected void addLegendAndSummary(JPanel panel) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JPanel legendPanel = createLegendPanel();
        bottomPanel.add(legendPanel, BorderLayout.WEST);

        JPanel summaryPanel = createSummaryPanel();
        bottomPanel.add(summaryPanel, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);
    }

    protected JPanel createLegendPanel() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legend.setBackground(Color.WHITE);

        JLabel beforeLabel = new JLabel("■ Before Swap");
        beforeLabel.setForeground(new Color(255, 100, 100));
        legend.add(beforeLabel);

        legend.add(Box.createHorizontalStrut(20));

        JLabel afterLabel = new JLabel("■ After Swap");
        afterLabel.setForeground(new Color(100, 255, 100));
        legend.add(afterLabel);

        return legend;
    }

    protected JPanel createSummaryPanel() {
        JPanel summary = new JPanel(new GridLayout(2, 1));
        summary.setBackground(Color.WHITE);
        summary.setBorder(BorderFactory.createTitledBorder("Summary"));

        double totalBefore = beforeSwapData.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalAfter = afterSwapData.values().stream().mapToDouble(Double::doubleValue).sum();
        double saved = totalBefore - totalAfter;
        double improvement = totalBefore > 0 ? (saved / totalBefore) * 100 : 0;

        JLabel totalLabel = new JLabel(String.format("Total %s saved: %.1f", selectedNutrient, saved));
        JLabel improvementLabel = new JLabel(String.format("Improvement: %.1f%%", improvement));

        summary.add(totalLabel);
        summary.add(improvementLabel);

        return summary;
    }

    /**
     * Abstract method to be implemented by subclasses for specific visualization
     */
    protected abstract void drawVisualization(JPanel panel);
}