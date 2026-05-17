package clancode;

/**
 * @deprecated Clase de diagnóstico usada durante el desarrollo para verificar
 * la conexión a MySQL manualmente.
 *
 * En producción la verificación de conexión la realiza el {@link clancode.controlador.Controlador}
 * en su constructor, con fallback automático a modo memoria si falla.
 *
 * Se conserva en el repositorio para mantener el historial de Git intacto.
 */
@Deprecated
public class Testconexion {
    // Ver: clancode.controlador.Controlador (verifica la conexión al arrancar)
    // Ver: clancode.util.ConexionBD#conectar()
}
