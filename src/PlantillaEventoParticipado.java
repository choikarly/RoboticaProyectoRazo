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

    public void setDatosEventoParticipado(int idEvento, String nombre, String sede, String fecha, String rol) {
        this.idEventoGuardado = idEvento;

        lblNombreEvento.setText(nombre);
        lblSede.setText(sede);
        lblFecha.setText(fecha);
        lblRol.setText(rol);

        if ("AMBOS".equalsIgnoreCase(rol)) {
            mostrarBoton(btnMasInfoEvento, true);
            mostrarBoton(btnEvaluarEvento, true);
            lblRol.setText("COACH Y JUEZ");

        } else if ("COACH".equalsIgnoreCase(rol)) {
            mostrarBoton(btnMasInfoEvento, true);
            mostrarBoton(btnEvaluarEvento, false);
            lblRol.setText("COACH");

        } else if ("JUEZ".equalsIgnoreCase(rol)) {
            mostrarBoton(btnMasInfoEvento, false);
            mostrarBoton(btnEvaluarEvento, true);
            lblRol.setText("JUEZ");

        } else {
            mostrarBoton(btnMasInfoEvento, false);
            mostrarBoton(btnEvaluarEvento, false);
            lblRol .setText("?");

        }
    }

    private void mostrarBoton(Button btn, boolean mostrar) {
        if (btn != null) {
            btn.setVisible(mostrar);
            btn.setManaged(mostrar);
        }
    }

    @FXML
    void btnEvaluarEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosEvaluar.fxml"));
            Parent root = loader.load();

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

            InfoEventosMasInfo controller = loader.getController();

            controller.cargarDatosRanking(
                    this.idEventoGuardado,
                    lblNombreEvento.getText(),
                    lblFecha.getText(),
                    lblSede.getText()
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ranking del Evento");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}