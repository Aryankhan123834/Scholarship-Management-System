import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageScholarshipsPanel extends JPanel {
private Connection conn;
private DefaultListModel<Scholarship> listModel;
private JList<Scholarship> scholarshipList;

public ManageScholarshipsPanel(Connection conn) {
	this.conn = conn;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout(15, 10));
	setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

	JLabel headerLabel = new JLabel("Manage Scholarships", JLabel.CENTER);
	headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
	headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
	add(headerLabel, BorderLayout.NORTH);

	listModel = new DefaultListModel<>();
	scholarshipList = new JList<>(listModel);
	scholarshipList.setCellRenderer(new ScholarshipListRenderer());
	scholarshipList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	add(new JScrollPane(scholarshipList), BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
	JButton addButton = new JButton("➕ Add");
	JButton editButton = new JButton("✏️ Edit");
	JButton deleteButton = new JButton("🗑️ Delete");
	JButton refreshButton = new JButton("🔄 Refresh");

	addButton.addActionListener(e -> addScholarship());
	editButton.addActionListener(e -> editScholarship(scholarshipList.getSelectedValue()));
	deleteButton.addActionListener(e -> deleteScholarship(scholarshipList.getSelectedValue()));
	refreshButton.addActionListener(e -> refreshScholarships());

	buttonPanel.add(addButton);
	buttonPanel.add(editButton);
	buttonPanel.add(deleteButton);
	buttonPanel.add(refreshButton);
	add(buttonPanel, BorderLayout.SOUTH);

	// Initial load
	refreshScholarships();
}

private List<Scholarship> getAllScholarships() throws SQLException {
	List<Scholarship> scholarships = new ArrayList<>();
	String query = "SELECT * FROM Scholarship ORDER BY deadline ASC";
	try (Statement stmt = conn.createStatement();
		 ResultSet rs = stmt.executeQuery(query)) {
		while (rs.next()) {
			scholarships.add(new Scholarship(
					rs.getInt("scholarship_id"),
					rs.getString("name"),
					rs.getString("description"),
					rs.getDouble("amount"),
					rs.getDate("deadline"),
					rs.getString("requirements")
			));
		}
	}
	return scholarships;
}

private void addScholarship() {
	new ScholarshipDialog(conn, null).setVisible(true);
	refreshScholarships(); // Refresh after add
}

private void editScholarship(Scholarship scholarship) {
	if (scholarship != null) {
		new ScholarshipDialog(conn, scholarship).setVisible(true);
		refreshScholarships(); // Refresh after edit
	} else {
		JOptionPane.showMessageDialog(this, "Please select a scholarship to edit.");
	}
}

private void deleteScholarship(Scholarship scholarship) {
	if (scholarship == null) {
		JOptionPane.showMessageDialog(this, "Please select a scholarship to delete.");
		return;
	}

	int confirm = JOptionPane.showConfirmDialog(
			this,
			"Are you sure you want to delete '" + scholarship.getName() + "'?",
			"Confirm Deletion",
			JOptionPane.YES_NO_OPTION
	);

	if (confirm == JOptionPane.YES_OPTION) {
		try (PreparedStatement stmt = conn.prepareStatement(
				"DELETE FROM Scholarship WHERE scholarship_id = ?")) {
			stmt.setInt(1, scholarship.getScholarshipId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				JOptionPane.showMessageDialog(this, "Scholarship deleted successfully.");
				refreshScholarships();
			} else {
				JOptionPane.showMessageDialog(this, "Failed to delete scholarship.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
		}
	}
}

private void refreshScholarships() {
	try {
		listModel.clear();
		for (Scholarship s : getAllScholarships()) {
			listModel.addElement(s);
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error loading scholarships: " + ex.getMessage());
	}
}
}
