package clancode;

/**
 * Punto de entrada legacy de la aplicación (Productos 1–4, versión consola).
 *
 * Para el Producto 5 (interfaz gráfica JavaFX), el punto de entrada es MainApp.
 * Esta clase se mantiene para no romper la compatibilidad con entregas anteriores;
 * simplemente redirige a MainApp.
 */
public class Main {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
