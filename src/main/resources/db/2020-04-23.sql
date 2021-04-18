DROP TABLE IF EXISTS `txmconfig`;
CREATE TABLE `txmconfig` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dw` varchar(50) DEFAULT NULL COMMENT '项目对应单位 0指挥中心 1自建 2交警 3教育 4小区 5内部 6其它',
  `xmid` varchar(255) DEFAULT NULL COMMENT 'tv_xm表id，多个用,分隔',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `xmmc` varchar(50) DEFAULT NULL COMMENT '项目名称，用于展示',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `isdel` int(11) DEFAULT NULL COMMENT '删除标示 0正常 1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `txmconfig`
-- ----------------------------
BEGIN;
INSERT INTO `txmconfig` VALUES ('1', '0', 'V201708150001', '2020-04-22 11:18:00', '飞飞', '天网一期', '1', '0'), ('2', '0', 'V201708150002,V201708150003', '2020-04-22 11:18:49', '飞飞', '天网二期', '2', '0'), ('3', '0', 'V201708150004', '2020-04-22 11:19:22', '飞飞', '天网三期', '3', '0'), ('4', '0', 'V201710050001', '2020-04-22 11:25:42', '飞飞', '天网四期', '4', '0'), ('5', '0', 'V201904150001', '2020-04-22 11:25:56', '飞飞', '天网五期', '5', '0'), ('6', '0', 'V201804110001', '2020-04-22 11:26:23', '飞飞', '人脸', '6', '0'), ('9', '0', 'V201902270001', '2020-04-22 11:29:57', '飞飞', '高清项目', '7', '0'), ('10', '3', 'V202003280002', '2020-04-22 15:53:27', '飞飞', '教育监控', '0', '0'), ('11', '6', 'V201709100003,V201709100004', '2020-04-23 10:51:43', '飞飞', '其他单位', '0', '0');
COMMIT;