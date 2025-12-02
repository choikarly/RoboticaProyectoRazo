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


public class MainEventosController {
    @FXML
    void btnRegistrarEquiposInfoEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventos.fxml"));
            Parent root = loader.load();

            // 2. Crear el escenario (Stage) nuevo
            Stage stagePaso1 = new Stage();
            stagePaso1.setScene(new Scene(root));
            stagePaso1.setTitle("Informacion Evento");

            // Esto obliga al usuario a terminar aquí antes de volver a Eventos
            stagePaso1.initModality(Modality.APPLICATION_MODAL);
            stagePaso1.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
