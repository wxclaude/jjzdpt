ALTER TABLE `tdwbz` ADD COLUMN `dwortd` int DEFAULT 0 COMMENT '0点位 1通道' AFTER `ip`;


ALTER TABLE `tdwbz` ADD COLUMN `dh` varchar(255) COMMENT '报修人电话' AFTER `dwortd`, ADD COLUMN `ip2` varchar(255) COMMENT '确认修复人' AFTER `dh`;