import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateProfileUI extends JFrame implements ActionListener {
    JButton submit;
    JTextField genderField, weightField, heightField, dobField, nameField;

    CreateProfileUI() {
        this.getContentPane().setBackground(new Color(36,128,34));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 700);
        this.setVisible(true);
        this.setLayout(null);
        this.setSize(1000, 500);
        JLabel name = new JLabel("Name:");
        nameField = new JTextField();
        nameField.setBounds(100, 60, 100, 30);
        name.setBounds(30, 60, 60, 30);
        JLabel gender = new JLabel("Gender:");
        genderField = new JTextField();
        gender.setBounds(50, 100, 60, 30);
        genderField.setBounds(120, 100, 100, 30);

        JLabel height = new JLabel("Height:");
        heightField = new JTextField();
        height.setBounds(230, 100, 60, 30);
        heightField.setBounds(290, 100, 100, 30);

        JLabel weight = new JLabel("Weight:");
        weightField = new JTextField();
        weight.setBounds(400, 100, 60, 30);
        weightField.setBounds(460, 100, 100, 30);

        JLabel dob = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobField = new JTextField();
        dob.setBounds(50, 250, 60, 30);
        dobField.setBounds(120, 250, 100, 30);
        dobField.setPreferredSize(new Dimension(200, 30));

        submit = new JButton("Submit");
        submit.setBounds(580, 300, 100, 30);
        submit.addActionListener(this);
        this.setResizable(false);
        this.add(gender);
        this.add(genderField);
        this.add(height);
        this.add(heightField);
        this.add(weight);
        this.add(weightField);
        this.add(dob);
        this.add(dobField);
        this.add(name);
        this.add(nameField);
        this.add(submit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // handle form submission here
        if(e.getSource()==submit){
            try {
                String name = nameField.getText();
                String gender = genderField.getText();
                String weight = weightField.getText();
                String height = heightField.getText();
                String dob = dobField.getText();
                Connection con = ConnectionProvider.getCon();
                if(con != null){
                    String query = "INSERT INTO users(sex, dateofbirth, height_cm, weight_kg, name) VALUES(?,?,?,?,?)";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1,gender);
                    stmt.setString(2, dob);
                    stmt.setInt(3, Integer.parseInt(height));
                    stmt.setInt(4, Integer.parseInt(weight));
                    stmt.setString(5, name);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(this, "Profile created successfully!");
                        UserProfile user = new UserProfile(dob,Integer.parseInt(weight) , Integer.parseInt(height),gender,name);
                        new MainUI(user);
                        this.dispose();

                    } else {
                        JOptionPane.showMessageDialog(this, "Profile creation failed.");
                    }
                    stmt.close();
                    con.close();
                }
            }
            catch (Exception ex) {
                System.out.println(ex);
            } {


            }
        }
    }
}
