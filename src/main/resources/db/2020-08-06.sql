CREATE TABLE `zh_mrtz` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `isdel` int(11) DEFAULT '0',
  `path` varchar(255) DEFAULT NULL,
  `tz_date` date DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `uid` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;


CREATE TABLE `zh_xscz` (
  `id` varchar(50) NOT NULL,
  `type` int(11) DEFAULT '0' COMMENT '0污染 1城管',
  `dept_id` varchar(50) DEFAULT NULL,
  `xsnr` text COMMENT '线索内容',
  `czsd` varchar(50) DEFAULT '' COMMENT '处置所对',
  `jcmj` varchar(50) DEFAULT NULL COMMENT '检查民警',
  `jcsj` varchar(255) DEFAULT NULL COMMENT '检查时间',
  `jcdz` varchar(255) DEFAULT NULL COMMENT '检查地址',
  `bjcdx` varchar(50) DEFAULT NULL COMMENT '被检查对象',
  `sffxsxhjwfxw` int(11) DEFAULT NULL COMMENT '是否发现涉嫌环境违法行为 0否  1是',
  `xcfzr` varchar(50) DEFAULT NULL COMMENT '现场负责人',
  `xcfzrdh` varchar(255) DEFAULT NULL COMMENT '现场负责人电话',
  `sfydzf` int(11) DEFAULT NULL COMMENT '是否移动执法 0否1是',
  `sfsyzfjyl` int(11) DEFAULT NULL COMMENT '是否使用执法记录仪 0否1是',
  `xcjcnr` text COMMENT '现场检查内容',
  `jcjl` text COMMENT '检查结论',
  `jcjg` int(11) DEFAULT NULL COMMENT '检查结果 0不属实 1已解决 2受理行政案件',
  `fwdx` varchar(50) DEFAULT NULL COMMENT '服务对象',
  `fwdxdh` varchar(50) DEFAULT NULL COMMENT '服务对象电话',
  `lxsj` varchar(255) DEFAULT NULL COMMENT '联系时间',
  `fwdxsfmy` int(11) DEFAULT NULL COMMENT '0不满意  1满意 2一般 3未接通',
  `fwdxgtxxqk` text COMMENT '服务对象沟通详细情况',
  `xfsj` datetime DEFAULT NULL COMMENT '下发时间',
  `czsj` datetime DEFAULT NULL COMMENT '处置时间',
  `fksj` datetime DEFAULT NULL COMMENT '反馈时间',
  `czzt` int(11) DEFAULT '0' COMMENT '处置状态  0未完成 1完成 2签收',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(255) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  `ip1` varchar(50) DEFAULT NULL,
  `ip2` varchar(50) DEFAULT NULL,
  `sfcs` int(11) DEFAULT '0' COMMENT '是否超时 0否 1是',
  `cssj` int(11) DEFAULT '0' COMMENT '超时时间 单位小时',
  `sjhdd` varchar(255) DEFAULT '' COMMENT '时间或地点',
  `qssj` datetime DEFAULT NULL COMMENT '签收时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `zh_xscz_fj` (
  `id` varchar(50) NOT NULL,
  `xsczid` varchar(50) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(50) DEFAULT NULL,
  `isdel` int(11) DEFAULT '0',
  `type` int(11) DEFAULT '0' COMMENT '0发布附件  0反馈附件',
  `ip` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;