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

            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();

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


    public static int crearEvento(String nombre, java.sql.Date fecha, int idSede) {
        int aviso = -2; // Valor por defecto si falla la conexión

        String sql = "{CALL crear_evento(?, ?, ?, ?)}";

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setDate(2, fecha);
            cs.setInt(3, idSede);

            cs.registerOutParameter(4, java.sql.Types.TINYINT);

            cs.execute();

            aviso = cs.getInt(4);

        } catch (SQLException e) {
            System.err.println("Error al crear evento: " + e.getMessage());
            e.printStackTrace();
        }
        return aviso;
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
                    escuela.put("fk_nivel", rs.getInt("fk_nivel"));
                    listaEscuelas.add(escuela);
                }
            }
            System.out.println("Escuelas encontradas: " + listaEscuelas.size());
        } catch (SQLException e) {
            System.err.println("\nError al obtener escuelas: " + e.getMessage());
        }
        return listaEscuelas;
    }

    public static List<Map<String, Object>> retornarEventos() {
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_eventos()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_evento", rs.getInt("id_evento"));

                fila.put("nombre", rs.getString("nombre"));
                fila.put("sede", rs.getString("sede"));
                fila.put("fecha", rs.getDate("fecha"));

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

                    fila.put("id_categoria", rs.getInt("id_categoria"));
                    fila.put("nombre", rs.getString("nombre"));

                    listaCategorias.add(fila);
                }
            }
            System.out.println("Categorías recuperadas: " + listaCategorias.size());

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
            cs.setInt(1, idCoach);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_equipo", rs.getInt("id_equipo"));
                    fila.put("id_evento", rs.getInt("id_evento"));
                    fila.put("equipo", rs.getString("equipo"));
                    fila.put("escuela", rs.getString("escuela"));
                    fila.put("evento", rs.getString("evento"));
                    fila.put("categoria", rs.getString("categoria"));

                    listaEquipos.add(fila);
                }
            }
            System.out.println("Equipos encontrados para el coach " + idCoach + ": " + listaEquipos.size());

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

                    fila.put("id_evento", rs.getInt("id_evento"));
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("fecha", rs.getDate("fecha"));
                    fila.put("sede", rs.getString("sede"));
                    fila.put("mi_rol", rs.getString("mi_rol"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static int crearEquipo(String nombreEquipo, int idEscuela) {
        int idEquipo = -1;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL crear_equipo(?, ?, ?)}")) {

            cs.setString(1, nombreEquipo);
            cs.setInt(2, idEscuela);

            cs.registerOutParameter(3, Types.INTEGER);

            cs.execute();

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
            cs.setInt(1, idDocente);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
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
            cs.setInt(1, idDocente);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
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
            cs.setInt(1, idEquipo);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
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
                    fila.put("id_docente", rs.getInt("id_docente"));
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("escuela", rs.getString("escuela"));
                    fila.put("es_coach", rs.getInt("es_coach") == 1);
                    fila.put("es_juez", rs.getInt("es_juez") == 1);

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
                    fila.put("id_equipo", rs.getInt("id_equipo"));
                    fila.put("id_evento", rs.getInt("id_evento"));
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

    public static List<Map<String, Object>> retornarSedes() {
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_sedes_combo()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_sede"));
                fila.put("nombre", rs.getString("nombre"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar sedes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public static List<Map<String, Object>> retornarCategoriasEvento(int idEvento) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_categorias_por_evento(?)}")) {

            cs.setInt(1, idEvento);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id", rs.getInt("id_categoria"));
                    fila.put("nombre", rs.getString("nombre"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public static List<Map<String, Object>> retornarDocentes() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_docentes()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();

                fila.put("id", rs.getInt("id_docente"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("escuela", rs.getString("escuela"));
                fila.put("especialidad", rs.getString("especialidad"));

                lista.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static int registrarTernaEnBD(int idEvento, int idCategoria, int j1, int j2, int j3) {
        int aviso = -1;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL asignar_terna_jueces(?, ?, ?, ?, ?, ?)}")) {

            cs.setInt(1, idEvento);
            cs.setInt(2, idCategoria);
            cs.setInt(3, j1);
            cs.setInt(4, j2);
            cs.setInt(5, j3);
            cs.registerOutParameter(6, java.sql.Types.TINYINT);

            cs.execute();
            aviso = cs.getInt(6);

        } catch (SQLException e) {
            System.err.println("Error al registrar terna de jueces: " + e.getMessage());
            e.printStackTrace();
        }
        return aviso;
    }

    public static Map<String, String> obtenerInfoPersonalDocente(int idDocente) {
        Map<String, String> info = new HashMap<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_info_docente(?)}")) {

            cs.setInt(1, idDocente);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    info.put("nombre", rs.getString("nombre"));
                    info.put("usuario", rs.getString("nombre_usuario"));
                    info.put("fecha", rs.getDate("fecha_nacimiento").toString());
                    info.put("sexo", rs.getString("sexo"));
                    info.put("especialidad", rs.getString("especialidad"));
                    info.put("escuela", rs.getString("nombre_escuela"));
                    info.put("nivel", rs.getString("nivel_academico"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener info personal: " + e.getMessage());
            e.printStackTrace();
        }
        return info;
    }

    public static List<Map<String, String>> retornarMiembrosEquipo(int idEquipo, int idEvento) {
        List<Map<String, String>> miembros = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_miembros_equipo(?, ?)}")) {
            cs.setInt(1, idEquipo);
            cs.setInt(2, idEvento);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("nombre", rs.getString("nombre"));
                m.put("num_control", String.valueOf(rs.getInt("num_control")));
                miembros.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return miembros;
    }

    public static int obtenerPuntajeEquipo(int idEquipo, int idEvento) {
        int puntos = -2;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_puntaje_equipo(?, ?, ?)}")) {
            cs.setInt(1, idEquipo);
            cs.setInt(2, idEvento);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            puntos = cs.getInt(3);
        } catch (SQLException e) { e.printStackTrace(); }
        return puntos;
    }

    public static List<Map<String, Object>> retornarEquiposParaEvaluar(int idEvento, int idJuez) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_equipos_a_evaluar(?, ?)}")) {

            cs.setInt(1, idEvento);
            cs.setInt(2, idJuez);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id", rs.getInt("id_equipo"));
                    fila.put("equipo", rs.getString("nombre_equipo"));
                    fila.put("categoria", rs.getString("nombre_categoria"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<Map<String, Object>> retornarCiudades() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_ciudades_combo()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_ciudad"));
                fila.put("nombre", rs.getString("nombre"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static int registrarSede(String nombre, int idCiudad) {
        int aviso = -99;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL ingresar_sede(?, ?, ?)}")) {

            cs.setString(1, nombre);
            cs.setInt(2, idCiudad);
            cs.registerOutParameter(3, java.sql.Types.TINYINT);

            cs.execute();
            aviso = cs.getInt(3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aviso;
    }

    public static List<Map<String, Object>> retornarEquiposAEvaluar(int idEvento, int idJuez) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_equipos_a_evaluar(?, ?)}")) {

            cs.setInt(1, idEvento);
            cs.setInt(2, idJuez);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_equipo", rs.getInt("id_equipo"));
                    fila.put("nombre_equipo", rs.getString("nombre_equipo"));
                    fila.put("nombre_categoria", rs.getString("nombre_categoria"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipos a evaluar: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public static void gestionarEvaluacionDiseno(int equipo, int evento, boolean... checks) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL gestionar_evaluacion_diseno(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setInt(1, equipo);
            cs.setInt(2, evento);
            for (int i = 0; i < checks.length; i++) {
                cs.setInt(i + 3, checks[i] ? 1 : 0);
            }

            cs.execute();
            System.out.println("Evaluación de Diseño guardada.");

        } catch (SQLException e) {
            System.err.println("Error al guardar evaluación de diseño: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<String, Boolean> obtenerEvaluacionDiseno(int equipo, int evento) {
        Map<String, Boolean> datos = new HashMap<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_evaluacion_diseno(?, ?)}")) {

            cs.setInt(1, equipo);
            cs.setInt(2, evento);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    // Mapeamos las columnas de la BD a claves del mapa
                    datos.put("registro_fechas", rs.getBoolean("registro_fechas"));
                    datos.put("justificacion_cambios_prototipos", rs.getBoolean("justificacion_cambios_prototipos"));
                    datos.put("ortografia_redacción", rs.getBoolean("ortografia_redacción"));
                    datos.put("presentación", rs.getBoolean("presentación"));
                    datos.put("video_animación", rs.getBoolean("video_animación"));
                    datos.put("diseno_modelado_software", rs.getBoolean("diseno_modelado_software"));
                    datos.put("analisis_elementos", rs.getBoolean("analisis_elementos"));
                    datos.put("ensamble_prototipo", rs.getBoolean("ensamble_prototipo"));
                    datos.put("modelo_acorde_robot", rs.getBoolean("modelo_acorde_robot"));
                    datos.put("acorde_simulacion_calculos", rs.getBoolean("acorde_simulacion_calculos"));
                    datos.put("restricciones_movimiento", rs.getBoolean("restricciones_movimiento"));
                    datos.put("diagramas_imagenes", rs.getBoolean("diagramas_imagenes"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    public static void gestionarEvaluacionProg(int equipo, int evento, boolean... checks) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL gestionar_evaluacion_prog(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setInt(1, equipo);
            cs.setInt(2, evento);

            for (int i = 0; i < checks.length; i++) {
                cs.setInt(i + 3, checks[i] ? 1 : 0);
            }

            cs.execute();
            System.out.println("Evaluación de Programación guardada.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Boolean> obtenerEvaluacionProg(int equipo, int evento) {
        Map<String, Boolean> datos = new HashMap<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_evaluacion_prog(?, ?)}")) {

            cs.setInt(1, equipo);
            cs.setInt(2, evento);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    datos.put("soft_prog", rs.getBoolean("soft_prog"));
                    datos.put("uso_func", rs.getBoolean("uso_func"));
                    datos.put("complejidad", rs.getBoolean("complejidad"));
                    datos.put("just_prog", rs.getBoolean("just_prog"));
                    datos.put("conocimiento_estr_func", rs.getBoolean("conocimiento_estr_func"));
                    datos.put("depuracion", rs.getBoolean("depuracion"));
                    datos.put("codigo_modular_efi", rs.getBoolean("codigo_modular_efi"));
                    datos.put("documentacion", rs.getBoolean("documentacion"));
                    datos.put("vinculación_acciones", rs.getBoolean("vinculación_acciones"));
                    datos.put("sensores", rs.getBoolean("sensores"));
                    datos.put("vinculo_jostick", rs.getBoolean("vinculo_jostick"));
                    datos.put("calibración", rs.getBoolean("calibración"));
                    datos.put("respuesta_dispositivo", rs.getBoolean("respuesta_dispositivo"));
                    datos.put("documentación_codigo", rs.getBoolean("documentación_codigo"));
                    datos.put("demostración_15min", rs.getBoolean("demostración_15min"));
                    datos.put("no_inconvenientes", rs.getBoolean("no_inconvenientes"));
                    datos.put("demostracion_objetivo", rs.getBoolean("demostracion_objetivo"));
                    datos.put("explicacion_rutina", rs.getBoolean("explicacion_rutina"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    public static void gestionarEvaluacionConst(int equipo, int evento, boolean... checks) {
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL gestionar_evaluacion_const(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

            cs.setInt(1, equipo);
            cs.setInt(2, evento);

            for (int i = 0; i < checks.length; i++) {
                cs.setInt(i + 3, checks[i] ? 1 : 0);
            }

            cs.execute();
            System.out.println("Evaluación de Construcción guardada.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Boolean> obtenerEvaluacionConst(int equipo, int evento) {
        Map<String, Boolean> datos = new HashMap<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL obtener_evaluacion_const(?, ?)}")) {

            cs.setInt(1, equipo);
            cs.setInt(2, evento);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    datos.put("prototipo_estetico", rs.getBoolean("prototipo_estetico"));
                    datos.put("estructuras_estables", rs.getBoolean("estructuras_estables"));
                    datos.put("uso_sistemas_transmision", rs.getBoolean("uso_sistemas_transmision"));
                    datos.put("uso_sensores", rs.getBoolean("uso_sensores"));
                    datos.put("cableado_adecuado", rs.getBoolean("cableado_adecuado"));
                    datos.put("calculo_implementacion_sistema_neumático", rs.getBoolean("calculo_implementacion_sistema_neumático"));
                    datos.put("conocimiento_alcance", rs.getBoolean("conocimiento_alcance"));
                    datos.put("implementación_marca_vex", rs.getBoolean("implementación_marca_vex"));
                    datos.put("uso_procesador_cortexm3", rs.getBoolean("uso_procesador_cortexm3"));
                    datos.put("analisis_Estruc", rs.getBoolean("analisis_Estruc"));
                    datos.put("relacion_velocidades", rs.getBoolean("relacion_velocidades"));
                    datos.put("tren_engranes", rs.getBoolean("tren_engranes"));
                    datos.put("centro_gravedad", rs.getBoolean("centro_gravedad"));
                    datos.put("sis_transmicion", rs.getBoolean("sis_transmicion"));
                    datos.put("potencia", rs.getBoolean("potencia"));
                    datos.put("torque", rs.getBoolean("torque"));
                    datos.put("velocidad", rs.getBoolean("velocidad"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    public static List<Map<String, Object>> retornarRankingEvento(int idEvento) {
        List<Map<String, Object>> ranking = new ArrayList<>();
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL retornar_ranking_evento(?)}")) {

            cs.setInt(1, idEvento);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("equipo", rs.getString("equipo"));
                    fila.put("escuela", rs.getString("escuela"));
                    fila.put("categoria", rs.getString("categoria"));
                    fila.put("puntos", rs.getInt("puntos"));
                    ranking.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranking;
    }

}