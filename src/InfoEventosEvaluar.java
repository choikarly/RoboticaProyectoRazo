import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class InfoEventosEvaluar implements Initializable {

    @FXML private ComboBox<String> cbEquiposFiltrados;

    private int idEventoActual;

    // Mapa para saber el ID del equipo seleccionado
    private Map<String, Integer> mapaEquipos = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    // ESTE METODO SE LLAMA DESDE LA PLANTILLA ANTERIOR
    public void recibirDatosEquiposFiltrados(int idEvento) {
        this.idEventoActual = idEvento;
        cargarEquipos();
    }

    @FXML
    void btnComenzarEvaluacion(ActionEvent event) {
        try {
            // 1. VALIDACIÓN: Verificar que haya seleccionado un equipo
            // (Asumo que tienes el método obtenerIdEquipoSeleccionado() que hicimos antes)
            int idEquipo = obtenerIdEquipoSeleccionado();

            if (idEquipo == -1) {
                // Si no seleccionó nada, mostrar alerta y detenerse
                mostrarAlertaError("ERROR",
                        "CAMPOS SIN RELLENAR",
                        "Por favor selecciona un equipo para evaluar.");
                return;
            } else {
                // 2. CARGAR LA NUEVA VISTA
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EvaluacionCriterioProgramacion.fxml"));
                Parent root = loader.load();

                // 3. PASAR DATOS (Opcional pero recomendado)
                // Aquí deberías pasar el ID del equipo al nuevo controlador para saber a quién calificar
                /*EvaluacionCriterioProgController controller = loader.getController();
                controller.setDatosEvaluacion(this.idEventoActual, idEquipo);
            */

                // 4. MOSTRAR LA NUEVA VENTANA
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Evaluación de Programación");
                stage.show();

                // 5. CERRAR LA VENTANA ACTUAL (ANTERIOR)
                // Obtenemos la referencia al Stage actual a través del botón presionado
                Node source = (Node) event.getSource();
                Stage stageActual = (Stage) source.getScene().getWindow();
                stageActual.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al abrir EvaluacionCriterioProg.fxml. Revisa el nombre del archivo.");
        }
    }

    private void cargarEquipos() {
        cbEquiposFiltrados.getItems().clear();
        mapaEquipos.clear();

        // Obtenemos el ID del juez (usuario logueado)
        int idJuez = Main.usuaioActual;

        List<Map<String, Object>> lista = Main.retornarEquiposParaEvaluar(this.idEventoActual, idJuez);

        if (lista.isEmpty()) {
            cbEquiposFiltrados.setPromptText("No hay equipos asignados a tu categoría.");
        } else {
            for (Map<String, Object> fila : lista) {
                String nombreEquipo = (String) fila.get("equipo");
                String categoria = (String) fila.get("categoria");
                int idEquipo = (int) fila.get("id");

                // Formato bonito: "Megatron (Sumo)"
                String textoCombo = nombreEquipo + " (" + categoria + ")";

                cbEquiposFiltrados.getItems().add(textoCombo);
                mapaEquipos.put(textoCombo, idEquipo);
            }
        }
    }

    public int obtenerIdEquipoSeleccionado() {
        String seleccion = cbEquiposFiltrados.getValue();
        if (seleccion != null && mapaEquipos.containsKey(seleccion)) {
            return mapaEquipos.get(seleccion);
        }
        return -1;
    }

    public void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }
}