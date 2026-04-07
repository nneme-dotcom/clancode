package clancode.excepciones;

/**
 * Excepción lanzada cuando se intenta cancelar un pedido
 * que ya ha sido enviado o cuyo plazo de cancelación ha expirado.
 *
 * Un pedido es cancelable solo si está pendiente de envío
 * y aún no ha transcurrido su tiempo de preparación.
 *
 * Casos de uso:
 * - Intento de eliminar pedido con estado "Enviado"
 * - Intento de eliminar pedido cuyo tiempo de preparación ya expiró
 *
 * @author clancode
 * @version 1.0
 */
public class PedidoNoCancelableException extends Exception {

    public PedidoNoCancelableException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con causa encadenada.
     *
     * @param mensaje descripción del error
     * @param causa la excepción original
     */
    public PedidoNoCancelableException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}