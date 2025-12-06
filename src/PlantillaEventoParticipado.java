import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class PlantillaEventoParticipado {
    @FXML
    private Label lblNombreEvento;
    @FXML
    private Label lblSede;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblRol;

    public void setDatosEventoParticipado(String nombre, String fecha) {
        lblNombreEvento.setText(nombre);
        //lblSede.setText(sede);
        lblFecha.setText(fecha);
    }

    @FXML
    void btnMasInfoEvento(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosMasInfo.fxml"));
            Parent root = loader.load();

            Stage stagePaso = new Stage();
            stagePaso.setScene(new Scene(root));
            stagePaso.setTitle("Más Informacion");

            // Esto obliga al usuario a terminar aquí antes de volver a Eventos
            stagePaso.initModality(Modality.APPLICATION_MODAL);
            stagePaso.setResizable(false);
            stagePaso.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnEvaluarEvento(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosEvaluar.fxml"));
            Parent root = loader.load();

            Stage stagePaso = new Stage();
            stagePaso.setScene(new Scene(root));
            stagePaso.setTitle("Más Informacion");

            // Esto obliga al usuario a terminar aquí antes de volver a Eventos
            stagePaso.initModality(Modality.APPLICATION_MODAL);
            stagePaso.setResizable(false);
            stagePaso.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
