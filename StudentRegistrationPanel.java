import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.Pattern;

public class StudentRegistrationPanel extends JPanel {
private final Connection conn;
private final JFrame parentFrame;

public StudentRegistrationPanel(Connection conn, JFrame parentFrame) {
	this.conn = conn;
	this.parentFrame = parentFrame;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout(10, 10));
	setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

	JLabel title = new JLabel("Student Registration", JLabel.CENTER);
	title.setFont(new Font("Segoe UI", Font.BOLD, 24));
	add(title, BorderLayout.NORTH);

	JPanel formPanel = new JPanel(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets = new Insets(8, 8, 8, 8);
	gbc.anchor = GridBagConstraints.WEST;

	JLabel nameLabel = new JLabel("Full Name:");
	JTextField nameField = new JTextField(20);

	JLabel emailLabel = new JLabel("Email:");
	JTextField emailField = new JTextField(20);

	JLabel passwordLabel = new JLabel("Password:");
	JPasswordField passwordField = new JPasswordField(20);

	JLabel confirmLabel = new JLabel("Confirm Password:");
	JPasswordField confirmField = new JPasswordField(20);

	JLabel gpaLabel = new JLabel("GPA:");
	JTextField gpaField = new JTextField(6);

	JLabel majorLabel = new JLabel("Major:");
	JTextField majorField = new JTextField(20);

	// Add components row-wise
	int row = 0;

	gbc.gridx = 0; gbc.gridy = row;
	formPanel.add(nameLabel, gbc);
	gbc.gridx = 1;
	formPanel.add(nameField, gbc);

	row++;
	gbc.gridx = 0; gbc.gridy = row;
	formPanel.add(emailLabel, gbc);
	gbc.gridx = 1;
	formPanel.add(emailField, gbc);

	row++;
	gbc.gridx = 0; gbc.gridy = row;
	formPanel.add(passwordLabel, gbc);
	gbc.gridx = 1;
	formPanel.add(passwordField, gbc);

	row++;
	gbc.gridx = 0; gbc.gridy = row;
	formPanel.add(confirmLabel, gbc);
	gbc.gridx = 1;
	formPanel.add(confirmField, gbc);

	row++;
	gbc.gridx = 0; gbc.gridy = row;
	formPanel.add(gpaLabel, gbc);
	gbc.gridx = 1;
	formPanel.add(gpaField, gbc);

	row++;
	gbc.gridx = 0; gbc.gridy = row;
	formPanel.add(majorLabel, gbc);
	gbc.gridx = 1;
	formPanel.add(majorField, gbc);

	add(formPanel, BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
	JButton registerButton = new JButton("Register");
	JButton backButton = new JButton("Back to Login");
	buttonPanel.add(registerButton);
	buttonPanel.add(backButton);

	add(buttonPanel, BorderLayout.SOUTH);

	// Action Listeners
	registerButton.addActionListener(e -> {
		registerButton.setEnabled(false);
		handleRegistration(
				nameField.getText().trim(),
				emailField.getText().trim(),
				new String(passwordField.getPassword()),
				new String(confirmField.getPassword()),
				gpaField.getText().trim(),
				majorField.getText().trim()
		);
		registerButton.setEnabled(true);
	});

	backButton.addActionListener(e -> returnToLogin());

	nameField.requestFocusInWindow();
}

private void handleRegistration(String name, String email, String password, String confirmPassword,
								String gpaStr, String major) {
	if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || gpaStr.isEmpty() || major.isEmpty()) {
		JOptionPane.showMessageDialog(this, "⚠️ Please fill in all fields.");
		return;
	}

	if (!isValidEmail(email)) {
		JOptionPane.showMessageDialog(this, "⚠️ Please enter a valid email address.");
		return;
	}

	if (!password.equals(confirmPassword)) {
		JOptionPane.showMessageDialog(this, "⚠️ Passwords do not match.");
		return;
	}

	if (password.length() < 6) {
		JOptionPane.showMessageDialog(this, "⚠️ Password must be at least 6 characters.");
		return;
	}

	double gpa;
	try {
		gpa = Double.parseDouble(gpaStr);
		if (gpa < 0 || gpa > 4.0) {
			JOptionPane.showMessageDialog(this, "⚠️ GPA must be between 0 and 4.0.");
			return;
		}
	} catch (NumberFormatException ex) {
		JOptionPane.showMessageDialog(this, "⚠️ Please enter a valid GPA.");
		return;
	}

	// Check and insert into database
	try (PreparedStatement checkStmt = conn.prepareStatement("SELECT 1 FROM Student WHERE email = ?")) {
		checkStmt.setString(1, email);
		ResultSet rs = checkStmt.executeQuery();
		if (rs.next()) {
			JOptionPane.showMessageDialog(this, "⚠️ Email already registered.");
			return;
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "❌ Database error: " + ex.getMessage());
		return;
	}

	try (PreparedStatement insertStmt = conn.prepareStatement(
			"INSERT INTO Student (name, email, password, gpa, major) VALUES (?, ?, ?, ?, ?)"
	)) {
		insertStmt.setString(1, name);
		insertStmt.setString(2, email);
		insertStmt.setString(3, password);
		insertStmt.setDouble(4, gpa);
		insertStmt.setString(5, major);

		int affectedRows = insertStmt.executeUpdate();
		if (affectedRows > 0) {
			JOptionPane.showMessageDialog(this, "🎉 Registration successful!");
			resetForm();
			returnToLogin();
		} else {
			JOptionPane.showMessageDialog(this, "❌ Registration failed. Please try again.");
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "❌ Database error: " + ex.getMessage());
	}
}

private void resetForm() {
	for (Component c : getComponents()) {
		if (c instanceof JPanel) {
			for (Component comp : ((JPanel) c).getComponents()) {
				if (comp instanceof JTextField) ((JTextField) comp).setText("");
				if (comp instanceof JPasswordField) ((JPasswordField) comp).setText("");
			}
		}
	}
}

private boolean isValidEmail(String email) {
	// Simple email regex pattern
	String emailRegex = "^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)+$";
	return Pattern.matches(emailRegex, email);
}

private void returnToLogin() {
	parentFrame.getContentPane().removeAll();
	parentFrame.add(new StudentLoginPanel(conn, parentFrame));
	parentFrame.revalidate();
	parentFrame.repaint();
}
}
