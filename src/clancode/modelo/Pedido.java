package clancode.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pedido {
    private static int contadorPedidos = 1;

    private int numeroPedido;
    private Cliente cliente;
    private Articulo articulo;
    private int cantidad;
    private LocalDateTime fechaHora;

    // Constructor completo
    public Pedido(int numeroPedido, Cliente cliente, Articulo articulo,
                  int cantidad, LocalDateTime fechaHora) {
        this.numeroPedido = numeroPedido;
        this.cliente = cliente;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
    }

    // Constructor con auto-generación de número y fecha actual
    public Pedido(Cliente cliente, Articulo articulo, int cantidad) {
        this(contadorPedidos++, cliente, articulo, cantidad, LocalDateTime.now());
    }

    public int getNumeroPedido() { return numeroPedido; }
    public Cliente getCliente() { return cliente; }
    public Articulo getArticulo() { return articulo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }

    public double precioEnvio() {
        return articulo.getGastosEnvio() * (1 - cliente.descuentoEnvio());
    }

    public double getPrecioTotal() {
        return articulo.getPrecioVenta() * cantidad + precioEnvio();
    }

    public boolean esCancelable() {
        LocalDateTime limite = fechaHora.plusMinutes(articulo.getTiempoPreparacion());
        return LocalDateTime.now().isBefore(limite);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format(
                "Nº: %d | Cliente: %s | Artículo: %s | Cantidad: %d | Total: %.2f€ | Fecha: %s | Estado: %s",
                numeroPedido, cliente.getEmail(), articulo.getCodigo(),
                cantidad, getPrecioTotal(), fechaHora.format(fmt),
                esCancelable() ? "Pendiente de envío" : "Enviado");
    }

    public boolean pedidoEnviado() {
    // Un pedido está enviado si ya ha pasado su tiempo de preparación
    LocalDateTime limite = fechaHora.plusMinutes(articulo.getTiempoPreparacion());
    return LocalDateTime.now().isAfter(limite);
    }
}