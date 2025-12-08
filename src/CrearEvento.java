import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; // No olvides implements Initializable
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CrearEvento implements Initializable {

    @FXML private TextField txtNombreEvento;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbSede;

    private Map<String, Integer> mapaSedes = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarSedes();
    }

    private void cargarSedes() {
        List<Map<String, Object>> sedes = Main.retornarSedes();

        cbSede.getItems().clear();
        mapaSedes.clear();

        for (Map<String, Object> fila : sedes) {
            String nombre = (String) fila.get("nombre");
            int id = (int) fila.get("id"); // Asegúrate que retornarSedes traiga el "id"

            cbSede.getItems().add(nombre);
            mapaSedes.put(nombre, id);
        }
    }

    @FXML
    void btnCrearEvento(ActionEvent event) {
        // 1. Obtener datos de la vista
        String nombre = txtNombreEvento.getText().trim();
        LocalDate fechaLocal = dpFecha.getValue();
        String nombreSede = cbSede.getValue();

        // 2. Validaciones básicas
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre del evento es obligatorio.");
            return;
        }
        if (fechaLocal == null) {
            mostrarAlerta("Error",
                    "Debes seleccionar una fecha.");
            return;
        }
        if (nombreSede == null) {
            mostrarAlerta("Error",
                    "Debes seleccionar una sede.");
            return;
        }

        // 3. Preparar datos para SQL
        // Convertimos de LocalDate (JavaFX) a java.sql.Date (Base de Datos)
        Date fechaSQL = Date.valueOf(fechaLocal);

        // Obtenemos el ID de la sede usando el mapa
        int idSede = mapaSedes.get(nombreSede);

        // 4. Llamar al Main
        int resultado = Main.crearEvento(nombre, fechaSQL, idSede);

        // 5. Interpretar la respuesta (aviso)
        if (resultado == 1) {
            mostrarAlerta("Éxito",
                    "Evento creado y categorías asignadas correctamente.");
            // Cerrar la ventana
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } else if (resultado == 0) {
            mostrarAlerta("Error",
                    "Ya existe un evento con ese nombre.");

        } else if (resultado == -1) {
            mostrarAlerta("Error",
                    "Sede ocupada: Ya hay un evento en esa fecha y lugar.");

        } else {
            mostrarAlerta("Error",
                    "Ocurrió un error inesperado al guardar.");
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}