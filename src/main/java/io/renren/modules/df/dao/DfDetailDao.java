package io.renren.modules.df.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.entity.DfDetail;
import io.renren.modules.df.entity.DfDetailForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Mapper
public interface DfDetailDao extends BaseMapper<DfDetail> {

    List<Map<String, Object>> queryDfTheadByRecordId(int recordId);

    int updateDfDetailForm(DfDetailForm dfDetailForm);

    List<Map<String, Object>> queryDfDetailByDeptIdAndDate(@Param("params") Map<String, Object> params);
}
