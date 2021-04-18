package io.renren.modules.mrtb.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@TableName("zh_mrtb_his")
@Data
public class ZhMrtbHis implements Serializable {

  @TableId
  private int id;
  private String content;
  private String phones;
  private String createBy;
  private Date createTime;
  @TableLogic
  private int isdel;
  private int mrtbId;
  private String ip;

  private String tbrq;

  private int successCount;
  private int phoneCount;

}
