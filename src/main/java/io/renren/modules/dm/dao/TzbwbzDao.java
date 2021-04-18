package io.renren.modules.dm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.TzbwbzEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TzbwbzDao extends BaseMapper<TzbwbzEntity> {

    List<Map<String, Object>> queryTzbwbzSta(@Param("params") Map<String, Object> params);
}
