import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLoginPanel extends JPanel {
private Connection conn;
private JFrame parentFrame;

public AdminLoginPanel(Connection conn, JFrame parentFrame) {
	this.conn = conn;
	this.parentFrame = parentFrame;
	initComponents();
}

private void initComponents() {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100)); // Padding

	JLabel titleLabel = new JLabel("Admin Login");
	titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
	titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	JLabel emailLabel = new JLabel("Email:");
	JTextField emailField = new JTextField(20);
	emailField.setMaximumSize(emailField.getPreferredSize());

	JLabel passwordLabel = new JLabel("Password:");
	JPasswordField passwordField = new JPasswordField(20);
	passwordField.setMaximumSize(passwordField.getPreferredSize());

	JButton loginButton = new JButton("Login");
	loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

	loginButton.addActionListener(e ->
			handleLogin(emailField.getText().trim(), new String(passwordField.getPassword()))
	);

	add(titleLabel);
	add(Box.createVerticalStrut(20));
	add(emailLabel);
	add(emailField);
	add(Box.createVerticalStrut(10));
	add(passwordLabel);
	add(passwordField);
	add(Box.createVerticalStrut(20));
	add(loginButton);
}

private void handleLogin(String email, String password) {
	try {
		PreparedStatement stmt = conn.prepareStatement(
				"SELECT * FROM Admin WHERE email = ? AND password = ?"
		);
		stmt.setString(1, email);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			openAdminDashboard(rs.getString("name"));
		} else {
			JOptionPane.showMessageDialog(this, "Invalid email or password");
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
	}
}

private void openAdminDashboard(String adminName) {
	parentFrame.getContentPane().removeAll();
	parentFrame.add(new AdminDashboardPanel(conn, parentFrame, adminName));
	parentFrame.revalidate();
	parentFrame.repaint();
}
}
