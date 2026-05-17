package clancode;

/**
 * Clase de lanzamiento para evitar el error "JavaFX runtime components are missing"
 * cuando se ejecuta desde classpath (sin module-path nativo de IntelliJ).
 *
 * El error ocurre porque JavaFX detecta que la clase principal extiende Application
 * y verifica que esté en el module-path. Al separar el lanzamiento en esta clase,
 * se evita esa comprobación y la app arranca correctamente.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
