import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentLoginPanel extends JPanel {
private Connection conn;
private JFrame parentFrame;

public StudentLoginPanel(Connection conn, JFrame parentFrame) {
	this.conn = conn;
	this.parentFrame = parentFrame;
	initComponents();
}

private void initComponents() {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100)); // Padding

	JLabel titleLabel = new JLabel("Student Login");
	titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
	titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	JLabel emailLabel = new JLabel("Email:");
	JTextField emailField = new JTextField(20);
	emailField.setMaximumSize(emailField.getPreferredSize());

	JLabel passwordLabel = new JLabel("Password:");
	JPasswordField passwordField = new JPasswordField(20);
	passwordField.setMaximumSize(passwordField.getPreferredSize());

	JButton loginButton = new JButton("Login");
	JButton registerButton = new JButton("Register");

	loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

	loginButton.addActionListener(e ->
			handleLogin(emailField.getText().trim(), new String(passwordField.getPassword()))
	);

	registerButton.addActionListener(e -> switchToRegistration());

	// Adding components with spacing
	add(titleLabel);
	add(Box.createVerticalStrut(20));
	add(emailLabel);
	add(emailField);
	add(Box.createVerticalStrut(10));
	add(passwordLabel);
	add(passwordField);
	add(Box.createVerticalStrut(20));
	add(loginButton);
	add(Box.createVerticalStrut(10));
	add(registerButton);
}

private void handleLogin(String email, String password) {
	try {
		PreparedStatement stmt = conn.prepareStatement(
				"SELECT * FROM Student WHERE email = ? AND password = ?"
		);
		stmt.setString(1, email);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			Student student = new Student(
					rs.getInt("student_id"),
					rs.getString("name"),
					email,
					password,
					rs.getDouble("gpa"),
					rs.getString("major")
			);
			openStudentDashboard(student);
		} else {
			JOptionPane.showMessageDialog(this, "Invalid email or password");
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
	}
}

private void switchToRegistration() {
	parentFrame.getContentPane().removeAll();
	parentFrame.add(new StudentRegistrationPanel(conn, parentFrame));
	parentFrame.revalidate();
	parentFrame.repaint();
}

private void openStudentDashboard(Student student) {
	parentFrame.getContentPane().removeAll();
	parentFrame.add(new StudentDashboardPanel(conn, parentFrame, student));
	parentFrame.revalidate();
	parentFrame.repaint();
}
}
