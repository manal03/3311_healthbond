package UI.Components;

import UI.MainUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackButtonToMain extends BackButton {
    private final JFrame currentFrame;
    private final models.UserProfile user;

    public BackButtonToMain(JFrame currentFrame, models.UserProfile user) {
        super(currentFrame); // This will set up visuals from BackButton
        this.currentFrame = currentFrame;
        this.user = user;

        // Override default action
        for (ActionListener al : this.getActionListeners()) {
            this.removeActionListener(al);
        }

        this.addActionListener(this::goToMain);
    }

    private void goToMain(ActionEvent e) {
        currentFrame.dispose();
        new MainUI(user);
    }
}
