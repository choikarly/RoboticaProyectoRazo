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
    private VBox vboxContenedorEventosParticipados;
    @FXML
    private Label lblMensajeEventiParticipado;

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
        // 1. Obtener ID del usuario actual
        int idUsuario = Main.usuaioActual;
        System.out.println("Cargando eventos para el usuario ID " + idUsuario);

        // 2. Llamar a la función
        List<Map<String, Object>> lista = Main.retornarEventosParticipados(idUsuario);

        // 3. Validar si está vacío
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

                    // Extraer datos
                    String nombre = (String) fila.get("nombre");
                    String sede = (String) fila.get("sede");
                    String rol = (String) fila.get("mi_rol");

                    // Manejo seguro de fecha (puede ser null)
                    String fecha = (fila.get("fecha") != null) ? fila.get("fecha").toString() : "Pendiente";

                    // Cargar Plantilla
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEvento.fxml"));
                    AnchorPane panelEventoParticipado = loader.load();

                    // Pasar datos al controlador de la plantilla
                    // Nota: Asegúrate que tu PlantillaEventoController tenga setDatos(nombre, sede, fecha)
                    PlantillaEventoParticipado controller = loader.getController();
                    controller.setDatosEventoParticipado(nombre, sede, fecha);

                    // --- CAMBIO DE COLOR SEGÚN ROL ---
                    if ("COACH".equals(rol)) {
                        // VERDE: Evento donde voy como Coach
                        // Ocultar el botón (Desaparece y no ocupa espacio)
                        btnEvaluarEvento.setVisible(false);
                        btnEvaluarEvento.setManaged(false);

                        // Mostrar el botón (Aparece y recupera su espacio)
                        btnMasInfoEvento.setVisible(true);
                        btnMasInfoEvento.setManaged(true);
                        //panelEventoParticipado.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 3; -fx-background-color: #eafaf1;");
                    } else {
                        // AZUL: Evento donde voy como Juez
                        // Ocultar el botón (Desaparece y no ocupa espacio)
                        btnEvaluarEvento.setVisible(true);
                        btnEvaluarEvento.setManaged(true);

                        // Mostrar el botón (Aparece y recupera su espacio)
                        btnMasInfoEvento.setVisible(false);
                        btnMasInfoEvento.setManaged(false);
                        //panelEventoParticipado.setStyle("-fx-border-color: #3498db; -fx-border-width: 3; -fx-background-color: #ebf5fb;");
                    }

                    // Agregar al contenedor
                    vboxContenedorEventosParticipados.getChildren().add(panelEventoParticipado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

