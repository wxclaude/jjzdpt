package io.renren.modules.dm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface HydmDao extends BaseMapper<HydmEntity> {
    Page<Map<String, Object>> queryHydmPage(Page<Map<String, Object>> pageParams);

    List<Map<String, Object>> queryHydmReportDate(Map<String, Object> params);

    List<Map<String, Object>> queryHydmStatisticsByDate(String startDate, String endDate);
}
