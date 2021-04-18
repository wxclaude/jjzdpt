package io.renren.modules.df.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.df.entity.DfQbxx;
import io.renren.modules.df.entity.DfSjfn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Mapper
public interface DfSjfnDao extends BaseMapper<DfSjfn> {

    List<Map<String, Object>> queryDfSjfnData(@Param("params")Map<String, Object> params);
}
