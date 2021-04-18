package io.renren.modules.xlgl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.xlgl.entity.TdkEntity;
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
public interface TdkDao extends BaseMapper<TdkEntity> {

    IPage<TdkEntity> queryDkByPage(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);


    List<Map<String,Object>> queryBksBySbId(String sbId);

    IPage<TdkEntity> queryDkDetailBySbIdPage(Page<TdkEntity> pageParams, @Param("params")Map<String, Object> params);

    List<Map<String, Object>> querySbSearchCondition(@Param("params")Map<String, Object> params);
}
