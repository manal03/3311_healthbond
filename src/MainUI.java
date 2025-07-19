import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private UserProfile user;

    // Taskbar buttons
    private JButton mealEntryBtn;
    private JButton editProfileBtn;
    private JButton generateGoalBtn;
    private JButton viewGoalsBtn; // New button

    public MainUI(UserProfile user) {
        this.user = user;
        setTitle("Nutrition Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // Center window on screen

        setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Welcome " + user.getName() + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(welcome, BorderLayout.NORTH);

        JPanel taskbar = new JPanel();
        taskbar.setBackground(new Color(116, 209, 115));
        taskbar.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // Initialize buttons
        mealEntryBtn = new JButton("Enter Meal");
        editProfileBtn = new JButton("Edit Profile");
        generateGoalBtn = new JButton("Generate Goal");
        viewGoalsBtn = new JButton("View/Manage Goals"); // New button

        // Common button styling
        Dimension btnSize = new Dimension(160, 50); // Increased width for new button text
        for (JButton btn : new JButton[]{mealEntryBtn, editProfileBtn, generateGoalBtn, viewGoalsBtn}) {
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
            this.dispose();
            new GoalGeneratorUI(user);
        } else if (e.getSource() == viewGoalsBtn) { // Handle new button click
            this.dispose();
            new ViewGoalsUI(user); // Open the new ViewGoalsUI window
        }
    }
}
