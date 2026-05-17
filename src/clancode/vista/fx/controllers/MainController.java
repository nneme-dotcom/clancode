package clancode.vista.fx.controllers;

import clancode.controlador.Controlador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.io.IOException;

/**
 * Controlador FXML de la ventana principal (main.fxml).
 *
 * Responsabilidades:
 *   1. Cargar los tres FXML hijos (articulos, clientes, pedidos) mediante FXMLLoader.
 *   2. Inyectar el Controlador MVC en cada controlador hijo con setControlador().
 *   3. Actualizar la barra de estado inferior según el modo de persistencia.
 *
 * El Controlador MVC llega desde MainApp mediante setControlador(),
 * que se llama justo después de cargar este FXML.
 */
public class MainController {

    // ── Nodos inyectados desde main.fxml ──────────────────────────────────────

    @FXML private Tab   tabArticulos;
    @FXML private Tab   tabClientes;
    @FXML private Tab   tabPedidos;
    @FXML private Label lblEstadoBD;

    // ── Inicialización ────────────────────────────────────────────────────────

    /**
     * Recibe el Controlador MVC y lo propaga a las tres pestañas.
     * Carga cada pestaña desde su FXML y le inyecta el controlador.
     * Actualiza la barra de estado con el modo de persistencia activo.
     *
     * @param controlador instancia única del Controlador MVC
     */
    public void setControlador(Controlador controlador) {
        tabArticulos.setContent(cargarPestana("articulos.fxml", controlador));
        tabClientes.setContent(cargarPestana("clientes.fxml",  controlador));
        tabPedidos.setContent(cargarPestana("pedidos.fxml",   controlador));
        actualizarBarra(controlador.isModoMySQL());
    }

    // ── Utilidades privadas ───────────────────────────────────────────────────

    /**
     * Carga un FXML de pestaña, obtiene su controlador y le inyecta el Controlador MVC.
     *
     * El patrón es: FXMLLoader crea el controlador del FXML → llamamos setControlador()
     * para pasarle la lógica de negocio. Esto es necesario porque FXMLLoader instancia
     * los controladores sin constructor con parámetros.
     *
     * @param archivo   nombre del archivo fxml (relativo a la carpeta fxml/)
     * @param controlador instancia del Controlador MVC a inyectar
     * @return el nodo raíz del FXML, listo para poner como contenido de la Tab
     */
    private javafx.scene.Node cargarPestana(String archivo, Controlador controlador) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/clancode/vista/fx/fxml/" + archivo));
            javafx.scene.Node nodo = loader.load();

            // Inyectamos el controlador MVC en el controlador FXML de la pestaña
            switch (archivo) {
                case "articulos.fxml" -> {
                    ArticulosController c = loader.getController();
                    c.setControlador(controlador);
                }
                case "clientes.fxml" -> {
                    ClientesController c = loader.getController();
                    c.setControlador(controlador);
                }
                case "pedidos.fxml" -> {
                    PedidosController c = loader.getController();
                    c.setControlador(controlador);
                }
            }
            return nodo;

        } catch (IOException e) {
            // Si el FXML no carga, mostramos un Label de error en la pestaña
            // en lugar de dejar la app sin funcionar
            System.err.println("[MainController] No se pudo cargar " + archivo + ": " + e.getMessage());
            return new Label("Error al cargar la vista: " + archivo);
        }
    }

    /**
     * Actualiza el texto y color de la barra de estado inferior.
     *
     * @param mysqlActivo true si el Controlador está usando MySQL
     */
    private void actualizarBarra(boolean mysqlActivo) {
        if (mysqlActivo) {
            lblEstadoBD.setText("●  MySQL conectado");
            lblEstadoBD.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");
        } else {
            lblEstadoBD.setText("●  Modo memoria (BD no disponible)");
            lblEstadoBD.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 11px;");
        }
    }
}
