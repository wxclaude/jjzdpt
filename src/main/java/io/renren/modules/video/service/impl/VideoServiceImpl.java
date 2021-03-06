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

            list.stream().filter(e -> "??????".equals(MapUtil.getStr(e, "xlzt"))).forEach(e -> {
                //??????????????????????????????
                String lastZxDate = jyVideoDao.getLastZxDate(e);
                e.put("zjzxsj", lastZxDate);
            });


        } else if (VideoConsts.TYPE_ZHZX.equals(type)) {//????????????

            List<Map<String, Object>> detailList = videoDao.queryVideoDataDetailByDateNew(params);
            xmConfigList.stream().forEach(e->{
                detailList.forEach(d->{
                    //??????xmConfig??????xmid??????????????????xmid??????????????????contains??????
                    if (e.getXmid().contains(MapUtil.getStr(d, "xmid"))) {
                        e.getXjList().add(d);
                    }
                });
            });

            Map<String,Object> resultMap = null;
            int htdws = 0;//???????????????
            int xjdws = 0;//???????????????
            int xjdwzxs = 0;//????????????????????? ?????????????????????????????????????????????
            int xjjks = 0;//???????????????
            int xjjkzxs = 0;//?????????????????????
            int xjdwgzs = 0;//??????????????? ????????????????????????????????????????????????

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
                xjjkzxs = (int) xmConfig.getXjList().stream().filter(e -> "??????".equals(MapUtil.getStr(e, "??????"))).count();
                xjdws = xmConfig.getXjList().stream().collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "???????????????"), 0, 4))).size();
                xjdwzxs = xmConfig.getXjList().stream().filter(e -> "??????".equals(MapUtil.getStr(e, "??????"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "???????????????"), 0, 4))).size();
                xjdwgzs = xmConfig.getXjList().stream().filter(e -> !"??????".equals(MapUtil.getStr(e, "??????"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "???????????????"), 0, 4))).size();

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
        else if (VideoConsts.TYPE_ZHZX.equals(type)) {//????????????
            list = new ArrayList<>();
            List<Map<String, Object>> detailList = videoDao.queryVideoDataDetailByDateNew(params);

            //total,
            //isnull(SUM(CASE WHEN ??????= '??????' THEN 1 ELSE 0 END),0) AS 'zxs',
            //isnull(SUM(CASE WHEN ??????!='??????' THEN 1 ELSE 0 END),0) AS 'lxs'
            Map<String,Object> resultMap = null;
            int dws = 0;//?????????
            int dwzxs = 0;//??????????????? ?????????????????????????????????????????????
            int jks = 0;//?????????
            int zxs = 0;//?????????
            int dwgzs = 0;//??????????????? ????????????????????????????????????????????????
            if (CollectionUtil.isNotEmpty(detailList)) {

                //????????????????????????
                Map<Object, List<Map<String, Object>>> listMap = detailList.stream().collect(Collectors.groupingBy(e -> e.get("????????????")));

                for (Map.Entry<Object, List<Map<String, Object>>> entry : listMap.entrySet()) {
                    resultMap = new HashMap<>();
                    dws = 0;
                    dwzxs = 0;
                    jks = 0;
                    zxs = 0;
                    dwgzs = 0;

                    jks = entry.getValue().size();
                    zxs = (int) entry.getValue().stream().filter(e -> "??????".equals(MapUtil.getStr(e, "??????"))).count();
                    dws = entry.getValue().stream().collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "???????????????"), 0, 4))).size();
                    dwzxs = entry.getValue().stream().filter(e -> "??????".equals(MapUtil.getStr(e, "??????"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "???????????????"), 0, 4))).size();
                    dwgzs = entry.getValue().stream().filter(e -> !"??????".equals(MapUtil.getStr(e, "??????"))).collect(Collectors.groupingBy(e -> StrUtil.subWithLength(MapUtil.getStr(e, "???????????????"), 0, 4))).size();

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
        else if (VideoConsts.TYPE_RL.equals(type)) {//??????

            list = videoDao.queryRLVideoDataByDate(params);
            list.stream().forEach(e -> {
                e.put("dept", "??????");
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
                e.put("dept", StrUtil.subBefore(String.valueOf(e.get("????????????")), " ", true));
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
            //?????????????????????????????????????????????????????????
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
                sn = MapUtil.getStr(record, "?????????SN");
                lastCheckTime = MapUtil.getStr(record, "??????????????????");
                zjsxsj = "";
                ljlxsj = new StringBuffer();
                ljlxsjLong = 0;
                zjsxsjDate = null;
                lastCheckDate = null;

                params.put("sn", sn);
                params.put("lastCheckTime", lastCheckTime);

                zjsxsj = videoDao.getZjsxBySn(params);

                if (StrUtil.isNotEmpty(zjsxsj)) {
                    //????????????????????????
                    zjsxsjDate = DateUtil.parse(zjsxsj,"yyyy-MM-dd HH").toJdkDate();
                    lastCheckDate = DateUtil.parse(lastCheckTime,"yyyy-MM-dd HH").toJdkDate();

                    ljlxsjLong = DateUtil.between(zjsxsjDate, lastCheckDate, DateUnit.HOUR);
                    if (ljlxsjLong > 0) {
                        if (ljlxsjLong >= 24) {
                            ljlxsj.append(ljlxsjLong / 24).append("???");
                        }
                        if (ljlxsjLong % 24 != 0) {
                            ljlxsj.append(ljlxsjLong % 24).append("??????");
                        }
                    }

                }
                //todo zjsxsj
                else {//??????????????????????????????????????????????????????????????????????????????????????????
                    scjcsj = videoDao.getScjcsjBySn(params);
                    zjjcsj = videoDao.getZjjcsjBySn(params);

                    //????????????????????????
                    scjcsjDate = DateUtil.parse(scjcsj, "yyyy-MM-dd HH").toJdkDate();
                    zjjcsjDate = DateUtil.parse(zjjcsj, "yyyy-MM-dd HH").toJdkDate();

                    ljlxsjLong = DateUtil.between(scjcsjDate, zjjcsjDate, DateUnit.HOUR);
                    if (ljlxsjLong > 0) {
                        if (ljlxsjLong >= 24) {
                            ljlxsj.append(ljlxsjLong / 24).append("???");
                        }
                        if (ljlxsjLong % 24 != 0) {
                            ljlxsj.append(ljlxsjLong % 24).append("??????");
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

        list.stream().filter(e -> "??????".equals(MapUtil.getStr(e, "xlzt"))).forEach(e -> {
            //??????????????????????????????
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

        //???????????????????????????
        String dateStr = DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT);


        list.stream().forEach(e->{
            JyVideoEntity exsited = jyVideoDao.selectOne(new QueryWrapper<JyVideoEntity>().eq("XMID", e.getXmid()).eq("CONVERT(varchar(100), datetime1, 23)", dateStr));
            if (exsited != null) {
                throw new RRException("????????????????????????");
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
                    //??????xmConfig??????xmid??????????????????xmid??????????????????contains??????
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

            //??????????????????  ???????????? ???????????????
            list.stream().forEach(e -> {
                int xjcs = videoDao.queryVideoXJCSByDateAndDept(params);
                e.put("xjcs", xjcs);
            });

        } else if (VideoConsts.TYPE_PCSZJ.equals(type) || VideoConsts.TYPE_JJ.equals(type)) {
            List<Map<String, Object>> templist = videoDao.queryVideoStatisticsByTypeAndDateOld(params);

            //????????????????????????????????????????????????????????????????????????????????????????????? ?????? ???????????? (118/145) / ???????????? (124/145)
            //??????????????????????????????????????????????????????
            Set<String> deptNameSet = new LinkedHashSet<>();

            templist.stream().forEach(e -> {
                e.put("dept",StrUtil.subBefore(String.valueOf(e.get("????????????")), " ", true));
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

            //??????????????????  ???????????? ???????????????
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
            totalMap.put("dept", "??????");
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

            //????????????????????????????????????????????????????????????????????????????????????????????? ?????? ???????????? (118/145) / ???????????? (124/145)
            //??????????????????????????????????????????????????????
            Set<String> deptNameSet = new LinkedHashSet<>();

            templist.stream().forEach(e -> {
                e.put("dept",StrUtil.subBefore(String.valueOf(e.get("????????????")), " ", true));
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

            //??????????????????  ???????????? ???????????????
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
            totalMap.put("dept", "??????");
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

        //?????????xjid???????????????????????????????????????????????????
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
        //??????????????????,??????????????????????????????
        List<Map<String, Object>> snList = videoDao.queryCompareDataBySnPage(params);
        list = snList.stream().filter(e->StrUtil.isEmpty(MapUtil.getStr(e,"bzs1"))).collect(Collectors.toList());
        snList = null;
        list.stream().forEach(e->{
            e.put("bz", "??????????????????");
        });


        return list;

    }

    @Override
    @DataSource("slave2")
    public List<Map<String,Object>> queryErrorVideoDataDetailByConfigId(Map<String, Object> params) {

        //??????configId????????????xmid
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
                record.put("gztc", "????????????");
            }else {
                record.put("gztc", "????????????");
            }

            params.put("dwid", MapUtil.getStr(record, "dwid"));
            params.put("jsZjsx", 0);
            zjjcsj = videoDao.getZjsxByDwid(params);
            params.put("jsZjsx", 1);
            params.put("jkzs", jkzs);
            zjsxsj = videoDao.getZjsxAndJkzsByDwid(params);


            //??????????????????????????????????????????????????????id????????????????????????
            xjId = videoDao.getAnyOneErrorXjData(params);

            if (StrUtil.isNotEmpty(zjsxsj)) {

                //????????????????????????
                zjsxsjDate = DateUtil.parse(zjsxsj, "yyyy-MM-dd HH").toJdkDate();
                zjjcsjDate = DateUtil.parse(zjjcsj, "yyyy-MM-dd HH").toJdkDate();

                ljlxsjLong = DateUtil.between(zjsxsjDate, zjjcsjDate, DateUnit.HOUR);
                if (ljlxsjLong > 0) {
                    if (ljlxsjLong >= 24) {
                        ljlxsj.append(ljlxsjLong / 24).append("???");
                    }
                    if (ljlxsjLong % 24 != 0) {
                        ljlxsj.append(ljlxsjLong % 24).append("??????");
                    }
                }

            } else {//??????????????????????????????????????????????????????????????????????????????????????????
                scjcsj = videoDao.getScjcsjByDwid(params);

                //????????????????????????
                scjcsjDate = DateUtil.parse(scjcsj, "yyyy-MM-dd HH").toJdkDate();
                zjjcsjDate = DateUtil.parse(zjjcsj, "yyyy-MM-dd HH").toJdkDate();

                ljlxsjLong = DateUtil.between(scjcsjDate, zjjcsjDate, DateUnit.HOUR);
                if (ljlxsjLong > 0) {
                    if (ljlxsjLong >= 24) {
                        ljlxsj.append(ljlxsjLong / 24).append("???");
                    }
                    if (ljlxsjLong % 24 != 0) {
                        ljlxsj.append(ljlxsjLong % 24).append("??????");
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
        //??????configId????????????xmid
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
        //??????configId????????????xmid
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
                    e.put("zt", "????????????");
                } else {
                    e.put("zt", "??????????????????");
                }
            } else {
                e.put("zt", "??????");
            }

            if (jkzs <= 0) {
                e.put("zt", "?????????");
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

        int ljlx = 0;//??????????????????
        int jkzs = 0;//?????????
        int gzjks = 0;//???????????????
        String date = null;//?????????????????????
        String lastDate = null;//????????????????????????
        String beginDate = null;//????????????
        String endDate = null;//????????????
        Map<String, Object> resultMap = null;

        for (Map.Entry<String, List<Map<String, Object>>> entry : dwbhGroupList.entrySet()) {

            ljlx = 0;
            jkzs = 0;//?????????
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

                if (gzjks > 0) {//??????????????????
                    //??????lastDate=null,?????????????????????ljlx+1
                    if (StrUtil.isEmpty(lastDate)) {
                        ljlx++;
                        lastDate = date;
                        beginDate = date;
                        endDate = date;
                        continue;
                    }

                    //??????????????????date???lastDate???????????????ljlx+????????????
                    ljlx += DateUtil.between(DateUtil.parseDate(date).toJdkDate(), DateUtil.parseDate(lastDate).toJdkDate(), DateUnit.DAY, true);
                    lastDate = date;
                    endDate = date;

                } else {//??????????????????

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

            //??????????????????????????????????????????
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

        //??????????????????????????????????????????
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
        //??????configId????????????xmid
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
                record.put("dwzt", "??????");
            } else {
                record.put("dwzt", "??????");
            }

            params.put("dwid", MapUtil.getStr(record, "dwid"));

            //??????????????????????????????????????????????????????id????????????????????????
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
        String wzStr = MapUtil.getStr(map, "????????????");
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
