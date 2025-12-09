import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlantillaEventoParticipado {
    @FXML private Label lblNombreEvento;
    @FXML private Label lblSede;
    @FXML private Label lblFecha;
    @FXML private Label lblRol;
    @FXML private Button btnEvaluarEvento;
    @FXML private Button btnMasInfoEvento;

    private int idEventoGuardado;

    // ACTUALIZADO: Recibe 5 parámetros para coincidir con tu MainEventosController
    public void setDatosEventoParticipado(int idEvento, String nombre, String sede, String fecha, String rol) {
        this.idEventoGuardado = idEvento;

        lblNombreEvento.setText(nombre);
        lblSede.setText(sede);
        lblFecha.setText(fecha);
        lblRol.setText(rol);

        // Lógica de visibilidad del botón Evaluar
        // Solo mostramos "Evaluar" si es JUEZ o AMBOS
        if (rol.equalsIgnoreCase("JUEZ") || rol.equalsIgnoreCase("AMBOS")) {
            btnEvaluarEvento.setVisible(true);
            btnEvaluarEvento.setManaged(true);
        } else {
            btnEvaluarEvento.setVisible(false);
            btnEvaluarEvento.setManaged(false);
        }
    }

    @FXML
    void btnEvaluarEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosEvaluar.fxml"));
            Parent root = loader.load();

            // Pasamos el ID del evento a la ventana de selección de equipos
            InfoEventosEvaluar controller = loader.getController();
            controller.inicializarDatos(this.idEventoGuardado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Selección de Equipo a Evaluar");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnMasInfoEvento(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosMasInfo.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Más Información");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}