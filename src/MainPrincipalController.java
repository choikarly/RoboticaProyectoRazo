import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;


public class MainPrincipalController {
    @FXML
    private Label lblNombreCompleto;
    @FXML
    private Label lbl_inicial;
    @FXML
    private AnchorPane PanelContenedor;
    @FXML
    private AnchorPane AdminOptionsContainer;
    @FXML
    private AnchorPane DocenteOptionsContainer;

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
    }

    public void setNombreCompleto(String nombre) {
        if (lblNombreCompleto != null) {
            // Usamos una verificación de seguridad: si 'nombre' es null, usamos un espacio
            String nombreSeguro = (nombre != null) ? nombre : "";
            lblNombreCompleto.setText(nombreSeguro);
        }
    }

    public void setInicialUsuario(String nombreCompleto) {
        if (nombreCompleto != null && !nombreCompleto.isEmpty()) {
            String inicial = nombreCompleto.substring(0, 1);
            lbl_inicial.setText(inicial.toUpperCase());
        } else {
            lbl_inicial.setText("?");
        }
    }

    private void cambiarVista(String nombreArchivoFxml) {
        try {
            // Cargar el archivo FXML hijo
            Parent vista = FXMLLoader.load(getClass().getResource(nombreArchivoFxml));
            PanelContenedor.getChildren().clear();
            PanelContenedor.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista: " + nombreArchivoFxml);
        }
    }

    public void configurarPermisos(int grado){
        System.out.println("Configurando vista para: " + grado);
        if (grado >= 0) {
            // MODO ADMIN: Se ve el panel Admin, se oculta el de Usuario
            AdminOptionsContainer.setVisible(true);
            AdminOptionsContainer.setManaged(true);

            DocenteOptionsContainer.setVisible(false);
            DocenteOptionsContainer.setManaged(false); // Colapsa el espacio

        } else {
            // MODO USUARIO: Al revés
            AdminOptionsContainer.setVisible(false);
            AdminOptionsContainer.setManaged(false);

            DocenteOptionsContainer.setVisible(true);
            DocenteOptionsContainer.setManaged(true);
        }
    }

    @FXML
    void btnMisEquiposDocente(ActionEvent event) throws IOException{
        cambiarVista("MainMisEquipos.fxml");
    }

    @FXML
    void btnEventosDocente(ActionEvent event)  throws IOException{
        cambiarVista("MainEventos.fxml");
    }


    @FXML
    void btnMainDocentesAdmin(ActionEvent event) throws IOException{
        cambiarVista("MainDocentesAdmin.fxml");
    }

    @FXML
    void btnMainEquiposAdmin(ActionEvent event) throws IOException{
        cambiarVista("MainEquiposAdmin.fxml");
    }

    @FXML
    void btnMainEventosAdmin(ActionEvent event) throws IOException{
        cambiarVista("MainEventosAdmin.fxml");
    }

    @FXML
    void btnMainEscuelasAdmin(ActionEvent event){
        cambiarVista("MainEscuelasAdmin.fxml");
    }



    @FXML
    void btnCerrarSesion(ActionEvent event) {
        ButtonType botonSi = new ButtonType("Sí, salir");
        ButtonType botonNo = new ButtonType("No, cancelar");

        // Crear la alerta con TUS botones
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", botonSi, botonNo);
        alert.setTitle("Cerrar Sesión");
        alert.setContentText("¿Quieres cerrar la aplicación?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == botonSi) {
            Platform.exit();
        }
    }

    @FXML
    void btnInfoPersonalDocente(ActionEvent event) throws IOException {
        cambiarVista("MainInfoPersonal.fxml");
    }
}
