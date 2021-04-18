INSERT INTO `sys_config` VALUES ('5', 'gzts', '\n							<p style=\"font-size: 21px;padding-top: 25px;font-family: 黑体;margin-bottom: 15px;\">\n								【工作提示】\n							</p>\n							<p style=\"font-size: 21px;font-family: 仿宋_GB2312;line-height: 30px; text-indent: 2em;\">\n								请各单位进一步加大安\n								全防范宣传力度，加强对辖区内重点区域、路段、部位的巡逻盘查，提高见警率，全力维护社会治安稳定。\n								<strong>根据市局统一部署要求，全区实行战时勤务。</strong>\n							</p>\n							<p style=\"font-size: 21px;font-family: 仿宋_GB2312;line-height: 30px; text-indent: 2em;\">\n								请各派出所对照《扬州市公安局邗江分局派出所综合指挥室工作规范》的要求，对综合指挥室运行质态进行自查整改，完善配套运作机制 ，严格执行实体化、实战化运作；要严格落实指挥员、警辅力量常态入驻综合指挥室制度。11111</p>\n						', '1', null);

ALTER TABLE `sys_login_log` ADD COLUMN `status`  int NULL DEFAULT 0 COMMENT '0登录成功 1登录失败' AFTER `type`;

ALTER TABLE `zh_mrtb` ADD COLUMN `msg_count`  int(255) NULL DEFAULT 0 COMMENT '发送短信次数' AFTER `xsjq99`;

ALTER TABLE `zh_mrtb` ADD COLUMN `msg`  text NULL COMMENT '短信内容' AFTER `msg_count`;

DROP TABLE IF EXISTS `zh_mrtb_dh`;
CREATE TABLE `zh_mrtb_dh` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(50) DEFAULT NULL COMMENT '姓名',
`phone` varchar(50) DEFAULT NULL,
`dept` varchar(50) DEFAULT NULL COMMENT '部门',
`role` varchar(255) DEFAULT NULL COMMENT '职务',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP,
`create_by` varchar(50) DEFAULT NULL,
`isdel` int(255) DEFAULT '0',
`ip` varchar(50) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `zh_mrtb_his`;
CREATE TABLE `zh_mrtb_his` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`content` text,
`phones` text,
`create_by` varchar(50) DEFAULT NULL,
`create_time` datetime DEFAULT CURRENT_TIMESTAMP,
`isdel` int(255) DEFAULT '0',
`mrtb_id` int(11) DEFAULT NULL,
`ip` varchar(50) DEFAULT NULL,
`tbrq` date DEFAULT NULL,
`success_count` int(11) DEFAULT '0' COMMENT '发送成功记录数',
`phone_count` int(11) DEFAULT '0' COMMENT '发送号码数量',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



