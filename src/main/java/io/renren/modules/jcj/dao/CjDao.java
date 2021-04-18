package io.renren.modules.jcj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.jcj.entity.JcjCjxx;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface CjDao extends BaseMapper<JcjCjxx> {

    IPage queryCjList(Page page, @Param("params")Map<String, Object> params);

    List<JcjCjxx> queryCjxx(Map<String, Object> params);

    List<JcjCjxx> queryCjListIn3days(Map<String, Object> params);

    List<Map<String, Object>> countOf8HourNoPoliceDeal(Map<String, Object> params);

    List<Map<String, Object>> countOf24HourNoApproval(Map<String, Object> params);

    List<Map<String, Object>> countOfUpOrLowLimitIsEmpty(Map<String, Object> params);

    List<Map<String, Object>> countOfWeatherConditionIsEmptyWhileTraffic(Map<String, Object> params);

    List<Map<String, Object>> countOfAddressIsEmpty(Map<String, Object> params);

    List<Map<String, Object>> countOfMonthlyJj(Map<String, Object> params);

    List<Map<String, Object>> countOfDailyJj(Map<String, Object> params);

    List<Map<String, Object>> countOfMonthlyCj(Map<String, Object> params);

    List<Map<String, Object>> countOfDailyCj(Map<String, Object> params);

    List<Map<String, Object>> countOfEverydayJj(String jjrqsj);

    List<Map<String, Object>> countOfMonthlyCjlbAnalysis();

    List<JcjCjxx> queryYesterdayXSJQ(Map<String, Object> params);

    List<JcjCjxx> queryYesterdaySHSE(Map<String, Object> params);

    int countOfLdcj8Djsj(Map<String, Object> params);
}
