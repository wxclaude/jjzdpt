package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tomchen
 * @date 2020-07-27
 */
@TableName("sys_login_log")
@Data
public class SysLoginLogEntity implements Serializable {

    @TableId
    private Long id;
    //用户名
    private String username;

    private String deptId;

    private String ip;

    private Date createTime;

    private Date loginTime;

    private int type;

    private int status;

}
