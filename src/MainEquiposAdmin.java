import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox; // O FlowPane, según tu diseño
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainEquiposAdmin implements Initializable {

    @FXML private VBox vboxContenedorEquiposFiltradosAdmin; // El contenedor donde van las tarjetas

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Al iniciar, cargamos TODOS los equipos (-1, -1)
        cargarEquiposFiltrados(-1, -1);
    }

    // Este método abre la ventana pequeña
    @FXML
    void btnAbrirFiltro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltroEquipo.fxml"));
            Parent root = loader.load();

            // 1. Obtener el controlador del filtro
            FiltroEquipos controller = loader.getController();

            // 2. Pasarle 'this' (esta clase) para que pueda llamarnos de vuelta
            controller.setPadre(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Filtrar Equipos");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- ESTE MÉTODO ES LLAMADO POR 'FiltroEquipos' O AL INICIAR ---

    // En MainEquiposAdmin.java

    public void cargarEquiposFiltrados(int idEvento, int idCategoria) {
        vboxContenedorEquiposFiltradosAdmin.getChildren().clear();

        // 1. Llamamos a la función que trae EQUIPOS (no eventos)
        List<Map<String, Object>> lista = Main.retornarEquiposAdminFiltro(idEvento, idCategoria);

        if (lista.isEmpty()) {
            vboxContenedorEquiposFiltradosAdmin.setVisible(false);
            vboxContenedorEquiposFiltradosAdmin.setManaged(false);

        } else {
            vboxContenedorEquiposFiltradosAdmin.setVisible(true);
            vboxContenedorEquiposFiltradosAdmin.setManaged(true);
            vboxContenedorEquiposFiltradosAdmin.setSpacing(10);

            try{
                for (Map<String, Object> fila : lista) {
                    String equipo = (String) fila.get("equipo");
                    String evento = (String) fila.get("evento");
                    String escuela = (String) fila.get("escuela");
                    String categoria = (String) fila.get("categoria");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaMisEquipos.fxml"));
                    AnchorPane tarjeta = loader.load();

                    PlantillaMisEquipos cardCtrl = loader.getController();
                    cardCtrl.setDatosMisEquipos(equipo, evento, escuela, categoria);

                    vboxContenedorEquiposFiltradosAdmin.getChildren().add(tarjeta);

                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}