CREATE TABLE `zh_mrtb_view` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(50) DEFAULT NULL,
  `tbrq` date DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


ALTER TABLE `zh_mrtb_xsjq` ADD COLUMN `jjbh` varchar(50) AFTER `isdel`, ADD COLUMN `cjbh` varchar(50) AFTER `jjbh`;