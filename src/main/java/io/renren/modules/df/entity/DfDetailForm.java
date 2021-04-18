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
public class DfDetailForm implements Serializable {

    private Integer recordId;

    private String deptId;

    private Integer configId;

    private Integer score;

    private String scoreQzb;

}
