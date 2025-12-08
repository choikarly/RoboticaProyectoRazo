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

public class PlantillaEvento {
    @FXML private Label lblNombreEvento;
    @FXML private Label lblSede;
    @FXML private Label lblFecha;

    private String nombreGuardado;
    private String fechaGuardada;
    private String sedeGuardada;
    private int idEventoGuardado; // <--- NUEVO CAMPO

    // Modificamos este método para recibir el ID
    public void setDatos(int idEvento, String nombre, String sede, String fecha) {
        this.idEventoGuardado = idEvento; // Guardamos el ID
        this.nombreGuardado = nombre;
        this.sedeGuardada = sede;
        this.fechaGuardada = fecha;

        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        if (lblSede != null) lblSede.setText(sede);
    }

    @FXML
    void btnRegistrarEquiposInfoEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventos.fxml"));
            Parent root = loader.load();

            InfoEventos controlador = loader.getController();
            // Pasamos también el ID del evento
            controlador.recibirDatosEvento(this.idEventoGuardado, this.nombreGuardado, this.fechaGuardada, this.sedeGuardada);

            Stage stagePaso1 = new Stage();
            stagePaso1.setScene(new Scene(root));
            stagePaso1.setTitle("Informacion Evento");
            stagePaso1.initModality(Modality.APPLICATION_MODAL);
            stagePaso1.setResizable(false);
            stagePaso1.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}