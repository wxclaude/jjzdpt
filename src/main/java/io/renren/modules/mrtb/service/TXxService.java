package io.renren.modules.mrtb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.mrtb.entity.TXx;
import io.renren.modules.mrtb.entity.ZhMrtb;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface TXxService extends IService<TXx> {

    boolean saveCC(TXx tXx);

    TXx getByIdCC(Serializable xxId);

    boolean updateByIdCC(TXx tXx);
}

