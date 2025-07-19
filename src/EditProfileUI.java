import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditProfileUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private UserProfile user;

    // UI Components
    private JLabel nameLabel, dobLabel, weightLabel, heightLabel, sexLabel;

    public EditProfileUI(UserProfile user) {
        this.user = user;
        setTitle("Edit Your Profile");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE
        setLayout(new BorderLayout(10, 10));

        // --- Title Panel (Top) ---
        JLabel title = new JLabel("<html><u>Edit Your Profile</u></html>", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 25));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Main Content Panel (Center) ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel currentLabel = new JLabel("Current Details:");
        currentLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(currentLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create and add rows for each profile detail
        nameLabel = new JLabel("Name: " + user.getName());
        mainPanel.add(createEditRow(nameLabel, e -> editName()));

        dobLabel = new JLabel("Date of Birth: " + user.getDob());
        mainPanel.add(createEditRow(dobLabel, e -> editDob()));

        heightLabel = new JLabel("Height: " + user.getHeight() + " cm");
        mainPanel.add(createEditRow(heightLabel, e -> editHeight()));

        weightLabel = new JLabel("Weight: " + user.getWeight() + " kg");
        mainPanel.add(createEditRow(weightLabel, e -> editWeight()));

        sexLabel = new JLabel("Sex: " + user.getSex());
        mainPanel.add(createEditRow(sexLabel, e -> editSex()));

        add(mainPanel, BorderLayout.CENTER);

        // --- Back Button Panel (Bottom) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.addActionListener(e -> {
            this.dispose(); // Close this window
            new MainUI(user).setVisible(true); // Open the main menu
        });
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    /**
     * Helper method to create a consistent row with a label and an "Edit" button.
     */
    private JPanel createEditRow(JLabel label, java.awt.event.ActionListener actionListener) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JButton button = new JButton("Edit");
        button.addActionListener(actionListener);
        rowPanel.add(label, BorderLayout.CENTER);
        rowPanel.add(button, BorderLayout.EAST);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height)); // Constrain height
        return rowPanel;
    }

    // --- Action Methods ---

    private void editName() {
        String newName = JOptionPane.showInputDialog(this, "Enter new name:", user.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            updateInDatabase("name", newName);
            user.setName(newName);
            nameLabel.setText("Name: " + newName);
        }
    }

    private void editDob() {
        String newDOB = JOptionPane.showInputDialog(this, "Enter new date of birth (YYYY-MM-DD):", user.getDob());
        if (newDOB != null && !newDOB.trim().isEmpty()) {
            // Add validation for date format here if needed
            updateInDatabase("dateofbirth", newDOB);
            user.setDob(newDOB);
            dobLabel.setText("Date of Birth: " + newDOB);
        }
    }

    private void editHeight() {
        String newHeightStr = JOptionPane.showInputDialog(this, "Enter new height (cm):", user.getHeight());
        try {
            int newHeight = Integer.parseInt(newHeightStr);
            if (newHeight > 0) {
                updateInDatabase("height_cm", newHeight);
                user.setHeight(newHeight);
                heightLabel.setText("Height: " + newHeight + " cm");
            }
        } catch (NumberFormatException ex) {
            if (newHeightStr != null) JOptionPane.showMessageDialog(this, "Invalid number for height.");
        }
    }

    private void editWeight() {
        String newWeightStr = JOptionPane.showInputDialog(this, "Enter new weight (kg):", user.getWeight());
        try {
            int newWeight = Integer.parseInt(newWeightStr);
            if (newWeight > 0) {
                updateInDatabase("weight_kg", newWeight);
                user.setWeight(newWeight);
                weightLabel.setText("Weight: " + newWeight + " kg");
            }
        } catch (NumberFormatException ex) {
            if (newWeightStr != null) JOptionPane.showMessageDialog(this, "Invalid number for weight.");
        }
    }

    private void editSex() {
        String[] genders = {"Male", "Female"};
        String newSex = (String) JOptionPane.showInputDialog(this, "Select new sex:", "Edit Sex",
                JOptionPane.QUESTION_MESSAGE, null, genders, user.getSex());
        if (newSex != null) {
            updateInDatabase("sex", newSex);
            user.setSex(newSex);
            sexLabel.setText("Sex: " + newSex);
        }
    }

    /**
     * Generic method to update a single field in the users table.
     * @param columnName The name of the column to update.
     * @param value The new value for the column.
     */
    private void updateInDatabase(String columnName, Object value) {
        String sql = "UPDATE users SET " + columnName + " = ? WHERE idusers = ?";
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (value instanceof String) {
                ps.setString(1, (String) value);
            } else if (value instanceof Integer) {
                ps.setInt(1, (Integer) value);
            }
            ps.setInt(2, user.getUserId());

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
