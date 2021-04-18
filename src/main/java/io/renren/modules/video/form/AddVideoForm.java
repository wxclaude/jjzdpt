package io.renren.modules.video.form;

import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.video.entity.JyVideoEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AddVideoForm implements Serializable {

    private int total;
    private int zxs;
    private String xcr;
    private String tjsj;
    private List<JyVideoEntity> list;


    public String getTjsj() {
        return tjsj;
    }

    public void setTjsj(String tjsj) {
        this.tjsj = tjsj;
    }

    public List<JyVideoEntity> getList() {
        return list;
    }

    public void setList(List<JyVideoEntity> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getZxs() {
        return zxs;
    }

    public void setZxs(int zxs) {
        this.zxs = zxs;
    }

    public String getXcr() {
        return xcr;
    }

    public void setXcr(String xcr) {
        this.xcr = xcr;
    }


}
