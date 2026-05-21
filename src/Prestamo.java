import java.time.LocalDate;

public class Prestamo {
    private Integer id;
    private Integer id_libro;
    private String nombre;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    //constructor
    public Prestamo(Integer id, Integer id_libro, String nombre, LocalDate fechaPrestamo,LocalDate fechaDevolucion) {
        this.id = id;
        this.id_libro = id_libro;
        this.nombre = nombre;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    // Getters y Setters
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
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {

        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {

        this.fechaDevolucion = fechaDevolucion;
    }

    public int getIdLibro() {

        return id_libro;
    }

    public String getNombreUsuario() {
        return nombre;
    }

    @Override
    public String toString() {
        String dev = (fechaDevolucion != null) ? fechaDevolucion.toString() : "Pendiente";
        return "Préstamo #" + id + " | Libro ID: " + id_libro + " | Lector: " + nombre + " | Prestado: " + fechaPrestamo + " | Devolución: " + dev;
    }
}