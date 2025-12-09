import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Importante para la alerta
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PlantillaEventoParticipado {
    @FXML private Label lblNombreEvento;
    @FXML private Label lblSede;
    @FXML private Label lblFecha;
    @FXML private Label lblRol;
    @FXML private Button btnEvaluarEvento;
    @FXML private Button btnMasInfoEvento;

    private int idEventoGuardado;
    private boolean evaluacionHabilitada = false; // Bandera de control

    public void setDatosEventoParticipado(int idEvento, String nombre, String sede, String fecha, String rol) {
        this.idEventoGuardado = idEvento;

        lblNombreEvento.setText(nombre);
        lblSede.setText(sede);
        lblFecha.setText(fecha);
        lblRol.setText(rol);

        if ("AMBOS".equalsIgnoreCase(rol)) {
            mostrarBoton(btnMasInfoEvento, true);
            configurarBotonEvaluar(fecha); // Lógica de fecha aquí
            lblRol.setText("COACH Y JUEZ");

        } else if ("COACH".equalsIgnoreCase(rol)) {
            mostrarBoton(btnMasInfoEvento, true);
            mostrarBoton(btnEvaluarEvento, false);
            lblRol.setText("COACH");

        } else if ("JUEZ".equalsIgnoreCase(rol)) {
            mostrarBoton(btnMasInfoEvento, false);
            configurarBotonEvaluar(fecha); // Lógica de fecha aquí
            lblRol.setText("JUEZ");

        } else {
            mostrarBoton(btnMasInfoEvento, false);
            mostrarBoton(btnEvaluarEvento, false);
            lblRol.setText("?");
        }
    }

    private void mostrarBoton(Button btn, boolean mostrar) {
        if (btn != null) {
            btn.setVisible(mostrar);
            btn.setManaged(mostrar);
        }
    }

    // Nuevo método auxiliar para aplicar las reglas de fecha
    private void configurarBotonEvaluar(String fechaTexto) {
        mostrarBoton(btnEvaluarEvento, true); // Primero aseguramos que se vea

        try {
            LocalDate fechaEvento = LocalDate.parse(fechaTexto);
            LocalDate hoy = LocalDate.now();

            if (fechaEvento.isEqual(hoy)) {
                // ES HOY: Habilitado
                this.evaluacionHabilitada = true;
                btnEvaluarEvento.setStyle("-fx-background-color: #274c77; -fx-background-radius: 15; -fx-text-fill: WHITE;");
                btnEvaluarEvento.setText("Evaluar");
            } else {
                // NO ES HOY: Deshabilitado visualmente
                this.evaluacionHabilitada = false;
                btnEvaluarEvento.setStyle("-fx-background-color: #B0B0B0; -fx-background-radius: 15; -fx-text-fill: #555555;");

                if (fechaEvento.isAfter(hoy)) {
                    btnEvaluarEvento.setText("Próximamente");
                } else {
                    btnEvaluarEvento.setText("Concluido");
                }
            }
        } catch (DateTimeParseException e) {
            this.evaluacionHabilitada = false;
            btnEvaluarEvento.setStyle("-fx-background-color: #B0B0B0; -fx-background-radius: 15;");
            btnEvaluarEvento.setText("Fecha Inválida");
        }
    }

    @FXML
    void btnEvaluarEvento(ActionEvent event) {
        // Bloqueo de seguridad si no es el día
        if (!evaluacionHabilitada) {
            mostrarAlertaBloqueo();
            return;
        }

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

    private void mostrarAlertaBloqueo() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Evaluación no disponible");
        alert.setHeaderText("Fuera de fecha");
        alert.setContentText("La evaluación solo está permitida el día del evento (" + lblFecha.getText() + ").");
        alert.showAndWait();
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