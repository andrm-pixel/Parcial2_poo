import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

// DAO = Data Access Object (Objeto de Acceso a Datos). Están todas las consultas SQL relacionadas con libros.
public class LibroDAO {

    // Trae todos los libros de la base de datos. Retorna una lista
    public List<Libro> consultarTodos() {
        List<Libro> lista = new ArrayList<>();

        // Consulta SQL para traer todos los libros ordenados por ID
        String sql = "SELECT * FROM libro ORDER BY id ASC";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Recorrer cada fila de la base de datos, Convierte cada fila en un objeto Libro y lo agrega a la lista
            while (rs.next()) {
                lista.add(mapearLibro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lo sentimos, ocurrió un error al consultar libros: " + e.getMessage());
        }
        return lista;
    }

    //Busca libro por ID
    public Libro consultarPorId(int id) {

        String sql = "SELECT * FROM libro WHERE id = ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            // Si encuentra un resultado, lo convierte en Libro
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Ocurrió un error al buscar libro con ID:  " + id + ": " + e.getMessage());
        }
        return null;
    }


    //Busca libros por título (búsqueda parcial)
    public List<Libro> consultarPorTitulo(String titulo) {
        List<Libro> lista = new ArrayList<>();

        // ILIKE para buscar sin importar mayúsculas o minúsculas
        String sql = "SELECT * FROM libro WHERE titulo ILIKE ?";

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
            System.err.println("Ocurrió un error buscando por titulo: " + e.getMessage());
        }
        return lista;
    }


    // Busca libros por nombre de autor (búsqueda parcial)
    public List<Libro> consultarPorAutor(String autor) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE autor ILIKE ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + autor + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ocurrió un error buscando por autor: " + e.getMessage());
        }
        return lista;
    }

    //Busca libros por género.
    public List<Libro> consultarPorGenero(String genero) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE genero ILIKE ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + genero + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ocurrió un error buscando por genero: " + e.getMessage());
        }
        return lista;
    }

    //Agrega un libro a la base de datos
    public boolean adicionar(Libro libro) {

        String sql = "INSERT INTO libro (titulo, autor, genero, año, disponible) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getGenero());
            ps.setInt(4, libro.getAño());
            ps.setBoolean(5, libro.isDisponible());

            // ps.executeUpdate retorna cuantas filas fueron afectadas
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lo sentimos, no se pudo agregar el libro: " + e.getMessage());
            return false;
        }
    }

    // Cambiar estado de disponibilidad de un libro
    public void actualizarDisponibilidad(int idLibro, boolean disponible, Connection con) throws SQLException {
        String sql = "UPDATE libro SET disponible = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, idLibro);
            ps.executeUpdate();
        }
    }


    // Método convierte una fila de la base de datos en un objeto Libro
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