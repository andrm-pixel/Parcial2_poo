import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class TestConnection {

    public static void main(String[] args) throws SQLException {
        // Lee las credenciales del .env, igual que ConeccionDataB
        Dotenv dotenv = Dotenv.load();

        String url  = dotenv.get("URL");
        String user = dotenv.get("USER");
        String key  = dotenv.get("KEY");

        Connection conn = DriverManager.getConnection(url, user, key);
        System.out.println("Conexión exitosa: " + conn.getMetaData().getDatabaseProductVersion());
        conn.close();
    }
}