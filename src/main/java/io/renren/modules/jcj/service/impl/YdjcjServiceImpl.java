package io.renren.modules.jcj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.modules.dm.dao.DeptDao;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.jcj.dao.YdjcjDao;
import io.renren.modules.jcj.entity.YdjcjEntity;
import io.renren.modules.jcj.service.YdjcjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Service()
public class YdjcjServiceImpl extends ServiceImpl<YdjcjDao, YdjcjEntity> implements YdjcjService {

    @Autowired
    private YdjcjDao ydjcjDao;

    @Autowired
    private DeptDao deptDao;

    @Override
    public List<YdjcjEntity> queryByDate(Map<String, Object> params) {
        List<YdjcjEntity> ydjcjList = ydjcjDao.queryByDate(params);

        if (CollectionUtil.isNotEmpty(ydjcjList)) {
            //计算邗江分局总数
            YdjcjEntity ydjcjEntity = ydjcjDao.queryTotalByDate(params);

            ydjcjList.add(ydjcjEntity);
        }


        for (int i = 0; i < ydjcjList.size(); i++) {
            ydjcjList.get(i).setXh(i + 1);
        }

        return ydjcjList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadYdjcj(List<YdjcjEntity> list, Long userId, String ydjcjDateStr) {

        Date ydjcjDate = DateUtil.parse(ydjcjDateStr, DatePattern.NORM_DATE_FORMAT);

        List<DeptEntity> ydjcjDeptList = deptDao.selectList(new QueryWrapper<DeptEntity>().gt("ydjcj", 0));

        List<YdjcjEntity> exsitedList = null;
        String xptjjlvExcel = null;
        String xptcjlvExcel = null;
        String yddcjlvExcel = null;

        //判断当天有无上传,有的话先删除再删
        baseMapper.delete(new QueryWrapper<YdjcjEntity>()
                .eq("date_format( ydjcj_date, '%Y-%m-%d' )", ydjcjDateStr)
                .like("dw","派出所"));

        for (YdjcjEntity ydjcjEntity : list) {

            xptjjlvExcel = null;
            xptcjlvExcel = null;
            yddcjlvExcel = null;


            //检查该单位当日有无上传记录，有的话不让上传
            //exsitedList = ydjcjDao.selectList(new QueryWrapper<YdjcjEntity>().eq("dw", ydjcjEntity.getDw()).eq("ydjcj_date", ydjcjDate));
            //if (CollectionUtil.isNotEmpty(exsitedList)) {
            //    throw new RRException("上传失败，" + ydjcjEntity.getDw() + "当天已经上传过移动接出警数据");
            //}

            if (ydjcjEntity.getDw().equals("扬州市公安局邗江分局")) {
                continue;
            }
            String dw = ydjcjEntity.getDw();
            if (dw.contains("派出所")) {
                ydjcjEntity.setUserId(String.valueOf(userId));
                ydjcjEntity.setYdjcjDate(ydjcjDate);

                //处理两个%
                xptjjlvExcel = StrUtil.replace(ydjcjEntity.getXptjjlvExcel(), "%", "");
                xptcjlvExcel = StrUtil.replace(ydjcjEntity.getXptcjlvExcel(), "%", "");
                yddcjlvExcel = StrUtil.replace(ydjcjEntity.getYddcjlvExcel(), "%", "");
                ydjcjEntity.setXptjjlv(Integer.parseInt(xptjjlvExcel));
                ydjcjEntity.setXptcjlv(Integer.parseInt(xptcjlvExcel));
                ydjcjEntity.setYddcjlv(Integer.parseInt(yddcjlvExcel));

                ydjcjDeptList.stream().forEach(e->{
                    if (e.getFullDeptName().equals(ydjcjEntity.getDw())) {
                        ydjcjEntity.setDeptId(e.getDeptId());
                    }
                });

                ydjcjEntity.setUserId(String.valueOf(userId));
                ydjcjEntity.setYdjcjDate(ydjcjDate);

                ydjcjDao.insert(ydjcjEntity);
            }

        }




    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadYdjcjZd(List<YdjcjEntity> list, Long userId, String ydjcjDateStr) {
        Date ydjcjDate = DateUtil.parse(ydjcjDateStr, DatePattern.NORM_DATE_FORMAT);

        List<DeptEntity> ydjcjDeptList = deptDao.selectList(new QueryWrapper<DeptEntity>().gt("ydjcj", 0));

        List<YdjcjEntity> exsitedList = null;
        String xptjjlvExcel = null;
        String xptcjlvExcel = null;
        String yddcjlvExcel = null;

        //判断当天有无上传,有的话先删除再删
        baseMapper.delete(new QueryWrapper<YdjcjEntity>()
                .eq("date_format( ydjcj_date, '%Y-%m-%d' )", ydjcjDateStr)
                .like("dw","中队"));

        for (YdjcjEntity ydjcjEntity : list) {

            xptjjlvExcel = null;
            xptcjlvExcel = null;
            yddcjlvExcel = null;

            if (ydjcjEntity.getDw().equals("扬州市公安局邗江分局")) {
                continue;
            }

            String dw = ydjcjEntity.getDw();
            if (dw.contains("汊河中队") || dw.contains("瓜洲中队") || dw.contains("公道中队") || dw.contains("方巷中队") || dw.contains("甘泉中队")) {
                ydjcjEntity.setUserId(String.valueOf(userId));
                ydjcjEntity.setYdjcjDate(ydjcjDate);

                //处理两个%
                xptjjlvExcel = StrUtil.replace(ydjcjEntity.getXptjjlvExcel(), "%", "");
                xptcjlvExcel = StrUtil.replace(ydjcjEntity.getXptcjlvExcel(), "%", "");
                yddcjlvExcel = StrUtil.replace(ydjcjEntity.getYddcjlvExcel(), "%", "");
                ydjcjEntity.setXptjjlv(Integer.parseInt(xptjjlvExcel));
                ydjcjEntity.setXptcjlv(Integer.parseInt(xptcjlvExcel));
                ydjcjEntity.setYddcjlv(Integer.parseInt(yddcjlvExcel));

                ydjcjDeptList.stream().forEach(e->{
                    if (e.getFullDeptName().equals(ydjcjEntity.getDw()) || ydjcjEntity.getDw().contains(e.getShortDeptName())) {
                        ydjcjEntity.setDeptId(e.getDeptId());
                    }
                });

                ydjcjEntity.setUserId(String.valueOf(userId));
                ydjcjEntity.setYdjcjDate(ydjcjDate);

                ydjcjDao.insert(ydjcjEntity);
            }

        }

    }

    @Override
    public List<String> queryUploadDateList(Map<String, Object> params) {
        return ydjcjDao.queryUploadDateList(params);
    }

}
