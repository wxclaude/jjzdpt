package io.renren.modules.mrtb.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-04-27
 */
@TableName("zh_mrtb")
@Data
public class ZhMrtb implements Serializable {

    @TableId
    private int id;

    private String fbrq;

    private String tbrq;

    private String qs;

    private String path;

    private Date createTime;

    private String createBy;

    private String chenbanren;

    private String shenheren;

    private String comment1;

    private String comment2;

    private String comment3;

    private String comment4;

    private String comment5;

    @TableLogic
    private int isdel;

    private String totalnum;
    private String num01;
    private String num02;
    private String num03;
    private String num04;
    private String num05;
    private String num06;
    private String num08;
    private String num11;
    private String num99;

    private String gq03;
    private String gq02;
    private String gq01;
    private String jy03;
    private String jy02;
    private String jy01;
    private String xh03;
    private String xh02;
    private String xh01;
    private String zx03;
    private String zx02;
    private String zx01;
    private String xs03;
    private String xs02;
    private String xs01;
    private String nsq03;
    private String nsq02;
    private String nsq01;
    private String sjy03;
    private String sjy02;
    private String sjy01;
    private String ys03;
    private String ys02;
    private String ys01;
    private String gd03;
    private String gd02;
    private String gd01;
    private String fx03;
    private String fx02;
    private String fx01;
    private String hss03;
    private String hss02;
    private String hss01;
    private String ym03;
    private String ym02;
    private String ym01;
    private String jw03;
    private String jw02;
    private String jw01;
    private String gz03;
    private String gz02;
    private String gz01;
    private String ch03;
    private String ch02;
    private String ch01;
    private String hs03;
    private String hs02;
    private String hs01;
    private String jd03;
    private String jd02;
    private String jd01;

    private Integer xsjq01;//入室盗窃
    private Integer xsjq02;//诈骗
    private Integer xsjq03;//盗窃非机动车
    private Integer xsjq04;
    private Integer xsjq05;
    private Integer xsjq06;
    private Integer xsjq07;
    private Integer xsjq08;
    private Integer xsjq09;
    private Integer xsjq99;//其它警情

    private Integer msgCount;//发送短信次数

    private String msg;

    private Integer published;

    private Integer xxId;

    private Date publishTime;

    private String publishIp;

    private String publishContent;


}
