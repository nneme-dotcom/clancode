package clancode.excepciones;

public class ArticuloYaExisteException extends Exception {
    public ArticuloYaExisteException(String mensaje) {
        super(mensaje);
    }
}