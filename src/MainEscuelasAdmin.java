import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainEscuelasAdmin implements Initializable {

    @FXML
    private VBox vboxContenedorEscuelas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarEscuelas();
    }

    private void cargarEscuelas() {
        // 1. Limpiar la lista visual
        vboxContenedorEscuelas.getChildren().clear();

        // 2. Obtener datos de la Base de Datos
        List<Map<String, Object>> listaEscuelas = Main.retornarEscuelas();

        if (listaEscuelas.isEmpty()) {
            // Opcional: Mostrar un Label de "No hay escuelas" si la lista está vacía
            System.out.println("No hay escuelas registradas.");
        } else {
            try {
                for (Map<String, Object> fila : listaEscuelas) {
                    // Extraer datos
                    String nombre = (String) fila.get("nombre");
                    // Puedes extraer más datos si tu plantilla lo requiere (nivel, ciudad, etc.)
                    // int nivel = (int) fila.get("fk_nivel"); 

                    // 3. Cargar la Plantilla (Tarjeta)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaEscuela.fxml"));
                    AnchorPane tarjeta = loader.load();

                    // 4. Pasar datos al controlador de la tarjeta
                    PlantillaEscuela controller = loader.getController();
                    controller.setDatos(nombre);

                    // 5. Agregar a la lista
                    vboxContenedorEscuelas.getChildren().add(tarjeta);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al cargar PlantillaEscuela.fxml. Verifica que el archivo exista.");
            }
        }
    }
}