package io.renren.modules.jcj.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JcjCjxxImport implements Serializable {

  @Excel(name = "接警编号")
  private String jjbh;

  @Excel(name = "分局")
  private String dw;

  @Excel(name = "处警类别定性不准确")
  private int a1;

  @Excel(name = "处警人填写不规范")
  private int a2;

  @Excel(name = "处警人联系方\n" +
          "式填写不规范",fixedIndex=5)
  private int a3;

  @Excel(name = "发生地点填写不规范")
  private int a4;

  @Excel(name = "警情属性定性不准确")
  private int a5;

  @Excel(name = "简要警情及处理结果要素填写不全")
  private int a6;

  @Excel(name = "简要警情及处理结果填写存在错误")
  private int a7;

  @Excel(name = "处警统计信息填写不规范")
  private int a8;

  @Excel(name = "损失情况填写不规范")
  private int a9;

  @Excel(name = "涉警人员填写不规范")
  private int a10;

  @Excel(name = "涉警单位填写不规范")
  private int a11;

  @Excel(name = "涉警物品填写不规范")
  private int a12;

  @Excel(name = "移送接收单位未填")
  private int a13;

  @Excel(name = "移送接收人未填")
  private int a14;

  @Excel(name = "移送接收时间未填")
  private int a15;

  @Excel(name = "发生原因选择不规范")
  private int a16;

  @Excel(name = "事发场所选择不规范")
  private int a17;

  @Excel(name = "事发时间上限下限填写错误")
  private int a18;

  @Excel(name = "两抢等重大警情未在两小时内反馈")
  private int a19;

  @Excel(name = "违规升格、\n" +
          "降格处理警情")
  private int a20;

  @Excel(name = "超24小时登记")
  private int a21;

  @Excel(name = "超24小时审批")
  private int a22;

  @Excel(name = "未  处  警")
  private int a23;

  @Excel(name = "自接警情登记不规范")
  private int a24;

  @Excel(name = "警情标注标签输入不规范")
  private int a25;

  /********************* 2021-01-12 add ********************/
    @Excel(name = "处警反馈时间未填")
  private int a26;

  @Excel(name = "天气情况未填写")
  private int a27;
  /******************** 2021-01-12 add ********************/

  @Excel(name = "无问题")
  private int a_1;


}
