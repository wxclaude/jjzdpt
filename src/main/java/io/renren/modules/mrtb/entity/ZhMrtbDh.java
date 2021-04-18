package io.renren.modules.mrtb.entity;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("zh_mrtb_dh")
@Data
public class ZhMrtbDh implements Serializable {

  @TableId
  private int id;
  @Excel(name = "姓名")
  private String name;
  @Excel(name = "电话号码")
  private String phone;
  @Excel(name = "部门")
  private String dept;
  @Excel(name = "职务")
  private String role;
  private Date createTime;
  private String createBy;
  @TableLogic
  private int isdel;
  private String ip;

}
