import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DetalleEventoAdminController {

    @FXML private Label lblNombreEvento;
    @FXML private TabPane tabPaneCategorias;

    public void cargarDatos(int idEvento, String nombreEvento) {
        lblNombreEvento.setText(nombreEvento);

        // 1. Obtener datos
        List<Map<String, String>> equipos = Main.retornarEquiposPorCategoria(idEvento);
        List<Map<String, String>> jueces = Main.retornarJuecesPorCategoria(idEvento);

        // 2. Agrupar por categoría
        Map<String, List<Map<String, String>>> equiposPorCat = equipos.stream()
                .collect(Collectors.groupingBy(m -> m.get("categoria")));

        Map<String, List<Map<String, String>>> juecesPorCat = jueces.stream()
                .collect(Collectors.groupingBy(m -> m.get("categoria")));

        // 3. Crear pestañas
        tabPaneCategorias.getTabs().clear();

        // Unimos todas las categorías encontradas (puede haber cat con equipos pero sin jueces, o viceversa)
        java.util.Set<String> todasCategorias = new java.util.HashSet<>();
        todasCategorias.addAll(equiposPorCat.keySet());
        todasCategorias.addAll(juecesPorCat.keySet());

        if (todasCategorias.isEmpty()) {
            Tab tab = new Tab("Sin Datos");
            tab.setContent(new Label("   No hay registros asociados a este evento."));
            tabPaneCategorias.getTabs().add(tab);
            return;
        }

        for (String cat : todasCategorias) {
            Tab tab = new Tab(cat);

            // Layout de la pestaña: HBox dividiendo Equipos (Izq) y Jueces (Der)
            HBox contenido = new HBox(20);
            contenido.setStyle("-fx-padding: 20;");

            // --- SECCIÓN EQUIPOS ---
            VBox boxEquipos = new VBox(10);
            boxEquipos.setPrefWidth(340);
            Label lblE = new Label("Equipos Inscritos");
            lblE.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #274c77;");
            ListView<String> listaEquipos = new ListView<>();

            if (equiposPorCat.containsKey(cat)) {
                for (Map<String, String> eq : equiposPorCat.get(cat)) {
                    listaEquipos.getItems().add(eq.get("equipo") + " (" + eq.get("escuela") + ")");
                }
            } else {
                listaEquipos.getItems().add("Sin equipos inscritos");
            }
            boxEquipos.getChildren().addAll(lblE, listaEquipos);

            // --- SECCIÓN JUECES ---
            VBox boxJueces = new VBox(10);
            boxJueces.setPrefWidth(340);
            Label lblJ = new Label("Jueces Asignados");
            lblJ.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #274c77;");
            ListView<String> listaJueces = new ListView<>();

            if (juecesPorCat.containsKey(cat)) {
                for (Map<String, String> j : juecesPorCat.get(cat)) {
                    listaJueces.getItems().add(j.get("juez"));
                }
            } else {
                listaJueces.getItems().add("Sin jueces asignados");
            }
            boxJueces.getChildren().addAll(lblJ, listaJueces);

            contenido.getChildren().addAll(boxEquipos, new Separator(javafx.geometry.Orientation.VERTICAL), boxJueces);
            tab.setContent(contenido);
            tabPaneCategorias.getTabs().add(tab);
        }
    }

    @FXML
    void btnCerrar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}