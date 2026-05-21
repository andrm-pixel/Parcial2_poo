import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) throws SQLException {

        String connection_key= "";

        Connection conn = DriverManager.getConnection(connection_key);
        System.out.println("Exitos "+ conn.getMetaData().getDatabaseProductVersion());

    }

}