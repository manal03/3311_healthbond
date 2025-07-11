import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/nutrition_app?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";             // your MySQL username
    private static final String PASSWORD = "Balwinder123/"; // your MySQL password

    public static Connection getCon() {
        try {
            // Load the MySQL JDBC driver (optional in newer versions)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Attempt to establish a connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection successful!");
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed.");
            e.printStackTrace();
        }
        return null;
    }
}
    
