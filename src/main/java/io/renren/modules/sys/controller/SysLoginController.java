/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.renren.common.utils.HttpContextUtils;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.R;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.sys.entity.SysLoginLogEntity;
import io.renren.modules.sys.entity.SysModelEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.entity.SysUserTokenEntity;
import io.renren.modules.sys.form.SysLoginForm;
import io.renren.modules.sys.service.*;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录相关
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
public class SysLoginController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserTokenService sysUserTokenService;
	@Autowired
	private SysCaptchaService sysCaptchaService;
	@Autowired
	private ShiroService shiroService;
	@Autowired
	private DmService dmService;
	@Autowired
	private SysLoginLogService sysLoginLogService;

	/**
	 * 验证码
	 */
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 登录
	 */
	@PostMapping("/sys/login")
	public Map<String, Object> login(@RequestBody SysLoginForm form)throws IOException {
//		boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
//		if(!captcha){
//			return R.error("验证码不正确");
//		}

		//用户信息
		//SysUserEntity user = sysUserService.queryByUserName(form.getUsername());
		//if(user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
		//	return R.error("账号或密码不正确");
		//}
		//
		////账号锁定
		//if(user.getStatus() == 0){
		//	return R.error("账号已被锁定,请联系管理员");
		//}

		//从sqlserver中获取用户
		SysUserEntity user = (SysUserEntity) shiroService.queryUserSqlServer(form.getUsername());

		if(user == null) {
			//部门用户
			user = (SysUserEntity) shiroService.queryDeptUserSqlServer(form.getUsername());
		}

		//记录登录日志

		try{

			SysLoginLogEntity sysLoginLogEntity = new SysLoginLogEntity();

			if (user != null && !user.getPassword().equals(form.getPassword())) {
				sysLoginLogEntity.setStatus(1);
				sysLoginLogEntity.setUsername(user.getName());
				sysLoginLogEntity.setDeptId(user.getDeptId());
				sysLoginLogEntity.setType(user.getType());
				sysLoginLogEntity.setLoginTime(new Date());
			} else {
				sysLoginLogEntity.setUsername(user.getName());
				sysLoginLogEntity.setDeptId(user.getDeptId());
				sysLoginLogEntity.setType(user.getType());
				sysLoginLogEntity.setLoginTime(new Date());
			}

			//获取request
			HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
			//设置IP地址
			sysLoginLogEntity.setIp(IPUtils.getIpAddr(request));

			sysLoginLogService.save(sysLoginLogEntity);

		}catch (Exception e){
			logger.error("Add Login log error, msg={}", e.getMessage());
		}

		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(form.getPassword())) {
			return R.error("账号或密码不正确");
		}

		//判断是否有已经存在的token，有的话直接返回，这样允许一个账户多个地方同时登陆
		SysUserTokenEntity loginedUserToken = sysUserTokenService.getById(user.getUserId());
		R r = null;

		//大于3天不续签
		if(loginedUserToken!=null
				&& StrUtil.isNotEmpty(loginedUserToken.getToken())
				&& loginedUserToken.getExpireTime().getTime() - System.currentTimeMillis() >= 3600 * 24 * 1000 * 3){

			r = R.ok()
					.put("token", loginedUserToken.getToken())
					.put("userId", loginedUserToken.getUserId())
					.put("userName", user.getName())
					.put("role", user.getRole())
					.put("deptId", user.getDeptId())
					.put("deptCode", user.getDeptCode())
					.put("type", user.getType());

		}else{
			//生成token，并保存到数据库
			r = sysUserTokenService.createToken(user.getUserId());
			r.put("userId", user.getUserId())
					.put("userName", user.getName())
					.put("role", user.getRole())
					.put("deptId", user.getDeptId())
					.put("deptCode", user.getDeptCode())
					.put("type", user.getType());
		}

		//获取该用户对应的权限
		Set<SysModelEntity> list = shiroService.queryModelListByUserId(user.getUserId(), user.getRole());

		r.put("modelList", list);

		return r;
	}


	/**
	 * 退出
	 */
	@PostMapping("/sys/logout")
	public R logout(HttpServletRequest request) {
		String token = getRequestToken(request);
		sysUserTokenService.logout(token);
		return R.ok();
	}
	
}
