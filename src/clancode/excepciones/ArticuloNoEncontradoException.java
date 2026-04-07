package clancode.excepciones;

/**
 * Excepción lanzada cuando se intenta buscar un artículo
 * que no existe en el inventario de la tienda.
 *
 * Casos de uso:
 * - Búsqueda de artículo por código inexistente
 * - Creación de pedido con código de artículo inválido
 * - Actualización de artículo que no existe
 *
 * @author clancode
 * @version 1.0
 */
public class ArticuloNoEncontradoException extends Exception {

    public ArticuloNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con causa encadenada.
     * Permite encadenar excepciones para debugging más detallado.
     * Útil cuando en el futuro trabajemos con base de datos.
     *
     * @param mensaje descripción del error
     * @param causa la excepción original que causó este error
     */
    public ArticuloNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}