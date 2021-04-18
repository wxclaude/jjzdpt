package io.renren.modules.dm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-05-29
 */
@TableName("tzbwbz")
@Data
public class TzbwbzEntity implements Serializable {

    @TableId
    private Integer id;

    private String deptId;

    private String zbdate;

    private Date createTime;

    private String createBy;

    @TableLogic
    private int isdel;

    @TableField(exist = false)
    private String deptIds;

    @TableField(exist = false)
    private String deptName;

}
