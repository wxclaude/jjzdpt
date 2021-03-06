/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.11-log : Database - renrenfast
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`renrenfast` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

USE `renrenfast`;

/*Table structure for table `qrtz_blob_triggers` */

DROP TABLE IF EXISTS `qrtz_blob_triggers`;

CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_blob_triggers` */

/*Table structure for table `qrtz_calendars` */

DROP TABLE IF EXISTS `qrtz_calendars`;

CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_calendars` */

/*Table structure for table `qrtz_cron_triggers` */

DROP TABLE IF EXISTS `qrtz_cron_triggers`;

CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_cron_triggers` */

insert  into `qrtz_cron_triggers`(`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`CRON_EXPRESSION`,`TIME_ZONE_ID`) values ('RenrenScheduler','TASK_1','DEFAULT','0 0/30 * * * ?','Asia/Shanghai');

/*Table structure for table `qrtz_fired_triggers` */

DROP TABLE IF EXISTS `qrtz_fired_triggers`;

CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_fired_triggers` */

/*Table structure for table `qrtz_job_details` */

DROP TABLE IF EXISTS `qrtz_job_details`;

CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_job_details` */

insert  into `qrtz_job_details`(`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`,`DESCRIPTION`,`JOB_CLASS_NAME`,`IS_DURABLE`,`IS_NONCONCURRENT`,`IS_UPDATE_DATA`,`REQUESTS_RECOVERY`,`JOB_DATA`) values ('RenrenScheduler','TASK_1','DEFAULT',NULL,'io.renren.modules.job.utils.ScheduleJob','0','0','0','0','??\0sr\0org.quartz.JobDataMap????????\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap?????](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap?.?(v\n?\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap???`?\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\rJOB_PARAM_KEYsr\0.io.renren.modules.job.entity.ScheduleJobEntity\0\0\0\0\0\0\0\0L\0beanNamet\0Ljava/lang/String;L\0\ncreateTimet\0Ljava/util/Date;L\0cronExpressionq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0paramsq\0~\0	L\0remarkq\0~\0	L\0statust\0Ljava/lang/Integer;xpt\0testTasksr\0java.util.Datehj?KYt\0\0xpw\0\0p?y?Xxt\00 0/30 * * * ?sr\0java.lang.Long;?????#?\0J\0valuexr\0java.lang.Number??????\0\0xp\0\0\0\0\0\0\0t\0renrent\0????????????sr\0java.lang.Integer??????8\0I\0valuexq\0~\0\0\0\0\0x\0');

/*Table structure for table `qrtz_locks` */

DROP TABLE IF EXISTS `qrtz_locks`;

CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_locks` */

insert  into `qrtz_locks`(`SCHED_NAME`,`LOCK_NAME`) values ('RenrenScheduler','STATE_ACCESS'),('RenrenScheduler','TRIGGER_ACCESS');

/*Table structure for table `qrtz_paused_trigger_grps` */

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;

CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_paused_trigger_grps` */

/*Table structure for table `qrtz_scheduler_state` */

DROP TABLE IF EXISTS `qrtz_scheduler_state`;

CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_scheduler_state` */

insert  into `qrtz_scheduler_state`(`SCHED_NAME`,`INSTANCE_NAME`,`LAST_CHECKIN_TIME`,`CHECKIN_INTERVAL`) values ('RenrenScheduler','WIN--20191104XL1584416613128',1584430076797,15000),('RenrenScheduler','WIN--20191104XL1584426562653',1584430079869,15000);

/*Table structure for table `qrtz_simple_triggers` */

DROP TABLE IF EXISTS `qrtz_simple_triggers`;

CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_simple_triggers` */

/*Table structure for table `qrtz_simprop_triggers` */

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;

CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_simprop_triggers` */

/*Table structure for table `qrtz_triggers` */

DROP TABLE IF EXISTS `qrtz_triggers`;

CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_triggers` */

insert  into `qrtz_triggers`(`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`JOB_NAME`,`JOB_GROUP`,`DESCRIPTION`,`NEXT_FIRE_TIME`,`PREV_FIRE_TIME`,`PRIORITY`,`TRIGGER_STATE`,`TRIGGER_TYPE`,`START_TIME`,`END_TIME`,`CALENDAR_NAME`,`MISFIRE_INSTR`,`JOB_DATA`) values ('RenrenScheduler','TASK_1','DEFAULT','TASK_1','DEFAULT',NULL,1584430200000,1584428400000,5,'WAITING','CRON',1584230208000,0,NULL,2,'??\0sr\0org.quartz.JobDataMap????????\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap?????](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap?.?(v\n?\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap???`?\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\rJOB_PARAM_KEYsr\0.io.renren.modules.job.entity.ScheduleJobEntity\0\0\0\0\0\0\0\0L\0beanNamet\0Ljava/lang/String;L\0\ncreateTimet\0Ljava/util/Date;L\0cronExpressionq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0paramsq\0~\0	L\0remarkq\0~\0	L\0statust\0Ljava/lang/Integer;xpt\0testTasksr\0java.util.Datehj?KYt\0\0xpw\0\0p?y?Xxt\00 0/30 * * * ?sr\0java.lang.Long;?????#?\0J\0valuexr\0java.lang.Number??????\0\0xp\0\0\0\0\0\0\0t\0renrent\0????????????sr\0java.lang.Integer??????8\0I\0valuexq\0~\0\0\0\0\0x\0');

/*Table structure for table `schedule_job` */

DROP TABLE IF EXISTS `schedule_job`;

CREATE TABLE `schedule_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '??????id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean??????',
  `params` varchar(2000) DEFAULT NULL COMMENT '??????',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron?????????',
  `status` tinyint(4) DEFAULT NULL COMMENT '????????????  0?????????  1?????????',
  `remark` varchar(255) DEFAULT NULL COMMENT '??????',
  `create_time` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='????????????';

/*Data for the table `schedule_job` */

insert  into `schedule_job`(`job_id`,`bean_name`,`params`,`cron_expression`,`status`,`remark`,`create_time`) values (1,'testTask','renren','0 0/30 * * * ?',0,'????????????','2020-03-15 07:55:51');

/*Table structure for table `schedule_job_log` */

DROP TABLE IF EXISTS `schedule_job_log`;

CREATE TABLE `schedule_job_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '????????????id',
  `job_id` bigint(20) NOT NULL COMMENT '??????id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean??????',
  `params` varchar(2000) DEFAULT NULL COMMENT '??????',
  `status` tinyint(4) NOT NULL COMMENT '????????????    0?????????    1?????????',
  `error` varchar(2000) DEFAULT NULL COMMENT '????????????',
  `times` int(11) NOT NULL COMMENT '??????(???????????????)',
  `create_time` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`log_id`),
  KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COMMENT='??????????????????';

/*Data for the table `schedule_job_log` */

insert  into `schedule_job_log`(`log_id`,`job_id`,`bean_name`,`params`,`status`,`error`,`times`,`create_time`) values (1,1,'testTask','renren',0,NULL,1,'2020-03-15 08:00:00'),(2,1,'testTask','renren',0,NULL,5,'2020-03-15 08:30:00'),(3,1,'testTask','renren',0,NULL,1,'2020-03-15 09:00:00'),(4,1,'testTask','renren',0,NULL,1,'2020-03-15 09:30:00'),(5,1,'testTask','renren',0,NULL,1,'2020-03-15 10:00:00'),(6,1,'testTask','renren',0,NULL,1,'2020-03-15 11:00:00'),(7,1,'testTask','renren',0,NULL,0,'2020-03-15 11:30:00'),(8,1,'testTask','renren',0,NULL,1,'2020-03-15 12:00:00'),(9,1,'testTask','renren',0,NULL,3,'2020-03-15 12:30:00'),(10,1,'testTask','renren',0,NULL,0,'2020-03-15 13:00:00'),(11,1,'testTask','renren',0,NULL,1,'2020-03-15 13:30:00'),(12,1,'testTask','renren',0,NULL,0,'2020-03-16 09:30:00'),(13,1,'testTask','renren',0,NULL,1,'2020-03-16 10:00:00'),(14,1,'testTask','renren',0,NULL,0,'2020-03-16 10:30:00'),(15,1,'testTask','renren',0,NULL,1,'2020-03-16 11:00:00'),(16,1,'testTask','renren',0,NULL,2,'2020-03-16 11:30:00'),(17,1,'testTask','renren',0,NULL,2,'2020-03-16 12:00:00'),(18,1,'testTask','renren',0,NULL,5,'2020-03-16 12:30:00'),(19,1,'testTask','renren',0,NULL,50,'2020-03-16 13:00:00'),(20,1,'testTask','renren',0,NULL,10,'2020-03-16 13:30:00'),(21,1,'testTask','renren',0,NULL,2,'2020-03-16 14:00:00'),(22,1,'testTask','renren',0,NULL,10,'2020-03-16 14:30:00'),(23,1,'testTask','renren',0,NULL,6,'2020-03-16 15:00:00'),(24,1,'testTask','renren',0,NULL,1,'2020-03-16 15:30:00'),(25,1,'testTask','renren',0,NULL,2,'2020-03-16 16:30:00'),(26,1,'testTask','renren',0,NULL,5,'2020-03-16 17:00:00'),(27,1,'testTask','renren',0,NULL,2,'2020-03-16 17:30:00'),(28,1,'testTask','renren',0,NULL,2,'2020-03-16 18:00:00'),(29,1,'testTask','renren',0,NULL,2,'2020-03-16 18:30:00'),(30,1,'testTask','renren',0,NULL,2,'2020-03-16 19:00:00'),(31,1,'testTask','renren',0,NULL,7,'2020-03-16 19:30:00'),(32,1,'testTask','renren',0,NULL,2,'2020-03-16 20:00:00'),(33,1,'testTask','renren',0,NULL,2,'2020-03-16 20:30:00'),(34,1,'testTask','renren',0,NULL,1,'2020-03-16 21:00:00'),(35,1,'testTask','renren',0,NULL,1,'2020-03-17 09:00:00'),(36,1,'testTask','renren',0,NULL,1,'2020-03-17 09:30:00'),(37,1,'testTask','renren',0,NULL,1,'2020-03-17 10:00:00'),(38,1,'testTask','renren',0,NULL,0,'2020-03-17 10:30:00'),(39,1,'testTask','renren',0,NULL,1,'2020-03-17 11:00:00'),(40,1,'testTask','renren',0,NULL,2,'2020-03-17 11:30:00'),(41,1,'testTask','renren',0,NULL,1,'2020-03-17 12:00:00'),(42,1,'testTask','renren',0,NULL,2,'2020-03-17 12:30:00'),(43,1,'testTask','renren',0,NULL,2,'2020-03-17 13:00:00'),(44,1,'testTask','renren',0,NULL,1,'2020-03-17 13:30:00'),(45,1,'testTask','renren',0,NULL,50,'2020-03-17 14:00:00'),(46,1,'testTask','renren',0,NULL,33,'2020-03-17 14:30:00'),(47,1,'testTask','renren',0,NULL,27,'2020-03-17 15:00:00');

/*Table structure for table `sys_captcha` */

DROP TABLE IF EXISTS `sys_captcha`;

CREATE TABLE `sys_captcha` (
  `uuid` char(36) NOT NULL COMMENT 'uuid',
  `code` varchar(6) NOT NULL COMMENT '?????????',
  `expire_time` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='???????????????';

/*Data for the table `sys_captcha` */

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) DEFAULT NULL COMMENT 'key',
  `param_value` varchar(2000) DEFAULT NULL COMMENT 'value',
  `status` tinyint(4) DEFAULT '1' COMMENT '??????   0?????????   1?????????',
  `remark` varchar(500) DEFAULT NULL COMMENT '??????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `param_key` (`param_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='?????????????????????';

/*Data for the table `sys_config` */

insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (1,'CLOUD_STORAGE_CONFIG_KEY','{\"aliyunAccessKeyId\":\"\",\"aliyunAccessKeySecret\":\"\",\"aliyunBucketName\":\"\",\"aliyunDomain\":\"\",\"aliyunEndPoint\":\"\",\"aliyunPrefix\":\"\",\"qcloudBucketName\":\"\",\"qcloudDomain\":\"\",\"qcloudPrefix\":\"\",\"qcloudSecretId\":\"\",\"qcloudSecretKey\":\"\",\"qiniuAccessKey\":\"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ\",\"qiniuBucketName\":\"ios-app\",\"qiniuDomain\":\"http://7xqbwh.dl1.z0.glb.clouddn.com\",\"qiniuPrefix\":\"upload\",\"qiniuSecretKey\":\"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV\",\"type\":1}',0,'?????????????????????');

/*Table structure for table `sys_log` */

DROP TABLE IF EXISTS `sys_log`;

CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '?????????',
  `operation` varchar(50) DEFAULT NULL COMMENT '????????????',
  `method` varchar(200) DEFAULT NULL COMMENT '????????????',
  `params` varchar(5000) DEFAULT NULL COMMENT '????????????',
  `time` bigint(20) NOT NULL COMMENT '????????????(??????)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP??????',
  `create_date` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='????????????';

/*Data for the table `sys_log` */

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '?????????ID??????????????????0',
  `name` varchar(50) DEFAULT NULL COMMENT '????????????',
  `url` varchar(200) DEFAULT NULL COMMENT '??????URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '??????(??????????????????????????????user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '??????   0?????????   1?????????   2?????????',
  `icon` varchar(50) DEFAULT NULL COMMENT '????????????',
  `order_num` int(11) DEFAULT NULL COMMENT '??????',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COMMENT='????????????';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`parent_id`,`name`,`url`,`perms`,`type`,`icon`,`order_num`) values (1,0,'????????????',NULL,NULL,0,'system',0),(2,1,'???????????????','sys/user',NULL,1,'admin',1),(3,1,'????????????','sys/role',NULL,1,'role',2),(4,1,'????????????','sys/menu',NULL,1,'menu',3),(5,1,'SQL??????','http://localhost:8080/renren-fast/druid/sql.html',NULL,1,'sql',4),(6,1,'????????????','job/schedule',NULL,1,'job',5),(7,6,'??????',NULL,'sys:schedule:list,sys:schedule:info',2,NULL,0),(8,6,'??????',NULL,'sys:schedule:save',2,NULL,0),(9,6,'??????',NULL,'sys:schedule:update',2,NULL,0),(10,6,'??????',NULL,'sys:schedule:delete',2,NULL,0),(11,6,'??????',NULL,'sys:schedule:pause',2,NULL,0),(12,6,'??????',NULL,'sys:schedule:resume',2,NULL,0),(13,6,'????????????',NULL,'sys:schedule:run',2,NULL,0),(14,6,'????????????',NULL,'sys:schedule:log',2,NULL,0),(15,2,'??????',NULL,'sys:user:list,sys:user:info',2,NULL,0),(16,2,'??????',NULL,'sys:user:save,sys:role:select',2,NULL,0),(17,2,'??????',NULL,'sys:user:update,sys:role:select',2,NULL,0),(18,2,'??????',NULL,'sys:user:delete',2,NULL,0),(19,3,'??????',NULL,'sys:role:list,sys:role:info',2,NULL,0),(20,3,'??????',NULL,'sys:role:save,sys:menu:list',2,NULL,0),(21,3,'??????',NULL,'sys:role:update,sys:menu:list',2,NULL,0),(22,3,'??????',NULL,'sys:role:delete',2,NULL,0),(23,4,'??????',NULL,'sys:menu:list,sys:menu:info',2,NULL,0),(24,4,'??????',NULL,'sys:menu:save,sys:menu:select',2,NULL,0),(25,4,'??????',NULL,'sys:menu:update,sys:menu:select',2,NULL,0),(26,4,'??????',NULL,'sys:menu:delete',2,NULL,0),(27,1,'????????????','sys/config','sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete',1,'config',6),(29,1,'????????????','sys/log','sys:log:list',1,'log',7),(30,1,'????????????','oss/oss','sys:oss:all',1,'oss',6);

/*Table structure for table `sys_oss` */

DROP TABLE IF EXISTS `sys_oss`;

CREATE TABLE `sys_oss` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) DEFAULT NULL COMMENT 'URL??????',
  `create_date` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='????????????';

/*Data for the table `sys_oss` */

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '????????????',
  `remark` varchar(100) DEFAULT NULL COMMENT '??????',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '?????????ID',
  `create_time` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='??????';

/*Data for the table `sys_role` */

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '??????ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '??????ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='???????????????????????????';

/*Data for the table `sys_role_menu` */

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '?????????',
  `password` varchar(100) DEFAULT NULL COMMENT '??????',
  `salt` varchar(20) DEFAULT NULL COMMENT '???',
  `email` varchar(100) DEFAULT NULL COMMENT '??????',
  `mobile` varchar(100) DEFAULT NULL COMMENT '?????????',
  `status` tinyint(4) DEFAULT NULL COMMENT '??????  0?????????   1?????????',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '?????????ID',
  `create_time` datetime DEFAULT NULL COMMENT '????????????',
  `name` varchar(50) DEFAULT NULL COMMENT '??????',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='????????????';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`username`,`password`,`salt`,`email`,`mobile`,`status`,`create_user_id`,`create_time`,`name`) values (1,'admin','9ec9750e709431dad22365cabc5c625482e574c74adaebba7dd02f1129e4ce1d','YzcmCZNvbXocrsz9dm8e','root@renren.io','13612345678',1,1,'2016-11-11 11:11:11','????????????');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '??????ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '??????ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='???????????????????????????';

/*Data for the table `sys_user_role` */

/*Table structure for table `sys_user_token` */

DROP TABLE IF EXISTS `sys_user_token`;

CREATE TABLE `sys_user_token` (
  `user_id` bigint(20) NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '????????????',
  `update_time` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='????????????Token';

/*Data for the table `sys_user_token` */

insert  into `sys_user_token`(`user_id`,`token`,`expire_time`,`update_time`) values (1,'7bae69cf2c1f1c2f8858734b66663538','2020-03-18 03:24:24','2020-03-17 15:24:24');

/*Table structure for table `tb_user` */

DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '?????????',
  `mobile` varchar(20) NOT NULL COMMENT '?????????',
  `password` varchar(64) DEFAULT NULL COMMENT '??????',
  `create_time` datetime DEFAULT NULL COMMENT '????????????',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='??????';

/*Data for the table `tb_user` */

insert  into `tb_user`(`user_id`,`username`,`mobile`,`password`,`create_time`) values (1,'mark','13612345678','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','2017-03-23 22:37:41');

/*Table structure for table `tzbmrdm` */

DROP TABLE IF EXISTS `tzbmrdm`;

CREATE TABLE `tzbmrdm` (
  `id` int(50) NOT NULL AUTO_INCREMENT,
  `duty_id` varchar(50) DEFAULT NULL,
  `chouqu_time` datetime DEFAULT NULL,
  `chouquren` varchar(50) DEFAULT NULL,
  `police` varchar(50) DEFAULT NULL,
  `djj` varchar(50) DEFAULT NULL,
  `police_code` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT NULL,
  `dept_id` varchar(50) DEFAULT NULL,
  `dm_police` varchar(50) DEFAULT NULL,
  `dm_time` datetime DEFAULT NULL,
  `yd` int(11) DEFAULT '0' COMMENT '0????????? 1?????? 2????????? 3??????',
  `bz` varchar(200) NOT NULL DEFAULT '',
  `ip` varchar(200) DEFAULT NULL,
  `short_phone` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

/*Data for the table `tzbmrdm` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
