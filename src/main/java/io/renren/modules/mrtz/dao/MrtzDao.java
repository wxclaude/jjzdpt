package io.renren.modules.mrtz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MrtzDao extends BaseMapper<ZhMrtzEntity> {

    Page<ZhMrtzEntity> queryMrtz(Page<ZhMrtzEntity> pageParams, @Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryMrtzData(Map<String, Object> params);

    List<Map<String, Object>> queryMrtzStat(@Param("params") Map<String, Object> params);
}
