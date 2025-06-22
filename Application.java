// Application.java
import java.sql.Date;

public class Application {
private int applicationId;
private int studentId;
private int scholarshipId;
private String status;
private Date dateApplied;
private String documentsPath;

public Application(int applicationId, int studentId, int scholarshipId, String status, Date dateApplied, String documentsPath) {
	this.applicationId = applicationId;
	this.studentId = studentId;
	this.scholarshipId = scholarshipId;
	this.status = status;
	this.dateApplied = dateApplied;
	this.documentsPath = documentsPath;
}

// Getters
public int getApplicationId() { return applicationId; }
public int getStudentId() { return studentId; }
public int getScholarshipId() { return scholarshipId; }
public String getStatus() { return status; }
public Date getDateApplied() { return dateApplied; }
public String getDocumentsPath() { return documentsPath; }
}