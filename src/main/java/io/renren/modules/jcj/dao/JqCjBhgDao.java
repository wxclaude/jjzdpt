package io.renren.modules.jcj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.jcj.entity.JqCjBhgEntity;
import io.renren.modules.jcj.entity.JqCjViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface JqCjBhgDao extends BaseMapper<JqCjBhgEntity> {

    List<Map<String, Object>> queryCjStatic(@Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryCj(@Param("params")Map<String, Object> params);
}
