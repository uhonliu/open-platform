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
/*Table structure for table `base_account` */

DROP TABLE IF EXISTS `base_account`;

CREATE TABLE `base_account` (
  `account_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL COMMENT '用户Id',
  `account` varchar(255) NOT NULL COMMENT '标识：手机号、邮箱、 用户名、或第三方应用的唯一标识',
  `password` varchar(255) NOT NULL COMMENT '密码凭证：站内的保存密码、站外的不保存或保存token）',
  `account_type` varchar(255) NOT NULL COMMENT '登录类型:password-密码、mobile-手机号、email-邮箱、weixin-微信、weibo-微博、qq-等等',
  `domain` varchar(255) DEFAULT NULL COMMENT '账户域:@admin.com,@developer.com',
  `register_ip` varchar(255) DEFAULT NULL COMMENT '注册IP',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `status` int(11) DEFAULT NULL COMMENT '状态:0-禁用 1-启用 2-锁定',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`account_id`),
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='登录账号';

/*Data for the table `base_account` */

insert  into `base_account`(`account_id`,`user_id`,`account`,`password`,`account_type`,`domain`,`register_ip`,`create_time`,`status`,`update_time`) values 
(521677655368531968,521677655146233856,'admin','$2a$10$A7EHximvrsa4ESX1uSlkJupbg2PLO2StzDzy67NX4YV25MxmbGvXu','username','@admin.com',NULL,'2019-07-03 17:11:59',1,'2019-07-11 17:38:21'),
(557063237787451392,557063237640650752,'test','$2a$10$SdqHS7Y8VcrR0WfCf9FI3uhcUfYKu58per0fVJLW.iPOBt.bFYp0y','username','@admin.com',NULL,'2019-07-03 17:12:02',1,'2019-07-11 17:20:44');

/*Table structure for table `base_account_logs` */

DROP TABLE IF EXISTS `base_account_logs`;

CREATE TABLE `base_account_logs` (
  `id` bigint(20) NOT NULL,
  `login_time` datetime NOT NULL,
  `login_ip` varchar(255) NOT NULL COMMENT '登录Ip',
  `login_agent` varchar(500) NOT NULL COMMENT '登录设备',
  `login_nums` int(11) NOT NULL COMMENT '登录次数',
  `user_id` bigint(20) NOT NULL,
  `account` varchar(100) NOT NULL,
  `account_type` varchar(50) NOT NULL,
  `account_id` bigint(20) NOT NULL COMMENT '账号ID',
  `domain` varchar(255) DEFAULT NULL COMMENT '账号域',
  PRIMARY KEY (`id`),
  KEY `account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='登录日志';

/*Data for the table `base_account_logs` */

/*Table structure for table `base_action` */

DROP TABLE IF EXISTS `base_action`;

CREATE TABLE `base_action` (
  `action_id` bigint(20) NOT NULL COMMENT '资源ID',
  `action_code` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '资源编码',
  `action_name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '资源名称',
  `action_desc` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '资源描述',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '资源父节点',
  `priority` int(10) NOT NULL DEFAULT 0 COMMENT '优先级 越小越靠前',
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态:0-无效 1-有效',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '保留数据0-否 1-是 不允许删除',
  `service_id` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '服务名称',
  PRIMARY KEY (`action_id`),
  UNIQUE KEY `action_code` (`action_code`) USING BTREE,
  UNIQUE KEY `action_id` (`action_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='系统资源-功能操作';

/*Data for the table `base_action` */

insert  into `base_action`(`action_id`,`action_code`,`action_name`,`action_desc`,`menu_id`,`priority`,`status`,`create_time`,`update_time`,`is_persist`,`service_id`) values 
(1131849293404176385,'systemMenuView','查看','',3,0,1,'2019-05-24 17:07:54','2019-08-22 14:25:48',1,'uaa-admin-server'),
(1131849510572654593,'systemMenuEdit','编辑','',3,0,1,'2019-05-24 17:08:46','2019-05-24 17:08:46',1,'uaa-admin-server'),
(1131858946338992129,'systemRoleView','查看','',8,0,1,'2019-05-24 17:46:16','2019-05-24 17:46:16',1,'uaa-admin-server'),
(1131863248310775809,'systemRoleEdit','编辑','',8,0,1,'2019-05-24 18:03:22','2019-05-24 18:03:22',1,'uaa-admin-server'),
(1131863723722551297,'systemAppView','查看','',9,0,1,'2019-05-24 18:05:15','2019-05-24 18:05:15',1,'uaa-admin-server'),
(1131863775899693057,'systemAppEdit','编辑','',9,0,1,'2019-05-24 18:05:27','2019-05-24 18:05:27',1,'uaa-admin-server'),
(1131864400507056130,'systemUserView','查看','',10,0,1,'2019-05-24 18:07:56','2019-05-24 18:07:56',1,'uaa-admin-server'),
(1131864444878598146,'systemUserEdit','编辑','',10,0,1,'2019-05-24 18:08:07','2019-05-24 18:08:07',1,'uaa-admin-server'),
(1131864827252322305,'gatewayIpLimitView','查看','',2,0,1,'2019-05-24 18:09:38','2019-05-24 18:09:38',1,'uaa-admin-server'),
(1131864864267055106,'gatewayIpLimitEdit','编辑','',2,0,1,'2019-05-24 18:09:47','2019-05-24 18:09:47',1,'uaa-admin-server'),
(1131865040289411074,'gatewayRouteView','查看','',5,0,1,'2019-05-24 18:10:29','2019-05-24 18:10:29',1,'uaa-admin-server'),
(1131865075609645057,'gatewayRouteEdit','编辑','',5,0,1,'2019-05-24 18:10:37','2019-05-24 18:10:37',1,'uaa-admin-server'),
(1131865482314526722,'systemApiView','查看','',6,0,1,'2019-05-24 18:12:14','2019-05-24 18:12:14',1,'uaa-admin-server'),
(1131865520738545666,'systemApiEdit','编辑','',6,0,1,'2019-05-24 18:12:23','2019-05-24 18:12:23',1,'uaa-admin-server'),
(1131865772929462274,'gatewayLogsView','查看','',12,0,1,'2019-05-24 18:13:23','2019-05-24 18:13:23',1,'uaa-admin-server'),
(1131865931146997761,'gatewayRateLimitView','查看','',14,0,1,'2019-05-24 18:14:01','2019-05-24 18:14:01',1,'uaa-admin-server'),
(1131865974704844802,'gatewayRateLimitEdit','编辑','',14,0,1,'2019-05-24 18:14:12','2019-05-24 18:14:12',1,'uaa-admin-server'),
(1131866278187905026,'jobView','查看','',16,0,1,'2019-05-24 18:15:24','2019-05-25 03:23:15',1,'uaa-admin-server'),
(1131866310622457857,'jobEdit','编辑','',16,0,1,'2019-05-24 18:15:32','2019-05-25 03:23:21',1,'uaa-admin-server'),
(1131866943459045377,'schedulerLogsView','查看','',19,0,1,'2019-05-24 18:18:03','2019-05-24 18:18:03',1,'uaa-admin-server'),
(1131867094479155202,'notifyHttpLogsView','查看','',18,0,1,'2019-05-24 18:18:39','2019-05-24 18:18:39',1,'uaa-admin-server'),
(1152234326254051329,'GenerateCode','生成代码','',1152141296369057794,0,1,'2019-07-19 23:10:46','2019-07-19 23:10:54',0,'uaa-admin-server'),
(1164422088547635202,'developerView','查看','',1149253733673287682,0,1,'2019-08-22 14:20:34','2019-08-22 14:24:53',0,'uaa-admin-server'),
(1164422211189084162,'developerEdit','编辑','',1149253733673287682,0,1,'2019-08-22 14:21:04','2019-08-22 14:21:04',0,'uaa-admin-server');

/*Table structure for table `base_api` */

DROP TABLE IF EXISTS `base_api`;

CREATE TABLE `base_api` (
  `api_id` bigint(20) NOT NULL COMMENT '接口ID',
  `api_code` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '接口编码',
  `api_name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '接口名称',
  `api_category` varchar(50) COLLATE utf8_bin DEFAULT 'default' COMMENT '接口分类:default-默认分类',
  `api_desc` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '资源描述',
  `request_method` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '请求方式',
  `content_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '响应类型',
  `service_id` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '服务ID',
  `path` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '请求路径',
  `priority` bigint(20) NOT NULL DEFAULT 0 COMMENT '优先级',
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态:0-无效 1-有效',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '保留数据0-否 1-是 不允许删除',
  `is_auth` tinyint(3) NOT NULL DEFAULT 1 COMMENT '是否需要认证: 0-无认证 1-身份认证 默认:1',
  `is_open` tinyint(3) NOT NULL DEFAULT 0 COMMENT '是否公开: 0-内部的 1-公开的',
  `class_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '类名',
  `method_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '方法名',
  PRIMARY KEY (`api_id`),
  UNIQUE KEY `api_code` (`api_code`),
  UNIQUE KEY `api_id` (`api_id`),
  KEY `service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='系统资源-API接口';

/*Data for the table `base_api` */

insert  into `base_api`(`api_id`,`api_code`,`api_name`,`api_category`,`api_desc`,`request_method`,`content_type`,`service_id`,`path`,`priority`,`status`,`create_time`,`update_time`,`is_persist`,`is_auth`,`is_open`,`class_name`,`method_name`) values 
(1,'all','全部','default','所有请求','get,post',NULL,'api-zuul-server','/**',0,1,'2019-03-07 21:52:17','2019-03-14 21:41:28',1,1,1,NULL,NULL),
(2,'actuator','监控端点','default','监控端点','post',NULL,'api-zuul-server','/actuator/**',0,1,'2019-03-07 21:52:17','2019-03-14 21:41:28',1,1,1,NULL,NULL);

/*Table structure for table `base_app` */

DROP TABLE IF EXISTS `base_app`;

CREATE TABLE `base_app` (
  `app_id` varchar(50) NOT NULL COMMENT '客户端ID',
  `api_key` varchar(255) DEFAULT NULL COMMENT 'API访问key',
  `secret_key` varchar(255) NOT NULL COMMENT 'API访问密钥',
  `app_name` varchar(255) NOT NULL COMMENT 'app名称',
  `app_name_en` varchar(255) NOT NULL COMMENT 'app英文名称',
  `app_icon` varchar(255) NOT NULL COMMENT '应用图标',
  `app_type` varchar(50) NOT NULL COMMENT 'app类型:server-服务应用 app-手机应用 pc-PC网页应用 wap-手机网页应用',
  `app_desc` varchar(255) DEFAULT NULL COMMENT 'app描述',
  `app_os` varchar(25) DEFAULT NULL COMMENT '移动应用操作系统:ios-苹果 android-安卓',
  `website` varchar(255) NOT NULL COMMENT '官网地址',
  `developer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '开发者ID:默认为0',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态:0-无效 1-有效',
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '保留数据0-否 1-是 不允许删除',
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统应用-基础信息';

/*Data for the table `base_app` */

insert  into `base_app`(`app_id`,`api_key`,`secret_key`,`app_name`,`app_name_en`,`app_icon`,`app_type`,`app_desc`,`app_os`,`website`,`developer_id`,`create_time`,`update_time`,`status`,`is_persist`) values 
('1552274783265','7gBZcbsC7kLIWCdELIl8nxcs','0osTIhce7uPvDKHz6aa67bhCukaKoYl4','平台用户认证服务器','uaa-admin-server','','server','资源服务器','','http://www.baidu.com',0,'2018-11-12 17:48:45','2019-07-11 18:31:05',1,1);

/*Table structure for table `base_authority` */

DROP TABLE IF EXISTS `base_authority`;

CREATE TABLE `base_authority` (
  `authority_id` bigint(20) NOT NULL,
  `authority` varchar(255) NOT NULL COMMENT '权限标识',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单资源ID',
  `api_id` bigint(20) DEFAULT NULL COMMENT 'API资源ID',
  `action_id` bigint(20) DEFAULT NULL,
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`authority_id`),
  KEY `menu_id` (`menu_id`),
  KEY `api_id` (`api_id`),
  KEY `action_id` (`action_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统权限-菜单权限、操作权限、API权限';

/*Data for the table `base_authority` */

insert  into `base_authority`(`authority_id`,`authority`,`menu_id`,`api_id`,`action_id`,`status`,`create_time`,`update_time`) values 
(1,'MENU_system',1,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(2,'MENU_gatewayIpLimit',2,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(3,'MENU_systemMenu',3,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(5,'MENU_gatewayRoute',5,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(6,'MENU_systemApi',6,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(8,'MENU_systemRole',8,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(9,'MENU_systemApp',9,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(10,'MENU_systemUser',10,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(11,'MENU_apiDebug',11,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(12,'MENU_gatewayLogs',12,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(13,'MENU_gateway',13,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(14,'MENU_gatewayRateLimit',14,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(15,'MENU_task',15,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(16,'MENU_job',16,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(17,'MENU_message',17,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(18,'MENU_webhook',18,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(19,'MENU_taskLogs',19,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(99,'API_all',NULL,1,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(100,'API_actuator',NULL,2,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131849293509033986,'ACTION_systemMenuView',NULL,NULL,1131849293404176385,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131849510677512193,'ACTION_systemMenuEdit',NULL,NULL,1131849510572654593,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131858946414489602,'ACTION_systemRoleView',NULL,NULL,1131858946338992129,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131863248373690369,'ACTION_systemRoleEdit',NULL,NULL,1131863248310775809,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131863723806437377,'ACTION_systemAppView',NULL,NULL,1131863723722551297,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131863775966801921,'ACTION_systemAppEdit',NULL,NULL,1131863775899693057,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131864400590942210,'ACTION_systemUserView',NULL,NULL,1131864400507056130,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131864444954095617,'ACTION_systemUserEdit',NULL,NULL,1131864444878598146,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131864827327819778,'ACTION_gatewayIpLimitView',NULL,NULL,1131864827252322305,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131864864325775361,'ACTION_gatewayIpLimitEdit',NULL,NULL,1131864864267055106,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865040381685761,'ACTION_gatewayRouteView',NULL,NULL,1131865040289411074,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865075697725442,'ACTION_gatewayRouteEdit',NULL,NULL,1131865075609645057,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865482390024193,'ACTION_systemApiView',NULL,NULL,1131865482314526722,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865520809848834,'ACTION_systemApiEdit',NULL,NULL,1131865520738545666,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865773000765441,'ACTION_gatewayLogsView',NULL,NULL,1131865772929462274,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865931214106626,'ACTION_gatewayRateLimitView',NULL,NULL,1131865931146997761,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131865974771953666,'ACTION_gatewayRateLimitEdit',NULL,NULL,1131865974704844802,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131866278280179714,'ACTION_jobView',NULL,NULL,1131866278187905026,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131866310676983810,'ACTION_jobEdit',NULL,NULL,1131866310622457857,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131866943534542850,'ACTION_schedulerLogsView',NULL,NULL,1131866943459045377,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1131867094550458369,'ACTION_notifyHttpLogsView',NULL,NULL,1131867094479155202,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1149253733748785154,'MENU_developer',1149253733673287682,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1152141296406806529,'MENU_Generate',1152141296369057794,NULL,NULL,1,NULL,NULL),
(1152234326304382978,'ACTION_GenerateCode',NULL,NULL,1152234326254051329,1,NULL,NULL),
(1164422088618938370,'ACTION_developerView',NULL,NULL,1164422088547635202,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1164422211226832898,'ACTION_developerEdit',NULL,NULL,1164422211189084162,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1187247165321658369,'MENU_monitor',1141579952217567234,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1187247181910130690,'MENU_SpringBootAdmin',1141580147030405121,NULL,NULL,1,'2019-07-30 15:43:15','2019-07-30 15:43:15'),
(1195628303781662721,'MENU_user',1195628303739719682,NULL,NULL,1,'2019-11-16 17:02:55','2019-11-16 17:02:55'),
(1195628508476280834,'MENU_userList',1195628508459503618,NULL,NULL,1,'2019-11-16 17:03:44','2019-11-16 17:03:44'),
(1195628690454548481,'MENU_payment',1195628690429382657,NULL,NULL,1,'2019-11-16 17:04:28','2019-11-16 17:04:28'),
(1195628899813232641,'MENU_payList',1195628899800649730,NULL,NULL,1,'2019-11-16 17:05:18','2019-11-16 17:05:18'),
(1195629041735897089,'MENU_transList',1195629041723314177,NULL,NULL,1,'2019-11-16 17:05:51','2019-11-16 17:26:23'),
(1195629211903004674,'MENU_refundList',1195629211894616065,NULL,NULL,1,'2019-11-16 17:06:32','2019-11-16 17:06:32'),
(1195629439829872642,'MENU_mchList',1195629439813095426,NULL,NULL,1,'2019-11-16 17:07:26','2019-11-16 17:07:26'),
(1195629695711776769,'MENU_channelList',1195629695703388161,NULL,NULL,1,'2019-11-16 17:08:27','2019-11-16 17:08:27'),
(1195629869716672514,'MENU_mchNotify',1195629869704089602,NULL,NULL,1,'2019-11-16 17:09:09','2019-11-16 17:09:09'),
(1195630357044465665,'MENU_organization',1195630357015105537,NULL,NULL,1,'2019-11-16 17:11:05','2019-11-16 17:11:05'),
(1195630504612663297,'MENU_companyList',1195630504604274690,NULL,NULL,1,'2019-11-16 17:11:40','2019-11-16 17:11:40'),
(1195630648716365826,'MENU_departmentList',1195630648687005697,NULL,NULL,1,'2019-11-16 17:12:15','2019-11-16 17:13:12'),
(1195630839120990209,'MENU_positionList',1195630839104212994,NULL,NULL,1,'2019-11-16 17:13:00','2019-11-16 17:13:00'),
(1195631071170859010,'MENU_staffList',1195631071149887490,NULL,NULL,1,'2019-11-16 17:13:55','2019-11-16 17:13:55'),
(1195631276234575874,'MENU_dingTalk',1195631276209410050,NULL,NULL,1,'2019-11-16 17:14:44','2019-11-16 17:30:18'),
(1195631509597261826,'MENU_comment',1195631509584678914,NULL,NULL,1,'2019-11-16 17:15:40','2019-11-16 17:15:40'),
(1195631662076989441,'MENU_commentList',1195631662056017922,NULL,NULL,1,'2019-11-16 17:16:16','2019-11-16 17:16:16');

/*Table structure for table `base_authority_action` */

DROP TABLE IF EXISTS `base_authority_action`;

CREATE TABLE `base_authority_action` (
  `authority_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '权限ID',
  `action_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '操作ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  UNIQUE KEY `authority_action` (`authority_id`,`action_id`) USING BTREE,
  KEY `action_id` (`action_id`) USING BTREE,
  KEY `authority_id` (`authority_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统权限-功能操作关联表';

/*Data for the table `base_authority_action` */

/*Table structure for table `base_authority_app` */

DROP TABLE IF EXISTS `base_authority_app`;

CREATE TABLE `base_authority_app` (
  `authority_id` bigint(50) NOT NULL COMMENT '权限ID',
  `app_id` varchar(100) NOT NULL COMMENT '应用ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间:null表示长期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  UNIQUE KEY `authority_app` (`authority_id`,`app_id`) USING BTREE,
  KEY `authority_id` (`authority_id`) USING BTREE,
  KEY `app_id` (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统权限-应用关联';

/*Data for the table `base_authority_app` */

/*Table structure for table `base_authority_role` */

DROP TABLE IF EXISTS `base_authority_role`;

CREATE TABLE `base_authority_role` (
  `authority_id` bigint(20) NOT NULL COMMENT '权限ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间:null表示长期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  UNIQUE KEY `authority_role` (`authority_id`,`role_id`) USING BTREE,
  KEY `authority_id` (`authority_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统权限-角色关联';

/*Data for the table `base_authority_role` */

insert  into `base_authority_role`(`authority_id`,`role_id`,`expire_time`,`create_time`,`update_time`) values 
(1,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(2,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(3,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(3,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(5,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(6,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(8,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(8,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(9,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(9,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(9,3,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(10,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(10,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(11,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(12,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(13,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(14,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(15,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(16,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(17,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(18,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(19,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131849293509033986,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131849293509033986,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131849510677512193,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131858946414489602,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131858946414489602,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131863248373690369,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131863723806437377,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131863723806437377,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131863775966801921,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131864400590942210,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131864400590942210,2,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131864444954095617,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131864827327819778,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131864864325775361,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865040381685761,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865075697725442,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865482390024193,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865520809848834,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865773000765441,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865931214106626,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131865974771953666,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131866278280179714,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131866310676983810,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131866943534542850,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1131867094550458369,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1149253733748785154,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1164422088618938370,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41'),
(1164422211226832898,1,NULL,'2019-08-22 14:54:41','2019-08-22 14:54:41');

/*Table structure for table `base_authority_user` */

DROP TABLE IF EXISTS `base_authority_user`;

CREATE TABLE `base_authority_user` (
  `authority_id` bigint(20) NOT NULL COMMENT '权限ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  UNIQUE KEY `authority_user` (`authority_id`,`user_id`) USING BTREE,
  KEY `authority_id` (`authority_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统权限-用户关联';

/*Data for the table `base_authority_user` */

/*Table structure for table `base_developer` */

DROP TABLE IF EXISTS `base_developer`;

CREATE TABLE `base_developer` (
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '登陆账号',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(50) DEFAULT NULL COMMENT '手机号',
  `user_type` varchar(20) DEFAULT 'isp' COMMENT '开发者类型: isp-服务提供商 normal-自研开发者',
  `company_id` bigint(20) DEFAULT NULL COMMENT '企业ID',
  `user_desc` varchar(255) DEFAULT '' COMMENT '描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(3) DEFAULT 1 COMMENT '状态:0-禁用 1-正常 2-锁定',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统用户-开发者信息';

/*Data for the table `base_developer` */

/*Table structure for table `base_menu` */

DROP TABLE IF EXISTS `base_menu`;

CREATE TABLE `base_menu` (
  `menu_id` bigint(20) NOT NULL COMMENT '菜单Id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级菜单',
  `menu_code` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '菜单编码',
  `menu_name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '菜单名称',
  `menu_desc` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `scheme` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '路径前缀',
  `path` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '请求路径',
  `icon` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT '菜单标题',
  `target` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '_self' COMMENT '打开方式:_self窗口内,_blank新窗口',
  `priority` bigint(20) NOT NULL DEFAULT 0 COMMENT '优先级 越小越靠前',
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态:0-无效 1-有效',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '保留数据0-否 1-是 不允许删除',
  `service_id` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '服务名',
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `menu_code` (`menu_code`),
  KEY `service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='系统资源-菜单信息';

/*Data for the table `base_menu` */

insert  into `base_menu`(`menu_id`,`parent_id`,`menu_code`,`menu_name`,`menu_desc`,`scheme`,`path`,`icon`,`target`,`priority`,`status`,`create_time`,`update_time`,`is_persist`,`service_id`) values 
(1,0,'system','系统管理','系统管理','/','','md-folder','_self',0,1,'2018-07-29 21:20:10','2018-07-29 21:20:10',1,'uaa-admin-server'),
(2,13,'gatewayIpLimit','访问控制','来源IP/域名访问控制,白名单、黑名单','/','gateway/ip-limit/index','md-document','_self',0,1,'2018-07-29 21:20:13','2018-07-29 21:20:13',1,'uaa-admin-server'),
(3,1,'systemMenu','功能菜单','功能菜单资源','/','system/menus/index','md-list','_self',0,1,'2018-07-29 21:20:13','2018-07-29 21:20:13',1,'uaa-admin-server'),
(5,13,'gatewayRoute','网关路由','网关路由','/','gateway/route/index','md-document','_self',0,1,'2018-07-29 21:20:13','2018-07-29 21:20:13',1,'uaa-admin-server'),
(6,13,'systemApi','API列表','API接口资源','/','system/api/index','md-document','_self',0,1,'2018-07-29 21:20:13','2018-07-29 21:20:13',1,'uaa-admin-server'),
(8,1,'systemRole','角色管理','角色信息管理','/','system/role/index','md-people','_self',0,1,'2018-12-27 15:26:54','2018-12-27 15:26:54',1,'uaa-admin-server'),
(9,1,'systemApp','应用管理','应用信息管理','/','system/app/index','md-apps','_self',0,1,'2018-12-27 15:41:52','2018-12-27 15:41:52',1,'uaa-admin-server'),
(10,1,'systemUser','用户管理','系统用户','/','system/user/index','md-person','_self',0,1,'2018-12-27 15:46:29','2018-12-27 15:46:29',1,'uaa-admin-server'),
(11,13,'apiDebug','接口调试','swagger接口调试','http://','localhost:8888','md-document','_blank',0,1,'2019-01-10 20:47:19','2019-01-10 20:47:19',1,'uaa-admin-server'),
(12,13,'gatewayLogs','访问日志','','/','gateway/logs/index','md-document','_self',0,1,'2019-01-28 02:37:42','2019-01-28 02:37:42',1,'uaa-admin-server'),
(13,0,'gateway','API网关','API网关','/','','md-folder','_self',0,1,'2019-02-25 00:15:09','2019-02-25 00:15:09',1,'uaa-admin-server'),
(14,13,'gatewayRateLimit','流量控制','API限流','/','gateway/rate-limit/index','md-document','_self',0,1,'2019-03-13 21:47:20','2019-03-13 21:47:20',1,'uaa-admin-server'),
(15,0,'task','任务调度','任务调度','/','','md-document','_self',0,1,'2019-04-01 16:30:27','2019-04-01 16:30:27',1,'uaa-admin-server'),
(16,15,'job','定时任务','定时任务列表','/','task/job/index','md-document','_self',0,1,'2019-04-01 16:31:15','2019-04-01 16:31:15',1,'uaa-admin-server'),
(17,0,'message','消息管理','消息管理','/','','md-document','_self',0,1,'2019-04-04 16:37:23','2019-04-04 16:37:23',1,'uaa-admin-server'),
(18,17,'webhook','异步通知日志','异步通知日志','/','msg/webhook/index','md-document','_self',0,1,'2019-04-04 16:38:21','2019-04-04 16:38:21',1,'uaa-admin-server'),
(19,15,'taskLogs','调度日志','调度日志','/','task/logs/index','md-document','_self',0,1,'2019-05-24 18:17:49','2019-05-24 18:17:49',1,'uaa-admin-server'),
(1141579952217567234,0,'monitor','系统监控','系统监控','/','','md-document','_self',0,1,'2019-06-20 13:34:04','2019-06-20 13:34:04',0,'uaa-admin-server'),
(1141580147030405121,1141579952217567234,'SpringBootAdmin','SpringBootAdmin','SpringBootAdmin','http://','localhost:8849','md-document','_blank',0,1,'2019-06-20 13:34:51','2019-06-20 13:34:51',0,'uaa-admin-server'),
(1149253733673287682,1,'developer','开发者管理','开发者管理','/','system/developer/index','md-person','_self',0,1,'2019-07-11 17:46:56','2019-07-11 17:46:56',0,'uaa-admin-server'),
(1152141296369057794,1,'Generate','在线代码生成','在线代码生成','/','system/generate/index','md-document','_self',0,1,'2019-07-19 17:01:05','2019-07-19 17:01:05',0,'uaa-admin-server'),
(1195628303739719682,0,'user','用户中心','','/','','md-document','_self',0,1,'2019-11-16 17:02:55','2019-11-16 17:02:55',0,'uaa-admin-server'),
(1195628508459503618,1195628303739719682,'userList','用户列表','','/','user/user/userList','md-document','_self',0,1,'2019-11-16 17:03:44','2019-11-16 17:03:44',0,'uaa-admin-server'),
(1195628690429382657,0,'payment','支付中心','','/','','md-document','_self',0,1,'2019-11-16 17:04:28','2019-11-16 17:04:28',0,'uaa-admin-server'),
(1195628899800649730,1195628690429382657,'payList','支付订单','','/','payment/payList','md-document','_self',0,1,'2019-11-16 17:05:18','2019-11-16 17:05:18',0,'uaa-admin-server'),
(1195629041723314177,1195628690429382657,'transList','转账订单','','/','payment/transList','md-document','_self',0,1,'2019-11-16 17:05:51','2019-11-16 17:26:23',0,'uaa-admin-server'),
(1195629211894616065,1195628690429382657,'refundList','退款订单','','/','payment/refundList','md-document','_self',0,1,'2019-11-16 17:06:32','2019-11-16 17:06:32',0,'uaa-admin-server'),
(1195629439813095426,1195628690429382657,'mchList','商户信息','','/','payment/mchList','md-document','_self',0,1,'2019-11-16 17:07:26','2019-11-16 17:07:26',0,'uaa-admin-server'),
(1195629695703388161,1195628690429382657,'channelList','支付渠道','','/','payment/channelList','md-document','_self',0,1,'2019-11-16 17:08:27','2019-11-16 17:08:27',0,'uaa-admin-server'),
(1195629869704089602,1195628690429382657,'mchNotify','商户通知','','/','payment/mchNotify','md-document','_self',0,1,'2019-11-16 17:09:09','2019-11-16 17:09:09',0,'uaa-admin-server'),
(1195630357015105537,0,'organization','组织架构','','/','','md-document','_self',0,1,'2019-11-16 17:11:05','2019-11-16 17:11:05',0,'uaa-admin-server'),
(1195630504604274690,1195630357015105537,'companyList','公司管理','','/','organization/company/companyList','md-document','_self',0,1,'2019-11-16 17:11:40','2019-11-16 17:11:40',0,'uaa-admin-server'),
(1195630648687005697,1195630357015105537,'departmentList','部门管理','','/','organization/department/departmentList','md-document','_self',0,1,'2019-11-16 17:12:15','2019-11-16 17:13:12',0,'uaa-admin-server'),
(1195630839104212994,1195630357015105537,'positionList','职位管理','','/','organization/position/positionList','md-document','_self',0,1,'2019-11-16 17:13:00','2019-11-16 17:13:00',0,'uaa-admin-server'),
(1195631071149887490,1195630357015105537,'staffList','人员管理','','/','organization/staff/staffList','md-document','_self',0,1,'2019-11-16 17:13:55','2019-11-16 17:13:55',0,'uaa-admin-server'),
(1195631276209410050,1195630357015105537,'dingTalk','钉钉配置','','/','organization/dingTalk/dingTalk','md-document','_self',0,1,'2019-11-16 17:14:44','2019-11-16 17:30:18',0,'uaa-admin-server'),
(1195631509584678914,0,'comment','评价中心','','/','','md-document','_self',0,1,'2019-11-16 17:15:40','2019-11-16 17:15:40',0,'uaa-admin-server'),
(1195631662056017922,1195631509584678914,'commentList','评论列表','','/','comment/commentList','md-document','_self',0,1,'2019-11-16 17:16:16','2019-11-16 17:16:16',0,'uaa-admin-server');

/*Table structure for table `base_role` */

DROP TABLE IF EXISTS `base_role`;

CREATE TABLE `base_role` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `role_code` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '角色编码',
  `role_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '角色名称',
  `status` tinyint(3) NOT NULL DEFAULT 1 COMMENT '状态:0-无效 1-有效',
  `role_desc` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '保留数据0-否 1-是 不允许删除',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_code` (`role_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='系统角色-基础信息';

/*Data for the table `base_role` */

insert  into `base_role`(`role_id`,`role_code`,`role_name`,`status`,`role_desc`,`create_time`,`update_time`,`is_persist`) values 
(1,'admin','系统管理员',1,'系统管理员','2018-07-29 21:14:54','2019-05-25 03:06:57',1),
(2,'operator','运营人员',1,'运营人员','2018-07-29 21:14:54','2019-05-25 15:14:56',1),
(3,'support','客服',1,'客服','2018-07-29 21:14:54','2019-05-25 15:17:07',1);

/*Table structure for table `base_role_user` */

DROP TABLE IF EXISTS `base_role_user`;

CREATE TABLE `base_role_user` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  KEY `fk_user` (`user_id`) USING BTREE,
  KEY `fk_role` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='系统角色-用户关联';

/*Data for the table `base_role_user` */

insert  into `base_role_user`(`user_id`,`role_id`,`create_time`,`update_time`) values 
(521677655146233856,1,'2019-07-30 15:51:08','2019-07-30 15:51:08'),
(557063237640650752,2,'2019-07-30 15:51:08','2019-07-30 15:51:08');

/*Table structure for table `base_tentant` */

DROP TABLE IF EXISTS `base_tentant`;

CREATE TABLE `base_tentant` (
  `tentant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tentant_name` varchar(100) NOT NULL COMMENT '租户名称',
  `tentant_desc` varchar(255) NOT NULL COMMENT '租户描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`tentant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='租户信息表';

/*Data for the table `base_tentant` */

/*Table structure for table `base_tentant_modules` */

DROP TABLE IF EXISTS `base_tentant_modules`;

CREATE TABLE `base_tentant_modules` (
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `tentant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `service_id` varchar(100) NOT NULL COMMENT '服务名称',
  `module_desc` varchar(255) NOT NULL COMMENT '模块描述',
  `is_persist` tinyint(3) NOT NULL DEFAULT 0 COMMENT '保留数据0-否 1-是 不允许删除',
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='租户模块';

/*Data for the table `base_tentant_modules` */

/*Table structure for table `base_user` */

DROP TABLE IF EXISTS `base_user`;

CREATE TABLE `base_user` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '登陆账号',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(50) DEFAULT NULL COMMENT '手机号',
  `user_type` varchar(20) DEFAULT 'normal' COMMENT '用户类型:super-超级管理员 normal-普通管理员',
  `company_id` bigint(20) DEFAULT NULL COMMENT '企业ID',
  `user_desc` varchar(255) DEFAULT '' COMMENT '描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(3) DEFAULT 1 COMMENT '状态:0-禁用 1-正常 2-锁定',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='系统用户-管理员信息';

/*Data for the table `base_user` */

insert  into `base_user`(`user_id`,`user_name`,`nick_name`,`avatar`,`email`,`mobile`,`user_type`,`company_id`,`user_desc`,`create_time`,`update_time`,`status`) values 
(521677655146233856,'admin','超级管理员','','515608851@qq.com','','super',NULL,'','2018-12-10 13:20:45',NULL,1),
(557063237640650752,'test','测试用户','','','','normal',NULL,'','2019-03-18 04:50:25',NULL,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
