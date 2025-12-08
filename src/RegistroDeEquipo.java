import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroDeEquipo implements Initializable {

    @FXML private Label lblNombreEquipo;
    @FXML private Label lblNombreEscuela;
    @FXML private ComboBox<AlumnoItem> cbIntegranteUno;
    @FXML private ComboBox<AlumnoItem> cbIntegranteDos;
    @FXML private ComboBox<AlumnoItem> cbIntegranteTres;

    private int idEquipoActual;
    private int idEventoActual;
    private int idEscuelaActual;
    private int idCategoriaCalculada; // Aquí guardamos si es Primaria(1), Uni(4), etc.

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void recibirDatosInscripcion(int idEquipo, String nombreEquipo, int idEvento) {
        this.idEquipoActual = idEquipo;
        this.idEventoActual = idEvento;

        int idCoach = Main.usuaioActual;
        this.idEscuelaActual = Main.obtenerIdEscuelaDelDocente(idCoach);
        String nombreEscuela = Main.obtenerNombreEscuela(idCoach);

        // Obtenemos el nivel (1=Primaria, 2=Secundaria, etc.)
        this.idCategoriaCalculada = Main.obtenerNivelEscuela(this.idEscuelaActual);

        if(lblNombreEscuela != null) lblNombreEscuela.setText(nombreEscuela);
        if(lblNombreEquipo != null) lblNombreEquipo.setText(nombreEquipo);

        cargarAlumnos();
    }

    private void cargarAlumnos() {
        List<Map<String, Object>> alumnos = Main.retornarAlumnosPorEscuela(idEscuelaActual);
        cbIntegranteUno.getItems().clear();
        cbIntegranteDos.getItems().clear();
        cbIntegranteTres.getItems().clear();

        for (Map<String, Object> fila : alumnos) {
            int id = (int) fila.get("id");
            String nombre = (String) fila.get("nombre");
            AlumnoItem item = new AlumnoItem(id, nombre);
            cbIntegranteUno.getItems().add(item);
            cbIntegranteDos.getItems().add(item);
            cbIntegranteTres.getItems().add(item);
        }
    }

    @FXML
    void btnGuardar(ActionEvent event) {
        if (cbIntegranteUno.getValue() == null || cbIntegranteDos.getValue() == null || cbIntegranteTres.getValue() == null) {
            mostrarAlerta("Error", "Selección incompleta", "Debes seleccionar 3 integrantes.");
            return;
        }
        int p1 = cbIntegranteUno.getValue().id;
        int p2 = cbIntegranteDos.getValue().id;
        int p3 = cbIntegranteTres.getValue().id;

        if (p1 == p2 || p1 == p3 || p2 == p3) {
            mostrarAlerta("Error", "Integrantes repetidos", "No puedes seleccionar al mismo alumno dos veces.");
            return;
        }

        int idCoach = Main.usuaioActual;
        int resultado = Main.registrarEquipo(idCoach, idEquipoActual, idEventoActual, idCategoriaCalculada, p1, p2, p3);

        if (resultado == 1) {
            mostrarAlertaInfo("Éxito", "Inscripción Realizada", "El equipo ha sido inscrito al evento correctamente.");
            ((Stage) ((Node)event.getSource()).getScene().getWindow()).close();
        } else if (resultado == -1) {
            mostrarAlerta("Error", "Ya registrado", "Este equipo ya está inscrito en este evento.");
        } else {
            mostrarAlerta("Error", "Fallo en BD", "Ocurrió un error al guardar la inscripción.");
        }
    }

    @FXML
    void btnNuevoParticipante(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroParticipante.fxml"));
            Parent root = loader.load();

            RegistroParticipante controller = loader.getController();

            // ¡IMPORTANTE! Pasamos el ID de la escuela Y el Nivel (categoría)
            controller.setDatosEscuela(this.idEscuelaActual, this.idCategoriaCalculada);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Nuevo Alumno");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            cargarAlumnos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAlertaInfo(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public static class AlumnoItem {
        int id;
        String nombre;
        public AlumnoItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
    }
}