package io.renren.modules.dm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.dm.dao.DeptDao;
import io.renren.modules.dm.dao.TzbwbzDao;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.TzbwbzEntity;
import io.renren.modules.dm.service.DeptService;
import io.renren.modules.dm.service.TzbwbzService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Service
public class TzbwbzServiceImpl extends ServiceImpl<TzbwbzDao, TzbwbzEntity> implements TzbwbzService {

    @Override
    public List<Map<String, Object>> queryTzbwbzSta(Map<String, Object> params) {
        return baseMapper.queryTzbwbzSta(params);
    }
}
