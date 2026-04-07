package clancode;

import clancode.vista.Menu;

/**
 * Clase principal que arranca la aplicación Online Store.
 * Sigue el patrón MVC, iniciando la Vista que a su vez instancia al Controlador.
 */
public class Main {

    public static void main(String[] args) {
        try {
            // 1. Creamos la instancia de la Vista (Menu)
            Menu menu = new Menu();
            
            // 2. Iniciamos el bucle del programa
            System.out.println("Cargando sistema de gestión clancode...");
            menu.iniciar();
            
        } catch (Exception e) {
            System.err.println("Error crítico al arrancar la aplicación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Aplicación finalizada. ¡Gracias por usar clancode!");
        }
    }
}