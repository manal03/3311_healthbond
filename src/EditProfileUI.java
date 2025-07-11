import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditProfileUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Display current information and on the side, CRUD operation
    JLabel title;
    JLabel name, dob, weight, height, sex, current;
    EditProfileUI(UserProfile user){
        this.setLayout(new BorderLayout());
        this.setSize(600, 520);
        title = new JLabel("<html><u>Edit your profile</u></html>", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 25 ));
        this.add(title, BorderLayout.NORTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Edit your profile");
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        name = new JLabel("Name: " + user.getName(), SwingConstants.LEFT);
        JButton editNameBtn = new JButton("Edit");
        dob = new JLabel("Date of birth: " + user.getDob(), SwingConstants.LEFT);
        JButton editDOBBtn = new JButton("Edit");
        height = new JLabel("Height: " + user.getHeight(), SwingConstants.LEFT);
        JButton editHeightBtn = new JButton("Edit");
        weight = new JLabel("Weight: " + user.getWeight(), SwingConstants.LEFT);
        JButton editWeightBtn = new JButton("Edit");
        sex = new JLabel("Sex: " + user.getSex(), SwingConstants.LEFT);
        JButton editSexBtn = new JButton("Edit");
        current = new JLabel("Current Details: ", SwingConstants.LEFT);
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel dobRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel heightRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel weightRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel sexRow = new JPanel(new FlowLayout(FlowLayout.LEFT));


        current.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        editNameBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        dob.setAlignmentX(Component.LEFT_ALIGNMENT);
        editDOBBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        height.setAlignmentX(Component.LEFT_ALIGNMENT);
        editHeightBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        weight.setAlignmentX(Component.LEFT_ALIGNMENT);
        editWeightBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sex.setAlignmentX(Component.LEFT_ALIGNMENT);
        editSexBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editNameBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editHeightBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editWeightBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        heightRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        sexRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dobRow.setAlignmentX(Component.LEFT_ALIGNMENT);


        current.setFont(new Font("SansSerif", Font.BOLD, 20));
        name.setFont(new Font("SansSerif", Font.PLAIN, 15 ));
        dob.setFont(new Font("SansSerif", Font.PLAIN, 15 ));
        sex.setFont(new Font("SansSerif", Font.PLAIN, 15 ));
        weight.setFont(new Font("SansSerif", Font.PLAIN, 15 ));
        height.setFont(new Font("SansSerif", Font.PLAIN, 15 ));


        leftPanel.add(Box.createRigidArea(new Dimension(0,20)));
        leftPanel.add(current);
        leftPanel.add(Box.createRigidArea(new Dimension(0,20)));
        nameRow.add(name);
        nameRow.add(editNameBtn);
        leftPanel.add(nameRow);
        leftPanel.add(Box.createRigidArea(new Dimension(0,10)));
        dobRow.add(dob);
        dobRow.add(editDOBBtn);
        leftPanel.add(dobRow);
        leftPanel.add(Box.createRigidArea(new Dimension(0,10)));
        heightRow.add(height);
        heightRow.add(editHeightBtn);
        leftPanel.add(heightRow);
        leftPanel.add(Box.createRigidArea(new Dimension(0,10)));
        weightRow.add(weight);
        weightRow.add(editWeightBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0,10)));
        sexRow.add(sex);
        sexRow.add(editSexBtn);
        leftPanel.add(sexRow);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.add(leftPanel);
        this.setVisible(true);

        editNameBtn.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", user.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                try (Connection con = ConnectionProvider.getCon()) {
                    String sql = "UPDATE users SET name = ? WHERE idusers = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, newName);
                    ps.setInt(2, user.getUserId());
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        name.setText("Name: " + newName);
                        user.setName(newName);
                        JOptionPane.showMessageDialog(this, "Name updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update name.");
                    }
                    ps.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        });

        editDOBBtn.addActionListener(e -> {
            String newDOB = JOptionPane.showInputDialog(this, "Enter new name:", user.getDob());
            if (newDOB != null && !newDOB.trim().isEmpty()) {
                try (Connection con = ConnectionProvider.getCon()) {
                    String sql = "UPDATE users SET dateofbirth = ? WHERE idusers = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, newDOB);
                    ps.setInt(2, user.getUserId());
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        dob.setText("Date of birth: " + newDOB);
                        user.setDob(newDOB);
                        JOptionPane.showMessageDialog(this, "DOB updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update DOB.");
                    }
                    ps.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        });

        editHeightBtn.addActionListener(e -> {
            Integer newHeight = Integer.valueOf(JOptionPane.showInputDialog(this, "Enter new height:", user.getHeight()));
            if (!(newHeight <= 0)) {
                try (Connection con = ConnectionProvider.getCon()) {
                    String sql = "UPDATE users SET height_cm = ? WHERE idusers = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, newHeight);
                    ps.setInt(2, user.getUserId());
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        height.setText("Height: " + newHeight);
                        user.setHeight(newHeight);
                        JOptionPane.showMessageDialog(this, "Height updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update Height.");
                    }
                    ps.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        });

        editHeightBtn.addActionListener(e -> {
            Integer newWeight = Integer.valueOf(JOptionPane.showInputDialog(this, "Enter new weight:", user.getWeight()));
            if (!(newWeight <= 0)) {
                try (Connection con = ConnectionProvider.getCon()) {
                    String sql = "UPDATE users SET weight_kg = ? WHERE idusers = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, newWeight);
                    ps.setInt(2, user.getUserId());
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        weight.setText("Weight: " + newWeight);
                        user.setHeight(newWeight);
                        JOptionPane.showMessageDialog(this, "Weight updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update Weight.");
                    }
                    ps.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
        });
        editSexBtn.addActionListener(e -> {
            String[] genders = {"Male", "Female", "Other"};
            String currentSex = user.getSex();

            JComboBox<String> genderBox = new JComboBox<>(genders);
            genderBox.setSelectedItem(currentSex);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    genderBox,
                    "Select new gender:",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                String selectedGender = (String) genderBox.getSelectedItem();
                if (selectedGender != null && !selectedGender.equals(currentSex)) {
                    try (Connection con = ConnectionProvider.getCon()) {
                        String sql = "UPDATE users SET sex = ? WHERE idusers = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, selectedGender);
                        ps.setInt(2, user.getUserId());

                        int updated = ps.executeUpdate();
                        if (updated > 0) {
                            sex.setText("Sex: " + selectedGender);
                            user.setSex(selectedGender);
                            JOptionPane.showMessageDialog(this, "Gender updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update gender.");
                        }
                        ps.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                    }
                }
            }
        });


    }



}

