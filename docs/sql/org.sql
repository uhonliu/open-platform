/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.2.4-MariaDB : Database - open_platform
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `org_company` */

DROP TABLE IF EXISTS `org_company`;

CREATE TABLE `org_company` (
  `company_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '企业ID',
  `company_name` varchar(50) NOT NULL DEFAULT '' COMMENT '企业全称',
  `company_name_en` varchar(50) NOT NULL DEFAULT '' COMMENT '企业英文名',
  `nature_id` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '企业性质ID',
  `industry_id` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '所属行业ID',
  `area_id` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '所在区域ID',
  `established_time` datetime DEFAULT NULL COMMENT '成立时间',
  `registered_capital` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '注册资金',
  `staff_num` int(11) unsigned NOT NULL DEFAULT 0 COMMENT '员工人数',
  `website` varchar(255) NOT NULL DEFAULT '' COMMENT '公司网址',
  `profile` varchar(2000) NOT NULL DEFAULT '' COMMENT '公司介绍',
  `contact` varchar(50) NOT NULL DEFAULT '' COMMENT '联系人',
  `phone` varchar(50) NOT NULL DEFAULT '' COMMENT '电话',
  `fax` varchar(50) NOT NULL DEFAULT '' COMMENT '传真',
  `email` varchar(50) NOT NULL DEFAULT '' COMMENT '电子邮件',
  `address` varchar(200) NOT NULL DEFAULT '' COMMENT '通信地址',
  `post_code` varchar(50) NOT NULL DEFAULT '' COMMENT '邮政编码',
  `logo` varchar(100) NOT NULL DEFAULT '' COMMENT '企业Logo',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '最后修改人',
  UNIQUE KEY `company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='企业信息表';

/*Table structure for table `org_department` */

DROP TABLE IF EXISTS `org_department`;

CREATE TABLE `org_department` (
  `department_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '部门ID',
  `parent_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '上级部门ID',
  `department_code` varchar(50) NOT NULL DEFAULT '' COMMENT '部门代码',
  `department_name` varchar(50) NOT NULL DEFAULT '' COMMENT '部门名称',
  `level` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '部门级别',
  `seq` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  `company_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '所属企业ID',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '最后修改人',
  UNIQUE KEY `department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门信息表';

/*Table structure for table `org_dingtalk` */

DROP TABLE IF EXISTS `org_dingtalk`;

CREATE TABLE `org_dingtalk` (
  `company_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '企业ID',
  `corp_id` varchar(255) NOT NULL DEFAULT '' COMMENT '企业corpid',
  `agentd_id` varchar(255) NOT NULL DEFAULT '' COMMENT '应用的agentdId',
  `app_key` varchar(255) NOT NULL DEFAULT '' COMMENT '应用的AppKey',
  `app_secret` varchar(255) NOT NULL DEFAULT '' COMMENT '应用的AppSecret',
  `encoding_aes_key` varchar(255) NOT NULL DEFAULT '' COMMENT '数据加密密钥',
  `token` varchar(255) NOT NULL DEFAULT '' COMMENT '加解密需要用到的token',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '最后修改人',
  UNIQUE KEY `company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钉钉配置信息';

/*Table structure for table `org_position` */

DROP TABLE IF EXISTS `org_position`;

CREATE TABLE `org_position` (
  `position_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '岗位ID',
  `position_code` varchar(50) NOT NULL DEFAULT '' COMMENT '岗位代码',
  `position_name` varchar(50) NOT NULL DEFAULT '' COMMENT '岗位名称',
  `work_content` varchar(2000) NOT NULL DEFAULT '' COMMENT '工作内容',
  `work_standard` varchar(2000) NOT NULL DEFAULT '' COMMENT '工作标准',
  `responsibility_weight` varchar(2000) NOT NULL DEFAULT '' COMMENT '责任权重',
  `required_qualifications` varchar(2000) NOT NULL DEFAULT '' COMMENT '所需资格条件',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '状态:0-禁用 1-启用',
  `seq` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `department_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '所属部门ID',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '最后修改人',
  UNIQUE KEY `position_id` (`position_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='岗位体系表';

/*Table structure for table `org_user` */

DROP TABLE IF EXISTS `org_user`;

CREATE TABLE `org_user` (
  `user_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '人员ID',
  `parent_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '上级ID',
  `position_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '岗位ID',
  `dd_userid` varchar(50) NOT NULL DEFAULT '' COMMENT '员工在当前企业内的唯一标识',
  `unionid` varchar(255) NOT NULL DEFAULT '' COMMENT '员工在当前开发者企业账号范围内的唯一标识',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '员工名字',
  `tel` varchar(20) NOT NULL DEFAULT '' COMMENT '分机号（仅限企业内部开发调用）',
  `work_place` varchar(255) NOT NULL DEFAULT '' COMMENT '办公地点',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `mobile` varchar(15) NOT NULL DEFAULT '' COMMENT '手机号码',
  `email` varchar(255) NOT NULL DEFAULT '' COMMENT '员工的电子邮箱',
  `org_email` varchar(255) NOT NULL DEFAULT '' COMMENT '员工的企业邮箱',
  `active` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '是否已经激活:1已激活，0未激活',
  `department` varchar(255) NOT NULL DEFAULT '' COMMENT '成员所属部门id列表',
  `position` varchar(50) NOT NULL DEFAULT '' COMMENT '职位信息',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '头像url',
  `hired_date` datetime DEFAULT NULL COMMENT '入职时间',
  `jobnumber` varchar(255) NOT NULL DEFAULT '' COMMENT '员工工号',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT '最后修改人',
  UNIQUE KEY `userid` (`dd_userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员信息表（钉钉）';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
