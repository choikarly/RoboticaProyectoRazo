import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoEventosMasInfo {

    @FXML private Label lblNombreEvento;
    @FXML private Label lblFecha;
    @FXML private Label lblSede;

    // Ahora usamos un TabPane en lugar de una tabla fija
    @FXML private TabPane tabPaneRankings;

    public void cargarDatosRanking(int idEvento, String nombre, String fecha, String sede) {
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        lblSede.setText(sede);

        // 1. Obtener todos los datos crudos de la BD
        List<Map<String, Object>> datosBD = Main.retornarRankingEvento(idEvento);

        // 2. Limpiar pestañas anteriores
        tabPaneRankings.getTabs().clear();

        // 3. Agrupar por categoría usando Streams de Java
        // Esto crea un mapa donde la Clave es la Categoría (ej. "Primaria") y el Valor es la lista de equipos de esa categoría
        Map<String, List<Map<String, Object>>> equiposPorCategoria = datosBD.stream()
                .collect(Collectors.groupingBy(m -> (String) m.get("categoria")));

        // 4. Si no hay datos, mostrar mensaje
        if (equiposPorCategoria.isEmpty()) {
            Tab tabVacio = new Tab("Sin Resultados");
            Label lblVacio = new Label("No hay equipos registrados o evaluados aún.");
            lblVacio.setStyle("-fx-padding: 20; -fx-font-size: 16;");
            tabVacio.setContent(lblVacio);
            tabPaneRankings.getTabs().add(tabVacio);
            return;
        }

        // 5. Crear una pestaña por cada categoría encontrada
        for (String categoria : equiposPorCategoria.keySet()) {
            Tab tab = new Tab(categoria); // El nombre de la pestaña es la categoría

            // Crear la tabla para esta categoría
            TableView<FilaRanking> tabla = crearTablaRanking();

            // Obtener la lista de equipos de esta categoría
            List<Map<String, Object>> equiposDeLaCategoria = equiposPorCategoria.get(categoria);

            // Convertir a FilaRanking y enumerar (1, 2, 3...)
            ObservableList<FilaRanking> filas = FXCollections.observableArrayList();
            int posicion = 1;

            // Es importante asegurar el orden por puntos (aunque el SQL ya lo hace, al agrupar se mantiene relativo)
            // Si quisieras reordenar por seguridad:
            // equiposDeLaCategoria.sort((a, b) -> Integer.compare((int)b.get("puntos"), (int)a.get("puntos")));

            for (Map<String, Object> fila : equiposDeLaCategoria) {
                filas.add(new FilaRanking(
                        posicion++,
                        (String) fila.get("equipo"),
                        (String) fila.get("escuela"),
                        (int) fila.get("puntos")
                ));
            }

            tabla.setItems(filas);
            tab.setContent(tabla);
            tabPaneRankings.getTabs().add(tab);
        }
    }

    // Método auxiliar para construir la tabla (columnas y configuración)
    private TableView<FilaRanking> crearTablaRanking() {
        TableView<FilaRanking> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Ajustar columnas al ancho

        TableColumn<FilaRanking, Integer> colPos = new TableColumn<>("#");
        colPos.setCellValueFactory(cell -> cell.getValue().posicionProperty().asObject());
        colPos.setPrefWidth(40);
        colPos.setStyle("-fx-alignment: CENTER;");

        TableColumn<FilaRanking, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(cell -> cell.getValue().equipoProperty());
        colEquipo.setPrefWidth(200);

        TableColumn<FilaRanking, String> colEscuela = new TableColumn<>("Escuela");
        colEscuela.setCellValueFactory(cell -> cell.getValue().escuelaProperty());
        colEscuela.setPrefWidth(200);

        TableColumn<FilaRanking, Integer> colPuntos = new TableColumn<>("Puntos");
        colPuntos.setCellValueFactory(cell -> cell.getValue().puntosProperty().asObject());
        colPuntos.setPrefWidth(80);
        colPuntos.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        tabla.getColumns().addAll(colPos, colEquipo, colEscuela, colPuntos);
        return tabla;
    }

    @FXML
    void btnCerrar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    // --- CLASE INTERNA PARA DATOS DE LA TABLA ---
    public static class FilaRanking {
        private final SimpleIntegerProperty posicion;
        private final SimpleStringProperty equipo;
        private final SimpleStringProperty escuela;
        private final SimpleIntegerProperty puntos;

        public FilaRanking(int pos, String eq, String esc, int pts) {
            this.posicion = new SimpleIntegerProperty(pos);
            this.equipo = new SimpleStringProperty(eq);
            this.escuela = new SimpleStringProperty(esc);
            this.puntos = new SimpleIntegerProperty(pts);
        }

        public SimpleIntegerProperty posicionProperty() { return posicion; }
        public SimpleStringProperty equipoProperty() { return equipo; }
        public SimpleStringProperty escuelaProperty() { return escuela; }
        public SimpleIntegerProperty puntosProperty() { return puntos; }
    }
}