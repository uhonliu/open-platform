/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : open-platform

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2019-07-17 16:58:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for msg_email_config
-- ----------------------------
DROP TABLE IF EXISTS `msg_email_config`;
CREATE TABLE `msg_email_config` (
  `config_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL COMMENT '配置名称',
  `smtp_host` varchar(255) NOT NULL COMMENT '发件服务器域名',
  `smtp_username` varchar(255) NOT NULL COMMENT '发件服务器账户',
  `smtp_password` varchar(255) NOT NULL COMMENT '发件服务器密码',
  `is_persist` tinyint(3) NOT NULL DEFAULT '0' COMMENT '保留数据0-否 1-是 不允许删除',
  `is_default` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否为默认 0-否 1-是 ',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件发送配置';

-- ----------------------------
-- Records of msg_email_config
-- ----------------------------
INSERT INTO `msg_email_config` VALUES ('1', '默认配置', 'smtp.qq.com', '515608851@qq.com', 'nmyytnzwadukcahh', '1', '1', '2019-07-17 15:42:43', '2019-07-17 15:42:46');

-- ----------------------------
-- Table structure for msg_email_logs
-- ----------------------------
DROP TABLE IF EXISTS `msg_email_logs`;
CREATE TABLE `msg_email_logs` (
  `log_id` bigint(20) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `send_to` varchar(255) NOT NULL,
  `send_cc` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `attachments` text COMMENT '附件路径',
  `send_nums` int(11) DEFAULT NULL COMMENT '发送次数',
  `error` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `result` tinyint(3) DEFAULT NULL COMMENT '0-失败 1-成功',
  `config` text COMMENT '发送配置',
  `tpl_code` varchar(255) DEFAULT NULL COMMENT '模板编号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件发送日志';

-- ----------------------------
-- Records of msg_email_logs
-- ----------------------------

-- ----------------------------
-- Table structure for msg_email_template
-- ----------------------------
DROP TABLE IF EXISTS `msg_email_template`;
CREATE TABLE `msg_email_template` (
  `tpl_id` bigint(20) NOT NULL,
  `code` varchar(255) NOT NULL COMMENT '模板编码',
  `config_id` bigint(11) NOT NULL COMMENT '发送服务器配置',
  `template` text COMMENT '模板',
  `params` text COMMENT '模板参数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`tpl_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件模板配置';

-- ----------------------------
-- Records of msg_email_template
-- ----------------------------
INSERT INTO `msg_email_template` VALUES ('1', 'test', '1', '测试${a}数据', '{\"a\":\"\"}', '2019-07-17 15:42:58', '2019-07-17 15:43:01');

-- ----------------------------
-- Table structure for msg_webhook_logs
-- ----------------------------
DROP TABLE IF EXISTS `msg_webhook_logs`;
CREATE TABLE `msg_webhook_logs` (
  `msg_id` varchar(50) NOT NULL,
  `retry_nums` decimal(8,0) NOT NULL COMMENT '重试次数',
  `total_nums` decimal(8,0) DEFAULT NULL COMMENT '通知总次数',
  `delay` decimal(16,0) NOT NULL COMMENT '延迟时间',
  `result` decimal(1,0) NOT NULL COMMENT '通知结果',
  `url` text NOT NULL COMMENT '通知地址',
  `type` varchar(255) DEFAULT NULL COMMENT '通知类型',
  `data` longtext COMMENT '请求数据',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息通知-异步通知日志';

-- ----------------------------
-- Records of msg_webhook_logs
-- ----------------------------
INSERT INTO `msg_webhook_logs` VALUES ('553642196767604736', '5', '6', '54000000', '0', 'http://www.baidu.com/notity/callback', 'order_pay', '{}', '2019-03-11 11:38:12', '2019-04-01 18:00:19');
INSERT INTO `msg_webhook_logs` VALUES ('553642668677136384', '5', '6', '54000000', '0', 'http://www.baidu.com/notity/callback', 'order_pay', '{}', '2019-03-11 11:38:12', '2019-04-01 18:00:19');
INSERT INTO `msg_webhook_logs` VALUES ('553643723964022784', '5', '6', '54000000', '0', 'http://www.baidu.com/notity/callback', 'order_pay', '{}', '2019-03-11 11:38:12', '2019-04-01 18:00:19');
INSERT INTO `msg_webhook_logs` VALUES ('563382527314624512', '10', '11', '54000000', '0', 'http://www.baidu.com/notity/callback', 'order_pay', '{}', '2019-04-04 15:21:02', '2019-05-17 08:13:20');
INSERT INTO `msg_webhook_logs` VALUES ('588689288300855296', '9', '10', '21600000', '0', 'http://www.baidu.com/notity/callback', 'order_pay', '{}', '2019-06-13 11:21:04', '2019-07-17 12:56:46');
INSERT INTO `msg_webhook_logs` VALUES ('588690845230694400', '9', '10', '21600000', '0', 'http://www.baidu.com/notity/callback', 'order_pay', '{}', '2019-06-13 11:27:15', '2019-07-17 12:56:46');
SET FOREIGN_KEY_CHECKS=1;
