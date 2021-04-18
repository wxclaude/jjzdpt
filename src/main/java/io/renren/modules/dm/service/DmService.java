package io.renren.modules.dm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.SqmjEntity;
import io.renren.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020/3/14
 */
public interface DmService extends IService<DianMingEntity> {

    List<Map<String,Object>> queryZhiBanDataByDate(String date, String deptId);

    void addDianMingBatch(List<DianMingEntity> dianMingEntityList);

    List<Map<String, Object>> getLastTwoDayDianMingData(String policeCode);

    void updateDianMingBatch(List<DianMingEntity> dianMingEntityList);

    List<DianMingEntity> queryDmDataByCondition(Map<String, Object> params);

    List<Map<String, Object>> queryZhiBanAllByDate(String date);

    Page<Map<String,Object>> queryDMPage(Map<String, Object> params);

    IPage queryDMByDatePage(Map<String, Object> params);

    Map<String, Object> queryZhiBanLeaderByDate(String date);

    Map<String,Object> queryUserSqlServer(String policeCode);

    List<Map<String,Object>> queryQsmjByDeptId(String deptId);

    List<Map<String, Object>> handerRepeatPolice(List<Map<String, Object>> list);

    List<Map<String, Object>> queryDMStatisticsDetailByDatePage(Map<String, Object> params);

    List<Map<String, Object>> queryXtjZhiBanDataByReportPointIdAndDate(Map<String, Object> paramsMap);

    List<Map<String,Object>> queryZhiBanZhangByDate(String date);

    String getDmTimeByTbrq(String tbrqParam);

//    List<ZhiBanFormat> queryZhiBanFormatDataByDate(String date);
}
