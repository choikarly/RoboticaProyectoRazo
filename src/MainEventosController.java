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
import java.util.List;
import java.util.Map;

public class MainEventosController {
    @FXML
    private VBox vboxContenedorEventos;
    @FXML
    private Label lblMnesajeEventosDisponibles;

    @FXML
    private VBox vboxContenedorEventosParticipados;
    @FXML
    private Label lblMensajeEventiParticipado;

    // Estos botones parecen ser para uso global o referencia,
    // pero recuerda que cada plantilla tiene sus propios botones.
    @FXML
    private Button btnEvaluarEvento;
    @FXML
    private Button btnMasInfoEvento;

    @FXML
    public void initialize() {
        cargarEventos();
        cargarEventosDelDocente();
    }

    private void cargarEventosDelDocente() {
        vboxContenedorEventosParticipados.getChildren().clear();
        int idUsuario = Main.usuaioActual;
        System.out.println("Cargando eventos para el usuario ID " + idUsuario);

        List<Map<String, Object>> lista = Main.retornarEventosParticipados(idUsuario);

        if (lista.isEmpty()) {
            vboxContenedorEventosParticipados.setVisible(false);
            vboxContenedorEventosParticipados.setManaged(false);

            lblMensajeEventiParticipado.setVisible(true);
            lblMensajeEventiParticipado.setManaged(true);
            lblMensajeEventiParticipado.setText("No estás asignado a ningún evento aún.");
        } else {
            vboxContenedorEventosParticipados.setVisible(true);
            vboxContenedorEventosParticipados.setManaged(true);
            vboxContenedorEventosParticipados.setSpacing(10);

            lblMensajeEventiParticipado.setVisible(false);
            lblMensajeEventiParticipado.setManaged(false);

            try {
                for (Map<String, Object> fila : lista) {
                    String nombre = (String) fila.get("nombre");
                    String sede = (String) fila.get("sede");
                    String rol = (String) fila.get("mi_rol");
                    String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "Pendiente";

                    // --- CORRECCIÓN AQUÍ ---
                    // Antes tenías "PlantillaEvento.fxml", debe ser "PlantillaEventoParticipado.fxml"
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEventoParticipado.fxml"));
                    AnchorPane panelEventoParticipado = loader.load();

                    // Ahora sí coincide el FXML con su clase controladora
                    PlantillaEventoParticipado controller = loader.getController();
                    controller.setDatosEventoParticipado(nombre, sede, fecha);

                    // Lógica visual según el rol (Opcional: Esto lo podrías mover adentro de la plantilla si prefieres)
                    /* Nota: Como btnEvaluarEvento es parte de ESTE controlador principal y no de la plantilla,
                       estas líneas de abajo no ocultarán los botones DENTRO de la tarjeta.
                       Si quieres ocultar los botones de la tarjeta, debes crear un método en
                       PlantillaEventoParticipado.java, ej: controller.configurarBotones(rol);
                    */

                    // Agregar al contenedor
                    vboxContenedorEventosParticipados.getChildren().add(panelEventoParticipado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarEventos() {
        vboxContenedorEventos.getChildren().clear();
        List<Map<String, Object>> listaEventos = Main.retornarEventos();

        if (listaEventos.isEmpty()) {
            vboxContenedorEventos.setVisible(false);
            vboxContenedorEventos.setManaged(false);

            lblMnesajeEventosDisponibles.setVisible(true);
            lblMnesajeEventosDisponibles.setManaged(true);
        } else {
            vboxContenedorEventos.setVisible(true);
            vboxContenedorEventos.setManaged(true);
            vboxContenedorEventos.setSpacing(10);

            lblMnesajeEventosDisponibles.setVisible(false);
            lblMnesajeEventosDisponibles.setManaged(false);

            try {
                for (Map<String, Object> fila : listaEventos) {
                    int idEvento = (int) fila.get("id_evento");
                    String nombre = (String) fila.get("nombre");
                    String sede = (String) fila.get("sede");
                    String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "";

                    // Aquí sí usamos la plantilla normal
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEvento.fxml"));
                    AnchorPane panelEvento = loader.load();

                    PlantillaEvento controller = loader.getController();
                    controller.setDatos(idEvento, nombre, sede, fecha);

                    vboxContenedorEventos.getChildren().add(panelEvento);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al cargar la plantilla FXML");
            }
        }
    }
}