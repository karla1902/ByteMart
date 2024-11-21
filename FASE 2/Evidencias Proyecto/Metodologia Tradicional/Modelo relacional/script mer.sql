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
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username` (`username` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`carrito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`carrito` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `carrito_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`categoria` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


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
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `direccion_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`estado_orden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`estado_orden` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`orden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`orden` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `estado_id` INT NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `estado_id` (`estado_id` ASC) VISIBLE,
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `orden_ibfk_1`
    FOREIGN KEY (`estado_id`)
    REFERENCES `proyecto`.`estado_orden` (`id`),
  CONSTRAINT `orden_ibfk_2`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`factura`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`factura` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `orden_id` INT NOT NULL,
  `monto` INT NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `orden_id` (`orden_id` ASC) VISIBLE,
  CONSTRAINT `factura_ibfk_1`
    FOREIGN KEY (`orden_id`)
    REFERENCES `proyecto`.`orden` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`marca`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`marca` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`producto` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(155) NOT NULL,
  `price` INT NOT NULL,
  `marca_id` INT NOT NULL,
  `descripcion` VARCHAR(1000) NOT NULL,
  `stock` INT NOT NULL,
  `category_id` INT NOT NULL,
  `en_oferta` TINYINT(1) NULL DEFAULT NULL,
  `destacado` TINYINT(1) NULL DEFAULT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE,
  INDEX `category_id` (`category_id` ASC) VISIBLE,
  INDEX `marca_id` (`marca_id` ASC) VISIBLE,
  CONSTRAINT `producto_ibfk_1`
    FOREIGN KEY (`category_id`)
    REFERENCES `proyecto`.`categoria` (`id`),
  CONSTRAINT `producto_ibfk_2`
    FOREIGN KEY (`marca_id`)
    REFERENCES `proyecto`.`marca` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`imagen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`imagen` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `image_url` VARCHAR(255) NOT NULL,
  `producto_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `producto_id` (`producto_id` ASC) VISIBLE,
  CONSTRAINT `imagen_ibfk_1`
    FOREIGN KEY (`producto_id`)
    REFERENCES `proyecto`.`producto` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`item_carrito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`item_carrito` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `carrito_id` INT NOT NULL,
  `producto_id` INT NOT NULL,
  `cantidad` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `carrito_id` (`carrito_id` ASC) VISIBLE,
  INDEX `producto_id` (`producto_id` ASC) VISIBLE,
  CONSTRAINT `item_carrito_ibfk_1`
    FOREIGN KEY (`carrito_id`)
    REFERENCES `proyecto`.`carrito` (`id`),
  CONSTRAINT `item_carrito_ibfk_2`
    FOREIGN KEY (`producto_id`)
    REFERENCES `proyecto`.`producto` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


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
  INDEX `orden_id` (`orden_id` ASC) VISIBLE,
  INDEX `producto_id` (`producto_id` ASC) VISIBLE,
  CONSTRAINT `orden_item_ibfk_1`
    FOREIGN KEY (`orden_id`)
    REFERENCES `proyecto`.`orden` (`id`),
  CONSTRAINT `orden_item_ibfk_2`
    FOREIGN KEY (`producto_id`)
    REFERENCES `proyecto`.`producto` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


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
  UNIQUE INDEX `unique_tarjeta_usuario` (`numero_tarjeta` ASC, `usuario_id` ASC) VISIBLE,
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `tarjetas_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `proyecto`.`usuario` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`proceso_pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`proceso_pago` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `tarjeta_id` INT NOT NULL,
  `fecha_pago` DATETIME NOT NULL,
  `monto` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `tarjeta_id` (`tarjeta_id` ASC) VISIBLE,
  CONSTRAINT `proceso_pago_ibfk_1`
    FOREIGN KEY (`tarjeta_id`)
    REFERENCES `proyecto`.`tarjetas` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`rol` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `proyecto`.`usuario_rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto`.`usuario_rol` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `rol_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `rol_id` (`rol_id` ASC) VISIBLE,
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
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


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
