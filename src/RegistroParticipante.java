import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistroParticipante implements Initializable {

    // Campos del FXML (para UN SOLO alumno)
    @FXML private TextField txtNumCtrl;
    @FXML private TextField txtIntegrante;
    @FXML private TextField txtCarrera;
    @FXML private DatePicker dateNacimiento;
    @FXML private ComboBox<Integer> semestreComboBox;
    @FXML private ComboBox<String> sexoComboBox;

    private int idEscuelaAsignada = -1; // Se recibirá desde la ventana anterior

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar combos
        sexoComboBox.getItems().addAll("H", "M");
        semestreComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }

    // Método para recibir la escuela automáticamente
    public void setDatosEscuela(int idEscuela) {
        this.idEscuelaAsignada = idEscuela;
    }

    @FXML
    void btnGuardar(ActionEvent event) {
        // 1. Validaciones
        if (txtIntegrante.getText().isEmpty() || txtNumCtrl.getText().isEmpty() ||
                dateNacimiento.getValue() == null || semestreComboBox.getValue() == null) {
            mostrarAlertaError("Error", "Campos Vacíos", "Llena todos los datos del alumno.");
            return;
        }

        if (idEscuelaAsignada == -1) {
            mostrarAlertaError("Error", "Sistema", "No se ha identificado la escuela del docente.");
            return;
        }

        try {
            // 2. Obtener datos
            String nombre = txtIntegrante.getText();
            java.sql.Date fecha = java.sql.Date.valueOf(dateNacimiento.getValue());
            String sexo = sexoComboBox.getValue();
            String carrera = txtCarrera.getText();
            int semestre = semestreComboBox.getValue();
            int numCtrl = Integer.parseInt(txtNumCtrl.getText());

            // 3. Guardar en BD
            int resultado = Main.registrarCompetidor(nombre, fecha, idEscuelaAsignada, sexo, carrera, semestre, numCtrl);

            if (resultado == 1) {
                mostrarAlertaExito("Éxito", "Alumno Registrado", "El alumno se ha guardado correctamente.");
                // Cerrar ventana
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            } else if (resultado == -1) {
                mostrarAlertaError("Error", "Duplicado", "Ese número de control ya existe en esta escuela.");
            } else {
                mostrarAlertaError("Error", "Fallo BD", "No se pudo guardar el registro.");
            }

        } catch (NumberFormatException e) {
            mostrarAlertaError("Error", "Formato inválido", "El número de control debe ser numérico.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAlertaExito(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}