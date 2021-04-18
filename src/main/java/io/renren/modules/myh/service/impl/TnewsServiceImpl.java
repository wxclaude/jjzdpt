package io.renren.modules.myh.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.modules.myh.consts.MyhConsts;
import io.renren.modules.myh.dao.TnewsDao;
import io.renren.modules.myh.entity.Tnews;
import io.renren.modules.myh.service.TnewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Service
public class TnewsServiceImpl extends ServiceImpl<TnewsDao, Tnews> implements TnewsService {

    @Autowired
    private MyhConsts myhConsts;

    @Override
    public String uploadNewsImage(MultipartFile file) {
        try {

            //保存文件
            if (!FileUtil.exist(myhConsts.getUPLOAD_DIR())) {
                FileUtil.mkdir(myhConsts.getUPLOAD_DIR());
            }

            long current = DateUtil.current(false);
            String fileName = "news_" + current + "_" + file.getOriginalFilename();
            File wordFile = FileUtil.file(myhConsts.getUPLOAD_DIR() + File.separator + fileName);
            FileUtil.writeBytes(file.getBytes(), wordFile);

            return myhConsts.getFILE_PREFIX() + fileName;

        } catch (Exception e) {
            throw new RRException("上传文件异常，请联系管理员");
        }
    }
}
