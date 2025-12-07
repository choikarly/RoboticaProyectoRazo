import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class InfoEventos implements Initializable {
    @FXML
    private ComboBox<String> cbCategoria;
    private Map<String, Integer> mapaCategorias = new HashMap<>();

    @FXML
    public void initialize (URL url, ResourceBundle resourceBundle){
        cargarCategoriasDesdeBD();
    }

    @FXML
    void btnContinuar(ActionEvent event) {
        try {
            String nombreCategoria = cbCategoria.getValue();

            if (nombreCategoria == null) {
                mostrarAlertaError(
                        "Error",
                        "Falta Categoria",
                        "Selecciona una categoría.");
                return;
            } else {
                // 1. OBTENER EL ID SELECCIONADO
                int idCategoriaSeleccionada = mapaCategorias.get(nombreCategoria);
                // 1. CERRAR la ventana actual
                Node source = (Node) event.getSource();
                Stage stageActual = (Stage) source.getScene().getWindow();
                stageActual.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroDeEquipo.fxml"));
                Parent root = loader.load();

                // Obtenemos acceso al código Java de la siguiente ventana
                RegistroDeEquipo ventanaSig = loader.getController();
                // Le pasamos el ID y hacemos que cargue las escuelas
                ventanaSig.recibirCategoriaYCargarEscuelas(idCategoriaSeleccionada);

                Stage stagePaso2 = new Stage();
                stagePaso2.setScene(new Scene(root));
                stagePaso2.setTitle("Integrantes");
                stagePaso2.initModality(Modality.APPLICATION_MODAL);
                stagePaso2.setResizable(false);
                stagePaso2.show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarCategoriasDesdeBD() {
        List<Map<String, Object>> lista = Main.retornarCategorias();

        cbCategoria.getItems().clear();
        mapaCategorias.clear();

        for (Map<String, Object> fila : lista) {
            // Extraemos los datos haciendo casting
            String nombre = (String) fila.get("nombre");
            int id = (int) fila.get("id_categoria");

            cbCategoria.getItems().add(nombre);

            // Llenamos el Mapa (Lógico para saber el ID después)
            mapaCategorias.put(nombre, id);
        }
    }

    public void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    public void mostrarAlertaExito(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
