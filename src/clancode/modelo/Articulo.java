package clancode.modelo;

public class Articulo {
    private String codigo;
    private String descripcion;
    private double precioVenta;
    private double gastosEnvio;
    private int tiempoPreparacion; // en minutos

    public Articulo(String codigo, String descripcion,
                    double precioVenta, double gastosEnvio,
                    int tiempoPreparacion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.gastosEnvio = gastosEnvio;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioVenta() { return precioVenta; }
    public double getGastosEnvio() { return gastosEnvio; }
    public int getTiempoPreparacion() { return tiempoPreparacion; }

    @Override
    public String toString() {
        return String.format(
                "Código: %s | Descripción: %s | Precio: %.2f€ | Envío: %.2f€ | Preparación: %d min",
                codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacion);
    }
}