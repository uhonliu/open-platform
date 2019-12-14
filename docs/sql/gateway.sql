/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.3.12-MariaDB : Database - open_platform
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `gateway_access_logs` */

DROP TABLE IF EXISTS `gateway_access_logs`;

CREATE TABLE `gateway_access_logs` (
  `access_id` bigint(20) NOT NULL COMMENT '访问ID',
  `path` varchar(255) DEFAULT NULL COMMENT '访问路径',
  `params` text DEFAULT NULL COMMENT '请求数据',
  `headers` text DEFAULT NULL COMMENT '请求头',
  `ip` varchar(500) DEFAULT NULL COMMENT '请求IP',
  `http_status` varchar(100) DEFAULT NULL COMMENT '响应状态',
  `method` varchar(50) DEFAULT NULL,
  `request_time` datetime DEFAULT NULL COMMENT '访问时间',
  `response_time` datetime DEFAULT NULL,
  `use_time` bigint(20) DEFAULT NULL,
  `user_agent` varchar(2000) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL COMMENT '区域',
  `authentication` text DEFAULT NULL COMMENT '认证信息',
  `service_id` varchar(100) DEFAULT NULL COMMENT '服务名',
  `error` varchar(255) DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`access_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='开放网关-访问日志';

/*Data for the table `gateway_access_logs` */

/*Table structure for table `gateway_ip_limit` */

DROP TABLE IF EXISTS `gateway_ip_limit`;

CREATE TABLE `gateway_ip_limit` (
  `policy_id` bigint(20) NOT NULL COMMENT '策略ID',
  `policy_name` varchar(100) NOT NULL COMMENT '策略名称',
  `policy_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '策略类型:0-拒绝/黑名单 1-允许/白名单',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最近一次修改时间',
  `ip_address` varchar(255) NOT NULL COMMENT 'ip地址/IP段:多个用隔开;最多10个',
  PRIMARY KEY (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='开放网关-IP访问控制-策略';

/*Data for the table `gateway_ip_limit` */

/*Table structure for table `gateway_ip_limit_api` */

DROP TABLE IF EXISTS `gateway_ip_limit_api`;

CREATE TABLE `gateway_ip_limit_api` (
  `policy_id` bigint(20) NOT NULL COMMENT '策略ID',
  `api_id` bigint(20) NOT NULL COMMENT '接口资源ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  UNIQUE KEY `api_policy` (`api_id`,`policy_id`) USING BTREE,
  KEY `policy_id` (`policy_id`) USING BTREE,
  KEY `api_id` (`api_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='开放网关-IP访问控制-API接口';

/*Data for the table `gateway_ip_limit_api` */

/*Table structure for table `gateway_rate_limit` */

DROP TABLE IF EXISTS `gateway_rate_limit`;

CREATE TABLE `gateway_rate_limit` (
  `policy_id` bigint(20) NOT NULL,
  `policy_name` varchar(255) DEFAULT NULL,
  `policy_type` varchar(255) DEFAULT NULL COMMENT '限流规则类型:url,origin,user',
  `limit_quota` bigint(20) NOT NULL DEFAULT 0 COMMENT '限流数',
  `interval_unit` varchar(10) NOT NULL DEFAULT 'seconds' COMMENT '单位时间:seconds-秒,minutes-分钟,hours-小时,days-天',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='开放网关-流量控制-策略';

/*Data for the table `gateway_rate_limit` */

/*Table structure for table `gateway_rate_limit_api` */

DROP TABLE IF EXISTS `gateway_rate_limit_api`;

CREATE TABLE `gateway_rate_limit_api` (
  `policy_id` bigint(11) NOT NULL DEFAULT 0 COMMENT '限制数量',
  `api_id` bigint(11) NOT NULL DEFAULT 1 COMMENT '时间间隔(秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  UNIQUE KEY `api_policy` (`api_id`,`policy_id`) USING BTREE,
  KEY `policy_id` (`policy_id`) USING BTREE,
  KEY `api_id` (`api_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='开放网关-流量控制-API接口';

/*Data for the table `gateway_rate_limit_api` */

/*Table structure for table `gateway_route` */

DROP TABLE IF EXISTS `gateway_route`;

CREATE TABLE `gateway_route` (
  `route_id` bigint(20) NOT NULL COMMENT '路由ID',
  `route_name` varchar(255) NOT NULL COMMENT '路由名称',
  `route_type` varchar(20) NOT NULL DEFAULT '' COMMENT '路由类型:service-负载均衡 url-反向代理',
  `path` varchar(255) DEFAULT NULL COMMENT '路径',
  `service_id` varchar(255) DEFAULT NULL COMMENT '服务ID',
  `url` varchar(255) DEFAULT NULL COMMENT '完整地址',
  `strip_prefix` tinyint(3) NOT NULL DEFAULT 1 COMMENT '忽略前缀',
  `retryable` tinyint(3) NOT NULL DEFAULT 0 COMMENT '0-不重试 1-重试',
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态:0-无效 1-有效',
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '是否为保留数据:0-否 1-是',
  `route_desc` varchar(255) DEFAULT NULL COMMENT '路由说明',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='开放网关-路由';

/*Data for the table `gateway_route` */

insert  into `gateway_route`(`route_id`,`route_name`,`route_type`,`path`,`service_id`,`url`,`strip_prefix`,`retryable`,`status`,`is_persist`,`route_desc`,`create_time`,`update_time`) values 
(556587504019439616,'base-server','service','/base/**','base-server','',0,0,1,1,'平台基础服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(556595619813130240,'uaa-admin-server','service','/admin/**','uaa-admin-server','',0,0,1,1,'平台用户认证服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(556595619813130241,'uaa-portal-server','service','/portal/**','uaa-portal-server','',0,0,1,1,'门户开发者认证服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(556595914240688128,'msg-server','service','/msg/**','msg-server','',0,0,1,1,'消息服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(556595914240688139,'task-server','service','/task/**','task-server','',0,0,1,1,'任务调度服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(556595914240688145,'bpm-server','service','/bpm/**','bpm-server','',0,0,1,1,'工作流服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(1152136796736503810,'generator-server','service','/code/**','generator-server','',0,0,1,0,'在线代码生成服务器','2019-07-30 15:33:29','2019-07-30 15:33:29'),
(1195622648748380161,'user-server','service','/plat/**','user-server','',0,0,1,0,'用户服务','2019-11-16 16:40:27','2019-11-16 16:58:15'),
(1195627355592134658,'dingtalk-server','service','/dingtalk/**','dingtalk-server',NULL,0,0,1,0,'钉钉服务','2019-11-16 16:59:09','2019-11-16 16:59:09'),
(1195627537897558018,'file-server','service','/file/**','file-server',NULL,0,0,1,0,'文件服务','2019-11-16 16:59:53','2019-11-16 16:59:53'),
(1195627683255357441,'payment-server','service','/pay/**','payment-server',NULL,0,0,1,0,'支付服务','2019-11-16 17:00:28','2019-11-16 17:00:28'),
(1195627819096281089,'org-server','service','/org/**','org-server',NULL,0,0,1,0,'组织架构服务','2019-11-16 17:01:00','2019-11-16 17:01:00'),
(1195627959622242306,'comment-server','service','/comment/**','comment-server',NULL,0,0,1,0,'评价服务','2019-11-16 17:01:33','2019-11-16 17:01:33');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
