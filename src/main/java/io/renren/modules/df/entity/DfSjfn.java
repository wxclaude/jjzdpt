package io.renren.modules.df.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("df_sjfn")
public class DfSjfn {

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
  private String s10;
  private String s11;
  private String s12;
  private String s13;
  private String s14;
  private String s15;
  private String s16;
  private String s17;
  private String s18;
  private String s19;
  private String s20;
  private String s21;
  private String s22;
  private String createBy;
  private Date createTime;
  @TableLogic
  private int isdel;

  @TableField(exist = false)
  private String total;
  @TableField(exist = false)
  private String total1;
  @TableField(exist = false)
  private String total2;

  private String deptId;

}
