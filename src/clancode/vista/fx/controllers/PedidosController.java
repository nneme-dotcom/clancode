package clancode.vista.fx.controllers;

import clancode.controlador.Controlador;
import clancode.excepciones.ArticuloNoEncontradoException;
import clancode.excepciones.PedidoNoCancelableException;
import clancode.excepciones.PedidoNoEncontradoException;
import clancode.modelo.Cliente;
import clancode.modelo.Pedido;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/** Controlador FXML de la pestaña Pedidos. */
public class PedidosController {

    @FXML private TextField txtEmailPedido;
    @FXML private TextField txtCodigoPedido;
    @FXML private TextField txtCantidad;
    @FXML private Label     lblInfoCliente;

    @FXML private TextField txtNumeroPedido;

    @FXML private TextField txtFiltroEmail;
    @FXML private Button    btnTodos;
    @FXML private Button    btnPendientes;
    @FXML private Button    btnEnviados;

    @FXML private TableView<Pedido>           tablaPedidos;
    @FXML private TableColumn<Pedido, Integer> colNumero;
    @FXML private TableColumn<Pedido, String>  colCliente;
    @FXML private TableColumn<Pedido, String>  colArticulo;
    @FXML private TableColumn<Pedido, Integer> colCantidad;
    @FXML private TableColumn<Pedido, Double>  colTotal;
    @FXML private TableColumn<Pedido, String>  colFecha;
    @FXML private TableColumn<Pedido, String>  colEstado;

    private Controlador controlador;
    private final ObservableList<Pedido> datos = FXCollections.observableArrayList();

    private String filtroEstado = "todos";

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(cd ->
            new SimpleIntegerProperty(cd.getValue().getNumeroPedido()).asObject());

        colCliente.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getCliente().getEmail()));

        colArticulo.setCellValueFactory(cd ->
            new SimpleStringProperty(
                cd.getValue().getArticulo().getCodigo()
                + " – " + cd.getValue().getArticulo().getDescripcion()));

        colCantidad.setCellValueFactory(cd ->
            new SimpleIntegerProperty(cd.getValue().getCantidad()).asObject());

        colTotal.setCellValueFactory(cd ->
            new SimpleDoubleProperty(cd.getValue().getPrecioTotal()).asObject());
        colTotal.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("%.2f €", v));
            }
        });

        colFecha.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getFechaHora().format(FMT)));

        colEstado.setCellValueFactory(cd ->
            new SimpleStringProperty(
                cd.getValue().esCancelable() ? "⏳ Pendiente" : "✅ Enviado"));

        tablaPedidos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Pedido p, boolean empty) {
                super.updateItem(p, empty);
                if (!empty && p != null) {
                    setStyle(p.esCancelable()
                        ? "-fx-background-color: #fff8e1;"
                        : "-fx-background-color: #e8f5e9;");
                } else {
                    setStyle("");
                }
            }
        });

        txtEmailPedido.focusedProperty().addListener((obs, anterior, focused) -> {
            if (!focused && controlador != null) actualizarInfoCliente();
        });

        tablaPedidos.setItems(datos);
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
        refrescarTabla();
    }

    @FXML
    private void onEmailFocusLost() {
        actualizarInfoCliente();
    }

    @FXML
    private void onCrearPedido() {
        String email   = txtEmailPedido.getText().trim();
        String codigo  = txtCodigoPedido.getText().trim();
        String cantStr = txtCantidad.getText().trim();

        if (email.isEmpty() || codigo.isEmpty() || cantStr.isEmpty()) {
            alerta(AlertType.WARNING, "Campos incompletos",
                "Rellena el email del cliente, el código del artículo y la cantidad.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) {
                alerta(AlertType.WARNING, "Cantidad inválida",
                    "La cantidad debe ser un número entero mayor que 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            alerta(AlertType.ERROR, "Formato incorrecto",
                "La cantidad debe ser un número entero.");
            return;
        }

        Cliente cliente = controlador.buscarClienteONull(email);
        if (cliente == null) {
            alerta(AlertType.ERROR, "Cliente no encontrado",
                "No existe ningún cliente con email: " + email
                + "\nRegístralo primero en la pestaña Clientes.");
            return;
        }

        try {
            controlador.añadirPedidoConCliente(cliente, codigo, cantidad);
            alerta(AlertType.INFORMATION, "Pedido creado",
                "El pedido para '" + email + "' se ha guardado correctamente.");
            onLimpiarPedido();
            refrescarTabla();

        } catch (ArticuloNoEncontradoException ex) {
            alerta(AlertType.ERROR, "Artículo no encontrado", ex.getMessage());
        } catch (RuntimeException ex) {
            alerta(AlertType.ERROR, "Error de persistencia", ex.getMessage());
        }
    }

    @FXML
    private void onLimpiarPedido() {
        txtEmailPedido.clear();
        txtCodigoPedido.clear();
        txtCantidad.clear();
        lblInfoCliente.setText("");
    }

    @FXML
    private void onEliminarPedido() {
        String numStr = txtNumeroPedido.getText().trim();
        if (numStr.isEmpty()) {
            alerta(AlertType.WARNING, "Campo vacío",
                "Introduce el número del pedido a eliminar.");
            return;
        }

        int numero;
        try {
            numero = Integer.parseInt(numStr);
        } catch (NumberFormatException ex) {
            alerta(AlertType.ERROR, "Formato incorrecto",
                "El número de pedido debe ser un entero.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar pedido nº " + numero + "?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            controlador.eliminarPedido(numero);
            alerta(AlertType.INFORMATION, "Pedido eliminado",
                "El pedido nº " + numero + " se ha eliminado correctamente.");
            txtNumeroPedido.clear();
            refrescarTabla();

        } catch (PedidoNoEncontradoException | PedidoNoCancelableException ex) {
            alerta(AlertType.ERROR, "No se puede eliminar", ex.getMessage());
        } catch (RuntimeException ex) {
            alerta(AlertType.ERROR, "Error de persistencia", ex.getMessage());
        }
    }

    @FXML
    private void onFiltroTodos() {
        filtroEstado = "todos";
        marcarActivo(btnTodos, btnPendientes, btnEnviados);
        refrescarTabla();
    }

    @FXML
    private void onFiltroPendientes() {
        filtroEstado = "pendientes";
        marcarActivo(btnPendientes, btnTodos, btnEnviados);
        refrescarTabla();
    }

    @FXML
    private void onFiltroEnviados() {
        filtroEstado = "enviados";
        marcarActivo(btnEnviados, btnTodos, btnPendientes);
        refrescarTabla();
    }

    @FXML
    private void onFiltrarEmail() {
        refrescarTabla();
    }

    @FXML
    private void onLimpiarFiltroEmail() {
        txtFiltroEmail.clear();
        refrescarTabla();
    }

    private void actualizarInfoCliente() {
        String email = txtEmailPedido.getText().trim();
        if (email.isEmpty()) {
            lblInfoCliente.setText("");
            return;
        }
        Cliente c = controlador.buscarClienteONull(email);
        if (c != null) {
            lblInfoCliente.setText("✓ " + c.getNombre() + " (" + c.tipoCliente() + ")");
            lblInfoCliente.setStyle("-fx-text-fill: #27ae60; -fx-font-style: italic;");
        } else {
            lblInfoCliente.setText("⚠ Cliente no encontrado — regístralo primero");
            lblInfoCliente.setStyle("-fx-text-fill: #e67e22; -fx-font-style: italic;");
        }
    }

    private void refrescarTabla() {
        String email = txtFiltroEmail.getText().trim();
        boolean hayEmail = !email.isEmpty();

        switch (filtroEstado) {
            case "pendientes" -> datos.setAll(hayEmail
                ? controlador.getPedidosPendientesPorCliente(email)
                : controlador.getPedidosPendientes());
            case "enviados" -> datos.setAll(hayEmail
                ? controlador.getPedidosEnviadosPorCliente(email)
                : controlador.getPedidosEnviados());
            default -> {
                var lista = new ArrayList<Pedido>();
                if (hayEmail) {
                    lista.addAll(controlador.getPedidosPendientesPorCliente(email));
                    lista.addAll(controlador.getPedidosEnviadosPorCliente(email));
                } else {
                    lista.addAll(controlador.getPedidosPendientes());
                    lista.addAll(controlador.getPedidosEnviados());
                }
                datos.setAll(lista);
            }
        }
    }

    private void marcarActivo(Button activo, Button... inactivos) {
        activo.setStyle("-fx-padding: 6 14; -fx-background-color: #2980b9; -fx-text-fill: white;");
        for (Button b : inactivos) b.setStyle("-fx-padding: 6 14;");
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
