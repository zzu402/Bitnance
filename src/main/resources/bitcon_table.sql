

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `bitcon_account`;
CREATE TABLE `bitcon_account` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `makerCommission` varchar(32) DEFAULT NULL,
  `buyerCommission` varchar(32) DEFAULT NULL,
  `canWithdraw` varchar(6) DEFAULT NULL,
  `sellerCommission` varchar(32) DEFAULT NULL,
  `updateTime` bigint(20) DEFAULT NULL,
  `canDeposit` varchar(6) DEFAULT NULL,
  `takerCommission` varchar(255) DEFAULT NULL,
  `canTrade` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bitcon_balance`;
CREATE TABLE `bitcon_balance` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `accountId` bigint(32) DEFAULT NULL,
  `asset` varchar(20) DEFAULT NULL,
  `free` varchar(32) DEFAULT NULL,
  `locked` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bitcon_my_trade`;
CREATE TABLE `bitcon_my_trade` (
  `id` varchar(50) NOT NULL,
  `price` varchar(32) DEFAULT NULL,
  `qty` varchar(32) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `isBestMatch` varchar(6) DEFAULT NULL,
  `orderId` varchar(255) DEFAULT NULL,
  `commission` varchar(128) DEFAULT NULL,
  `commissionAsset` varchar(128) DEFAULT NULL,
  `isMaker` varchar(6) DEFAULT NULL,
  `isBuyer` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderId` (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bitcon_price`;
CREATE TABLE `bitcon_price` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(20) DEFAULT NULL,
  `price` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bitcon_trade`;
CREATE TABLE `bitcon_trade` (
  `id` varchar(50) NOT NULL,
  `price` varchar(32) DEFAULT NULL,
  `qty` varchar(32) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `isBestMatch` varchar(6) DEFAULT NULL,
  `isBuyerMaker` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bitcon_user`;
CREATE TABLE `bitcon_user` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `secret_key` varchar(255) DEFAULT NULL,
  `api_key` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bitcon_order`;
CREATE TABLE `bitcon_order` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(255) DEFAULT NULL,
  `orderId` varchar(255) DEFAULT NULL,
  `clientOrderId` varchar(255) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `origQty` varchar(255) DEFAULT NULL,
  `executedQty` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `timeInForce` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `side` varchar(255) DEFAULT NULL,
  `stopPrice` varchar(255) DEFAULT NULL,
  `icebergQty` varchar(255) DEFAULT NULL,
  `isWorking` varchar(255) DEFAULT NULL,
  `time` bigint(32) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderId` (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bitcon_config`;
CREATE TABLE `bitcon_config` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(255) DEFAULT NULL,
  `type` VARCHAR(100) NOT NULL,
  `configInfo` VARCHAR(1000) DEFAULT NULL ,
  `description` VARCHAR(1000) DEFAULT NULL,
  `status` INT DEFAULT 0,
  `updateTime` BIGINT DEFAULT NULL,
  `createTime` BIGINT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `symbol_type` (`symbol`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE bitcon_price
  ADD COLUMN `createTime` BIGINT(32) DEFAULT NULL ;
ALTER TABLE bitcon_price
  ADD COLUMN `pointType` INT DEFAULT 0 ;
  ALTER TABLE bitcon_price
  ADD COLUMN `pointSellType` INT DEFAULT 0 ;

ALTER TABLE bitcon_user
  ADD COLUMN `name` varchar(255) DEFAULT NULL ;
ALTER TABLE bitcon_user
  ADD COLUMN `email` varchar(255) DEFAULT NULL ;
ALTER TABLE bitcon_user
  ADD COLUMN `sellTemplet` varchar(255) DEFAULT NULL ;
ALTER TABLE bitcon_user
  ADD COLUMN `buyTemplet` varchar(255) DEFAULT NULL ;
ALTER TABLE bitcon_account
    ADD COLUMN `moneyCount` BIGINT(32)DEFAULT 0;