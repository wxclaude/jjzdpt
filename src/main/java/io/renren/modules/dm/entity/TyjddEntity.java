package io.renren.modules.dm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("tyjdd")
@Data
public class TyjddEntity implements Serializable {

  @TableId
  private int id;
  private String deptId;
  private Integer zg;
  private Integer yd;
  private Integer xg;
  private Integer gf;
  @TableLogic
  private int isdel;
  private String bz;
  private Date createTime;
  private Date ddsj;
  private String createBy;
  private String ip;

}
