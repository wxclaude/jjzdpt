-- 修改tdwbz表 添加xjid字段 varchar(50)

ALTER TABLE `txl` ADD COLUMN `bz` varchar(255) COMMENT ''备注'' AFTER `xl_sb_id`;