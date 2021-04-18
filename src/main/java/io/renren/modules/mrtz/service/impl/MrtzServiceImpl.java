package io.renren.modules.mrtz.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.modules.mrtz.dao.MrtzDao;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.mrtz.service.MrtzService;
import io.renren.modules.sys.consts.SysModelConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class MrtzServiceImpl extends ServiceImpl<MrtzDao, ZhMrtzEntity> implements MrtzService {

    @Autowired
    private MrtzDao mrtzDao;

    @Autowired
    private SysModelConsts sysModelConsts;

    @Override
    public IPage<ZhMrtzEntity> queryMrtz(Map<String, Object> params) {

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<ZhMrtzEntity> pageParams = new Page(page,limit);
        Page<ZhMrtzEntity> pageResult = mrtzDao.queryMrtz(pageParams,params);

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMrtz(MultipartFile file, String tzDate, String deptId, String ip) {
        //先删除旧数据
        mrtzDao.delete(new QueryWrapper<ZhMrtzEntity>().eq("tz_date", tzDate).eq("dept_id", deptId));

        //先插入数据
        ZhMrtzEntity mrtzEntity = new ZhMrtzEntity();
        mrtzEntity.setDeptId(deptId);
        mrtzEntity.setIp(ip);
        mrtzEntity.setTzDate(tzDate);
        mrtzEntity.setPath(sysModelConsts.getFILE_PATH_MRTZ());
        mrtzEntity.setFileName(file.getOriginalFilename());
        mrtzEntity.setUid(String.valueOf(DateUtil.current(false)));

        if (mrtzDao.insert(mrtzEntity) <= 0) {
            throw new RRException("上传每日台账异常，请联系管理员");
        }

        try {

            //保存文件
            if (FileUtil.exist(sysModelConsts.getFILE_PATH_MRTZ())) {
                FileUtil.mkdir(sysModelConsts.getFILE_PATH_MRTZ());
            }

            //if (FileUtil.exist(sysModelConsts.getFILE_PATH_MRTZ() + deptId)) {
            //    FileUtil.mkdir(sysModelConsts.getFILE_PATH_MRTZ() + deptId);
            //}

            File wordFile = FileUtil.file(sysModelConsts.getFILE_PATH_MRTZ() + deptId + File.separator + mrtzEntity.getUid() + File.separator + file.getOriginalFilename());
            FileUtil.writeBytes(file.getBytes(), wordFile);

        } catch (Exception e) {
            throw new RRException("上传每日台账异常，请联系管理员");
        }


    }

    @Override
    public List<Map<String, Object>> queryMrtzStat(Map<String, Object> params) {

        List<Map<String, Object>> list = mrtzDao.queryMrtzStat(params);
        //获取当前日期区间

        String startDate = MapUtil.getStr(params, "startDate");
        String endDate = MapUtil.getStr(params, "endDate");
        long total = DateUtil.between(DateUtil.parseDate(startDate), DateUtil.parseDate(endDate), DateUnit.DAY) + 1;

        list.forEach(e -> {
            e.put("total", total);
            e.put("wsb", NumberUtil.sub(String.valueOf(total), String.valueOf(e.get("ysb"))));
            e.put("sbl", NumberUtil.div(String.valueOf(e.get("ysb")), String.valueOf(total), 2));
            e.put("sblStr", NumberUtil.mul(String.valueOf(e.get("sbl")), "100").intValue() + "%");
        });

        return list;

    }
}
