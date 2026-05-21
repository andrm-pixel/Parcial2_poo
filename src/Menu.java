import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Menu {

    // Herramienta para leer lo que el usuario escribe en el teclado
    private final Scanner teclado = new Scanner(System.in);

    // Los DAOs que usaremos para hablar con la base de datos
    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public void iniciar() {
        boolean continuar = true;

        while (continuar) {
            mostrarOpciones();
            int opcion = leerNumero(">> Elige una opcion: ");
            continuar = ejecutarOpcion(opcion);
        }

        System.out.println("\nHasta luego. El sistema ha cerrado correctamente.");
        // Cerramos la conexion antes de salir
    }
    public void mostrar() {
        iniciar();
    }

    // Imprime el menu principal en pantalla
    private void mostrarOpciones() {
        System.out.println("       BIBLIOTECA     ");
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
    }

    // Ejecuta la opcion que el usuario eligio
    private boolean ejecutarOpcion(int opcion) {
        switch (opcion) {

            case 1 -> {
                // Mostramos todos los libros de la base de datos
                System.out.println("\n--- CATALOGO COMPLETO DE LIBROS ---");
                List<Libro> libros = libroDAO.consultarTodos();
                if (libros.isEmpty()) {
                    System.out.println("No hay libros registrados todavia.");
                } else {
                    libros.forEach(System.out::println);
                    System.out.println("Total: " + libros.size() + " libro(s).");
                }
            }

            case 2 -> {
                // Buscamos un libro por su numero de ID
                int id = leerNumero("ID del libro a buscar: ");
                Libro libro = libroDAO.consultarPorId(id);
                if (libro != null) {
                    System.out.println("\nLibro encontrado:");
                    System.out.println(libro);
                } else {
                    System.out.println("No se encontro ningun libro con ese ID.");
                }
            }

            case 3 -> {
                // Buscamos libros por titulo
                String titulo = leerTexto("Titulo (o parte del titulo) a buscar: ");
                List<Libro> resultado = libroDAO.consultarPorTitulo(titulo);
                mostrarListaLibros(resultado, "titulo '" + titulo + "'");
            }

            case 4 -> {
                // Buscamos todos los libros de un autor
                String autor = leerTexto("Nombre del autor a buscar: ");
                List<Libro> resultado = libroDAO.consultarPorAutor(autor);
                mostrarListaLibros(resultado, "autor '" + autor + "'");
            }

            case 5 -> {
                // Filtramos libros por genero (novela, ciencia ficcion, etc.)
                String genero = leerTexto("Genero literario a buscar: ");
                List<Libro> resultado = libroDAO.consultarPorGenero(genero);
                mostrarListaLibros(resultado, "genero '" + genero + "'");
            }

            case 6 -> {
                // Pedimos los datos del libro nuevo y lo guardamos
                System.out.println("\n--- AGREGAR LIBRO NUEVO ---");
                String titulo  = leerTexto("Titulo: ");
                String autor   = leerTexto("Autor: ");
                String genero  = leerTexto("Genero: ");
                int año       = leerNumero("Año de publicación: ");

                // Creamos el objeto Libro con disponible = true (es nuevo, nadie lo ha pedido)
                Libro nuevo = new Libro(0, titulo, autor, genero, año, true);

                if (libroDAO.adicionar(nuevo)) {
                    System.out.println("Libro agregado correctamente a la biblioteca.");
                } else {
                    System.out.println("No se pudo agregar el libro.");
                }
            }

            case 7 -> {
                // Mostramos todos los prestamos
                System.out.println("\n--- HISTORIAL COMPLETO DE PRESTAMOS ---");
                List<Prestamo> prestamos = prestamoDAO.consultarTodos();
                if (prestamos.isEmpty()) {
                    System.out.println("No hay prestamos registrados todavia.");
                } else {
                    prestamos.forEach(System.out::println);
                    System.out.println("Total: " + prestamos.size() + " prestamo(s).");
                }
            }

            case 8 -> {
                // Buscamos un prestamo por su ID
                int id = leerNumero("ID del prestamo a buscar: ");
                Prestamo p = prestamoDAO.consultarPorId(id);
                if (p != null) {
                    System.out.println("\nPrestamo encontrado:");
                    System.out.println(p);
                } else {
                    System.out.println("No se encontro ningun prestamo con ese ID.");
                }
            }

            case 9 -> {
                // Vemos todos los prestamos que tiene una persona por su nombre
                String nombre = leerTexto("Nombre del lector a buscar: ");
                List<Prestamo> resultado = prestamoDAO.consultarPorNombre(nombre);
                if (resultado.isEmpty()) {
                    System.out.println("No se encontraron prestamos para: " + nombre);
                } else {
                    resultado.forEach(System.out::println);
                }
            }

            case 10 -> {
                // Prestamos un libro: pedimos el ID del libro y el nombre del lector
                System.out.println("\n--- PRESTAR UN LIBRO ---");
                int idLibro = leerNumero("ID del libro a prestar: ");
                String nombre = leerTexto("Nombre del lector: ");

                // El prestamo empieza hoy y aún no tiene fecha de devolucion (null)
                Prestamo nuevo = new Prestamo(0, idLibro, nombre, LocalDate.now(), null);

                if (prestamoDAO.adicionar(nuevo)) {
                    System.out.println("[OK] Prestamo registrado. El libro ya no esta disponible.");
                } else {
                    System.out.println("[Error] No se pudo registrar el prestamo.");
                }
            }

            case 11 -> {
                // Devolvemos un libro: pedimos el ID del libro que regresa
                System.out.println("\n DEVOLVER UN LIBRO ");
                int idLibro = leerNumero("ID del libro que se devuelve: ");

                if (prestamoDAO.registrarDevolucion(idLibro)) {
                    System.out.println("Libro devuelto. Ya puede ser prestado de nuevo.");
                } else {
                    System.out.println("No se pudo registrar la devolucion.");
                }
            }

            case 0 -> {
                // El usuario quiere salir
                return false;
            }

            default -> {
                // El usuario escribio una opcion que no existe
                System.out.println("Opcion invalida. Escribe un numero del 0 al 11.");
            }
        }

        return true;
    }

    // Le pide un numero al usuario.
    private int leerNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor escribe solo un numero entero.");
            }
        }
    }

    // Le pide al usuario que escriba un texto.
    private String leerTexto(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = teclado.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("El campo no puede estar vacio.");
            }
        } while (texto.isEmpty());
        return texto;
    }

    // Muestra una lista de libros en pantalla.
    private void mostrarListaLibros(List<Libro> lista, String criterio) {
        if (lista.isEmpty()) {
            System.out.println("No se encontraron libros con " + criterio + ".");
        } else {
            System.out.println("\nResultados para " + criterio + ":");
            lista.forEach(System.out::println);
            System.out.println("Total: " + lista.size() + " libro(s).");
        }
    }
}