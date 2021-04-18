DROP TABLE IF EXISTS `tdwbz`;
CREATE TABLE `tdwbz` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bh` varchar(50) DEFAULT NULL,
  `sn` varchar(50) DEFAULT NULL,
  `bz` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `user_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `tdwbz` ADD INDEX `idx_sn` (`sn`) comment '';