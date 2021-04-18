package io.renren.modules.mrtb.entity;

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
 * @date 2020-06-05
 */
@TableName("zh_mrtb_view")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZhMrtbView implements Serializable {

    @TableId
    private int id;

    private String deptId;

    private String tbrq;

    private Date createTime;

    @TableLogic
    private int isdel;


}
