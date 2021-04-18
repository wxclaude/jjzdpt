package io.renren.modules.xlgl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.xlgl.entity.TsbEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
@Mapper
public interface TsbDao extends BaseMapper<TsbEntity> {

    IPage<TsbEntity> querySbByPage(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);

    IPage<Map<String,Object>> querySbByPageNew(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);

    List<String> querySbIdAll( @Param("params")Map<String, Object> params);

    IPage<Map<String, Object>> querySbByPageST(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);

    List<Map<String, Object>> querySbSearchCondition(int type);

    List<Map<String, Object>> querySbDkDetailBySbId(@Param("params")Map<String, Object> params);

    Map<String, Object> getSbById(String sbId);

    Map<String, Object> querySbCount(@Param("params")Map<String, Object> params);
}
