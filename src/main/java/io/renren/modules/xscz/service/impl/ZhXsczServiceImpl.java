package io.renren.modules.xscz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.modules.dm.dao.DeptDao;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.mrtz.dao.MrtzDao;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.mrtz.service.MrtzService;
import io.renren.modules.sys.consts.SysModelConsts;
import io.renren.modules.xscz.dao.ZhXsczDao;
import io.renren.modules.xscz.dao.ZhXsczFjDao;
import io.renren.modules.xscz.entity.ZhXsczEntity;
import io.renren.modules.xscz.entity.ZhXsczFjEntity;
import io.renren.modules.xscz.service.ZhXsczService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ZhXsczServiceImpl extends ServiceImpl<ZhXsczDao, ZhXsczEntity> implements ZhXsczService {


    @Autowired
    private ZhXsczFjDao zhXsczFjDao;
    @Autowired
    private DeptDao deptDao;

    @Override
    public IPage<ZhXsczEntity> queryZhXsczPage(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<ZhXsczEntity> pageParams = new Page(page,limit);
        Page<ZhXsczEntity> pageResult = baseMapper.queryZhXsczPage(pageParams,params);

        long nowTamp = System.currentTimeMillis();
        pageResult.getRecords().forEach(e->{

            //下发时的附件
            e.setZhXsczFjList(zhXsczFjDao.selectList(
                    new QueryWrapper<ZhXsczFjEntity>()
                            .eq("type", 0)
                            .eq("xsczid", e.getId())
            ));

            //反馈附件
            e.setFkFjList(zhXsczFjDao.selectList(
                    new QueryWrapper<ZhXsczFjEntity>()
                            .eq("type", 1)
                            .eq("xsczid", e.getId())
            ));

            Date fksj = e.getFksj();

            //设置是否超时
            if (fksj != null) {
                if (fksj.getTime() >= e.getCzsj().getTime()) {
                    e.setSfcs(1);
                }
            } else {
                if(nowTamp >= e.getCzsj().getTime()){
                    e.setSfcs(1);
                }
            }

        });

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFj(ZhXsczEntity zhXsczEntity) {
        //save zhXsczEntity

        DeptEntity deptEntity = deptDao.selectOne(new QueryWrapper<DeptEntity>().eq("dept_id", zhXsczEntity.getDeptId()).last("limit 1"));
        zhXsczEntity.setCzsd(deptEntity.getDeptName());
        if (baseMapper.insert(zhXsczEntity) <= 0) {
            throw new RRException("下发处置异常，请联系管理员");
        }
        String typeName = zhXsczEntity.getType() == 0 ? "污染防治_" : "城管_";

        String fileName = typeName + DateUtil.formatDate(zhXsczEntity.getXfsj()) + "_" + zhXsczEntity.getCzsd() + "（" + zhXsczEntity.getSjhdd() + "）";

        //update zhXsczFjEntity
        if (CollectionUtil.isNotEmpty(zhXsczEntity.getZhXsczFjList())) {
            zhXsczEntity.getZhXsczFjList().forEach(e -> {

                ZhXsczFjEntity zhXsczFjEntity = zhXsczFjDao.selectById(e.getId());
                zhXsczFjEntity.setXsczid(zhXsczEntity.getId());

                File rename = FileUtil.rename(new File(zhXsczFjEntity.getPath() + File.separator + zhXsczFjEntity.getId() + File.separator + zhXsczFjEntity.getFilename()), fileName, true, false);

                zhXsczFjEntity.setFilename(rename.getName());

                zhXsczFjDao.updateById(zhXsczFjEntity);

            });
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFj(ZhXsczEntity zhXsczEntity) {

        if (baseMapper.updateById(zhXsczEntity) <= 0) {
            throw new RRException("反馈处置异常，请联系管理员");
        }

        ZhXsczEntity exsitedZhXsczEntity = baseMapper.selectById(zhXsczEntity.getId());

        String typeName = exsitedZhXsczEntity.getType() == 0 ? "污染防治_" : "城管_";

        String fileName = typeName + DateUtil.formatDate(exsitedZhXsczEntity.getXfsj()) + "_" + exsitedZhXsczEntity.getCzsd() + "（" + exsitedZhXsczEntity.getSjhdd() + "）";

        //update zhXsczFjEntity
        if(CollectionUtil.isNotEmpty(zhXsczEntity.getFkFjList())){
            zhXsczEntity.getFkFjList().forEach(e -> {

                ZhXsczFjEntity zhXsczFjEntity = zhXsczFjDao.selectById(e.getId());
                zhXsczFjEntity.setXsczid(zhXsczEntity.getId());

                File rename = FileUtil.rename(new File(zhXsczFjEntity.getPath() + File.separator + zhXsczFjEntity.getId() + File.separator + zhXsczFjEntity.getFilename()), fileName, true, false);

                zhXsczFjEntity.setFilename(rename.getName());

                zhXsczFjDao.updateById(zhXsczFjEntity);
            });
        }

    }

    @Override
    public List<Map<String, Object>> queryZhXsczStat(Map<String, Object> params) {
        params.put("now", new Date());
        List<Map<String, Object>> list = baseMapper.queryZhXsczStat(params);

        //select ifnull(temp.total,0) as total,
        //ifnull(temp.zcTotal,0) as zcTotal,
        //ifnull(temp.csTotal,0) as csTotal,
        //ifnull(temp.wfkTotal,0) as wfkTotal,
        //t.dept_id, t.dept_name as deptName

        Map<String, Object> total = new HashMap<>();
        total.put("deptName", "合计");
        total.put("total", list.stream().mapToInt(e -> Integer.parseInt(String.valueOf(e.get("total")))).sum());
        total.put("zcTotal", list.stream().mapToInt(e -> Integer.parseInt(String.valueOf(e.get("zcTotal")))).sum());
        total.put("csTotal", list.stream().mapToInt(e -> Integer.parseInt(String.valueOf(e.get("csTotal")))).sum());
        total.put("wfkTotal", list.stream().mapToInt(e -> Integer.parseInt(String.valueOf(e.get("wfkTotal")))).sum());

        list.add(total);
        return list;
    }


    @Override
    public List<Map<String, Object>> queryZhXsczStatDetail(Map<String, Object> params) {
        return baseMapper.queryZhXsczStatDetail(params);
    }

    @Override
    public void modifyWithFj(ZhXsczEntity zhXsczEntity) {

        if (baseMapper.updateById(zhXsczEntity) <= 0) {
            throw new RRException("反馈处置异常，请联系管理员");
        }

        ZhXsczEntity exsitedZhXsczEntity = baseMapper.selectById(zhXsczEntity.getId());


        String typeName = exsitedZhXsczEntity.getType() == 0 ? "污染防治_" : "城管_";
        String fileName = typeName + DateUtil.formatDate(exsitedZhXsczEntity.getXfsj()) + "_" + exsitedZhXsczEntity.getCzsd() + "（" + exsitedZhXsczEntity.getSjhdd() + "）";

        //update zhXsczFjEntity
        if (CollectionUtil.isNotEmpty(zhXsczEntity.getZhXsczFjList())) {

            List<ZhXsczFjEntity> exsitedFjEntities = zhXsczFjDao.selectList(new QueryWrapper<ZhXsczFjEntity>().eq("xsczid", zhXsczEntity.getId()).eq("type", 0));
            exsitedFjEntities.forEach(e -> {
                zhXsczFjDao.deleteById(e.getId());
            });

            zhXsczEntity.getZhXsczFjList().forEach(e -> {

                ZhXsczFjEntity zhXsczFjEntity = zhXsczFjDao.selectById(e.getId());
                zhXsczFjEntity.setXsczid(zhXsczEntity.getId());

                File rename = FileUtil.rename(new File(zhXsczFjEntity.getPath() + File.separator + zhXsczFjEntity.getId() + File.separator + zhXsczFjEntity.getFilename()), fileName, true, false);

                zhXsczFjEntity.setFilename(rename.getName());

                zhXsczFjDao.updateById(zhXsczFjEntity);

            });
        }

        if(CollectionUtil.isNotEmpty(zhXsczEntity.getFkFjList())){

            List<ZhXsczFjEntity> exsitedFjEntities = zhXsczFjDao.selectList(new QueryWrapper<ZhXsczFjEntity>().eq("xsczid", zhXsczEntity.getId()).eq("type", 1));
            exsitedFjEntities.forEach(e -> {
                zhXsczFjDao.deleteById(e.getId());
            });

            zhXsczEntity.getFkFjList().forEach(e -> {

                ZhXsczFjEntity zhXsczFjEntity = zhXsczFjDao.selectById(e.getId());
                zhXsczFjEntity.setXsczid(zhXsczEntity.getId());

                File rename = FileUtil.rename(new File(zhXsczFjEntity.getPath() + File.separator + zhXsczFjEntity.getId() + File.separator + zhXsczFjEntity.getFilename()), fileName, true, false);

                zhXsczFjEntity.setFilename(rename.getName());

                zhXsczFjDao.updateById(zhXsczFjEntity);
            });
        }

    }

}
