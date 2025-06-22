public class Student // Student.java
					{
private int StudentId;
private String name;
private String email;
private String password;
private double gpa;
private String major;

public Student(int studentId, String name, String email, String password, double gpa, String major) {
	this.StudentId = studentId;
	this.name = name;
	this.email = email;
	this.password = password;
	this.gpa = gpa;
	this.major = major;
}

// Getters and Setters
public int getStudentId() { return StudentId; }
public String getName() { return name; }
public String getEmail() { return email; }
public String getPassword() { return password; }
public double getGpa() { return gpa; }
public String getMajor() { return major; }

public void setPassword(String password) { this.password = password; }
public void setGpa(double gpa) { this.gpa = gpa; }
public void setMajor(String major) { this.major = major; }
}

