import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainEquiposAdmin implements Initializable {

    @FXML private VBox vboxContenedorEquiposFiltradosAdmin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Al iniciar, cargamos TODOS los equipos (-1, -1)
        cargarEquiposFiltrados(-1, -1);
    }

    @FXML
    void btnAbrirFiltro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltroEquipo.fxml"));
            Parent root = loader.load();

            FiltroEquipos controller = loader.getController();
            controller.setPadre(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Filtrar Equipos");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarEquiposFiltrados(int idEvento, int idCategoria) {
        vboxContenedorEquiposFiltradosAdmin.getChildren().clear();

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
                    // 1. Extraer los IDs (Ahora ya vienen gracias al cambio en Main y SQL)
                    int idEquipo = (int) fila.get("id_equipo");
                    int idEventoFila = (int) fila.get("id_evento");

                    String equipo = (String) fila.get("equipo");
                    String evento = (String) fila.get("evento");
                    String escuela = (String) fila.get("escuela");
                    String categoria = (String) fila.get("categoria");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("PlantillaMisEquipos.fxml"));
                    AnchorPane tarjeta = loader.load();

                    PlantillaMisEquipos cardCtrl = loader.getController();

                    // 2. Usar el método actualizado que acepta los 6 parámetros
                    cardCtrl.setDatosMisEquipos(idEquipo, idEventoFila, equipo, evento, escuela, categoria);

                    vboxContenedorEquiposFiltradosAdmin.getChildren().add(tarjeta);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}