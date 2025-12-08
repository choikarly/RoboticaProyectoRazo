import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class PlantillaMisEquipos {
    @FXML
    private Label lblNombreEquipo;
    @FXML
    private Label lblEvento;
    @FXML
    private Label lblEscuela;
    @FXML
    private Label lblCategoria;

    public void setDatosMisEquipos(String equipo,String evento, String escuela,String categoria) {
        lblNombreEquipo.setText(equipo);
        lblEvento.setText(evento);
        lblEscuela.setText(escuela);
        lblCategoria.setText(categoria);
    }

    @FXML
    void btnMostrarResultadosEquipo(ActionEvent event) {

    }


}
