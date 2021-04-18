package io.renren.modules.video.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.video.entity.JyVideoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-03-31
 */

@Mapper
public interface JyVideoDao  extends BaseMapper<JyVideoEntity> {

    List<Map<String, Object>> queryVideoDataByTypeAndDate(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryLastVideoDataByType(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryVideoStatisticsByTypeAndDate(@Param("params")Map<String, Object> params);

    List<String> queryLastVideoDateByType(@Param("params")Map<String, Object> params);

    String getLastZxDate(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryVideoDataByTypeAndDateNew(@Param("params")Map<String, Object> params);
}
