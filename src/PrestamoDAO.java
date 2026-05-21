import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // Necesitamos el LibroDAO para poder cambiar el estado del libro
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
            System.err.println("Error:No se pudo traer la lista de prestamos " + e.getMessage());
        }
        return lista;
    }

    // Busca un prestamo por su ID.
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
            System.err.println("Error: No se pudo buscar el prestamo con ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    // Busca préstamos por el nombre del lector.
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
            System.err.println("Error buscando prestamos de " + nombre + ": " + e.getMessage());
        }
        return lista;
    }

    // Registra un prestamo nuevo.
    public boolean adicionar(Prestamo p) {
        // Primero verificamos que el libro exista y este disponible
        Libro libro = libroDAO.consultarPorId(p.getIdLibro());
        if (libro == null) {
            System.err.println("[Aviso] El libro con ID " + p.getIdLibro() + " no existe.");
            return false;
        }
        if (!libro.isDisponible()) {
            System.err.println("[Aviso] El libro '" + libro.getTitulo() + "' ya esta prestado.");
            return false;
        }

        String sql = "INSERT INTO prestamo (id_libro, nombre_usuario, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?)";
        Connection con = null;

        try {
            con = ConeccionDataB.getConnection();
            // Desactivamos el guardado automatico para controlar la transaccion nosotros
            con.setAutoCommit(false);

            // Insertamos el registro del prestamo
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, p.getIdLibro());
                ps.setString(2, p.getNombreUsuario());
                ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));

                // La fecha de devolucion puede ser null (cuando el libro aun no se devuelve)
                if (p.getFechaDevolucion() == null) {
                    ps.setNull(4, Types.DATE);
                } else {
                    ps.setDate(4, Date.valueOf(p.getFechaDevolucion()));
                }
                ps.executeUpdate();
            }

            // Marcamos el libro como no disponible
            libroDAO.actualizarDisponibilidad(p.getIdLibro(), false, con);

            // Si ambas operaciones funcionaron, guardamos los cambios
            con.commit();
            return true;

        } catch (SQLException e) {
            // Si algo fallo, deshacemos todo lo que habiamos hecho
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("[Error] No se pudo deshacer la transaccion: " + ex.getMessage());
            }
            System.err.println("[Error] Fallo al registrar el prestamo: " + e.getMessage());
            return false;

        } finally {
            // Siempre volvemos a activar el guardado automatico
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("[Error] No se pudo restaurar autoCommit: " + ex.getMessage());
            }
        }
    }

    // Registra la devolucion de un libro.
    public boolean registrarDevolucion(int idLibro) {
        // Buscamos si hay un prestamo activo para ese libro (sin fecha de devolución)
        String sqlBuscar = "SELECT * FROM prestamo WHERE id_libro = ? AND fecha_devolucion IS NULL";
        Connection con = null;

        try {
            con = ConeccionDataB.getConnection();

            // Primero buscamos el prestamo activo
            Prestamo prestamoActivo = null;
            try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
                ps.setInt(1, idLibro);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        prestamoActivo = mapearPrestamo(rs);
                    }
                }
            }

            // Si no hay prestamo activo, no hay nada que devolver
            if (prestamoActivo == null) {
                System.err.println("El libro con ID " + idLibro + " no tiene un prestamo activo.");
                return false;
            }

            // Iniciamos la transaccion para hacer los dos cambios juntos
            con.setAutoCommit(false);

            // Registramos la fecha de devolucion de hoy en el prestamo
            String sqlActualizar = "UPDATE prestamo SET fecha_devolucion = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlActualizar)) {
                ps.setDate(1, Date.valueOf(LocalDate.now())); // La fecha de hoy
                ps.setInt(2, prestamoActivo.getId());
                ps.executeUpdate();
            }

            // Volvemos a marcar el libro como disponible
            libroDAO.actualizarDisponibilidad(idLibro, true, con);

            // Guardamos ambos cambios
            con.commit();
            System.out.println("Devolucion registrada. El libro esta disponible de nuevo.");
            return true;

        } catch (SQLException e) {
            // Si algo fallo, deshacemos los cambios
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("No se pudo deshacer la transaccion: " + ex.getMessage());
            }
            System.err.println("Fallo al registrar la devolucion: " + e.getMessage());
            return false;

        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("No se pudo restaurar autoCommit: " + ex.getMessage());
            }
        }
    }

    // Metodo que convierte una fila de la base de datos en un objeto Prestamo.
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        // La fecha de devolucion puede ser null en la base de datos
        Date fechaDevSQL = rs.getDate("fecha_devolucion");
        LocalDate fechaDev = (fechaDevSQL != null) ? fechaDevSQL.toLocalDate() : null;

        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_libro"),
                rs.getString("nombre_usuario"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                fechaDev
        );
    }
}