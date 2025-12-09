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
import javafx.stage.StageStyle;

public class InfoEventosEvaluar {

    @FXML
    private ComboBox<String> cbEquipos;

    private Map<String, Integer> mapaEquipos = new HashMap<>();
    private int idEventoActual;

    public void inicializarDatos(int idEvento) {
        this.idEventoActual = idEvento;
        int idJuez = Main.usuaioActual;


        List<Map<String, Object>> lista = Main.retornarEquiposAEvaluar(idEvento, idJuez);

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("EvaluarCriterioDiseno.fxml"));
            Parent root = loader.load();

            EvaluarDisenoController controller = loader.getController();
            controller.iniciarEvaluacion(this.idEventoActual, idEquipo, nombreEquipo);

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActual.close();

            Stage stage = new Stage();

            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOnCloseRequest(e -> e.consume());

            stage.setScene(new Scene(root));
            stage.setTitle("Evaluación: Diseño");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}