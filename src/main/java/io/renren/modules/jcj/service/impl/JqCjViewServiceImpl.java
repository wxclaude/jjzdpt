package io.renren.modules.jcj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.dao.JjDao;
import io.renren.modules.jcj.dao.JqCjViewDao;
import io.renren.modules.jcj.entity.JcjJjxx;
import io.renren.modules.jcj.entity.JqCjViewEntity;
import io.renren.modules.jcj.service.JjService;
import io.renren.modules.jcj.service.JqCjViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JqCjViewServiceImpl extends ServiceImpl<JqCjViewDao, JqCjViewEntity> implements JqCjViewService {

    @Autowired
    private JqCjViewDao jqCjViewDao;

}
