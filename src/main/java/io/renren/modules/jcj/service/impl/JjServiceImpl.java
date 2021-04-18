package io.renren.modules.jcj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.dao.JjDao;
import io.renren.modules.jcj.entity.JcjJjxx;
import io.renren.modules.jcj.service.JjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JjServiceImpl extends ServiceImpl<JjDao, JcjJjxx> implements JjService {

    @Autowired
    private  JjDao jjDao;

    @Override
    @DataSource("slave2")
    public IPage queryJjList(Map<String, Object> params) {

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<JcjJjxx> pageParams = new Page(page,limit);
        IPage<JcjJjxx> pageResult = jjDao.queryJjList(pageParams, String.valueOf(params.get("condition")));
        for (JcjJjxx jcjJjxx:pageResult.getRecords()) {
            if(jcjJjxx.getDjsj()!=null&&!jcjJjxx.getDjsj().isEmpty()){
                jcjJjxx.setDjsj(jcjJjxx.getDjsj().substring(0,4)+"/"+jcjJjxx.getDjsj().substring(4,6)+"/"+jcjJjxx.getDjsj().substring(6,8)+" "+jcjJjxx.getDjsj().substring(8,10)+":"+jcjJjxx.getDjsj().substring(10,12));
            }
            if(jcjJjxx.getDjdwmc()!=null&&!jcjJjxx.getDjdwmc().isEmpty()){
                jcjJjxx.setDjdwmc(jcjJjxx.getDjdwmc().substring(10));
            }
        }

        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public JcjJjxx queryByJjbh(String jjbh) {
        return jjDao.selectOne(new QueryWrapper<JcjJjxx>().eq("jjbh", jjbh));
    }
}
