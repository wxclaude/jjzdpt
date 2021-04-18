package io.renren.modules.xscz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@TableName("zh_xscz")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZhXsczEntity implements Serializable {

  @TableId(type = IdType.UUID)
  private String id;
  private Integer type;
  private String deptId;
  private String xsnr;
  private String czsd;
  private String jcmj;
  private String jcsj;
  private String jcdz;
  private String bjcdx;
  private Integer sffxsxhjwfxw;
  private String xcfzr;
  private String xcfzrdh;
  private Integer sfydzf;
  private Integer sfsyzfjyl;
  private String xcjcnr;
  private String jcjl;
  private Integer jcjg;
  private String fwdx;
  private String fwdxdh;
  private String lxsj;
  private Integer fwdxsfmy;
  private String fwdxgtxxqk;
  private Date xfsj;
  private Date czsj;
  private Date fksj;
  private Integer czzt;
  private Date createTime;
  private String createBy;
  @TableLogic
  private int isdel;

  @TableField(exist = false)
  private String deptName;

  @TableField(exist = false)
  private List<ZhXsczFjEntity> zhXsczFjList;

  @TableField(exist = false)
  private List<ZhXsczFjEntity> fkFjList;

  private String ip1;
  private String ip2;

  private int sfcs;

  private long cssj;

  private String sjhdd;

  private Date qssj;
  private Date zhzxqssj;

}
