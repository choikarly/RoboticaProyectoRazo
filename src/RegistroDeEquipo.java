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
import java.util.Map;
import java.util.ResourceBundle;


public class RegistroDeEquipo implements Initializable {
    @FXML private ComboBox<String> cbEscuelasFiltradas;
    private Map<String, Integer> mapaEscuelas = new HashMap<>();
    private int idCategoriaActual;

    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private ComboBox<String> cbEscuelaProcedencia;

    @FXML
    private TextField txtNumCtrlUno;
    @FXML
    private TextField txtIntegranteUno;
    @FXML
    private TextField txtCarreraUno;
    @FXML
    private DatePicker dateUno;
    @FXML
    private ComboBox<String> semestreComboBoxUno;
    @FXML
    private ComboBox<String> sexoComboBoxUno;




    @FXML
    private TextField txtNumCtrlDos;
    @FXML
    private TextField txtIntegranteDos;
    @FXML
    private TextField txtCarreraDos;
    @FXML
    private DatePicker dateDos;
    @FXML
    private ComboBox<String> semestreComboBoxDos;
    @FXML
    private ComboBox<String> sexoComboBoxDos;


    @FXML
    private TextField txtNumCtrlTres;
    @FXML
    private TextField txtIntegranteTres;
    @FXML
    private TextField txtCarreraTres;
    @FXML
    private DatePicker dateTres;
    @FXML
    private ComboBox<String> semestreComboBoxTres;
    @FXML
    private ComboBox<String> sexoComboBoxTres;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        sexoComboBoxUno.getItems().addAll("H", "M");
        sexoComboBoxDos.getItems().addAll("H", "M");
        sexoComboBoxTres.getItems().addAll("H", "M");
    }

    @FXML
    void btnGuardar(ActionEvent event) {

    }

    public void recibirCategoriaYCargarEscuelas(int idCategoria) {
        this.idCategoriaActual = idCategoria;
        System.out.println("Cargando escuelas para la categoría ID: " + idCategoria);
        cargarEscuelasPorNivel(idCategoria);
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
