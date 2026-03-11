package clancode.excepciones;

public class PedidoNoCancelableException extends Exception {
    public PedidoNoCancelableException(String mensaje) {
        super(mensaje);
    }
}