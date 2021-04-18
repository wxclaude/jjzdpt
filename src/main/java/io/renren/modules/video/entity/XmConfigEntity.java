package io.renren.modules.video.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-22
 */
@TableName("txmconfig")
@Data
public class XmConfigEntity implements Serializable {

    @TableId
    private Integer id;

    private String dw;
    private String xmid;
    @TableField(exist = false)
    private String xmmcShow;
    private String xmmc;
    private Date createTime;
    private String createBy;
    private int sort;
    @TableLogic
    private int isdel;

    @TableField(exist = false)
    private List<Map<String, Object>> xjList = new ArrayList<>();


}
