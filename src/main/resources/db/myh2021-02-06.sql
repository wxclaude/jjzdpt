CREATE TABLE `myh_news` (
                            `id` varchar(50) NOT NULL,
                            `title` varchar(255) DEFAULT NULL,
                            `type` int(11) DEFAULT '0',
                            `content` text,
                            `img` varchar(255) DEFAULT NULL,
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `create_by` varchar(50) DEFAULT NULL,
                            `isdel` int(11) DEFAULT '0',
                            `news_time` datetime DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `myh_news`
    ADD COLUMN `s1`  varchar(255) NULL AFTER `news_time`,
    ADD COLUMN `s2`  varchar(255) NULL AFTER `s1`,
    ADD COLUMN `s3`  varchar(255) NULL AFTER `s2`,
    ADD COLUMN `s4`  varchar(255) NULL AFTER `s3`;

ALTER TABLE `myh_news`
    ADD COLUMN `s5`  varchar(255) NULL AFTER `s4`,
    ADD COLUMN `s6`  varchar(255) NULL AFTER `s5`,
    ADD COLUMN `s7`  varchar(255) NULL AFTER `s6`,
    ADD COLUMN `s8`  varchar(255) NULL AFTER `s7`,
    ADD COLUMN `s9`  varchar(255) NULL AFTER `s8`;
