-- ---------------------------------------------- --
--                 TABLE DROP                     --
-- ---------------------------------------------- --

-- PRICING --
-- DROP THE TABLES RELATED TO PRICING --
DROP TABLE IF EXISTS `kinton`.`pricing_template`;
DROP TABLE IF EXISTS `kinton`.`costCode`;
DROP TABLE IF EXISTS `kinton`.`pricingCostCode`;
DROP TABLE IF EXISTS `kinton`.`pricingTier`;
DROP TABLE IF EXISTS `kinton`.`currency`;
DROP TABLE IF EXISTS `kinton`.`costCodeCurrency`;


-- ---------------------------------------------- --
--                  TABLE CREATION                --
-- ---------------------------------------------- --

-- PRICING --
-- Definition of table `kinton`.`currency`
CREATE TABLE `kinton`.`currency` (
  `idCurrency` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `symbol` varchar(256) NOT NULL ,
  `name` varchar(256) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCurrency`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
    
-- PRICING --
-- Definition of table `kinton`.`costCode`
CREATE TABLE `kinton`.`costCode` (
  `idCostCode` int(10) NOT NULL AUTO_INCREMENT ,
  `name` varchar(256) NOT NULL ,
  `description` varchar(256) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCostCode`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- PRICING --
-- Definition of table `kinton`.`pricing`
CREATE TABLE `kinton`.`pricing_template` (
  `idPricingTemplate` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idCurrency` int(10) UNSIGNED NOT NULL ,
  `name` varchar(256) NOT NULL ,
  `chargingPeriod`  int(10) UNSIGNED NOT NULL ,
  `minimumCharge` int(10) UNSIGNED NOT NULL ,
  `showChangesBefore` boolean NOT NULL default 0,
  `showMinimumCharge` boolean NOT NULL default 0,
  `standingChargePeriod` DECIMAL(20) NOT NULL default 0,
  `minimumChargePeriod` DECIMAL(20) NOT NULL default 0,
  `vcpu` DECIMAL(20) NOT NULL default 0,
  `memoryMB` DECIMAL(20) NOT NULL default 0,
  `hdGB` DECIMAL(20) NOT NULL default 0,
  `vlan` DECIMAL(20) NOT NULL default 0,
  `publicIp` DECIMAL(20) NOT NULL default 0,
   `defaultTemplate` boolean NOT NULL default 0,
   `last_update` timestamp NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingTemplate`) ,
  KEY `Pricing_FK2_Currency` (`idCurrency`),
  CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency` ) REFERENCES `kinton`.`currency` (`idCurrency` ) ON DELETE NO ACTION
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- PRICING --
-- Definition of table `kinton`.`pricingCostCode`
CREATE TABLE `kinton`.`pricingCostCode` (
`idPricingCostCode` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idPricingTemplate` int(10) UNSIGNED NOT NULL,
  `idCostCode` int(10) UNSIGNED NOT NULL,
  `price` DECIMAL(20) NOT NULL default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingCostCode`)  
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;  
  
 --
-- Table `kinton`.`costCodeCurrency`
-- 
CREATE TABLE  `kinton`.`costCodeCurrency` (
  `idCostCodeCurrency` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idCostCode` int(10) unsigned,
  `idCurrency` int(10) unsigned,
  `price` DECIMAL(20) NOT NULL default 0,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`idCostCodeCurrency`)
  -- CONSTRAINT `idCostCode_FK` FOREIGN KEY (`idCostCode`) REFERENCES `costCode` (`idCostCode`),
  -- CONSTRAINT `idCurrency_FK` FOREIGN KEY (`idCurrency`) REFERENCES `currency` (`idCurrency`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- PRICING --
-- Definition of table `kinton`.`pricingTier`
CREATE TABLE `kinton`.`pricingTier` (
  `idPricingTemplate` int(10) UNSIGNED NOT NULL,
  `idTier` int(10) UNSIGNED NOT NULL,
  `price` int(10) UNSIGNED NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingTemplate`, `idTier`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;    

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

-- PRICING --
-- ADD THE COLUMN ID_PRICING TO ENTERPRISE --
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idPricingTemplate` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricing_template` (`idPricingTemplate`);


-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

-- PRICING --
-- Dumping data for table `kinton`.`privilege`

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (49,'PRICING_VIEW',0),
 (50,'PRICING_MANAGE_PRICING',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

-- PRICING --
-- Dumping data for table `kinton`.`currency`
LOCK TABLES `kinton`.`currency` WRITE;
INSERT INTO `kinton`.`currency` values (1, "USD", "Dollar - $", 0);
INSERT INTO `kinton`.`currency` values (2, "EUR", CONCAT("Euro - " ,0xE282AC), 0);
INSERT INTO `kinton`.`currency` values (3, "JPY", CONCAT("Yen - " , 0xc2a5), 0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;  

-- PRICING --
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES
 (1,49,0),(1,50,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;


/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.wiki.pricing.createCurrency","","Currency creation wiki");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;




-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --










  

  

