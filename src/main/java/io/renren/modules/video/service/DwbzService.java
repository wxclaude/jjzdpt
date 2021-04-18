package io.renren.modules.video.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public interface DwbzService extends IService<DwbzEntity> {

    void updateBatchByIdAndBefore(List<DwbzEntity> list);

}
