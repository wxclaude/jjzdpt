package io.renren.modules.dm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName("tyjdm")
public class YjdmEntity implements Serializable {

    @TableId
    private Integer id;

    private String deptId;

    private String deptName;

    private int yd;

    private Date dmTime;

    private String dmPolice;

    private String ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getYd() {
        return yd;
    }

    public void setYd(int yd) {
        this.yd = yd;
    }

    public Date getDmTime() {
        return dmTime;
    }

    public void setDmTime(Date dmTime) {
        this.dmTime = dmTime;
    }

    public String getDmPolice() {
        return dmPolice;
    }

    public void setDmPolice(String dmPolice) {
        this.dmPolice = dmPolice;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
