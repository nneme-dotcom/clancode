-- ================================================
-- Script de creación de BD para ClanCode Online Store
-- FP.447 - UOC
-- ================================================

CREATE DATABASE IF NOT EXISTS clancode_shop;
USE clancode_shop;

-- ================================================
-- TABLAS
-- ================================================

CREATE TABLE IF NOT EXISTS `articulos` (
                                           `codigo` varchar(50) NOT NULL,
    `descripcion` varchar(255) NOT NULL,
    `precio_venta` decimal(10,2) NOT NULL,
    `gastos_envio` decimal(10,2) NOT NULL,
    `tiempo_preparacion_min` int NOT NULL,
    PRIMARY KEY (`codigo`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `clientes` (
                                          `email` varchar(100) NOT NULL,
    `nombre` varchar(100) NOT NULL,
    `domicilio` varchar(255) DEFAULT NULL,
    `nif` varchar(20) NOT NULL,
    `tipo_cliente` enum('Estandar','Premium') NOT NULL,
    `cuota_anual` decimal(10,2) DEFAULT '0.00',
    `descuento_envio` decimal(5,2) DEFAULT '0.00',
    PRIMARY KEY (`email`),
    UNIQUE KEY `nif` (`nif`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `pedidos` (
                                         `numero_pedido` int NOT NULL AUTO_INCREMENT,
                                         `cliente_email` varchar(100) NOT NULL,
    `articulo_codigo` varchar(50) NOT NULL,
    `cantidad` int NOT NULL,
    `fecha_hora` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`numero_pedido`),
    CONSTRAINT `fk_pedido_cliente` FOREIGN KEY (`cliente_email`)
    REFERENCES `clientes` (`email`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_pedido_articulo` FOREIGN KEY (`articulo_codigo`)
    REFERENCES `articulos` (`codigo`) ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- PROCEDIMIENTOS ALMACENADOS
-- ================================================

DROP PROCEDURE IF EXISTS eliminarPedido;

DELIMITER //
CREATE PROCEDURE eliminarPedido(IN p_numero_pedido INT)
BEGIN
DELETE FROM pedidos WHERE numero_pedido = p_numero_pedido;
END //
DELIMITER ;