import javax.swing.*;
import java.sql.*;

public class Main {
// Update these based on your MySQL setup
private static final String DB_URL = "jdbc:mysql://localhost:3306/scholarship_db";
private static final String DB_USER = "root";
private static final String DB_PASS = "Aryan22468"; // Replace with your actual password

public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> {
		try {
			// Load JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Establish connection
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

			// Create main application frame
			JFrame frame = new JFrame("Scholarship Management System");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(900, 600);
			frame.setLocationRelativeTo(null);

			// Create tabbed pane for login
			JTabbedPane tabs = new JTabbedPane();
			tabs.addTab("Student Login", new StudentLoginPanel(conn, frame));
			tabs.addTab("Admin Login", new AdminLoginPanel(conn, frame));

			frame.add(tabs);
			frame.setVisible(true);

		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "JDBC Driver not found!");
			e.printStackTrace();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	});
}
}
