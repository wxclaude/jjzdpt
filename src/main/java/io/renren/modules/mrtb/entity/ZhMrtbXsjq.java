package io.renren.modules.mrtb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.jcj.entity.JcjJjxx;
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
@TableName("zh_mrtb_xsjq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZhMrtbXsjq implements Serializable {

    @TableId
    private int id;

    private int tbid;

    private String jjsj;

    private String jjdw;

    private String jqlb;

    private String jyaq;

    private String dw;

    private Date createTime;

    private String jjbh;
    private String cjbh;

    @TableLogic
    private int isdel;

    @TableField(exist = false)
    private JcjJjxx jcjJjxx;

    @TableField(exist = false)
    private JcjCjxx jcjCjxx;


}
