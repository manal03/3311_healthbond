import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class MainUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private UserProfile user;

    // Taskbar buttons
    private JButton mealEntryBtn;
    private JButton editProfileBtn;
    private JButton generateGoalBtn;

    public MainUI(UserProfile user) {
        this.user = user;
        setTitle("Nutrition Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // Center window on screen

        // Set up the main layout
        setLayout(new BorderLayout());

        // Create a welcome label with some styling
        JLabel welcome = new JLabel("Welcome " + user.getName() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(welcome, BorderLayout.NORTH);

        // Create taskbar panel (buttons)
        JPanel taskbar = new JPanel();
        taskbar.setBackground(new Color(116, 209, 115));
        taskbar.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Centered

        // Initialize buttons
        mealEntryBtn = new JButton("Enter Meal");
        editProfileBtn = new JButton("Edit Profile");
        generateGoalBtn = new JButton("Generate Goal");

        // Common button styling
        Dimension btnSize = new Dimension(140, 50);
        for (JButton btn : new JButton[]{mealEntryBtn, editProfileBtn, generateGoalBtn}) {
            btn.setPreferredSize(btnSize);
            btn.setFocusable(false);
            btn.addActionListener(this);
            taskbar.add(btn);
        }

        add(taskbar, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mealEntryBtn) {
            this.dispose();
            new MealLog(user);
        } else if (e.getSource() == editProfileBtn) {
            this.dispose();
            new EditProfileUI(user);
        } else if (e.getSource() == generateGoalBtn) {
            // --- THIS IS THE NEW LOGIC FOR THE "Generate Goal" BUTTON ---

            // 1. Instantiate the class that implements the interface.
            RecommendationInterface recommendationFinder = new RecommendNutrients();

            System.out.println("--- Button Clicked: Calling findForUser method ---");

            // 2. Call the method to get the recommended daily goals.
            //    The method will automatically print the HashMap contents to the console.
            Map<String, Double> recommendedGoals = recommendationFinder.findForUser(user);

            // 3. Show a confirmation message to the user.
            JOptionPane.showMessageDialog(this,
                    "The recommended goals have been fetched. Please check the console for the results.",
                    "Test Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
