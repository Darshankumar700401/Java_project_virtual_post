import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    public static Connection getConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database details
            String url = "jdbc:mysql://localhost:5555/dhachu"; // ✅ your DB
            String user = "root";
            String password = "mkce";

            // Return a NEW connection each time
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected!");
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("Failed to connect to database!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
