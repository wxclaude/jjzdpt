package io.renren.modules.mrtb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.mrtb.entity.TXx;
import io.renren.modules.mrtb.entity.ZhMrtb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface TXxDao extends BaseMapper<TXx> {

    public boolean saveCC(TXx tXx);

}
