package io.renren.modules.jcj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.jcj.entity.JcjJjxx;

import java.util.Map;

public interface JjService extends IService<JcjJjxx> {

    IPage queryJjList(Map<String, Object> params);

    JcjJjxx queryByJjbh(String jjbh);
}
