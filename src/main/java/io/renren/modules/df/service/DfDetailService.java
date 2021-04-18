package io.renren.modules.df.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.df.entity.DfDetail;
import io.renren.modules.df.entity.DfDetailForm;
import io.renren.modules.df.entity.DfRecord;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
public interface DfDetailService extends IService<DfDetail> {

    int generateDfRecordAndDetail(DfRecord dfRecord, String name);

    void saveDfDetail(List<DfDetailForm> dfDetailFormList, Map<String, Object> params);

    void deleteDfRecordAndDetail(int recordId);

    List<Map<String, Object>> queryDfDetailByDeptIdAndDate(String dfMonth, String deptId);

    List<Map<String, Object>> queryDfDetailByDeptIdAndDateNew(String dfMonth, String deptId, String type);
}

