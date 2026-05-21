import java.time.LocalDate;

public class Prestamo {
    private Integer id;
    private Integer id_libro;
    private String nombre;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    public Prestamo(Integer id, Integer id_libro, String nombre, LocalDate fechaPrestamo,LocalDate fechaDevolucion) {
        this.id = id;
        this.id_libro = id_libro;
        this.nombre = nombre;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    public Prestamo() {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_libro() {
        return id_libro;
    }
    public void setId_libro(Integer id_libro) {
        this.id_libro = id_libro;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getIdLibro() {
        return 0;
    }

    public String getNombreUsuario() {
        return null;
    }
}