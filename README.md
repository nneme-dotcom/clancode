# ClanCode — Online Store

Aplicación de escritorio en Java para la gestión de ventas de una tienda online.
Desarrollada como proyecto grupal para la asignatura FP.447 de la UOC.

## Requisitos previos

- Java 17 o superior
- MySQL 8.0 o superior
- IntelliJ IDEA

## Configuración del entorno

### 1. Conector MySQL (JDBC)
El conector MySQL no está incluido en el repositorio. Hay que descargarlo manualmente:

1. Ve a https://dev.mysql.com/downloads/connector/j/
2. Selecciona **Platform Independent** y descarga el ZIP
3. Descomprime y copia el archivo `mysql-connector-j-x.x.x.jar` en la carpeta `lib/` del proyecto
4. En IntelliJ: `File > Project Structure > Libraries > + > Java` y selecciona el JAR

### 2. Base de datos
1. Crea la base de datos en MySQL:
```sql
CREATE DATABASE clancode_shop;
```
2. Ejecuta el script de creación de tablas disponible en `BBDD/BBDD_clancode.sql`

### 3. Configuración de conexión
Edita el archivo `src/clancode/util/ConexionBD.java` y ajusta los parámetros:
```java
private static final String URL = "jdbc:mysql://localhost:3306/clancode_shop";
private static final String USER = "root";
private static final String PASSWORD = "tu_contraseña";
```

## Estructura del proyecto

    src/clancode/
    ├── Main.java
    ├── controlador/
    │   └── Controlador.java
    ├── modelo/
    │   ├── Articulo.java
    │   ├── Cliente.java
    │   ├── ClienteEstandar.java
    │   ├── ClientePremium.java
    │   ├── Pedido.java
    │   ├── Tienda.java
    │   ├── ListaGenerica.java
    │   └── dao/
    │       ├── DAO.java
    │       ├── DAOFactory.java
    │       ├── MySQLDAOFactory.java
    │       ├── ArticuloDAO_MySQL.java
    │       ├── ClienteDAO_MySQL.java
    │       └── PedidoDAO_MySQL.java
    ├── vista/
    │   └── Menu.java
    └── util/
        └── ConexionBD.java

## Ejecución
Ejecuta la clase `Main.java` para arrancar la aplicación.