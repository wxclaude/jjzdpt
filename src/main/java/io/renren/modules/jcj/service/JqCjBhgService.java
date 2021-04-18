package io.renren.modules.jcj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.jcj.dao.JqCjBhgDao;
import io.renren.modules.jcj.entity.JqCjBhgEntity;
import io.renren.modules.jcj.entity.JqCjViewEntity;

import java.util.List;
import java.util.Map;

public interface JqCjBhgService extends IService<JqCjBhgEntity> {

    List<Map<String, Object>> queryCjStatic(Map<String, Object> params,boolean isSorted);

    List<Map<String, Object>> queryCj(Map<String, Object> params);

}
