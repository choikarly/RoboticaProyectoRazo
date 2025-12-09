import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;

public class EvaluarProgController {
    @FXML private Label lblNombreEquipo;

    // Checkboxes
    @FXML private CheckBox chbsoft_prog, chbuso_func, chbcomplejidad, chbjust_prog;
    @FXML private CheckBox chbconocimiento_estr_func, chbdepuracion, chbcodigo_modular_efi;
    @FXML private CheckBox chbdocumentacion, chbvinculación_acciones, chbsensores;
    @FXML private CheckBox chbvinculo_jostick, chbcalibración, chbrespuesta_dispositivo;
    @FXML private CheckBox chbdocumentación_codigo, chbdemostración_15min, chbno_inconvenientes;
    @FXML private CheckBox chbdemostracion_objetivo, chbexplicacion_rutina;

    private int idEvento, idEquipo;
    private String nombreEquipo;

    public void iniciarEvaluacion(int idEvento, int idEquipo, String nombreEquipo) {
        this.idEvento = idEvento;
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        lblNombreEquipo.setText(nombreEquipo);

        cargarDatosPrevios();
    }

    private void cargarDatosPrevios() {
        Map<String, Boolean> datos = Main.obtenerEvaluacionProg(idEquipo, idEvento);

        if (!datos.isEmpty()) {
            // AQUI ESTABA EL ERROR: Faltaban líneas. Ahora están todas:
            chbsoft_prog.setSelected(datos.getOrDefault("soft_prog", false));
            chbuso_func.setSelected(datos.getOrDefault("uso_func", false));
            chbcomplejidad.setSelected(datos.getOrDefault("complejidad", false));
            chbjust_prog.setSelected(datos.getOrDefault("just_prog", false));
            chbconocimiento_estr_func.setSelected(datos.getOrDefault("conocimiento_estr_func", false));
            chbdepuracion.setSelected(datos.getOrDefault("depuracion", false));
            chbcodigo_modular_efi.setSelected(datos.getOrDefault("codigo_modular_efi", false));
            chbdocumentacion.setSelected(datos.getOrDefault("documentacion", false));

            // Ojo con los acentos, deben coincidir con Main.java
            chbvinculación_acciones.setSelected(datos.getOrDefault("vinculación_acciones", false));
            chbsensores.setSelected(datos.getOrDefault("sensores", false));
            chbvinculo_jostick.setSelected(datos.getOrDefault("vinculo_jostick", false));
            chbcalibración.setSelected(datos.getOrDefault("calibración", false));
            chbrespuesta_dispositivo.setSelected(datos.getOrDefault("respuesta_dispositivo", false));
            chbdocumentación_codigo.setSelected(datos.getOrDefault("documentación_codigo", false));
            chbdemostración_15min.setSelected(datos.getOrDefault("demostración_15min", false));

            chbno_inconvenientes.setSelected(datos.getOrDefault("no_inconvenientes", false));
            chbdemostracion_objetivo.setSelected(datos.getOrDefault("demostracion_objetivo", false));
            chbexplicacion_rutina.setSelected(datos.getOrDefault("explicacion_rutina", false));
        }
    }

    @FXML
    void btnSigCriterio(ActionEvent event) {
        try {
            // 1. Guardar
            Main.gestionarEvaluacionProg(idEquipo, idEvento,
                    chbsoft_prog.isSelected(), chbuso_func.isSelected(), chbcomplejidad.isSelected(),
                    chbjust_prog.isSelected(), chbconocimiento_estr_func.isSelected(), chbdepuracion.isSelected(),
                    chbcodigo_modular_efi.isSelected(), chbdocumentacion.isSelected(), chbvinculación_acciones.isSelected(),
                    chbsensores.isSelected(), chbvinculo_jostick.isSelected(), chbcalibración.isSelected(),
                    chbrespuesta_dispositivo.isSelected(), chbdocumentación_codigo.isSelected(), chbdemostración_15min.isSelected(),
                    chbno_inconvenientes.isSelected(), chbdemostracion_objetivo.isSelected(), chbexplicacion_rutina.isSelected()
            );

            // 2. Navegar
            java.net.URL url = getClass().getResource("EvaluarCriterioConstruccion.fxml");
            if (url == null) {
                System.err.println("ERROR: No se encuentra EvaluarCriterioConstruccion.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            EvaluarConstController controller = loader.getController();
            controller.iniciarEvaluacion(idEvento, idEquipo, nombreEquipo);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Evaluación: Construcción");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}