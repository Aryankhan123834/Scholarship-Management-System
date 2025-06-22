// DatabaseConnector.java
import java.sql.*;

public class Databaseconnector {
private static final String DB_URL = "jdbc:mysql://localhost:3306/scholarship_db";
private static final String DB_USER = "root";
private static final String DB_PASS = "Aryan22468";

public static Connection getConnection() throws SQLException {
	try {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
	} catch (ClassNotFoundException e) {
		throw new SQLException("Database driver not found", e);
	}
}

public static void closeConnection(Connection conn) {
	if (conn != null) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
}