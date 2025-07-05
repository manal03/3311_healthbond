import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends JFrame implements ActionListener {
    private UserProfile user;
    JButton mealEntryBtn;
    JButton editProfileBtn;
    JPanel mealEntryPanel;
    JLabel mealTypeLabel;
    public MainUI(UserProfile user){
        this.user = user;
        this.setLayout(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(116, 209, 115));
        JLabel welcome = new JLabel("Welcome " + user.getName() + "!");
        welcome.setBounds(100,100, 500, 200);
        this.add(welcome);
        mealEntryBtn = new JButton();
        editProfileBtn = new JButton();
        editProfileBtn.setBounds(20, 120, 150, 80);
        editProfileBtn.setText("Edit Profile");
        editProfileBtn.setFocusable(false);
        editProfileBtn.addActionListener(this);
        mealEntryBtn.setBounds(20, 20, 150, 80);
        mealEntryBtn.setText("Enter Meal:");
        mealEntryBtn.setFocusable(false);
        mealEntryBtn.addActionListener(this);

        this.add(mealEntryBtn);
        this.add(editProfileBtn);
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==mealEntryBtn){
            MealLog mealLogger = new MealLog(user);
            this.dispose();
        }else if(e.getSource()==editProfileBtn){
            this.dispose();
            EditProfileUI editProfile = new EditProfileUI(user);

        }
    }


}

