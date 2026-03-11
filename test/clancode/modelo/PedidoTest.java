package clancode.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    private Articulo articulo;
    private ClienteEstandar cliEstandar;
    private ClientePremium cliPremium;

    @BeforeEach
    void setUp() {
        // Artículo: precio 100€, gastos envío 10€, preparación 60 minutos
        articulo = new Articulo("A001", "Teclado mecánico", 100.0, 10.0, 60);
        cliEstandar = new ClienteEstandar("Ana García", "Calle Mayor 1", "12345678A", "ana@test.com");
        cliPremium = new ClientePremium("Luis Pérez", "Av. Libertad 5", "87654321B", "luis@test.com");
    }

    // ==================== TESTS getPrecioTotal() ====================

    @Test
    @DisplayName("Precio total estándar: precio*cantidad + envío completo")
    void testPrecioTotalClienteEstandar() {
        Pedido pedido = new Pedido(cliEstandar, articulo, 2);
        // 2 * 100 + 10 * (1 - 0.0) = 200 + 10 = 210
        assertEquals(210.0, pedido.getPrecioTotal(), 0.01);
    }

    @Test
    @DisplayName("Precio total premium: 20% descuento en envío")
    void testPrecioTotalClientePremium() {
        Pedido pedido = new Pedido(cliPremium, articulo, 2);
        // 2 * 100 + 10 * (1 - 0.20) = 200 + 8 = 208
        assertEquals(208.0, pedido.getPrecioTotal(), 0.01);
    }

    @Test
    @DisplayName("Precio total 1 unidad estándar")
    void testPrecioTotalUnaUnidadEstandar() {
        Pedido pedido = new Pedido(cliEstandar, articulo, 1);
        // 1 * 100 + 10 = 110
        assertEquals(110.0, pedido.getPrecioTotal(), 0.01);
    }

    @Test
    @DisplayName("Precio total 1 unidad premium")
    void testPrecioTotalUnaUnidadPremium() {
        Pedido pedido = new Pedido(cliPremium, articulo, 1);
        // 1 * 100 + 10 * 0.8 = 108
        assertEquals(108.0, pedido.getPrecioTotal(), 0.01);
    }

    // ==================== TESTS esCancelable() ====================

    @Test
    @DisplayName("Pedido recién creado es cancelable")
    void testPedidoRecienCreadoEsCancelable() {
        Pedido pedido = new Pedido(cliEstandar, articulo, 1);
        assertTrue(pedido.esCancelable());
    }

    @Test
    @DisplayName("Pedido de hace 2 horas NO es cancelable (prep=60min)")
    void testPedidoAntiguoNoEsCancelable() {
        Pedido pedido = new Pedido(1, cliEstandar, articulo, 1,
                LocalDateTime.now().minusHours(2));
        assertFalse(pedido.esCancelable());
    }

    @Test
    @DisplayName("Pedido de hace 30 min SÍ es cancelable (prep=60min)")
    void testPedidoDentroDePlazoCancelable() {
        Pedido pedido = new Pedido(2, cliEstandar, articulo, 1,
                LocalDateTime.now().minusMinutes(30));
        assertTrue(pedido.esCancelable());
    }

    @Test
    @DisplayName("Pedido de hace 61 min NO es cancelable (prep=60min)")
    void testPedidoFueraDePlazoNoCancelable() {
        Pedido pedido = new Pedido(3, cliEstandar, articulo, 1,
                LocalDateTime.now().minusMinutes(61));
        assertFalse(pedido.esCancelable());
    }
}