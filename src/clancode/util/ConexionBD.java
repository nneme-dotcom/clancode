package clancode.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad de conexión a la base de datos MySQL.
 *
 * Centraliza los parámetros de conexión (URL, usuario, contraseña) para que
 * cambiar la configuración solo requiera modificar esta clase.
 *
 * Método principal de uso:
 *   {@link #conectar()} — carga el driver explícitamente y devuelve una conexión.
 *   Usado por todos los DAOs del proyecto.
 */
public class ConexionBD {

    private static final String URL      = "jdbc:mysql://localhost:3306/clancode_shop";
    private static final String USER     = "root";
    private static final String PASSWORD = "1234";

    // Constructor privado: clase de utilidades, no se instancia
    private ConexionBD() {}

    /**
     * Abre y devuelve una conexión a MySQL cargando el driver JDBC explícitamente.
     * Recomendado para versiones de JDBC anteriores a 4.0; en versiones modernas
     * el driver se carga automáticamente, pero mantenerlo no causa problemas.
     *
     * @return una nueva conexión activa
     * @throws SQLException si el driver no se encuentra o la conexión falla
     */
    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver MySQL JDBC.", e);
        }
    }
}
