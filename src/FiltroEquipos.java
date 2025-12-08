import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FiltroEquipos implements Initializable {

    @FXML private ComboBox<String> cbEvento;
    @FXML private ComboBox<String> cbCategoria;

    // Mapas para guardar los IDs ocultos (Nombre -> ID)
    private Map<String, Integer> mapaEventos = new HashMap<>();
    private Map<String, Integer> mapaCategorias = new HashMap<>();

    // Variable para guardar quién es la ventana principal
    private MainEquiposAdmin controladorPadre;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCombos();
    }

    // Método VITAL: Permite que el Main se "presente" ante esta ventana
    public void setPadre(MainEquiposAdmin padre) {
        this.controladorPadre = padre;
    }

    private void cargarCombos() {
        // --- 1. EVENTOS ---
        List<Map<String, Object>> eventos = Main.retornarEventos();

        cbEvento.getItems().add("Todos");
        mapaEventos.put("Todos", -1);

        for (Map<String, Object> fila : eventos) {
            String nombre = (String) fila.get("nombre");

            // ERROR AQUI? Si la BD no trae "id_evento", esto es NULL
            if (fila.get("id_evento") != null) {
                int id = (int) fila.get("id_evento");
                cbEvento.getItems().add(nombre);
                mapaEventos.put(nombre, id);
            } else {
                System.err.println("¡ALERTA! El evento " + nombre + " no trajo ID. Revisa el Stored Procedure.");
            }
        }

        // --- 2. CATEGORÍAS ---
        List<Map<String, Object>> categorias = Main.retornarCategorias();

        cbCategoria.getItems().add("Todas");
        mapaCategorias.put("Todas", -1);

        for (Map<String, Object> fila : categorias) {
            String nombre = (String) fila.get("nombre");

            // Verificamos que no sea null antes de convertir
            if (fila.get("id_categoria") != null) {
                int id = (int) fila.get("id_categoria");
                cbCategoria.getItems().add(nombre);
                mapaCategorias.put(nombre, id);
            }
        }
    }

    @FXML
    void btnFiltrarBusqueda(ActionEvent event) {
        // 1. Obtener selección
        String eventoTxt = cbEvento.getValue();
        String catTxt = cbCategoria.getValue();

        // 2. Obtener IDs (Si es null o no existe, usamos -1)
        int idEvento = (eventoTxt != null && mapaEventos.containsKey(eventoTxt)) ? mapaEventos.get(eventoTxt) : -1;
        int idCategoria = (catTxt != null && mapaCategorias.containsKey(catTxt)) ? mapaCategorias.get(catTxt) : -1;

        // 3. COMUNICARSE CON EL PADRE
        if (controladorPadre != null) {
            System.out.println("Filtrando por Evento ID: " + idEvento + ", Categoria ID: " + idCategoria);
            controladorPadre.cargarEquiposFiltrados(idEvento, idCategoria);
        }

        // 4. Cerrar ventana
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}