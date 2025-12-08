import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class InfoEventos implements Initializable {
    @FXML
    private ComboBox<String> cbEquipos;
    @FXML
    private Label lblNombreEvento;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblSede;
    private Map<String, Integer> mapaEquipos = new HashMap<>();

    @FXML
    public void initialize (URL url, ResourceBundle resourceBundle){
        cargarEquiposDelCoach();

    }

    @FXML
    void btnContinuar(ActionEvent event) { //ESTE TE LLEVA A "RegistroDeEquipo"
        try {
            String nombreEquipo = cbEquipos.getValue();
/*
            if (nombreEquipo == null) {
                mostrarAlertaError(
                        "Error",
                        "Falta EQUIPO",
                        "Selecciona un Equipo.");
            } else {*/
                // 1. OBTENER EL ID SELECCIONADO
                int idEquiposSeleccionado = mapaEquipos.get(nombreEquipo);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroDeEquipo.fxml"));
                Parent root = loader.load();

                RegistroDeEquipo ventanaSig = loader.getController();
                // 4. PASAR EL ID A LA SIGUIENTE VENTANA
                // Asegúrate que 'RegistroDeEquipo' tenga este método creado
                //ventanaSig.recibirCategoriaYCargarEscuelas(idEquiposSeleccionado);

                Stage stagePaso2 = new Stage();
                stagePaso2.setScene(new Scene(root));
                stagePaso2.setTitle("Integrantes");
                stagePaso2.initModality(Modality.APPLICATION_MODAL);
                stagePaso2.setResizable(false);
                stagePaso2.show();

           // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnIrARegistrarEquipo(ActionEvent event) { //te lleva a CrearEquipo, lo cierras y regresas a InfoEventos para seleccionar el equipo
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CrearEquipo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Nuevo Equipo");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();
            cargarEquiposDelCoach();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void recibirDatosEvento(String nombre, String fecha, String sede) {
        // 2. Actualizamos la interfaz visual
        lblNombreEvento.setText(nombre);
        lblFecha.setText(fecha);
        lblSede.setText(sede);
    }

    private void cargarEquiposDelCoach() {
        // 1. Obtener el ID del coach logueado
        int idCoach = Main.usuaioActual;
        List<Map<String, Object>> lista = Main.retornarEquiposCoach(idCoach);

        // 2. Limpiar
        cbEquipos.getItems().clear();
        // 3. Limpiar el ComboBox visualmente
        cbEquipos.getItems().clear();
        mapaEquipos.clear();

        // 4. Recorrer la lista
        for (Map<String, Object> fila : lista) {
            String nombreEquipo = (String) fila.get("nombre");
            int id = (int) fila.get("id_equipo");


            // Lo agregamos al ComboBox
            cbEquipos.getItems().add(nombreEquipo);
            mapaEquipos.put(nombreEquipo, id);
        }

    }


    /*private void cargarCategoriasDesdeBD() {
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
    }*/

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
