import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainUI extends JFrame implements ActionListener {
    /**
	 * 
	 */
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
        setLocationRelativeTo(null);  // Center window on screen

        // Set up the main layout
        setLayout(new BorderLayout());

        // Create a welcome label with some styling
        JLabel welcome = new JLabel("Welcome " + user.getName() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add welcome label at the top (north)
        add(welcome, BorderLayout.NORTH);

        // Create taskbar panel (buttons)
        JPanel taskbar = new JPanel();
        taskbar.setBackground(new Color(116, 209, 115));
        taskbar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));  // Left aligned with spacing

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

        // Add the taskbar to the center (or south) depending on design
        add(taskbar, BorderLayout.CENTER);

        // Optionally add a main content panel below the taskbar for other info
        // For now, just empty or a placeholder
        JPanel mainContent = new JPanel();
        mainContent.setBackground(Color.WHITE);
        add(mainContent, BorderLayout.SOUTH);

        // Show window
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mealEntryBtn) {
            new MealLog(user);
            this.dispose();
        } else if (e.getSource() == editProfileBtn) {
            new EditProfileUI(user);
            this.dispose();
        } else if (e.getSource() == generateGoalBtn) {
            new GoalGeneratorUI(user);
            this.dispose();
        }
    }
}
