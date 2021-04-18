package io.renren.modules.xlgl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.xlgl.entity.TdkEntity;
import io.renren.modules.xlgl.entity.TsbEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
public interface TdkService extends IService<TdkEntity> {

    IPage<TdkEntity> queryDkByPage(Map<String, Object> params, List<String> sbIdList);

    void deleteDkBatch(List<TdkEntity> tdkEntityList);

    IPage<TdkEntity> queryDkDetailBySbIdPage(Map<String, Object> params);

    void saveBatchCC(List<TdkEntity> tdkEntityList);

    List<TdkEntity> queryDkBySbId(String sbId);

    List<Map<String, Object>> querySbDkDetailBySbId(Map<String, Object> params);

    List<Map<String, Object>> queryDkBkhBySbId(Map<String, Object> params);
}

