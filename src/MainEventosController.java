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
import java.util.List;
import java.util.Map;


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
        // 1. Limpiamos la vista actual
        vboxContenedorEventos.getChildren().clear();

        // 2. Llamamos a tu función estática del Main (que usa el SP retornar_eventos)
        List<Map<String, Object>> listaEventos = Main.retornarEventos();

        // 3. Verificamos si hay resultados
        if (listaEventos.isEmpty()) {
            // CASO VACÍO: Ocultamos contenedor, mostramos mensaje
            vboxContenedorEventos.setVisible(false);
            vboxContenedorEventos.setManaged(false);

            lblMnesajeEventosDisponibles.setVisible(true);
            lblMnesajeEventosDisponibles.setManaged(true);
        } else {
            // CASO CON DATOS: Mostramos contenedor, ocultamos mensaje
            vboxContenedorEventos.setVisible(true);
            vboxContenedorEventos.setManaged(true);
            vboxContenedorEventos.setSpacing(10); // Espacio entre tarjetas

            lblMnesajeEventosDisponibles.setVisible(false);
            lblMnesajeEventosDisponibles.setManaged(false);

            // 4. Recorremos la lista y creamos las tarjetas
            try {
                for (Map<String, Object> fila : listaEventos) {

                    // Extraemos los datos del Mapa
                    String nombre = (String) fila.get("nombre");
                    String sede = (String) fila.get("sede"); // ¡Ahora ya tienes la sede disponible!
                    // Convertimos la fecha a String. Si es null, ponemos cadena vacía para que no explote
                    String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "";

                    // Cargar el "Molde" (PlantillaEvento.fxml)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEvento.fxml"));
                    AnchorPane panelEvento = loader.load();

                    // Obtener el controlador del molde
                    PlantillaEvento controller = loader.getController();
                    controller.setDatos(nombre,sede, fecha);

                    // FORZAR ALTURA MINIMA (Solo para probar si es problema de colapso)



                    // Agregamos la tarjeta al VBox
                    vboxContenedorEventos.getChildren().add(panelEvento);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al cargar la plantilla FXML");
            }
        }
    }
}

