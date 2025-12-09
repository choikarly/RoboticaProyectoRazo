import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfoEventosMasInfo {

    @FXML private Label lblNombreEvento;
    @FXML private Label lblFecha;
    @FXML private Label lblSede;

    // Método para recibir los datos desde la tarjeta
    public void setDatos(String nombre, String fecha, String sede) {
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        lblSede.setText(sede);
    }

    @FXML
    void btnCerrar(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stageActual = (Stage) source.getScene().getWindow();
        stageActual.close();
    }
}
