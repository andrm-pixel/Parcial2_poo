import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) throws SQLException {

        String connection_key = "jdbc:postgresql://ep-little-morning-apj4q2k9-pooler.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";

        Connection conn = DriverManager.getConnection(connection_key, "neondb_owner", "npg_fO1EnRxSs3ro");
        System.out.println("Exito: " + conn.getMetaData().getDatabaseProductVersion());
        conn.close();
    }
}