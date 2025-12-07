import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroDeEquipo implements Initializable {

    @FXML private ComboBox<String> cbEscuelasFiltradas;
    // Nota: Eliminé cbEscuelaProcedencia porque usas cbEscuelasFiltradas en la lógica

    private Map<String, Integer> mapaEscuelas = new HashMap<>();
    private int idCategoriaActual;

    @FXML private TextField txtNombreEquipo;

    // --- INTEGRANTE 1 ---
    @FXML private TextField txtNumCtrlUno;
    @FXML private TextField txtIntegranteUno;
    @FXML private TextField txtCarreraUno;
    @FXML private DatePicker dateUno;
    @FXML private ComboBox<Integer> semestreComboBoxUno; // Cambiado a Integer
    @FXML private ComboBox<String> sexoComboBoxUno;

    // --- INTEGRANTE 2 ---
    @FXML private TextField txtNumCtrlDos;
    @FXML private TextField txtIntegranteDos;
    @FXML private TextField txtCarreraDos;
    @FXML private DatePicker dateDos;
    @FXML private ComboBox<Integer> semestreComboBoxDos; // Cambiado a Integer
    @FXML private ComboBox<String> sexoComboBoxDos;

    // --- INTEGRANTE 3 ---
    @FXML private TextField txtNumCtrlTres;
    @FXML private TextField txtIntegranteTres;
    @FXML private TextField txtCarreraTres;
    @FXML private DatePicker dateTres;
    @FXML private ComboBox<Integer> semestreComboBoxTres; // Cambiado a Integer
    @FXML private ComboBox<String> sexoComboBoxTres;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Definimos los semestres
        Integer[] semestres = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        // 2. Llenamos los combos
        sexoComboBoxUno.getItems().addAll("H", "M");
        sexoComboBoxDos.getItems().addAll("H", "M");
        sexoComboBoxTres.getItems().addAll("H", "M");

        semestreComboBoxUno.getItems().addAll(semestres);
        semestreComboBoxDos.getItems().addAll(semestres);
        semestreComboBoxTres.getItems().addAll(semestres);
    }

    @FXML
    void btnGuardar(ActionEvent event) {
        // 1. Validar que se haya seleccionado una escuela
        String nombreEscuelaSeleccionada = cbEscuelasFiltradas.getValue();

        if (nombreEscuelaSeleccionada == null || !mapaEscuelas.containsKey(nombreEscuelaSeleccionada)) {
            mostrarAlertaError("Error", "Escuela no válida", "Por favor selecciona una escuela de la lista.");
            return;
        }

        int idEscuela = mapaEscuelas.get(nombreEscuelaSeleccionada);

        // 2. Intentar registrar a los 3 estudiantes
        // Nota: Solo continuamos si el anterior fue exitoso o si decides que pueden fallar individualmente.
        // Aquí asumiré que deben registrarse los 3 para que sea éxito total.

        boolean r1 = registrarEstudianteIndividual(txtIntegranteUno, dateUno, sexoComboBoxUno, txtCarreraUno, semestreComboBoxUno, txtNumCtrlUno, idEscuela, 1);
        if (!r1) return; // Si falla el 1, nos detenemos

        boolean r2 = registrarEstudianteIndividual(txtIntegranteDos, dateDos, sexoComboBoxDos, txtCarreraDos, semestreComboBoxDos, txtNumCtrlDos, idEscuela, 2);
        if (!r2) return;

        boolean r3 = registrarEstudianteIndividual(txtIntegranteTres, dateTres, sexoComboBoxTres, txtCarreraTres, semestreComboBoxTres, txtNumCtrlTres, idEscuela, 3);
        if (!r3) return;

        // 3. Si llegamos aquí, los 3 se registraron (o intentaron)
        mostrarAlertaExito(
                "Éxito",
                "Equipo Registrado",
                "Los participantes han sido registrados correctamente.");
        cerrarVentana(event);
    }

    // --- MÉTODO AUXILIAR PARA NO REPETIR CÓDIGO 3 VECES ---
    private boolean registrarEstudianteIndividual(TextField txtNombre, DatePicker datePicker, ComboBox<String> cbSexo,
                                                  TextField txtCarrera, ComboBox<Integer> cbSemestre, TextField txtNumCtrl,
                                                  int idEscuela, int numIntegrante) {

        // Validación básica de campos vacíos para este estudiante
        if (txtNombre.getText().isEmpty() || datePicker.getValue() == null || cbSexo.getValue() == null ||
                txtCarrera.getText().isEmpty() || cbSemestre.getValue() == null || txtNumCtrl.getText().isEmpty()) {
            mostrarAlertaError(
                    "Error",
                    "Datos Incompletos",
                    "Faltan datos del Integrante " + numIntegrante);
            return false;
        }

        try {
            // Extracción y Conversión de datos
            String nombre = txtNombre.getText();
            java.sql.Date fechaNacimiento = java.sql.Date.valueOf(datePicker.getValue());
            String sexo = cbSexo.getValue();
            String carrera = txtCarrera.getText();
            int semestre = cbSemestre.getValue(); // Ya es int porque cambiamos el ComboBox a Integer
            int numControl = Integer.parseInt(txtNumCtrl.getText()); // Convertir String a int

            // Llamada al MAIN
            int resultado = Main.registrarCompetidor(nombre, fechaNacimiento, idEscuela, sexo, carrera, semestre, numControl);

            if (resultado == 1) {
                return true; // Éxito
            } else if (resultado == -1) {
                mostrarAlertaError("Error", "Duplicado", "El integrante " + numIntegrante + " con No. Control " + numControl + " ya existe.");
                return false;
            } else {
                mostrarAlertaError("Error", "Base de Datos", "Error al guardar al integrante " + numIntegrante);
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlertaError("Error",
                    "Formato Numérico",
                    "El No. Control del integrante " + numIntegrante + " debe ser un número entero.");
            return false;
        } catch (Exception e) {
            mostrarAlertaError("Error",
                    "Excepción",
                    "Ocurrió un error inesperado: " + e.getMessage());
            return false;
        }
    }

    private void cerrarVentana(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stageActual = (Stage) source.getScene().getWindow();
            stageActual.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("InicioSesion.fxml"));
            Parent root = loader.load();
            Stage stageNuevo = new Stage();
            stageNuevo.setScene(new Scene(root));
            stageNuevo.setTitle("Inicio de Sesion");
            stageNuevo.setResizable(false);
            stageNuevo.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE CARGA DE ESCUELAS (Sin cambios mayores, solo integración) ---

    public void recibirCategoriaYCargarEscuelas(int idCategoria) {
        this.idCategoriaActual = idCategoria;
        cargarEscuelasPorNivel(idCategoria);
    }

    private void cargarEscuelasPorNivel(int idFiltro) {
        List<Map<String, Object>> todasLasEscuelas = Main.retornarEscuelas();
        cbEscuelasFiltradas.getItems().clear();
        mapaEscuelas.clear();

        for (Map<String, Object> fila : todasLasEscuelas) {
            int nivelEscuela = (int) fila.get("fk_nivel");
            if (nivelEscuela == idFiltro) {
                String nombre = (String) fila.get("nombre");
                int id = (int) fila.get("id_escuela");
                cbEscuelasFiltradas.getItems().add(nombre);
                mapaEscuelas.put(nombre, id);
            }
        }
    }

    public void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void mostrarAlertaExito(String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}