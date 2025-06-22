import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Date;

public class ScholarshipDialog extends JDialog {
private final Connection conn;
private final Scholarship scholarship;
private JTextField nameField, amountField, deadlineField;
private JTextArea descArea, reqArea;

public ScholarshipDialog(Connection conn, Scholarship scholarship) {
	this.conn = conn;
	this.scholarship = scholarship;
	initComponents();
}

private void initComponents() {
	setTitle(scholarship == null ? "➕ Add New Scholarship" : "✏️ Edit Scholarship");
	setSize(550, 430);
	setModal(true);
	setLocationRelativeTo(null); // Center dialog
	setLayout(new BorderLayout(15, 10));
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	// --- Title Label ---
	JLabel headerLabel = new JLabel(getTitle(), JLabel.CENTER);
	headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
	headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
	add(headerLabel, BorderLayout.NORTH);

	// --- Form Panel ---
	JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
	formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

	nameField = new JTextField(scholarship != null ? scholarship.getName() : "");
	amountField = new JTextField(scholarship != null ? String.valueOf(scholarship.getAmount()) : "");
	deadlineField = new JTextField(scholarship != null ? scholarship.getDeadline().toString() : "");

	descArea = new JTextArea(scholarship != null ? scholarship.getDescription() : "", 3, 20);
	descArea.setLineWrap(true);
	descArea.setWrapStyleWord(true);

	reqArea = new JTextArea(scholarship != null ? scholarship.getRequirements() : "", 3, 20);
	reqArea.setLineWrap(true);
	reqArea.setWrapStyleWord(true);

	formPanel.add(new JLabel("🏷️ Name:"));
	formPanel.add(nameField);

	formPanel.add(new JLabel("💰 Amount:"));
	formPanel.add(amountField);

	formPanel.add(new JLabel("📅 Deadline (YYYY-MM-DD):"));
	formPanel.add(deadlineField);

	formPanel.add(new JLabel("📝 Description:"));
	formPanel.add(new JScrollPane(descArea));

	formPanel.add(new JLabel("📌 Requirements:"));
	formPanel.add(new JScrollPane(reqArea));

	add(formPanel, BorderLayout.CENTER);

	// --- Button Panel ---
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	JButton saveButton = new JButton("💾 Save");
	JButton cancelButton = new JButton("❌ Cancel");

	saveButton.addActionListener(e -> saveScholarship());
	cancelButton.addActionListener(e -> dispose());

	buttonPanel.add(cancelButton);
	buttonPanel.add(saveButton);

	add(buttonPanel, BorderLayout.SOUTH);
}

private void saveScholarship() {
	try {
		String name = nameField.getText().trim();
		String amountText = amountField.getText().trim();
		String deadlineText = deadlineField.getText().trim();
		String description = descArea.getText().trim();
		String requirements = reqArea.getText().trim();

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "⚠️ Scholarship name is required.");
			return;
		}

		double amount;
		try {
			amount = Double.parseDouble(amountText);
			if (amount <= 0) throw new NumberFormatException();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "⚠️ Enter a valid positive amount.");
			return;
		}

		Date deadline;
		try {
			deadline = Date.valueOf(deadlineText);
			if (deadline.before(new Date(System.currentTimeMillis()))) {
				JOptionPane.showMessageDialog(this, "⚠️ Deadline must be a future date.");
				return;
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, "⚠️ Invalid date format. Use YYYY-MM-DD.");
			return;
		}

		if (scholarship == null) {
			// Insert new scholarship
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO Scholarship (name, description, amount, deadline, requirements) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS
			);

			stmt.setString(1, name);
			stmt.setString(2, description);
			stmt.setDouble(3, amount);
			stmt.setDate(4, deadline);
			stmt.setString(5, requirements);

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				JOptionPane.showMessageDialog(this, "✅ Scholarship added successfully!");
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "❌ Failed to add scholarship.");
			}
		} else {
			// Update existing scholarship
			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE Scholarship SET name = ?, description = ?, amount = ?, deadline = ?, requirements = ? WHERE scholarship_id = ?"
			);

			stmt.setString(1, name);
			stmt.setString(2, description);
			stmt.setDouble(3, amount);
			stmt.setDate(4, deadline);
			stmt.setString(5, requirements);
			stmt.setInt(6, scholarship.getScholarshipId());

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				JOptionPane.showMessageDialog(this, "✅ Scholarship updated successfully!");
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "❌ Failed to update scholarship.");
			}
		}

	} catch (SQLException ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, "❗ Database error: " + ex.getMessage());
	}
}
}
