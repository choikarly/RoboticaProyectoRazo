import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistroParticipante implements Initializable {

    @FXML private ComboBox<String> cbEscuelasFiltradas;
    // (Borré cbEscuelaProcedencia porque usas cbEscuelasFiltradas para la lógica)

    private Map<String, Integer> mapaEscuelas = new HashMap<>();
    private int idCategoriaActual;

    @FXML private TextField txtNombreEquipo;

    // --- INTEGRANTE 1 ---
    @FXML private TextField txtNumCtrlUno;
    @FXML private TextField txtIntegranteUno;
    @FXML private TextField txtCarreraUno;
    @FXML private DatePicker dateUno;
    @FXML private ComboBox<Integer> semestreComboBoxUno; // CAMBIO A INTEGER
    @FXML private ComboBox<String> sexoComboBoxUno;

    // --- INTEGRANTE 2 ---
    @FXML private TextField txtNumCtrlDos;
    @FXML private TextField txtIntegranteDos;
    @FXML private TextField txtCarreraDos;
    @FXML private DatePicker dateDos;
    @FXML private ComboBox<Integer> semestreComboBoxDos; // CAMBIO A INTEGER
    @FXML private ComboBox<String> sexoComboBoxDos;

    // --- INTEGRANTE 3 ---
    @FXML private TextField txtNumCtrlTres;
    @FXML private TextField txtIntegranteTres;
    @FXML private TextField txtCarreraTres;
    @FXML private DatePicker dateTres;
    @FXML private ComboBox<Integer> semestreComboBoxTres; // CAMBIO A INTEGER
    @FXML private ComboBox<String> sexoComboBoxTres;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Definimos los semestres como ENTEROS para que cuadren con la BD
        Integer[] semestres = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        sexoComboBoxUno.getItems().addAll("H", "M");
        sexoComboBoxDos.getItems().addAll("H", "M");
        sexoComboBoxTres.getItems().addAll("H", "M");

        semestreComboBoxUno.getItems().addAll(semestres);
        semestreComboBoxDos.getItems().addAll(semestres);
        semestreComboBoxTres.getItems().addAll(semestres);
    }

    @FXML
    void btnGuardar(ActionEvent event) {
        // 0. Validar Escuela
        String nombreEscuela = cbEscuelasFiltradas.getValue();
        if (nombreEscuela == null || !mapaEscuelas.containsKey(nombreEscuela)) {
            mostrarAlertaError("Error", "Escuela no válida", "Selecciona una escuela de la lista.");
            return;
        }
        int idEscuela = mapaEscuelas.get(nombreEscuela);

        // 0.1 Validar Campos Vacíos Generales
        if (txtIntegranteUno.getText().isEmpty() || txtIntegranteDos.getText().isEmpty() || txtIntegranteTres.getText().isEmpty() ||
                dateUno.getValue() == null || dateDos.getValue() == null || dateTres.getValue() == null) {
            mostrarAlertaError("Error", "Campos Vacíos", "Por favor llena todos los campos de todos los integrantes.");
            return;
        }

        try {
            // ========================================================================
            // INTEGRANTE 1
            // ========================================================================
            String nombre1 = txtIntegranteUno.getText();
            java.sql.Date fecha1 = java.sql.Date.valueOf(dateUno.getValue());
            String sexo1 = sexoComboBoxUno.getValue();
            String carrera1 = txtCarreraUno.getText();
            int semestre1 = semestreComboBoxUno.getValue();
            int numCtrl1 = Integer.parseInt(txtNumCtrlUno.getText()); // Convertir String a Int

            int res1 = Main.registrarCompetidor(nombre1, fecha1, idEscuela, sexo1, carrera1, semestre1, numCtrl1);

            if (res1 == -1) {
                mostrarAlertaError("Error", "Duplicado", "El integrante 1 ya existe.");
                return; // Detenemos todo
            } else if (res1 == -2) {
                mostrarAlertaError("Error", "BD", "Error al guardar integrante 1.");
                return;
            }

            // ========================================================================
            // INTEGRANTE 2
            // ========================================================================
            String nombre2 = txtIntegranteDos.getText(); // OJO: Corregido para leer del txtDos
            java.sql.Date fecha2 = java.sql.Date.valueOf(dateDos.getValue());
            String sexo2 = sexoComboBoxDos.getValue();
            String carrera2 = txtCarreraDos.getText();
            int semestre2 = semestreComboBoxDos.getValue();
            int numCtrl2 = Integer.parseInt(txtNumCtrlDos.getText());

            int res2 = Main.registrarCompetidor(nombre2, fecha2, idEscuela, sexo2, carrera2, semestre2, numCtrl2);

            if (res2 == -1) {
                mostrarAlertaError("Error", "Duplicado", "El integrante 2 ya existe.");
                return;
            } else if (res2 == -2) {
                mostrarAlertaError("Error", "BD", "Error al guardar integrante 2.");
                return;
            }

            // ========================================================================
            // INTEGRANTE 3
            // ========================================================================
            String nombre3 = txtIntegranteTres.getText(); // OJO: Corregido para leer del txtTres
            java.sql.Date fecha3 = java.sql.Date.valueOf(dateTres.getValue());
            String sexo3 = sexoComboBoxTres.getValue();
            String carrera3 = txtCarreraTres.getText();
            int semestre3 = semestreComboBoxTres.getValue();
            int numCtrl3 = Integer.parseInt(txtNumCtrlTres.getText());

            int res3 = Main.registrarCompetidor(nombre3, fecha3, idEscuela, sexo3, carrera3, semestre3, numCtrl3);

            if (res3 == -1) {
                mostrarAlertaError("Error", "Duplicado", "El integrante 3 ya existe.");
                return;
            } else if (res3 == -2) {
                mostrarAlertaError("Error", "BD", "Error al guardar integrante 3.");
                return;
            }

            // ========================================================================
            // FINALIZACIÓN (Si llegó aquí, los 3 se guardaron)
            // ========================================================================
            mostrarAlertaExito("Éxito", "Registro Completo", "Equipo registrado correctamente.");

            // Cerrar y volver al Login
            Node source = (Node) event.getSource();
            Stage stageActual = (Stage) source.getScene().getWindow();
            stageActual.close();


        } catch (NumberFormatException e) {
            mostrarAlertaError("Error de Formato", "Números inválidos", "El semestre y número de control deben ser números enteros.");
        } catch (Exception e) {
            mostrarAlertaError("Error", "Fallo del Sistema", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE CARGA Y UTILIDAD ---



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

        if (cbEscuelasFiltradas.getItems().isEmpty()) {
            mostrarAlertaError("Aviso", "Sin Escuelas", "No hay escuelas registradas para esta categoría.");
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