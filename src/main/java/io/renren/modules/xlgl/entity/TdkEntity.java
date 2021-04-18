package io.renren.modules.xlgl.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
@Data
@TableName("tV_RWDK")
public class TdkEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String sbId;
	/**
	 * 
	 */
	private String dklx;
	/**
	 * 
	 */
	private String bkh;
	/**
	 * 
	 */
	private String dkh;
	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private String createBy;

	/**
	 * 端口数
	 */
	@TableField(exist = false)
	private int dks;

	/**
	 * 板卡数
	 */
	@TableField(exist = false)
	private int bks;

	/**
	 * 端口号起始
	 */
	@TableField(exist = false)
	private int dkhqs;


	@TableLogic
	private int isdel;

	@TableField(exist = false)
	private String sbbh;

	@TableField(exist = false)
	private Map<String,Object> sbMap;

	@TableField(exist = false)
	private String xlbh;

	@TableField(exist = false)
	private String xlbh1;

	@TableField(exist = false)
	private String xlbh2;

	@TableField(exist = false)
	private String dkType;

	@TableField(exist = false)
	private String wllx;


}
