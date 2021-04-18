DROP TABLE IF EXISTS `df_config`;
CREATE TABLE `df_config` (
  `id` int(50) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dfx` varchar(50) DEFAULT NULL COMMENT '打分项',
  `qzb` int(11) DEFAULT NULL COMMENT '权重比',
  `type` int(11) DEFAULT NULL COMMENT '打分项类型 0大项 1子项',
  `parent_id` int(50) DEFAULT NULL COMMENT '父项id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '添加人',
  `isdel` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `df_rule`;
CREATE TABLE `df_rule` (
  `id` int(50) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '添加人',
  `isdel` int(11) DEFAULT '0',
  `dept_ids` varchar(500) DEFAULT NULL COMMENT '单位id，逗号分割',
  `config_ids` varchar(500) DEFAULT NULL COMMENT '打分项id，逗号分割',
  `title` varchar(50) DEFAULT NULL COMMENT '打分规则标题',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `df_record`;
CREATE TABLE `df_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `df_month` varchar(255) DEFAULT NULL COMMENT '打分月份yyyy-MM格式',
  `title` varchar(50) DEFAULT NULL COMMENT '打分标题',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '添加人',
  `isdel` int(11) DEFAULT '0',
  `state` int(11) DEFAULT '0' COMMENT '状态 0未完成  1完成',
  `rule_id` int(11) DEFAULT NULL COMMENT '打分规则id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `df_detail`;
CREATE TABLE `df_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `record_id` int(50) DEFAULT NULL COMMENT 'record表id',
  `dept_id` varchar(50) DEFAULT NULL COMMENT '单位id（防控预警平台）',
  `dept_name` varchar(50) DEFAULT NULL COMMENT '单位名称（防控预警平台）',
  `score` int(11) DEFAULT NULL COMMENT '得分',
  `config_id` int(50) DEFAULT NULL COMMENT '打分子项id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '添加人',
  `parent_config_id` int(11) DEFAULT NULL COMMENT '打分父项id',
  `isdel` int(11) DEFAULT '0',
  `score_qzb` varchar(255) DEFAULT NULL COMMENT '计算权重后的分值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=709 DEFAULT CHARSET=utf8;