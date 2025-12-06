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
        cargarEscuelasDesdeBD();
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

    @FXML
    void btnRegistrarse(ActionEvent event) throws IOException {
        // 1. Extraer los datos de texto
        String nombreCompleto = txt_nombrecompleto.getText();
        String usuarioNuevo = txt_usuario.getText();
        String passwordIngresado = txt_contra_oculta.getText();
        String especialidad = txt_especialidad.getText();
        String sexo = sexoComboBox.getSelectionModel().getSelectedItem();
        String escuelaProcedencia =  EscuelaProcedenciaComboBox.getValue();

        if (fecha_nacimiento_date.getValue() == null || EscuelaProcedenciaComboBox.getValue() == null || sexoComboBox.getValue() == null || nombreCompleto.isEmpty() || usuarioNuevo.isEmpty() || passwordIngresado.isEmpty() || especialidad.isEmpty()) {
            mostrarAlertaError("ERROR",
                    "Campos Vacíos",
                    "Por favor llena todos los campos de texto.");
        } else {
            java.sql.Date fechaParaBD = java.sql.Date.valueOf(fecha_nacimiento_date.getValue());
            int idEscuelaParaBD = mapaEscuelas.get(escuelaProcedencia);

            int codigoResultado = Main.registrarDocente(
                    nombreCompleto,
                    usuarioNuevo,
                    passwordIngresado,
                    fechaParaBD,
                    idEscuelaParaBD,
                    sexo,
                    especialidad
            );
            // 2. EVALUAR EL NÚMERO
            switch (codigoResultado) {
                case 1:
                    // ÉXITO
                    mostrarAlertaExito(
                            "Éxito",
                            "Registro Completado",
                            "Cierra esta ventana para ir al Inicio de Sesion");
                    limpiarCampos();
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

                case -1:
                    // DUPLICADO
                    mostrarAlertaError(
                            "Atención",
                            "Usuario Duplicado",
                            "Ese usuario o ID ya existe en el sistema.");
                    break;

                case -2:
                    // ERROR GENERAL
                    mostrarAlertaError(
                            "Error",
                            "Fallo del Sistema",
                            "Hubo un error al intentar guardar en la base de datos.");
                    break;

                default:
                    mostrarAlertaError(
                            "Error",
                            "Fallo del Sistema",
                            "Hubo un error al intentar guardar en la base de datos.");
                    break;
            }
        }
    }
    public void cargarEscuelasDesdeBD() {
        String sql = "SELECT id_escuela, nombre FROM escuela";

        try (Connection conn = Main.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Limpiamos por si acaso
            EscuelaProcedenciaComboBox.getItems().clear();
            mapaEscuelas.clear();
            while (rs.next()) {
                int id = rs.getInt("id_escuela");
                String nombre = rs.getString("nombre");
                EscuelaProcedenciaComboBox.getItems().add(nombre);
                mapaEscuelas.put(nombre, id);
            }
        } catch (SQLException e) {
            mostrarAlertaError("Error", "Error al cargar escuelas", e.getMessage());
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