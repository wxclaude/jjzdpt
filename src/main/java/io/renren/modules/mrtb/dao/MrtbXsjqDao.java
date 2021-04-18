package io.renren.modules.mrtb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.mrtb.entity.ZhMrtbXsjq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MrtbXsjqDao extends BaseMapper<ZhMrtbXsjq> {

}
