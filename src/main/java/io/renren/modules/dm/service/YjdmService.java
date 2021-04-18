package io.renren.modules.dm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.YjdmEntity;

import java.util.List;
import java.util.Map;

public interface YjdmService extends IService<YjdmEntity> {
    void addYjdmBatch(List<YjdmEntity> yjdmEntityList);

    Page queryYjdmPage(Map<String, Object> params);

    List<Map<String, Object>> queryYjdmReportDate(Map<String, Object> params);

    IPage queryYjdmStatisticsByDatePage(Map<String, Object> params);
}
