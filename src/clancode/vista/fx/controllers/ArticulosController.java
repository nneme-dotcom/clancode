package clancode.vista.fx.controllers;

import clancode.controlador.Controlador;
import clancode.excepciones.ArticuloYaExisteException;
import clancode.modelo.Articulo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador FXML de la pestaña Artículos.
 *
 * Recibe los nodos declarados en articulos.fxml mediante @FXML y se instancia
 * automáticamente por FXMLLoader cuando se carga el archivo.
 *
 * El Controlador MVC se inyecta después de la carga con setControlador(),
 * ya que FXMLLoader no puede pasarlo por constructor.
 */
public class ArticulosController {

    // ── Nodos inyectados desde articulos.fxml ────────────────────────────────

    @FXML private TextField txtCodigo;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtGastos;
    @FXML private TextField txtTiempo;

    @FXML private TableView<Articulo>          tablaArticulos;
    @FXML private TableColumn<Articulo, String>  colCodigo;
    @FXML private TableColumn<Articulo, String>  colDescripcion;
    @FXML private TableColumn<Articulo, Double>  colPrecio;
    @FXML private TableColumn<Articulo, Double>  colGastos;
    @FXML private TableColumn<Articulo, Integer> colTiempo;

    // ── Estado interno ────────────────────────────────────────────────────────

    private Controlador controlador;
    private final ObservableList<Articulo> datos = FXCollections.observableArrayList();

    // ── Inicialización ────────────────────────────────────────────────────────

    /**
     * Llamado automáticamente por FXMLLoader tras inyectar todos los @FXML.
     * Configura las columnas de la tabla. Los datos se cargan cuando
     * el Controlador MVC se inyecta con setControlador().
     */
    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colPrecio.setCellFactory(c -> celdaMoneda());

        colGastos.setCellValueFactory(new PropertyValueFactory<>("gastosEnvio"));
        colGastos.setCellFactory(c -> celdaMoneda());

        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempoPreparacion"));

        tablaArticulos.setItems(datos);
    }

    /**
     * Inyecta el Controlador MVC y carga los datos iniciales de la tabla.
     * Debe llamarse desde MainController justo después de cargar el FXML.
     */
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
        refrescarTabla();
    }

    // ── Manejadores de eventos (@FXML → onAction en el XML) ──────────────────

    /** Recoge el formulario, valida y delega al Controlador. */
    @FXML
    private void onAñadirArticulo() {
        String codigo      = txtCodigo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioStr   = txtPrecio.getText().trim();
        String gastosStr   = txtGastos.getText().trim();
        String tiempoStr   = txtTiempo.getText().trim();

        if (codigo.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()
                || gastosStr.isEmpty() || tiempoStr.isEmpty()) {
            alerta(AlertType.WARNING, "Campos incompletos",
                "Rellena todos los campos antes de añadir el artículo.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            double gastos = Double.parseDouble(gastosStr);
            int    tiempo = Integer.parseInt(tiempoStr);

            if (precio < 0 || gastos < 0 || tiempo < 0) {
                alerta(AlertType.WARNING, "Valores inválidos",
                    "Precio, gastos y tiempo deben ser valores positivos.");
                return;
            }

            controlador.añadirArticulo(codigo, descripcion, precio, gastos, tiempo);
            alerta(AlertType.INFORMATION, "Artículo añadido",
                "El artículo '" + codigo + "' se ha guardado correctamente.");
            onLimpiar();
            refrescarTabla();

        } catch (NumberFormatException ex) {
            alerta(AlertType.ERROR, "Formato incorrecto",
                "Precio, gastos y tiempo deben ser números válidos.");
        } catch (ArticuloYaExisteException ex) {
            alerta(AlertType.ERROR, "Artículo duplicado", ex.getMessage());
        } catch (RuntimeException ex) {
            alerta(AlertType.ERROR, "Error de persistencia", ex.getMessage());
        }
    }

    /** Limpia todos los campos del formulario. */
    @FXML
    private void onLimpiar() {
        txtCodigo.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtGastos.clear();
        txtTiempo.clear();
    }

    // ── Utilidades privadas ───────────────────────────────────────────────────

    /** Recarga la lista de artículos desde el Controlador. */
    private void refrescarTabla() {
        datos.setAll(controlador.getArticulos());
    }

    /**
     * Factoría de celdas con formato "X.XX €".
     * Reutilizada para las columnas Precio y Gastos.
     */
    private TableCell<Articulo, Double> celdaMoneda() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("%.2f €", v));
            }
        };
    }

    /** Muestra un diálogo Alert estándar de JavaFX. */
    private void alerta(AlertType tipo, String cabecera, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == AlertType.INFORMATION ? "Información" :
                       tipo == AlertType.WARNING      ? "Aviso" : "Error");
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
