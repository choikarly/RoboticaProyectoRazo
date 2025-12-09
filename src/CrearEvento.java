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
    @FXML private DatePicker dateEvento;
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
        try {
            String nombre = txtNombreEvento.getText();
            LocalDate fechaLocal = dateEvento.getValue();
            String nombreSede = cbSede.getValue();

            // 2. Validaciones básicas (Aquí detenemos si falta algo)
            if (nombre.isEmpty()) {
                mostrarAlerta("Error de Validación",
                        "El nombre del evento es obligatorio.");
                return; // <--- DETIENE EL CÓDIGO AQUÍ
            }

            if (fechaLocal == null) {
                mostrarAlerta("Error de Validación",
                        "Debes seleccionar una fecha.");
                return; // <--- DETIENE EL CÓDIGO AQUÍ
            }

            if (nombreSede == null || nombreSede.isEmpty()) {
                mostrarAlerta("Error de Validación",
                        "Debes seleccionar una sede de la lista.");
                return; // <--- DETIENE EL CÓDIGO AQUÍ
            }

            // Validación de seguridad: Verificar que la sede existe en el mapa
            // Esto evita un NullPointerException si el mapa no cargó bien
            if (!mapaSedes.containsKey(nombreSede)) {
                mostrarAlerta("Error Interno",
                        "No se pudo obtener el ID de la sede seleccionada.");
                return;
            }

            // 3. Preparar datos para SQL
            Date fechaSQL = Date.valueOf(fechaLocal);
            int idSede = mapaSedes.get(nombreSede);

            // 4. Llamar al Main
            int resultado = Main.crearEvento(nombre, fechaSQL, idSede);

            // 5. Interpretar la respuesta (aviso)
            if (resultado == 1) {
                mostrarAlerta("Éxito", "Evento creado y categorías asignadas correctamente.");

                // Cerrar la ventana actual de forma segura
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();

            } else if (resultado == 0) {
                mostrarAlerta("Duplicado", "Ya existe un evento con ese nombre.");

            } else if (resultado == -1) {
                mostrarAlerta("Sede Ocupada", "Ya hay un evento registrado en esa sede y fecha.");

            } else {
                mostrarAlerta("Error Base de Datos", "No se pudo guardar el evento. Código de error: " + resultado);
            }

        } catch (Exception e) {
            // 6. CACHAR CUALQUIER ERROR INESPERADO
            e.printStackTrace(); // Imprime el error en la consola para que tú lo veas
            mostrarAlerta("Error Crítico", "Ocurrió un error inesperado: " + e.getMessage());
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