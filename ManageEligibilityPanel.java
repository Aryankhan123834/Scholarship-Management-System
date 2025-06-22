import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageEligibilityPanel extends JPanel {
private Connection conn;

public ManageEligibilityPanel(Connection conn) {
	this.conn = conn;
	initComponents();
}

private void initComponents() {
	setLayout(new BorderLayout(10, 10));
	setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40)); // Less padding

	JLabel title = new JLabel("Manage Eligibility Rules", JLabel.CENTER);
	title.setFont(new Font("Segoe UI", Font.BOLD, 18));
	title.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
	add(title, BorderLayout.NORTH);

	try {
		List<Scholarship> scholarships = getAllScholarships();
		JComboBox<Scholarship> scholarshipCombo = new JComboBox<>(scholarships.toArray(new Scholarship[0]));

		JPanel formPanel = new JPanel();
		formPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0; gbc.gridy = 0;
		formPanel.add(new JLabel("🎓 Scholarship:"), gbc);
		gbc.gridx = 1;
		formPanel.add(scholarshipCombo, gbc);

		gbc.gridx = 0; gbc.gridy++;
		formPanel.add(new JLabel("📊 Min GPA (optional):"), gbc);
		JTextField gpaField = new JTextField(15);
		gbc.gridx = 1;
		formPanel.add(gpaField, gbc);

		gbc.gridx = 0; gbc.gridy++;
		formPanel.add(new JLabel("📚 Major (optional):"), gbc);
		JTextField majorField = new JTextField(15);
		gbc.gridx = 1;
		formPanel.add(majorField, gbc);

		add(formPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JButton loadButton = new JButton("🔍 Load");
		JButton saveButton = new JButton("💾 Save");

		loadButton.addActionListener(e -> {
			Scholarship selected = (Scholarship) scholarshipCombo.getSelectedItem();
			if (selected != null) {
				loadExistingRules(selected, gpaField, majorField);
			}
		});

		saveButton.addActionListener(e -> {
			Scholarship selected = (Scholarship) scholarshipCombo.getSelectedItem();
			if (selected != null) {
				saveEligibilityRules(selected, gpaField.getText().trim(), majorField.getText().trim());
			}
		});

		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);
		add(buttonPanel, BorderLayout.SOUTH);

	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "❌ Error loading scholarships: " + ex.getMessage());
	}
}

private List<Scholarship> getAllScholarships() throws SQLException {
	List<Scholarship> scholarships = new ArrayList<>();
	String query = "SELECT * FROM Scholarship ORDER BY name ASC";
	try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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

private void saveEligibilityRules(Scholarship scholarship, String minGpaStr, String majorReq) {
	try {
		try (PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM Eligibility_Rule WHERE scholarship_id = ?")) {
			deleteStmt.setInt(1, scholarship.getScholarshipId());
			deleteStmt.executeUpdate();
		}

		try (PreparedStatement insertStmt = conn.prepareStatement(
				"INSERT INTO Eligibility_Rule (scholarship_id, min_gpa, major_req) VALUES (?, ?, ?)")) {
			insertStmt.setInt(1, scholarship.getScholarshipId());

			if (minGpaStr.isEmpty()) {
				insertStmt.setNull(2, Types.DECIMAL);
			} else {
				insertStmt.setDouble(2, Double.parseDouble(minGpaStr));
			}

			if (majorReq.isEmpty()) {
				insertStmt.setNull(3, Types.VARCHAR);
			} else {
				insertStmt.setString(3, majorReq);
			}

			int affected = insertStmt.executeUpdate();
			JOptionPane.showMessageDialog(this,
					affected > 0 ? "✅ Rules saved." : "⚠️ No rules saved.");
		}
	} catch (NumberFormatException ex) {
		JOptionPane.showMessageDialog(this, "⚠️ Invalid GPA format.");
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "❌ DB Error: " + ex.getMessage());
	}
}

private void loadExistingRules(Scholarship scholarship, JTextField gpaField, JTextField majorField) {
	try (PreparedStatement stmt = conn.prepareStatement(
			"SELECT * FROM Eligibility_Rule WHERE scholarship_id = ?")) {
		stmt.setInt(1, scholarship.getScholarshipId());
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			double minGpa = rs.getDouble("min_gpa");
			gpaField.setText(rs.wasNull() ? "" : String.valueOf(minGpa));
			majorField.setText(rs.getString("major_req") != null ? rs.getString("major_req") : "");
			JOptionPane.showMessageDialog(this, "✅ Rules loaded.");
		} else {
			gpaField.setText("");
			majorField.setText("");
			JOptionPane.showMessageDialog(this, "ℹ️ No rules found.");
		}
	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "❌ DB Error: " + ex.getMessage());
	}
}
}
