import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SplashScreenUI extends JFrame implements ActionListener {
    JButton button;
    JList<String> profileList;
    DefaultListModel<String> listModel;
    SplashScreenUI(){
        this.setLayout(null);
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


        //Display user profiles
        listModel = new DefaultListModel<>();
        profileList = new JList<>(listModel);
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        profileList.addMouseListener(new MouseAdapter(){
           public void mouseClicked(MouseEvent evt){
               if(evt.getClickCount() ==2){
                   String selectedProfile = profileList.getSelectedValue();
                   openMainUIForUser(selectedProfile);
               }
           }
        });

        this.add(button);
        this.add(panel);
        this.add(label2);
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(profileList), BorderLayout.CENTER);
        loadUserProfiles();
        this.setVisible(true);

    }
    private void loadUserProfiles() {
        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT name FROM users";
            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                listModel.addElement(name);
            }

            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading profiles: " + e.getMessage());
        }
    }

    private void openMainUIForUser(String name){
        try (Connection con = ConnectionProvider.getCon()) {
            String query = "SELECT * FROM users WHERE name = ?";
            assert con != null;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("idusers");
                String gender = rs.getString("sex");
                String dob = rs.getString("dateofbirth");
                int height = rs.getInt("height_cm");
                int weight = rs.getInt("weight_kg");
                String unit = rs.getString("unit");

                UserProfile user = new UserProfile(id, dob, weight , height,gender,name, unit);
                new MainUI(user);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening profile: " + e.getMessage());
        }
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
