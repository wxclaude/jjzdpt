package io.renren.modules.jcj.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("jq_cj_xcr")
@Data
public class JqCjXcrEntity implements Serializable {

  @TableId
  private int id;
  private String name;
  private Date createTime;
  private String createBy;
  @TableLogic
  private int isdel;
  private String deptCode;


}
