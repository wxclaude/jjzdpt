package io.renren.modules.video.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-04-09
 */
@TableName("tdwbz")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwbzEntity implements Serializable {

    @TableId
    private Integer id;

    private String bh;

    private String sn;

    private String bz;

    private String userId;

    private Date createTime;

    @TableField(exist=false)
    private String xcr;

    private String xjid;

    //0指挥中心 1自建 2交警  3教育 4小区 5内部 6其它单位 7人脸
    private String type;

    //异常原因 0画面树木遮挡 1监控角度异常 2设备离线 3画面异常 4录像异常 5其它
    private String category;

    private int state;

    private String czqk;

    private String repairTime;

    private String ip;

    private Integer dwortd;

    @TableField(exist = false)
    private String s1;
    @TableField(exist = false)
    private String s2;
    @TableField(exist = false)
    private String s3;
    @TableField(exist = false)
    private String s4;

    private String dh;
    private String ip2;

}
