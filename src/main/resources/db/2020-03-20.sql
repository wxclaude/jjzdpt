ALTER TABLE `tzbmrdm`
MODIFY COLUMN `djj`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' AFTER `police`;

update tzbmrdm set djj ='' where djj is null;

DROP TABLE IF EXISTS `tyjdm`;
CREATE TABLE `tyjdm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT NULL,
  `yd` int(11) NOT NULL DEFAULT '0' COMMENT '1应答 2未应答',
  `dm_time` datetime DEFAULT NULL,
  `dm_police` varchar(50) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tdept`;
CREATE TABLE `tdept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(50) COLLATE utf8_bin DEFAULT '',
  `dept_code` varchar(50) COLLATE utf8_bin DEFAULT '',
  `dept_name` varchar(50) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of tdept
-- ----------------------------
INSERT INTO `tdept` VALUES ('1', 'D0001', '', '邗江分局');
INSERT INTO `tdept` VALUES ('2', 'D0055', '', ' 办公室');
INSERT INTO `tdept` VALUES ('3', 'D0048', '', '指挥中心');
INSERT INTO `tdept` VALUES ('4', 'D0050', '', '巡特警大队');
INSERT INTO `tdept` VALUES ('5', 'D0018', '', '刑警大队');
INSERT INTO `tdept` VALUES ('6', 'D0011', '', '治安大队');
INSERT INTO `tdept` VALUES ('7', 'D0032', '', '交警大队');
INSERT INTO `tdept` VALUES ('8', 'D0057', '', '督察大队');
INSERT INTO `tdept` VALUES ('9', 'D0047', '', '法制大队');
INSERT INTO `tdept` VALUES ('10', 'D0044', '', '汊河交警中队');
INSERT INTO `tdept` VALUES ('11', 'D0038', '', '公道交警中队');
INSERT INTO `tdept` VALUES ('12', 'D0037', '', '瓜州交警中队');
INSERT INTO `tdept` VALUES ('13', 'D0039', '', '甘泉交警中队');
INSERT INTO `tdept` VALUES ('14', 'D0040', '', '方巷交警中队');
INSERT INTO `tdept` VALUES ('15', 'D0059', '', '邗上派出所');
INSERT INTO `tdept` VALUES ('16', 'D0068', '', '汊河派出所');
INSERT INTO `tdept` VALUES ('17', 'D0078', '', '瓜洲派出所');
INSERT INTO `tdept` VALUES ('18', 'D0086', '', '蒋王派出所');
INSERT INTO `tdept` VALUES ('19', 'D0095', '', '杨庙派出所');
INSERT INTO `tdept` VALUES ('20', 'D0102', '', '槐泗派出所');
INSERT INTO `tdept` VALUES ('21', 'D0112', '', '方巷派出所');
INSERT INTO `tdept` VALUES ('22', 'D0122', '', '公道派出所');
INSERT INTO `tdept` VALUES ('23', 'D0173', '', '杨寿派出所');
INSERT INTO `tdept` VALUES ('24', 'D0181', '', '四季园派出所');
INSERT INTO `tdept` VALUES ('25', 'D0191', '', '念四桥派出所');
INSERT INTO `tdept` VALUES ('26', 'D0209', '', '竹西派出所');
INSERT INTO `tdept` VALUES ('27', 'D0220', '', '西湖派出所');
INSERT INTO `tdept` VALUES ('28', 'D0238', '', '江阳派出所');
INSERT INTO `tdept` VALUES ('29', 'D0245', '', '甘泉派出所');
INSERT INTO `tdept` VALUES ('30', 'D00003', '', '新盛派出所');
