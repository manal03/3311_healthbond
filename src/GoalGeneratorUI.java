import javax.swing.*;
import java.awt.*;

public class GoalGeneratorUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private UserProfile user;
    private JTextArea goalsTextArea; // Text area to display goals

    public GoalGeneratorUI(UserProfile user) {
    	
        this.user = user;
        setTitle("Generate Goals");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Title Label ---
        JLabel titleLabel = new JLabel("Your Nutrition Goals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // --- Text Area for Goal Content ---
        // This text area will be populated by your GoalGenerator class in the future.
        goalsTextArea = new JTextArea("Potential goals will be displayed here.\n\nThis area will show a comparison of your daily intake vs. your recommended targets.");
        goalsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        goalsTextArea.setEditable(false);
        goalsTextArea.setLineWrap(true);
        goalsTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(goalsTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // --- Back Button for navigation ---
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            this.dispose(); // Close this window
            new MainUI(user).setVisible(true); // Open the main menu
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the window
        setVisible(true);

    }
}
