package UI.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class FoodSelectionComboBox {
    private final JComboBox<String> comboBox;
    private final List<String> allFoodNames;
    private final JPanel panel;

    public FoodSelectionComboBox(List<String> allFoodNames, String initialSelection) {
        this.allFoodNames = allFoodNames;

        comboBox = new JComboBox<>(allFoodNames.toArray(new String[0]));
        comboBox.setEditable(true);
        comboBox.setPreferredSize(new Dimension(250, 25));  // Set preferred size

        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = editor.getText().toLowerCase();
                List<String> filtered = allFoodNames.stream()
                        .filter(name -> name.toLowerCase().contains(input))
                        .collect(Collectors.toList());

                comboBox.setModel(new DefaultComboBoxModel<>(filtered.toArray(new String[0])));
                comboBox.setSelectedItem(input);
                comboBox.showPopup();
            }
        });

        if (initialSelection != null) {
            comboBox.setSelectedItem(initialSelection);
        }

        panel = new JPanel(new BorderLayout());  // use BorderLayout so combo fills panel
        panel.add(comboBox, BorderLayout.CENTER);
    }


    public String getSelectedFood() {
        return (String) comboBox.getSelectedItem();
    }

    public void setSelectedFood(String foodName) {
        comboBox.setSelectedItem(foodName);
    }

    public JComponent getComponent() {
        return panel;
    }
}
