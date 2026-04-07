package clancode.excepciones;

/**
 * Excepción lanzada cuando se intenta registrar un cliente
 * con un email que ya existe en el sistema.
 *
 * Los emails son únicos en la tienda, por lo que no pueden
 * existir dos clientes con el mismo email.
 *
 * Casos de uso:
 * - Intento de añadir cliente con email duplicado
 *
 * @author clancode
 * @version 1.0
 */
public class ClienteYaExisteException extends Exception {

    public ClienteYaExisteException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con causa encadenada para debugging mejorado.
     *
     * @param mensaje descripción del error
     * @param causa la excepción original
     */
    public ClienteYaExisteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}