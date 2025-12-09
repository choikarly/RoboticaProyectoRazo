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
import java.net.URL;
import java.util.Map;

public class EvaluarDisenoController {
    @FXML
    private Label lblNombreEquipo;
    // Asegúrate que los fx:id en el FXML coincidan con estos nombres:
    @FXML
    private CheckBox chbregistro_fechas, chbjustificacion_cambios_prototipos, chbortografia_redacción;
    @FXML
    private CheckBox chbpresentación, chbvideo_animación, chbdiseno_modelado_software;
    @FXML
    private CheckBox chbanalisis_elementos, chbensamble_prototipo, chbmodelo_acorde_robot;
    @FXML
    private CheckBox chbacorde_simulacion_calculos, chbrestricciones_movimiento;
    @FXML
    private CheckBox chbdiagramas_imagenes;

    private int idEvento, idEquipo;
    private String nombreEquipo;

    public void iniciarEvaluacion(int idEvento, int idEquipo, String nombreEquipo) {
        this.idEvento = idEvento;
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        lblNombreEquipo.setText(nombreEquipo);

        // Cargar datos previos si existen
        Map<String, Boolean> datos = Main.obtenerEvaluacionDiseno(idEquipo, idEvento);
        if (!datos.isEmpty()) {
            chbregistro_fechas.setSelected(datos.getOrDefault("registro_fechas", false));
            chbjustificacion_cambios_prototipos.setSelected(datos.getOrDefault("justificacion_cambios_prototipos", false));
            chbortografia_redacción.setSelected(datos.getOrDefault("ortografia_redacción", false));
            chbpresentación.setSelected(datos.getOrDefault("presentación", false));
            chbvideo_animación.setSelected(datos.getOrDefault("video_animación", false));
            chbdiseno_modelado_software.setSelected(datos.getOrDefault("diseno_modelado_software", false));
            chbanalisis_elementos.setSelected(datos.getOrDefault("analisis_elementos", false));
            chbensamble_prototipo.setSelected(datos.getOrDefault("ensamble_prototipo", false));
            chbmodelo_acorde_robot.setSelected(datos.getOrDefault("modelo_acorde_robot", false));
            chbacorde_simulacion_calculos.setSelected(datos.getOrDefault("acorde_simulacion_calculos", false));
            chbrestricciones_movimiento.setSelected(datos.getOrDefault("restricciones_movimiento", false));
            chbdiagramas_imagenes.setSelected(datos.getOrDefault("diagramas_imagenes", false));
        }
    }

    @FXML
    void btnSigCriterio(ActionEvent event) {
        // --- PARTE 1: GUARDAR EN BD ---
        try {
            System.out.println("Intentando guardar evaluación de diseño...");
            Main.gestionarEvaluacionDiseno(idEquipo, idEvento,
                    chbregistro_fechas.isSelected(), chbjustificacion_cambios_prototipos.isSelected(),
                    chbortografia_redacción.isSelected(), chbpresentación.isSelected(),
                    chbvideo_animación.isSelected(), chbdiseno_modelado_software.isSelected(),
                    chbanalisis_elementos.isSelected(), chbensamble_prototipo.isSelected(),
                    chbmodelo_acorde_robot.isSelected(), chbacorde_simulacion_calculos.isSelected(),
                    chbrestricciones_movimiento.isSelected(), chbdiagramas_imagenes.isSelected()
            );
            System.out.println("¡Guardado exitoso!");
        } catch (Exception e) {
            System.err.println("ERROR AL GUARDAR EN BD: " + e.getMessage());
            e.printStackTrace();
            // No detenemos el flujo, permitimos intentar avanzar
        }

        // --- PARTE 2: CAMBIAR DE VENTANA ---
        try {
            System.out.println("Cargando siguiente ventana...");

            // Verificamos si el archivo existe antes de cargarlo
            java.net.URL url = getClass().getResource("EvaluarCriterioProgramacion.fxml");
            if (url == null) {
                System.err.println("ERROR FATAL: No se encuentra el archivo 'EvaluarCriterioProgramacion.fxml'");
                System.err.println("Asegúrate de que está en la carpeta: " + getClass().getResource("").getPath());
                return; // Detenemos aquí si no existe el archivo
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Configurar el siguiente controlador
            EvaluarProgController controller = loader.getController();
            if (controller == null) {
                System.err.println("ADVERTENCIA: El controlador de la siguiente ventana es NULL. Revisa el fx:controller en el FXML.");
            } else {
                controller.iniciarEvaluacion(idEvento, idEquipo, nombreEquipo);
            }

            // Cambiar la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Evaluación: Programación");
            stage.show();
            System.out.println("Cambio de ventana exitoso.");

        } catch (IOException e) {
            System.err.println("ERROR DE E/S AL CARGAR FXML:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR DESCONOCIDO AL CAMBIAR VENTANA:");
            e.printStackTrace();
        }
    }
}