package io.renren.modules.xscz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.xscz.entity.ZhXsczEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZhXsczDao extends BaseMapper<ZhXsczEntity> {

    Page<ZhXsczEntity> queryZhXsczPage(Page<ZhXsczEntity> pageParams, @Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryZhXsczStat( @Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryZhXsczStatDetail(@Param("params")Map<String, Object> params);
}
