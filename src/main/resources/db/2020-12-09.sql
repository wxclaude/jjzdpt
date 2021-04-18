ALTER TABLE `zh_mrtb`
    ADD COLUMN `published`  int(11) NULL DEFAULT 0 COMMENT '是否发布 0否 1是' AFTER `msg`;

ALTER TABLE `zh_mrtb`
    ADD COLUMN `xx_id`  int(11) NULL COMMENT 't_xx表id' AFTER `published`;

ALTER TABLE `zh_mrtb`
    ADD COLUMN `publish_time`  datetime NULL COMMENT '发布日期' AFTER `xx_id`,
    ADD COLUMN `publish_ip`  varchar(255) NULL DEFAULT NULL AFTER `publish_time`;

ALTER TABLE `zh_mrtb`
    ADD COLUMN `publish_content`  text NULL COMMENT '发布内容' AFTER `publish_ip`;



