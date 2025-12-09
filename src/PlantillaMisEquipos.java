import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class PlantillaMisEquipos {
    @FXML private Label lblNombreEquipo;
    @FXML private Label lblEvento;
    @FXML private Label lblEscuela;
    @FXML private Label lblCategoria;

    private int idEquipoGuardado;
    private int idEventoGuardado;
    private String nombreEquipoGuardado;

    // ACTUALIZADO PARA RECIBIR IDs
    public void setDatosMisEquipos(int idEquipo, int idEvento, String equipo, String evento, String escuela, String categoria) {
        this.idEquipoGuardado = idEquipo;
        this.idEventoGuardado = idEvento;
        this.nombreEquipoGuardado = equipo;

        lblNombreEquipo.setText(equipo);
        lblEvento.setText(evento);
        lblEscuela.setText(escuela);
        lblCategoria.setText(categoria);
    }

    @FXML
    void btnMostrarResultadosEquipo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEquipo.fxml"));
            Parent root = loader.load();

            InfoEquipoController controller = loader.getController();
            // Pasamos los datos para que busque en la BD
            controller.cargarDatos(idEquipoGuardado, idEventoGuardado, nombreEquipoGuardado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Información del Equipo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}