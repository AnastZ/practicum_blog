-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema blog
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema blog
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `blog` DEFAULT CHARACTER SET utf8 ;
USE `blog` ;

-- -----------------------------------------------------
-- Table `blog`.`post`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blog`.`post` (
  `idpost` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `text` VARCHAR(2000) NOT NULL,
  `likes_count` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`idpost`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idpost_UNIQUE` ON `blog`.`post` (`idpost` ASC) VISIBLE;

CREATE UNIQUE INDEX `title_UNIQUE` ON `blog`.`post` (`title` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `blog`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blog`.`tag` (
  `idtag` INT NOT NULL AUTO_INCREMENT,
  `tag_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idtag`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idtag_UNIQUE` ON `blog`.`tag` (`idtag` ASC) VISIBLE;

CREATE UNIQUE INDEX `tag_name_UNIQUE` ON `blog`.`tag` (`tag_name` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `blog`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blog`.`comment` (
  `idcomment` INT NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(255) NOT NULL,
  `idpost` INT NOT NULL,
  PRIMARY KEY (`idcomment`),
  CONSTRAINT `comment_post`
    FOREIGN KEY (`idpost`)
    REFERENCES `blog`.`post` (`idpost`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idcomment_UNIQUE` ON `blog`.`comment` (`idcomment` ASC) VISIBLE;

CREATE INDEX `comment_post_idx` ON `blog`.`comment` (`idpost` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `blog`.`post_tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blog`.`post_tags` (
  `idpost_tags` INT NOT NULL AUTO_INCREMENT,
  `idpost` INT NOT NULL,
  `idtag` INT NOT NULL,
  PRIMARY KEY (`idpost_tags`),
  CONSTRAINT `posttag_post`
    FOREIGN KEY (`idpost`)
    REFERENCES `blog`.`post` (`idpost`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `posttag_tag`
    FOREIGN KEY (`idtag`)
    REFERENCES `blog`.`tag` (`idtag`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idpost_tags_UNIQUE` ON `blog`.`post_tags` (`idpost_tags` ASC) VISIBLE;

CREATE INDEX `posttag_post_idx` ON `blog`.`post_tags` (`idpost` ASC) VISIBLE;

CREATE INDEX `posttag_tag_idx` ON `blog`.`post_tags` (`idtag` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
