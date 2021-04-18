package io.renren.modules.df.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.xlgl.entity.TdkEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Mapper
public interface DfConfigDao extends BaseMapper<DfConfig> {

    @Insert("INSERT INTO df_config(dfx, qzb, type, create_by) " + "VALUES(#{dfx}, #{qzb}, #{type}, #{createBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertOne(DfConfig fConfig);


    List<Map<String, Object>> queryAllDept(@Param("params") Map<String, Object> params);

    Map<String, Object> getDeptByDeptId(String deptId);
}
