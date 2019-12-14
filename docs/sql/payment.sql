/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.2.4-MariaDB : Database - open_platform_payment
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `bsd_goods_order` */

DROP TABLE IF EXISTS `bsd_goods_order`;

CREATE TABLE `bsd_goods_order` (
  `goods_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商品订单ID',
  `goods_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商品ID',
  `goods_name` varchar(64) NOT NULL DEFAULT '' COMMENT '商品名称',
  `amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '金额(单位分)',
  `user_id` varchar(30) NOT NULL DEFAULT '' COMMENT '用户ID',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '订单状态,订单生成(0),支付成功(1),处理完成(2),处理失败(-1)',
  `pay_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '支付订单号',
  `channel_code` varchar(24) NOT NULL DEFAULT '' COMMENT '渠道编码',
  `channel_user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '支付渠道用户ID(微信openID或支付宝账号等第三方支付账号)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`goods_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品订单表';

/*Table structure for table `bsd_iap_receipt` */

DROP TABLE IF EXISTS `bsd_iap_receipt`;

CREATE TABLE `bsd_iap_receipt` (
  `pay_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '支付订单号',
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `transaction_id` varchar(24) NOT NULL DEFAULT '' COMMENT 'IAP业务号',
  `receipt_data` text NOT NULL COMMENT '凭据内容',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '处理状态:0-未处理,1-处理成功,-1-处理失败',
  `handle_count` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '处理次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`pay_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='苹果支付凭据表';

/*Table structure for table `bsd_mch_info` */

DROP TABLE IF EXISTS `bsd_mch_info`;

CREATE TABLE `bsd_mch_info` (
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `name` varchar(30) NOT NULL DEFAULT '' COMMENT '名称',
  `type` varchar(24) NOT NULL DEFAULT '' COMMENT '类型',
  `req_key` varchar(512) NOT NULL DEFAULT '' COMMENT '请求私钥',
  `res_key` varchar(512) NOT NULL DEFAULT '' COMMENT '响应私钥',
  `state` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '商户状态,0-停止使用,1-使用中',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`mch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户信息表';

/*Table structure for table `bsd_mch_notify` */

DROP TABLE IF EXISTS `bsd_mch_notify`;

CREATE TABLE `bsd_mch_notify` (
  `order_id` varchar(24) NOT NULL DEFAULT '' COMMENT '订单ID',
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `mch_order_no` varchar(30) NOT NULL DEFAULT '' COMMENT '商户订单号',
  `order_type` varchar(8) NOT NULL DEFAULT '' COMMENT '订单类型:1-支付,2-转账,3-退款',
  `notify_url` varchar(2048) NOT NULL DEFAULT '' COMMENT '通知地址',
  `notify_count` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '通知次数',
  `result` varchar(2048) NOT NULL DEFAULT '' COMMENT '通知响应结果',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '通知状态,1-通知中,2-通知成功,3-通知失败',
  `last_notify_time` datetime DEFAULT NULL COMMENT '最后一次通知时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `mch_id` (`mch_id`,`order_type`,`mch_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户通知表';

/*Table structure for table `bsd_pay_channel` */

DROP TABLE IF EXISTS `bsd_pay_channel`;

CREATE TABLE `bsd_pay_channel` (
  `channel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
  `channel_code` varchar(24) NOT NULL DEFAULT '' COMMENT '渠道编码',
  `channel_name` varchar(30) NOT NULL DEFAULT '' COMMENT '渠道名称,如:alipay,wechat',
  `channel_mch_id` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道商户ID',
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `state` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '渠道状态,0-停止使用,1-使用中',
  `param` varchar(6148) NOT NULL DEFAULT '' COMMENT '配置参数,json字符串',
  `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`channel_id`),
  UNIQUE KEY `mch_id` (`mch_id`,`channel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付渠道表';

/*Table structure for table `bsd_pay_order` */

DROP TABLE IF EXISTS `bsd_pay_order`;

CREATE TABLE `bsd_pay_order` (
  `pay_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '支付订单号',
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `mch_order_no` varchar(30) NOT NULL DEFAULT '' COMMENT '商户订单号',
  `channel_code` varchar(24) NOT NULL DEFAULT '' COMMENT '渠道编码',
  `amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额(单位分)',
  `currency` varchar(3) NOT NULL DEFAULT 'cny' COMMENT '三位货币代码,人民币:cny',
  `status` tinyint(3) NOT NULL DEFAULT 0 COMMENT '支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成',
  `client_ip` varchar(32) NOT NULL DEFAULT '' COMMENT '客户端IP',
  `device` varchar(64) NOT NULL DEFAULT '' COMMENT '设备',
  `subject` varchar(64) NOT NULL DEFAULT '' COMMENT '商品标题',
  `body` varchar(256) NOT NULL DEFAULT '' COMMENT '商品描述信息',
  `extra` varchar(6148) NOT NULL DEFAULT '' COMMENT '特定渠道发起时额外参数',
  `channel_mch_id` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道商户ID',
  `channel_order_no` varchar(64) NOT NULL DEFAULT '' COMMENT '渠道订单号',
  `err_code` varchar(64) NOT NULL DEFAULT '' COMMENT '渠道支付错误码',
  `err_msg` varchar(128) NOT NULL DEFAULT '' COMMENT '渠道支付错误描述',
  `param1` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展参数1',
  `param2` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展参数2',
  `notify_url` varchar(128) NOT NULL DEFAULT '' COMMENT '通知地址',
  `notify_count` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '通知次数',
  `last_notify_time` datetime DEFAULT NULL COMMENT '最后一次通知时间',
  `expire_time` datetime DEFAULT NULL COMMENT '订单失效时间',
  `pay_succ_time` datetime DEFAULT NULL COMMENT '订单支付成功时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`pay_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';

/*Table structure for table `bsd_refund_order` */

DROP TABLE IF EXISTS `bsd_refund_order`;

CREATE TABLE `bsd_refund_order` (
  `refund_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '退款订单号',
  `pay_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '支付订单号',
  `channel_pay_order_no` varchar(64) NOT NULL DEFAULT '' COMMENT '渠道支付单号',
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `mch_refund_no` varchar(30) NOT NULL DEFAULT '' COMMENT '商户退款单号',
  `channel_code` varchar(24) NOT NULL DEFAULT '' COMMENT '渠道编码',
  `pay_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额(单位分)',
  `refund_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '退款金额(单位分)',
  `currency` varchar(3) NOT NULL DEFAULT 'cny' COMMENT '三位货币代码,人民币:cny',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-业务处理完成',
  `result` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '退款结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败',
  `client_ip` varchar(32) DEFAULT '' COMMENT '客户端IP',
  `device` varchar(64) DEFAULT '' COMMENT '设备',
  `remark_info` varchar(256) DEFAULT '' COMMENT '备注',
  `channel_user` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道用户标识,如微信openId,支付宝账号',
  `user_name` varchar(24) NOT NULL DEFAULT '' COMMENT '用户姓名',
  `channel_mch_id` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道商户ID',
  `channel_order_no` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道订单号',
  `channel_err_code` varchar(128) NOT NULL DEFAULT '' COMMENT '渠道错误码',
  `channel_err_msg` varchar(128) NOT NULL DEFAULT '' COMMENT '渠道错误描述',
  `extra` varchar(6148) NOT NULL DEFAULT '' COMMENT '特定渠道发起时额外参数',
  `notify_url` varchar(128) NOT NULL DEFAULT '' COMMENT '通知地址',
  `param1` varchar(64) DEFAULT '' COMMENT '扩展参数1',
  `param2` varchar(64) DEFAULT '' COMMENT '扩展参数2',
  `expire_time` datetime DEFAULT NULL COMMENT '订单失效时间',
  `refund_succ_time` datetime DEFAULT NULL COMMENT '订单退款成功时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`refund_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款订单表';

/*Table structure for table `bsd_trans_order` */

DROP TABLE IF EXISTS `bsd_trans_order`;

CREATE TABLE `bsd_trans_order` (
  `trans_order_id` varchar(30) NOT NULL DEFAULT '' COMMENT '转账订单号',
  `mch_id` varchar(30) NOT NULL DEFAULT '' COMMENT '商户ID',
  `mch_trans_no` varchar(30) NOT NULL DEFAULT '' COMMENT '商户转账单号',
  `channel_code` varchar(24) NOT NULL DEFAULT '' COMMENT '渠道编码',
  `amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '转账金额(单位分)',
  `currency` varchar(3) NOT NULL DEFAULT 'cny' COMMENT '三位货币代码,人民币:cny',
  `status` tinyint(3) NOT NULL DEFAULT 0 COMMENT '转账状态:0-订单生成,1-转账中,2-转账成功,3-转账失败,4-业务处理完成',
  `result` tinyint(3) NOT NULL DEFAULT 0 COMMENT '转账结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败',
  `client_ip` varchar(32) DEFAULT '' COMMENT '客户端IP',
  `device` varchar(64) DEFAULT '' COMMENT '设备',
  `remark_info` varchar(256) NOT NULL DEFAULT '' COMMENT '备注',
  `channel_user` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道用户标识,如微信openId,支付宝账号',
  `user_name` varchar(24) NOT NULL DEFAULT '' COMMENT '用户姓名',
  `channel_mch_id` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道商户ID',
  `channel_order_no` varchar(32) NOT NULL DEFAULT '' COMMENT '渠道订单号',
  `channel_err_code` varchar(128) NOT NULL DEFAULT '' COMMENT '渠道错误码',
  `channel_err_msg` varchar(128) NOT NULL DEFAULT '' COMMENT '渠道错误描述',
  `extra` varchar(512) NOT NULL DEFAULT '' COMMENT '特定渠道发起时额外参数',
  `notify_url` varchar(128) NOT NULL DEFAULT '' COMMENT '通知地址',
  `param1` varchar(64) DEFAULT '' COMMENT '扩展参数1',
  `param2` varchar(64) DEFAULT '' COMMENT '扩展参数2',
  `expire_time` datetime DEFAULT NULL COMMENT '订单失效时间',
  `trans_succ_time` datetime DEFAULT NULL COMMENT '订单转账成功时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`trans_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转账订单表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
