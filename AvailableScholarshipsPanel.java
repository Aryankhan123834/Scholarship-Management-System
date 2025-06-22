import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvailableScholarshipsPanel extends JPanel {
private Connection conn;
private Student student;

public AvailableScholarshipsPanel(Connection conn, Student student) {
	this.conn = conn;
	this.student = student;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout());

	try {
		List<Scholarship> scholarships = getEligibleScholarships();

		DefaultListModel<Scholarship> listModel = new DefaultListModel<>();
		for (Scholarship s : scholarships) {
			listModel.addElement(s);
		}

		JList<Scholarship> scholarshipList = new JList<>(listModel);
		scholarshipList.setCellRenderer(new ScholarshipListRenderer());

		JScrollPane scrollPane = new JScrollPane(scholarshipList);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		JButton applyButton = new JButton("Apply");
		JButton refreshButton = new JButton("Refresh");

		applyButton.addActionListener(e -> {
			Scholarship selected = scholarshipList.getSelectedValue();
			if (selected != null) {
				new ApplicationDialog(conn, student, selected).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Please select a scholarship first");
			}
		});

		refreshButton.addActionListener(e -> refreshScholarships(scholarshipList));

		buttonPanel.add(applyButton);
		buttonPanel.add(refreshButton);
		add(buttonPanel, BorderLayout.SOUTH);
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error loading scholarships: " + ex.getMessage());
	}
}

private List<Scholarship> getEligibleScholarships() throws SQLException {
	List<Scholarship> eligible = new ArrayList<>();

	String query = "SELECT s.* FROM Scholarship s " +
			"JOIN Eligibility_Rule e ON s.scholarship_id = e.scholarship_id " +
			"WHERE (e.min_gpa IS NULL OR e.min_gpa <= ?) " +
			"AND (e.major_req IS NULL OR e.major_req = ?) " +
			"AND s.deadline >= CURRENT_DATE()";

	PreparedStatement stmt = conn.prepareStatement(query);
	stmt.setDouble(1, student.getGpa());
	stmt.setString(2, student.getMajor());

	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
		eligible.add(new Scholarship(
				rs.getInt("scholarship_id"),
				rs.getString("name"),
				rs.getString("description"),
				rs.getDouble("amount"),
				rs.getDate("deadline"),
				rs.getString("requirements")
		));
	}

	return eligible;
}

private void refreshScholarships(JList<Scholarship> scholarshipList) {
	try {
		List<Scholarship> scholarships = getEligibleScholarships();
		DefaultListModel<Scholarship> model = (DefaultListModel<Scholarship>) scholarshipList.getModel();
		model.clear();
		for (Scholarship s : scholarships) {
			model.addElement(s);
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error refreshing scholarships: " + ex.getMessage());
	}
}
}