package io.renren.modules.dm.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.dm.dao.DeptDao;
import io.renren.modules.dm.dao.YjdmDao;
import io.renren.modules.dm.dao.ZhiBanDao;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.dm.service.YjdmService;
import io.renren.modules.sys.entity.SysRoleEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service()
public class YjdmServiceImpl extends ServiceImpl<YjdmDao, YjdmEntity> implements YjdmService {

    @Autowired
    private YjdmDao yjdmDao;

    @Autowired
    private DeptDao deptDao;

    @Override
    @Transactional
    public void addYjdmBatch(List<YjdmEntity> yjdmEntityList) {

        yjdmEntityList.forEach(e -> {
            DeptEntity deptEntity = deptDao.selectOne(new QueryWrapper<DeptEntity>().eq("dept_id", e.getDeptId()));
            e.setDeptName(deptEntity.getDeptName());
            yjdmDao.insert(e);
        });

    }

    @Override
    public Page queryYjdmPage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        Page<Map<String, Object>> pageResult = yjdmDao.queryYjdmPage(pageParams);
        List<Map<String, Object>> list = pageResult.getRecords();
        for (Map<String, Object> map : list) {
            int total = Integer.parseInt(String.valueOf(map.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(map.get("yd1Total")));
            int yd2Total = Integer.parseInt(String.valueOf(map.get("yd2Total")));

            double yd1totalPrecent = NumberUtil.div(yd1Total, total,3);
            double yd2totalPrecent = NumberUtil.div(yd2Total, total,3);
            map.put("yd1totalPrecent", String.valueOf(yd1totalPrecent));
            map.put("yd2totalPrecent", String.valueOf(yd2totalPrecent));

        }

        return pageResult;
    }

    @Override
    public List<Map<String, Object>> queryYjdmReportDate(Map<String, Object> params) {

        List<Map<String, Object>> list = yjdmDao.queryYjdmReportDate(params);
        for (Map<String, Object> map : list) {
            int total = Integer.parseInt(String.valueOf(map.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(map.get("yd1Total")));
            int yd2Total = Integer.parseInt(String.valueOf(map.get("yd2Total")));

            double yd1totalPrecent = 0;
            double yd2totalPrecent = 0;
            if (total != 0) {
                yd1totalPrecent = NumberUtil.div(yd1Total, total,2);
                yd2totalPrecent = NumberUtil.div(yd2Total, total,2);
            }
            map.put("yd1totalPrecent", String.valueOf(yd1totalPrecent));
            map.put("yd2totalPrecent", String.valueOf(yd2totalPrecent));

        }

         return list;

    }

    @Override
    public IPage queryYjdmStatisticsByDatePage(Map<String, Object> params) {

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);
        IPage<Map<String, Object>> pageResult = yjdmDao.queryYjdmStatisticsByDatePage(pageParams, String.valueOf(params.get("startDate")), String.valueOf(params.get("endDate")));
        pageResult.getRecords().forEach(e -> {
            int total = Integer.parseInt(String.valueOf(e.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(e.get("yd1Total")));

            double yd1totalPrecent = 0.0;
            if (total != 0) {
                yd1totalPrecent = NumberUtil.mul(NumberUtil.div(yd1Total, total, 3), 100);
            }

            if (yd1totalPrecent == 0.0) {
                e.put("yd1totalPrecent", "0%");
            } else {
                e.put("yd1totalPrecent", yd1totalPrecent + "%");
            }

        });

        return pageResult;

    }

}
