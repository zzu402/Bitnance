/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50637
Source Host           : localhost:3306
Source Database       : bitcon

Target Server Type    : MYSQL
Target Server Version : 50637
File Encoding         : 65001

Date: 2018-03-08 10:08:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bitcon_account
-- ----------------------------
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

-- ----------------------------
-- Table structure for bitcon_balance
-- ----------------------------
DROP TABLE IF EXISTS `bitcon_balance`;
CREATE TABLE `bitcon_balance` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `accountId` bigint(32) DEFAULT NULL,
  `asset` varchar(20) DEFAULT NULL,
  `free` varchar(32) DEFAULT NULL,
  `locked` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bitcon_my_trade
-- ----------------------------
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bitcon_price
-- ----------------------------
DROP TABLE IF EXISTS `bitcon_price`;
CREATE TABLE `bitcon_price` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(20) DEFAULT NULL,
  `price` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bitcon_trade
-- ----------------------------
DROP TABLE IF EXISTS `bitcon_trade`;
CREATE TABLE `bitcon_trade` (
  `id` varchar(50) NOT NULL,
  `price` varchar(32) DEFAULT NULL,
  `qty` varchar(32) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `isBestMatch` varchar(6) DEFAULT NULL,
  `isBuyerMaker` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bitcon_user
-- ----------------------------
DROP TABLE IF EXISTS `bitcon_user`;
CREATE TABLE `bitcon_user` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `secret_key` varchar(255) DEFAULT NULL,
  `api_key` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
