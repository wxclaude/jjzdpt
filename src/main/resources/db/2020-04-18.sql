DROP TABLE IF EXISTS `tdk`;
CREATE TABLE `tdk` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sb_id` varchar(50) DEFAULT NULL,
  `dklx` varchar(50) DEFAULT NULL,
  `bkh` varchar(50) DEFAULT NULL,
  `dkh` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_sbid` (`sb_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `txl`;
CREATE TABLE `txl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `xlbh` varchar(50) DEFAULT NULL,
  `sl_dk_id` int(11) DEFAULT NULL,
  `xl_dk_id` int(11) DEFAULT NULL,
  `ljlx` varchar(50) DEFAULT NULL,
  `vlan` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  `sl_sb_id` varchar(50) DEFAULT NULL,
  `xl_sb_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;