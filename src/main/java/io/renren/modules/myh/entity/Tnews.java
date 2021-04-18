package io.renren.modules.myh.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("myh_news")
public class Tnews implements Serializable {

  @TableId(type = IdType.ASSIGN_UUID)
  private String id;
  private String title;
  private int type;// 0随手拍 1好人好事 2交通事故 3酒驾 4聚众斗殴
  private Date createTime;
  private String createBy;
  @TableLogic
  private int isdel;
  private String img;
  private String content;

  private Date newsTime;

  private String s1;
  private String s2;
  private String s3;
  private String s4;

}
