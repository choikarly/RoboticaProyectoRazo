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
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroDeEquipo implements Initializable {

    @FXML private Label lblNombreEquipo;
    @FXML private Label lblNombreEscuela;

    // ComboBoxes para seleccionar alumnos
    @FXML private ComboBox<AlumnoItem> cbIntegranteUno;
    @FXML private ComboBox<AlumnoItem> cbIntegranteDos;
    @FXML private ComboBox<AlumnoItem> cbIntegranteTres;

    private int idEquipoActual;
    private int idEventoActual;
    private int idEscuelaActual;
    private int idCategoriaCalculada;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialización vacía, esperamos a recibir datos
    }

    public void recibirDatosInscripcion(int idEquipo, int idEvento) {
        this.idEquipoActual = idEquipo;
        this.idEventoActual = idEvento;

        // 1. Obtener datos del coach y escuela
        int idCoach = Main.usuaioActual;
        this.idEscuelaActual = Main.obtenerIdEscuelaDelDocente(idCoach);
        String nombreEscuela = Main.obtenerNombreEscuela(idCoach);

        // 2. Obtener Categoría (Nivel de la escuela)
        this.idCategoriaCalculada = Main.obtenerNivelEscuela(this.idEscuelaActual);

        // 3. Actualizar UI
        if(lblNombreEscuela != null) lblNombreEscuela.setText(nombreEscuela);
        if(lblNombreEquipo != null) lblNombreEquipo.setText("Equipo ID: " + idEquipo);

        // 4. Cargar alumnos en los combos
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
            Parent root = loader.load(); // Ahora esto funcionará porque el FXML tiene controller

            // --- IMPORTANTE: Pasar el ID de la escuela ---
            RegistroParticipante controller = loader.getController();
            controller.setDatosEscuela(this.idEscuelaActual);
            // ---------------------------------------------

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Nuevo Alumno");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

            // Al volver, recargamos la lista para ver al nuevo alumno
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

    // Clase auxiliar para que el ComboBox muestre el nombre pero guarde el ID
    public static class AlumnoItem {
        int id;
        String nombre;

        public AlumnoItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}