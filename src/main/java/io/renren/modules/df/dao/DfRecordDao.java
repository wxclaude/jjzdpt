package io.renren.modules.df.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.entity.DfDetail;
import io.renren.modules.df.entity.DfRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@Mapper
public interface DfRecordDao extends BaseMapper<DfRecord> {

    @Insert("INSERT INTO df_record(df_month, title, create_by, rule_id) " + "VALUES(#{dfMonth}, #{title}, #{createBy} ,#{ruleId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertOne(DfRecord dfRecord);
}
