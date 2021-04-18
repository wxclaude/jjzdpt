package io.renren.modules.xlgl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.xlgl.entity.TxlEntity;
import io.renren.modules.xlgl.entity.TxlInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
@Mapper
public interface TxlDao extends BaseMapper<TxlEntity> {

    IPage<TxlEntity> queryXlByPage(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);

    TxlInfoEntity queryDkAndSbInfoByXlbh(@Param("params") Map<String, Object> params);

    TxlInfoEntity getSlDkAndSbEntity(String xlbh);

    int deleteBySbId(int sbId);

    Map<String, Object> getSbById(String sbid);
}
