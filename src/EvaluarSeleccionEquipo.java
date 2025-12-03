import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class EvaluarSeleccionEquipo {
    @FXML
    void btnComenzarEvaluacion(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stageActual = (Stage) source.getScene().getWindow();
            stageActual.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("EvaluarCriterioProgramacion.fxml"));
            Parent root = loader.load();

            Stage stagePaso = new Stage();
            stagePaso.setScene(new Scene(root));
            stagePaso.setTitle("Evaluacion");

            stagePaso.initModality(Modality.APPLICATION_MODAL);
            stagePaso.setResizable(false);
            stagePaso.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
