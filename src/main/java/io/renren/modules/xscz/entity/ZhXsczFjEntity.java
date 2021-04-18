package io.renren.modules.xscz.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@TableName("zh_xscz_fj")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZhXsczFjEntity implements Serializable {

  @TableId(type = IdType.UUID)
  private String id;
  private String xsczid;
  private String path;
  private String filename;
  private Date createTime;
  private String createBy;

  @TableLogic
  private int isdel;

  private Integer type;

  private String ip;

}
