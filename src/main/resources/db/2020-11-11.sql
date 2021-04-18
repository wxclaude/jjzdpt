ALTER TABLE `jq_cj_bhg` ADD COLUMN `jcr` varchar(50) DEFAULT null AFTER `createway`;
ALTER TABLE `jq_cj_bhg` ADD COLUMN `double_check`  int NULL DEFAULT 0 AFTER `jcr`;

DROP TABLE IF EXISTS `jq_cj_xcr`;
CREATE TABLE `jq_cj_xcr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  `dept_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;