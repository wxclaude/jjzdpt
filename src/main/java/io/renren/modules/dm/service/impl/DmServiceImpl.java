package io.renren.modules.dm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.dm.consts.DmConsts;
import io.renren.modules.dm.dao.SqmjDao;
import io.renren.modules.dm.dao.ZhiBanDao;
import io.renren.modules.dm.entity.*;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.sys.dao.SysUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Service("dmService")
public class DmServiceImpl extends ServiceImpl<ZhiBanDao, DianMingEntity> implements DmService {

    @Autowired
    private ZhiBanDao zhiBanDao;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SqmjDao sqmjDao;

    @Override
    @DataSource("slave2")
    public List<Map<String,Object>> queryZhiBanDataByDate(String date, String deptId) {

        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        if(DmConsts.pcs16DeptIdList.contains(deptId)){
            params.put("pcs", 1);
        }

        List<Map<String, Object>> result = null;
        if (!StrUtil.contains(deptId, '-')) {
            params.put("deptId", deptId);
            result = zhiBanDao.queryZhiBanDataByDate(params);
        } else {
            String deptIdOld = deptId;
            params.put("deptId",StrUtil.subBefore(deptId, "-", true));
            params.put("reportPointId",StrUtil.subAfter(deptId, "-", true));
            result = zhiBanDao.queryPickXtj3ZhiBanDataByDate(params);
            result.forEach(e->{
                e.put("deptId", deptIdOld);
                e.put("deptName",DmConsts.XTJ_NAME.get(params.get("reportPointId")));
            });
        }

        return result;
    }

    @Override
    @Transactional()
    public void addDianMingBatch(List<DianMingEntity> dianMingEntityList) {
        for (DianMingEntity dianMingEntity : dianMingEntityList) {
            zhiBanDao.insert(dianMingEntity);
        }
    }

    @Override
    public List<Map<String, Object>> getLastTwoDayDianMingData(String policeCode) {
        return zhiBanDao.getLastTwoDayDianMingData(policeCode);
    }

    @Override
    @Transactional()
    public void updateDianMingBatch(List<DianMingEntity> dianMingEntityList) {

        zhiBanDao.updateDianMingBatch(dianMingEntityList);
    }

    @Override
    public List<DianMingEntity> queryDmDataByCondition(Map<String, Object> params) {
        return zhiBanDao.queryDmDataByCondition(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryZhiBanAllByDate(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("pcs", 1);

        List<Map<String, Object>> list = zhiBanDao.queryZhiBanAllByDate(params);
        List<Map<String, Object>> result = new ArrayList<>();

        Set<String> keySet = new LinkedHashSet<>();
        for (Map<String, Object> map : list) {
            keySet.add(String.valueOf(map.get("deptName")));
        }

        String today = DateUtil.format(new Date(),"yyyy-MM-dd");

        StringBuffer leaderSb = new StringBuffer();
        StringBuffer policeSb = new StringBuffer();
        StringBuffer zsLeaderSb = new StringBuffer();
        String leaderStr = null;
        String policeStr = null;
        String zsLeaderStr = null;
        String deptId = null;
        List<Map<String, Object>> leaderList = new ArrayList<>();
        List<Map<String, Object>> policeList = new ArrayList<>();
        List<Map<String, Object>> zsLeaderList = new ArrayList<>();

        for (String keyStr : keySet) {
            Map<String, Object> dwMap = new LinkedHashMap<>();
            dwMap.put("dw",keyStr);
            leaderSb = new StringBuffer();
            policeSb = new StringBuffer();
            zsLeaderSb = new StringBuffer();
            leaderStr = "";
            policeStr = "";
            zsLeaderStr = "";
            deptId = "";
            leaderList = new ArrayList<>();
            policeList = new ArrayList<>();
            zsLeaderList = new ArrayList<>();

            for (Map<String, Object> map : list) {
                if(keyStr.equals(String.valueOf(map.get("deptName")))){
                    deptId = String.valueOf(map.get("deptId"));
                }
                if(keyStr.equals(String.valueOf(map.get("deptName"))) && map.get("isLeader") != null){
                    int isLeader = (int) map.get("isLeader");

                    if (isLeader == 1) {
                        leaderSb.append(map.get("police")).append("(").append(map.get("djj") == null ? "" : map.get("djj")).append("),");
                        leaderList.add(map);
                    }else if (isLeader == 0){
                        policeSb.append(map.get("police")).append("(").append(map.get("djj") == null ? "" : map.get("djj")).append("),");
                        policeList.add(map);
                    }else if (isLeader == 2){
                        zsLeaderSb.append(map.get("police")).append("(").append(map.get("djj") == null ? "" : map.get("djj")).append("),");
                        zsLeaderList.add(map);
                    }
                }
            }
            if(StrUtil.isNotEmpty(leaderSb)){
                leaderStr = leaderSb.substring(0, leaderSb.length() - 1);
            }
            if(StrUtil.isNotEmpty(policeSb)){
                policeStr = policeSb.substring(0, policeSb.length() - 1);
            }
            if(StrUtil.isNotEmpty(zsLeaderSb)){
                zsLeaderStr = zsLeaderSb.substring(0, zsLeaderSb.length() - 1);
            }

            dwMap.put("deptId",deptId);
            dwMap.put("leaderStr",leaderStr);
            dwMap.put("policeStr",policeStr);
            dwMap.put("zsLeaderStr",zsLeaderStr);
            dwMap.put("zhibanDate",today);
            dwMap.put("leaderList",leaderList);
            dwMap.put("policeList",policeList);
            dwMap.put("zsLeaderList",zsLeaderList);

            result.add(dwMap);
        }

        return result;
    }

    @Override
    public Page<Map<String,Object>> queryDMPage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        Page<Map<String, Object>> pageResult = zhiBanDao.queryDMPage(pageParams);
        List<Map<String, Object>> list = pageResult.getRecords();
        for (Map<String, Object> map : list) {
            int total = Integer.parseInt(String.valueOf(map.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(map.get("yd1Total")));
            int yd2Total = Integer.parseInt(String.valueOf(map.get("yd2Total")));
            int yd3Total = Integer.parseInt(String.valueOf(map.get("yd3Total")));

            double yd1totalPrecent = NumberUtil.div(yd1Total, total,3);
            double yd2totalPrecent = NumberUtil.div(yd2Total, total,3);
            double yd3totalPrecent = NumberUtil.div(yd3Total, total,3);
            map.put("yd1totalPrecent", String.valueOf(yd1totalPrecent));
            map.put("yd2totalPrecent", String.valueOf(yd2totalPrecent));
            map.put("yd3totalPrecent", String.valueOf(yd3totalPrecent));

        }

        return pageResult;
    }

    @Override
    public IPage queryDMByDatePage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        IPage<Map<String, Object>> pageResult = zhiBanDao.queryDMByDatePage(pageParams, String.valueOf(params.get("startDate")), String.valueOf(params.get("endDate")));
        List<Map<String, Object>> list = pageResult.getRecords();
        for (Map<String, Object> map : list) {
            int total = Integer.parseInt(String.valueOf(map.get("total")));
            int yd1Total = Integer.parseInt(String.valueOf(map.get("yd1Total")));
            int yd2Total = Integer.parseInt(String.valueOf(map.get("yd2Total")));
            int yd3Total = Integer.parseInt(String.valueOf(map.get("yd3Total")));

            double yd1totalPrecent = 0;
            double yd2totalPrecent = 0;
            double yd3totalPrecent = 0;
            double yd4totalPrecent = 0;
            if (total != 0) {
                yd1totalPrecent = NumberUtil.div(yd1Total, total,3);
                yd2totalPrecent = NumberUtil.div(yd2Total, total,3);
                yd3totalPrecent = NumberUtil.div(yd3Total, total,3);
                yd4totalPrecent = NumberUtil.add(yd1totalPrecent, yd3totalPrecent);
            }

            map.put("yd1totalPrecent", String.valueOf(yd1totalPrecent));
            map.put("yd2totalPrecent", String.valueOf(yd2totalPrecent));
            map.put("yd3totalPrecent", String.valueOf(yd3totalPrecent));
            map.put("yd4totalPrecent", String.valueOf(yd4totalPrecent));

        }
        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public Map<String, Object> queryZhiBanLeaderByDate(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("leader1", "");
        resultMap.put("leader2", "");
        resultMap.put("leader3", "");
        resultMap.put("leader4", "");
        resultMap.put("leader1pic", "");
        resultMap.put("leader2pic", "");
        resultMap.put("leader3pic", "");
        resultMap.put("leader4pic", "");

        List<Map<String,Object>> list = zhiBanDao.queryZhiBanLeaderByDate(params);
        for (Map<String, Object> map : list) {
            int isLeader = Integer.parseInt(String.valueOf(map.get("isLeader")));
            if (isLeader == 1) {
                resultMap.put("leader1", map.get("police"));
                resultMap.put("leader1pic", map.get("pic"));
            }else if (isLeader == 2) {
                resultMap.put("leader2", map.get("police"));
                resultMap.put("leader2pic", map.get("pic"));
            }else if (isLeader == 3) {
                resultMap.put("leader3", map.get("police"));
                resultMap.put("leader3pic", map.get("pic"));
            }else if (isLeader == 4) {
                resultMap.put("leader4", map.get("police"));
                resultMap.put("leader4pic", map.get("pic"));
            }
        }

        return resultMap;
    }

    @Override
    @DataSource("slave2")
    public Map<String,Object> queryUserSqlServer(String policeCode) {
        return BeanUtil.beanToMap(sysUserDao.queryUserSqlServer(policeCode));
    }

    @Override
    public List<Map<String, Object>> handerRepeatPolice(List<Map<String, Object>> list){
        List<Map<String, Object>> result = new ArrayList<>();

        //值班民警 社区民警中去重值班领导
        List<Map<String, Object>> leaderList = list.stream().filter(e -> Integer.parseInt(String.valueOf(e.get("isLeader"))) == 1).collect(Collectors.toList());
        List<Map<String, Object>> zbPoliceList = list.stream().filter(e -> Integer.parseInt(String.valueOf(e.get("isLeader"))) == 0).collect(Collectors.toList());
        List<Map<String, Object>> sqPoliceList = list.stream().filter(e -> Integer.parseInt(String.valueOf(e.get("isLeader"))) == -1).collect(Collectors.toList());

        leaderList.forEach(e -> {
            String leaderCode = String.valueOf(e.get("policeCode"));
            Iterator<Map<String, Object>> it = zbPoliceList.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = it.next();
                if (leaderCode.equals(String.valueOf(map.get("policeCode")))) {
                    it.remove();
                }
            }

            it = sqPoliceList.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = it.next();
                if (leaderCode.equals(String.valueOf(map.get("policeCode")))) {
                    it.remove();
                }
            }

        });

        //社区民警中去重值班民警
        zbPoliceList.forEach(e -> {
            String zbPoliceCode = String.valueOf(e.get("policeCode"));
            Iterator<Map<String, Object>> it = sqPoliceList.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = it.next();
                if (zbPoliceCode.equals(String.valueOf(map.get("policeCode")))) {
                    it.remove();
                }
            }
        });

        result.addAll(leaderList);
        result.addAll(zbPoliceList);
        result.addAll(sqPoliceList);
        return result;
    }

    @Override
    public List<Map<String,Object>> queryQsmjByDeptId(String deptId) {
        List<SqmjEntity> list = sqmjDao.selectList(new QueryWrapper<SqmjEntity>().eq("dept_id", deptId));
        List<Map<String, Object>> result = new ArrayList<>();

        list.forEach(e -> {
            result.add(BeanUtil.beanToMap(e));
        });

        return result;

    }

    @Override
    public List<Map<String, Object>> queryDMStatisticsDetailByDatePage(Map<String, Object> params) {
        return zhiBanDao.queryDMStatisticsDetailByDatePage(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryXtjZhiBanDataByReportPointIdAndDate(Map<String, Object> paramsMap) {
        return zhiBanDao.queryPickXtj3ZhiBanDataByDate(paramsMap);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String,Object>> queryZhiBanZhangByDate(String date) {
        return zhiBanDao.queryZhiBanZhangByDate(date);
    }

    @Override
    public String getDmTimeByTbrq(String tbrqParam) {
        return zhiBanDao.getDmTimeByTbrq(tbrqParam);
    }
}
