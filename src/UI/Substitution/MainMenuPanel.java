package UI.Substitution;

import models.UserProfile;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel {
    private final UserProfile user;
    private final JFrame parent;
    private final JPanel panel;

    public MainMenuPanel(UserProfile user, JFrame parent) {
        this.user = user;
        this.parent = parent;
        panel = new JPanel();
        initUI();
    }

    private void initUI() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(142, 182, 101));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200)); // top, left, bottom, right
        panel.setPreferredSize(new Dimension(600, 350));

        // Buttons
        JButton btnNew = createButton("Log New Substitution", () -> new SubstitutionDialog(parent, user).showDialog());
        JButton btnHistory = createButton("View Substitution History", () -> new HistoryDialog(parent, user).showDialog());
        JButton btnStats = createButton("View Substitution Stats", () -> new StatsDialog(parent, user).showDialog());

        panel.add(btnNew);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // spacing
        panel.add(btnHistory);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // spacing
        panel.add(btnStats);
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        styleButton(button);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(85, 170, 85));
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));
    }

    public JPanel getPanel() {
        return panel;
    }
}
