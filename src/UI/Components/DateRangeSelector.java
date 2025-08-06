package UI.Components;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DateRangeSelector {
    private final JPanel panel;
    private final JComboBox<String> rangeCombo;
    private final JTextField startDateField;
    private final JTextField endDateField;

    public DateRangeSelector() {
        panel = new JPanel(new GridBagLayout());

        String[] ranges = {"Past 7 Days", "Past 15 Days", "Past 30 Days", "Custom Range"};
        rangeCombo = new JComboBox<>(ranges);
        rangeCombo.setPreferredSize(new Dimension(150, 25));

        startDateField = new JTextField(LocalDate.now().minusDays(7).toString(), 10);
        endDateField = new JTextField(LocalDate.now().toString(), 10);

        startDateField.setEnabled(false);
        endDateField.setEnabled(false);

        startDateField.setPreferredSize(new Dimension(150, 25));
        endDateField.setPreferredSize(new Dimension(150, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_START;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Date Range:"), gbc);

        gbc.gridx = 1;
        panel.add(rangeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        panel.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        panel.add(endDateField, gbc);

        rangeCombo.addActionListener(e -> updateDateFields());
    }


    private void updateDateFields() {
        String selected = (String) rangeCombo.getSelectedItem();
        LocalDate today = LocalDate.now();
        switch (selected) {
            case "Past 7 Days" -> {
                startDateField.setText(today.minusDays(7).toString());
                endDateField.setText(today.toString());
                startDateField.setEnabled(false);
                endDateField.setEnabled(false);
            }
            case "Past 15 Days" -> {
                startDateField.setText(today.minusDays(15).toString());
                endDateField.setText(today.toString());
                startDateField.setEnabled(false);
                endDateField.setEnabled(false);
            }
            case "Past 30 Days" -> {
                startDateField.setText(today.minusDays(30).toString());
                endDateField.setText(today.toString());
                startDateField.setEnabled(false);
                endDateField.setEnabled(false);
            }
            case "Custom Range" -> {
                startDateField.setEnabled(true);
                endDateField.setEnabled(true);
            }
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    public String getStartDate() {
        return startDateField.getText().trim();
    }

    public String getEndDate() {
        return endDateField.getText().trim();
    }

    public String getSelectedRange() {
        return (String) rangeCombo.getSelectedItem();
    }
}
