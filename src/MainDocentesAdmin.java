import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainDocentesAdmin implements Initializable {

    @FXML private VBox vboxContenedorDocentesAdmin;     // Contenedor de tarjetas
    @FXML private CheckBox chbMostrarCoach;       // Checkbox "Ver Coaches"
    @FXML private CheckBox chbMostrarJueces;        // Checkbox "Ver Jueces"

    // Guardamos la lista completa aquí para filtrar rápido
    private List<Map<String, Object>> listaMaestra;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Cargar datos de la BD una sola vez
        listaMaestra = Main.retornarDocentesConRoles();

        // 2. Mostrar la lista inicial (Caso 1: Ninguno seleccionado)
        aplicarFiltros();

        // 3. Agregar "Listeners": Cada vez que clicamos un check, se ejecuta 'aplicarFiltros'
        chbMostrarCoach.setOnAction(e -> aplicarFiltros());
        chbMostrarJueces.setOnAction(e -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        vboxContenedorDocentesAdmin.getChildren().clear(); // Limpiamos la vista

        boolean filtrarCoach = chbMostrarCoach.isSelected();
        boolean filtrarJuez = chbMostrarJueces.isSelected();

        if (listaMaestra.isEmpty()) {
            vboxContenedorDocentesAdmin.setVisible(false);
            vboxContenedorDocentesAdmin.setManaged(false);

        } else {
            vboxContenedorDocentesAdmin.setVisible(true);
            vboxContenedorDocentesAdmin.setManaged(true);
            vboxContenedorDocentesAdmin.setSpacing(10);
        }

        for (Map<String, Object> docente : listaMaestra) {

            // Extraer datos
            boolean esCoach = (boolean) docente.get("es_coach");
            boolean esJuez = (boolean) docente.get("es_juez");
            String nombre = (String) docente.get("nombre");
            String escuela = (String) docente.get("escuela");

            boolean mostrar = false;

            // --- LÓGICA DE LOS 4 CASOS ---

            if (!filtrarCoach && !filtrarJuez) {
                // CASO 1: Ninguno seleccionado -> Mostrar solo los que NO son nada
                if (!esCoach && !esJuez) {
                    mostrar = true;
                }
            }
            else if (filtrarCoach && !filtrarJuez) {
                // CASO 2: Solo Coach -> Mostrar Coaches (sean jueces o no)
                if (esCoach) {
                    mostrar = true;
                }
            }
            else if (!filtrarCoach && filtrarJuez) {
                // CASO 3: Solo Juez -> Mostrar Jueces (sean coaches o no)
                if (esJuez) {
                    mostrar = true;
                }
            }
            else {
                // CASO 4: Ambos seleccionados -> Mostrar SOLO los que son AMBOS (Intersección)
                if (esCoach && esJuez) {
                    mostrar = true;
                }
            }

            // --- SI PASA EL FILTRO, CREAMOS LA TARJETA ---
            if (mostrar) {
                crearTarjetaDocente(nombre, escuela, esCoach, esJuez);
            }
        }
    }

    private void crearTarjetaDocente(String nombre, String escuela, boolean esCoach, boolean esJuez) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaDocenteAdmin.fxml"));
            AnchorPane tarjeta = loader.load();

            PlantillaDocenteAdmin controller = loader.getController();
            controller.setDatosDocentesAdmin(nombre, escuela, esCoach, esJuez);

            vboxContenedorDocentesAdmin.getChildren().add(tarjeta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}