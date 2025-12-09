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

        if (lblRol != null) {
            // Un pequeño ajuste visual para el texto del rol
            if ("AMBOS".equalsIgnoreCase(rol)) {
                lblRol.setText("Rol: Coach y Juez");
            } else {
                lblRol.setText("Rol: " + rol);
            }
        }

        // 2. Lógica de Botones (Coach vs Juez vs Ambos)
        if ("AMBOS".equalsIgnoreCase(rol)) {
            // CASO 1: Es Coach y Juez -> MOSTRAR TODO
            mostrarBoton(btnMasInfoEvento, true);
            mostrarBoton(btnEvaluarEvento, true);

        } else if ("COACH".equalsIgnoreCase(rol)) {
            // CASO 2: Solo Coach -> Ver Info, Ocultar Evaluar
            mostrarBoton(btnMasInfoEvento, true);
            mostrarBoton(btnEvaluarEvento, false);

        } else if ("JUEZ".equalsIgnoreCase(rol)) {
            // CASO 3: Solo Juez -> Ocultar Info, Ver Evaluar
            mostrarBoton(btnMasInfoEvento, false);
            mostrarBoton(btnEvaluarEvento, true);

        } else {
            // Seguridad: Ocultar todo si no hay rol claro
            mostrarBoton(btnMasInfoEvento, false);
            mostrarBoton(btnEvaluarEvento, false);
        }
    }

    private void mostrarBoton(Button btn, boolean mostrar) {
        if (btn != null) {
            btn.setVisible(mostrar);
            btn.setManaged(mostrar); // Si es false, el botón no ocupa espacio visual
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