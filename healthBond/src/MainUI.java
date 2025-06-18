import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends JFrame implements ActionListener {
    JButton mealEntry;
    JPanel mealEntryPanel;
    JLabel mealTypeLabel;
    public MainUI(UserProfile user){
        this.setLayout(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(116, 209, 115));
        JLabel welcome = new JLabel("Welcome " + user.getName() + "!");
        welcome.setBounds(100,100, 500, 200);
        this.add(welcome);
        mealEntry = new JButton();
        mealEntry.setBounds(20, 20, 150, 80);
        mealEntry.setText("Enter Meal:");
        mealEntry.setFocusable(false);
        mealEntry.addActionListener(this);

        this.add(mealEntry);
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(this);



    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==mealEntry){
            showMealEntryForm();
        }
    }
    private void addIngredientRow(JPanel container) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(480, 40));

        JTextField ingredientField = new JTextField(15); // for ingredient name
        JTextField quantityField = new JTextField(7);    // for quantity

        row.add(new JLabel("Ingredient:"));
        row.add(ingredientField);
        row.add(new JLabel("Quantity:"));
        row.add(quantityField);

        container.add(row);
        container.revalidate();
        container.repaint();
    }

    private void showMealEntryForm() {
        if(mealEntryPanel!=null){
            this.remove(mealEntryPanel);
        }
        mealEntryPanel = new JPanel();
        mealEntryPanel.setLayout(null);
        mealEntryPanel.setVisible(true);
        mealEntryPanel.setBounds(200, 20, 900, 600);
        mealEntryPanel.setBackground(new Color(142, 182, 101));

        mealTypeLabel = new JLabel("Meal Type: ");
        mealTypeLabel.setBounds(10, 30, 100, 50);
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        JComboBox<String> mealTypeBox = new JComboBox<>(mealTypes);
        mealTypeBox.setBounds(10, 70, 100, 50);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(10, 130, 100, 30);
        JTextField dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(10, 160, 150, 30);

        JLabel ingredientsLabel = new JLabel("Ingredients:");
        ingredientsLabel.setBounds(10, 220, 100, 30);

        JPanel ingredientContainer = new JPanel();
        ingredientContainer.setLayout(new BoxLayout(ingredientContainer, BoxLayout.Y_AXIS));
        ingredientContainer.setBounds(10, 250, 500, 300);
        ingredientContainer.setBackground(new Color(142, 182, 101));

        JScrollPane scrollPane = new JScrollPane(ingredientContainer);
        scrollPane.setBounds(10, 250, 500, 300);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mealEntryPanel.add(scrollPane);

        JButton addRowButton = new JButton("Add Ingredient");
        addRowButton.setBounds(50, 460, 150, 30);
        mealEntryPanel.add(addRowButton);

        addIngredientRow(ingredientContainer);

        addRowButton.addActionListener(e -> addIngredientRow(ingredientContainer));
        this.add(mealEntryPanel);
        mealEntryPanel.add(mealTypeLabel);
        mealEntryPanel.add(mealTypeBox);
        mealEntryPanel.add(dateLabel);
        mealEntryPanel.add(dateField);
        mealEntryPanel.add(ingredientsLabel);
        mealEntryPanel.add(scrollPane);

        this.repaint();
        this.revalidate();
    }
}

