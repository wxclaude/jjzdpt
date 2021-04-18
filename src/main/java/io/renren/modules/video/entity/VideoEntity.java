package io.renren.modules.video.entity;

/**
 * @author tomchen
 * @date 2020-03-31
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@TableName("tXDJ_TWJK_MRXJ")
public class VideoEntity implements Serializable {

    private int id;

    @TableField("编号")
    private String bh;

    @TableField("所属域")
    private String ssy;

    @TableField("所属单位")
    private String ssdw;

    @TableField("监控点名称")
    private String jjdmc;

    @TableField("监控点SN")
    private String sn;

    @TableField("检测开始时间")
    private String jckssj;

    @TableField("检测结束时间")
    private String jcjssj;

    @TableField("状态")
    private String zt;

    @TableField("状态描述")
    private String ztms;

    @TableField("检测次数")
    private String jccs;

    private Date datetime1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getSsy() {
        return ssy;
    }

    public void setSsy(String ssy) {
        this.ssy = ssy;
    }

    public String getSsdw() {
        return ssdw;
    }

    public void setSsdw(String ssdw) {
        this.ssdw = ssdw;
    }

    public String getJjdmc() {
        return jjdmc;
    }

    public void setJjdmc(String jjdmc) {
        this.jjdmc = jjdmc;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getJckssj() {
        return jckssj;
    }

    public void setJckssj(String jckssj) {
        this.jckssj = jckssj;
    }

    public String getJcjssj() {
        return jcjssj;
    }

    public void setJcjssj(String jcjssj) {
        this.jcjssj = jcjssj;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getZtms() {
        return ztms;
    }

    public void setZtms(String ztms) {
        this.ztms = ztms;
    }

    public String getJccs() {
        return jccs;
    }

    public void setJccs(String jccs) {
        this.jccs = jccs;
    }

    public Date getDatetime1() {
        return datetime1;
    }

    public void setDatetime1(Date datetime1) {
        this.datetime1 = datetime1;
    }
}
