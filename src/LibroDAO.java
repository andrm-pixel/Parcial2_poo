import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

// DAO = Data Access Object (Objeto de Acceso a Datos). Aquí están todas las consultas SQL relacionadas con libros.
public class LibroDAO {

    // Trae TODOS los libros de la base de datos. Retorna una lista
    public List<Libro> consultarTodos() {
        List<Libro> lista = new ArrayList<>();

        // Consulta SQL para traer todos los libros ordenados por id
        String sql = "SELECT * FROM libros ORDER BY id ASC";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Recorremos cada fila que nos devolvió la base de datos
            while (rs.next()) {
                // Convertimos cada fila en un objeto Libro y lo agregamos a la lista
                lista.add(mapearLibro(rs));
            }
        } catch (SQLException e) {
            System.err.println("[Error] No se pudo traer la lista de libros: " + e.getMessage());
        }
        return lista;
    }

    //Busca un libro por su ID y si no lo encuentra, retorna null
    public Libro consultarPorId(int id) {

        String sql = "SELECT * FROM libros WHERE id = ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            // Si encontramos un resultado, lo convertimos en Libro
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }

        // Si no se encuentra, retornamos null
        } catch (SQLException e) {
            System.err.println("[Error] No se pudo buscar el libro con ID " + id + ": " + e.getMessage());
        }
        return null;
    }


    //Busca libros por título (búsqueda parcial). Ej: si buscas "cien" encontrará "Cien Años de Soledad".
    public List<Libro> consultarPorTitulo(String titulo) {
        List<Libro> lista = new ArrayList<>();

        // ILIKE para buscar sin importar mayúsculas o minúsculas
        String sql = "SELECT * FROM libros WHERE titulo ILIKE ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // El % al principio y al final permite búsqueda parcial del título
            ps.setString(1, "%" + titulo + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[Error] Error buscando por titulo: " + e.getMessage());
        }
        return lista;
    }


    // Busca libros por nombre de autor (búsqueda parcial)
    public List<Libro> consultarPorAutor(String autor) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE autor ILIKE ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + autor + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[Error] Error buscando por autor: " + e.getMessage());
        }
        return lista;
    }

    //Busca libros por género.
    public List<Libro> consultarPorGenero(String genero) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE genero ILIKE ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + genero + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[Error] Error buscando por genero: " + e.getMessage());
        }
        return lista;
    }

    //Agrega un libro nuevo a la base de datos. Retorna true si se guardo bien, false si hubo un error.
    public boolean adicionar(Libro libro) {

        // No incluimos el 'id' porque la base de datos lo genera automáticamente (SERIAL)
        String sql = "INSERT INTO libros (titulo, autor, genero, anio, disponible) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Colocamos cada dato en su lugar correspondiente
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getGenero());
            ps.setInt(4, libro.getAño());
            ps.setBoolean(5, libro.isDisponible());

            // executeUpdate retorna cuantas filas fueron afectadas
            // Si es mayor a 0, significa que se insertó bien
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Error] No se pudo agregar el libro: " + e.getMessage());
            return false;
        }
    }

    // Cambia el estado de disponibilidad de un libro
    public void actualizarDisponibilidad(int idLibro, boolean disponible, Connection con) throws SQLException {
        String sql = "UPDATE libros SET disponible = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, idLibro);
            ps.executeUpdate();
        }
    }


    // Método que convierte una fila de la base de datos en un objeto Libro
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("genero"),
                rs.getInt("año"),
                rs.getBoolean("disponible")
        );
    }
}