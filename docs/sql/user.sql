/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.2.4-MariaDB : Database - open_platform_user
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `bsd_consignee_address` */

DROP TABLE IF EXISTS `bsd_consignee_address`;

CREATE TABLE `bsd_consignee_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id(用户表主键id)',
  `consignee_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '收件人',
  `country_code` varchar(20) CHARACTER SET utf8 NOT NULL COMMENT '手机地区编号',
  `mobile` varchar(20) CHARACTER SET utf8 NOT NULL COMMENT '手机号',
  `postal_code` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '邮政编码',
  `country` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '国家',
  `province` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '省份',
  `city` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '城市',
  `detail_address` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为默认地址:0-否 1-是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `address_user_id_index` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收货地址';

/*Table structure for table `bsd_user` */

DROP TABLE IF EXISTS `bsd_user`;

CREATE TABLE `bsd_user` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_code` varchar(255) NOT NULL COMMENT '用户编码',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '昵称',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '头像',
  `sex` tinyint(1) NOT NULL DEFAULT 0 COMMENT '性别 0 保密  1男 2女',
  `email` varchar(100) NOT NULL DEFAULT '' COMMENT '邮箱',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `user_type` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '用户类型:0-普通 1-潜在客户 2-客户',
  `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业ID',
  `register_ip` varchar(100) NOT NULL DEFAULT '' COMMENT '注册IP',
  `register_time` datetime NOT NULL COMMENT '注册时间',
  `source` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '用户来源:0-跨境知道 1-卖家成长 3-人工录入',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用 2-锁定',
  `user_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `invited_code` varchar(10) NOT NULL DEFAULT '' COMMENT '邀请人编码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '登陆密码同步标识:0-未同步 1-已同步',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE,
  UNIQUE KEY `user_code` (`user_code`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户-基础信息';

/*Table structure for table `bsd_user_account` */

DROP TABLE IF EXISTS `bsd_user_account`;

CREATE TABLE `bsd_user_account` (
  `account_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `platform` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '账户类型:1-微信移动应用 、2-微信网站应用、3-微信公众号、4-微信小程序、5-QQ、6-微博',
  `user_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '用户Id',
  `openid` varchar(255) NOT NULL DEFAULT '' COMMENT '第三方应用的唯一标识',
  `unionid` varchar(50) NOT NULL DEFAULT '',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '头像',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '昵称',
  `gender` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '性别 0：未知、1：男、2：女',
  `language` varchar(50) NOT NULL DEFAULT '' COMMENT '语言',
  `city` varchar(80) NOT NULL DEFAULT '' COMMENT '城市',
  `province` varchar(80) NOT NULL DEFAULT '' COMMENT '省',
  `country` varchar(80) NOT NULL DEFAULT '' COMMENT '国家',
  `country_code` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号码国家编码',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`account_id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `openid` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户-第三方账号';

/*Table structure for table `bsd_user_login_logs` */

DROP TABLE IF EXISTS `bsd_user_login_logs`;

CREATE TABLE `bsd_user_login_logs` (
  `log_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户Id',
  `account_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '账号ID',
  `login_ip` varchar(255) NOT NULL COMMENT '登录Ip',
  `login_time` datetime NOT NULL,
  `login_agent` varchar(500) NOT NULL COMMENT '登录设备',
  `login_nums` int(11) unsigned NOT NULL COMMENT '登录次数',
  `login_account` varchar(100) NOT NULL COMMENT '标识：手机号、邮箱、 用户名、或第三方应用的唯一标识',
  `login_type` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '登录类型:0-密码、1-验证码、2-第三方账号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`log_id`),
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户-登录日志';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
