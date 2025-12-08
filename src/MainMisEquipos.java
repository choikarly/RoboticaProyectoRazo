import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainMisEquipos {

    @FXML
    private VBox vboxContenedorMisEquipos;
    @FXML
    private Label lblMensajeEquipos;

    // --- NUEVO: Agregamos la referencia al contenedor padre ---
    @FXML
    private AnchorPane paneContenedorListaEquipos;

    @FXML
    public void initialize() {
        cargarMisEquipos();
    }

    private void cargarMisEquipos() {
        int idCoach = Main.usuaioActual;
        vboxContenedorMisEquipos.getChildren().clear();

        List<Map<String, Object>> misEquipos = Main.retornarEquiposCoach(idCoach);

        if (misEquipos.isEmpty()) {
            // CASO VACÍO:
            // 1. Ocultamos el panel contenedor (El ScrollPane y todo lo de adentro)
            if(paneContenedorListaEquipos != null) paneContenedorListaEquipos.setVisible(false);
            if(paneContenedorListaEquipos != null) paneContenedorListaEquipos.setManaged(false);

            // 2. Mostramos el mensaje de "No tienes equipos"
            lblMensajeEquipos.setVisible(true);
            lblMensajeEquipos.setManaged(true);

        } else {
            // CASO CON DATOS:
            // 1. Hacemos VISIBLE el panel contenedor
            if(paneContenedorListaEquipos != null) paneContenedorListaEquipos.setVisible(true);
            if(paneContenedorListaEquipos != null) paneContenedorListaEquipos.setManaged(true);

            // 2. Ocultamos el mensaje
            lblMensajeEquipos.setVisible(false);
            lblMensajeEquipos.setManaged(false);

            vboxContenedorMisEquipos.setSpacing(10);

            try {
                for (Map<String, Object> fila : misEquipos) {
                    String nombreEquipo = (String) fila.get("equipo");
                    String nombreEvento = (String) fila.get("evento");
                    String categoria = (String) fila.get("categoria");
                    String escuela = (String) fila.get("escuela");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaMisEquipos.fxml"));
                    AnchorPane tarjetaEquipo = loader.load();

                    PlantillaMisEquipos controller = loader.getController();
                    controller.setDatosMisEquipos(nombreEquipo, nombreEvento, escuela, categoria);

                    vboxContenedorMisEquipos.getChildren().add(tarjetaEquipo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}