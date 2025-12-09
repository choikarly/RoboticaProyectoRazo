import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class PlantillaDocenteAdmin {

    @FXML private Label lblNombre;
    @FXML private Label lblEscuela;
    @FXML private Label lblRol;

    private int idDocenteGuardado;

    public void setDatosDocentesAdmin(int idDocente, String nombre, String escuela, boolean esCoach, boolean esJuez) {
        this.idDocenteGuardado = idDocente;
        lblNombre.setText(nombre);
        lblEscuela.setText(escuela);

        if (esCoach && esJuez) {
            lblRol.setText("ROL: HÍBRIDO (Coach y Juez)");
            lblRol.setStyle("-fx-text-fill: purple; -fx-font-weight: bold;");
        } else if (esCoach) {
            lblRol.setText("ROL: COACH");
            lblRol.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else if (esJuez) {
            lblRol.setText("ROL: JUEZ");
            lblRol.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        } else {
            lblRol.setText("SIN ROL EN EVENTOS");
            lblRol.setStyle("-fx-text-fill: grey;");
        }
    }

    @FXML
    void btnMasInfoDocentesMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainInfoPersonal.fxml"));
            Parent root = loader.load();

            MainInfoPersonal controller = loader.getController();
            controller.cargarDatos(this.idDocenteGuardado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalle del Docente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}