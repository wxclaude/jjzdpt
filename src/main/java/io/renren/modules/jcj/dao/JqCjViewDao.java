package io.renren.modules.jcj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.jcj.entity.JqCjViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface JqCjViewDao extends BaseMapper<JqCjViewEntity> {

}
