package io.renren.modules.xscz.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.MyUtils;
import io.renren.modules.sys.consts.SysModelConsts;
import io.renren.modules.xscz.dao.ZhXsczFjDao;
import io.renren.modules.xscz.entity.ZhXsczFjEntity;
import io.renren.modules.xscz.service.ZhXsczFjService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Service
@Slf4j
public class ZhXsczFjServiceImpl extends ServiceImpl<ZhXsczFjDao, ZhXsczFjEntity> implements ZhXsczFjService {

    @Autowired
    private SysModelConsts sysModelConsts;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadZhXsczFj(MultipartFile file, int type, String name, String ip) {


        ZhXsczFjEntity zhXsczFjEntity = new ZhXsczFjEntity();
        zhXsczFjEntity.setId(MyUtils.getUUID());
        zhXsczFjEntity.setFilename(file.getOriginalFilename());
        zhXsczFjEntity.setPath(sysModelConsts.getFILE_PATH_XSCZ());
        zhXsczFjEntity.setType(type);
        zhXsczFjEntity.setCreateBy(name);
        zhXsczFjEntity.setIp(ip);

        if (baseMapper.insert(zhXsczFjEntity) <= 0) {
            throw new RRException("上传文件异常，请联系管理员");
        }

        try {

            //保存文件
            if (FileUtil.exist(sysModelConsts.getFILE_PATH_XSCZ())) {
                FileUtil.mkdir(sysModelConsts.getFILE_PATH_XSCZ());
            }

            File wordFile = FileUtil.file(sysModelConsts.getFILE_PATH_XSCZ() + zhXsczFjEntity.getId() + File.separator + file.getOriginalFilename());
            FileUtil.writeBytes(file.getBytes(), wordFile);

        } catch (Exception e) {
            throw new RRException("上传文件异常，请联系管理员");
        }

        return zhXsczFjEntity.getId();

    }
}
