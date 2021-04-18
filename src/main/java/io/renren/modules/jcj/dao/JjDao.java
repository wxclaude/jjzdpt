package io.renren.modules.jcj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.jcj.entity.JcjJjxx;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface JjDao extends BaseMapper<JcjJjxx>{

    IPage<JcjJjxx> queryJjList(Page page,@Param("condition") String condition);

}
