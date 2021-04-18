package io.renren.modules.dm.form;

import io.renren.modules.dm.entity.HydmEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class HydmForm implements Serializable {

    private String title;

    private Date dmTime;

    private List<HydmEntity> data;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDmTime() {
        return dmTime;
    }

    public void setDmTime(Date dmTime) {
        this.dmTime = dmTime;
    }

    public List<HydmEntity> getData() {
        return data;
    }

    public void setData(List<HydmEntity> data) {
        this.data = data;
    }
}
