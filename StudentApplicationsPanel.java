import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentApplicationsPanel extends JPanel {
private final Connection conn;
private final Student student;
private JTable applicationsTable;
private DefaultTableModel tableModel;

public StudentApplicationsPanel(Connection conn, Student student) {
	this.conn = conn;
	this.student = student;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout(10, 10));
	setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	JLabel titleLabel = new JLabel("📋 Your Scholarship Applications", JLabel.CENTER);
	titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
	add(titleLabel, BorderLayout.NORTH);

	// Setup table
	tableModel = new DefaultTableModel(new Object[]{"Scholarship", "Amount", "Date Applied", "Status"}, 0) {
		public boolean isCellEditable(int row, int column) {
			return false; // All cells read-only
		}
	};

	applicationsTable = new JTable(tableModel);
	applicationsTable.setRowHeight(25);
	applicationsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

	JScrollPane scrollPane = new JScrollPane(applicationsTable);
	add(scrollPane, BorderLayout.CENTER);

	// Refresh button
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	JButton refreshButton = new JButton("🔄 Refresh");
	refreshButton.addActionListener(e -> refreshApplications());
	buttonPanel.add(refreshButton);
	add(buttonPanel, BorderLayout.SOUTH);

	// Initial load
	refreshApplications();
}

private void refreshApplications() {
	try {
		List<Application> applications = getStudentApplications();
		tableModel.setRowCount(0); // Clear old data

		for (Application app : applications) {
			Scholarship sch = getScholarshipById(app.getScholarshipId());
			if (sch != null) {
				tableModel.addRow(new Object[]{
						sch.getName(),
						sch.getAmount(),
						app.getDateApplied(),
						app.getStatus()
				});
			}
		}

	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "⚠️ Error loading applications:\n" + ex.getMessage());
	}
}

private List<Application> getStudentApplications() throws SQLException {
	List<Application> applications = new ArrayList<>();

	PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Application WHERE student_id = ?");
	stmt.setInt(1, student.getStudentId());
	ResultSet rs = stmt.executeQuery();

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
