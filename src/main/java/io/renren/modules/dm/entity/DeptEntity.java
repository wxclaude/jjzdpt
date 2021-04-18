package io.renren.modules.dm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("tdept")
@Data
public class DeptEntity implements Serializable {
    @TableId
    private Integer id;

    private String deptId;

    private String deptCode;

    private String deptName;

    private String fullDeptName;

    private String shortDeptName;

    private int ydjcj;//移动接出警排序


}
