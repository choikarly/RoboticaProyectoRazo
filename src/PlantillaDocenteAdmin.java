import javafx.event.ActionEvent; // <--- ¡ESTA ES LA CLAVE! (NO java.awt)
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PlantillaDocenteAdmin {

    @FXML private Label lblNombre;
    @FXML private Label lblEscuela;
    @FXML private Label lblRol; // Un label extra para decir "COACH", "JUEZ" o "AMBOS"

    @FXML private Button btnAsignarComoJuez;

    public void setDatosDocentesAdmin(String nombre, String escuela, boolean esCoach, boolean esJuez) {
        lblNombre.setText(nombre);
        lblEscuela.setText(escuela);

        // Lógica visual para identificar el rol
        if (esCoach && esJuez) {
            lblRol.setText("ROL: HÍBRIDO (Coach y Juez)");
            lblRol.setStyle("-fx-text-fill: purple; -fx-font-weight: bold;");
        } else if (esCoach) {
            lblRol.setText("ROL: COACH");
            lblRol.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else if (esJuez) {
            lblRol.setText("ROL: JUEZ");
            lblRol.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        } else {
            lblRol.setText("SIN ROL EN EVENTOS");
            lblRol.setStyle("-fx-text-fill: grey;");
        }

        if (esJuez) {
            btnAsignarComoJuez.setVisible(false);
            btnAsignarComoJuez.setManaged(false); // No ocupa espacio
        } else {
            // Si NO es juez, mostramos el botón para permitir asignarlo
            btnAsignarComoJuez.setVisible(true);
            btnAsignarComoJuez.setManaged(true);
        }

    }
    @FXML
    void btnAsignarComoJuez(ActionEvent event) {
        System.out.println("boton desplegado");
    }

    @FXML
    void btnMasInfoDocentesMain(ActionEvent event) {
        System.out.println("info desplegado");
    }
}