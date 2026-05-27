import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConeccionDataB {

    private static final String URL = System.getenv("URL");
    private static final String USER = System.getenv("USER");
    private static final String KEY = System.getenv("KEY");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, KEY);
    }
}