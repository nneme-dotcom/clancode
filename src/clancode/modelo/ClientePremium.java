package clancode.modelo;

public class ClientePremium extends Cliente {
    private static final double CUOTA_ANUAL = 30.0;
    private static final double DESCUENTO_ENVIO = 0.20; // 20%

    public ClientePremium(String nombre, String domicilio, String nif, String email) {
        super(nombre, domicilio, nif, email);
    }

    @Override
    public String tipoCliente() { return "Premium"; }

    @Override
    public double descuentoEnvio() { return DESCUENTO_ENVIO; }

    public double getCuotaAnual() { return CUOTA_ANUAL; }

    @Override
    public String toString() {
        return super.toString() + String.format(
                " | Cuota anual: %.2f€ | Descuento envío: %d%%",
                CUOTA_ANUAL, (int)(DESCUENTO_ENVIO * 100));
    }
}