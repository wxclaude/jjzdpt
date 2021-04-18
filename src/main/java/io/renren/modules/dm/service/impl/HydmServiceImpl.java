package io.renren.modules.dm.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.dm.dao.DeptDao;
import io.renren.modules.dm.dao.HydmDao;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.dm.service.HydmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service()
public class HydmServiceImpl extends ServiceImpl<HydmDao, HydmEntity> implements HydmService {

    @Autowired
    private HydmDao hydmDao;

    @Autowired
    private DeptDao deptDao;

    @Override
    public void addHydmBatch(List<HydmEntity> hydmEntityList) {
        hydmEntityList.forEach(e -> {
            DeptEntity deptEntity = deptDao.selectOne(new QueryWrapper<DeptEntity>().eq("dept_id", e.getDeptId()));
            e.setDeptName(deptEntity.getDeptName());
            hydmDao.insert(e);
        });
    }

    @Override
    public Page queryHydmPage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        Page<Map<String, Object>> pageResult = hydmDao.queryHydmPage(pageParams);
        List<Map<String, Object>> list = pageResult.getRecords();
        for (Map<String, Object> map : list) {
            int total = Integer.parseInt(String.valueOf(map.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(map.get("yd1Total")));
            int yd2Total = Integer.parseInt(String.valueOf(map.get("yd2Total")));

            double yd1totalPrecent = NumberUtil.div(yd1Total, total,4);
            double yd2totalPrecent = NumberUtil.div(yd2Total, total,4);
            map.put("yd1totalPrecent", String.valueOf(yd1totalPrecent));
            map.put("yd2totalPrecent", String.valueOf(yd2totalPrecent));

        }

        return pageResult;
    }

    @Override
    public List<Map<String, Object>> queryHydmReportDate(Map<String, Object> params) {
        List<Map<String, Object>> list = hydmDao.queryHydmReportDate(params);
        for (Map<String, Object> map : list) {
            int total = Integer.parseInt(String.valueOf(map.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(map.get("yd1Total")));
            int yd2Total = Integer.parseInt(String.valueOf(map.get("yd2Total")));

            int yd1totalPrecent = 0;
            int yd2totalPrecent = 0;
            if (total != 0) {
                yd1totalPrecent = (int) NumberUtil.mul(NumberUtil.div(yd1Total, total, 2), 100);
                yd2totalPrecent = (int) NumberUtil.mul(NumberUtil.div(yd2Total, total,2), 100);
            }
            map.put("yd1totalPrecent", String.valueOf(yd1totalPrecent) + "%");
            map.put("yd2totalPrecent", String.valueOf(yd2totalPrecent) + "%");

        }

        return list;
    }


}
