package io.renren.modules.xlgl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.xlgl.entity.TxlEntity;
import io.renren.modules.xlgl.entity.TxlInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
public interface TxlService extends IService<TxlEntity> {

    IPage<TxlEntity> queryXlByPage(Map<String, Object> params, List<String> sbIdList);

    void deleteXlBatch(List<TxlEntity> txlEntityList);

    List<TxlInfoEntity> queryXlInfo(Map<String, Object> params);

    Map<String, Object> getSbById(String sbid);

    List<TxlEntity> getListLikeXlbh(String xlbh);

    List<TxlEntity> getListByXlbh(String xlbh);

    List<TxlEntity> listBySlDkIdOrXlDkId(Integer dkId);

    void saveCC(TxlEntity txlEntity);

    void updateByIdCC(TxlEntity txlEntity);

}

