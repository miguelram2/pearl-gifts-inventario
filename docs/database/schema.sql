-- Pearl Gifts - Sistema de Control de Inventario
-- Script de creacion de base de datos

CREATE DATABASE IF NOT EXISTS pearl_gifts_inventario
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pearl_gifts_inventario;

CREATE TABLE IF NOT EXISTS categorias (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  descripcion VARCHAR(255),
  activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(64) NOT NULL,
  nombre_completo VARCHAR(150) NOT NULL,
  rol ENUM('ADMIN', 'OPERATIVO') NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS productos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  categoria_id INT NOT NULL,
  precio DECIMAL(10,2) NOT NULL DEFAULT 0,
  cantidad INT NOT NULL DEFAULT 0,
  stock_minimo INT NOT NULL DEFAULT 5,
  proveedor VARCHAR(150),
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS movimientos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  producto_id INT NOT NULL,
  tipo ENUM('ENTRADA', 'SALIDA') NOT NULL,
  cantidad INT NOT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_id INT NOT NULL,
  nota VARCHAR(255),
  FOREIGN KEY (producto_id) REFERENCES productos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS auditoria (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  accion VARCHAR(255) NOT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Usuario administrador inicial (password: admin123)
-- El hash corresponde a SHA-256 de "admin123"
INSERT INTO usuarios (usuario, password_hash, nombre_completo, rol)
VALUES ('perla', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Perla Montiel', 'ADMIN')
ON DUPLICATE KEY UPDATE usuario = usuario;

-- Categorias iniciales de ejemplo
INSERT INTO categorias (nombre, descripcion) VALUES
  ('Cajas de regalo', 'Cajas armadas y personalizadas'),
  ('Decoracion', 'Globos, listones y accesorios decorativos'),
  ('Complementos', 'Peluches, chocolates y detalles adicionales')
ON DUPLICATE KEY UPDATE nombre = nombre;