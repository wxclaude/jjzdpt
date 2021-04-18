-- ----------------------------
--  Table structure for `tdept`
-- ----------------------------
DROP TABLE IF EXISTS `tdept`;
CREATE TABLE `tdept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(50) COLLATE utf8_bin DEFAULT '',
  `dept_code` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `dept_name` text COLLATE utf8_bin,
  `short_dept_name` varchar(50) COLLATE utf8_bin DEFAULT '',
  `dm` int(11) DEFAULT '0',
  `yjdm` int(11) DEFAULT '0',
  `hydm` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
--  Records of `tdept`
-- ----------------------------
BEGIN;
INSERT INTO `tdept` VALUES ('1', 'D0001', '', '邗江分局', '邗江分局', '0', '0', '0'), ('2', 'D0055', '', ' 办公室', '办公室', '0', '0', '0'), ('3', 'D0048', '', '指挥中心', '指挥中心', '0', '0', '0'), ('5', 'D0018', '', '刑警大队', '刑警', '0', '1', '0'), ('6', 'D0011', '', '治安大队', '治安', '0', '0', '0'), ('7', 'D0032', '', '交警大队', '交警', '0', '1', '1'), ('8', 'D0057', '', '督察大队', '督察', '0', '0', '0'), ('9', 'D0047', '', '法制大队', '法制', '0', '0', '0'), ('100', 'D0059', '', '邗上派出所', '邗上', '0', '1', '1'), ('101', 'D0068', '', '汊河派出所', '汊河', '0', '1', '1'), ('102', 'D0191', '', '念四桥派出所', '念四桥', '0', '1', '1'), ('103', 'D0181', '', '四季园派出所', '四季园', '0', '1', '1'), ('104', 'D0220', '', '西湖派出所', '西湖', '0', '1', '1'), ('105', 'D00003', '', '新盛派出所', '新盛', '0', '1', '1'), ('106', 'D0086', '', '蒋王派出所', '蒋王', '0', '1', '1'), ('107', 'D0238', '', '江阳派出所', '江阳', '0', '1', '1'), ('108', 'D0112', '', '方巷派出所', '方巷', '0', '1', '1'), ('109', 'D0245', '', '甘泉派出所', '甘泉', '0', '1', '1'), ('110', 'D0122', '', '公道派出所', '公道', '0', '1', '1'), ('111', 'D0078', '', '瓜洲派出所', '瓜洲', '0', '1', '1'), ('112', 'D0102', '', '槐泗派出所', '槐泗', '0', '1', '1'), ('113', 'D0209', '', '竹西派出所', '竹西', '0', '1', '1'), ('114', 'D0095', '', '杨庙派出所', '杨庙', '0', '1', '1'), ('115', 'D0173', '', '杨寿派出所', '杨寿', '0', '1', '1'), ('200', 'D0050', '', '巡特警大队', '巡特警', '0', '1', '0'), ('210', 'D0044', '', '汊河交警中队', '汊河中队', '0', '1', '0'), ('211', 'D0040', '', '方巷交警中队', '方巷中队', '0', '1', '0'), ('212', 'D0039', '', '甘泉交警中队', '甘泉中队', '0', '1', '0'), ('213', 'D0038', '', '公道交警中队', '公道中队', '0', '1', '0'), ('214', 'D0037', '', '瓜州交警中队', '瓜州中队', '0', '1', '0');
COMMIT;



DROP TABLE IF EXISTS `thydm`;
CREATE TABLE `thydm` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `title` varchar(255) COLLATE utf8_bin DEFAULT NULL,
 `dept_id` varchar(50) COLLATE utf8_bin DEFAULT NULL,
 `dept_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
 `yd` int(255) DEFAULT '0' COMMENT '1正常应答 2未应答',
 `xg` int(255) DEFAULT '0' COMMENT '1良好 2异常',
 `bz` varchar(255) COLLATE utf8_bin DEFAULT NULL,
 `dm_time` datetime DEFAULT NULL,
 `dm_police` varchar(50) COLLATE utf8_bin DEFAULT NULL,
 `ip` varchar(50) COLLATE utf8_bin DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


ALTER TABLE `tzbmrdm` ADD COLUMN `is_leader` int DEFAULT 0 COMMENT '-1社区民警 0值班民警 1值班领导 2驻所领导' AFTER `short_phone`;

DROP TABLE IF EXISTS `tsqmj`;
CREATE TABLE `tsqmj` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `police` varchar(50) DEFAULT NULL,
  `police_id` varchar(50) DEFAULT NULL,
  `police_code` varchar(50) DEFAULT NULL,
  `djj` varchar(50) DEFAULT NULL,
  `short_phone` varchar(50) DEFAULT NULL,
  `dept_id` varchar(50) DEFAULT NULL,
  `dept_code` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT NULL,
  `is_leader` int(11) DEFAULT '-1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
