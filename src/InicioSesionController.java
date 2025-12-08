import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class InicioSesionController {
    @FXML
    private PasswordField txt_contra_oculta;
    @FXML
    private TextField txt_contra_visible;
    @FXML
    private ToggleButton btn_ver;
    @FXML
    private TextField txt_usuario;



    @FXML
    void abrirVentanaRegistro(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Registro.fxml"));
        Scene scene = new Scene(root);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        stage.close();
        Stage newStage = new Stage();
        newStage.setTitle("Formulario de Registro");
        newStage.setScene(scene);
        newStage.setResizable(false);
        newStage.show();
    }

    @FXML
    public void initialize() {
        txt_contra_visible.textProperty().bindBidirectional(txt_contra_oculta.textProperty());

        txt_contra_visible.visibleProperty().bind(btn_ver.selectedProperty());
        txt_contra_oculta.visibleProperty().bind(btn_ver.selectedProperty().not());

        // 3. Opcional: Cambiar el texto del botón según el estado
        btn_ver.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                btn_ver.setText("Ocultar");
            } else {
                btn_ver.setText("Ver");
            }
        });
    }

    @FXML
    void btnIniciarSesion(ActionEvent event) {
        String usuarioIngresado = txt_usuario.getText();
        String passwordIngresado = txt_contra_oculta.getText();

        if (usuarioIngresado.isEmpty() || passwordIngresado.isEmpty()) {
            mostrarAlerta(
                    "Error de Acceso",
                    "Campos sin llenar",
                    "LLene todos los campos que se solicitan."
            );
        } else{
            // 1. Llamamos a la BD UNA SOLA VEZ y guardamos el resultado
            int[] resultados = Main.iniciarSesion(usuarioIngresado, passwordIngresado);
            int idUsuario = resultados[0];
            int grado = resultados[1];

            // 2. Verificamos si el login fue exitoso (ID > 0 y Grado válido)
            if (idUsuario > 0 && grado >= -1) {
                try {
                    Node source = (Node) event.getSource();
                    Stage stageActual = (Stage) source.getScene().getWindow();
                    stageActual.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPrincipal.fxml"));
                    Parent root = loader.load();

                    // Obtenemos el controlador UNA SOLA VEZ
                    MainPrincipalController dashboardController = loader.getController();

                    // Configuramos todos los datos
                    dashboardController.setInicialUsuario(usuarioIngresado);
                    dashboardController.configurarPermisos(grado);

                    // Pasamos el nombre que Main ya recuperó de la BD
                    dashboardController.setNombreCompleto(Main.nombreCompletoUsuario);

                    Stage stageNuevo = new Stage();
                    stageNuevo.setScene(new Scene(root));
                    stageNuevo.setTitle("Concurso Robotica");
                    stageNuevo.setResizable(false);
                    stageNuevo.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "Fallo al abrir ventana", "No se pudo cargar la vista: " + e.getMessage());
                }
            } else {
                // Si el ID es -1 o -2, mostramos error
                mostrarAlerta("Error de Acceso", "Datos Incorrectos", "El usuario o la contraseña que ingresaste no son válidos.");
            }
        }
    }

    //===================================================================================================

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}