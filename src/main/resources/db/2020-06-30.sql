ALTER TABLE `tdwbz` ADD COLUMN `type` varchar(50) COMMENT 'type 0指挥中心 1自建 2交警  3教育 4小区 5内部 6其它单位 7人脸' AFTER `xjid`, ADD COLUMN `category` varchar(50) COMMENT '异常原因 0画面树木遮挡 1监控角度异常 2设备离线 3画面异常 4录像异常 5其它' AFTER `type`, ADD COLUMN `state` int DEFAULT 0 COMMENT '状态 0未修复 1已修复' AFTER `category`;

ALTER TABLE `tdwbz` ADD COLUMN `czqk` varchar(255) AFTER `state`;

ALTER TABLE `tdwbz` ADD COLUMN `repair_time` varchar(50) AFTER `czqk`;