package io.renren.modules.mrtb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.mrtb.entity.ZhMrtb;

import java.util.List;
import java.util.Map;


public interface MrtbService extends IService<ZhMrtb> {
    Map<String, Object> getYesterday(Map<String, Object> params);

    boolean saveCC(ZhMrtb zhMrtb, List<JcjCjxx> jcjcjxx);

    void calMrtb(int tbid);

    void sentMrtb(int mrtbId, String msg, String ipAddr, String createBy);
}

