import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;

public class ApplicationDialog extends JDialog {
private Connection conn;
private Student student;
private Scholarship scholarship;

public ApplicationDialog(Connection conn, Student student, Scholarship scholarship) {
	this.conn = conn;
	this.student = student;
	this.scholarship = scholarship;
	initComponents();
}

private void initComponents() {
	setTitle("Apply for " + scholarship.getName());
	setSize(500, 400);
	setModal(true);
	setLayout(new BorderLayout());

	JPanel infoPanel = new JPanel(new GridLayout(0, 1, 10, 10));
	infoPanel.add(new JLabel("Scholarship: " + scholarship.getName()));
	infoPanel.add(new JLabel("Amount: $" + scholarship.getAmount()));
	infoPanel.add(new JLabel("Deadline: " + scholarship.getDeadline()));
	infoPanel.add(new JLabel("Requirements: " + scholarship.getRequirements()));

	JTextArea documentsArea = new JTextArea(5, 20);
	documentsArea.setLineWrap(true);
	JScrollPane documentsScroll = new JScrollPane(documentsArea);

	JPanel documentsPanel = new JPanel(new BorderLayout());
	documentsPanel.add(new JLabel("Documents (list what you're attaching):"), BorderLayout.NORTH);
	documentsPanel.add(documentsScroll, BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();
	JButton submitButton = new JButton("Submit Application");
	JButton cancelButton = new JButton("Cancel");

	submitButton.addActionListener(e -> submitApplication(documentsArea.getText()));
	cancelButton.addActionListener(e -> dispose());

	buttonPanel.add(submitButton);
	buttonPanel.add(cancelButton);

	add(infoPanel, BorderLayout.NORTH);
	add(documentsPanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
}

private void submitApplication(String documents) {
	try {
		PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO Application (student_id, scholarship_id, status, date_applied, documents) " +
						"VALUES (?, ?, 'Pending', ?, ?)"
		);

		stmt.setInt(1, student.getStudentId());
		stmt.setInt(2, scholarship.getScholarshipId());
		stmt.setDate(3, new java.sql.Date(new Date().getTime()));
		stmt.setString(4, documents);

		int affectedRows = stmt.executeUpdate();

		if (affectedRows > 0) {
			JOptionPane.showMessageDialog(this, "Application submitted successfully!");
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Failed to submit application");
		}
	} catch (SQLException e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
	}
}
}