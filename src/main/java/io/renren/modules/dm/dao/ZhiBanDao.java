package io.renren.modules.dm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.ZhiBanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Mapper
public interface ZhiBanDao extends BaseMapper<DianMingEntity> {

    List<Map<String,Object>> queryZhiBanDataByDate(Map params);

    List<Map<String, Object>> getLastTwoDayDianMingData(String policeCode);

    void updateDianMingBatch(List<DianMingEntity> list);

    List<DianMingEntity> queryDmDataByCondition(Map<String, Object> params);

    List<Map<String, Object>> queryZhiBanAllByDate(Map params);

    Page<Map<String,Object>> queryDMPage(Page page);

    IPage<Map<String, Object>> queryDMByDatePage(Page page, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> queryZhiBanLeaderByDate(Map<String, Object> params);

    List<Map<String,Object>> queryDMStatisticsDetailByDatePage(Map<String, Object> params);

    List<Map<String, Object>> queryPickXtj3ZhiBanDataByDate(@Param("params")Map<String, Object> params);

    List<Map<String,Object>> queryZhiBanZhangByDate(String date);

    String getDmTimeByTbrq(String tbrqParam);

}
