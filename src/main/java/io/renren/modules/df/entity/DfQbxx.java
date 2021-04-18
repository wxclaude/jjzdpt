package io.renren.modules.df.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("df_qbxx")
public class DfQbxx {

  @TableId
  private int id;
  private String type;
  private String dept;
  private String dfMonth;
  private String s1;
  private String s2;
  private String s3;
  private String s4;
  private String s5;
  private String s6;
  private String s7;
  private String s8;
  private String s9;
  private String createBy;
  private Date createTime;
  @TableLogic
  private int isdel;
  private String deptId;

}
