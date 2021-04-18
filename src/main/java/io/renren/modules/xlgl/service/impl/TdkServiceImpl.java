package io.renren.modules.xlgl.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.utils.MyUtils;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.xlgl.dao.TdkDao;
import io.renren.modules.xlgl.dao.TsbDao;
import io.renren.modules.xlgl.dao.TxlDao;
import io.renren.modules.xlgl.entity.TdkEntity;
import io.renren.modules.xlgl.entity.TsbEntity;
import io.renren.modules.xlgl.entity.TxlEntity;
import io.renren.modules.xlgl.service.TdkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


@Service("tdkService")
public class TdkServiceImpl extends ServiceImpl<TdkDao, TdkEntity> implements TdkService {

    @Autowired
    private TxlDao txlDao;

    @Autowired
    private TsbDao tsbDao;

    @Override
    @DataSource("slave2")
    public IPage<TdkEntity> queryDkByPage(Map<String, Object> params, List<String> sbIdList) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        //这里拼接sbId，在tdk中用in查询
        Page<Map<String,Object>> pageParams = new Page(page,limit);

        StringBuffer sbIdSb = new StringBuffer();
        sbIdList.forEach(e->{
            sbIdSb.append(e).append(",");
        });

        params.put("sbIds", MyUtils.formatSqlIn(sbIdSb.toString()));

        IPage<TdkEntity> tdkEntityIPage = this.baseMapper.queryDkByPage(pageParams, params);
        tdkEntityIPage.getRecords().stream().forEach(e->{
            List<TdkEntity> tdkEntityList = baseMapper.selectList(new QueryWrapper<TdkEntity>().eq("sb_id", e.getSbId()));
            if (CollectionUtil.isNotEmpty(tdkEntityList)) {
                e.setCreateTime(tdkEntityList.get(0).getCreateTime());
                e.setCreateBy(tdkEntityList.get(0).getCreateBy());
            }

            //设置板卡数
            List<Map<String,Object>> bksMaps = baseMapper.queryBksBySbId(e.getSbId());
            if (CollectionUtil.isNotEmpty(tdkEntityList)) {
                e.setBks(bksMaps.size());

            }

        });
        return tdkEntityIPage;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataSource("slave2")
    public void deleteDkBatch(List<TdkEntity> tdkEntityList) {
        tdkEntityList.forEach(e->{
            baseMapper.delete(new QueryWrapper<TdkEntity>().eq("sb_id", e.getSbId()));
            //删除线路
            txlDao.delete(new QueryWrapper<TxlEntity>().eq("sl_sb_id", e.getSbId()).or().eq("xl_sb_id", e.getSbId()));
        });
    }

    @Override
    @DataSource("slave2")
    public IPage<TdkEntity> queryDkDetailBySbIdPage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        //这里拼接sbId，在tdk中用in查询
        Page<TdkEntity> pageParams = new Page(page,limit);

        //获取该设备所有的端口号，
        IPage<TdkEntity> pageResult =  this.baseMapper.queryDkDetailBySbIdPage(pageParams, params);
        pageResult.getRecords().stream().forEach(e->{
            if (StrUtil.isNotEmpty(e.getXlbh1())) {
                e.setXlbh(e.getXlbh1());
                e.setDkType("上联端口");
            }else if(StrUtil.isNotEmpty(e.getXlbh2())){
                e.setXlbh(e.getXlbh2());
                e.setDkType("下联端口");
            }
        });
        return pageResult;
    }

    @Override
    @DataSource("slave2")
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchCC(List<TdkEntity> tdkEntityList) {
        tdkEntityList.stream().forEach(e->{
            baseMapper.insert(e);
        });

    }

    @Override
    @DataSource("slave2")
    public List<TdkEntity> queryDkBySbId(String sbId) {

        int page = 1;
        int limit = 99999;
        Page<TdkEntity> pageParams = new Page(page,limit);

        Map<String, Object> params = new HashMap<>();
        params.put("sbId", sbId);


        IPage<TdkEntity> tdkEntityIPage = baseMapper.queryDkDetailBySbIdPage(pageParams, params);
        tdkEntityIPage.getRecords().stream().forEach(e -> {
            if(StrUtil.isNotEmpty(e.getXlbh1())){
                e.setXlbh(e.getXlbh1());
            }
            if(StrUtil.isNotEmpty(e.getXlbh2())){
                e.setXlbh(e.getXlbh2());
            }

        });

        return tdkEntityIPage.getRecords();
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> querySbDkDetailBySbId(Map<String, Object> params) {

        return tsbDao.querySbDkDetailBySbId(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryDkBkhBySbId(Map<String, Object> params) {
        return baseMapper.querySbSearchCondition(params);
    }

}
