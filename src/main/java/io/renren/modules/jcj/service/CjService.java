package io.renren.modules.jcj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.jcj.entity.JqCjBhgEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CjService extends IService<JcjCjxx> {

    IPage<JcjCjxx> queryCjList(Map<String, Object> params);

    List<JcjCjxx> queryCjxx(Map<String, Object> params);

    List<JcjCjxx> queryCjListIn3days(Map<String, Object> params);

    List<Map<String, Object>> countOf8HourNoPoliceDeal(Map<String, Object> params);

    List<Map<String, Object>> countOf24HourNoApproval(Map<String, Object> params);

    List<Map<String, Object>> countOfUpOrLowLimitIsEmpty(Map<String, Object> params);

    List<Map<String, Object>> countOfWeatherConditionIsEmptyWhileTraffic(Map<String, Object> params);

    List<Map<String, Object>> countOfAddressIsEmpty(Map<String, Object> params);

    JcjCjxx transformCjxx (JcjCjxx jcjCjxx);

    JcjCjxx transformCjxxNew (JcjCjxx jcjCjxx);

    List<Map<String, Object>> countOfMonthlyJj(Map<String, Object> params);

    List<Map<String, Object>> countOfDailyJj(Map<String, Object> params);

    List<Map<String, Object>> countOfMonthlyCj(Map<String, Object> params);

    List<Map<String, Object>> countOfDailyCj(Map<String, Object> params);

    List<Map<String, Object>> countOfEverydayJj(String jjrqsj);

    List<Map<String, Object>> countOfMonthlyCjlbAnalysis();

    List<JcjCjxx> queryYesterdayXSJQ(Map<String, Object> params);

    List<JcjCjxx> queryYesterdaySHSE(Map<String, Object> params);

    List<JcjCjxx> queryCjSd(Map<String, Object> params);

    int countOfLdcj8Djsj(Map<String, Object> params);
}
