package io.renren.modules.report.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.app.utils.JwtUtils;
import io.renren.modules.df.consts.DfConsts;
import io.renren.modules.df.dao.DfDetailDao;
import io.renren.modules.df.entity.*;
import io.renren.modules.df.service.*;
import io.renren.modules.mrtz.dao.MrtzDao;
import io.renren.modules.mrtz.service.MrtzService;
import io.renren.modules.report.dao.ReportDao;
import io.renren.modules.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020-05-06
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao reportDao;
    @Autowired
    private DfDetailDao dfDetailDao;
    @Autowired
    private DfQbxxService dfQbxxService;
    @Autowired
    private DfSjfnService dfSjfnService;
    @Autowired
    private DfZhzhService dfZhzhService;
    @Autowired
    private DfJqzlkhService dfJqzlkhService;
    @Autowired
    private DfQbxsService qbxsService;
    @Autowired
    private DfZdmbService dfZdmbService;

    @Autowired
    private MrtzDao mrtzDao;

    @Autowired
    private DfConsts dfConsts;

    @Override
    @DataSource("slave2")
    public Map<String, Object> queryTwData(String date) {

        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        //data={
        //    day:["4月1日","4月2日"],
        //    onlinePer:[12,12]
        //}

        List<Map<String, Object>> list = reportDao.queryTwData(params);

        Map<String, Object> reportMap = new HashMap<>();
        List<String> dayList = new ArrayList<>();
        List<String> perList = new ArrayList<>();

        list.stream().forEach(e -> {

            dayList.add(StrUtil.subAfter(MapUtil.getStr(e, "day"), "-", false));
            int total = MapUtil.getInt(e, "total");
            int zxs = MapUtil.getInt(e, "zxs");
            double per = 0;
            if (total > 0) {
                per = NumberUtil.div(zxs,total);
            }
            perList.add(String.valueOf(new BigDecimal(per * 100).intValue()));

        });

        reportMap.put("day", dayList);
        reportMap.put("onlinePer", perList);

        return reportMap;
    }

    @Override
    @DataSource("slave2")
    public Map<String, Object> queryZjData(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        //data={
        //    day:["邗上","蒋王"],
        //    onlinePer:[12,12]
        //}

        List<Map<String, Object>> list = reportDao.queryZjData(params);
        Iterator<Map<String, Object>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            if (MapUtil.getStr(next, "所属单位").contains("(") || MapUtil.getStr(next, "所属单位").contains("（") ) {
                iterator.remove();
            }
        }

        //去除自建两个字
        String ssdw = null;
        for (Map<String, Object> map : list) {
            ssdw = MapUtil.getStr(map, "所属单位");
            if (StrUtil.isNotEmpty(ssdw)) {
                map.put("所属单位", StrUtil.replace(ssdw, "自建", ""));
            }
        }

        Map<String, Object> reportMap = new HashMap<>();
        List<String> dayList = new ArrayList<>();
        List<String> perList = new ArrayList<>();

        list.stream().forEach(e -> {

            dayList.add(MapUtil.getStr(e, "所属单位"));
            int total = MapUtil.getInt(e, "total");
            int zxs = MapUtil.getInt(e, "zxs");
            double per = 0;
            if (total > 0) {
                per = NumberUtil.div(zxs,total);
            }
            perList.add(String.valueOf(new BigDecimal(per * 100).intValue()));

        });

        reportMap.put("pcs", dayList);
        reportMap.put("onlinePer", perList);

        return reportMap;
    }

    @Override
    public Map<String, Object> queryYdjcjData(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        List<Map<String, Object>> list = reportDao.queryYdjcjData(params);

        List<String> pcsList = new ArrayList<>();
        List<String> xptjjlvList = new ArrayList<>();
        List<String> xptcjlvList = new ArrayList<>();
        List<String> yddcjlvList = new ArrayList<>();


        for (Map<String, Object> map : list) {
            pcsList.add(MapUtil.getStr(map, "short_dept_name"));
            xptjjlvList.add(StrUtil.subBefore(MapUtil.getStr(map, "xptjjlv"), ".", false));
            xptcjlvList.add(StrUtil.subBefore(MapUtil.getStr(map, "xptcjlv"), ".", false));
            yddcjlvList.add(StrUtil.subBefore(MapUtil.getStr(map, "yddcjlv"), ".", false));
        }


        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("xptjjlv", xptjjlvList);
        resultMap.put("xptcjlv", xptcjlvList);
        resultMap.put("yddcjlv", yddcjlvList);
        resultMap.put("pcs", pcsList);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryDfData(Map<String, Object> params) {

        String dfMonth = MapUtil.getStr(params, "dfMonth");
        String type = MapUtil.getStr(params, "type");//1派出所 2机关
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> detailList = reportDao.queryDfData(params);

        Set<String> xAxisSet = new LinkedHashSet<>();
        List<Map<String, Object>> series = new ArrayList<>();

        Map<String, Object> seriesMap = null;
        for (Map<String, Object> map : detailList) {
            xAxisSet.add(MapUtil.getStr(map, "deptName"));
            seriesMap = new HashMap<>();
            seriesMap.put("deptId", MapUtil.getStr(map, "deptId"));
            seriesMap.put("value", new BigDecimal(MapUtil.getStr(map, "totalScore")).setScale(2,BigDecimal.ROUND_HALF_UP));
            series.add(seriesMap);
        }


        result.put("xAxis", xAxisSet);
        result.put("series", series);

        return result;
    }


    @Override
    public Map<String, Object> queryDfDataNew(Map<String, Object> params) {

        String dfMonth = MapUtil.getStr(params, "dfMonth");
        String type = MapUtil.getStr(params, "type");//1派出所 2机关

        int count = 0;
        int exsitedData = 0;
        while (1 == 1) {
            if (count > 10) {
                break;
            }
            System.out.println("count=" + count);
            System.out.println("dfMonth=" + dfMonth);

            exsitedData = dfQbxxService.count(
                    new QueryWrapper<DfQbxx>()
                            .eq("df_month", dfMonth)
                            .eq("type", type)
            );

            if (exsitedData > 0) {
                params.put("dfMonth", dfMonth);
                break;
            }

            count++;

            dfMonth = DateUtil.format(DateUtil.offsetMonth(DateUtil.parse(dfMonth,"yyyy-MM"), -1), "yyyy-MM");


        }

        Map<String, Object> result = generateDfDetail(dfMonth, type);

        return result;

    }

    @Override
    public List<Map<String,Object>> queryYdjcjDataNew(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        List<Map<String, Object>> list = reportDao.queryYdjcjData(params);

        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String,Object> dataMap = null;
        for (Map<String, Object> map : list) {

            dataMap = new HashMap<>();
            dataMap.put("pcs", MapUtil.getStr(map, "short_dept_name"));
            dataMap.put("新平台接警率", StrUtil.subBefore(MapUtil.getStr(map, "xptjjlv"), ".", false));
            dataMap.put("新平台处警率", StrUtil.subBefore(MapUtil.getStr(map, "xptcjlv"), ".", false));
            dataMap.put("移动端处警率", StrUtil.subBefore(MapUtil.getStr(map, "yddcjlv"), ".", false));

            resultList.add(dataMap);
        }


        return resultList;
    }

    @Override
    public Map<String, Object> queryQbxxData(Map<String, Object> params) {


        String dfMonth = MapUtil.getStr(params, "dfMonth");

        int count = 0;
        int exsitedData = 0;
        while (1 == 1) {
            if (count > 10) {
                break;
            }
            System.out.println("count=" + count);
            System.out.println("dfMonth=" + dfMonth);

            exsitedData = reportDao.queryQbxxDataCount(dfMonth);

            if (exsitedData > 0) {
                params.put("dfMonth", dfMonth);
                break;
            }

            count++;

            dfMonth = DateUtil.format(DateUtil.offsetMonth(DateUtil.parse(dfMonth,"yyyy-MM"), -1), "yyyy-MM");


        }

        List<Map<String, Object>> list = reportDao.queryQbxxData(params);
        Map<String, Object> resultMap = generateQbxx(list);
        return resultMap;
    }

    private Map<String, Object> generateQbxx(List<Map<String, Object>> list) {
        List<String> pcsList = new ArrayList<>();
        List<String> s3List = new ArrayList<>();
        List<String> s5List = new ArrayList<>();
        List<String> s7List = new ArrayList<>();


        for (Map<String, Object> map : list) {
            pcsList.add(MapUtil.getStr(map, "short_dept_name"));
            s3List.add(StrUtil.replace(MapUtil.getStr(map, "s3"), "%", ""));
            s5List.add(StrUtil.replace(MapUtil.getStr(map, "s5"), "%", ""));
            s7List.add(StrUtil.replace(MapUtil.getStr(map, "s7"), "%", ""));
        }


        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("s3", s3List);
        resultMap.put("s5", s5List);
        resultMap.put("s7", s7List);
        resultMap.put("pcs", pcsList);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryMrtzData(Map<String, Object> params) {

        Set<String> xAxisSet = new LinkedHashSet<>();
        List<String> series = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> qbxxlist = mrtzDao.queryMrtzData(params);

        qbxxlist.forEach(e -> {
            xAxisSet.add(MapUtil.getStr(e,"deptName"));
            series.add(MapUtil.getStr(e,"total"));
        });

        result.put("xAxis", xAxisSet);
        result.put("series", series);

        return result;
    }

    @Override
    public Map<String, Object> queryDfDataNull(Map<String, Object> params) {

        String dfMonth = MapUtil.getStr(params, "dfMonth");
        String type = MapUtil.getStr(params, "type");//1派出所 2机关

        Map<String, Object> result = generateDfDetail(dfMonth, type);

        return result;

    }

    @Override
    public Map<String, Object> queryQbxxDataNull(Map<String, Object> params) {

        List<Map<String, Object>> list = reportDao.queryQbxxData(params);
        Map<String, Object> resultMap = generateQbxx(list);
        return resultMap;
    }

    private Map<String, Object> generateDfDetail(String dfMonth, String type) {
        List<Map<String, Object>> qbxxlist = dfQbxxService.listMaps(
                new QueryWrapper<DfQbxx>()
                        .select("sum(ifnull(s9,0)) as total", "dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        List<Map<String, Object>> sjfnlist = dfSjfnService.listMaps(
                new QueryWrapper<DfSjfn>()
                        .select("sum(ifnull(s2,0)+ifnull(s4,0)+ifnull(s6,0)+ifnull(s17,0)+ifnull(s16,0)+ifnull(s18,0)+ifnull(s19,0)+ifnull(s20,0)+ifnull(s21,0)+ifnull(s22,0)) as totalAll"
                                ,"sum(ifnull(s2,0)+ifnull(s4,0)+ifnull(s6,0)+ifnull(s17,0)) as total"
                                ,"sum(ifnull(s16,0)+ifnull(s18,0)+ifnull(s19,0)+ifnull(s20,0)+ifnull(s21,0)+ifnull(s22,0)) as total2"
                                ,"dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        List<Map<String, Object>> kjxxList = dfSjfnService.listMaps(
                new QueryWrapper<DfSjfn>()
                        .select("sum(ifnull(s2,0)+ifnull(s4,0)+ifnull(s6,0)+ifnull(s17,0)+ifnull(s16,0)+ifnull(s18,0)+ifnull(s19,0)+ifnull(s20,0)+ifnull(s21,0)+ifnull(s22,0)) as totalAll"
                                ,"sum(ifnull(s16,0)+ifnull(s18,0)+ifnull(s19,0)+ifnull(s20,0)+ifnull(s21,0)+ifnull(s22,0)) as total"
                                ,"dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        List<Map<String, Object>> zhzhList = dfZhzhService.listMaps(
                new QueryWrapper<DfZhzh>()
                        //.select("sum(ifnull(s4,0)+ifnull(s7,0)+ifnull(s10,0)+ifnull(s13,0)+ifnull(s15,0)+ifnull(s17,0)) as total", "dept", "dept_id")
                        .select("sum(ifnull(s99,0)) as total", "dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        List<Map<String, Object>> jqzlkhList = dfJqzlkhService.listMaps(
                new QueryWrapper<DfJqzlkh>()
                        .select("sum(ifnull(s3,0)) as total", "dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        List<Map<String, Object>> qbxsList = qbxsService.listMaps(
                new QueryWrapper<DfQbxs>()
                        .select("sum(ifnull(s1,0)+ifnull(s2,0)-ifnull(s3,0)) as total", "dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        List<Map<String, Object>> zdmbList = dfZdmbService.listMaps(
                new QueryWrapper<DfZdmb>()
                        .select("sum(ifnull(s1,0)) as total", "dept", "dept_id")
                        .eq("df_month", dfMonth)
                        .eq("type", type)
                        .groupBy("dept", "dept_id")
                        .orderByAsc("length(dept_id)", "dept_id")
        );

        Set<String> xAxisSet = new LinkedHashSet<>();
        List<Map<String, Object>> series = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> seriesMap = null;
        //for (Map<String, Object> sjfn : sjfnlist) {
        //    //xAxisSet.add(MapUtil.getStr(sjfn, "dept"));
        //    seriesMap = new HashMap<>();
        //    seriesMap.put("dept", MapUtil.getStr(sjfn, "dept"));
        //    seriesMap.put("deptId", MapUtil.getStr(sjfn, "dept_id"));
        //    seriesMap.put("value", NumberUtil.mul(MapUtil.getStr(sjfn, "total"), dfConsts.getSJFN()).setScale(2, BigDecimal.ROUND_HALF_UP));
        //
        //
        //    for (Map<String, Object> kjxx : kjxxList) {
        //        if (MapUtil.getStr(kjxx, "dept_id").equals(MapUtil.getStr(sjfn, "dept_id"))) {
        //            seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(kjxx, "total"), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        //        }
        //    }
        //
        //    for (Map<String, Object> zhzh : zhzhList) {
        //        if (MapUtil.getStr(zhzh, "dept_id").equals(MapUtil.getStr(sjfn, "dept_id"))) {
        //            seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(zhzh, "total"), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        //        }
        //    }
        //
        //    for (Map<String, Object> jqzlkh : jqzlkhList) {
        //        if (MapUtil.getStr(jqzlkh, "dept_id").equals(MapUtil.getStr(sjfn, "dept_id"))) {
        //            seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(jqzlkh, "total"), dfConsts.getJQZLKH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        //        }
        //    }
        //
        //    for (Map<String, Object> qbxx : qbxxlist) {
        //        if (MapUtil.getStr(qbxx, "dept_id").equals(MapUtil.getStr(sjfn, "dept_id"))) {
        //            seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(qbxx, "total"), dfConsts.getQBXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        //        }
        //    }
        //
        //    for (Map<String, Object> qbxs : qbxsList) {
        //        if (MapUtil.getStr(qbxs, "dept_id").equals(MapUtil.getStr(sjfn, "dept_id"))) {
        //            seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(qbxs, "total"), dfConsts.getQBXS()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        //        }
        //    }
        //
        //    for (Map<String, Object> zdmb : zdmbList) {
        //        if (MapUtil.getStr(zdmb, "dept_id").equals(MapUtil.getStr(sjfn, "dept_id"))) {
        //            seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(zdmb, "total"), dfConsts.getZDMB()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        //        }
        //    }
        //
        //    series.add(seriesMap);
        //}

        for (Map<String, Object> zhzh : zhzhList) {
            //xAxisSet.add(MapUtil.getStr(sjfn, "dept"));
            seriesMap = new HashMap<>();
            seriesMap.put("dept", MapUtil.getStr(zhzh, "dept"));
            seriesMap.put("deptId", MapUtil.getStr(zhzh, "dept_id"));
            seriesMap.put("value", NumberUtil.mul(MapUtil.getStr(zhzh, "total"), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP));


            for (Map<String, Object> kjxx : kjxxList) {
                if (MapUtil.getStr(kjxx, "dept_id").equals(MapUtil.getStr(zhzh, "dept_id"))) {
                    seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(kjxx, "total"), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }

            for (Map<String, Object> sjfn : sjfnlist) {
                if (MapUtil.getStr(sjfn, "dept_id").equals(MapUtil.getStr(zhzh, "dept_id"))) {
                    seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(sjfn, "total"), dfConsts.getSJFN()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }

            for (Map<String, Object> jqzlkh : jqzlkhList) {
                if (MapUtil.getStr(jqzlkh, "dept_id").equals(MapUtil.getStr(zhzh, "dept_id"))) {
                    seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(jqzlkh, "total"), dfConsts.getJQZLKH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }

            for (Map<String, Object> qbxx : qbxxlist) {
                if (MapUtil.getStr(qbxx, "dept_id").equals(MapUtil.getStr(zhzh, "dept_id"))) {
                    seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(qbxx, "total"), dfConsts.getQBXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }

            for (Map<String, Object> qbxs : qbxsList) {
                if (MapUtil.getStr(qbxs, "dept_id").equals(MapUtil.getStr(zhzh, "dept_id"))) {
                    seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(qbxs, "total"), dfConsts.getQBXS()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }

            for (Map<String, Object> zdmb : zdmbList) {
                if (MapUtil.getStr(zdmb, "dept_id").equals(MapUtil.getStr(zhzh, "dept_id"))) {
                    seriesMap.put("value", NumberUtil.add(MapUtil.getStr(seriesMap, "value"), NumberUtil.mul(MapUtil.getStr(zdmb, "total"), dfConsts.getZDMB()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
            }

            series.add(seriesMap);
        }


        //按照分值排序
        //series = CollectionUtil.sort(series, (a, b) -> MapUtil.getStr(b, "value").compareTo(MapUtil.getStr(a, "value")));
        Collections.sort(series, (a, b) -> MapUtil.getDouble(b, "value").compareTo(MapUtil.getDouble(a, "value")));


        result.put("xAxis", series.stream().map(e -> e.get("dept")).collect(Collectors.toList()));
        result.put("series", series);
        result.put("date", dfMonth);
        return result;
    }

}
