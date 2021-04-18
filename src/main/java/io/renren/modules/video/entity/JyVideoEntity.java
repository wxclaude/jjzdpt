package io.renren.modules.video.entity;

/**
 * @author tomchen
 * @date 2020-03-31
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@TableName("tXDJ_TWJK_XYJK")
public class JyVideoEntity implements Serializable {

    @TableField("XMID")
    private String xmid;

    @TableField("XLZT")
    private String xlzt;

    @TableField("BZ")
    private String bz;

    @TableField("JKZS")
    private String jkzs;

    @TableField("ZXJKS")
    private String zxjks;

    private Date datetime1;

    public String getXmid() {
        return xmid;
    }

    public void setXmid(String xmid) {
        this.xmid = xmid;
    }

    public String getXlzt() {
        return xlzt;
    }

    public void setXlzt(String xlzt) {
        this.xlzt = xlzt;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getJkzs() {
        return jkzs;
    }

    public void setJkzs(String jkzs) {
        this.jkzs = jkzs;
    }

    public String getZxjks() {
        return zxjks;
    }

    public void setZxjks(String zxjks) {
        this.zxjks = zxjks;
    }

    public Date getDatetime1() {
        return datetime1;
    }

    public void setDatetime1(Date datetime1) {
        this.datetime1 = datetime1;
    }
}
