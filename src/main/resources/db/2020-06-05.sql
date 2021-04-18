ALTER TABLE `tydjcj` ADD COLUMN `yddcjlv` int COMMENT '移动端处警率' AFTER `ydjcj_date`;

DROP TABLE IF EXISTS `zh_mrtb_xsjq`;
CREATE TABLE `zh_mrtb_xsjq` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tbid` int(11) DEFAULT NULL,
  `jjsj` varchar(50) DEFAULT NULL,
  `jjdw` varchar(50) DEFAULT NULL,
  `jqlb` varchar(50) DEFAULT NULL,
  `jyaq` text,
  `dw` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;