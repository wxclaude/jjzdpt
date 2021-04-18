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
@TableName("df_config")
public class DfConfig implements Serializable {

    @TableId
    private Integer id;

    private String dfx;

    private Integer qzb;

    private Integer type;

    private Integer parentId;

    private Date createTime;

    private String createBy;

    @TableLogic
    private int isdel;

    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private int totalQzb;

    @TableField(exist = false)
    private String content;
}
