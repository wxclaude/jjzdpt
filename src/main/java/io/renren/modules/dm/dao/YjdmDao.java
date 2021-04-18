package io.renren.modules.dm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface YjdmDao  extends BaseMapper<YjdmEntity> {
    Page<Map<String, Object>> queryYjdmPage(Page<Map<String, Object>> pageParams);

    List<Map<String, Object>> queryYjdmReportDate(Map<String, Object> params);

    IPage<Map<String, Object>> queryYjdmStatisticsByDatePage(Page<Map<String, Object>> pageParams, String startDate, String endDate);
}
