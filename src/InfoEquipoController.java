import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class InfoEquipoController {

    @FXML private Label lblNombreEquipo;
    @FXML private Label lblCalificacion;
    @FXML private ListView<String> listaIntegrantes;

    public void cargarDatos(int idEquipo, int idEvento, String nombreEquipo) {
        lblNombreEquipo.setText(nombreEquipo);

        // 1. Cargar Integrantes
        List<Map<String, String>> miembros = Main.retornarMiembrosEquipo(idEquipo, idEvento);
        listaIntegrantes.getItems().clear();
        for (Map<String, String> m : miembros) {
            String info = m.get("nombre") + " - No. Control: " + m.get("num_control");
            listaIntegrantes.getItems().add(info);
        }

        // 2. Cargar Calificación
        int puntos = Main.obtenerPuntajeEquipo(idEquipo, idEvento);

        if (puntos == -1 || puntos == -2) { // -1 es valor por defecto en BD, -2 es null
            lblCalificacion.setText("Aun sin calificación");
            lblCalificacion.setStyle("-fx-text-fill: #e63946;"); // Rojo
        } else {
            lblCalificacion.setText(puntos + " Puntos");
            lblCalificacion.setStyle("-fx-text-fill: #2a9d8f;"); // Verde azulado
        }
    }

    @FXML
    void btnCerrar(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}