package clancode.modelo;

public class ClienteEstandar extends Cliente {

    public ClienteEstandar(String nombre, String domicilio, String nif, String email) {
        super(nombre, domicilio, nif, email);
    }

    @Override
    public String tipoCliente() { return "Estándar"; }

    @Override
    public double descuentoEnvio() { return 0.0; }
}