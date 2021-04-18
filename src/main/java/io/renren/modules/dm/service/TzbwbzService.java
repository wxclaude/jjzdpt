package io.renren.modules.dm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.TzbwbzEntity;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020/3/14
 */
public interface TzbwbzService extends IService<TzbwbzEntity> {


    List<Map<String, Object>> queryTzbwbzSta(Map<String, Object> params);
}
