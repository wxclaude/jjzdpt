package io.renren.modules.video.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.video.entity.DwbzEntity;
import io.renren.modules.video.entity.XmConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-03-31
 */

@Mapper
public interface XmConfigDao extends BaseMapper<XmConfigEntity>{


    List<Map<String, Object>> queryAllXmConfig(@Param("params") Map<String, Object> params);

    List<String> getXmNameByIds(@Param("ids") String ids);

    List<String> queryXmIdsByType(String type);
}
