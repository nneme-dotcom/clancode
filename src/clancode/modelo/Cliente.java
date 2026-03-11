package clancode.modelo;

public abstract class Cliente {
    private String nombre;
    private String domicilio;
    private String nif;
    private String email; // Identificador único

    public Cliente(String nombre, String domicilio, String nif, String email) {
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.nif = nif;
        this.email = email;
    }

    public String getNombre() { return nombre; }
    public String getDomicilio() { return domicilio; }
    public String getNif() { return nif; }
    public String getEmail() { return email; }

    public abstract String tipoCliente();
    public abstract double descuentoEnvio();

    @Override
    public String toString() {
        return String.format(
                "Email: %s | Nombre: %s | NIF: %s | Domicilio: %s | Tipo: %s",
                email, nombre, nif, domicilio, tipoCliente());
    }
}