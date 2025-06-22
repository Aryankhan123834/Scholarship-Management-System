import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

public class ScholarshipSystem {
public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> {
		try {
			Connection conn = Databaseconnector.getConnection();  // Ensure class name matches actual filename

			JFrame frame = new JFrame("🎓 Scholarship Management System");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(900, 600);
			frame.setLocationRelativeTo(null); // Center the window

			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.addTab("👨‍🎓 Student Login", new StudentLoginPanel(conn, frame));
			tabbedPane.addTab("🛡️ Admin Login", new AdminLoginPanel(conn, frame));

			frame.add(tabbedPane);
			frame.setVisible(true);

			// Gracefully close DB connection on exit
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					Databaseconnector.closeConnection(conn);
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"❌ Database connection failed:\n" + e.getMessage(),
					"Connection Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	});
}
}
