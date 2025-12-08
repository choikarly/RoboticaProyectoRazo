import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainMisEquipos {
    @FXML
    private VBox vboxContenedorMisEquipos;
    @FXML
    private Label lblMensajeEquipos;

    @FXML
    public void initialize() {
        cargarMisEquipos();
    }

    private void cargarMisEquipos() {
        // 1. Obtener el ID del coach actual (asegúrate de tener este dato guardado tras el login)
        int idCoach = Main.usuaioActual; // O como tengas guardada la sesión
        vboxContenedorMisEquipos.getChildren().clear();

        // 2. Llamar a la función
        List<Map<String, Object>> misEquipos = Main.retornarEquiposCoach(idCoach);

        // 3. Verificamos si hay resultados
        if (misEquipos.isEmpty()) {
            // CASO VACÍO: Ocultamos contenedor, mostramos mensaje
            vboxContenedorMisEquipos.setVisible(false);
            vboxContenedorMisEquipos.setManaged(false);

            lblMensajeEquipos.setVisible(true);
            lblMensajeEquipos.setManaged(true);
        } else {
            // CASO CON DATOS: Mostramos contenedor, ocultamos mensaje
            vboxContenedorMisEquipos.setVisible(true);
            vboxContenedorMisEquipos.setManaged(true);
            vboxContenedorMisEquipos.setSpacing(10); // Espacio entre tarjetas

            lblMensajeEquipos.setVisible(false);
            lblMensajeEquipos.setManaged(false);

            // 4. Recorremos la lista y creamos las tarjetas
            try {
                // 3. Recorrer y mostrar
                for (Map<String, Object> fila : misEquipos) {
                    String nombreEquipo = (String) fila.get("equipo");
                    String nombreEvento = (String) fila.get("evento");
                    String categoria = (String) fila.get("categoria");
                    String escuela = (String) fila.get("escuela");

                    System.out.println("Equipo: " + nombreEquipo + " en " + nombreEvento + " (" + categoria + ")");
                    // Cargar el "Molde" (PlantillaEvento.fxml)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaMisEquipos.fxml"));
                    AnchorPane panelMisEquipos = loader.load();

                    // Obtener el controlador del molde
                    PlantillaMisEquipos controller = loader.getController();
                    controller.setDatosMisEquipos(nombreEquipo, nombreEvento, escuela, categoria);

                    // Agregamos la tarjeta al VBox
                    vboxContenedorMisEquipos.getChildren().add(panelMisEquipos);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al cargar la plantilla FXML");
            }

        }

    }

}
