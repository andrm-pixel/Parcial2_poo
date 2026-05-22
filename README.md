# Sistema de Gestión de Biblioteca

Proyecto en Java que permite gestionar el catálogo de libros y los préstamos de una biblioteca, con persistencia de datos en una base de datos **PostgreSQL** alojada en [Neon](https://neon.tech). Implementa el patrón **DAO (Data Access Object)** para separar la lógica de negocio del acceso a datos.

---

## ¿Qué hace este proyecto?

El sistema permite:

- Consultar, buscar y agregar **libros** (por ID, título, autor o género)
- Registrar y consultar **préstamos** de libros a lectores
- Registrar **devoluciones**, actualizando automáticamente la disponibilidad del libro
- Garantizar la integridad de los datos mediante **transacciones SQL** (si el préstamo falla, ningún cambio queda guardado)

Todo se maneja desde un menú interactivo en consola.

---

## Diagrama de clases

<img width="781" height="733" alt="Diagrama de Clases" src="https://github.com/user-attachments/assets/ceff7a09-4bdd-407e-ab3b-b4c03bd5bd74" />

| Color      | Capa |
|------------|------|
| 🟣 Morado  | Modelos (`Libro`, `Prestamo`) |
| 🟢 Verde   | DAOs (`LibroDAO`, `PrestamoDAO`) |
| 🟠 Naranja | Conexión (`ConeccionDataB`) |
| ⚫ Gris     | Control y entrada (`Menu`, `Main`) |

---

## Estructura del proyecto

```
biblioteca/
├── src/
│   ├── ConeccionDataB.java   # Gestiona la conexión a la base de datos PostgreSQL
│   ├── Libro.java            # Clase modelo que representa un libro
│   ├── LibroDAO.java         # Consultas SQL relacionadas con libros (CRUD)
│   ├── Main.java             # Punto de entrada de la aplicación
│   ├── Menu.java             # Interfaz de usuario en consola (menú interactivo)
│   ├── Prestamo.java         # Clase modelo que representa un préstamo
│   ├── PrestamoDAO.java      # Consultas SQL relacionadas con préstamos
│   ├── README.md
│   └── TestConnection.java   # Prueba rápida de conexión a la base de datos
└── docs/
    └── diagrama_clases.png   # Diagrama UML de clases

```

---

## Descripción de cada clase

| Clase | Responsabilidad |
|-------|----------------|
| `ConeccionDataB` | Centraliza la configuración de la conexión (URL, usuario, contraseña) y expone `getConnection()` |
| `Libro` | Modelo con atributos: `id`, `titulo`, `autor`, `genero`, `año`, `disponible` |
| `LibroDAO` | CRUD de libros: consultar todos, buscar por ID/título/autor/género, agregar, actualizar disponibilidad |
| `Prestamo` | Modelo con atributos: `id`, `id_libro`, `nombre`, `fechaPrestamo`, `fechaDevolucion` |
| `PrestamoDAO` | Registrar préstamos y devoluciones usando transacciones; consultar por ID o nombre |
| `Menu` | Muestra el menú, lee la entrada del usuario y llama a los DAOs correspondientes |
| `Main` | Crea un `Menu` y llama a `mostrar()` para iniciar la aplicación |
| `TestConnection` | Clase de utilidad para verificar que la conexión a la base de datos funciona |

---

## Tecnologías utilizadas

- **Java 17+**
- **PostgreSQL** (base de datos relacional)
- **Neon** (PostgreSQL serverless en la nube)
- **JDBC** (Java Database Connectivity) para la comunicación con la base de datos
- Patrón de diseño **DAO**
- **dotenv-java-3.2.0** (Leer el archivo .env)
- **Transacciones SQL** (`commit` / `rollback`) para garantizar integridad en préstamos y devoluciones

---

## Requisitos para ejecutar

- IntelliJ IDEA (Community o Ultimate)
- JDK 17 o superior
- Acceso a internet (la base de datos está en Neon)
- Driver JDBC de PostgreSQL (se agrega como dependencia en IntelliJ)

---

## Esquema de la base de datos

Ejecuta este script en tu consola de Neon antes de correr el proyecto:

```sql
CREATE TABLE libro (
    id          SERIAL PRIMARY KEY,
    titulo      TEXT    NOT NULL,
    autor       TEXT    NOT NULL,
    genero      TEXT,
    año         INT,
    disponible  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE prestamo (
    id                SERIAL PRIMARY KEY,
    id_libro          INT  REFERENCES libro(id),
    nombre_usuario    TEXT NOT NULL,
    fecha_prestamo    DATE NOT NULL,
    fecha_devolucion  DATE
);
```

---

## Menú disponible

```
       --BIBLIOTECA--
 LIBROS
  1. Ver todos los libros
  2. Buscar libro por ID
  3. Buscar libro por titulo
  4. Buscar libros por autor
  5. Buscar libros por genero
  6. Agregar un libro nuevo
 PRESTAMOS
  7. Ver todos los prestamos
  8. Buscar préstamo por ID
  9. Ver prestamos por nombre de lector
 10. Prestar un libro
 11. Devolver un libro
 SISTEMA
  0. Salir
```