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
public interface VideoService extends IService<JyVideoEntity> {

    List<Map<String, Object>> queryVideoDataByTypeAndDate(String date, String type, List<XmConfigEntity> xmIds);

    IPage<Map<String,Object>> queryVideoDataDetailPage(Map<String, Object> params);

    List<Map<String, Object>> queryLastVideoDataByType(Map<String, Object> params);

    void addVideoForm(List<JyVideoEntity> list);

    List<Map<String, Object>> queryVideoStatisticsByTypeAndDate(Map<String, Object> params);

    List<String> queryLastVideoDateByType(Map<String, Object> params);

    void updateVideoBz(Map<String, Object> params);

    @DataSource("slave2")
    VideoEntity getById(Map<String, Object> params);

    IPage<DwbzEntity> queryVideoBzDataPage(Map<String, Object> params);

    String getPoliceNameByPoliceCode(String userId);

    DwbzEntity getLastBzByBh(String sn);

    List<Map<String, Object>> queryCompareDataPage(Map<String, Object> params);

    List<Map<String,Object>> queryErrorVideoDataDetailByConfigId(Map<String, Object> params);

    IPage<Map<String, Object>> queryNormalVideoDataDetailByConfigId(Map<String, Object> params);

    IPage<Map<String, Object>> queryHtVideoDataDetailByConfigId(Map<String, Object> params);

    DwbzEntity getBzByXjid(Integer id);

    IPage<Map<String, Object>> queryHistoryDataPage(Map<String, Object> params);

    List<Map<String, Object>> queryDwDetailByDwidAndDate(Map<String, Object> params);

    List<Map<String, Object>> queryTwLxData(Map<String, Object> params);

    IPage<Map<String, Object>> queryVideoBzDataByXMIDPage(Map<String, Object> params);

    IPage<DwbzEntity> queryBxDataPage(Map<String, Object> params);

    List<Map<String, Object>> queryGZDWData(Map<String, Object> params);

    String queryGZDWLastUpData(Map<String, Object> params);

    List<Map<String, Object>> queryGZTDData(Map<String, Object> params);

    String queryGZTDastUpData(Map<String, Object> params);

    String getNameBySn(String sn);

    IPage<Map<String, Object>> queryDwOrTd(Map<String, Object> params);

    List<Map<String, Object>> queryAllCompareXm(Map<String, Object> params);

    List<Map<String, Object>> queryDwVideoDataDetailByConfigId(Map<String, Object> params);
}
