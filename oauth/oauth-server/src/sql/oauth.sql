-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema rasr_oauth
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `rasr_oauth` ;

-- -----------------------------------------------------
-- Schema rasr_oauth
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `rasr_oauth` DEFAULT CHARACTER SET utf8 ;
USE `rasr_oauth` ;

-- -----------------------------------------------------
-- Table `rasr_oauth`.`client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rasr_oauth`.`client` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `client_id` VARCHAR(45) NOT NULL,
  `client_secret` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rasr_oauth`.`tokens`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rasr_oauth`.`tokens` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(45) NOT NULL,
  `expires` DATETIME NOT NULL,
  `client_id` INT NOT NULL,
  `authorization_server` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_tokens_client_idx` (`client_id` ASC),
  CONSTRAINT `fk_tokens_client`
    FOREIGN KEY (`client_id`)
    REFERENCES `rasr_oauth`.`client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
