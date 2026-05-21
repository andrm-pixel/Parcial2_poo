import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // LibroDAO para poder cambiar el estado del libro
    private final LibroDAO libroDAO = new LibroDAO();

    // Trae todos los préstamos de la base de datos
    public List<Prestamo> consultarTodos() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamo ORDER BY id ASC";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lo sentimos, ocurrió un error al traer la lista de prestamos" + e.getMessage());
        }
        return lista;
    }

    // Buscar prestamo por ID.
    public Prestamo consultarPorId(int id) {
        String sql = "SELECT * FROM prestamo WHERE id = ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPrestamo(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lo sentimos, no se pudo buscar el préstamo con ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    // Buscar préstamos por nombre del lector
    public List<Prestamo> consultarPorNombre(String nombre) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamo WHERE nombre_usuario ILIKE ?";

        try (Connection con = ConeccionDataB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrestamo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lo sentimos, ocurrió un error buscando prestamos de " + nombre + ": " + e.getMessage());
        }
        return lista;
    }

    // Registrar prestamo
    public boolean adicionar(Prestamo p) {
        // Verificamos que el libro exista y esté disponible
        Libro libro = libroDAO.consultarPorId(p.getIdLibro());
        if (libro == null) {
            System.err.println("El libro con ID " + p.getIdLibro() + " no existe");
            return false;
        }
        if (!libro.isDisponible()) {
            System.err.println("Lo sentimos, el libro '" + libro.getTitulo() + "' ya ha sido prestado");
            return false;
        }

        String sql = "INSERT INTO prestamo (id_libro, nombre_usuario, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?)";
        Connection con = null;

        try {
            con = ConeccionDataB.getConnection();
            // Desactiva el guardado automático para controlar la transacción nosotros
            con.setAutoCommit(false);

            // Inserta el registro del prestamo
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, p.getIdLibro());
                ps.setString(2, p.getNombreUsuario());
                ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));

                if (p.getFechaDevolucion() == null) {
                    ps.setNull(4, Types.DATE);
                } else {
                    ps.setDate(4, Date.valueOf(p.getFechaDevolucion()));
                }
                ps.executeUpdate();
            }

            // Marca el libro como no disponible
            libroDAO.actualizarDisponibilidad(p.getIdLibro(), false, con);

            // Guardamos los cambios
            con.commit();
            return true;

        } catch (SQLException e) {
            // Si algo falla, deshacemos todo
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("Lo sentimos, no se pudo deshacer la transacción: " + ex.getMessage());
            }
            System.err.println("Lo sentimos, ocurrió un error al registrar el préstamo: " + e.getMessage());
            return false;

        }

            // Volvemos a activar el autoguardado
            finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Lo sentimos, no se pudo restaurar autoCommit: " + ex.getMessage());
            }
        }
    }

    // Registrar devolución de un libro
    public boolean registrarDevolucion(int idLibro) {

        // Buscamos si hay un prestamo activo para el libro
        String sqlBuscar = "SELECT * FROM prestamo WHERE id_libro = ? AND fecha_devolucion IS NULL";
        Connection con = null;

        try {
            con = ConeccionDataB.getConnection();

            // Busca el prestamo activo
            Prestamo prestamoActivo = null;
            try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
                ps.setInt(1, idLibro);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        prestamoActivo = mapearPrestamo(rs);
                    }
                }
            }

            // Si no hay nada que devolver
            if (prestamoActivo == null) {
                System.err.println("El libro con ID " + idLibro + " no tiene  prestamos activos");
                return false;
            }

            con.setAutoCommit(false);

            // Registrar fecha devolución del prestamo (Hoy)
            String sqlActualizar = "UPDATE prestamo SET fecha_devolucion = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlActualizar)) {
                ps.setDate(1, Date.valueOf(LocalDate.now())); // La fecha de hoy
                ps.setInt(2, prestamoActivo.getId());
                ps.executeUpdate();
            }

            // Volver a marcar el libro disponible
            libroDAO.actualizarDisponibilidad(idLibro, true, con);

            // Guardar cambios
            con.commit();
            System.out.println("La devolución ha sido registrada con éxito!! El libro esta disponible de nuevo");
            return true;

        }

            // Si algo falla, deshace los cambios
            catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("Lo sentimos, no se pudo deshacer la transacción: " + ex.getMessage());
            }
            System.err.println("Lo sentimos, ocurrió un error al registrar la devolución: " + e.getMessage());
            return false;

        }

            // Volvemos a activar el autoguardado
            finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Lo sentimos,no se pudo restaurar autoCommit: " + ex.getMessage());
            }
        }
    }

    // Método que convierte una fila de la base de datos en un objeto Prestamo
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {

        // La fecha de devolución puede ser null en la base de datos
        Date fechaDevSQL = rs.getDate("fecha_devolución");
        LocalDate fechaDev = (fechaDevSQL != null) ? fechaDevSQL.toLocalDate() : null;

        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_libro"),
                rs.getString("nombre_usuario"),
                rs.getDate("fecha_préstamo").toLocalDate(),
                fechaDev
        );
    }
}