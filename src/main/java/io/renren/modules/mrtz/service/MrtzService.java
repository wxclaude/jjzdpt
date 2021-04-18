package io.renren.modules.mrtz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface MrtzService extends IService<ZhMrtzEntity> {

    IPage<ZhMrtzEntity> queryMrtz(Map<String, Object> params);

    void addMrtz(MultipartFile file, String tzDate, String deptId, String ip);

    List<Map<String, Object>> queryMrtzStat(Map<String, Object> params);
}

