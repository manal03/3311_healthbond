package UI;

import UI.Substitution.MainMenuPanel;
import models.UserProfile;

import javax.swing.*;
import java.awt.*;

public class SubstitutionUI {
    private final UserProfile user;
    private final JFrame frame;

    public SubstitutionUI(UserProfile user) {
        this.user = user;
        frame = new JFrame("Meal Substitution System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        initUI();
        frame.setVisible(true);
    }

    private void initUI() {
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(116, 209, 115));

        JLabel title = new JLabel("Food Substitution Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        frame.add(title, BorderLayout.NORTH);

        MainMenuPanel mainMenu = new MainMenuPanel(user, frame);
        frame.add(mainMenu.getPanel(), BorderLayout.CENTER);
    }

    public JFrame getFrame() {
        return frame;
    }
}
