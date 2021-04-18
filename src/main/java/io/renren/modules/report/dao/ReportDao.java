package io.renren.modules.report.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-05-06
 */
@Mapper
public interface ReportDao {

    List<Map<String, Object>> queryTwData(@Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryZjData(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryYdjcjData(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryDfData(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryQbxxData(@Param("params")Map<String, Object> params);

    int queryQbxxDataCount(String dfMonth);
}
