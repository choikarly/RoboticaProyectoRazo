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


public class MainPrincipalController {
    @FXML
    private Label lbl_inicial;
    @FXML
    private AnchorPane PanelContenedor;

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
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

            PanelContenedor.getChildren().removeAll();
            PanelContenedor.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista: " + nombreArchivoFxml);
        }
    }

    @FXML
    void btn_infoPersonalDocente(ActionEvent event)  throws IOException{
        cambiarVista("MainInfoPersonal.fxml");
    }

    @FXML
    void btn_MisEquiposDocente(ActionEvent event)  throws IOException{
        cambiarVista("MainMisEquipos.fxml");
    }

    @FXML
    void btn_EventosDocente(ActionEvent event)  throws IOException{
        cambiarVista("MainEventos.fxml");
    }

    /*void btnCerrarSesion(ActionEvent event){

    }
*/
}
