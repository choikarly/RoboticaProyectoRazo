import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlantillaEventoParticipado {
    @FXML
    private Label lblNombreEvento;
    @FXML
    private Label lblSede;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblRol;

    private int idEventoActual;
    @FXML private Button btnMasInfoEvento; // Para el Coach
    @FXML private Button btnEvaluarEvento; // Para el Juez

    public void setDatosEventoParticipado(int idEvento,String nombre,String sede, String fecha, String rol) {
        this.idEventoActual = idEvento;
        lblNombreEvento.setText(nombre);
        lblSede.setText(sede);
        lblFecha.setText(fecha);
        lblRol.setText(rol);

        // --- LÓGICA DE VISIBILIDAD DE BOTONES ---
        if("AMBOS".equalsIgnoreCase(rol)) {
            // MOSTRAR LOS DOS BOTONES
            btnMasInfoEvento.setVisible(true);
            btnMasInfoEvento.setManaged(true);

            btnEvaluarEvento.setVisible(true);
            btnEvaluarEvento.setManaged(true);

            if(lblRol != null) lblRol.setText("Rol: Coach y Juez");
        } else if ("COACH".equalsIgnoreCase(rol)) {
            btnMasInfoEvento.setVisible(true);
            btnMasInfoEvento.setManaged(true);

            btnEvaluarEvento.setVisible(false);
            btnEvaluarEvento.setManaged(false);
        } else if ("JUEZ".equalsIgnoreCase(rol)) {
            btnMasInfoEvento.setVisible(false);
            btnMasInfoEvento.setManaged(false);

            btnEvaluarEvento.setVisible(true);
            btnEvaluarEvento.setManaged(true);
        }
    }

    @FXML
    void btnMasInfoEvento(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosMasInfo.fxml"));
            Parent root = loader.load();

            Stage stagePaso = new Stage();
            stagePaso.setScene(new Scene(root));
            stagePaso.setTitle("Más Informacion");

            // Esto obliga al usuario a terminar aquí antes de volver a Eventos
            stagePaso.initModality(Modality.APPLICATION_MODAL);
            stagePaso.setResizable(false);
            stagePaso.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnEvaluarEvento(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventosEvaluar.fxml"));
            Parent root = loader.load();

            Stage stagePaso = new Stage();
            stagePaso.setScene(new Scene(root));
            stagePaso.setTitle("Más Informacion");

            // Esto obliga al usuario a terminar aquí antes de volver a Eventos
            stagePaso.initModality(Modality.APPLICATION_MODAL);
            stagePaso.setResizable(false);
            stagePaso.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
