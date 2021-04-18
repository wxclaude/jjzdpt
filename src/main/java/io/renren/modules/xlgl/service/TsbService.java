package io.renren.modules.xlgl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
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
public interface TsbService extends IService<TsbEntity> {

    IPage<TsbEntity> querySbByPage(Map<String, Object> params);

    void deleteSbBatch(List<TsbEntity> tsbEntityList);

    IPage<Map<String,Object>> querySbByPageNew(Map<String, Object> params);

    List<String> querySbIdAll(Map<String, Object> params);

    IPage<Map<String, Object>> querySbByPageST(Map<String, Object> params);

    List<Map<String, Object>> querySbSearchCondition(int type);

    Map<String, Object> querySbCount(Map<String, Object> params);
}

