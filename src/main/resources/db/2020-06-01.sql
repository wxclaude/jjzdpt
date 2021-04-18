DROP TABLE IF EXISTS `tzbwbz`;
CREATE TABLE `tzbwbz` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `zbdate` date DEFAULT NULL,
  `dept_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `zh_mrtb`;
CREATE TABLE `zh_mrtb` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fbrq` date DEFAULT NULL COMMENT '发布日期',
  `tbrq` date DEFAULT NULL COMMENT '通报日期',
  `qs` varchar(50) DEFAULT NULL COMMENT '期数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `path` varchar(255) DEFAULT NULL COMMENT '文件路径',
  `chenbanren` varchar(50) DEFAULT NULL COMMENT '承办人',
  `shenheren` varchar(50) DEFAULT NULL COMMENT '审核人',
  `comment1` text,
  `comment2` text,
  `comment3` text,
  `comment4` text,
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;