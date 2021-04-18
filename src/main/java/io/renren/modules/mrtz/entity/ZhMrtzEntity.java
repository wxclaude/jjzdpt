package io.renren.modules.mrtz.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@TableName("zh_mrtz")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZhMrtzEntity implements Serializable {

  @TableId
  private Integer id;
  private String deptId;
  private Date createTime;
  @TableLogic
  private int isdel;
  private String path;
  private String tzDate;
  private String ip;
  private String fileName;
  private String uid;

  @TableField(exist = false)
  private String deptName;

}
