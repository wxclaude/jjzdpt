package io.renren.modules.xlgl.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.xlgl.dao.TdkDao;
import io.renren.modules.xlgl.dao.TsbDao;
import io.renren.modules.xlgl.dao.TxlDao;
import io.renren.modules.xlgl.entity.TdkEntity;
import io.renren.modules.xlgl.entity.TsbEntity;
import io.renren.modules.xlgl.service.TsbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;


@Service("tsbService")
public class TsbServiceImpl extends ServiceImpl<TsbDao, TsbEntity> implements TsbService {

    @Autowired
    private TdkDao tdkDao;

    @Autowired
    private TxlDao txlDao;


    @Override
    public IPage<TsbEntity> querySbByPage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        return this.baseMapper.querySbByPage(pageParams, params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSbBatch(List<TsbEntity> tsbEntityList) {

        tsbEntityList.forEach(e -> {

            baseMapper.deleteById(e.getId());

            //删除相关线路
            txlDao.deleteBySbId(e.getId());

            //删除相关端口
            tdkDao.delete(new QueryWrapper<TdkEntity>().eq("sb_id", e.getId()));

        });

    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String,Object>> querySbByPageNew(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);
        return baseMapper.querySbByPageNew(pageParams,params);
    }

    @Override
    @DataSource("slave2")
    public List<String> querySbIdAll(Map<String, Object> params) {
        return baseMapper.querySbIdAll(params);
    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String, Object>> querySbByPageST(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);
        IPage<Map<String, Object>> pageResult = baseMapper.querySbByPageST(pageParams, params);
        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> querySbSearchCondition(int type) {

        return baseMapper.querySbSearchCondition(type);
    }

    @Override
    @DataSource("slave2")
    public Map<String, Object> querySbCount(Map<String, Object> params) {

        return baseMapper.querySbCount(params);
    }


}
