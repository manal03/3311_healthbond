package UI.Components;

//import UI.SplashScreenUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackButton extends JButton implements ActionListener {
    private final JFrame currentFrame;
    private final JFrame previousFrame;

    public BackButton(JFrame currentFrame, JFrame previousFrame) {
        super("← Back");
        this.currentFrame = currentFrame;
        this.previousFrame = previousFrame;
        setupButton();
    }

    public BackButton(JFrame currentFrame) {
        super("← Back");
        this.currentFrame = currentFrame;
        this.previousFrame = null; // Will create new SplashScreenUI
        setupButton();
    }

    private void setupButton() {
        this.addActionListener(this);
        this.setFocusable(false);
        this.setFont(new Font("Arial", Font.PLAIN, 14));
        this.setPreferredSize(new Dimension(80, 30));
        this.setBackground(new Color(70, 130, 180));
        this.setForeground(Color.WHITE);
        this.setBorder(BorderFactory.createRaisedBevelBorder());

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(70, 130, 180));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentFrame.dispose();


        if (previousFrame != null) {
            previousFrame.setVisible(true);
        } else {
//            new SplashScreenUI();
        }
    }

    public void setPosition(int x, int y) {
        this.setBounds(x, y, 80, 30);
    }

    public void setCustomSize(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.setSize(width, height);
    }
}