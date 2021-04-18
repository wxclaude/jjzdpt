package io.renren.modules.video.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.MyUtils;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.video.consts.VideoConsts;
import io.renren.modules.video.dao.DwbzDao;
import io.renren.modules.video.dao.JyVideoDao;
import io.renren.modules.video.dao.VideoDao;
import io.renren.modules.video.dao.XmConfigDao;
import io.renren.modules.video.entity.DwbzEntity;
import io.renren.modules.video.entity.JyVideoEntity;
import io.renren.modules.video.entity.VideoEntity;
import io.renren.modules.video.entity.XmConfigEntity;
import io.renren.modules.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020/3/14
 */
@Service()
public class VideoServiceImpl extends ServiceImpl<JyVideoDao, JyVideoEntity> implements VideoService {

    @Autowired
    private JyVideoDao jyVideoDao;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private DwbzDao dwbzDao;

    @Autowired
    private XmConfigDao xmConfigDao;


    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryVideoDataByTypeAndDate(String date, String type, List<XmConfigEntity> xmConfigList) {

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("type", type);

        String xmIds = getXmIdStrFromList(xmConfigList);
        params.put("xmIds", MyUtils.formatSqlIn(xmIds));


        if (VideoConsts.TYPE_JY.equals(type) || VideoConsts.TYPE_XQ.equals(type) || VideoConsts.TYPE_NB.equals(type) || VideoConsts.TYPE_QT.equals(type)) {

            //list = jyVideoDao.queryVideoDataByTypeAndDate(params);
            list = jyVideoDao.queryVideoDataByTypeAndDateNew(params);
            list.stream().forEach(e -> {
                //e.put("wz",StrUtil.subBefore(String.valueOf(e.get("pointNo")), "-", true));
                //e.put("luh",StrUtil.subAfter(String.valueOf(e.get("pointNo")), "-", true));

                String pointNo = MapUtil.getStr(e, "pointNo");
                if (StrUtil.count(pointNo, "-") == 3) {//22-1-2-123435

                    e.put("wz",StrUtil.subBefore(pointNo, "-", true));
                    e.put("luh",StrUtil.subAfter(pointNo, "-", true));
                } else if (StrUtil.count(pointNo, "-") == 2) {//22-1-2

                    e.put("wz", pointNo);
                    e.put("luh", "");
                } else {

                    e.put("wz", "");
                    e.put("luh", "");
                }

                int total = Integer.parseInt(String.valueOf(e.get("total")));
                int zxs = Integer.parseInt(String.valueOf(e.get("zxs")));
                double zxl = 0;
                if (total != 0) {
                    zxl = NumberUtil.div(zxs, total, 4);
                }
                e.put("zxl",String.valueOf(zxl));

                e.put("pointName", MapUtil.getStr(e, "wz") + MapUtil.getStr(e, "pointName"));
            });

            list.stream().filter(e -> "异常".equals(MapUtil.getStr(e, "xlzt"))).forEach(e -> {
                //获取最近一次在线时间
                String lastZxDate = jyVideoDao.getLastZxDate(e);
                e.put("zjzxsj", lastZxDate);
            });


        } else if (VideoConsts.TYPE_ZHZX.equals(type)) {//分局自建

            List<Map<String, Object>> detailList = videoDao.queryVideoDataDetailByDateNew(params);
            xmConfigList.stream().forEach(e->{
                detailList.forEach(d->{
                    //因为xmConfig中的xmid可能存在多个xmid，所以这里用contains判断
                    if (e.getXmid().contains(MapUtil.getStr(d, "xmid"))) {
                        e.getXjList().add(d);
                    }
                });
            });

            Map<String,Object> resultMap = null;
            int htdws = 0;//合同点位数
            int xjdws = 0;//巡检点位数
            int xjdwzxs = 0;//巡检点位在线数 有一个监控在线即视为该点位在线
            int xjjks = 0;//巡检监控数
            int xjjkzxs = 0;//巡检监控在线数
            int xjdwgzs = 0;//点位故障数 有一个监控离线即视为该点位有故障

            for (XmConfigEntity xmConfig : xmConfigList) {
                resultMap = new HashMap<>();
                htdws = 0;
                xjdws = 0;
                xjdwzxs = 0;
                xjjks = 0;
                xjjkzxs = 0;
                xjdwgzs = 0;

                htdws = videoDao.getDwByXmids(MyUtils.formatSqlIn(xmConfig.getXmid()));

                xjjks = xmConfig.getXjList().size();
                xjjkzxs = (int) xmConfig.getXjList().stream().filter(e -> "成功".equals(MapUtil.getStr(e, "状态"))).count();
                xjdws = xmConfig.getXjList().stream().collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "监控点名称"), 0, 4))).size();
                xjdwzxs = xmConfig.getXjList().stream().filter(e -> "成功".equals(MapUtil.getStr(e, "状态"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "监控点名称"), 0, 4))).size();
                xjdwgzs = xmConfig.getXjList().stream().filter(e -> !"成功".equals(MapUtil.getStr(e, "状态"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "监控点名称"), 0, 4))).size();

                resultMap.put("dept", xmConfig.getXmmc());
                resultMap.put("configId", xmConfig.getId());
                resultMap.put("htdws", htdws);
                resultMap.put("xjdws", xjdws);
                resultMap.put("xjdwzxs", xjdwzxs);
                resultMap.put("xjjks", xjjks);
                resultMap.put("xjjkzxs", xjjkzxs);
                resultMap.put("xjdwgzs", xjdwgzs);

                list.add(resultMap);

            }


        }
        /*
        else if (VideoConsts.TYPE_ZHZX.equals(type)) {//分局自建
            list = new ArrayList<>();
            List<Map<String, Object>> detailList = videoDao.queryVideoDataDetailByDateNew(params);

            //total,
            //isnull(SUM(CASE WHEN 状态= '成功' THEN 1 ELSE 0 END),0) AS 'zxs',
            //isnull(SUM(CASE WHEN 状态!='成功' THEN 1 ELSE 0 END),0) AS 'lxs'
            Map<String,Object> resultMap = null;
            int dws = 0;//点位数
            int dwzxs = 0;//点位在线数 有一个监控在线即视为该点位在线
            int jks = 0;//监控数
            int zxs = 0;//在线数
            int dwgzs = 0;//点位故障数 有一个监控离线即视为该点位有故障
            if (CollectionUtil.isNotEmpty(detailList)) {

                //按照所属单位分组
                Map<Object, List<Map<String, Object>>> listMap = detailList.stream().collect(Collectors.groupingBy(e -> e.get("所属单位")));

                for (Map.Entry<Object, List<Map<String, Object>>> entry : listMap.entrySet()) {
                    resultMap = new HashMap<>();
                    dws = 0;
                    dwzxs = 0;
                    jks = 0;
                    zxs = 0;
                    dwgzs = 0;

                    jks = entry.getValue().size();
                    zxs = (int) entry.getValue().stream().filter(e -> "成功".equals(MapUtil.getStr(e, "状态"))).count();
                    dws = entry.getValue().stream().collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "监控点名称"), 0, 4))).size();
                    dwzxs = entry.getValue().stream().filter(e -> "成功".equals(MapUtil.getStr(e, "状态"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "监控点名称"), 0, 4))).size();
                    dwgzs = entry.getValue().stream().filter(e -> !"成功".equals(MapUtil.getStr(e, "状态"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "监控点名称"), 0, 4))).size();

                    resultMap.put("dept", StrUtil.subBefore(String.valueOf(entry.getKey()), " ", true));
                    resultMap.put("dws", dws);
                    resultMap.put("dwzxs", dwzxs);
                    resultMap.put("jks", jks);
                    resultMap.put("zxs", zxs);
                    resultMap.put("dwgzs", dwgzs);

                    list.add(resultMap);
                }

            }


        } */
        else if (VideoConsts.TYPE_RL.equals(type)) {//人脸

            list = videoDao.queryRLVideoDataByDate(params);
            list.stream().forEach(e -> {
                e.put("dept", "人脸");
                int total = Integer.parseInt(String.valueOf(e.get("total")));
                int zxs = Integer.parseInt(String.valueOf(e.get("zxs")));
                double zxl = 0;
                if (total != 0) {
                    zxl = NumberUtil.div(zxs, total, 4);
                }
                e.put("zxl", String.valueOf(zxl));
            });

        } else {
            list = videoDao.queryVideoDataByTypeAndDate(params);
            list.stream().forEach(e -> {
                e.put("dept", StrUtil.subBefore(String.valueOf(e.get("所属单位")), " ", true));
                int total = Integer.parseInt(String.valueOf(e.get("total")));
                int zxs = Integer.parseInt(String.valueOf(e.get("zxs")));
                double zxl = 0;
                if (total != 0) {
                    zxl = NumberUtil.div(zxs, total, 4);
                }
                e.put("zxl", String.valueOf(zxl));
            });

        }

        return list;
    }


    @Override
    @DataSource("slave2")
    public IPage<Map<String,Object>> queryVideoDataDetailPage(Map<String, Object> params) {
        IPage<Map<String, Object>> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        String type = String.valueOf(params.get("type"));
        if (VideoConsts.TYPE_RL.equals(type)) {
            pageResult = videoDao.queryRLVideoDataDetailPage(pageParams, params);
        }else{
            pageResult = videoDao.queryVideoDataDetailPage(pageParams, params);
        }

        Integer kind = MapUtil.getInt(params, "kind");
        if (kind == 1) {
            //故障点位查找最近上线时间和累计离线时间
            String sn = null;
            String lastCheckTime = null;
            String zjsxsj = null;
            StringBuffer ljlxsj = null;
            String zjjcsj = null;
            String scjcsj = null;
            long ljlxsjLong = 0;
            Date zjsxsjDate = null;
            Date lastCheckDate = null;
            Date scjcsjDate = null;
            Date zjjcsjDate = null;

            for (Map<String, Object> record : pageResult.getRecords()) {
                sn = MapUtil.getStr(record, "监控点SN");
                lastCheckTime = MapUtil.getStr(record, "检测开始时间");
                zjsxsj = "";
                ljlxsj = new StringBuffer();
                ljlxsjLong = 0;
                zjsxsjDate = null;
                lastCheckDate = null;

                params.put("sn", sn);
                params.put("lastCheckTime", lastCheckTime);

                zjsxsj = videoDao.getZjsxBySn(params);

                if (StrUtil.isNotEmpty(zjsxsj)) {
                    //计算累计离线时间
                    zjsxsjDate = DateUtil.parse(zjsxsj,"yyyy-MM-dd HH").toJdkDate();
                    lastCheckDate = DateUtil.parse(lastCheckTime,"yyyy-MM-dd HH").toJdkDate();

                    ljlxsjLong = DateUtil.between(zjsxsjDate, lastCheckDate, DateUnit.HOUR);
                    if (ljlxsjLong > 0) {
                        if (ljlxsjLong >= 24) {
                            ljlxsj.append(ljlxsjLong / 24).append("天");
                        }
                        if (ljlxsjLong % 24 != 0) {
                            ljlxsj.append(ljlxsjLong % 24).append("小时");
                        }
                    }

                }
                //todo zjsxsj
                else {//无最近上线时间，说明一直离线，拿第一次巡检的时间计算离线时间
                    scjcsj = videoDao.getScjcsjBySn(params);
                    zjjcsj = videoDao.getZjjcsjBySn(params);

                    //计算累计离线时间
                    scjcsjDate = DateUtil.parse(scjcsj, "yyyy-MM-dd HH").toJdkDate();
                    zjjcsjDate = DateUtil.parse(zjjcsj, "yyyy-MM-dd HH").toJdkDate();

                    ljlxsjLong = DateUtil.between(scjcsjDate, zjjcsjDate, DateUnit.HOUR);
                    if (ljlxsjLong > 0) {
                        if (ljlxsjLong >= 24) {
                            ljlxsj.append(ljlxsjLong / 24).append("天");
                        }
                        if (ljlxsjLong % 24 != 0) {
                            ljlxsj.append(ljlxsjLong % 24).append("小时");
                        }
                    }
                }


                record.put("zjsxsj", zjsxsj);
                record.put("ljlxsj", ljlxsj.toString());

            }
        }


        return pageResult;

    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryLastVideoDataByType(Map<String, Object> params) {

        String xmIds = getXmIdStrFromList((List<XmConfigEntity>) params.get("xmConfigList"));
        params.put("xmIds", MyUtils.formatSqlIn(xmIds));

        List<Map<String, Object>> list = jyVideoDao.queryLastVideoDataByType(params);
        list.stream().forEach(e -> {
            String pointNo = MapUtil.getStr(e, "pointNo");
            if (StrUtil.count(pointNo, "-") == 3) {//22-1-2-123435

                e.put("wz",StrUtil.subBefore(pointNo, "-", true));
                e.put("luh",StrUtil.subAfter(pointNo, "-", true));
            } else if (StrUtil.count(pointNo, "-") == 2) {//22-1-2

                e.put("wz", pointNo);
                e.put("luh", "");
            } else {
                e.put("wz", "");
                e.put("luh", "");
            }

            e.put("pointName", MapUtil.getStr(e, "wz") + MapUtil.getStr(e, "pointName"));
        });

        list.stream().filter(e -> "异常".equals(MapUtil.getStr(e, "xlzt"))).forEach(e -> {
            //获取最近一次在线时间
            String lastZxDate = jyVideoDao.getLastZxDate(e);
            e.put("zjzxsj", lastZxDate);
        });

        //String type = MapUtil.getStr(params, "type");
        //if(VideoConsts.TYPE_JY.equals(type)){
        //    list = list.stream()
        //            .sorted(Comparator.comparing(VideoServiceImpl::comparingByWz2))
        //            .sorted(Comparator.comparing(VideoServiceImpl::comparingByWz1))
        //            .collect(Collectors.toList());
        //}
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataSource("slave2")
    public void addVideoForm(List<JyVideoEntity> list) {

        //判断当天是否添加过
        String dateStr = DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT);


        list.stream().forEach(e->{
            JyVideoEntity exsited = jyVideoDao.selectOne(new QueryWrapper<JyVideoEntity>().eq("XMID", e.getXmid()).eq("CONVERT(varchar(100), datetime1, 23)", dateStr));
            if (exsited != null) {
                throw new RRException("当天已有巡检记录");
            }
            jyVideoDao.insert(e);
        });

    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryVideoStatisticsByTypeAndDate(Map<String, Object> params) {
        String type = String.valueOf(params.get("type"));
        List<XmConfigEntity> xmConfigList = (List<XmConfigEntity>) params.get("xmConfigList");
        String xmIds = getXmIdStrFromList(xmConfigList);
        params.put("xmIds", MyUtils.formatSqlIn(xmIds));

        List<Map<String, Object>> list = new ArrayList<>();

        if (VideoConsts.TYPE_ZHZX.equals(type) || VideoConsts.TYPE_RL.equals(type)) {
            List<Map<String, Object>> templist = videoDao.queryVideoStatisticsByTypeAndDate(params);


            for (XmConfigEntity xmConfig : xmConfigList) {
                Map<String, Object> map = new HashMap<>();
                int total = 0;
                int zxs = 0;
                for (Map<String, Object> objectMap : templist) {
                    //因为xmConfig中的xmid可能存在多个xmid，所以这里用contains判断
                    if (xmConfig.getXmid().contains(MapUtil.getStr(objectMap, "xmid"))) {
                        total += Integer.parseInt(String.valueOf(objectMap.get("total")));
                        zxs += Integer.parseInt(String.valueOf(objectMap.get("zxs")));
                    }
                }
                map.put("dept", xmConfig.getXmmc());
                map.put("total", total);
                map.put("zxs", zxs);
                double pjzxl = 0;
                if (total != 0) {
                    pjzxl = NumberUtil.div(zxs, total, 4);
                }
                map.put("zxl", pjzxl);

                list.add(map);
            }

            //获取巡检次数  计算总和 平均在线率
            list.stream().forEach(e -> {
                int xjcs = videoDao.queryVideoXJCSByDateAndDept(params);
                e.put("xjcs", xjcs);
            });

        } else if (VideoConsts.TYPE_PCSZJ.equals(type) || VideoConsts.TYPE_JJ.equals(type)) {
            List<Map<String, Object>> templist = videoDao.queryVideoStatisticsByTypeAndDateOld(params);

            //因为统计一段时间内的数据，同一所属单位在库里会存在不一样的关系 比如 高清项目 (118/145) / 高清项目 (124/145)
            //这边在代码里面截取空格之前的，并合并
            Set<String> deptNameSet = new LinkedHashSet<>();

            templist.stream().forEach(e -> {
                e.put("dept",StrUtil.subBefore(String.valueOf(e.get("所属单位")), " ", true));
                deptNameSet.add(String.valueOf(e.get("dept")));
            });

            for (String e : deptNameSet) {
                Map<String, Object> map = new HashMap<>();
                int total = 0;
                int zxs = 0;
                for (Map<String, Object> objectMap : templist) {
                    if(e.equals(objectMap.get("dept"))){
                        total += Integer.parseInt(String.valueOf(objectMap.get("total")));
                        zxs += Integer.parseInt(String.valueOf(objectMap.get("zxs")));
                    }
                }
                map.put("dept", e);
                map.put("total", total);
                map.put("zxs", zxs);
                double pjzxl = 0;
                if (total != 0) {
                    pjzxl = NumberUtil.div(zxs, total, 4);
                }
                map.put("zxl", pjzxl);

                list.add(map);
            }

            //获取巡检次数  计算总和 平均在线率
            list.stream().forEach(e -> {
                params.put("dept", String.valueOf(e.get("dept")));
                int xjcs = videoDao.queryVideoXJCSByDateAndDeptOld(params);
                e.put("xjcs", xjcs);
            });
        } else {
            list = jyVideoDao.queryVideoStatisticsByTypeAndDate(params);

            list.forEach(e -> {
                int total = Integer.parseInt(String.valueOf(e.get("total")));
                int zxs = Integer.parseInt(String.valueOf(e.get("zxs")));
                double pjzxl = 0;
                if (total != 0) {
                    pjzxl = NumberUtil.div(zxs, total, 4);
                }
                e.put("zxl", pjzxl);
            });

        }

        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Object> totalMap = new HashMap<>();
            totalMap.put("dept", "总计");
            totalMap.put("xjcs", list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("xjcs")))).sum());
            totalMap.put("total", list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("total")))).sum());
            totalMap.put("zxs", list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("zxs")))).sum());
            totalMap.put("zxl", NumberUtil.div(list.stream().mapToDouble(e -> Double.parseDouble(String.valueOf(e.get("zxl")))).average().getAsDouble(), 1, 4));

            list.add(totalMap);
        }

        return list;
    }

    /*
    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryVideoStatisticsByTypeAndDate(Map<String, Object> params) {
        String type = String.valueOf(params.get("type"));

        List<Map<String, Object>> list = new ArrayList<>();

        if (VideoConsts.TYPE_ZHZX.equals(type) || VideoConsts.TYPE_PCSZJ.equals(type) || VideoConsts.TYPE_JJ.equals(type)) {
            List<Map<String, Object>> templist = videoDao.queryVideoStatisticsByTypeAndDate(params);

            //因为统计一段时间内的数据，同一所属单位在库里会存在不一样的关系 比如 高清项目 (118/145) / 高清项目 (124/145)
            //这边在代码里面截取空格之前的，并合并
            Set<String> deptNameSet = new LinkedHashSet<>();

            templist.stream().forEach(e -> {
                e.put("dept",StrUtil.subBefore(String.valueOf(e.get("所属单位")), " ", true));
                deptNameSet.add(String.valueOf(e.get("dept")));
            });

            for (String e : deptNameSet) {
                Map<String, Object> map = new HashMap<>();
                int total = 0;
                int zxs = 0;
                for (Map<String, Object> objectMap : templist) {
                    if(e.equals(objectMap.get("dept"))){
                        total += Integer.parseInt(String.valueOf(objectMap.get("total")));
                        zxs += Integer.parseInt(String.valueOf(objectMap.get("zxs")));
                    }
                }
                map.put("dept", e);
                map.put("total", total);
                map.put("zxs", zxs);
                double pjzxl = 0;
                if (total != 0) {
                    pjzxl = NumberUtil.div(zxs, total, 4);
                }
                map.put("zxl", pjzxl);

                list.add(map);
            }

            //获取巡检次数  计算总和 平均在线率
            list.stream().forEach(e -> {
                params.put("dept", String.valueOf(e.get("dept")));
                int xjcs = videoDao.queryVideoXJCSByDateAndDept(params);
                e.put("xjcs", xjcs);
            });

        } else {
            list = jyVideoDao.queryVideoStatisticsByTypeAndDate(params);

            list.forEach(e -> {
                int total = Integer.parseInt(String.valueOf(e.get("total")));
                int zxs = Integer.parseInt(String.valueOf(e.get("zxs")));
                double pjzxl = 0;
                if (total != 0) {
                    pjzxl = NumberUtil.div(zxs, total, 4);
                }
                e.put("zxl", pjzxl);
            });

        }

        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Object> totalMap = new HashMap<>();
            totalMap.put("dept", "总计");
            totalMap.put("xjcs", list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("xjcs")))).sum());
            totalMap.put("total", list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("total")))).sum());
            totalMap.put("zxs", list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("zxs")))).sum());
            totalMap.put("zxl", NumberUtil.div(list.stream().mapToDouble(e -> Double.parseDouble(String.valueOf(e.get("zxl")))).average().getAsDouble(), 1, 4));

            list.add(totalMap);
        }

        return list;
    }
    */

    @Override
    @DataSource("slave2")
    public List<String> queryLastVideoDateByType(Map<String, Object> params) {
        List<String> resultMapList = new ArrayList<>();
        String type = MapUtil.getStr(params, "type");
        if (VideoConsts.TYPE_ZHZX.equals(type) || VideoConsts.TYPE_PCSZJ.equals(type) || VideoConsts.TYPE_JJ.equals(type)) {
            resultMapList = videoDao.queryLastVideoDateByType(params);
        } else if(VideoConsts.TYPE_RL.equals(type)) {
            resultMapList = videoDao.queryLastRLVideoDate(params);
        } else {
            resultMapList = jyVideoDao.queryLastVideoDateByType(params);
        }
        return resultMapList;
    }

    @Override
    public void updateVideoBz(Map<String, Object> params) {

        //判断该xjid有无备注信息，没有就新增，有就更新
        //List<DwbzEntity> entityList = dwbzDao.selectList(new QueryWrapper<DwbzEntity>().eq("xjid", MapUtil.getStr(params, "xjid")));
        //
        //DwbzEntity dwbzEntity = new DwbzEntity();
        //dwbzEntity.setBh(MapUtil.getStr(params,"bh"));
        //dwbzEntity.setBz(MapUtil.getStr(params,"bz"));
        //dwbzEntity.setXjid(MapUtil.getStr(params,"xjid"));
        //dwbzEntity.setUserId(MapUtil.getStr(params,"userId"));
        //
        //if (CollectionUtil.isEmpty(entityList)) {
        //    dwbzDao.insert(dwbzEntity);
        //} else {
        //    dwbzDao.update(dwbzEntity, new QueryWrapper<DwbzEntity>().eq("xjid", dwbzEntity.getXjid()));
        //}
        DwbzEntity dwbzEntity = new DwbzEntity();
        dwbzEntity.setBh(MapUtil.getStr(params,"bh"));
        dwbzEntity.setBz(MapUtil.getStr(params,"bz"));
        dwbzEntity.setUserId(MapUtil.getStr(params,"userId"));
        dwbzEntity.setType(MapUtil.getStr(params,"type"));
        dwbzEntity.setCategory(MapUtil.getStr(params,"category"));
        dwbzEntity.setState(MapUtil.getInt(params, "state") == null ? 0 : MapUtil.getInt(params, "state"));
        dwbzEntity.setIp(MapUtil.getStr(params, "ip"));
        dwbzEntity.setDwortd(MapUtil.getInt(params, "dwortd") == null ? 0 : MapUtil.getInt(params, "dwortd"));
        dwbzEntity.setDh(MapUtil.getStr(params,"dh"));

        dwbzDao.insert(dwbzEntity);

    }

    @Override
    @DataSource("slave2")
    public VideoEntity getById(Map<String, Object> params){
        return videoDao.selectById(String.valueOf(params.get("id")));
    }

    @Override
    public IPage<DwbzEntity> queryVideoBzDataPage(Map<String, Object> params) {
        IPage<DwbzEntity> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);

        String type = String.valueOf(params.get("type"));

        pageResult = videoDao.queryVideoBzDataPage(pageParams, params);
        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public String getPoliceNameByPoliceCode(String userId) {
        return videoDao.getPoliceNameByPoliceCode(userId);
    }

    @Override
    public DwbzEntity getLastBzByBh(String sn) {
        DwbzEntity dwbzEntity = null;
        List<DwbzEntity> dwbzEntityList = dwbzDao.selectList(new QueryWrapper<DwbzEntity>().eq("bh", sn).orderByDesc("create_time"));
        if (CollectionUtil.isNotEmpty(dwbzEntityList)) {
            dwbzEntity = dwbzEntityList.get(0);
        }
        return dwbzEntity;
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryCompareDataPage(Map<String, Object> params) {
        List<Map<String, Object>> list = new ArrayList<>();
        //先根据国标查,查出国标编码不一致的
        List<Map<String, Object>> snList = videoDao.queryCompareDataBySnPage(params);
        list = snList.stream().filter(e->StrUtil.isEmpty(MapUtil.getStr(e,"bzs1"))).collect(Collectors.toList());
        snList = null;
        list.stream().forEach(e->{
            e.put("bz", "国标码不匹配");
        });


        return list;

    }

    @Override
    @DataSource("slave2")
    public List<Map<String,Object>> queryErrorVideoDataDetailByConfigId(Map<String, Object> params) {

        //根据configId获取所有xmid
        XmConfigEntity xmConfig = (XmConfigEntity) params.get("xmConfig");

        List<Map<String,Object>> list = null;
        List<Map<String, Object>> resultList = new ArrayList<>();


        params.put("xmIds", MyUtils.formatSqlIn(xmConfig.getXmid()));
        list = videoDao.queryErrorVideoDataDetailByConfigId(params);

        int jkzs = 0;
        int gzjks = 0;
        String zjsxsj = null;
        String zjjcsj = null;
        String scjcsj = null;
        StringBuffer ljlxsj = null;
        long ljlxsjLong = 0;
        Date zjsxsjDate = null;
        Date zjjcsjDate = null;
        Date scjcsjDate = null;
        int xjId = 0;

        for (Map<String, Object> record : list) {
            ljlxsj = new StringBuffer();
            jkzs = MapUtil.getInt(record, "jkzs");
            gzjks = MapUtil.getInt(record, "gzjks");

            if (jkzs == gzjks) {
                record.put("gztc", "点位故障");
            }else {
                record.put("gztc", "相机故障");
            }

            params.put("dwid", MapUtil.getStr(record, "dwid"));
            params.put("jsZjsx", 0);
            zjjcsj = videoDao.getZjsxByDwid(params);
            params.put("jsZjsx", 1);
            params.put("jkzs", jkzs);
            zjsxsj = videoDao.getZjsxAndJkzsByDwid(params);


            //获取出一条改点位检测失败的检测记录表id，用于备注时关联
            xjId = videoDao.getAnyOneErrorXjData(params);

            if (StrUtil.isNotEmpty(zjsxsj)) {

                //计算累计离线时间
                zjsxsjDate = DateUtil.parse(zjsxsj, "yyyy-MM-dd HH").toJdkDate();
                zjjcsjDate = DateUtil.parse(zjjcsj, "yyyy-MM-dd HH").toJdkDate();

                ljlxsjLong = DateUtil.between(zjsxsjDate, zjjcsjDate, DateUnit.HOUR);
                if (ljlxsjLong > 0) {
                    if (ljlxsjLong >= 24) {
                        ljlxsj.append(ljlxsjLong / 24).append("天");
                    }
                    if (ljlxsjLong % 24 != 0) {
                        ljlxsj.append(ljlxsjLong % 24).append("小时");
                    }
                }

            } else {//无最近上线时间，说明一直离线，拿第一次巡检的时间计算离线时间
                scjcsj = videoDao.getScjcsjByDwid(params);

                //计算累计离线时间
                scjcsjDate = DateUtil.parse(scjcsj, "yyyy-MM-dd HH").toJdkDate();
                zjjcsjDate = DateUtil.parse(zjjcsj, "yyyy-MM-dd HH").toJdkDate();

                ljlxsjLong = DateUtil.between(scjcsjDate, zjjcsjDate, DateUnit.HOUR);
                if (ljlxsjLong > 0) {
                    if (ljlxsjLong >= 24) {
                        ljlxsj.append(ljlxsjLong / 24).append("天");
                    }
                    if (ljlxsjLong % 24 != 0) {
                        ljlxsj.append(ljlxsjLong % 24).append("小时");
                    }
                }
            }


            record.put("xjId", xjId);
            record.put("zjsxsj", StrUtil.subBefore(zjsxsj, " ", false));
            record.put("ljlxsj", ljlxsj.toString());

        }


        return list;

    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String, Object>> queryNormalVideoDataDetailByConfigId(Map<String, Object> params) {
        //根据configId获取所有xmid
        XmConfigEntity xmConfig = (XmConfigEntity) params.get("xmConfig");

        IPage<Map<String, Object>> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<Map<String,Object>> pageParams = new Page(page,limit);
        params.put("xmIds", MyUtils.formatSqlIn(xmConfig.getXmid()));

        pageResult = videoDao.queryNormalVideoDataDetailByConfigIdPage(pageParams, params);
        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String, Object>> queryHtVideoDataDetailByConfigId(Map<String, Object> params) {
        //根据configId获取所有xmid
        XmConfigEntity xmConfig = (XmConfigEntity) params.get("xmConfig");

        IPage<Map<String, Object>> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<Map<String,Object>> pageParams = new Page(page,limit);
        params.put("xmIds", MyUtils.formatSqlIn(xmConfig.getXmid()));
        pageResult = videoDao.queryHtVideoDataDetailByConfigIdPage(pageParams, params);

        return pageResult;
    }

    @Override
    public DwbzEntity getBzByXjid(Integer id) {
        List<DwbzEntity> entityList = dwbzDao.selectList(new QueryWrapper<DwbzEntity>().eq("xjid", id));
        if (CollectionUtil.isEmpty(entityList)) {
            return new DwbzEntity();
        } else {
            return entityList.get(0);
        }
    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String, Object>> queryHistoryDataPage(Map<String, Object> params) {
        IPage<Map<String, Object>> pageResult = null;
        String dwid = MapUtil.getStr(params, "dwid");

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<Map<String,Object>> pageParams = new Page(page,limit);
        pageResult = videoDao.queryHistoryDataPage(pageParams, params);
        pageResult.getRecords().stream().forEach(e->{
            e.put("dwid", dwid);
            int jkzs = MapUtil.getInt(e, "jkzs");
            int gzjks = MapUtil.getInt(e, "gzjks");
            if (gzjks > 0) {
                if (gzjks == jkzs) {
                    e.put("zt", "点位故障");
                } else {
                    e.put("zt", "相机故障故障");
                }
            } else {
                e.put("zt", "正常");
            }

            if (jkzs <= 0) {
                e.put("zt", "未巡检");
            }

        });

        return pageResult;

    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryDwDetailByDwidAndDate(Map<String, Object> params) {
        return videoDao.queryDwDetailByDwidAndDate(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryTwLxData(Map<String, Object> params) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> list = videoDao.queryTwLxData(params);

        //for (Map<String, Object> e : list) {
        //    int jkzs = MapUtil.getInt(e, "jkzs");
        //    int gzjks = MapUtil.getInt(e, "gzjks");
        //    if (gzjks > 0) {
        //        e.put("zt", 1);
        //    } else {
        //        e.put("zt", 0);
        //    }
        //}

        Map<String, List<Map<String, Object>>> dwbhGroupList = list.stream().collect(Collectors.groupingBy(e -> MapUtil.getStr(e, "dwbh"), LinkedHashMap::new, Collectors.toList()));

        int ljlx = 0;//累计离线天数
        int jkzs = 0;//监控数
        int gzjks = 0;//故障监控数
        String date = null;//当前记录的时间
        String lastDate = null;//上一次记录的时间
        String beginDate = null;//离线开始
        String endDate = null;//离线结束
        Map<String, Object> resultMap = null;

        for (Map.Entry<String, List<Map<String, Object>>> entry : dwbhGroupList.entrySet()) {

            ljlx = 0;
            jkzs = 0;//监控数
            gzjks = 0;
            date = null;
            lastDate = null;
            beginDate = null;
            endDate = null;
            //resultMap = new HashMap<>();

            for (Map<String, Object> map : entry.getValue()) {

                resultMap = new HashMap<>();
                jkzs = MapUtil.getInt(map, "jkzs");
                gzjks = MapUtil.getInt(map, "gzjks");
                date = MapUtil.getStr(map, "date");

                if (gzjks > 0) {//当前点位异常
                    //判断lastDate=null,是则是第一次，ljlx+1
                    if (StrUtil.isEmpty(lastDate)) {
                        ljlx++;
                        lastDate = date;
                        beginDate = date;
                        endDate = date;
                        continue;
                    }

                    //不是，则计算date和lastDate间隔天数，ljlx+间隔天数
                    ljlx += DateUtil.between(DateUtil.parseDate(date).toJdkDate(), DateUtil.parseDate(lastDate).toJdkDate(), DateUnit.DAY, true);
                    lastDate = date;
                    endDate = date;

                } else {//当前点位正常

                    if (ljlx > 4) {
                        resultMap.put("dwbh", entry.getKey());
                        resultMap.put("ljlx", ljlx);
                        resultMap.put("jkzs", jkzs);
                        resultMap.put("gzjks", gzjks);
                        resultMap.put("beginDate", beginDate);
                        resultMap.put("endDate", endDate);
                        resultList.add(resultMap);
                    }

                    ljlx = 0;
                    lastDate = null;
                    beginDate = null;
                    endDate = null;

                }
            }

            //一直没有正常的点位也要放进去
            if (ljlx > 4) {
                resultMap.put("dwbh", entry.getKey());
                resultMap.put("ljlx", ljlx);
                resultMap.put("jkzs", jkzs);
                resultMap.put("gzjks", gzjks);
                resultMap.put("beginDate", beginDate);
                resultMap.put("endDate", endDate);
                resultList.add(resultMap);
            }

        }


        return resultList;
    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String, Object>> queryVideoBzDataByXMIDPage(Map<String, Object> params) {

        IPage<Map<String, Object>> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<Map<String,Object>> pageParams = new Page(page,limit);


        pageResult = videoDao.queryVideoBzDataByXMIDPage(pageParams, params);
        return pageResult;

    }

    @Override
    public IPage<DwbzEntity> queryBxDataPage(Map<String, Object> params) {

        IPage<DwbzEntity> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Page<DwbzEntity> pageParams = new Page(page,limit);
        pageResult = videoDao.queryBxDataPage(pageParams, params);

        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryGZDWData(Map<String, Object> params) {

        return videoDao.queryGZDWData(params);
    }

    @Override
    @DataSource("slave2")
    public String queryGZDWLastUpData(Map<String, Object> params) {
        return videoDao.queryGZDWLastUpData(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryGZTDData(Map<String, Object> params) {
        return videoDao.queryGZTDData(params);
    }

    @Override
    @DataSource("slave2")
    public String queryGZTDastUpData(Map<String, Object> params) {
        return videoDao.queryGZTDastUpData(params);
    }

    @Override
    @DataSource("slave2")
    public String getNameBySn(String sn) {
        return videoDao.getNameBySn(sn);
    }

    @Override
    @DataSource("slave2")
    public IPage<Map<String, Object>> queryDwOrTd(Map<String, Object> params) {
        IPage<Map<String, Object>> pageResult = null;

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        //获取巡检表最新一次的巡检时间
        String xjsj = videoDao.getLastXjsj();
        params.put("xjsj", xjsj);

        Page<Map<String, Object>> pageParams = new Page(page,limit);
        pageResult = videoDao.queryDwOrTd(pageParams, params);

        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryAllCompareXm(Map<String, Object> params) {
        return videoDao.queryAllCompareXm(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> queryDwVideoDataDetailByConfigId(Map<String, Object> params) {
        //根据configId获取所有xmid
        XmConfigEntity xmConfig = (XmConfigEntity) params.get("xmConfig");

        List<Map<String,Object>> list = null;
        List<Map<String, Object>> resultList = new ArrayList<>();


        params.put("xmIds", MyUtils.formatSqlIn(xmConfig.getXmid()));
        list = videoDao.queryDwVideoDataDetailByConfigId(params);

        int jkzs = 0;
        int zcjks = 0;
        Integer xjId = null;

        for (Map<String, Object> record : list) {
            jkzs = MapUtil.getInt(record, "jkzs");
            zcjks = MapUtil.getInt(record, "zcjks");

            if (zcjks == 0) {
                record.put("dwzt", "离线");
            } else {
                record.put("dwzt", "在线");
            }

            params.put("dwid", MapUtil.getStr(record, "dwid"));

            //获取出一条改点位检测失败的检测记录表id，用于备注时关联
            xjId = videoDao.getAnyOneErrorXjData(params);

            record.put("xjId", xjId);

        }

        return list;
    }


    /*
    private static int comparingByWz1(Map<String, Object> map){
        String wzStr = MapUtil.getStr(map, "wz");
        int order = Integer.parseInt(StrUtil.subAfter(StrUtil.subBefore(wzStr, "-", true),"-",true));
        return order;
    }

    private static int comparingByWz2(Map<String, Object> map){
        String wzStr = MapUtil.getStr(map, "wz");
        int order = Integer.parseInt(StrUtil.subAfter(wzStr, "-", true));
        return order;
    }
    */

    private static int comparingByDw(Map<String, Object> map){
        String wzStr = MapUtil.getStr(map, "所属单位");
        int order = Integer.parseInt(StrUtil.subAfter(wzStr, "-", true));
        return order;
    }

    private String getXmIdStrFromList(List<XmConfigEntity> xmConfigList) {
        StringBuffer sb = new StringBuffer();
        String xmIds = "";
        xmConfigList.stream().forEach(e->{
            sb.append(e.getXmid()).append(",");
        });
        if (sb.toString().endsWith(",")) {
            xmIds = StrUtil.subBefore(sb.toString(), ",", true);
        } else {
            xmIds = sb.toString();
        }
        return xmIds;
    }
}
