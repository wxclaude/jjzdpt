package io.renren.modules.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.video.entity.DwbzEntity;
import io.renren.modules.video.entity.JyVideoEntity;
import io.renren.modules.video.entity.VideoEntity;
import io.renren.modules.video.entity.XmConfigEntity;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020/3/14
 */
public interface XmConfigService extends IService<XmConfigEntity> {

    List<Map<String, Object>> queryAllXm(Map<String, Object> params);

    List<XmConfigEntity> queryAllXmConfig(Map<String, Object> params);

    List<String> getXmNameByIds(String ids);

    String queryXmIdsByType(String type);
}
