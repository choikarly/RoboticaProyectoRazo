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

    @FXML private VBox vboxContenedorEventosActvosAdmin;
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
        // En el método cargarEventos() de MainEventosAdmin.java:
        List<Map<String, Object>> lista = Main.retornarEventos();

        if (lista.isEmpty()) {
            vboxContenedorEventosActvosAdmin.setVisible(false);
            vboxContenedorEventosActvosAdmin.setManaged(false);

        } else {
            vboxContenedorEventosActvosAdmin.setVisible(true);
            vboxContenedorEventosActvosAdmin.setManaged(true);
            vboxContenedorEventosActvosAdmin.setSpacing(10);

            try{
                for (Map<String, Object> fila : lista) {
                    // Extraer datos del mapa
                    int idEvento = (int) fila.get("id_evento"); // <--- AQUI SACAMOS EL ID
                    String nombre = (String) fila.get("nombre");
                    String sede = (String) fila.get("sede");
                    String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "Sin fecha";

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEventosAdmin.fxml"));
                    AnchorPane tarjeta = loader.load();

                    PlantillaEventosAdmin controller = loader.getController();


                    // Pasamos los 4 datos (incluyendo el ID)
                    controller.setDatosEventoAdmin(idEvento, nombre, fecha, sede);

                    vboxContenedorEventosActvosAdmin.getChildren().add(tarjeta);

                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}