import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistroParticipante implements Initializable {

    @FXML private TextField txtNumCtrl;
    @FXML private TextField txtIntegrante;
    @FXML private TextField txtCarrera;
    @FXML private DatePicker dateNacimiento;
    @FXML private ComboBox<Integer> semestreComboBox;
    @FXML private ComboBox<String> sexoComboBox;

    private int idEscuelaAsignada = -1;
    private int nivelEscuela = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sexoComboBox.getItems().addAll("H", "M");
        semestreComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }

    public void setDatosEscuela(int idEscuela, int nivel) {
        this.idEscuelaAsignada = idEscuela;
        this.nivelEscuela = nivel;

        // Lógica visual: Si es Primaria(1) o Secundaria(2), ocultamos campos
        if (nivel == 1 || nivel == 2) {
            txtCarrera.setVisible(false);
            txtCarrera.setManaged(false); // Libera el espacio visual

            semestreComboBox.setVisible(false);
            semestreComboBox.setManaged(false);
        } else {
            // Aseguramos que se vean para prepa/uni
            txtCarrera.setVisible(true);
            txtCarrera.setManaged(true);

            semestreComboBox.setVisible(true);
            semestreComboBox.setManaged(true);
        }
    }

    @FXML
    void btnGuardar(ActionEvent event) {
        // 1. VALIDACIÓN CAMPOS VACÍOS (Ya la tienes, la mantenemos)
        if (txtIntegrante.getText().trim().isEmpty() || txtNumCtrl.getText().trim().isEmpty() ||
                dateNacimiento.getValue() == null) {
            mostrarAlertaError("Error", "Campos Vacíos", "Llena el nombre, número de control y fecha.");
            return;
        }

        String nombre = txtIntegrante.getText().trim();
        String numCtrlTexto = txtNumCtrl.getText().trim();

        // 2. NUEVA: VALIDACIÓN DE NOMBRE (Solo letras y espacios)
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            mostrarAlertaError("Formato Inválido", "Nombre Incorrecto",
                    "El nombre del integrante solo puede contener letras.");
            return;
        }

        // 3. NUEVA: VALIDACIÓN NÚMERO DE CONTROL (Solo dígitos y longitud exacta)
        // Ajusta {8} al tamaño real de tus números de control (ej. 8 dígitos)
        if (!numCtrlTexto.matches("^\\d{1,10}$")) {
            mostrarAlertaError("Formato Inválido", "Número de Control",
                    "El número de control debe contener solo dígitos numéricos (Máximo 10).");
            return;
        }

        // 4. NUEVA: FECHA LÓGICA (No puede nacer hoy o en el futuro)
        if (dateNacimiento.getValue().isAfter(java.time.LocalDate.now().minusYears(3))) {
            mostrarAlertaError("Fecha Inválida", "Edad Incorrecta",
                    "La fecha de nacimiento es demasiado reciente.");
            return;
        }

        // --- CONTINÚA TU LÓGICA DE GUARDADO ---
        try {
            // Ahora es seguro convertir a entero
            int numCtrl = Integer.parseInt(numCtrlTexto);
            // ... resto del código ...
            // 3. Preparar Datos
            nombre = txtIntegrante.getText();
            java.sql.Date fecha = java.sql.Date.valueOf(dateNacimiento.getValue());
            String sexo = sexoComboBox.getValue();
            numCtrl = Integer.parseInt(txtNumCtrl.getText());

            // Datos variables según nivel
            String carrera;
            int semestre;

            if (nivelEscuela == 1 || nivelEscuela == 2) {
                // Para niños enviamos valores por defecto
                carrera = "N/A"; // No aplica
                semestre = 1;    // Valor genérico
            } else {
                // Para grandes tomamos lo que escribieron
                carrera = txtCarrera.getText();
                semestre = semestreComboBox.getValue();
            }

            // 4. Llamar a la BD (que ahora valida la edad)
            int resultado = Main.registrarCompetidor(nombre, fecha, idEscuelaAsignada, sexo, carrera, semestre, numCtrl);

            // 5. Interpretar Respuesta
            if (resultado == 1) {
                mostrarAlertaExito("Éxito", "Alumno Registrado", "El alumno se ha guardado correctamente.");
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
            else if (resultado == -1) {
                mostrarAlertaError("Registro Fallido", "Duplicado", "Ese número de control ya existe.");
            }
            else if (resultado == -3) {
                // Este es el nuevo error de la BD
                mostrarAlertaError("Registro Fallido", "Edad Insuficiente",
                        "La fecha de nacimiento no corresponde al nivel académico de la escuela.");
            }
            else {
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