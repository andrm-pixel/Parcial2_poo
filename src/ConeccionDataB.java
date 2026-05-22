import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConeccionDataB {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("URL");
    private static final String USER = dotenv.get("USER");
    private static final String KEY = dotenv.get("KEY");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, KEY);
    }
}