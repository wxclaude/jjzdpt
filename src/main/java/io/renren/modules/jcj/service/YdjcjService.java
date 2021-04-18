package io.renren.modules.jcj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.jcj.entity.YdjcjEntity;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020/3/14
 */
public interface YdjcjService extends IService<YdjcjEntity> {


    List<YdjcjEntity> queryByDate(Map<String, Object> params);

    void uploadYdjcj(List<YdjcjEntity> list, Long userId, String ydjcjDateStr);

    void uploadYdjcjZd(List<YdjcjEntity> list, Long userId, String ydjcjDateStr);

    List<String> queryUploadDateList(Map<String, Object> params);
}
