package io.renren.modules.report.service;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-05-06
 */
public interface ReportService {
    Map<String, Object> queryTwData(String date);

    Map<String, Object> queryZjData(String date);

    Map<String, Object> queryYdjcjData(String date);

    Map<String, Object> queryDfData(Map<String, Object> params);

    Map<String, Object> queryDfDataNew(Map<String, Object> params);

    List<Map<String,Object>> queryYdjcjDataNew(String date);

    Map<String, Object> queryQbxxData(Map<String, Object> params);

    Map<String, Object> queryMrtzData(Map<String, Object> params);

    Map<String, Object> queryDfDataNull(Map<String, Object> params);

    Map<String, Object> queryQbxxDataNull(Map<String, Object> params);

}
