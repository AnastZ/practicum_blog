-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema blog_db
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `blog_db` ;

-- -----------------------------------------------------
-- Schema blog_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `blog_db` DEFAULT CHARACTER SET utf8mb3 ;
USE `blog_db` ;

-- -----------------------------------------------------
-- Table `blog_db`.`post`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `blog_db`.`post` ;

CREATE TABLE IF NOT EXISTS `blog_db`.`post` (
  `idpost` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `text` VARCHAR(2000) NOT NULL,
  `likes_count` BIGINT NOT NULL,
  `created_date` DATE NOT NULL,
  `image_path` VARCHAR(500) NULL DEFAULT NULL,
  PRIMARY KEY (`idpost`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `idpost_UNIQUE` ON `blog_db`.`post` (`idpost` ASC) VISIBLE;

CREATE UNIQUE INDEX `title_UNIQUE` ON `blog_db`.`post` (`title` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `blog_db`.`comment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `blog_db`.`comment` ;

CREATE TABLE IF NOT EXISTS `blog_db`.`comment` (
  `idcomment` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(255) NOT NULL,
  `idpost` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`idcomment`),
  CONSTRAINT `commit_post`
    FOREIGN KEY (`idpost`)
    REFERENCES `blog_db`.`post` (`idpost`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `idcomment_UNIQUE` ON `blog_db`.`comment` (`idcomment` ASC) VISIBLE;

CREATE INDEX `comment_post_idx` ON `blog_db`.`comment` (`idpost` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `blog_db`.`tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `blog_db`.`tag` ;

CREATE TABLE IF NOT EXISTS `blog_db`.`tag` (
  `idtag` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `tag_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`idtag`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `idtag_UNIQUE` ON `blog_db`.`tag` (`idtag` ASC) VISIBLE;

CREATE UNIQUE INDEX `tag_name_UNIQUE` ON `blog_db`.`tag` (`tag_name` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `blog_db`.`post_tags`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `blog_db`.`post_tags` ;

CREATE TABLE IF NOT EXISTS `blog_db`.`post_tags` (
  `idpost_tags` INT NOT NULL AUTO_INCREMENT,
  `idpost` BIGINT UNSIGNED NOT NULL,
  `idtag` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`idpost_tags`),
  CONSTRAINT `posttag_post`
    FOREIGN KEY (`idpost`)
    REFERENCES `blog_db`.`post` (`idpost`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `posttag_tag`
    FOREIGN KEY (`idtag`)
    REFERENCES `blog_db`.`tag` (`idtag`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb3;

CREATE UNIQUE INDEX `idpost_tags_UNIQUE` ON `blog_db`.`post_tags` (`idpost_tags` ASC) VISIBLE;

CREATE INDEX `posttag_tag_idx` ON `blog_db`.`post_tags` (`idtag` ASC) VISIBLE;

CREATE INDEX `posttag_post_idx` ON `blog_db`.`post_tags` (`idpost` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
