/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.sys.entity.SysLoginLogEntity;
import io.renren.modules.sys.service.SysLogService;
import io.renren.modules.sys.service.SysLoginLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 系统日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping("/sys/loginlog")
public class SysLoginLogController {
	@Autowired
	private SysLoginLogService sysLoginLogService;

	
	/**
	 * 列表
	 */
	@ResponseBody
	@GetMapping("/list")
	public R list(@RequestParam Map<String, Object> params){

		int page = Integer.parseInt(String.valueOf(params.get("page")));
		int limit = Integer.parseInt(String.valueOf(params.get("limit")));
		Page pageParams = new Page(page,limit);

		QueryWrapper<SysLoginLogEntity> queryWrapper = new QueryWrapper<SysLoginLogEntity>().eq("type", 1);
		queryWrapper.ge(StrUtil.isNotBlank(String.valueOf(params.get("beginTime"))), "date_format(login_time, '%Y-%m-%d')", String.valueOf(params.get("beginTime")));
		queryWrapper.le(StrUtil.isNotBlank(String.valueOf(params.get("endTime"))), "date_format(login_time, '%Y-%m-%d')", String.valueOf(params.get("endTime")));
		queryWrapper.eq(StrUtil.isNotBlank(String.valueOf(params.get("deptId"))), "dept_id", String.valueOf(params.get("deptId")));
		queryWrapper.orderByDesc("login_time");


		IPage<SysLoginLogEntity> list = sysLoginLogService.page(pageParams,queryWrapper);

		return R.ok().put("data", list.getRecords()).put("count", list.getTotal());
	}
	
}
