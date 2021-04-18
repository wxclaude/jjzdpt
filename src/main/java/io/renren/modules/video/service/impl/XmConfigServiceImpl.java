package io.renren.modules.video.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.MyUtils;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.video.consts.VideoConsts;
import io.renren.modules.video.dao.DwbzDao;
import io.renren.modules.video.dao.JyVideoDao;
import io.renren.modules.video.dao.VideoDao;
import io.renren.modules.video.dao.XmConfigDao;
import io.renren.modules.video.entity.DwbzEntity;
import io.renren.modules.video.entity.JyVideoEntity;
import io.renren.modules.video.entity.VideoEntity;
import io.renren.modules.video.entity.XmConfigEntity;
import io.renren.modules.video.service.VideoService;
import io.renren.modules.video.service.XmConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Service
public class XmConfigServiceImpl extends ServiceImpl<XmConfigDao, XmConfigEntity> implements XmConfigService {



    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryAllXm(Map<String, Object> params) {
        return baseMapper.queryAllXmConfig(params);

    }

    @Override
    public List<XmConfigEntity> queryAllXmConfig(Map<String, Object> params) {
        List<XmConfigEntity> list = baseMapper.selectList(new QueryWrapper<XmConfigEntity>().orderByAsc("sort"));
        return list;
    }

    @Override
    @DataSource("slave2")
    public List<String> getXmNameByIds(String ids) {
        return baseMapper.getXmNameByIds(ids);
    }

    @Override
    public String queryXmIdsByType(String type) {
        List<String> list = baseMapper.queryXmIdsByType(type);
        StringBuffer sb = new StringBuffer();
        String result = "";
        list.stream().forEach(e->{
            sb.append(e).append(",");
        });
        if (sb.toString().endsWith(",")) {
            result = StrUtil.subBefore(sb.toString(), ",", true);
        } else {
            result = sb.toString();
        }
        return result;
    }

}
