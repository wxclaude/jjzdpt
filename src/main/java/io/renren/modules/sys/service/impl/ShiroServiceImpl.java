/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import io.renren.common.utils.Constant;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.sys.consts.SysModelConsts;
import io.renren.modules.sys.dao.SysMenuDao;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.dao.SysUserTokenDao;
import io.renren.modules.sys.entity.SysMenuEntity;
import io.renren.modules.sys.entity.SysModelEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.entity.SysUserTokenEntity;
import io.renren.modules.sys.service.ShiroService;
import io.renren.modules.video.consts.VideoConsts;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;
    @Autowired
    private SysModelConsts sysModelConsts;

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<SysMenuEntity> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(SysMenuEntity menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUserTokenEntity queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);
    }

    @Override
    public SysUserEntity queryUser(Long userId) {
        return sysUserDao.selectById(userId);
    }

    @Override
    @DataSource("slave2")
    public SysUserEntity queryUserSqlServer(String policeCode) {
        return sysUserDao.queryUserSqlServer(policeCode);
    }

    @Override
    @DataSource("slave2")
    public SysUserEntity queryDeptUserSqlServer(String username) {
        return sysUserDao.queryDeptUserSqlServer(username);
    }

    @Override
    @DataSource("slave2")
    public SysUserEntity queryDeptUserSqlServerByDeptCode(String deptCode) {
        return sysUserDao.queryDeptUserSqlServerByDeptCode(deptCode);
    }

    @Override
    @DataSource("slave2")
    public Set<SysModelEntity> queryModelListByUserId(Long userId, String role) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("url", VideoConsts.URL);

        List<SysModelEntity> list = sysUserDao.queryModelListByUserId(params);

        //这里用set去重
        Set<SysModelEntity> set = new LinkedHashSet<>();
        set.addAll(list);

        //这里处理一下子父菜单
        Set<SysModelEntity> resultSet = set.stream()
                .filter(e -> StrUtil.isEmpty(e.getParentModel()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        resultSet.stream().forEach(e -> {
            e.setSubModelList(new ArrayList<>());
            e.getSubModelList().addAll(set.stream().filter(l ->e.getModelId().equals(l.getParentModel())).collect(Collectors.toList()));
            e.getSubModelList().forEach(s->{
                s.setUrl(StrUtil.replace(s.getUrl(), VideoConsts.URL, ""));
            });
        });

        //写死，有综合指挥室权限的可以有如下权限
        // 每日通报
        if (role !=null && role.contains("ROLE0021")) {
            SysModelEntity sysModelEntity110 = new SysModelEntity();
            sysModelEntity110.setModelId("9999");
            sysModelEntity110.setModelName("每日通报110");

            SysModelEntity subSysModelEntity110 = new SysModelEntity();
            subSysModelEntity110.setModelId("99990");
            subSysModelEntity110.setModelName("生成通报");
            subSysModelEntity110.setUrl("mr110.html");
            subSysModelEntity110.setParentModel("9999");
            subSysModelEntity110.setSorted("1001");

            List<SysModelEntity> subModelList = new ArrayList<>();
            subModelList.add(subSysModelEntity110);

            sysModelEntity110.setSubModelList(subModelList);
            resultSet.add(sysModelEntity110);
        }

        resultSet.forEach(e -> {
            //综合指挥室添加登录日志功能
            if (e.getModelId().equals(sysModelConsts.getZHZH_ID())) {
                SysModelEntity subSysModelEntityLoginLog = new SysModelEntity();
                subSysModelEntityLoginLog.setModelId("999900");
                subSysModelEntityLoginLog.setModelName(sysModelConsts.getDLRZ_NAME());
                subSysModelEntityLoginLog.setUrl("log.html");
                subSysModelEntityLoginLog.setParentModel(sysModelConsts.getZHZH_ID());
                subSysModelEntityLoginLog.setSorted("9999");

                e.getSubModelList().add(subSysModelEntityLoginLog);

                SysModelEntity subSysModelEntityMrtz = new SysModelEntity();
                subSysModelEntityMrtz.setModelId("999900");
                subSysModelEntityMrtz.setModelName(sysModelConsts.getMRTZ_NAME());
                subSysModelEntityMrtz.setUrl("mrtz.html");
                subSysModelEntityMrtz.setParentModel(sysModelConsts.getZHZH_ID());
                subSysModelEntityMrtz.setSorted("9999");

                e.getSubModelList().add(subSysModelEntityMrtz);

                SysModelEntity subSysModelEntityXscz = new SysModelEntity();
                subSysModelEntityXscz.setModelId("999900");
                subSysModelEntityXscz.setModelName(sysModelConsts.getXSCZ_NAME());
                subSysModelEntityXscz.setUrl("xfzl.html");
                subSysModelEntityXscz.setParentModel(sysModelConsts.getZHZH_ID());
                subSysModelEntityXscz.setSorted("9999");

                e.getSubModelList().add(subSysModelEntityXscz);

                int index1 = 0;
                int index2 = 0;
                int index3 = 0;

                for (int i = 0; i < e.getSubModelList().size(); i++) {
                    SysModelEntity s = e.getSubModelList().get(i);

                    if (s.getUrl().equals("zhiban2.html")) {
                        s.setModelName(sysModelConsts.getNAME1_1());
                    }
                    if (s.getUrl().equals("zhiban4.html")) {
                        s.setModelName(sysModelConsts.getNAME1_2());
                    }
                    if (s.getUrl().equals("zhibantongji.html")) {
                        s.setModelName(sysModelConsts.getNAME1_3());
                    }


                    if (s.getUrl().equals("yingji.html")) {
                        s.setModelName(sysModelConsts.getNAME2_1());
                    }
                    if (s.getUrl().equals("yingjichakan.html")) {
                        s.setModelName(sysModelConsts.getNAME2_2());
                    }
                    if (s.getUrl().equals("yingjitongji.html")) {
                        s.setModelName(sysModelConsts.getNAME2_3());
                    }


                    if (s.getUrl().equals("huiyidianming.html")) {
                        s.setModelName(sysModelConsts.getNAME3_1());
                    }
                    if (s.getUrl().equals("huiyitongji.html")) {
                        s.setModelName(sysModelConsts.getNAME3_2());
                    }
                }

                SysModelEntity subSysModelEntity1 = new SysModelEntity();
                subSysModelEntity1.setModelId("999901");
                subSysModelEntity1.setModelName(sysModelConsts.getNAME1());
                subSysModelEntity1.setUrl("javascript:void(0)");
                subSysModelEntity1.setParentModel(sysModelConsts.getZHZH_ID());

                SysModelEntity subSysModelEntity2 = new SysModelEntity();
                subSysModelEntity2.setModelId("999902");
                subSysModelEntity2.setModelName(sysModelConsts.getNAME2());
                subSysModelEntity2.setUrl("javascript:void(0)");
                subSysModelEntity2.setParentModel(sysModelConsts.getZHZH_ID());

                SysModelEntity subSysModelEntity3 = new SysModelEntity();
                subSysModelEntity3.setModelId("999903");
                subSysModelEntity3.setModelName(sysModelConsts.getNAME3());
                subSysModelEntity3.setUrl("javascript:void(0)");
                subSysModelEntity3.setParentModel(sysModelConsts.getZHZH_ID());

                for (int i = 0; i < e.getSubModelList().size(); i++) {
                    if (e.getSubModelList().get(i).getUrl().equals("zhiban2.html")) {
                        index1 = i;
                    }
                }
                e.getSubModelList().add(index1,subSysModelEntity1);

                for (int i = 0; i < e.getSubModelList().size(); i++) {
                    if (e.getSubModelList().get(i).getUrl().equals("yingji.html")) {
                        index2 = i;
                    }
                }
                e.getSubModelList().add(index2,subSysModelEntity2);

                for (int i = 0; i < e.getSubModelList().size(); i++) {
                    if (e.getSubModelList().get(i).getUrl().equals("huiyidianming.html")) {
                        index3 = i;
                    }
                }
                e.getSubModelList().add(index3,subSysModelEntity3);

            }
        });






        return resultSet;
    }

}
