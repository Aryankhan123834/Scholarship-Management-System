import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewApplicationsPanel extends JPanel {
private final Connection conn;
private JTable applicationsTable;
private DefaultTableModel tableModel;

public ViewApplicationsPanel(Connection conn) {
	this.conn = conn;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout(10, 10));
	setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	JLabel titleLabel = new JLabel("📄 All Scholarship Applications", JLabel.CENTER);
	titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
	add(titleLabel, BorderLayout.NORTH);

	tableModel = new DefaultTableModel(new Object[]{"ID", "Student", "Scholarship", "Amount", "Date Applied", "Status"}, 0) {
		public boolean isCellEditable(int row, int column) {
			return false; // Disable editing
		}
	};

	applicationsTable = new JTable(tableModel);
	applicationsTable.setRowHeight(25);
	applicationsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

	JScrollPane scrollPane = new JScrollPane(applicationsTable);
	add(scrollPane, BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	JButton approveButton = new JButton("✅ Approve Selected");
	JButton rejectButton = new JButton("❌ Reject Selected");
	JButton refreshButton = new JButton("🔄 Refresh");

	approveButton.addActionListener(e -> updateApplicationStatus("Approved"));
	rejectButton.addActionListener(e -> updateApplicationStatus("Rejected"));
	refreshButton.addActionListener(e -> refreshApplications());

	buttonPanel.add(approveButton);
	buttonPanel.add(rejectButton);
	buttonPanel.add(refreshButton);
	add(buttonPanel, BorderLayout.SOUTH);

	refreshApplications(); // Initial load
}

private void refreshApplications() {
	try {
		List<Application> applications = getAllApplications();
		tableModel.setRowCount(0); // Clear old data

		for (Application app : applications) {
			Student student = getStudentById(app.getStudentId());
			Scholarship scholarship = getScholarshipById(app.getScholarshipId());

			if (student != null && scholarship != null) {
				tableModel.addRow(new Object[]{
						app.getApplicationId(),
						student.getName(),
						scholarship.getName(),
						scholarship.getAmount(),
						app.getDateApplied(),
						app.getStatus()
				});
			}
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error loading applications:\n" + ex.getMessage());
	}
}

private void updateApplicationStatus(String status) {
	int selectedRow = applicationsTable.getSelectedRow();
	if (selectedRow == -1) {
		JOptionPane.showMessageDialog(this, "Please select an application first.");
		return;
	}

	int applicationId = (int) tableModel.getValueAt(selectedRow, 0);

	try {
		PreparedStatement stmt = conn.prepareStatement(
				"UPDATE Application SET status = ? WHERE application_id = ?"
		);
		stmt.setString(1, status);
		stmt.setInt(2, applicationId);

		int affectedRows = stmt.executeUpdate();
		if (affectedRows > 0) {
			JOptionPane.showMessageDialog(this, "Application updated to '" + status + "'");
			refreshApplications();
		} else {
			JOptionPane.showMessageDialog(this, "Failed to update application.");
		}
	} catch (SQLException e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
	}
}

private List<Application> getAllApplications() throws SQLException {
	List<Application> applications = new ArrayList<>();
	Statement stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM Application");

	while (rs.next()) {
		applications.add(new Application(
				rs.getInt("application_id"),
				rs.getInt("student_id"),
				rs.getInt("scholarship_id"),
				rs.getString("status"),
				rs.getDate("date_applied"),
				rs.getString("documents")
		));
	}

	return applications;
}

private Student getStudentById(int studentId) throws SQLException {
	PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Student WHERE student_id = ?");
	stmt.setInt(1, studentId);
	ResultSet rs = stmt.executeQuery();

	if (rs.next()) {
		return new Student(
				rs.getInt("student_id"),
				rs.getString("name"),
				rs.getString("email"),
				rs.getString("password"),
				rs.getDouble("gpa"),
				rs.getString("major")
		);
	}
	return null;
}

private Scholarship getScholarshipById(int scholarshipId) throws SQLException {
	PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Scholarship WHERE scholarship_id = ?");
	stmt.setInt(1, scholarshipId);
	ResultSet rs = stmt.executeQuery();

	if (rs.next()) {
		return new Scholarship(
				rs.getInt("scholarship_id"),
				rs.getString("name"),
				rs.getString("description"),
				rs.getDouble("amount"),
				rs.getDate("deadline"),
				rs.getString("requirements")
		);
	}
	return null;
}
}
