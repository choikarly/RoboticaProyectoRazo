import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.*;
import java.sql.Date;

public class Main extends Application {
    private static final String CONTROLADOR = "com.mysql.cj.jdbc.Driver";
    private static final String URL_BASEDATOS = "jdbc:mysql://127.0.0.1/concurso_robotica";
    private static final String USUARIO_BD = "administrador_concursos";
    private static final String CLAVE_BD = "12345";

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
            System.out.println("Driver de MySQL cargado correctamente.");
            //menuPrincipal();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de la base de datos.");
            System.err.println("Asegúrate de que el archivo .jar de MySQL Connector/J esté en tu classpath.");
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    public static boolean registrarCompetidor( String nombre, Date fecha_nacimiento, int escuela,
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
            cs.registerOutParameter(8, Types.VARCHAR);
            cs.execute();
            String mensaje = cs.getString(8);
            System.out.println(mensaje);
            if (!mensaje.equals("Se registro al participante correctamente"))
                return false;
        } catch (SQLException e) {
            System.err.println("\nError en la comunicación con la base de datos: " + e.getMessage());
            return false;
        }
        return true;
    }
    public static String registrarDocente(String nombre, String usuario, String clave, Date fecha_nacimiento,
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
            cs.registerOutParameter(8, Types.VARCHAR);
            cs.execute();
            String mensaje = cs.getString(8);
            System.out.println(mensaje);
            return mensaje;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return "DUPLICADO";
            }
            System.err.println("\nError en la comunicación con la base de datos: " + e.getMessage());
            return "ERROR" + e.getMessage();
        }
    }

    public static int iniciarSesion(String nombre_usuario, String clave){
        int id_usuario;
        try (Connection conn = getConexion();
             CallableStatement cs = conn.prepareCall("{CALL inicio_sesion(?, ?, ?, ?)}")) {
            cs.setString(1, nombre_usuario);
            cs.setString(2, clave);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.INTEGER);
            cs.execute();
            int grado = cs.getInt(3);
            id_usuario = cs.getInt(4);
            if(id_usuario == -1)
                System.out.println("Nombre de usuario o contraseña incorrecto");
            else
                System.out.println("Se inicio sesion correctamente");
            if(grado > 0)
                System.out.println("¡Bienvenido administrador!");
        } catch (SQLException e) {
            System.err.println("\nError en la comunicación con la base de datos: " + e.getMessage());
            return -2;
        }
        return id_usuario;
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


    public static boolean agregarEscuela(String nombre, int ciudad, int nivel){
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


}