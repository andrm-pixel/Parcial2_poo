public class Libro {
    private Integer id;
    private String titulo;
    private String autor;
    private String genero;
    private Integer año;
    private Boolean disponible;

    //constructor
    public Libro(Integer id, Integer año, String titulo, String autor, String genero, boolean disponible) {
        this.id = id;
        this.año = año;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.disponible = disponible;

    }
    // Getters y Setters
    public Libro(int id, String titulo, String autor, String genero, int año, boolean disponible) {

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAño() {
        return año;
    }
    public void setAño(Integer año) {
        this.año = año;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.autor = genero;
    }

    public Boolean getDisponible() {
        return disponible;
    }
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return id + " | " + titulo + " - " + autor + (disponible ? " [Disponible]" : " [Prestado]");
    }

    public boolean isDisponible() {
        return true;
    }
}