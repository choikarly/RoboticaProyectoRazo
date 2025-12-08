import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private static final String CONTROLADOR = "com.mysql.cj.jdbc.Driver";
    private static final String URL_BASEDATOS = "jdbc:mysql://127.0.0.1/concurso_robotica";
    private static final String USUARIO_BD = "administrador_concursos";
    private static final String CLAVE_BD = "12345";
    public static Integer usuaioActual = -1;
    public static String nombreCompletoUsuario = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("InicioSesion.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Inicio de Sesión");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL_BASEDATOS, USUARIO_BD, CLAVE_BD);
    }

    public static void main(String[] args) {
        try {
            launch(args);
            Class.forName(CONTROLADOR);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de la base de datos.\n" +
                    "Asegúrate de que el archivo .jar de MySQL Connector/J esté en tu classpath.");
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    public static int registrarCompetidor(String nombre, Date fecha_nacimiento, int escuela,
                                          String sexo, String carrera, int semestre, int num_control) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL registrar_competidor(?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setString(1, nombre);
            cs.setDate(2, fecha_nacimiento);
            cs.setInt(3, escuela);
            cs.setString(4, sexo);
            cs.setString(5, carrera);
            cs.setInt(6, semestre);
            cs.setInt(7, num_control);

            // CORRECCIÓN: Registramos parámetro de salida como ENTERO, no VARCHAR
            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();

            // Obtenemos el número directamente (1 = Éxito, -1 = Duplicado)
            int resultado = cs.getInt(8);

            System.out.println("Código respuesta BD: " + resultado);
            return resultado;

        } catch (SQLException e) {
            System.err.println("\nError SQL: " + e.getMessage());
            return -2; // Error General
        }
    }

    public static int registrarDocente(String nombre, String usuario, String clave, Date fecha_nacimiento,
                                       int escuela, String sexo, String especialidad) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL registrar_docente(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, nombre);
            cs.setString(2, usuario);
            cs.setString(3, clave);
            cs.setDate(4, fecha_nacimiento);
            cs.setInt(5, escuela);
            cs.setString(6, sexo);
            cs.setString(7, especialidad);
            cs.registerOutParameter(8, Types.INTEGER);
            cs.execute();
            return cs.getInt(8);
        } catch (SQLException e) {
            System.err.println("\nError SQL: " + e.getMessage());
            return -2; // Error de conexión o SQL
        }
    }

    public static int[] iniciarSesion(String nombre_usuario, String clave) {
        int id_usuario, grado;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL inicio_sesion(?, ?, ?, ?, ?)}")) {
            cs.setString(1, nombre_usuario);
            cs.setString(2, clave);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.INTEGER);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.execute();
            grado = cs.getInt(3);
            id_usuario = cs.getInt(4);
            nombreCompletoUsuario = cs.getString(5);
            if (id_usuario == -1)
                System.out.println("Nombre de usuario o contraseña incorrecto");
            else
                System.out.println("Se inicio sesion correctamente");
            if (grado >= 0)
                System.out.println("¡Bienvenido administrador!");
        } catch (SQLException e) {
            System.err.println("\nError en la comunicación con la base de datos: " + e.getMessage());
            return new int[]{-2, -1};
        }
        usuaioActual = id_usuario;
        return new int[]{id_usuario, grado};
    }


    public static boolean organizarEvento(String nombre, Date fecha, int escuela) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL crear_evento(?, ?, ?, ?)}")) {
            cs.setString(1, nombre);
            cs.setDate(2, fecha);
            cs.setInt(3, escuela);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();
            String mensaje = cs.getString(4);
            System.out.println(mensaje);
            if (!mensaje.equals("Alta exitosa"))
                return false;
        } catch (SQLException e) {
            System.err.println("\nError en la comunicación con la base de datos: " + e.getMessage());
            return false;
        }
        return true;
    }


    public static boolean agregarEscuela(String nombre, int ciudad, int nivel) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL ingresar_escuela(?, ?, ?, ?)}")) {
            cs.setString(1, nombre);
            cs.setInt(2, ciudad);
            cs.setInt(3, nivel);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();
            String mensaje = cs.getString(4);
            System.out.println(mensaje);
            if (!mensaje.equals("Se ingreso la escuela correctamente"))
                return false;
        } catch (SQLException e) {
            System.err.println("\nError en la comunicación con la base de datos: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static List<Map<String, Object>> retornarEscuelas() {
        List<Map<String, Object>> listaEscuelas = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_escuelas()}")) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> escuela = new HashMap<>();

                    // Guardamos los datos tal cual vienen de la consulta
                    escuela.put("id_escuela", rs.getInt("id_escuela"));
                    escuela.put("nombre", rs.getString("nombre"));
                    escuela.put("fk_ciudad", rs.getInt("fk_ciudad"));
                    escuela.put("fk_nivel", rs.getInt("fk_nivel")); // getObject por si es int o string
                    listaEscuelas.add(escuela);
                }
            }
            System.out.println("Escuelas encontradas: " + listaEscuelas.size()); // Debug
        } catch (SQLException e) {
            System.err.println("\nError al obtener escuelas: " + e.getMessage());
            // En caso de error devolvemos la lista vacía para no romper el programa
        }
        return listaEscuelas;
    }

    public static List<Map<String, Object>> retornarEventos() {
        List<Map<String, Object>> lista = new ArrayList<>();

        // Asegúrate de que el SP en SQL ya incluya 'id_evento' en el SELECT
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_eventos()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();

                // --- ESTA ES LA LÍNEA QUE TE FALTA ---
                fila.put("id_evento", rs.getInt("id_evento"));
                // -------------------------------------

                fila.put("nombre", rs.getString("nombre"));
                fila.put("sede", rs.getString("sede"));
                fila.put("fecha", rs.getDate("fecha")); // O getString, según como lo manejes

                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al retornar eventos: " + e.getMessage());
        }
        return lista;
    }

    public static List<Map<String, Object>> retornarCategorias() {
        List<Map<String, Object>> listaCategorias = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_categorias()}")) {

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();

                    // Guardamos las columnas de la tabla 'categoria'
                    fila.put("id_categoria", rs.getInt("id_categoria"));
                    fila.put("nombre", rs.getString("nombre"));

                    listaCategorias.add(fila);
                }
            }
            System.out.println("Categorías recuperadas: " + listaCategorias.size()); // Debug

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        }

        return listaCategorias;
    }

    public static List<Map<String, Object>> retornarEquiposCoach(int idCoach) {
        List<Map<String, Object>> listaEquipos = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_equipos_coach(?)}")) {

            // Pasamos el parámetro de entrada (ID del coach)
            cs.setInt(1, idCoach);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();

                    // Usamos los alias exactos que definiste en el SELECT del Procedure
                    fila.put("equipo", rs.getString("equipo"));
                    fila.put("escuela", rs.getString("escuela"));
                    fila.put("evento", rs.getString("evento"));
                    fila.put("categoria", rs.getString("categoria"));

                    listaEquipos.add(fila);
                }
            }
            System.out.println("Equipos encontrados para el coach " + idCoach + ": " + listaEquipos.size()); // Debug

        } catch (SQLException e) {
            System.err.println("Error al obtener equipos del coach: " + e.getMessage());
            e.printStackTrace();
        }

        return listaEquipos;
    }

    public static List<Map<String, Object>> retornarEventosParticipados(int idUsuario) {
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_eventos_participados(?)}")) {

            cs.setInt(1, idUsuario);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();

                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("fecha", rs.getDate("fecha")); // Ojo: SQL Date
                    fila.put("sede", rs.getString("sede"));
                    fila.put("mi_rol", rs.getString("mi_rol")); // "COACH" o "JUEZ"

                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener eventos participados: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public static int crearEquipo(String nombreEquipo, int idEscuela) {
        int idEquipo = -1; // Valor inicial de error

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL crear_equipo(?, ?, ?)}")) {

            // 1. Parámetros de ENTRADA (Lo que mandas)
            cs.setString(1, nombreEquipo);
            cs.setInt(2, idEscuela);

            // 2. Parámetro de SALIDA (Lo que esperas recibir: el ID)
            cs.registerOutParameter(3, Types.INTEGER);

            // 3. Ejecutar
            cs.execute();

            // 4. Leer el valor que devolvió la base de datos
            idEquipo = cs.getInt(3);

            System.out.println("Equipo gestionado correctamente. ID: " + idEquipo); // Debug

        } catch (SQLException e) {
            System.err.println("Error al crear/buscar equipo: " + e.getMessage());
            return -2; // Código de error de base de datos
        }
        return idEquipo;
    }

    public static int obtenerIdEscuelaDelDocente(int idDocente) {
        int idEscuela = -1;

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_id_escuela_docente(?, ?)}")) {
            // 1. Parámetro de entrada: El ID del usuario logueado
            cs.setInt(1, idDocente);
            // 2. Parámetro de salida: El ID de la escuela que queremos recuperar
            cs.registerOutParameter(2, Types.INTEGER);
            // 3. Ejecutar
            cs.execute();
            // 4. Obtener el resultado
            idEscuela = cs.getInt(2);

        } catch (SQLException e) {
            System.err.println("Error al obtener escuela del docente: " + e.getMessage());
        }
        return idEscuela;
    }

    public static String obtenerNombreEscuela(int idDocente) {
        String nombreEscuela = "Desconocida";

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_nombre_escuela_docente(?, ?)}")) {

            // 1. Entrada: ID del docente
            cs.setInt(1, idDocente);

            // 2. Salida: Nombre de la escuela (VARCHAR)
            cs.registerOutParameter(2, Types.VARCHAR);

            // 3. Ejecutar
            cs.execute();

            // 4. Recuperar el texto
            nombreEscuela = cs.getString(2);

            if (nombreEscuela == null) {
                nombreEscuela = "Sin Escuela Asignada";
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de escuela: " + e.getMessage());
        }
        return nombreEscuela;
    }

    public static List<Map<String, Object>> retornarAlumnosPorEscuela(int idEscuela) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_alumnos_por_escuela(?)}")) {

            cs.setInt(1, idEscuela);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id", rs.getInt("id_participante"));
                    fila.put("nombre", rs.getString("nombre"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static int registrarEquipo(int coach, int equipo, int evento, int categoria, int p1, int p2, int p3) {
        int aviso = -2;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL registrar_equipo(?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setInt(1, coach);
            cs.setInt(2, equipo);
            cs.setInt(3, evento);
            cs.setInt(4, categoria);
            cs.setInt(5, p1);
            cs.setInt(6, p2);
            cs.setInt(7, p3);
            cs.registerOutParameter(8, Types.TINYINT);

            cs.execute();
            aviso = cs.getInt(8);

        } catch (SQLException e) {
            System.err.println("Error al registrar equipo: " + e.getMessage());
        }
        return aviso;
    }

    public static int obtenerEscuelaDeEquipo(int idEquipo) {
        int idEscuela = -1;

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_escuela_equipo(?, ?)}")) {

            // 1. Entrada: ID del Equipo
            cs.setInt(1, idEquipo);

            // 2. Salida: ID de la Escuela
            cs.registerOutParameter(2, Types.INTEGER);

            // 3. Ejecutar
            cs.execute();

            // 4. Recuperar el valor
            idEscuela = cs.getInt(2);

        } catch (SQLException e) {
            System.err.println("Error al obtener escuela del equipo: " + e.getMessage());
            e.printStackTrace();
        }
        return idEscuela;
    }

    public static List<Map<String, Object>> retornarDocentesConRoles() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_docentes_con_roles()}")) {

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("escuela", rs.getString("escuela"));
                    // Recuperamos las "banderas" (flags)
                    fila.put("es_coach", rs.getInt("es_coach") == 1); // Guardamos como boolean
                    fila.put("es_juez", rs.getInt("es_juez") == 1);   // Guardamos como boolean

                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<Map<String, Object>> retornarEquiposAdminFiltro(int idEvento, int idCategoria) {
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_equipos_admin_filtro(?, ?)}")) {

            cs.setInt(1, idEvento);
            cs.setInt(2, idCategoria);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("equipo", rs.getString("equipo"));
                    fila.put("escuela", rs.getString("escuela"));
                    fila.put("evento", rs.getString("evento"));
                    fila.put("categoria", rs.getString("categoria"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // Agrega esto en RoboticaProyectoRazo/src/Main.java

    public static List<Map<String, Object>> retornarEquiposDocente(int idDocente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_equipos_docente(?)}")) {

            cs.setInt(1, idDocente);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_equipo", rs.getInt("id_equipo"));
                    fila.put("nombre", rs.getString("nombre"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipos del docente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public static int obtenerNivelEscuela(int idEscuela) {
        int nivel = -1;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_nivel_escuela(?, ?)}")) {
            cs.setInt(1, idEscuela);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            nivel = cs.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nivel;
    }

}