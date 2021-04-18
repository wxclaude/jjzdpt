package io.renren.modules.df.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.df.dao.DfConfigDao;
import io.renren.modules.df.dao.DfDetailDao;
import io.renren.modules.df.dao.DfRecordDao;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.entity.DfDetail;
import io.renren.modules.df.entity.DfRecord;
import io.renren.modules.df.service.DfConfigService;
import io.renren.modules.df.service.DfRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DfRecordServiceImpl extends ServiceImpl<DfRecordDao, DfRecord> implements DfRecordService {

    @Autowired
    private DfDetailDao dfDetailDao;
    @Autowired
    private DfConfigDao dfConfigDao;

    @Override
    public IPage<DfRecord> queryDfRecord(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        String date = MapUtil.getStr(params, "date");
        boolean isSearchByMonth = StrUtil.isNotEmpty(date);

        Page<DfRecord> pageParams = new Page(page,limit);
        return baseMapper.selectPage(pageParams, new QueryWrapper<DfRecord>().eq(isSearchByMonth, "df_month", date).orderByDesc("create_time"));

    }

    @Override
    public Map<String, Object> queryDfRecordDetailBeforeEdit(Map<String, Object> params) {

        //
        Map<String, Object> mapResult = new HashMap<>();
        int recordId = MapUtil.getInt(params, "recordId");
        DfRecord dfRecord = baseMapper.selectById(recordId);


        return mapResult;
    }

    @Override
    public DfRecord queryDfDetailByRecordId(Map<String, Object> params) {
        Map<String, Object> mapResult = new HashMap<>();
        int recordId = MapUtil.getInt(params, "recordId");
        DfRecord dfRecord = baseMapper.selectById(recordId);

        if (dfRecord == null) {
            throw new RRException("打分记录不存在，请联系管理员");
        }

        List<DfDetail> detailList = dfDetailDao.selectList(new QueryWrapper<DfDetail>().eq("record_id", recordId).orderByAsc("LENGTH(dept_id)").orderByAsc("dept_id").orderByAsc("config_id"));
        if (CollectionUtil.isEmpty(detailList)) {
            throw new RRException("打分详情不存在，请联系管理员");
        }

        dfRecord.setDfDetailList(new ArrayList<>());

        Map<String, List<DfDetail>> detailGroupMap = detailList.stream().collect(Collectors.groupingBy(e -> e.getDeptId(), LinkedHashMap::new, Collectors.toList()));
        Map<String, Object> detailMap = null;
        BigDecimal totalScore = null;
        for (Map.Entry<String, List<DfDetail>> entry : detailGroupMap.entrySet()) {
            detailMap = new LinkedHashMap<>();
            detailMap.put("deptId", entry.getKey());

            totalScore = new BigDecimal("0");
            for (DfDetail dfDetail : entry.getValue()) {
                detailMap.put("recordId", dfDetail.getRecordId());
                detailMap.put("dfx_" + dfDetail.getConfigId(), dfDetail.getScore() == null ? "" : dfDetail.getScore());
                totalScore = NumberUtil.add(totalScore.toString(), dfDetail.getScoreQzb());
            }
            //设置带权重的总分
            detailMap.put("total", totalScore.setScale(2).doubleValue());
            dfRecord.getDfDetailList().add(detailMap);
        }


        return dfRecord;
    }

    @Override
    public List<Map<String, Object>> queryDfTheadByRecordId(int recordId) {

        List<Map<String, Object>> list = dfDetailDao.queryDfTheadByRecordId(recordId);
        list.stream().forEach(e->{
            e.put("configId", "dfx_" + MapUtil.getStr(e, "configId"));
        });
        return list;
    }

    @Override
    public List<Map<String, Object>> generateParentConfigList(List<Map<String, Object>> configList) {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, List<Map<String, Object>>> parentIdGroupList = configList.stream().collect(Collectors.groupingBy(e -> MapUtil.getStr(e, "parentId"), LinkedHashMap::new, Collectors.toList()));


        Map<String, Object> parentMap = null;
        DfConfig parentConfig = null;
        int parentQzb = 0;

        for (Map.Entry<String, List<Map<String, Object>>> entry : parentIdGroupList.entrySet()) {
            parentConfig = dfConfigDao.selectById(entry.getKey());
            parentMap = new HashMap<>();

            parentQzb = entry.getValue().stream().mapToInt(e -> Integer.parseInt(String.valueOf(e.get("qzb")))).sum();

            parentMap.put("configName", parentConfig.getDfx() + "(" + parentQzb + "%" + ")");
            parentMap.put("colspan", entry.getValue().stream().count());
            parentMap.put("id", parentConfig.getId());


            list.add(parentMap);
        }


        return list;
    }

    @Override
    public DfRecord queryDfDetailByDfMonthAndParentConfigId(Map<String, Object> params) {
        Map<String, Object> mapResult = new HashMap<>();
        String dfMonth = MapUtil.getStr(params, "dfMonth");
        String parentConfigId = MapUtil.getStr(params, "parentConfigId");
        DfRecord dfRecord = baseMapper.selectOne(new QueryWrapper<DfRecord>().eq("df_month", dfMonth).last("limit 1"));

        if (dfRecord == null) {
            throw new RRException("打分记录不存在，请联系管理员");
        }

        List<DfDetail> detailList = dfDetailDao.selectList(new QueryWrapper<DfDetail>().eq("record_id", dfRecord.getId()).eq("parent_config_id", parentConfigId).orderByAsc("LENGTH(dept_id)").orderByAsc("dept_id").orderByAsc("config_id"));
        if (CollectionUtil.isEmpty(detailList)) {
            throw new RRException("打分详情不存在，请联系管理员");
        }

        dfRecord.setDfDetailList(new ArrayList<>());

        Map<String, List<DfDetail>> detailGroupMap = detailList.stream().collect(Collectors.groupingBy(e -> e.getDeptId(), LinkedHashMap::new, Collectors.toList()));

        Map<String, Object> detailMap = null;
        BigDecimal totalScore = null;
        for (Map.Entry<String, List<DfDetail>> entry : detailGroupMap.entrySet()) {
            detailMap = new LinkedHashMap<>();
            detailMap.put("deptId", entry.getKey());

            totalScore = new BigDecimal("0");
            for (DfDetail dfDetail : entry.getValue()) {
                detailMap.put("recordId", dfDetail.getRecordId());
                detailMap.put("dfx_" + dfDetail.getConfigId(), dfDetail.getScore() == null ? "" : dfDetail.getScore());
                totalScore = NumberUtil.add(totalScore.toString(), dfDetail.getScoreQzb());
            }
            //设置带权重的总分
            detailMap.put("total", totalScore);
            dfRecord.getDfDetailList().add(detailMap);
        }


        return dfRecord;
    }

}
