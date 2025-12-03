import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class RegistroDeEquipo{
    @FXML private ComboBox<String> cbEscuelasFiltradas;
    private Map<String, Integer> mapaEscuelas = new HashMap<>();
/*
    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private TextField txtNumCtrlUno;
    @FXML
    private TextField txtNumCtrlDos;
    @FXML
    private TextField txtNumCtrlTres;
    @FXML
    private TextField txtIntegranteUno;
    @FXML
    private TextField txtIntegranteDos;
    @FXML
    private TextField txtIntegranteTres;

*/
    // Variable para guardar qué categoría estamos usando
    private int idCategoriaActual;

    public void recibirCategoriaYCargarEscuelas(int idCategoria) {
        this.idCategoriaActual = idCategoria;
        System.out.println("Cargando escuelas para la categoría ID: " + idCategoria);
        cargarEscuelasPorNivel(idCategoria);
    }

    @FXML
    void btnGuardar(ActionEvent event) {
        System.out.println("Información Guardada Exitosamente");

        Node source = (Node) event.getSource();
        Stage stageActual = (Stage) source.getScene().getWindow();
        stageActual.close();
    }


    private void cargarEscuelasPorNivel(int idFiltro) {
        // SQL CON FILTRO: Solo trae las escuelas que coincidan con la categoría/nivel
        String sql = "SELECT id_escuela, nombre FROM escuela WHERE fk_nivel = ?";

        try (Connection con = Main.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFiltro); // Pasamos el ID que recibimos

            try (ResultSet rs = ps.executeQuery()) {
                cbEscuelasFiltradas.getItems().clear();
                mapaEscuelas.clear();

                while (rs.next()) {
                    String nombre = rs.getString("nombre");
                    int id = rs.getInt("id_escuela");

                    cbEscuelasFiltradas.getItems().add(nombre);
                    mapaEscuelas.put(nombre, id);
                }
            }

            // Si no encontró nada, avisa en consola
            if (cbEscuelasFiltradas.getItems().isEmpty()) {
                System.out.println("No hay escuelas registradas para este nivel/categoría.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
