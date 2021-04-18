package io.renren.modules.dm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZhiBanFormat implements Serializable {

    private String dutyId;

    private String deptName;

    private String leader;

    private String leaderDevNo;

    private List<Map<String,Object>> policeInfoList = new ArrayList<>();

    private String dutyDate;

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }


    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getLeaderDevNo() {
        return leaderDevNo;
    }

    public void setLeaderDevNo(String leaderDevNo) {
        this.leaderDevNo = leaderDevNo;
    }

    public List<Map<String, Object>> getPoliceInfoList() {
        return policeInfoList;
    }

    public void setPoliceInfoList(List<Map<String, Object>> policeInfoList) {
        this.policeInfoList = policeInfoList;
    }

    public String getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(String dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
