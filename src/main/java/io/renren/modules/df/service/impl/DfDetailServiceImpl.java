package io.renren.modules.df.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.modules.df.consts.DfConsts;
import io.renren.modules.df.dao.*;
import io.renren.modules.df.entity.*;
import io.renren.modules.df.service.DfDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DfDetailServiceImpl extends ServiceImpl<DfDetailDao, DfDetail> implements DfDetailService {

    @Autowired
    private DfRecordDao dfRecordDao;

    @Autowired
    private DfConfigDao dfConfigDao;

    @Autowired
    private DfRuleDao dfRuleDao;

    @Autowired
    private DfQbxxDao dfQbxxDao;

    @Autowired
    private DfSjfnDao dfSjfnDao;

    @Autowired
    private DfZhzhDao dfZhzhDao;

    @Autowired
    private DfJqzlkhDao dfJqzlkhDao;

    @Autowired
    private DfQbxsDao qbxsDao;

    @Autowired
    private DfZdmbDao dfZdmbDao;

    @Autowired
    private DfConsts dfConsts;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateDfRecordAndDetail(DfRecord dfRecord, String name) {

        DfRule dfRule = dfRuleDao.selectById(dfRecord.getRuleId());
        if (dfRule == null) {
            throw new RRException("打分配置不存在，请联系管理员");
        }

        //1生成打分dfRecord
        dfRecord.setCreateBy(name);
        dfRecord.setTitle(dfRecord.getDfMonth() + "" + dfRule.getTitle());

        int i = dfRecordDao.insertOne(dfRecord);
        if (i <= 0) {
            throw new RRException("生成打分记录异常，请联系管理员");
        }

        List<String> configList = StrUtil.split(dfRule.getConfigIds(), ',');
        List<String> deptIdList = StrUtil.split(dfRule.getDeptIds(), ',');
        DfConfig dfConfig = null;
        DfDetail dfDetail = null;
        for (String configId : configList) {

            if (StrUtil.isEmpty(configId)) {
                continue;
            }

            dfConfig = dfConfigDao.selectById(configId);
            if (dfConfig.getType() == 0) {//父节点，跳过
                continue;
            }

            for (String deptId : deptIdList) {

                if (StrUtil.isEmpty(deptId)) {
                    continue;
                }

                dfDetail = new DfDetail();
                dfDetail.setConfigId(Integer.parseInt(configId));
                dfDetail.setDeptId(deptId);
                dfDetail.setCreateBy(name);
                dfDetail.setRecordId(dfRecord.getId());
                dfDetail.setParentConfigId(dfConfig.getParentId());

                baseMapper.insert(dfDetail);

            }
        }

        /*
        //2根据deptIds和parentConfigIds循环生成dfDetial
        List<String> parentConfigList = StrUtil.split(dfRecord.getParentConfigIds(), ',');
        List<String> deptIdList = StrUtil.split(dfRecord.getDeptIds(), ',');
        List<DfConfig> dfConfigList = null;
        DfDetail dfDetail = null;

        for (String parentConfigId : parentConfigList) {

            if (StrUtil.isEmpty(parentConfigId)) {
                continue;
            }

            //
            dfConfigList = dfConfigDao.selectList(new QueryWrapper<DfConfig>().eq("type", 1));

            for (DfConfig dfConfig : dfConfigList) {
                for (String deptId : deptIdList) {

                    if (StrUtil.isEmpty(deptId)) {
                        continue;
                    }

                    dfDetail = new DfDetail();
                    dfDetail.setConfigId(dfConfig.getId());
                    dfDetail.setParentConfigId(Integer.parseInt(parentConfigId));
                    dfDetail.setDeptId(deptId);
                    dfDetail.setCreateBy(name);

                    baseMapper.insert(dfDetail);

                }
            }

        }
        */


        return dfRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDfDetail(List<DfDetailForm> dfDetailFormList, Map<String, Object> params) {
        // 1批量更新df_detail

        dfDetailFormList.stream().forEach(e->{

            if (e.getScore() == null) {
                return;
            }

            // 根据configId
            DfConfig dfConfig = dfConfigDao.selectById(e.getConfigId());
            BigDecimal qzbDouble = NumberUtil.div(String.valueOf(dfConfig.getQzb()), "100", 2);

            //这里不设置带全占比的分数，而是每次计算带全占比的分数
            //e.setScoreQzb(String.valueOf(NumberUtil.mul(String.valueOf(e.getScore()), qzbDouble.toString()).setScale(1)));

            int i = baseMapper.updateDfDetailForm(e);
            if (i <= 0) {
                throw new RRException("保存打分详情异常，请联系管理员");
            }
        });


        // 2修改df_record
        String dfMonth = MapUtil.getStr(params, "dfMonth");
        int state = MapUtil.getInt(params, "state");
        int recordId = MapUtil.getInt(params, "recordId");

        DfRecord dfRecord = dfRecordDao.selectById(recordId);
        if (dfRecord == null) {
            throw new RRException("打分记录不存在，请联系管理员");
        }

        //DfRule dfRule = dfRuleDao.selectById(dfRecord.getRuleId());
        //if (dfRule == null) {
        //    throw new RRException("打分配置不存在，请联系管理员");
        //}

        //dfRecord.setDfMonth(dfMonth);
        dfRecord.setState(state);
        //dfRecord.setTitle(dfMonth + dfRule.getTitle());

        int i = dfRecordDao.updateById(dfRecord);
        if (i <= 0) {
            throw new RRException("保存打分记录异常，请联系管理员");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDfRecordAndDetail(int recordId) {
        dfRecordDao.deleteById(recordId);
        baseMapper.delete(new QueryWrapper<DfDetail>().eq("record_id", recordId));
    }

    @Override
    public List<Map<String, Object>> queryDfDetailByDeptIdAndDate(String dfMonth, String deptId) {
        Map<String, Object> params = new HashMap<>();
        params.put("dfMonth", dfMonth);
        params.put("deptId", deptId);

        List<Map<String, Object>> list = baseMapper.queryDfDetailByDeptIdAndDate(params);

        return list;
    }

    @Override
    public List<Map<String, Object>> queryDfDetailByDeptIdAndDateNew(String dfMonth, String deptId, String type) {

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("configName", "情报信息");
        map1.put("totalScore", "");
        DfQbxx dfQbxx = dfQbxxDao.selectOne(new QueryWrapper<DfQbxx>()
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (dfQbxx != null) {
            map1.put("totalScore", NumberUtil.mul(dfQbxx.getS9(), dfConsts.getQBXX()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        list.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("configName", "数据赋能");
        map2.put("totalScore", "");
        List<Map<String, Object>> maps2 = dfSjfnDao.selectMaps(new QueryWrapper<DfSjfn>()
                .select("sum(ifnull(s2,0)+ifnull(s4,0)+ifnull(s6,0)+ifnull(s17,0)) as totalScore")
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (CollectionUtil.isNotEmpty(maps2)) {
            map2.put("totalScore", NumberUtil.mul(MapUtil.getStr(maps2.get(0), "totalScore"), dfConsts.getSJFN()).setScale(2,BigDecimal.ROUND_HALF_UP));

        }
        list.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("configName", "综合指挥");
        map3.put("totalScore", "");
        List<Map<String, Object>> maps3 = dfZhzhDao.selectMaps(new QueryWrapper<DfZhzh>()
                //.select("sum(ifnull(s4,0)+ifnull(s7,0)+ifnull(s10,0)+ifnull(s13,0)+ifnull(s15,0)+ifnull(s17,0)) as totalScore")
                .select("sum(ifnull(s99,0)) as totalScore")
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (CollectionUtil.isNotEmpty(maps3)) {
            map3.put("totalScore", NumberUtil.mul(MapUtil.getStr(maps3.get(0), "totalScore"), dfConsts.getZHZH()).setScale(2,BigDecimal.ROUND_HALF_UP));

        }
        list.add(map3);

        Map<String, Object> map4 = new HashMap<>();
        map4.put("configName", "警情质量考核");
        map4.put("totalScore", "");
        List<Map<String, Object>> maps4 = dfJqzlkhDao.selectMaps(new QueryWrapper<DfJqzlkh>()
                .select("sum(ifnull(s3,0)) as totalScore")
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (CollectionUtil.isNotEmpty(maps4)) {
            map4.put("totalScore", NumberUtil.mul(MapUtil.getStr(maps4.get(0), "totalScore"), dfConsts.getJQZLKH()).setScale(2,BigDecimal.ROUND_HALF_UP));

        }
        list.add(map4);

        Map<String, Object> map5 = new HashMap<>();
        map5.put("configName", "情报线索核查处置");
        map5.put("totalScore", "");
        List<Map<String, Object>> maps5 = qbxsDao.selectMaps(new QueryWrapper<DfQbxs>()
                .select("sum(ifnull(s1,0)+ifnull(s2,0)-ifnull(s3,0)) as totalScore")
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (CollectionUtil.isNotEmpty(maps5)) {
            map5.put("totalScore", NumberUtil.mul(MapUtil.getStr(maps5.get(0), "totalScore"), dfConsts.getQBXS()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        list.add(map5);

        Map<String, Object> map6 = new HashMap<>();
        map6.put("configName", "重点目标基础信息采集");
        map6.put("totalScore", "");
        List<Map<String, Object>> maps6 = dfZdmbDao.selectMaps(new QueryWrapper<DfZdmb>()
                .select("sum(ifnull(s1,0)) as totalScore")
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (CollectionUtil.isNotEmpty(maps6)) {
            map6.put("totalScore", NumberUtil.mul(MapUtil.getStr(maps6.get(0), "totalScore"), dfConsts.getZDMB()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        list.add(map6);

        Map<String, Object> map7 = new HashMap<>();
        map7.put("configName", "科技信息化");
        map7.put("totalScore", "");
        List<Map<String, Object>> maps7 = dfSjfnDao.selectMaps(new QueryWrapper<DfSjfn>()
                .select("sum(ifnull(s16,0)+ifnull(s18,0)+ifnull(s19,0)+ifnull(s20,0)+ifnull(s21,0)+ifnull(s22,0)) as totalScore")
                .eq("df_month", dfMonth)
                .eq("type", type)
                .eq("dept_id", deptId)
                .last("limit 1"));
        if (CollectionUtil.isNotEmpty(maps7)) {
            map7.put("totalScore", NumberUtil.mul(MapUtil.getStr(maps7.get(0), "totalScore"), dfConsts.getKJXX()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        list.add(map7);

        return list;

    }

}
