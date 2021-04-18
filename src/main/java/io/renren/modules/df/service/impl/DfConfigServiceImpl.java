package io.renren.modules.df.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.MyUtils;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.df.dao.DfConfigDao;
import io.renren.modules.df.entity.DfConfig;
import io.renren.modules.df.service.DfConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class DfConfigServiceImpl extends ServiceImpl<DfConfigDao, DfConfig> implements DfConfigService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDfConfig(List<DfConfig> list, String name) {
        //先添加父项目
        /*
        if (CollectionUtil.isEmpty(list)) {
            throw new RRException("请输入打分父项");
        }

        String parentName = list.get(0).getParentName();
        if (StrUtil.isEmpty(parentName)) {
            throw new RRException("请输入打分父项");
        }

        List<DfConfig> dfConfigsExsit = baseMapper.selectList(new QueryWrapper<DfConfig>().eq("dfx", parentName).eq("type", 0));
        if (CollectionUtil.isNotEmpty(dfConfigsExsit)) {
            throw new RRException("存在同名打分父项");
        }

        DfConfig parent = new DfConfig();
        parent.setDfx(parentName);
        parent.setType(0);
        parent.setCreateBy(name);

        int parentId = baseMapper.insertOne(parent);
        */

        list.stream().forEach(e->{
            e.setCreateBy(name);
            e.setType(1);
            //e.setParentId(parent.getId());
            baseMapper.insert(e);
        });

    }

    @Override
    public IPage<DfConfig> queryDfConfig(Map<String, Object> params) {
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<DfConfig> pageParams = new Page(page,limit);

        IPage<DfConfig> dfConfigList = baseMapper.selectPage(pageParams, new QueryWrapper<DfConfig>().orderByDesc("create_time"));

        //IPage<DfConfig> dfConfigList = baseMapper.selectPage(pageParams, new QueryWrapper<DfConfig>().eq("type", 0).orderByDesc("create_time"));
        //dfConfigList.getRecords().stream().forEach(e->{
        //    List<DfConfig> list = baseMapper.selectList(new QueryWrapper<DfConfig>().eq("type", 1).eq("parent_id", e.getId()).orderByDesc("create_time"));
        //
        //    int totalQzb = list.stream().mapToInt(DfConfig::getQzb).sum();
        //    e.setTotalQzb(totalQzb);
        //
        //    StringBuffer sb = new StringBuffer();
        //    list.stream().forEach(l->{
        //        sb.append(l.getDfx()).append("(").append(l.getQzb()).append("%) ");
        //    });
        //
        //    e.setContent(sb.toString());
        //
        //});
        return dfConfigList;
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryAllDept(Map<String, Object> params) {
        return baseMapper.queryAllDept(params);
    }

    @Override
    @DataSource("slave2")
    public Map<String, Object> getDeptByDeptId(String deptId) {

        return baseMapper.getDeptByDeptId(deptId);
    }

}
