import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistroController implements Initializable {
    @FXML
    private ToggleButton btn_ver;


    @FXML
    private TextField txt_nombrecompleto;
    @FXML
    private TextField txt_usuario;
    @FXML
    private TextField txt_contra;
    @FXML
    private TextField txt_contra_oculta;
    @FXML
    private ComboBox<String> EscuelaProcedenciaComboBox;
    @FXML
    private DatePicker fecha_nacimiento_date;
    @FXML
    private ComboBox<String> sexoComboBox;
    @FXML
    private TextField txt_especialidad;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] listaEscuelas = {
                "CBTis 103",
                "CETis 109",
                "CBTis 164",
                "Prepa Madero",
                "Tec Madero",
                "IEST Anáhuac",
                "UAT",
                "Otra"
        };
        sexoComboBox.getItems().addAll("HOMBRE", "MUJER");
        EscuelaProcedenciaComboBox.getItems().addAll(listaEscuelas);
        // ==============================================================================================================
        // ==============================================================================================================

        txt_contra.textProperty().bindBidirectional(txt_contra_oculta.textProperty());
        // 2. SINCRONIZAR LA VISIBILIDAD CON EL BOTÓN
        txt_contra.visibleProperty().bind(btn_ver.selectedProperty());
        txt_contra_oculta.visibleProperty().bind(btn_ver.selectedProperty().not());

        // 3. Opcional: Cambiar el texto del botón según el estado
        btn_ver.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                btn_ver.setText("Ocultar"); // O cambia el ícono a ojo cerrado
            } else {
                btn_ver.setText("Ver");     // O cambia el ícono a ojo abierto
            }
        });
    }

    @FXML
    void btnRegistrarse(ActionEvent event) {
        // 1. Extraer los datos de texto (TextFields son seguros, nunca son null, solo vacíos)
        String nombreCompleto = txt_nombrecompleto.getText();
        String usuarioNuevo = txt_usuario.getText();
        String passwordIngresado = txt_contra_oculta.getText();
        String especialidad = txt_especialidad.getText();

        // Antes de convertirlos a String, preguntamos si son null
        if (fecha_nacimiento_date.getValue() == null) {
            mostrarAlertaError("ERROR", "Fecha requerida", "Selecciona tu fecha de nacimiento.");
            return; // Detenemos el código aquí
        }
        if (EscuelaProcedenciaComboBox.getValue() == null) {
            mostrarAlertaError("ERROR", "Escuela requerida", "Selecciona tu escuela.");
            return;
        }
        if (sexoComboBox.getValue() == null) {
            mostrarAlertaError("ERROR", "Sexo requerido", "Selecciona tu sexo.");
            return;
        }
        // 3. Validar que los TextFields no estén vacíos
        if (nombreCompleto.isEmpty() || usuarioNuevo.isEmpty() || passwordIngresado.isEmpty() || especialidad.isEmpty()) {
            mostrarAlertaError("ERROR", "Campos Vacíos", "Por favor llena todos los campos de texto.");
            return;
        }

        // --- SI EL CÓDIGO LLEGA AQUÍ, SIGNIFICA QUE TODO ESTÁ LLENO ---

        /* 4. Ahora sí es seguro convertir la fecha para la Base de Datos
        java.sql.Date fechaParaBD = java.sql.Date.valueOf(fecha_nacimiento_date.getValue());

        // Obtenemos los valores de los combos (ya sabemos que no son null)
        String escuelaSeleccionada = EscuelaProcedenciaComboBox.getValue();
        String sexoSeleccionado = sexoComboBox.getValue();

        System.out.println("Guardando usuario: " + usuarioNuevo);

        // AQUÍ LLAMAS A TU CÓDIGO DE BASE DE DATOS (INSERT)
        // insertarUsuario(nombreCompleto, usuarioNuevo, passwordIngresado, escuelaSeleccionada, fechaParaBD, ...);*/
    }

    private void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }
}