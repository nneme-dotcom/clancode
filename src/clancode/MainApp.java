package clancode;

import clancode.controlador.Controlador;
import clancode.vista.fx.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la interfaz gráfica JavaFX (Producto 5).
 *
 * Flujo de arranque:
 *   1. Se crea el Controlador MVC (intenta MySQL, cae a memoria si falla).
 *   2. Se carga main.fxml mediante FXMLLoader.
 *   3. Se obtiene el MainController del FXML y se le inyecta el Controlador MVC.
 *   4. MainController carga a su vez los tres FXML hijos (artículos, clientes, pedidos).
 *
 * Para ejecutar: esta clase es la Main class de la Run Configuration en IntelliJ.
 * VM options necesarias:
 *   --module-path "ruta/a/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 1. Crear el Controlador MVC (conecta con MySQL o usa modo memoria)
        Controlador controlador = new Controlador();

        // 2. Cargar la ventana principal desde FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/clancode/vista/fx/fxml/main.fxml"));
        javafx.scene.Parent root = loader.load();

        // 3. Inyectar el Controlador MVC en MainController para que lo propague
        //    a las pestañas hijas (articulos, clientes, pedidos)
        MainController mainController = loader.getController();
        mainController.setControlador(controlador);

        // 4. Configurar y mostrar la ventana
        Scene scene = new Scene(root, 900, 650);
        primaryStage.setTitle("Online Store — Gestión");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
