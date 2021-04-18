package io.renren.modules.jcj.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.jcj.dao.JqCjBhgDao;
import io.renren.modules.jcj.dao.JqCjXcrDao;
import io.renren.modules.jcj.entity.JqCjBhgEntity;
import io.renren.modules.jcj.entity.JqCjXcrEntity;
import io.renren.modules.jcj.service.JqCjBhgService;
import io.renren.modules.jcj.service.JqCjXcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class JqCjXcrServiceImpl extends ServiceImpl<JqCjXcrDao, JqCjXcrEntity> implements JqCjXcrService {

    @Autowired
    private JqCjXcrDao jqCjXcrDao;

}
