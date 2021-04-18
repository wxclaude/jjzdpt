package io.renren.modules.mrtb.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("T_XX")
@Data
public class TXx implements Serializable {

  @TableId
  private int id;
  private Integer lmid;
  private Integer lbid;
  private String fbdw;
  private String bt;
  private String nr;
  private String keys;
  private Date fbrq;
  private Integer hits;
  private Integer plsx;
  @TableField(value = "Uid")
  private String Uid;
  private Integer bmid;
  @TableField(value = "PicUrl")
  private String PicUrl;
  private String fujian;
  private String bmi;
  private String zhyw;
  @TableField(value = "canComment")
  private String canComment;
  @TableField(value = "reqAccept")
  private String reqAccept;
  @TableField(value = "isPicNews")
  private String isPicNews;
  @TableField(value = "isImport")
  private String isImport;
  @TableField(value = "linkUrl")
  private String linkUrl;
  private String jrdd;
  private String tcxx;
  private Date dtime;
  private String tcw;
  private String tch;
  private String tcl;
  private String tct;
  private String canps;
  private String btcolor;
  private String sffb;
  private String sfbtj;
  private String stcy;
  private String ydjw;


}
