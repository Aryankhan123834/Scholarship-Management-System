// AdminDashboardPanel.java
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class AdminDashboardPanel extends JPanel {
private Connection conn;
private JFrame parentFrame;
private String adminName;

public AdminDashboardPanel(Connection conn, JFrame parentFrame, String adminName) {
	this.conn = conn;
	this.parentFrame = parentFrame;
	this.adminName = adminName;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout());

	JLabel welcomeLabel = new JLabel("Welcome, Admin " + adminName);
	add(welcomeLabel, BorderLayout.NORTH);

	JTabbedPane adminTabs = new JTabbedPane();
	adminTabs.addTab("Manage Scholarships", new ManageScholarshipsPanel(conn));
	adminTabs.addTab("View Applications", new ViewApplicationsPanel(conn));
	adminTabs.addTab("Manage Eligibility Rules", new ManageEligibilityPanel(conn));

	add(adminTabs, BorderLayout.CENTER);

	JButton logoutButton = new JButton("Logout");
	logoutButton.addActionListener(e -> logout());
	add(logoutButton, BorderLayout.SOUTH);
}

private void logout() {
	parentFrame.getContentPane().removeAll();
	JTabbedPane loginTabs = new JTabbedPane();
	loginTabs.addTab("Student Login", new StudentLoginPanel(conn, parentFrame));
	loginTabs.addTab("Admin Login", new AdminLoginPanel(conn, parentFrame));
	parentFrame.add(loginTabs);
	parentFrame.revalidate();
	parentFrame.repaint();
}
}