package io.renren.modules.df.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Data
@TableName("df_rule")
public class DfRule implements Serializable {

    @TableId
    private Integer id;

    private Date createTime;

    private String createBy;

    private String title;

    @TableLogic
    private int isdel;

    private String deptIds;

    private String configIds;

    //@TableField(exist = false)




}
