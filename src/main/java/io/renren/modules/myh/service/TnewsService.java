package io.renren.modules.myh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.myh.entity.Tnews;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author tomchen
 * @date 2020-10-22
 */
public interface TnewsService extends IService<Tnews> {
    String uploadNewsImage(MultipartFile file);
}

