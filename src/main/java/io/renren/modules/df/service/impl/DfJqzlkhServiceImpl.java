package io.renren.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.df.dao.DfConfigDao;
import io.renren.modules.df.dao.DfJqzlkhDao;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.entity.DfJqzlkh;
import io.renren.modules.df.service.DfConfigService;
import io.renren.modules.df.service.DfJqzlkhService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class DfJqzlkhServiceImpl extends ServiceImpl<DfJqzlkhDao, DfJqzlkh> implements DfJqzlkhService {


}
