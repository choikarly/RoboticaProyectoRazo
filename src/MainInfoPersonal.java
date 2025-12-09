import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.Map;

public class MainInfoPersonal {

    @FXML private Label lblNombre;
    @FXML private Label lblUsuario;
    @FXML private Label lblFecha;
    @FXML private Label lblSexo;
    @FXML private Label lblEspecialidad;
    @FXML private Label lblEscuela;
    @FXML private Label lblNivel;

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        int idDocente = Main.usuaioActual;
        Map<String, String> info = Main.obtenerInfoPersonalDocente(idDocente);

        if (!info.isEmpty()) {
            lblNombre.setText(info.get("nombre"));
            lblUsuario.setText(info.get("usuario"));
            lblFecha.setText(info.get("fecha"));

            String sexo = info.get("sexo");
            lblSexo.setText(sexo.equals("H") ? "Hombre" : "Mujer");

            lblEspecialidad.setText(info.get("especialidad"));
            lblEscuela.setText(info.get("escuela"));
            lblNivel.setText(info.get("nivel"));
        } else {
            lblNombre.setText("Error al cargar datos.");
        }
    }
}