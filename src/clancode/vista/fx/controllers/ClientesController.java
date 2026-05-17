package clancode.vista.fx.controllers;

import clancode.controlador.Controlador;
import clancode.excepciones.ClienteYaExisteException;
import clancode.modelo.Cliente;
import clancode.modelo.ClientePremium;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador FXML de la pestaña Clientes.
 *
 * Gestiona el formulario de alta, los filtros rápidos (Todos/Estándar/Premium)
 * y la tabla de clientes con resaltado visual para clientes Premium.
 *
 * El Controlador MVC se inyecta con setControlador() desde MainController.
 */
public class ClientesController {

    // ── Nodos inyectados desde clientes.fxml ─────────────────────────────────

    @FXML private TextField   txtNombre;
    @FXML private TextField   txtDomicilio;
    @FXML private TextField   txtNif;
    @FXML private TextField   txtEmail;
    @FXML private RadioButton rbEstandar;
    @FXML private RadioButton rbPremium;

    @FXML private Button btnTodos;
    @FXML private Button btnEstandar;
    @FXML private Button btnPremium;

    @FXML private TableView<Cliente>          tablaClientes;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colNif;
    @FXML private TableColumn<Cliente, String> colDomicilio;
    @FXML private TableColumn<Cliente, String> colTipo;

    // ── Estado interno ────────────────────────────────────────────────────────

    private Controlador controlador;
    private final ObservableList<Cliente> datos = FXCollections.observableArrayList();

    // ── Inicialización ────────────────────────────────────────────────────────

    /**
     * Inicializa columnas y ToggleGroup.
     * Los RadioButton del FXML no comparten grupo automáticamente;
     * hay que asignarlo aquí manualmente.
     */
    @FXML
    public void initialize() {
        // Agrupar los RadioButton para que sean mutuamente excluyentes
        ToggleGroup grupo = new ToggleGroup();
        rbEstandar.setToggleGroup(grupo);
        rbPremium.setToggleGroup(grupo);

        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNif.setCellValueFactory(new PropertyValueFactory<>("nif"));
        colDomicilio.setCellValueFactory(new PropertyValueFactory<>("domicilio"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoCliente"));

        // Filas Premium con fondo amarillo suave para distinguirlas visualmente
        tablaClientes.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setStyle(!empty && c instanceof ClientePremium
                    ? "-fx-background-color: #fffde7;" : "");
            }
        });

        tablaClientes.setItems(datos);
    }

    /** Inyecta el Controlador MVC y carga la tabla con todos los clientes. */
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
        refrescarTabla("todos");
    }

    // ── Manejadores de eventos ────────────────────────────────────────────────

    /** Valida el formulario y crea el cliente mediante el Controlador. */
    @FXML
    private void onAñadirCliente() {
        String nombre    = txtNombre.getText().trim();
        String domicilio = txtDomicilio.getText().trim();
        String nif       = txtNif.getText().trim();
        String email     = txtEmail.getText().trim();

        if (nombre.isEmpty() || domicilio.isEmpty() || nif.isEmpty() || email.isEmpty()) {
            alerta(AlertType.WARNING, "Campos incompletos",
                "Rellena todos los campos antes de añadir el cliente.");
            return;
        }
        if (!email.contains("@")) {
            alerta(AlertType.WARNING, "Email inválido",
                "El email introducido no tiene un formato válido.");
            return;
        }

        try {
            if (rbPremium.isSelected()) {
                controlador.añadirClientePremium(nombre, domicilio, nif, email);
            } else {
                controlador.añadirClienteEstandar(nombre, domicilio, nif, email);
            }
            alerta(AlertType.INFORMATION, "Cliente añadido",
                "El cliente '" + email + "' se ha guardado correctamente.");
            onLimpiar();
            refrescarTabla("todos");
            marcarActivo(btnTodos, btnEstandar, btnPremium);

        } catch (ClienteYaExisteException ex) {
            alerta(AlertType.ERROR, "Cliente duplicado", ex.getMessage());
        } catch (RuntimeException ex) {
            alerta(AlertType.ERROR, "Error de persistencia", ex.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        txtNombre.clear();
        txtDomicilio.clear();
        txtNif.clear();
        txtEmail.clear();
        rbEstandar.setSelected(true);
    }

    @FXML
    private void onFiltroTodos() {
        marcarActivo(btnTodos, btnEstandar, btnPremium);
        refrescarTabla("todos");
    }

    @FXML
    private void onFiltroEstandar() {
        marcarActivo(btnEstandar, btnTodos, btnPremium);
        refrescarTabla("estandar");
    }

    @FXML
    private void onFiltroPremium() {
        marcarActivo(btnPremium, btnTodos, btnEstandar);
        refrescarTabla("premium");
    }

    // ── Utilidades privadas ───────────────────────────────────────────────────

    private void refrescarTabla(String filtro) {
        switch (filtro) {
            case "estandar" -> datos.setAll(controlador.getClientesEstandar());
            case "premium"  -> datos.setAll(controlador.getClientesPremium());
            default         -> datos.setAll(controlador.getClientes());
        }
    }

    /** Resalta el botón de filtro activo y deja los demás en estilo neutro. */
    private void marcarActivo(Button activo, Button... inactivos) {
        activo.setStyle("-fx-padding: 6 16; -fx-background-color: #2980b9; -fx-text-fill: white;");
        for (Button b : inactivos) b.setStyle("-fx-padding: 6 16;");
    }

    private void alerta(AlertType tipo, String cabecera, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == AlertType.INFORMATION ? "Información" :
                       tipo == AlertType.WARNING      ? "Aviso" : "Error");
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
