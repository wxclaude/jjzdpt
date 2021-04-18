package io.renren.modules.mrtb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.mrtb.dao.MrtbViewDao;
import io.renren.modules.mrtb.dao.TXxDao;
import io.renren.modules.mrtb.entity.TXx;
import io.renren.modules.mrtb.entity.ZhMrtbView;
import io.renren.modules.mrtb.service.MrtbViewService;
import io.renren.modules.mrtb.service.TXxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service
@Slf4j
//@DataSource("slave3")
public class TXxServiceImpl extends ServiceImpl<TXxDao, TXx> implements TXxService {


    @Override
    @DataSource("slave3")
    public boolean saveCC(TXx tXx) {
        return baseMapper.saveCC(tXx);
    }

    @Override
    @DataSource("slave3")
    public TXx getByIdCC(Serializable xxId) {
        return baseMapper.selectById(xxId);
    }

    @Override
    @DataSource("slave3")
    public boolean updateByIdCC(TXx tXx) {
        return baseMapper.updateById(tXx) > 0 ? true : false;
    }
}
