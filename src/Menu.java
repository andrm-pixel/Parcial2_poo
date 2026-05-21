import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Menu {

    // leer lo que el usuario escribe en el teclado
    private final Scanner teclado = new Scanner(System.in);

    // Los DAOs que usaremos para hablar con la base de datos
    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public void iniciar() {
        boolean continuar = true;

        while (continuar) {
            mostrarOpciones();
            int opcion = leerNumero(">> Elige una opción: ");
            continuar = ejecutarOpcion(opcion);
        }
        // Cerramos la conexion antes de salir
        System.out.println("\nHasta luego. El sistema ha cerrado correctamente!!");
    }
    public void mostrar() {
        iniciar();
    }

    // Imprime el menu principal en pantalla
    private void mostrarOpciones() {
        System.out.println("       --BIBLIOTECA--     ");
        System.out.println(" LIBROS ");
        System.out.println("  1. Ver todos los libros");
        System.out.println("  2. Buscar libro por ID");
        System.out.println("  3. Buscar libro por titulo");
        System.out.println("  4. Buscar libros por autor");
        System.out.println("  5. Buscar libros por genero");
        System.out.println("  6. Agregar un libro nuevo");
        System.out.println(" PRESTAMOS ");
        System.out.println("  7. Ver todos los prestamos");
        System.out.println("  8. Buscar préstamo por ID");
        System.out.println("  9. Ver prestamos por nombre de lector");
        System.out.println(" 10. Prestar un libro");
        System.out.println(" 11. Devolver un libro");
        System.out.println(" SISTEMA ");
        System.out.println("  0. salir");
        System.out.println("Que deseas realizar hoy??");
    }

    // Ejecuta la opcion que el usuario eligió
    private boolean ejecutarOpcion(int opcion) {
        switch (opcion) {

            // Mostrar todos los libros de la base de datos
            case 1 -> {
                System.out.println("\n--CATALOGO DE LIBROS--");
                List<Libro> libros = libroDAO.consultarTodos();
                if (libros.isEmpty()) {
                    System.out.println("No hay libros registrados aun");
                } else {
                    libros.forEach(System.out::println);
                    System.out.println("Total: " + libros.size() + " libro(s).");
                }
            }

            // Buscar un libro por su ID
            case 2 -> {
                int id = leerNumero("ID del libro a buscar: ");
                Libro libro = libroDAO.consultarPorId(id);
                if (libro != null) {
                    System.out.println("\nLibro encontrado:");
                    System.out.println(libro);
                } else {
                    System.out.println("Lo sentimos, no se encontró ningún libro con ese ID");
                }
            }

            // Buscar libros por título
            case 3 -> {
                String titulo = leerTexto("Titulo o parte del titulo a buscar: ");
                List<Libro> resultado = libroDAO.consultarPorTitulo(titulo);
                mostrarListaLibros(resultado, "titulo '" + titulo + "'");
            }

            // Buscar libros por autor
            case 4 -> {
                String autor = leerTexto("Nombre o parte del nombre del autor a buscar: ");
                List<Libro> resultado = libroDAO.consultarPorAutor(autor);
                mostrarListaLibros(resultado, "autor '" + autor + "'");
            }

            // Buscar libros por género
            case 5 -> {
                String genero = leerTexto("Genero literario a buscar: ");
                List<Libro> resultado = libroDAO.consultarPorGenero(genero);
                mostrarListaLibros(resultado, "genero '" + genero + "'");
            }

            // Pedir datos del libro nuevo y guardamos
            case 6 -> {
                System.out.println("\n--AGREGAR NUEVO LIBRO--");
                String titulo  = leerTexto("Titulo: ");
                String autor   = leerTexto("Autor: ");
                String genero  = leerTexto("Genero: ");
                int año       = leerNumero("Año de publicación: ");

                // Crea el objeto Libro como disponible = true (es nuevo, nadie lo ha pedido)
                Libro nuevo = new Libro(0, titulo, autor, genero, año, true);

                if (libroDAO.adicionar(nuevo)) {
                    System.out.println("Libro agregado correctamente a la biblioteca");
                } else {
                    System.out.println("Lo sentimos, no se pudo agregar el libro");
                }
            }

            // Muestra todos los prestamos
            case 7 -> {
                System.out.println("\n--HISTORIAL DE PRESTAMOS--");
                List<Prestamo> prestamos = prestamoDAO.consultarTodos();
                if (prestamos.isEmpty()) {
                    System.out.println("No hay prestamos registrados");
                } else {
                    prestamos.forEach(System.out::println);
                    System.out.println("Total: " + prestamos.size() + " préstamo(s).");
                }
            }

            // Buscar prestamo por ID
            case 8 -> {
                int id = leerNumero("ID del préstamo a buscar: ");
                Prestamo p = prestamoDAO.consultarPorId(id);
                if (p != null) {
                    System.out.println("\nPréstamo fue encontrado con éxito:");
                    System.out.println(p);
                } else {
                    System.out.println("lo sentimos, no se encontró ningún préstamo con ese ID.");
                }
            }

            // Ver todos los préstamos por su nombre de lector
            case 9 -> {
                String nombre = leerTexto("Nombre del lector a buscar: ");
                List<Prestamo> resultado = prestamoDAO.consultarPorNombre(nombre);
                if (resultado.isEmpty()) {
                    System.out.println("No se encontraron prestamos para: " + nombre);
                } else {
                    resultado.forEach(System.out::println);
                }
            }

            // Prestamo de un libro
            case 10 -> {
                System.out.println("\n--PRESTAR UN LIBRO--");
                int idLibro = leerNumero("ID del libro a prestar: ");
                String nombre = leerTexto("Nombre del lector: ");

                // El prestamo empieza hoy y no tiene fecha de devolución (null)
                Prestamo nuevo = new Prestamo(0, idLibro, nombre, LocalDate.now(), null);

                if (prestamoDAO.adicionar(nuevo)) {
                    System.out.println("Préstamo registrado con éxito");
                    System.out.println("El libro ya no estará disponible para otros lectores hasta su devolución");
                } else {
                    System.out.println("Lo sentimos, no se pudo registrar el préstamo");
                }
            }

            // Devolver libro
            case 11 -> {
                System.out.println("\n --DEVOLVER UN LIBRO-- ");
                int idLibro = leerNumero("ID del libro a devolver: ");

                if (prestamoDAO.registrarDevolucion(idLibro)) {
                    System.out.println("Gracias por devolver este libro, ahora puede ser prestado por otros lectores");
                } else {
                    System.out.println("Lo sentimos, no se pudo registrar la devolución.");
                }
            }

            // salir de la biblioteca
            case 0 -> {
                return false;
            }

            default -> {
                // Opcion inexistente
                System.out.println("Opción invalida, escribe un numero del 0 al 11");
            }
        }

        return true;
    }

    // Pide un numero al usuario.
    private int leerNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor escribe un numero entero");
            }
        }
    }

    // Pide al usuario que escriba un texto.
    private String leerTexto(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = teclado.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("El campo no puede estar vacío.");
            }
        } while (texto.isEmpty());
        return texto;
    }

    // Muestra lista de libros en pantalla.
    private void mostrarListaLibros(List<Libro> lista, String criterio) {
        if (lista.isEmpty()) {
            System.out.println("Lo sentimos, no se encontraron libros con " + criterio + ".");
        } else {
            System.out.println("\nResultados para " + criterio + ":");
            lista.forEach(System.out::println);
            System.out.println("Total: " + lista.size() + " libro(s).");
        }
    }
}