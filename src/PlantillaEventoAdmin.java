import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.event.ActionEvent;

public class PlantillaEventoAdmin {

    @FXML private Label lblNombreEvento;
    @FXML private Label lblFecha;
    @FXML private Label lblSede;

    public void setDatosEventoAdmin(String nombre, String fecha, String sede) {
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        lblSede.setText(sede);
    }

    @FXML
    void btnAsignarJueces(ActionEvent event) {
        System.out.println("Si");
    }
}