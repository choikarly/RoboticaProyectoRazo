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
            String catTxt = cbCategoriaJuez.getValue();
            String j1 = cbJuezUno.getValue();
            String j2 = cbJuezDos.getValue();
            String j3 = cbJuezTres.getValue();

            if (catTxt == null || j1 == null || j2 == null || j3 == null) {
                mostrarAlertaError("ERROR",
                        "Faltan datos",
                        "Por favor selecciona una categoría y los 3 jueces.");
                return;
            }

            if (j1.equals(j2) || j1.equals(j3) || j2.equals(j3)) {
                mostrarAlertaError("ERROR",
                        "Jueces Duplicados",
                        "No puedes asignar al mismo juez dos veces en la misma categoría.");
                return;
            }


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
            String resultado = Main.registrarTernaEnBD(this.idEventoActual, idCat, idJ1, idJ2, idJ3);

            if (resultado.equals("1")) {
                mostrarAlertaExito("Éxito", "Asignación Correcta", "Los 3 jueces se asignaron correctamente.");

                cbCategoriaJuez.setValue(null);
                cbJuezUno.setValue(null); cbJuezDos.setValue(null); cbJuezTres.setValue(null);
                cargarListas();

            } else if (resultado.equals("0")) {
                mostrarAlertaError("Error", "Jueces Repetidos", "Uno o más jueces ya estaban asignados a esta categoría.");

            } else if (resultado.startsWith("CONFLICTO|")) {

                // Separamos el texto: "CONFLICTO|Juan Perez" -> ["CONFLICTO", "Juan Perez"]
                String[] partes = resultado.split("\\|");
                String nombreDocente = (partes.length > 1) ? partes[1] : "Desconocido";

                mostrarAlertaError("Conflicto de Interés",
                        "Acción No Permitida",
                        "El docente '" + nombreDocente + "' es COACH de un equipo inscrito en esta categoría.\n\n" +
                                "No puede ser Juez y Coach al mismo tiempo.");

            } else {
                mostrarAlertaError("Error", "Error Base de Datos", "Código: " + resultado);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error Crítico", "Excepción", e.getMessage());
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

        List<Map<String, Object>> profes = Main.retornarJuecesDisponibles(this.idEventoActual);

        // Limpiamos los combos
        cbJuezUno.getItems().clear();
        cbJuezDos.getItems().clear();
        cbJuezTres.getItems().clear();
        mapaJueces.clear();

        if (profes.isEmpty()) {
            // Opcional: Avisar si ya no queda nadie disponible
            cbJuezUno.setPromptText("No hay docentes disponibles");
        } else {
            for (Map<String, Object> fila : profes) {
                String nombre = (String) fila.get("nombre");
                int id = (int) fila.get("id");

                cbJuezUno.getItems().add(nombre);
                cbJuezDos.getItems().add(nombre);
                cbJuezTres.getItems().add(nombre);

                mapaJueces.put(nombre, id);
            }
        }

        if (cats.isEmpty()) {
            cbCategoriaJuez.setPromptText("No hay categorías disponibles (Llenas o sin equipos)");
            cbCategoriaJuez.setDisable(true); // Desactivar el combo si no hay nada
        } else {
            cbCategoriaJuez.setPromptText("Seleccione una categoría");
            cbCategoriaJuez.setDisable(false);
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