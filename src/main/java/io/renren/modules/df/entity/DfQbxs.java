package io.renren.modules.df.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("df_qbxs")
public class DfQbxs {

  @TableId
  private int id;
  private String type;
  private String dept;
  private String dfMonth;
  private String s1;
  private String s2;
  private String s3;
  private String createBy;
  private Date createTime;
  @TableLogic
  private int isdel;

  @TableField(exist = false)
  private String total;

  private String deptId;

}
