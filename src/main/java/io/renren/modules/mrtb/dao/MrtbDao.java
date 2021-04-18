package io.renren.modules.mrtb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.xlgl.entity.TdkEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MrtbDao extends BaseMapper<ZhMrtb> {

    Map<String, Object> getYesterday(@Param("params") Map<String, Object> params);

    //@Insert("INSERT INTO zh_mrtb(tbrq, create_by, totalnum, Num01, Num02, Num03, Num04, Num05, Num06, Num08, Num11, Num99) "
    //        + "VALUES(#{tbrq}, #{createBy})")
    //@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    boolean saveCC(ZhMrtb zhMrtb);
}
