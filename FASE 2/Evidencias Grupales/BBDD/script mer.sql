-- MySQL Script generated by MySQL Workbench
-- Mon Oct 21 01:04:01 2024
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema proyecto
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `proyecto` ;

-- -----------------------------------------------------
-- Schema proyecto
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `proyecto` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `proyecto` ;

-- -----------------------------------------------------
-- Table `proyecto`.`alembic_version`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`alembic_version` (
  `version_num` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`version_num`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`usuario` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `apellido` VARCHAR(100) NOT NULL,
  `email` VARCHAR(120) NOT NULL,
  `direccion` VARCHAR(100) NULL DEFAULT NULL,
  `reset_code` VARCHAR(6) NULL DEFAULT NULL,
  `reset_code_expiration` DATETIME NULL DEFAULT NULL,
  `is_admin` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `username` ON `proyecto`.`usuario` (`username` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`carrito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`carrito` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `carrito_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `usuario_id` ON `proyecto`.`carrito` (`usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`categoria` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `name` ON `proyecto`.`categoria` (`name` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`direccion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`direccion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `numero_domicilio` VARCHAR(10) NOT NULL,
  `comuna_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `direccion_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `usuario_id` ON `proyecto`.`direccion` (`usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`estado_orden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`estado_orden` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `nombre` ON `proyecto`.`estado_orden` (`nombre` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`orden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`orden` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `estado_id` INT NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `orden_ibfk_1`
    FOREIGN KEY (`estado_id`)
    REFERENCES `proyecto`.`estado_orden` (`id`),
  CONSTRAINT `orden_ibfk_2`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `estado_id` ON `proyecto`.`orden` (`estado_id` ASC) VISIBLE;

CREATE INDEX `usuario_id` ON `proyecto`.`orden` (`usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`factura`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`factura` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `orden_id` INT NOT NULL,
  `monto` FLOAT NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `factura_ibfk_1`
    FOREIGN KEY (`orden_id`)
    REFERENCES `proyecto`.`orden` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `orden_id` ON `proyecto`.`factura` (`orden_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`producto` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(155) NOT NULL,
  `price` INT NOT NULL,
  `marca` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(1000) NOT NULL,
  `stock` INT NOT NULL,
  `category_id` INT NOT NULL,
  `en_oferta` TINYINT(1) NULL DEFAULT NULL,
  `destacado` TINYINT(1) NULL DEFAULT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `producto_ibfk_1`
    FOREIGN KEY (`category_id`)
    REFERENCES `proyecto`.`categoria` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 22
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `name` ON `proyecto`.`producto` (`name` ASC) VISIBLE;

CREATE INDEX `category_id` ON `proyecto`.`producto` (`category_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`imagen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`imagen` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `image_url` VARCHAR(255) NOT NULL,
  `producto_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `imagen_ibfk_1`
    FOREIGN KEY (`producto_id`)
    REFERENCES `proyecto`.`producto` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `producto_id` ON `proyecto`.`imagen` (`producto_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`item_carrito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`item_carrito` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `carrito_id` INT NOT NULL,
  `producto_id` INT NOT NULL,
  `cantidad` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `item_carrito_ibfk_1`
    FOREIGN KEY (`carrito_id`)
    REFERENCES `proyecto`.`carrito` (`id`),
  CONSTRAINT `item_carrito_ibfk_2`
    FOREIGN KEY (`producto_id`)
    REFERENCES `proyecto`.`producto` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `carrito_id` ON `proyecto`.`item_carrito` (`carrito_id` ASC) VISIBLE;

CREATE INDEX `producto_id` ON `proyecto`.`item_carrito` (`producto_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`orden_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`orden_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `orden_id` INT NOT NULL,
  `producto_id` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `orden_item_ibfk_1`
    FOREIGN KEY (`orden_id`)
    REFERENCES `proyecto`.`orden` (`id`),
  CONSTRAINT `orden_item_ibfk_2`
    FOREIGN KEY (`producto_id`)
    REFERENCES `proyecto`.`producto` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `orden_id` ON `proyecto`.`orden_item` (`orden_id` ASC) VISIBLE;

CREATE INDEX `producto_id` ON `proyecto`.`orden_item` (`producto_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`tarjetas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`tarjetas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `numero_tarjeta` VARCHAR(16) NOT NULL,
  `mes_vencimiento` INT NOT NULL,
  `anio_vencimiento` INT NOT NULL,
  `codigo_verificacion` VARCHAR(3) NOT NULL,
  `saldo` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `tarjetas_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `unique_tarjeta_usuario` ON `proyecto`.`tarjetas` (`numero_tarjeta` ASC, `usuario_id` ASC) VISIBLE;

CREATE INDEX `usuario_id` ON `proyecto`.`tarjetas` (`usuario_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`proceso_pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`proceso_pago` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `tarjeta_id` INT NOT NULL,
  `fecha_pago` DATETIME NOT NULL,
  `monto` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `proceso_pago_ibfk_1`
    FOREIGN KEY (`tarjeta_id`)
    REFERENCES `proyecto`.`tarjetas` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `tarjeta_id` ON `proyecto`.`proceso_pago` (`tarjeta_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`rol` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `nombre` ON `proyecto`.`rol` (`nombre` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `proyecto`.`usuario_rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`usuario_rol` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `rol_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `usuario_rol_ibfk_1`
    FOREIGN KEY (`rol_id`)
    REFERENCES `proyecto`.`rol` (`id`),
  CONSTRAINT `usuario_rol_ibfk_2`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `rol_id` ON `proyecto`.`usuario_rol` (`rol_id` ASC) VISIBLE;

CREATE INDEX `usuario_id` ON `proyecto`.`usuario_rol` (`usuario_id` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;