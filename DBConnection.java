import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/student";
    private static final String USER = "student_user";
    private static final String PASSWORD = "studentpass";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Check your classpath.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
        return null;
    }
}