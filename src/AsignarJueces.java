import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AsignarJueces implements Initializable {

    private Map<String, Integer> mapaCategorias = new HashMap<>();
    private Map<String, Integer> mapaJueces = new HashMap<>();

    private int idEventoActual;
    private String nombreEventoActual;

    @FXML private Label lblNombreEvento;
    @FXML private Label lblSede;

    @FXML private ComboBox<String> cbCategoriaJuez;
    @FXML private ComboBox<String> cbJuezUno;
    @FXML private ComboBox<String> cbJuezDos;
    @FXML private ComboBox<String> cbJuezTres;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // --- MÉTODO PARA RECIBIR EL ID DEL EVENTO ---
    public void recibirDatosEventoJuez(int idEvento, String nombreEvento, String nombreSede) {
        this.idEventoActual = idEvento;
        lblNombreEvento.setText(nombreEvento);
        lblSede.setText(nombreSede);
        cargarListas();
    }

    @FXML
    void btnAsignarJueces(ActionEvent event) {
        try {
            // 1. Obtener valores de los ComboBox
            String catTxt = cbCategoriaJuez.getValue();
            String j1 = cbJuezUno.getValue();
            String j2 = cbJuezDos.getValue();
            String j3 = cbJuezTres.getValue();

            // 2. Validar que no haya campos vacíos
            if (catTxt == null || j1 == null || j2 == null || j3 == null) {
                mostrarAlertaError("ERROR",
                        "Faltan datos",
                        "Por favor selecciona una categoría y los 3 jueces.");
                return; // Detiene la ejecución
            }

            // 3. Validar que no se repitan los jueces
            if (j1.equals(j2) || j1.equals(j3) || j2.equals(j3)) {
                mostrarAlertaError("ERROR",
                        "Jueces Duplicados",
                        "No puedes asignar al mismo juez dos veces en la misma categoría.");
                return; // Detiene la ejecución
            }

            // 4. Validación de Integridad (Verificar que los IDs existan en los mapas)
            // Esto evita un NullPointerException si por alguna razón el nombre no tiene ID asociado
            if (!mapaCategorias.containsKey(catTxt) ||
                    !mapaJueces.containsKey(j1) ||
                    !mapaJueces.containsKey(j2) ||
                    !mapaJueces.containsKey(j3)) {

                mostrarAlertaError("ERROR",
                        "Error Interno",
                        "No se pudieron obtener los códigos de los jueces o categoría. Intenta cerrar y abrir la ventana.");
                return;
            }

            int idCat = mapaCategorias.get(catTxt);
            int idJ1 = mapaJueces.get(j1);
            int idJ2 = mapaJueces.get(j2);
            int idJ3 = mapaJueces.get(j3);

            // LLAMADA ÚNICA: TODO O NADA
            int resultado = Main.registrarTernaEnBD(this.idEventoActual, idCat, idJ1, idJ2, idJ3);

            if (resultado == 1) {
                mostrarAlertaExito("Éxito",
                        "Los 3 jueces se asignaron correctamente.",
                        "");

                // Actualizar interfaz
                cbCategoriaJuez.setValue(null);
                cbJuezUno.setValue(null); cbJuezDos.setValue(null); cbJuezTres.setValue(null);
                cargarListas();

            } else if (resultado == 0) {
                mostrarAlertaError("Error",
                        "Uno o más de los jueces seleccionados YA estaban asignados a esta categoría anteriormente.",
                        "");
            }else if (resultado == -2) {
                mostrarAlertaError("Conflicto de Interés",
                        "Acción No Permitida",
                        "Uno de los docentes seleccionados es COACH de un equipo inscrito en esta categoría.\n\nNo puede ser Juez y Coach al mismo tiempo.");
            }else {
                mostrarAlertaError("Error",
                        "Ocurrió un error en la base de datos.",
                        "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error Crítico",
                    "",
                    "");
        }
    }

    private void cargarListas() {
        List<Map<String, Object>> cats = Main.retornarCategoriasEvento(this.idEventoActual);
        cbCategoriaJuez.getItems().clear();
        mapaCategorias.clear();

        for (Map<String, Object> fila : cats) {
            String nombre = (String) fila.get("nombre");
            int id = (int) fila.get("id");
            cbCategoriaJuez.getItems().add(nombre);
            mapaCategorias.put(nombre, id);
        }

        // B. Cargar Docentes (USANDO TU PROCESO REUTILIZADO)
        List<Map<String, Object>> profes = Main.retornarDocentes();
        cbJuezUno.getItems().clear(); cbJuezDos.getItems().clear(); cbJuezTres.getItems().clear();
        mapaJueces.clear();
        for (Map<String, Object> fila : profes) {
            String nombre = (String) fila.get("nombre");
            int id = (int) fila.get("id");
            cbJuezUno.getItems().add(nombre); cbJuezDos.getItems().add(nombre); cbJuezTres.getItems().add(nombre);
            mapaJueces.put(nombre, id);
        }
    }

    public void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    public void mostrarAlertaExito(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

}