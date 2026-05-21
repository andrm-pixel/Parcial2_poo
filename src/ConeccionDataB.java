import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConeccionDataB {
    private static final String USER = "neondb_owner";
    private static final String KEY = "npg_fO1EnRxSs3ro";
    private static final String URL = "jdbc:postgresql://ep-little-morning-apj4q2k9-pooler.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, KEY);
    }
}