// StudentDashboardPanel.java
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class StudentDashboardPanel extends JPanel {
private Connection conn;
private JFrame parentFrame;
private Student student;

public StudentDashboardPanel(Connection conn, JFrame parentFrame, Student student) {
	this.conn = conn;
	this.parentFrame = parentFrame;
	this.student = student;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout());

	JLabel welcomeLabel = new JLabel("Welcome, " + student.getName() + " | GPA: " + student.getGpa() + " | Major: " + student.getMajor());
	add(welcomeLabel, BorderLayout.NORTH);

	JTabbedPane studentTabs = new JTabbedPane();
	studentTabs.addTab("Available Scholarships", new AvailableScholarshipsPanel(conn, student));
	studentTabs.addTab("My Applications", new StudentApplicationsPanel(conn, student));
	studentTabs.addTab("Profile", new StudentProfilePanel(conn, student, parentFrame));

	add(studentTabs, BorderLayout.CENTER);

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