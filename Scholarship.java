// Scholarship.java
import java.sql.Date;

public class Scholarship {
private int scholarshipId;
private String name;
private String description;
private double amount;
private Date deadline;
private String requirements;

public Scholarship(int scholarshipId, String name, String description, double amount, Date deadline, String requirements) {
	this.scholarshipId = scholarshipId;
	this.name = name;
	this.description = description;
	this.amount = amount;
	this.deadline = deadline;
	this.requirements = requirements;
}

// Getters
public int getScholarshipId() { return scholarshipId; }
public String getName() { return name; }
public String getDescription() { return description; }
public double getAmount() { return amount; }
public Date getDeadline() { return deadline; }
public String getRequirements() { return requirements; }

@Override
public String toString() {
	return name + " ($" + amount + ") - Deadline: " + deadline;
}
}