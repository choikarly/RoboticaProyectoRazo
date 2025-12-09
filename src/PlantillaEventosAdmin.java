import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class PlantillaEventosAdmin {
    @FXML
    private Label lblNombreEvento;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblSede;


    private int idEventoGuardado;
    private String nombreEventoGuardado;
    private String sedeGuardada;

    public void setDatosEventoAdmin(int idEvento, String nombre, String fecha, String sede) {
        this.idEventoGuardado = idEvento;
        this.nombreEventoGuardado = nombre;
        this.sedeGuardada = sede;

        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        lblSede.setText(sede);
    }

    @FXML
    void btnIrAsignarJuecesEvento(ActionEvent event) {
        try {
            // 1. Cargar el FXML de la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AsignarJueces.fxml"));
            Parent root = loader.load();

            // 2. Obtener el controlador y PASARLE EL ID
            AsignarJueces controller = loader.getController();
            controller.recibirDatosEventoJuez(
                    this.idEventoGuardado,
                    this.nombreEventoGuardado,
                    this.sedeGuardada);

            // 3. Abrir la ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Asignación de Jueces - " + this.nombreEventoGuardado);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al abrir AsignarJueces.fxml. Revisa que el archivo exista y el nombre esté bien escrito.");
        }
    }

}
