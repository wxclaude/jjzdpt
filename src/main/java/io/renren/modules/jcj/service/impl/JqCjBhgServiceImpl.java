package io.renren.modules.jcj.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.jcj.dao.JqCjBhgDao;
import io.renren.modules.jcj.dao.JqCjViewDao;
import io.renren.modules.jcj.entity.JqCjBhgEntity;
import io.renren.modules.jcj.entity.JqCjViewEntity;
import io.renren.modules.jcj.service.JqCjBhgService;
import io.renren.modules.jcj.service.JqCjViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JqCjBhgServiceImpl extends ServiceImpl<JqCjBhgDao, JqCjBhgEntity> implements  JqCjBhgService{

    @Autowired
    private JqCjBhgDao jqCjBhgDao;

    @Override
    public List<Map<String, Object>> queryCjStatic(Map<String, Object> params, boolean isSorted) {
        List<Map<String, Object>> list = jqCjBhgDao.queryCjStatic(params);

        list.forEach(e -> {
            if (MapUtil.getInt(e, "ccs") > 0) {
                BigDecimal bigDecimalLv = NumberUtil.mul(NumberUtil.div(MapUtil.getStr(e, "bhgs"), MapUtil.getStr(e, "ccs")), new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (bigDecimalLv.intValue() == 0) {
                    e.put("bhglv", 0);
                } else if (bigDecimalLv.intValue() == 100) {
                    e.put("bhglv", 100);
                } else {
                    e.put("bhglv", bigDecimalLv);
                }
            } else {
                e.put("bhglv", 0);

            }
        });

        if (isSorted) {
            Collections.sort(list, (a, b) -> MapUtil.getDouble(a, "bhglv").compareTo(MapUtil.getDouble(b, "bhglv")));
        }



        //int ccsTotal = list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("ccs")))).sum();
        //int bhgsTotal = list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("bhgs")))).sum();
        //double bhglvtal = list.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("bhglv")))).average().getAsDouble();
        //int wzgsTotal = list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("wzgs")))).sum();
        //
        //Map<String, Object> totalMap = new HashMap<>();
        //totalMap.put("ccs", ccsTotal);
        //totalMap.put("bhgs", bhgsTotal);
        //totalMap.put("bhglv", new BigDecimal(bhglvtal).setScale(2,BigDecimal.ROUND_HALF_UP));
        //totalMap.put("wzgs", wzgsTotal);
        //totalMap.put("deptname", "合计");
        //list.add(totalMap);

        return list;


    }

    @Override
    public List<Map<String, Object>> queryCj(Map<String, Object> params) {
        return jqCjBhgDao.queryCj(params);
    }
}
