package io.renren.modules.jcj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @date 2020-07-30
 */

@TableName("jq_cj_view")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JqCjViewEntity implements Serializable {

    @TableField
    private Integer id;

    private String cjbh;

    private String deptId;

    private String deptCode;

    private Date createTime;

    @TableLogic
    private int isdel;

    private String ip;
}
