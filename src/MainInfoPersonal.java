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
        // Al abrirse, intenta cargar los datos del usuario actual (comportamiento para Docentes)
        // Si es Admin, esto no mostrará nada relevante hasta que llamemos manualmente a cargarDatos(id)
        if (Main.usuaioActual != null && Main.usuaioActual > 0) {
            cargarDatos(Main.usuaioActual);
        }
    }

    public void cargarDatos(int idDocente) {
        Map<String, String> info = Main.obtenerInfoPersonalDocente(idDocente);

        if (!info.isEmpty()) {
            lblNombre.setText(info.get("nombre"));
            lblUsuario.setText(info.get("usuario"));
            lblFecha.setText(info.get("fecha"));

            String sexo = info.get("sexo");
            lblSexo.setText(sexo != null && sexo.equals("H") ? "Hombre" : "Mujer");

            lblEspecialidad.setText(info.get("especialidad"));
            lblEscuela.setText(info.get("escuela"));
            lblNivel.setText(info.get("nivel"));
        } else {
            lblNombre.setText("No se encontró información.");
            // Limpiamos los otros campos
            lblUsuario.setText("-"); lblFecha.setText("-"); lblSexo.setText("-");
            lblEspecialidad.setText("-"); lblEscuela.setText("-"); lblNivel.setText("-");
        }
    }
}