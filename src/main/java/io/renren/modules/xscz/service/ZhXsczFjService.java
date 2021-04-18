package io.renren.modules.xscz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.xscz.entity.ZhXsczFjEntity;
import org.springframework.web.multipart.MultipartFile;


public interface ZhXsczFjService extends IService<ZhXsczFjEntity> {

    String uploadZhXsczFj(MultipartFile file, int type, String name, String ip);
}

