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

    public void setDatos(String nombre, String sede, String fecha) {
        // 1. Guardamos los datos en memoria (IMPORTANTE para el botón)
        this.nombreGuardado = nombre;
        this.sedeGuardada = sede;
        this.fechaGuardada = fecha;

        // 2. Actualizamos la parte visual (Labels)
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);

        // Validación por si el label de sede no existe en algun FXML reutilizado
        if (lblSede != null) {
            lblSede.setText(sede);
        }
    }



    // Este método lo llama MainEventosController al crear la lista
    public void setDatosEvento(String nombre, String sede, String fecha) {
        // 1. Guardamos los datos en memoria
        this.nombreGuardado = nombre;
        this.sedeGuardada = sede;
        this.fechaGuardada = fecha;

        // 2. Los mostramos en la tarjeta pequeña
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        if(lblSede != null) lblSede.setText(sede);
    }


    @FXML
    void btnRegistrarEquiposInfoEvento(ActionEvent event) { //este boton te lleva a INFOEVENTOS
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InfoEventos.fxml"));
            Parent root = loader.load();

            // OBTENEMOS EL CONTROLADOR DE LA VENTANA QUE SE VA A ABRIR
            InfoEventos controlador = loader.getController();
            // ¡AQUÍ PASA LA MAGIA! Le pasamos los datos que tenemos guardados
            controlador.recibirDatosEvento(this.nombreGuardado, this.fechaGuardada, this.sedeGuardada);

            // 2. Crear el escenario (Stage) nuevo
            Stage stagePaso1 = new Stage();
            stagePaso1.setScene(new Scene(root));
            stagePaso1.setTitle("Informacion Evento");

            // Esto obliga al usuario a terminar aquí antes de volver a Eventos
            stagePaso1.initModality(Modality.APPLICATION_MODAL);
            stagePaso1.setResizable(false);
            stagePaso1.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
