package io.renren.modules.xscz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.xscz.entity.ZhXsczEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface ZhXsczService extends IService<ZhXsczEntity> {

    IPage<ZhXsczEntity> queryZhXsczPage(Map<String, Object> params);

    void saveWithFj(ZhXsczEntity zhXsczEntity);

    void updateWithFj(ZhXsczEntity zhXsczEntity);

    List<Map<String, Object>> queryZhXsczStat(Map<String, Object> params);

    List<Map<String, Object>> queryZhXsczStatDetail(Map<String, Object> params);

    void modifyWithFj(ZhXsczEntity zhXsczEntity);
}

