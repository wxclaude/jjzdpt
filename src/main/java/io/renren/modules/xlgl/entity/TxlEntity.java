package io.renren.modules.xlgl.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
@Data
@TableName("tV_RWXL")
public class TxlEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String xlbh;
	/**
	 * 
	 */
	private Integer slDkId;
	/**
	 * 
	 */
	private Integer xlDkId;
	/**
	 * 
	 */
	private String ljlx;
	/**
	 * 
	 */
	private String vlan;
	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private String createBy;

	private String bz;

	@TableLogic
	private int isdel;

	private String slSbId;

	private String xlSbId;

	@TableField(exist = false)
	private String slSbbh;

	@TableField(exist = false)
	private String xlSbbh;

	@TableField(exist = false)
	private String slDklx;

	@TableField(exist = false)
	private String xlDklx;

	@TableField(exist = false)
	private String slBkh;

	@TableField(exist = false)
	private String xlBkh;

	@TableField(exist = false)
	private String slDkh;

	@TableField(exist = false)
	private String xlDkh;

	@TableField(exist = false)
	private Map<String, Object> slSbMap = new HashMap();
	@TableField(exist = false)
	private Map<String, Object> xlSbMap = new HashMap();


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TxlEntity txlEntity = (TxlEntity) o;
		return Objects.equals(id, txlEntity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
