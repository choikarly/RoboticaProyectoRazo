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

public class InfoEventos {
    @FXML
    void btnContinuar(ActionEvent event) {
        try {
            // 1. CERRAR la ventana actual
            Node source = (Node) event.getSource();
            Stage stageActual = (Stage) source.getScene().getWindow();
            stageActual.close();

            // 2. ABRIR la siguiente ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroDeEquipo.fxml"));
            Parent root = loader.load();

            Stage stagePaso2 = new Stage();
            stagePaso2.setScene(new Scene(root));
            stagePaso2.setTitle("Integrantes");

            stagePaso2.initModality(Modality.APPLICATION_MODAL);
            stagePaso2.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
