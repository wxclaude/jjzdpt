package io.renren.modules.xlgl.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
@Data
@TableName("tsb")
public class TsbEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String sbbh;
	/**
	 * 
	 */
	private String sblx;
	/**
	 * 
	 */
	private String jrlx;
	/**
	 * 
	 */
	private String sxdw;
	/**
	 * 
	 */
	private String szwz;
	/**
	 * 
	 */
	private String jgh;
	/**
	 * 
	 */
	private String sbghs;
	/**
	 * 
	 */
	private String wbdh;
	/**
	 * 
	 */
	private String glzzr;
	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private String createBy;

	private String pzwjdz;

	@TableLogic
	private int isdel;

	@TableField(exist = false)
	private String deptName;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TsbEntity tsbEntity = (TsbEntity) o;
		return Objects.equals(id, tsbEntity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
