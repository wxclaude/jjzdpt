package io.renren.modules.dm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.dm.consts.DmConsts;
import io.renren.modules.dm.dao.DeptDao;
import io.renren.modules.dm.dao.SqmjDao;
import io.renren.modules.dm.dao.ZhiBanDao;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.SqmjEntity;
import io.renren.modules.dm.service.DeptService;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.sys.dao.SysUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Service()
public class DeptServiceImpl extends ServiceImpl<DeptDao, DeptEntity> implements DeptService {


}
