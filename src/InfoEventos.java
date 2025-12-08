import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class InfoEventos implements Initializable {
    @FXML private ComboBox<String> cbEquipos;
    @FXML private Label lblNombreEvento;
    @FXML private Label lblFecha;
    @FXML private Label lblSede;

    private Map<String, Integer> mapaEquipos = new HashMap<>();
    private int idEventoActual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarEquiposDelCoach();
    }

    public void recibirDatosEvento(int idEvento, String nombre, String fecha, String sede) {
        this.idEventoActual = idEvento;
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        lblSede.setText(sede);
    }

    @FXML
    void btnContinuar(ActionEvent event) {
        try {
            String nombreEquipo = cbEquipos.getValue();

            // --- VALIDACIÓN CRÍTICA PARA EVITAR EL ERROR NULLPOINTER ---
            if (nombreEquipo == null || !mapaEquipos.containsKey(nombreEquipo)) {
                mostrarAlertaError("Atención", "Equipo no seleccionado", "Por favor selecciona un equipo para continuar.");
                return;
            }

            int idEquipoSeleccionado = mapaEquipos.get(nombreEquipo);

            // Cargar la siguiente ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroDeEquipo.fxml"));
            Parent root = loader.load();

            // Pasar los datos a RegistroDeEquipo
            RegistroDeEquipo controller = loader.getController();
            controller.recibirDatosInscripcion(idEquipoSeleccionado, this.idEventoActual);

            Stage stagePaso2 = new Stage();
            stagePaso2.setScene(new Scene(root));
            stagePaso2.setTitle("Inscripción de Participantes");
            stagePaso2.initModality(Modality.APPLICATION_MODAL);
            stagePaso2.setResizable(false);

            stagePaso2.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA Y CAUSABA EL ERROR "LoadException" ---
    @FXML
    void btnIrARegistrarEquipo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CrearEquipo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Nuevo Equipo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait(); // Espera a que termine

            // Al volver, recarga la lista para que aparezca el nuevo equipo
            cargarEquiposDelCoach();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarEquiposDelCoach() {
        int idCoach = Main.usuaioActual;
        // Usamos el método nuevo que trae TODOS los equipos de la escuela
        List<Map<String, Object>> lista = Main.retornarEquiposDocente(idCoach);

        cbEquipos.getItems().clear();
        mapaEquipos.clear();

        for (Map<String, Object> fila : lista) {
            String nombreEquipo = (String) fila.get("nombre");
            int id = (int) fila.get("id_equipo");
            cbEquipos.getItems().add(nombreEquipo);
            mapaEquipos.put(nombreEquipo, id);
        }
    }

    public void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}