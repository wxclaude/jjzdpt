package io.renren.modules.dm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("tZbmrdm")
public class DianMingEntity implements Serializable {

    @TableId
    private Integer id;

    private String dutyId;

    private Date chouquTime;

    private String chouquren;

    private String police;

    private String djj = "";

    private String policeCode;
    private String deptName;
    private String deptId;
    private String dmPolice;
    private Date dmTime;
    private int yd;
    private String bz;
    private String ip;
    private String shortPhone;
    private int isLeader;

    private String reportPointId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public Date getChouquTime() {
        return chouquTime;
    }

    public void setChouquTime(Date chouquTime) {
        this.chouquTime = chouquTime;
    }

    public String getChouquren() {
        return chouquren;
    }

    public void setChouquren(String chouquren) {
        this.chouquren = chouquren;
    }

    public String getPolice() {
        return police;
    }

    public void setPolice(String police) {
        this.police = police;
    }

    public String getDjj() {
        return djj;
    }

    public void setDjj(String djj) {
        this.djj = djj;
    }

    public String getPoliceCode() {
        return policeCode;
    }

    public void setPoliceCode(String policeCode) {
        this.policeCode = policeCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDmPolice() {
        return dmPolice;
    }

    public void setDmPolice(String dmPolice) {
        this.dmPolice = dmPolice;
    }

    public Date getDmTime() {
        return dmTime;
    }

    public void setDmTime(Date dmTime) {
        this.dmTime = dmTime;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getYd() {
        return yd;
    }

    public void setYd(int yd) {
        this.yd = yd;
    }

    public String getShortPhone() {
        return shortPhone;
    }

    public void setShortPhone(String shortPhone) {
        this.shortPhone = shortPhone;
    }

    public int getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(int isLeader) {
        this.isLeader = isLeader;
    }

    public String getReportPointId() {
        return reportPointId;
    }

    public void setReportPointId(String reportPointId) {
        this.reportPointId = reportPointId;
    }
}
