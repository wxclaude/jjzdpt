package io.renren.modules.video.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.video.entity.DwbzEntity;
import io.renren.modules.video.entity.VideoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-03-31
 */

@Mapper
public interface VideoDao  extends BaseMapper<VideoEntity>{

    List<Map<String, Object>> queryVideoDataByTypeAndDate(@Param("params") Map<String, Object> params);

    IPage<Map<String, Object>> queryVideoDataDetailPage(Page<Map<String, Object>> pageParams, @Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryVideoStatisticsByTypeAndDate(@Param("params") Map<String, Object> params);

    int queryVideoXJCSByDateAndDept(@Param("params")Map<String, Object> params);

    List<String> queryLastVideoDateByType(@Param("params")Map<String, Object> params);

    String getZjsxBySn(@Param("params")Map<String, Object> params);

    void updateVideoBz(@Param("params")Map<String, Object> params);

    IPage<DwbzEntity> queryVideoBzDataPage(Page<Map<String, Object>> pageParams, @Param("params")Map<String, Object> params);

    String getPoliceNameByPoliceCode(String userId);

    List<Map<String, Object>> queryVideoDataDetailByDate(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryCompareDataBySnPage(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryCompareDataByNamePage(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryVideoDataDetailByDateNew(@Param("params")Map<String, Object> params);

    IPage<Map<String, Object>> queryNormalVideoDataDetailByConfigIdPage(Page<Map<String, Object>> pageParams, @Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryErrorVideoDataDetailByConfigId(@Param("params")Map<String, Object> params);

    String getZjsxByDwid(@Param("params")Map<String, Object> params);

    String getScjcsjByDwid(@Param("params")Map<String, Object> params);

    int getDwByXmids(String xmIds);

    IPage<Map<String, Object>> queryHtVideoDataDetailByConfigIdPage(Page<Map<String, Object>> pageParams, @Param("params")Map<String, Object> params);

    Integer getAnyOneErrorXjData(@Param("params")Map<String, Object> params);

    IPage<Map<String, Object>> queryHistoryDataPage(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryDwDetailByDwidAndDate(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryTwLxData(@Param("params")Map<String, Object> params);

    IPage<Map<String, Object>> queryVideoBzDataByXMIDPage(Page<Map<String, Object>> pageParams, @Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryRLVideoDataByDate(@Param("params")Map<String, Object> params);

    List<String> queryLastRLVideoDate(@Param("params")Map<String, Object> params);

    IPage<Map<String, Object>> queryRLVideoDataDetailPage(Page<Map<String, Object>> pageParams, @Param("params")Map<String, Object> params);

    String getScjcsjBySn(@Param("params")Map<String, Object> params);

    String getZjjcsjBySn(@Param("params")Map<String, Object> params);

    String getZjsxAndJkzsByDwid(@Param("params")Map<String, Object> params);

    IPage<DwbzEntity> queryBxDataPage(Page<DwbzEntity> pageParams, @Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryGZDWData(@Param("params")Map<String, Object> params);

    String queryGZDWLastUpData(@Param("params")Map<String, Object> params);

    List<Map<String, Object>> queryGZTDData(@Param("params")Map<String, Object> params);

    String queryGZTDastUpData(@Param("params")Map<String, Object> params);

    String getNameBySn(String sn);

    List<Map<String, Object>> queryVideoStatisticsByTypeAndDateOld(@Param("params")Map<String, Object> params);

    int queryVideoXJCSByDateAndDeptOld(@Param("params")Map<String, Object> params);

    IPage<Map<String, Object>> queryDwOrTd(Page<Map<String, Object>> pageParams, @Param("params")Map<String, Object> params);

    String getLastXjsj();

    List<Map<String, Object>> queryAllCompareXm(Map<String, Object> params);

    List<Map<String, Object>> queryDwVideoDataDetailByConfigId(@Param("params")Map<String, Object> params);
}
