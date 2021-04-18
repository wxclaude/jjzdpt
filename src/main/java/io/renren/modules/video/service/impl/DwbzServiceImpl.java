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
import io.renren.modules.video.service.DwbzService;
import io.renren.modules.video.service.VideoService;
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
public class DwbzServiceImpl extends ServiceImpl<DwbzDao, DwbzEntity> implements DwbzService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatchByIdAndBefore(List<DwbzEntity> list) {

        for (DwbzEntity dwbzEntity : list) {

            //1跟新当前报修记录
            if (baseMapper.updateById(dwbzEntity) <= 0) {
                throw new RRException("更新报修记录失败，请联系管理员");
            }

            DwbzEntity entity = baseMapper.selectById(dwbzEntity.getId());

            //2跟该点位/通道之前未修复的报修记录

            List<DwbzEntity> beforeList = baseMapper.selectList(
                new QueryWrapper<DwbzEntity>().eq("bh", entity.getBh()).eq("state", 0).le("create_time", entity.getCreateTime())
            );

            beforeList.forEach(e->{
                e.setState(1);
                e.setRepairTime(entity.getRepairTime());
                e.setIp2(entity.getIp2());
                if (baseMapper.updateById(e) <= 0) {
                    throw new RRException("更新报修记录失败，请联系管理员");
                }
            });
        }

    }
}
