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

    @FXML
    public void initialize() {
        cargarEventos();
        cargarEventosDelDocente();
    }

    private void cargarEventosDelDocente() {
        vboxContenedorEventosParticipados.getChildren().clear();
        int idUsuario = Main.usuaioActual;

        System.out.println("Buscando eventos para usuario ID: " + idUsuario);

        List<Map<String, Object>> lista = Main.retornarEventosParticipados(idUsuario);

        if (lista.isEmpty()) {
            System.out.println("La lista de eventos participados llegó VACÍA.");
            vboxContenedorEventosParticipados.setVisible(false);
            vboxContenedorEventosParticipados.setManaged(false);

            lblMensajeEventiParticipado.setVisible(true);
            lblMensajeEventiParticipado.setManaged(true);
        } else {
            System.out.println("Se encontraron " + lista.size() + " eventos participados.");

            vboxContenedorEventosParticipados.setVisible(true);
            vboxContenedorEventosParticipados.setManaged(true);
            vboxContenedorEventosParticipados.setSpacing(10);

            lblMensajeEventiParticipado.setVisible(false);
            lblMensajeEventiParticipado.setManaged(false);

            for (Map<String, Object> fila : lista) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEventoParticipado.fxml"));
                    AnchorPane panelEventoParticipado = loader.load();

                    PlantillaEventoParticipado controller = loader.getController();

                    // 1. OBTENER DATOS (Incluyendo el ID que faltaba)
                    // Validamos si viene nulo para evitar NullPointerException
                    int idEvento = (fila.get("id_evento") != null) ? (int) fila.get("id_evento") : 0;
                    String nombre = (String) fila.get("nombre");
                    String sede = (String) fila.get("sede");
                    String rol = (String) fila.get("mi_rol"); // Asegúrate que en Main.java la clave sea "mi_rol"
                    String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "Pendiente";

                    // 2. PASAR DATOS A LA PLANTILLA
                    controller.setDatosEventoParticipado(idEvento, nombre, sede, fecha, rol);

                    vboxContenedorEventosParticipados.getChildren().add(panelEventoParticipado);
                } catch (IOException e) {
                    System.err.println("Error al cargar FXML de plantilla participada:");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error general al procesar fila de evento:");
                    e.printStackTrace();
                }
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