package io.renren.modules.jcj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.jcj.entity.YdjcjEntity;
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
public interface YdjcjDao extends BaseMapper<YdjcjEntity>{


    List<YdjcjEntity> queryByDate(@Param("params") Map<String, Object> params);

    YdjcjEntity queryTotalByDate(@Param("params")Map<String, Object> params);

    List<String> queryUploadDateList(@Param("params")Map<String, Object> params);
}
