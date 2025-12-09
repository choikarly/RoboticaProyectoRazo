import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistrarSede implements Initializable {

    @FXML private TextField txtNombreSede;
    @FXML private ComboBox<String> cbCiudad;

    // Mapa para guardar ID oculto de la ciudad
    private Map<String, Integer> mapaCiudades = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCiudades();
    }

    private void cargarCiudades() {
        List<Map<String, Object>> ciudades = Main.retornarCiudades();

        cbCiudad.getItems().clear();
        mapaCiudades.clear();

        for (Map<String, Object> fila : ciudades) {
            String nombre = (String) fila.get("nombre");
            int id = (int) fila.get("id");

            cbCiudad.getItems().add(nombre);
            mapaCiudades.put(nombre, id);
        }
    }

    @FXML
    void btnGuardarSede(ActionEvent event) {
        String nombre = txtNombreSede.getText().trim();
        String nombreCiudad = cbCiudad.getValue();

        // VALIDACIONES
        if (nombre.isEmpty()) {
            mostrarAlerta("Atención", "Escribe el nombre de la sede.");
            return;
        }
        if (nombreCiudad == null) {
            mostrarAlerta("Atención", "Debes seleccionar una ciudad.");
            return;
        }

        // Recuperar ID de la ciudad seleccionada
        int idCiudad = mapaCiudades.get(nombreCiudad);

        // Llamar al Main
        int resultado = Main.registrarSede(nombre, idCiudad);

        if (resultado == 1) {
            mostrarAlerta("Éxito", "Sede registrada correctamente.");

            // Cerrar la ventana para regresar a CrearEvento
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

        } else if (resultado == -1) {
            mostrarAlerta("Duplicado", "Ya existe una sede con ese nombre en la ciudad seleccionada.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar la sede en la base de datos.");
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