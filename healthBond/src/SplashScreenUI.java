import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreenUI extends JFrame implements ActionListener {
    JButton button;
    SplashScreenUI(){
        this.setLayout(null);
        this.setVisible(true);
        JLabel label = new JLabel();
        JLabel label2 = new JLabel();
        label2.setText("Select Existing Profiles:");
        JPanel panel = new JPanel();
        label2.setBounds(600, 0, 400, 100);
        panel.setBounds(600, 100, 300, 350);
        panel.setBackground(Color.PINK);
        Font font = new Font("Arial", Font.PLAIN, 40);
        Font font2 = new Font("Arial", Font.PLAIN, 20);
        label.setFont(font);
        label2.setFont(font2);
        label2.setForeground(Color.WHITE);
        button = new JButton();
        button.addActionListener(this);
        label.setVerticalTextPosition(JLabel.TOP);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setForeground(Color.white);
        label.setText("Welcome to HealthBond");
        label.setBounds(100, 100, 500, 30);
        this.setResizable(false);
        this.setSize(1000, 500);
        this.setTitle("HealthBond");
        ImageIcon logo = new ImageIcon("src/healthbond-logo.jpg");
        this.setIconImage(logo.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(36,128,34));
        this.add(label);
        button.setText("Create A Profile");
        button.setBounds(200, 300, 200,50);
        button.setFocusable(false);
        this.add(button);
        this.add(panel);
        this.add(label2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()== button){
            this.dispose();
            CreateProfileUI newProfile = new CreateProfileUI();
            System.out.println("Profile created");
        }
    }
}
