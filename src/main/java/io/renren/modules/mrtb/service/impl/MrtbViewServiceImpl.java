package io.renren.modules.mrtb.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.mrtb.dao.MrtbDao;
import io.renren.modules.mrtb.dao.MrtbViewDao;
import io.renren.modules.mrtb.dao.MrtbXsjqDao;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.mrtb.entity.ZhMrtbView;
import io.renren.modules.mrtb.entity.ZhMrtbXsjq;
import io.renren.modules.mrtb.service.MrtbService;
import io.renren.modules.mrtb.service.MrtbViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MrtbViewServiceImpl extends ServiceImpl<MrtbViewDao, ZhMrtbView> implements MrtbViewService {



}
