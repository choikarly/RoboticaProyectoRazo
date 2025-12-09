import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoEventosEvaluar {

    // --- ESTAS VARIABLES FALTABAN O NO ESTABAN VINCULADAS ---
    @FXML
    private ComboBox<String> cbEquipos; // Debe coincidir con fx:id="cbEquipos"

    private Map<String, Integer> mapaEquipos = new HashMap<>();
    private int idEventoActual;

    // Método que llama la ventana anterior para cargar los datos
    public void inicializarDatos(int idEvento) {
        this.idEventoActual = idEvento;
        int idJuez = Main.usuaioActual;

        // Llamamos al Main para traer solo equipos que este juez puede evaluar
        // (Asegúrate de que este método exista en Main.java)
        List<Map<String, Object>> lista = Main.retornarEquiposAEvaluar(idEvento, idJuez);

        // Limpiamos y llenamos el combo
        if (cbEquipos != null) {
            cbEquipos.getItems().clear();
            mapaEquipos.clear();

            for (Map<String, Object> fila : lista) {
                String nombre = (String) fila.get("nombre_equipo");
                int id = (int) fila.get("id_equipo");
                cbEquipos.getItems().add(nombre);
                mapaEquipos.put(nombre, id);
            }
        } else {
            System.err.println("ERROR: cbEquipos es NULL. Revisa el fx:id en el FXML.");
        }
    }

    @FXML
    void btnComenzarEvaluacion(ActionEvent event) {
        try {
            String nombreEquipo = cbEquipos.getValue();
            if (nombreEquipo == null) return;

            int idEquipo = mapaEquipos.get(nombreEquipo);

            // Cargar la PRIMERA ventana: DISEÑO
            // Asegúrate que el nombre del recurso sea exacto (mayúsculas/minúsculas)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EvaluarCriterioDiseno.fxml"));
            Parent root = loader.load();

            // Pasar datos al controlador de Diseño
            EvaluarDisenoController controller = loader.getController();
            controller.iniciarEvaluacion(this.idEventoActual, idEquipo, nombreEquipo);

            // Cerrar la ventana actual
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActual.close();

            // Abrimos la evaluación
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Evaluación: Diseño");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}