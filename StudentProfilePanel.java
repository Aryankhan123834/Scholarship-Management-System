import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentProfilePanel extends JPanel {
private Connection conn;
private Student student;
private JFrame parentFrame;

public StudentProfilePanel(Connection conn, Student student, JFrame parentFrame) {
	this.conn = conn;
	this.student = student;
	this.parentFrame = parentFrame;
	initComponents();
}

private void initComponents() {
	setLayout(new GridLayout(6, 2, 10, 10));

	JLabel nameLabel = new JLabel("Name:");
	JTextField nameField = new JTextField(student.getName());
	nameField.setEditable(false);

	JLabel emailLabel = new JLabel("Email:");
	JTextField emailField = new JTextField(student.getEmail());
	emailField.setEditable(false);

	JLabel gpaLabel = new JLabel("GPA:");
	JTextField gpaField = new JTextField(String.valueOf(student.getGpa()));

	JLabel majorLabel = new JLabel("Major:");
	JTextField majorField = new JTextField(student.getMajor());

	JLabel passwordLabel = new JLabel("New Password:");
	JPasswordField passwordField = new JPasswordField();

	JButton updateButton = new JButton("Update Profile");
	JButton cancelButton = new JButton("Cancel");

	add(nameLabel);
	add(nameField);
	add(emailLabel);
	add(emailField);
	add(gpaLabel);
	add(gpaField);
	add(majorLabel);
	add(majorField);
	add(passwordLabel);
	add(passwordField);
	add(updateButton);
	add(cancelButton);

	updateButton.addActionListener(e -> updateProfile(
			gpaField.getText(),
			majorField.getText(),
			new String(passwordField.getPassword())
	));

	cancelButton.addActionListener(e -> cancelUpdate());
}

private void updateProfile(String gpaStr, String major, String newPassword) {
	try {
		double gpa = Double.parseDouble(gpaStr);
		if (gpa < 0 || gpa > 4.0) {
			JOptionPane.showMessageDialog(this, "GPA must be between 0 and 4.0");
			return;
		}

		String updateQuery = "UPDATE Student SET gpa = ?, major = ?";
		if (!newPassword.isEmpty()) {
			updateQuery += ", password = ?";
		}
		updateQuery += " WHERE student_id = ?";

		PreparedStatement stmt = conn.prepareStatement(updateQuery);
		stmt.setDouble(1, gpa);
		stmt.setString(2, major);

		int paramIndex = 3;
		if (!newPassword.isEmpty()) {
			stmt.setString(paramIndex++, newPassword);
		}
		stmt.setInt(paramIndex, student.getStudentId());

		int affectedRows = stmt.executeUpdate();

		if (affectedRows > 0) {
			student.setGpa(gpa);
			student.setMajor(major);
			if (!newPassword.isEmpty()) {
				student.setPassword(newPassword);
			}
			JOptionPane.showMessageDialog(this, "Profile updated successfully!");
		} else {
			JOptionPane.showMessageDialog(this, "Failed to update profile");
		}
	} catch (NumberFormatException e) {
		JOptionPane.showMessageDialog(this, "Please enter a valid GPA");
	} catch (SQLException e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
	}
}

private void cancelUpdate() {
	parentFrame.getContentPane().removeAll();
	parentFrame.add(new StudentDashboardPanel(conn, parentFrame, student));
	parentFrame.revalidate();
	parentFrame.repaint();
}
}