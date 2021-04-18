package io.renren.modules.df.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.entity.DfRule;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Mapper
public interface DfRuleDao extends BaseMapper<DfRule> {

}
