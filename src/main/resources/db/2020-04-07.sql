ALTER TABLE `tzbmrdm` ADD COLUMN `report_point_id` varchar(50) AFTER `is_leader`;
ALTER TABLE `sys_log` CHANGE COLUMN `params` `params` longtext DEFAULT NULL COMMENT '请求参数', AUTO_INCREMENT=1;
delete from tsqmj where police_id in('P000096','P000506');