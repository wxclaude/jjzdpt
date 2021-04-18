package io.renren.modules.dm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.dm.entity.YjdmEntity;

import java.util.List;
import java.util.Map;

public interface HydmService extends IService<HydmEntity> {

    void addHydmBatch(List<HydmEntity> hydmEntityList);

    Page queryHydmPage(Map<String, Object> params);

    List<Map<String, Object>> queryHydmReportDate(Map<String, Object> params);

}
