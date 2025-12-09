import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CrearEquipo implements Initializable{
    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private Label lblNombreEscuela;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int idDocente = Main.usuaioActual;

        // Ahora esta línea llama al nuevo Stored Procedure
        String nombreEscuela = Main.obtenerNombreEscuela(idDocente);

        lblNombreEscuela.setText(nombreEscuela);
    }

    @FXML
    void btnCrearEquipo(ActionEvent event) {
        try {
            String nombreEquipo = txtNombreEquipo.getText().trim();
            if (nombreEquipo.isEmpty()) {
                mostrarAlertaError("Error", "Campo Vacío", "Escribe el nombre del equipo.");
                return;
            }

            // NUEVA: VALIDACIÓN CARACTERES ESPECIALES Y LONGITUD
            // Evita que pongan emojis o nombres kilométricos que rompan la BD
            if (nombreEquipo.length() > 80) {
                mostrarAlertaError("Nombre muy largo", "Límite excedido", "El nombre debe tener máximo 80 caracteres.");
                return;
            }

            if (!nombreEquipo.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-]+$")) {
                mostrarAlertaError("Caracteres Inválidos", "Nombre de Equipo",
                        "Usa solo letras, números y guiones.");
                return;
            } else {
                // 2. Obtener el ID del Coach que está logueado
                int idCoach = Main.usuaioActual;

                // 3. Obtener el ID de la Escuela a la que pertenece ese Coach
                // (Usamos la función nueva que agregamos al Main)
                int idEscuela = Main.obtenerIdEscuelaDelDocente(idCoach);

                // Validación de seguridad por si el docente no tiene escuela asignada en la BD
                if (idEscuela <= 0) {
                    mostrarAlertaError("Error de Datos", "Sin Escuela", "No se encontró la escuela asignada a tu usuario.");
                    return;
                }

                // 4. Mandar a crear el Equipo a la Base de Datos
                int idEquipoGenerado = Main.crearEquipo(nombreEquipo, idEscuela);

                // 5. Evaluar el resultado
                if (idEquipoGenerado > 0) {
                    // --- ÉXITO ---
                    mostrarAlertaExito("Éxito",
                            "Equipo Creado",
                            "El equipo se registró correctamente.");

                    // Cerrar la ventana actual (el modal)
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();

                } else {
                    // --- ERROR ---
                    mostrarAlertaError("Error",
                            "Fallo al Guardar",
                            "No se pudo crear el equipo. Es posible que ya exista un equipo con ese nombre en tu escuela.");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            mostrarAlertaError("Error",
                    "Excepción", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }


    public void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    public void mostrarAlertaExito(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
