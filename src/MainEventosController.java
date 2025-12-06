import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MainEventosController {
    @FXML
    private VBox vboxContenedorEventos;
    @FXML
    private Label lblMnesajeEventosDisponibles;

    @FXML
    public void initialize() {
        cargarEventos();
    }

    private void cargarEventos() {
        // 1. Limpiamos la lista por si acaso
        vboxContenedorEventos.getChildren().clear();
        vboxContenedorEventos.setVisible(false);
        vboxContenedorEventos.setManaged(false);

        String sql = "SELECT nombre, fecha FROM evento";

        try (Connection con = Main.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean hayEventos = false;

            while (rs.next()) {
                hayEventos = true;
                String nombre = rs.getString("nombre");
                //String sede = rs.getString("sede");
                String fecha = rs.getString("fecha"); // O rs.getDate().toString()

                // 3. Cargar el "Molde" (ItemEvento.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEvento.fxml"));
                AnchorPane panelEvento = loader.load();

                // 4. Obtener el controlador del molde para pasarle los datos
                PlantillaEvento controller = loader.getController();
                controller.setDatos(nombre, fecha);
                vboxContenedorEventos.getChildren().add(panelEvento);

                // (Opcional) Agregar espacio entre elementos
                vboxContenedorEventos.setSpacing(10);

                if (hayEventos) {
                    vboxContenedorEventos.setVisible(true);
                    vboxContenedorEventos.setManaged(true);

                    lblMnesajeEventosDisponibles.setVisible(false);
                    lblMnesajeEventosDisponibles.setManaged(false);

                } else {
                    vboxContenedorEventos.setVisible(false);
                    vboxContenedorEventos.setManaged(false);

                    lblMnesajeEventosDisponibles.setVisible(true);
                    lblMnesajeEventosDisponibles.setManaged(true);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
