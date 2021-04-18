package io.renren.modules.jcj.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JcjCjxx implements Serializable {

  private String cjbh;
  private String jjbh;
  private String jjdbh;
  private String pjdbh;
  private String fklyh;
  private String cjlb;
  private String cjsj;
  private String ddxcsj;
  private String cjxzqh;
  private String cjjlx;
  private String cjmlph;
  private String cjmphz;
  private String cjxz;
  private String sfcs;
  private String fsyy;
  private String tqqk;
  private String jqsx;
  private String cjr;
  private String cjrhzxs;
  private String cjrlxfs;
  private String sfxq;
  private String sfsjsx;
  private String sfsjxx;
  private String cljgnr;
  private String bccljg;
  private String ssxxqk;
  private String cjfksj;
  private String spsj;
  private String zbld;
  private String zbldxm;
  private String ldclsj;
  private String cjjg;
  private String cjysjsdw;
  private String cjysjsr;
  private String cjysjssj;
  private String cdjl;
  private String cdjdc;
  private String cdcz;
  private String jjfns;
  private String jjets;
  private String jjrzs;
  private String jzqz;
  private String jzsy;
  private String rysss;
  private String rysws;
  private String lzscrs;
  private String phxxaj;
  private String phxsaj;
  private String phzaaj;
  private String tprf;
  private String zhzacy;
  private double zjjjss;
  private double whss;
  private String ajslr;
  private String ajsldw;
  private String glajbh;
  private String glajzt;
  private String gisX;
  private String gisY;
  private String djdw;
  private String djr;
  private String djsj;
  private String xgr;
  private String xgsj;
  private String xgdw;
  private String djrxm;
  private String djdwmc;
  private String xgrxm;
  private String xgdwmc;
  private String cjdw;
  private String cjdwmc;
  private String cjxxdd;
  private String zblddw;
  private String zblddwmc;
  private String spxgsj;

  //来自接警表数据
  private String bjxs;
  private String bjlx;
  private String jjrqsj;
  private String bjr;
  private String bjrxb;
  private String sfdd;
  private String bjnr;

  //来自deptsName
  private String deptsname;
  //自定义字段，非数据库内字段
  //警情备注，为普通警情/重大警情，新冠、债务、恶劣天气
  private String remarks = "";

  //移送报警，移送接受单位/人/时间为空
  private String p0 = "0";
  //事发场所未具体，==990
  private String p1 = "0";
  //处警类别为其他警情or处警类别未填到字典项最底层
  private String p2 = "0";
  //处警统计信息未填写
  private String p3 = "0";
  //关联损失情况警情类型，损失情况未填写
  private String p4 = "0";
  //网络诈骗类警情属性未标注涉网
  private String p5 = "0";
  //处警超时，登记时间-接警时间>24h
  private String p6 = "0";
  //审批超时，审批时间-登记时间>24h
  private String p7 = "0";
  //重大警情，处警时间-接警时间<2h
  private String p8 = "0";
  //自接警情，填写不完善
  private String p9 = "0";
  //事发时间上限、下限为空
  private String p10 = "0";
  //交通类警情天气情况为空
  private String p11 = "0";
  //手动标注为不合格
  private String p99 = "0";

  private int a1;
  private int a2;
  private int a3;
  private int a4;
  private int a5;
  private int a6;
  private int a7;
  private int a8;
  private int a9;
  private int a10;
  private int a11;
  private int a12;
  private int a13;
  private int a14;
  private int a15;
  private int a16;
  private int a17;
  private int a18;
  private int a19;
  private int a20;
  private int a21;
  private int a22;
  private int a23;
  private int a24;

  private JqCjBhgEntity jqCjBhgEntity;

  private JcjJjxx jjxx;

  private String type;
  private String jcr;

}
