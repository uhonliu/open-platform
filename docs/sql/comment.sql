/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.2.4-MariaDB : Database - open_platform_comment
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `bsd_comment` */

DROP TABLE IF EXISTS `bsd_comment`;

CREATE TABLE `bsd_comment` (
  `comment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `topic_id` bigint(20) NOT NULL COMMENT '主题ID(商品,课程,活动ID)',
  `topic_name` varchar(32) NOT NULL COMMENT '主题名称(商品,课程,活动名称)',
  `topic_type` varchar(20) NOT NULL COMMENT '主题类型',
  `topic_sub_type` varchar(20) NOT NULL COMMENT '主题子类型',
  `user_id` bigint(20) NOT NULL COMMENT '评论用户ID',
  `user_name` varchar(20) NOT NULL COMMENT '用户名',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `source` tinyint(3) DEFAULT 4 COMMENT '来源 1.客户端APP 2.PC 3.WAP 4.unknow',
  `status` tinyint(3) DEFAULT 1 COMMENT '状态 1.未审核 2.未回复 3.已回复 4.已屏蔽',
  `is_top` bit(1) DEFAULT b'0' COMMENT '是否置顶 0.否 1.是',
  `un_like_num` int(11) DEFAULT 0 COMMENT '点踩数',
  `like_num` int(11) DEFAULT 0 COMMENT '点赞数',
  `reply_num` int(11) DEFAULT 0 COMMENT '回复数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`comment_id`),
  KEY `topic_type` (`topic_type`,`user_name`,`create_time`) USING BTREE,
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

/*Table structure for table `bsd_comment_reply` */

DROP TABLE IF EXISTS `bsd_comment_reply`;

CREATE TABLE `bsd_comment_reply` (
  `reply_id` bigint(20) NOT NULL COMMENT '评论回复ID',
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `parent_id` bigint(20) NOT NULL COMMENT '上级回复ID',
  `from_user_id` bigint(20) NOT NULL COMMENT '回复者ID',
  `from_user_name` varchar(20) NOT NULL COMMENT '回复者的名字',
  `content` varchar(500) NOT NULL COMMENT '回复内容',
  `to_user_id` bigint(20) NOT NULL COMMENT '回复目标ID',
  `is_shield` bit(1) DEFAULT b'0' COMMENT '是否屏蔽 0.不屏蔽 1.屏蔽',
  `is_author` tinyint(2) DEFAULT 1 COMMENT '是否后台回复 1.普通回复  2.平台回复',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`reply_id`),
  KEY `reply_index` (`comment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论回复表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
