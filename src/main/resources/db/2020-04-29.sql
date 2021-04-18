CREATE TABLE [tV_RWDK] (
	[id] int IDENTITY(1,1) NOT NULL,
	[sb_id] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[dklx] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[bkh] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[dkh] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[create_time] datetime NULL DEFAULT (getdate()),
	[create_by] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[isdel] int NULL DEFAULT ((0))
)
ON [PRIMARY]
GO


-- ----------------------------
--  Primary key structure for table tV_RWDK
-- ----------------------------
ALTER TABLE [tV_RWDK] ADD
	CONSTRAINT [PK__tdk__3213E83F73BA3083] PRIMARY KEY CLUSTERED ([id])
	WITH (PAD_INDEX = OFF,
		IGNORE_DUP_KEY = OFF,
		ALLOW_ROW_LOCKS = ON,
		ALLOW_PAGE_LOCKS = ON)
	ON [default]
GO

CREATE TABLE [tV_RWXL] (
	[id] int IDENTITY(1,1) NOT NULL,
	[xlbh] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[sl_dk_id] int NULL,
	[xl_dk_id] int NULL,
	[ljlx] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[vlan] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[create_time] datetime NULL DEFAULT (getdate()),
	[create_by] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[isdel] int NULL DEFAULT ((0)),
	[sl_sb_id] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[xl_sb_id] varchar(50) COLLATE Chinese_PRC_CI_AS NULL,
	[bz] varchar(255) COLLATE Chinese_PRC_CI_AS NULL
)
ON [PRIMARY]
GO


-- ----------------------------
--  Primary key structure for table tV_RWXL
-- ----------------------------
ALTER TABLE [tV_RWXL] ADD
	CONSTRAINT [PK__txl__3213E83F797309D9] PRIMARY KEY CLUSTERED ([id])
	WITH (PAD_INDEX = OFF,
		IGNORE_DUP_KEY = OFF,
		ALLOW_ROW_LOCKS = ON,
		ALLOW_PAGE_LOCKS = ON)
	ON [default]
GO