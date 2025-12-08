import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainEventosAdmin implements Initializable {

    @FXML private VBox vboxContenedorEventosActvosAdmin; // Donde van las tarjetas

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarEventos();
    }

    @FXML
    void btnAgregarEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CrearEvento.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Nuevo Evento");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.setResizable(false);

            // MAGIA: Espera a que se cierre para refrescar la lista
            stage.showAndWait();

            cargarEventos(); // Refrescar lista

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarEventos() {
        vboxContenedorEventosActvosAdmin.getChildren().clear();

        // Reutilizamos el método que ya tienes en Main
        List<Map<String, Object>> lista = Main.retornarEventos();

        for (Map<String, Object> fila : lista) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEventoAdmin.fxml"));
                AnchorPane tarjeta = loader.load();

                PlantillaEventoAdmin controller = loader.getController();

                String nombre = (String) fila.get("nombre");
                String sede = (String) fila.get("sede");
                String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "Sin fecha";

                controller.setDatosEventoAdmin(nombre, fecha, sede);

                vboxContenedorEventosActvosAdmin.getChildren().add(tarjeta);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}