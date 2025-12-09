import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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


public class RegistroController implements Initializable {
    @FXML
    private ToggleButton btn_ver;
    private Map<String, Integer> mapaEscuelas = new HashMap<>();

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
        cargarEscuelas();
        sexoComboBox.getItems().addAll("H", "M");
        txt_contra.textProperty().bindBidirectional(txt_contra_oculta.textProperty());
        txt_contra.visibleProperty().bind(btn_ver.selectedProperty());
        txt_contra_oculta.visibleProperty().bind(btn_ver.selectedProperty().not());

        btn_ver.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                btn_ver.setText("Ocultar");
            } else {
                btn_ver.setText("Ver");
            }
        });
    }

    private void cargarEscuelas() {
        // 1. Traemos la lista de la Base de Datos
        List<Map<String, Object>> listaBD = Main.retornarEscuelas();

        // 2. Limpiamos por si acaso
        EscuelaProcedenciaComboBox.getItems().clear();
        mapaEscuelas.clear();

        // 3. Llenamos el ComboBox y el Mapa al mismo tiempo
        for (Map<String, Object> fila : listaBD) {
            String nombre = (String) fila.get("nombre");
            int id = (int) fila.get("id_escuela");

            // Agregamos visualmente al ComboBox
            EscuelaProcedenciaComboBox.getItems().add(nombre);

            // Guardamos logicamente en el Mapa
            mapaEscuelas.put(nombre, id);
        }
    }


    @FXML
    void btnRegistrarse(ActionEvent event) throws IOException {
        // 1. Extraer los datos de texto
        String nombreCompleto = txt_nombrecompleto.getText().trim(); // .trim() elimina espacios extra al inicio/final
        String usuarioNuevo = txt_usuario.getText().trim();

        // Obtenemos la contraseña del campo visible u oculto según corresponda
        String passwordIngresado = txt_contra_oculta.getText();

        String especialidad = txt_especialidad.getText();
        String escuelaProcedencia = EscuelaProcedenciaComboBox.getValue();

        // --- VALIDACIÓN 1: CAMPOS VACÍOS ---
        if (fecha_nacimiento_date.getValue() == null ||
                EscuelaProcedenciaComboBox.getValue() == null ||
                sexoComboBox.getValue() == null ||
                nombreCompleto.isEmpty() ||
                usuarioNuevo.isEmpty() ||
                passwordIngresado.isEmpty() ||
                especialidad.isEmpty()) {

            mostrarAlertaError("ERROR", "Campos Vacíos", "Por favor llena todos los campos de texto.");
            return; // Detenemos la ejecución aquí
        }

         /*VALIDACIÓN 2: NOMBRE SIN NÚMEROS NI SÍMBOLOS ---
        Explicación Regex: ^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$
        Permite letras mayúsculas, minúsculas, vocales con acento, ñ y espacios.*/

        if (!nombreCompleto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            mostrarAlertaError("Error de Formato", "Nombre Inválido",
                    "El nombre solo puede contener letras y espacios (no números ni símbolos).");
            return;
        }
        /*VALIDACIÓN 3: CONTRASEÑA SEGURA (Mayúscula, Número, Especial, Min 6) ---
         (?=.*[0-9])       -> Debe contener al menos un número
         (?=.*[A-Z])       -> Debe contener al menos una letra mayúscula
         (?=.*[@#$%^&+=!._-]) -> Debe contener al menos un carácter especial (puedes agregar más si quieres)
         .{6,}             -> Debe tener al menos 6 caracteres de longitud
        */
        String patronContrasena = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{6,}$";

        if (!passwordIngresado.matches(patronContrasena)) {
            mostrarAlertaError("Seguridad", "Contraseña Débil",
                    "La contraseña debe cumplir con:\n" +
                            "- Mínimo 6 caracteres\n" +
                            "- Al menos una mayúscula (A-Z)\n" +
                            "- Al menos un número (0-9)\n" +
                            "- Al menos un símbolo especial (@ # $ % ^ & + = ! . _ -)");
            return;
        }

        // --- SI PASA LAS VALIDACIONES, CONTINUAMOS CON LA LÓGICA DE BD ---

        java.sql.Date fechaParaBD = java.sql.Date.valueOf(fecha_nacimiento_date.getValue());
        String sexo = sexoComboBox.getSelectionModel().getSelectedItem();

        if (mapaEscuelas.containsKey(escuelaProcedencia)) {

            int idEscuelaParaBD = mapaEscuelas.get(escuelaProcedencia);

            // Llamada al Main
            int codigoResultado = Main.registrarDocente(
                    nombreCompleto,
                    usuarioNuevo,
                    passwordIngresado,
                    fechaParaBD,
                    idEscuelaParaBD,
                    sexo,
                    especialidad
            );
            switch (codigoResultado) {
                case 1: // ÉXITO
                    mostrarAlertaExito("Éxito", "Registro Completado", "Se le redirecionara al Inicio de Sesion");
                    Node source = (Node) event.getSource();
                    Stage stageActual = (Stage) source.getScene().getWindow();
                    stageActual.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioSesion.fxml"));
                    Parent root = loader.load();
                    Stage stageNuevo = new Stage();
                    stageNuevo.setScene(new Scene(root));
                    stageNuevo.setTitle("Inicio de Sesion");
                    stageNuevo.setResizable(false);
                    stageNuevo.show();
                    break;

                case 0:
                    mostrarAlertaError("Registro Fallido",
                            "Usuario Duplicado",
                            "Ese usuario ya existe en el sistema.");
                    limpiarCampos();
                    break;

                case -1:
                    mostrarAlertaError("Registro Fallido",
                            "Edad Insuficiente",
                            "El docente debe ser mayor de edad (18 años) para registrarse.");
                    break;

                case -2:
                default:
                    mostrarAlertaError("Error",
                            "Fallo del Sistema",
                            "Hubo un error al intentar guardar en la BD.");
                    limpiarCampos();
                    break;
            }
        } else {
            mostrarAlertaError("Error",
                    "Escuela Inválida",
                    "La escuela seleccionada no es válida.");
        }
    }


    private void limpiarCampos() {
        txt_nombrecompleto.setText("");
        txt_usuario.setText("");
        txt_contra_oculta.setText("");
        txt_contra.setText("");
        txt_especialidad.setText("");
        fecha_nacimiento_date.setValue(null);
        EscuelaProcedenciaComboBox.setValue(null);
        sexoComboBox.setValue(null);
        txt_nombrecompleto.requestFocus();
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