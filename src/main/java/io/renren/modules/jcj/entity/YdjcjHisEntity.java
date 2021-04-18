package io.renren.modules.jcj.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-04-13
 */

//@TableName("tydjcj_his")
//@Data
public class YdjcjHisEntity implements Serializable {

    @TableId
    private int id;

    @Excel(name = "单位名称")
    private String dw;

    private String deptCode;

    private String deptId;

    //总警情量
    @Excel(name = "总警情量")
    private int zjql;
    //新平台接警量
    @Excel(name = "新平台接警量")
    private int xptjjl;
    //老平台接警量
    @Excel(name = "老平台接警量")
    private int lptjjl;
    //新平台接警率
    //@Excel(name = "新平台接警率")
    private int xptjjlv;
    //总出警的登记
    @Excel(name = "总处警登记")
    private int zcjdj;
    //新平台桌面端出警登记
    @Excel(name = "新平台桌面端处警登记")
    private int xptzmdcjdj;
    //新平台移动端出警登记
    @Excel(name = "新平台移动端处警登记")
    private int xptyddcjdj;
    //新平台出镜率
    //@Excel(name = "新平台处警率")
    private int xptcjlv;
    //移动端出镜率
    private int yddcjlv;

    private Date createTime;
    private Date ydjcjDate;

    private String userId;

    private String createBy;

    @TableField(exist=false)
    @Excel(name = "新平台接警率")
    private String xptjjlvExcel;

    @TableField(exist=false)
    @Excel(name = "新平台处警率")
    private String xptcjlvExcel;

    @TableField(exist=false)
    @Excel(name = "移动端处警率")
    private String yddcjlvExcel;

    @TableField(exist = false)
    private int xh;

    @TableField(exist = false)
    private String xptjjlvExport;//导出用

    @TableField(exist = false)
    private String xptcjlvExport;//导出用

    @TableField(exist = false)
    private String yddcjlvExport;//导出用

}
