package io.renren.modules.df.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("df_detail")
public class DfDetail implements Serializable {

    @TableId
    private Integer id;

    private int recordId;

    private String deptId;

    private String deptName;

    private Integer score;

    private int configId;

    private int parentConfigId;

    private Date createTime;

    private String createBy;

    @TableLogic
    private int isdel;

    private String scoreQzb;

    @TableField(exist = false)
    private String configName;

}
