package io.renren.modules.df.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.df.entity.DfDetail;
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
public interface DfRecordService extends IService<DfRecord> {

    IPage<DfRecord> queryDfRecord(Map<String, Object> params);

    Map<String, Object> queryDfRecordDetailBeforeEdit(Map<String, Object> params);

    DfRecord queryDfDetailByRecordId(Map<String, Object> params);

    List<Map<String, Object>> queryDfTheadByRecordId(int recordId);

    List<Map<String, Object>> generateParentConfigList(List<Map<String, Object>> configList);

    DfRecord queryDfDetailByDfMonthAndParentConfigId(Map<String, Object> params);
}

