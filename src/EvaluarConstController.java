import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.Map;

public class EvaluarConstController {
    @FXML private Label lblNombreEquipo;

    // Checkboxes
    @FXML private CheckBox chbprototipo_estetico, chbestructuras_estables, chbuso_sistemas_transmision;
    @FXML private CheckBox chbimplementación_marca_vex, chbuso_sensores, chbuso_procesador_cortexm3;
    @FXML private CheckBox chbcableado_adecuado, chbcalculo_implementacion_sistema_neumático;
    @FXML private CheckBox chbconocimiento_alcance, chbanalisis_Estruc, chbrelacion_velocidades;
    @FXML private CheckBox chbcentro_gravedad, chbsis_transmicion, chbtren_engranes;
    @FXML private CheckBox chbpotencia, chbtorque, chbvelocidad;

    private int idEvento, idEquipo;

    public void iniciarEvaluacion(int idEvento, int idEquipo, String nombreEquipo) {
        this.idEvento = idEvento;
        this.idEquipo = idEquipo;
        lblNombreEquipo.setText(nombreEquipo);
        cargarDatosPrevios();
    }

    private void cargarDatosPrevios() {
        Map<String, Boolean> datos = Main.obtenerEvaluacionConst(idEquipo, idEvento);

        if (!datos.isEmpty()) {
            // AQUI ESTABA EL ERROR: Ahora asignamos TODOS los campos
            chbprototipo_estetico.setSelected(datos.getOrDefault("prototipo_estetico", false));
            chbestructuras_estables.setSelected(datos.getOrDefault("estructuras_estables", false));
            chbuso_sistemas_transmision.setSelected(datos.getOrDefault("uso_sistemas_transmision", false));
            chbimplementación_marca_vex.setSelected(datos.getOrDefault("implementación_marca_vex", false));
            chbuso_sensores.setSelected(datos.getOrDefault("uso_sensores", false));
            chbuso_procesador_cortexm3.setSelected(datos.getOrDefault("uso_procesador_cortexm3", false));
            chbcableado_adecuado.setSelected(datos.getOrDefault("cableado_adecuado", false));
            chbcalculo_implementacion_sistema_neumático.setSelected(datos.getOrDefault("calculo_implementacion_sistema_neumático", false));
            chbconocimiento_alcance.setSelected(datos.getOrDefault("conocimiento_alcance", false));
            chbanalisis_Estruc.setSelected(datos.getOrDefault("analisis_Estruc", false));
            chbrelacion_velocidades.setSelected(datos.getOrDefault("relacion_velocidades", false));
            chbtren_engranes.setSelected(datos.getOrDefault("tren_engranes", false));
            chbcentro_gravedad.setSelected(datos.getOrDefault("centro_gravedad", false));
            chbsis_transmicion.setSelected(datos.getOrDefault("sis_transmicion", false));
            chbpotencia.setSelected(datos.getOrDefault("potencia", false));
            chbtorque.setSelected(datos.getOrDefault("torque", false));
            chbvelocidad.setSelected(datos.getOrDefault("velocidad", false));
        }
    }

    @FXML
    void btnFinalizarEvaluacion(ActionEvent event) {
        try {
            Main.gestionarEvaluacionConst(idEquipo, idEvento,
                    chbprototipo_estetico.isSelected(), chbestructuras_estables.isSelected(), chbuso_sistemas_transmision.isSelected(),
                    chbuso_sensores.isSelected(), chbcableado_adecuado.isSelected(), chbcalculo_implementacion_sistema_neumático.isSelected(),
                    chbconocimiento_alcance.isSelected(), chbimplementación_marca_vex.isSelected(), chbuso_procesador_cortexm3.isSelected(),
                    chbanalisis_Estruc.isSelected(), chbrelacion_velocidades.isSelected(), chbtren_engranes.isSelected(),
                    chbcentro_gravedad.isSelected(), chbsis_transmicion.isSelected(), chbpotencia.isSelected(), chbtorque.isSelected(),
                    chbvelocidad.isSelected()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Evaluación Completa");
            alert.setHeaderText(null);
            alert.setContentText("Evaluación guardada exitosamente.");
            alert.showAndWait();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}