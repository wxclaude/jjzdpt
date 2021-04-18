package io.renren.modules.jcj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@TableName("jq_cj_bhg")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JqCjBhgEntity implements Serializable {

  @TableField
  private Integer id;
  private String jjbh;
  private String cjbh;
  private Date createTime;
  private String createBy;
  private String reason;
  private int state;
  private Date updateTime;
  private String updateBy;
  private String ip;
  private String ip2;
  private String deptCode;

  @TableLogic
  private int isdel;

  private String type;
  private String remark;
  private String jjsj;
  private String dw;
  private String jcr;
  private Integer frm;

  private Integer createway;//数据录入方式  手动录入/excel导入

  @TableField(exist = false)
  private List<String> typeList;

  private Integer doubleCheck;

}
