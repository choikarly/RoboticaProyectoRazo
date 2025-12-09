import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlantillaEscuela {
    @FXML
    private Label lblNombreEscuela;

    public void setDatos(String nombre) {
        lblNombreEscuela.setText(nombre);
    }
}