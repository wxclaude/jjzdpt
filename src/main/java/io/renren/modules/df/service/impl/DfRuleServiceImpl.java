package io.renren.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.df.dao.DfConfigDao;
import io.renren.modules.df.dao.DfRuleDao;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.entity.DfRule;
import io.renren.modules.df.service.DfConfigService;
import io.renren.modules.df.service.DfRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class DfRuleServiceImpl extends ServiceImpl<DfRuleDao, DfRule> implements DfRuleService {



}
