import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionProvider {
    public static Connection getCon(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/createprofile?useSSL=false", "root", "Root");
            return con;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
