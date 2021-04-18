CREATE TABLE `tyjdd` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(50) DEFAULT NULL,
  `zg` int(11) DEFAULT '0' COMMENT '在岗人员',
  `yd` int(11) DEFAULT '0' COMMENT '应答情况',
  `xg` int(11) DEFAULT '0' COMMENT '声音图像',
  `gf` int(11) DEFAULT '0' COMMENT '报告规范',
  `isdel` int(11) DEFAULT '0',
  `bz` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `ddsj` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;