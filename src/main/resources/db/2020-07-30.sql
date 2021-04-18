CREATE TABLE `jq_cj_bhg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cjbh` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `reason` varchar(50) DEFAULT NULL,
  `state` int(11) DEFAULT '0',
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;