package io.renren.modules.df.entity;

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
import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("df_record")
public class DfRecord implements Serializable {

    @TableId
    private Integer id;

    private Date createTime;

    private String createBy;

    private String dfMonth;

    private String title;

    @TableLogic
    private int isdel;

    private int state;

    @TableField(exist = false)
    private String deptIds;

    private int ruleId;

    @TableField(exist = false)
    private List<Map<String, Object>> dfDetailList;

    @TableField(exist = false)
    private List<Map<String, Object>> dfTheadList;

    @TableField(exist = false)
    private List<Map<String, Object>> dfParentTheadList;



}
