package io.renren.modules.df.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.df.dao.DfQbxxDao;
import io.renren.modules.df.dao.DfSjfnDao;
import io.renren.modules.df.entity.DfQbxx;
import io.renren.modules.df.entity.DfSjfn;
import io.renren.modules.df.service.DfQbxxService;
import io.renren.modules.df.service.DfSjfnService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class DfSjfnServiceImpl extends ServiceImpl<DfSjfnDao, DfSjfn> implements DfSjfnService {



    @Override
    public List<Map<String, Object>> queryDfSjfnData(Map<String, Object> params) {
        return baseMapper.queryDfSjfnData(params);
    }

}
