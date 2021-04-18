package io.renren.modules.mrtb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.mrtb.dao.MrtbDao;
import io.renren.modules.mrtb.dao.MrtbXsjqDao;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.mrtb.entity.ZhMrtbXsjq;
import io.renren.modules.mrtb.service.MrtbService;
import io.renren.modules.mrtb.service.MrtbXsjqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class MrtbXsjqServiceImpl extends ServiceImpl<MrtbXsjqDao, ZhMrtbXsjq> implements MrtbXsjqService {

}
