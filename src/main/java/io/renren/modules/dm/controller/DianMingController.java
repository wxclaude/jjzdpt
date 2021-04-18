package io.renren.modules.dm.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.modules.dm.consts.DmConsts;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.service.DeptService;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.*;

@RestController
@RequestMapping("/dm")
public class DianMingController extends AbstractController {

    @Autowired
    private DmService dmService;
    @Autowired
    private DeptService deptService;

    @Autowired
    private SysOssService sysOssService;

    @ResponseBody
    @GetMapping("/queryZhiBanDataByDate")
    public R queryZhiBanDataByDate(String date,String deptId){
        logger.info("[queryZhiBanDataByDate] date={}, deptId={}", date, deptId);

        List<Map<String,Object>> zhiBanList = null;
        if(!"D0001".equals(deptId)){
            zhiBanList = dmService.queryZhiBanDataByDate(date,deptId);
            if (!StrUtil.contains(deptId, '-')) {
                //sqmj
                zhiBanList.addAll(dmService.queryQsmjByDeptId(deptId));
                zhiBanList = dmService.handerRepeatPolice(zhiBanList);
            }
        }else{
            zhiBanList = dmService.queryZhiBanZhangByDate(date);
        }

        StringBuffer infoSb = null;
        String info = null;
        for (Map<String, Object> map : zhiBanList) {
            infoSb = new StringBuffer();
            info = "";
            String policeCode = (String) map.get("policeCode");
            List<Map<String,Object>> dianMingDataList = dmService.getLastTwoDayDianMingData(policeCode);
            for (Map<String, Object> dmMap : dianMingDataList) {
                infoSb.append(dmMap.get("dmDate")).append("(");
                int yd = (int) dmMap.get("yd");
                if (yd == 1) {
                    infoSb.append("正常应答");
                } else if (yd == 2) {
                    infoSb.append("未应答");

                }else if (yd == 3) {
                    infoSb.append("其它设备应答");
                }
                infoSb.append("),");
            }
            if(StrUtil.isNotEmpty(infoSb)){
                info = infoSb.substring(0, infoSb.length() - 1);
            }
            map.put("info",info);
        }
        return R.ok().put("data", zhiBanList);
    }

    @ResponseBody
    @PostMapping("/addDMBatch")
    @SysLog("抽取值班点名人员")
    public R addDianMingBatch(@RequestBody List<DianMingEntity> dianMingEntityList){
        SysUserEntity user = getUser();
        Date date = new Date();

        logger.info("[addDianMingBatch] user={}, chouqutime={}, data={}", user.getName(), date, JSONUtil.toJsonStr(dianMingEntityList));


        for (DianMingEntity dianMingEntity : dianMingEntityList) {
            dianMingEntity.setChouquren(user.getName());
            dianMingEntity.setChouquTime(date);
            if (StrUtil.contains(dianMingEntity.getDeptId(), '-')) {
                dianMingEntity.setReportPointId(StrUtil.subAfter(dianMingEntity.getDeptId(), "-", true));
                dianMingEntity.setDeptId(StrUtil.subBefore(dianMingEntity.getDeptId(), "-", true));
            }
        }
        dmService.addDianMingBatch(dianMingEntityList);
        return R.ok();
    }

    @ResponseBody
    @PostMapping("/updateDMBatch")
    @SysLog("更新值班点名人员")
    public R updateDianMingBatch(@RequestBody List<DianMingEntity> dianMingEntityList, HttpServletRequest request){
        SysUserEntity user = getUser();
        Date date = new Date();
        String remoteAddr = request.getRemoteAddr();
        logger.info("[updateDianMingBatch] user={}, remoteAddr={}, data={}", user.getName(), remoteAddr, JSONUtil.toJsonStr(dianMingEntityList));


        DianMingEntity exsitedDianMingEntity = null;
        for (DianMingEntity dianMingEntity : dianMingEntityList) {
            //这里判断是否第一次更新，第一次更新才设置点名时间，点名人，ip
            exsitedDianMingEntity = dmService.getById(dianMingEntity.getId());
            if (exsitedDianMingEntity.getDmTime() == null) {
                dianMingEntity.setDmTime(date);
                dianMingEntity.setDmPolice(user.getName());
                dianMingEntity.setIp(remoteAddr);
            }
        }
        dmService.updateDianMingBatch(dianMingEntityList);
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/queryDmDataByCondition")
    public R queryDmDataByCondition(String chouquDate,String dmTime){
        logger.info("[queryDmDataByCondition] chouquDate={}, dmTime={}", chouquDate, dmTime);

        Map<String, Object> params = new HashMap<>();
        params.put("chouquDate", chouquDate);
        params.put("dmTime", dmTime);

        List<DianMingEntity> dmList = dmService.queryDmDataByCondition(params);
        return R.ok().put("data", dmList);
    }

    @ResponseBody
    @GetMapping("/deleteDM")
    @SysLog("删除值班点名抽取人员")
    public R deleteDM(String id){
        SysUserEntity user = getUser();

        logger.info("[deleteDM] id={}, userName={}", id, user.getName());

        dmService.removeById(id);
        return R.ok();
    }

    @ResponseBody
    @PostMapping("/deleteDMBatch")
    @SysLog("删除值班点名抽取人员")
    public R deleteDMBatch(@RequestBody List<DianMingEntity> dianMingEntityList){
        SysUserEntity user = getUser();

        logger.info("[deleteDMBatch] dianMingEntityList={}, userName={}", dianMingEntityList, user.getName());

        List<String> idList = new ArrayList<>();
        dianMingEntityList.forEach(e -> { idList.add(String.valueOf(e.getId())); });

        dmService.removeByIds(idList);
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/queryZhiBanAllByDate")
    public R queryZhiBanAllByDate(String date){
        logger.info("[queryZhiBanAllByDate] date={}", date);

        List<Map<String,Object>> zhiBanList = dmService.queryZhiBanAllByDate(date);
        zhiBanList.forEach(e -> {

            List<Map<String,Object>> list = dmService.queryQsmjByDeptId(String.valueOf(e.get("deptId")));
            StringBuffer sqPoliceSb = new StringBuffer();
            String sqPoliceStr = "";
            for (Map<String,Object> sqmjEntity : list) {
                sqPoliceSb.append(sqmjEntity.get("police")).append("(").append(sqmjEntity.get("djj") == null ? "" : sqmjEntity.get("djj")).append("),");
            }
            if(StrUtil.isNotEmpty(sqPoliceSb)){
                sqPoliceStr = sqPoliceSb.substring(0, sqPoliceSb.length() - 1);
            }
            e.put("sqPoliceStr", sqPoliceStr);
        });

        Map<String,Object> leaderMap = dmService.queryZhiBanLeaderByDate(date);

        return R.ok().put("data", zhiBanList).put("leaderData",leaderMap);
    }

    /**
     * 查询当日值班长
     * @param date
     * @return
     */
    @ResponseBody
    @GetMapping("/queryZhiBanZhangByDate")
    public R queryZhiBanZhangByDate(String date){
        logger.info("[queryZhiBanZhangByDate] date={}", date);

        List<Map<String,Object>> leaderList = dmService.queryZhiBanZhangByDate(date);

        return R.ok().put("data", leaderList);
    }


    @ResponseBody
    @GetMapping("/queryDMPage")
    public R queryDMPage(@RequestParam Map<String, Object> params){
        logger.info("[queryDMPage] params={}", params);
        Page page = dmService.queryDMPage(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    @GetMapping("/exportDMByDate")
    public void exportDMByDate(String dmTime, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportDMByDate] dmTime={}", dmTime);
        Map<String, Object> params = new HashMap<>();
        params.put("dmTime", dmTime);

        List<DianMingEntity> entityList = dmService.queryDmDataByCondition(params);
        Set<String> deptNameSet = new LinkedHashSet<>();
        Set<String> zd5DeptNameSet = new LinkedHashSet<>();
        Set<String> xtjDeptNameSet = new LinkedHashSet<>();
        Set<String> otherDeptNameSet = new LinkedHashSet<>();
        List<Map<String, Object>> pcs16ResultList = new ArrayList<>();
        List<Map<String, Object>> zd5ResultList = new ArrayList<>();
        List<Map<String, Object>> xtjResultList = new ArrayList<>();
        List<Map<String, Object>> otherResultList = new ArrayList<>();

        entityList.stream().filter(e -> DmConsts.pcs16DeptIdList.contains(e.getDeptId())).forEach(e -> {
            deptNameSet.add(e.getDeptName());
        });
        entityList.stream().filter(e -> DmConsts.zd5DeptIdList.contains(e.getDeptId())).forEach(e -> {
            zd5DeptNameSet.add(e.getDeptName());
        });
        entityList.stream().filter(e -> DmConsts.xtj3List.contains(e.getReportPointId())).forEach(e -> {
            xtjDeptNameSet.add(e.getDeptName());
        });
        entityList.stream().filter(e -> DmConsts.qjd2List.contains(e.getReportPointId())).forEach(e -> {
            xtjDeptNameSet.add(e.getDeptName());
        });
        entityList.stream().filter(e -> DmConsts.otherDeptList.contains(e.getDeptId())).forEach(e -> {
            otherDeptNameSet.add(e.getDeptName());
        });

        deptNameSet.forEach(e -> {
            Map<String,Object> map = new HashMap<>();
            map.put("deptName",e);
            pcs16ResultList.add(map);
        });

        zd5DeptNameSet.forEach(e -> {
            Map<String,Object> map = new HashMap<>();
            map.put("deptName",e);
            zd5ResultList.add(map);
        });

        xtjDeptNameSet.forEach(e -> {
            Map<String,Object> map = new HashMap<>();
            map.put("deptName",e);
            xtjResultList.add(map);
        });
        otherDeptNameSet.forEach(e -> {
            Map<String,Object> map = new HashMap<>();
            map.put("deptName",e);
            otherResultList.add(map);
        });


        entityList.stream().forEach(e -> {
            pcs16ResultList.forEach(r -> {
                String deptName = String.valueOf(r.get("deptName"));
                String yd = "";
                if (e.getYd() == 1) {
                    yd = "√";
                } else if (e.getYd() == 2) {
                    yd = "×";
                } else if (e.getYd() == 3) {
                    yd = "○";
                }

                if (e.getDeptName().equals(deptName)) {
                    if (e.getIsLeader() == DmConsts.TYPE_LEADER) {
                        r.put("police", e.getPolice());
                        r.put("yd", yd);
                    } else if (e.getIsLeader() == DmConsts.TYPE_ZBPOLICE) {
                        r.put("zbPolice", e.getPolice());
                        r.put("zbYd", yd);
                    } else if (e.getIsLeader() == DmConsts.TYPE_SQPOLICE) {
                        r.put("sqPolice", e.getPolice());
                        r.put("sqYd", yd);
                    }
                }
            });

            zd5ResultList.forEach(r -> {
                String deptName = String.valueOf(r.get("deptName"));
                if (e.getDeptName().equals(deptName)) {
                    String yd = "";
                    if (e.getYd() == 1) {
                        yd = "√";
                    } else if (e.getYd() == 2) {
                        yd = "×";
                    } else if (e.getYd() == 3) {
                        yd = "○";
                    }
                    r.put("police", e.getPolice());
                    r.put("yd", yd);
                }
            });

            Map<String, Object> newMap = new HashMap<>();
            xtjResultList.forEach(r -> {
                String deptName = String.valueOf(r.get("deptName"));
                if (e.getDeptName().equals(deptName)) {
                    String yd = "";
                    if (e.getYd() == 1) {
                        yd = "√";
                    } else if (e.getYd() == 2) {
                        yd = "×";
                    } else if (e.getYd() == 3) {
                        yd = "○";
                    }
                    if (r.get("police") == null) {
                        r.put("police", e.getPolice());
                        r.put("yd", yd);
                    } else {
                        newMap.put("deptName", r.get("deptName"));
                        newMap.put("police", e.getPolice());
                        newMap.put("yd", yd);
                        //xtjResultList.add(newMap);
                    }
                }
            });

            if (MapUtil.isNotEmpty(newMap)) {
                xtjResultList.add(newMap);
            }

            otherResultList.forEach(r -> {
                String deptName = String.valueOf(r.get("deptName"));
                if (e.getDeptName().equals(deptName)) {
                    String yd = "";
                    if (e.getYd() == 1) {
                        yd = "√";
                    } else if (e.getYd() == 2) {
                        yd = "×";
                    } else if (e.getYd() == 3) {
                        yd = "○";
                    }
                    r.put("police", e.getPolice());
                    r.put("yd", yd);
                }
            });

        });

        otherResultList.addAll(zd5ResultList);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/dm.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());

        Date d = DateUtil.parse(dmTime);
        int monthInt = DateUtil.month(d) + 1;
        String dmTimeFormat = monthInt + "月" + DateUtil.dayOfMonth(d) + "日" + DateUtil.hour(d, true) + "时" + DateUtil.minute(d) + "分";

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("pcs16List", pcs16ResultList);
        excelMap.put("zd5List", otherResultList);
        excelMap.put("xtjList", xtjResultList);
        excelMap.put("dmTimeFormat", dmTimeFormat);

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = dmTimeFormat +"点名结果.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }



    }

    @ResponseBody
    @GetMapping("/queryDMStatisticsByDatePage")
    public R queryDMByDatePage(@RequestParam Map<String, Object> params){
        logger.info("[queryDMByDatePage] params={}", params);
        IPage page = dmService.queryDMByDatePage(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    @GetMapping("/exportDMStatisticsByDate")
    public void exportDMStatisticsByDate(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportDMStatisticsByDate] params={}", params);
        String startDate = String.valueOf(params.get("startDate"));
        String endDate = String.valueOf(params.get("endDate"));
        params.put("page", 1);
        params.put("limit", 200);//这里设置200，假装不分页
        IPage page = dmService.queryDMByDatePage(params);

        List<Map<String, Object>> records = page.getRecords();


        //添加总计 邗江分局
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("deptName", "邗江分局");
        totalMap.put("total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("total")))).sum());
        totalMap.put("yd1Total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("yd1Total")))).sum());
        totalMap.put("yd2Total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("yd2Total")))).sum());
        totalMap.put("yd3Total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("yd3Total")))).sum());
        totalMap.put("yd1totalPrecent", records.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("yd1totalPrecent")))).average().getAsDouble());
        totalMap.put("yd2totalPrecent", records.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("yd2totalPrecent")))).average().getAsDouble());
        totalMap.put("yd3totalPrecent", records.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("yd3totalPrecent")))).average().getAsDouble());
        totalMap.put("yd4totalPrecent", records.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("yd4totalPrecent")))).average().getAsDouble());

        records.add(totalMap);

        //处理百分数
        for (Map<String, Object> record : records) {
            String  yd1totalPrecentStr = String.valueOf(record.get("yd1totalPrecent"));
            String  yd2totalPrecentStr = String.valueOf(record.get("yd2totalPrecent"));
            String  yd3totalPrecentStr = String.valueOf(record.get("yd3totalPrecent"));
            String  yd4totalPrecentStr = String.valueOf(record.get("yd4totalPrecent"));
            if (StrUtil.isNotEmpty(yd1totalPrecentStr)) {
                record.put("yd1totalPrecent", NumberUtil.mul(Double.parseDouble(yd1totalPrecentStr),100) + "%");
            }
            if (StrUtil.isNotEmpty(yd2totalPrecentStr)) {
                record.put("yd2totalPrecent", NumberUtil.mul(Double.parseDouble(yd2totalPrecentStr),100) + "%");
            }
            if (StrUtil.isNotEmpty(yd3totalPrecentStr)) {
                record.put("yd3totalPrecent", NumberUtil.mul(Double.parseDouble(yd3totalPrecentStr),100) + "%");
            }
            if (StrUtil.isNotEmpty(yd4totalPrecentStr)) {
                record.put("yd4totalPrecent", NumberUtil.mul(Double.parseDouble(yd4totalPrecentStr),100) + "%");
            }
        }

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/dmStatistics.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("mapList", records);

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = startDate + "_" + endDate + "统计情况.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }


    @ResponseBody
    @GetMapping("/randomDm")
    @SysLog("随即点名")
    public R randomDm(int leaderCount, int policeCount, int sqPoliceCount, String date) {
        logger.info("[randomDm] leaderCount={}, police={}", leaderCount, policeCount);
        List<DianMingEntity> dianMingList = new ArrayList<>();

        //16个派出所
        List<DianMingEntity> pcs16List = this.randomPickPcs16(leaderCount, policeCount, sqPoliceCount, date);

        //5个中队只选一个
        List<DianMingEntity> zd5List = this.randomPickZd5(date);

        //巡特警邗巡逻3个各选一个
        List<DianMingEntity> xtj3List = this.randomPickXtj3(date);

        //骑警队随即选2个
        List<DianMingEntity> qjd3List = this.randomPickQjd2(date, 2);


        dianMingList.addAll(pcs16List);
        dianMingList.addAll(zd5List);
        dianMingList.addAll(xtj3List);
        dianMingList.addAll(qjd3List);

        SysUserEntity user = getUser();
        Date now = new Date();

        for (DianMingEntity temp : dianMingList) {
            temp.setChouquren(user.getName());
            temp.setChouquTime(now);
        }

        dmService.addDianMingBatch(dianMingList);
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/queryDMReportDate")
    public R queryDMReportDate(@RequestParam Map<String, Object> params){
        logger.info("[queryDMReportDate] params={}", params);

        //先默认去当前月
        params.put("startDate", DateUtil.format(DateUtil.beginOfMonth(new Date()).toJdkDate(), DatePattern.NORM_DATE_FORMAT));
        params.put("endDate", DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));

        params.put("page", 1);
        params.put("limit", 100);//mock page

        List<Map<String,Object>> list= dmService.queryDMByDatePage(params).getRecords();

        //根据应答率倒序排序
        Collections.sort(list,new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double yd1totalPrecent1 = Double.parseDouble(String.valueOf(o1.get("yd1totalPrecent")));
                Double yd1totalPrecent2 = Double.parseDouble(String.valueOf(o2.get("yd1totalPrecent")));

                return yd1totalPrecent2.compareTo(yd1totalPrecent1);
            }
        });

        List<String> xAxisList = new ArrayList<>();
        List<Integer> seriesList = new ArrayList<>();
        list.stream().forEach(e -> {
            xAxisList.add(String.valueOf(e.get("shortDeptName")));
            double yd1totalPrecent = Double.parseDouble(String.valueOf(e.get("yd1totalPrecent")));
            seriesList.add(new Double(String.valueOf(NumberUtil.mul(yd1totalPrecent, 100))).intValue());
        });


        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("xAxis", xAxisList);
        dataMap.put("series", seriesList);


        return R.ok().put("data", dataMap);
    }

    @ResponseBody
    @GetMapping("/queryDMStatisticsDetailByDate")
    public R queryDMStatisticsDetailByDate(@RequestParam Map<String, Object> params){
        logger.info("[queryDMStatisticsDetailByDate] params={}", params);
        List<Map<String, Object>> list = dmService.queryDMStatisticsDetailByDatePage(params);
        return R.ok().put("data", list);
    }


    private List<DianMingEntity> randomPickPcs16(int leaderCount, int policeCount, int sqPoliceCount, String date) {
        List<String> deptIdList = new ArrayList<>();
        deptIdList.addAll(DmConsts.pcs16DeptIdList);
        //deptIdList.addAll(DmConsts.zd5DeptIdList);

        List<Map<String, Object>> paramsList = new ArrayList<>();
        Map<String, Object> paramsMap = null;

        for (String deptId : deptIdList) {
            paramsMap = new HashMap<>();
            paramsMap.put("deptId", deptId);
            paramsMap.put("leaderCount", leaderCount);
            paramsMap.put("policeCount", policeCount);
            paramsMap.put("date", date);
            paramsList.add(paramsMap);
        }

        List<Map<String, Object>> zhiBanList = null;
        List<DianMingEntity> zhiBanLeaderList = null;
        List<DianMingEntity> zhiBanPoliceList = null;
        List<DianMingEntity> zhiBanSqPoliceList = null;
        List<DianMingEntity> dianMingList = new ArrayList<>();
        DianMingEntity dianMingEntity = null;
        DianMingEntity emptyDMEntity = null;
        List<Map<String, Object>> sqPoliceList = null;

        for (Map<String, Object> map : paramsList) {
            zhiBanLeaderList = new ArrayList<>();
            zhiBanPoliceList = new ArrayList<>();
            zhiBanSqPoliceList = new ArrayList<>();

            zhiBanList = dmService.queryZhiBanDataByDate(date, String.valueOf(map.get("deptId")));
            //get sqmj list by deptId
            zhiBanList.addAll(dmService.queryQsmjByDeptId(String.valueOf(map.get("deptId"))));
            //delete repeat sqmj before random pick
            zhiBanList = dmService.handerRepeatPolice(zhiBanList);

            for (Map<String, Object> zhiBanMap : zhiBanList) {
                int isLeader = Integer.parseInt(String.valueOf(zhiBanMap.get("isLeader")));
                dianMingEntity = new DianMingEntity();
                dianMingEntity.setDutyId(String.valueOf(zhiBanMap.get("dutyId")));
                dianMingEntity.setDeptId(String.valueOf(zhiBanMap.get("deptId")));
                dianMingEntity.setDeptName(String.valueOf(zhiBanMap.get("deptName")));
                dianMingEntity.setPolice(String.valueOf(zhiBanMap.get("police")));
                dianMingEntity.setPoliceCode(String.valueOf(zhiBanMap.get("policeCode")));
                dianMingEntity.setShortPhone(String.valueOf(zhiBanMap.get("shortPhone")));
                dianMingEntity.setDjj(String.valueOf(zhiBanMap.get("djj")));
                dianMingEntity.setIsLeader(isLeader);


                if (isLeader == DmConsts.TYPE_LEADER) {
                    zhiBanLeaderList.add(dianMingEntity);
                } else if (isLeader == DmConsts.TYPE_ZBPOLICE) {
                    zhiBanPoliceList.add(dianMingEntity);
                } else if (isLeader == DmConsts.TYPE_SQPOLICE) {
                    zhiBanSqPoliceList.add(dianMingEntity);
                }
            }

            //random select leader and police
            if (zhiBanLeaderList.size() == 0) {
                emptyDMEntity = new DianMingEntity();
                emptyDMEntity.setDeptId(String.valueOf(map.get("deptId")));
                emptyDMEntity.setPolice("未报备");
                emptyDMEntity.setIsLeader(1);
//                emptyDMEntity.setYd(2);
                emptyDMEntity.setDeptName(deptService.getOne(new QueryWrapper<DeptEntity>().eq("dept_id", emptyDMEntity.getDeptId()).last("limit 1")).getDeptName());

                dianMingList.add(emptyDMEntity);
            } else if (zhiBanLeaderList.size() <= leaderCount) {
                dianMingList.addAll(zhiBanLeaderList);
            } else {
                Collections.shuffle(zhiBanLeaderList);
                for (int i = 0; i < leaderCount; i++) {
                    dianMingList.add(zhiBanLeaderList.get(i));
                }
            }

            if (zhiBanPoliceList.size() == 0) {
                emptyDMEntity = new DianMingEntity();
                emptyDMEntity.setDeptId(String.valueOf(map.get("deptId")));
                emptyDMEntity.setPolice("未报备");
//                emptyDMEntity.setYd(2);
                emptyDMEntity.setDeptName(deptService.getOne(new QueryWrapper<DeptEntity>().eq("dept_id", emptyDMEntity.getDeptId()).last("limit 1")).getDeptName());

                dianMingList.add(emptyDMEntity);
            } else if (zhiBanPoliceList.size() <= policeCount) {
                dianMingList.addAll(zhiBanPoliceList);
            } else {
                Collections.shuffle(zhiBanPoliceList);
                for (int i = 0; i < policeCount; i++) {

                    dianMingList.add(zhiBanPoliceList.get(i));
                }
            }

            if (zhiBanSqPoliceList.size() <= sqPoliceCount) {
                dianMingList.addAll(zhiBanSqPoliceList);
            } else {
                Collections.shuffle(zhiBanSqPoliceList);
                for (int i = 0; i < sqPoliceCount; i++) {

                    dianMingList.add(zhiBanSqPoliceList.get(i));
                }
            }

        }

        return dianMingList;
    }

    private List<DianMingEntity> randomPickZd5(String date) {
        List<String> deptIdList = new ArrayList<>();
        deptIdList.addAll(DmConsts.zd5DeptIdList);

        List<Map<String, Object>> paramsList = new ArrayList<>();
        Map<String, Object> paramsMap = null;

        for (String deptId : deptIdList) {
            paramsMap = new HashMap<>();
            paramsMap.put("deptId", deptId);
            paramsMap.put("date", date);
            paramsList.add(paramsMap);
        }

        List<Map<String, Object>> zhiBanList = null;
        List<DianMingEntity> dianMingList = new ArrayList<>();
        DianMingEntity dianMingEntity = null;
        DianMingEntity emptyDMEntity = null;
        List<Map<String, Object>> sqPoliceList = null;

        for (Map<String, Object> map : paramsList) {

            //查出一个分队所有值班人员
            zhiBanList = dmService.queryZhiBanDataByDate(date, String.valueOf(map.get("deptId")));
            if (CollectionUtil.isNotEmpty(zhiBanList)) {
                Map<String, Object> zhiBanMap = zhiBanList.stream().findAny().get();

                int isLeader = Integer.parseInt(String.valueOf(zhiBanMap.get("isLeader")));
                dianMingEntity = new DianMingEntity();
                dianMingEntity.setDutyId(String.valueOf(zhiBanMap.get("dutyId")));
                dianMingEntity.setDeptId(String.valueOf(zhiBanMap.get("deptId")));
                dianMingEntity.setDeptName(String.valueOf(zhiBanMap.get("deptName")));
                dianMingEntity.setPolice(String.valueOf(zhiBanMap.get("police")));
                dianMingEntity.setPoliceCode(String.valueOf(zhiBanMap.get("policeCode")));
                dianMingEntity.setShortPhone(String.valueOf(zhiBanMap.get("shortPhone")));
                dianMingEntity.setDjj(String.valueOf(zhiBanMap.get("djj")));
                dianMingEntity.setIsLeader(isLeader);

                dianMingList.add(dianMingEntity);
            } else {
                emptyDMEntity = new DianMingEntity();
                emptyDMEntity.setDeptId(String.valueOf(map.get("deptId")));
                emptyDMEntity.setPolice("未报备");
//                emptyDMEntity.setYd(2);
                emptyDMEntity.setDeptName(deptService.getOne(new QueryWrapper<DeptEntity>().eq("dept_id", emptyDMEntity.getDeptId()).last("limit 1")).getDeptName());

                dianMingList.add(emptyDMEntity);
            }

        }

        return dianMingList;

    }

    private List<DianMingEntity> randomPickXtj3(String date) {
        List<DianMingEntity> dianMingList = new ArrayList<>();

        List<String> reportPointIdList = new ArrayList<>();
        reportPointIdList.addAll(DmConsts.xtj3List);

        List<Map<String, Object>> paramsList = new ArrayList<>();
        Map<String, Object> paramsMap = null;

        for (String reportPointId : reportPointIdList) {
            paramsMap = new HashMap<>();
            paramsMap.put("deptId", DmConsts.XTJDD);
            paramsMap.put("date", date);
            paramsMap.put("reportPointId", reportPointId);
            paramsList.add(paramsMap);
        }

        List<Map<String, Object>> zhiBanList = null;
        DianMingEntity dianMingEntity = null;
        DianMingEntity emptyDMEntity = null;

        for (Map<String, Object> map : paramsList) {
            zhiBanList = dmService.queryXtjZhiBanDataByReportPointIdAndDate(map);
            if (CollectionUtil.isNotEmpty(zhiBanList)) {
                Map<String, Object> zhiBanMap = zhiBanList.stream().findAny().get();
                int isLeader = Integer.parseInt(String.valueOf(zhiBanMap.get("isLeader")));
                dianMingEntity = new DianMingEntity();
                dianMingEntity.setDutyId(String.valueOf(zhiBanMap.get("dutyId")));
                dianMingEntity.setDeptId(String.valueOf(zhiBanMap.get("deptId")));
                dianMingEntity.setDeptName(DmConsts.XTJ_NAME.get(map.get("reportPointId")));
                dianMingEntity.setPolice(String.valueOf(zhiBanMap.get("police")));
                dianMingEntity.setPoliceCode(String.valueOf(zhiBanMap.get("policeCode")));
                dianMingEntity.setShortPhone(String.valueOf(zhiBanMap.get("shortPhone")));
                dianMingEntity.setDjj(String.valueOf(zhiBanMap.get("djj")));
                dianMingEntity.setIsLeader(isLeader);
                dianMingEntity.setReportPointId(String.valueOf(map.get("reportPointId")));

                dianMingList.add(dianMingEntity);
            } else {
                emptyDMEntity = new DianMingEntity();
                emptyDMEntity.setDeptId(String.valueOf(map.get("deptId")));
                emptyDMEntity.setPolice("未报备");
                emptyDMEntity.setDeptName(DmConsts.XTJ_NAME.get(map.get("reportPointId")));
                emptyDMEntity.setReportPointId(String.valueOf(map.get("reportPointId")));

                dianMingList.add(emptyDMEntity);
            }
        }

        return dianMingList;
    }

    private List<DianMingEntity> randomPickQjd2(String date,int count) {

        List<DianMingEntity> dianMingList = new ArrayList<>();
        List<DianMingEntity> dianMingTempList = new ArrayList<>();

        List<String> reportPointIdList = new ArrayList<>();
        reportPointIdList.addAll(DmConsts.qjd2List);

        List<Map<String, Object>> paramsList = new ArrayList<>();
        Map<String, Object> paramsMap = null;

        for (String reportPointId : reportPointIdList) {
            paramsMap = new HashMap<>();
            paramsMap.put("deptId", DmConsts.XTJDD);
            paramsMap.put("date", date);
            paramsMap.put("reportPointId", reportPointId);
            paramsList.add(paramsMap);
        }

        List<Map<String, Object>> zhiBanList = null;
        DianMingEntity dianMingEntity = null;
        DianMingEntity emptyDMEntity = null;

        for (Map<String, Object> map : paramsList) {
            zhiBanList = dmService.queryXtjZhiBanDataByReportPointIdAndDate(map);
            if (CollectionUtil.isNotEmpty(zhiBanList)) {
                for (Map<String, Object> zhiBanMap : zhiBanList) {
                    int isLeader = Integer.parseInt(String.valueOf(zhiBanMap.get("isLeader")));
                    dianMingEntity = new DianMingEntity();
                    dianMingEntity.setDutyId(String.valueOf(zhiBanMap.get("dutyId")));
                    dianMingEntity.setDeptId(String.valueOf(zhiBanMap.get("deptId")));
                    dianMingEntity.setDeptName(DmConsts.XTJ_NAME.get(map.get("reportPointId")));
                    dianMingEntity.setPolice(String.valueOf(zhiBanMap.get("police")));
                    dianMingEntity.setPoliceCode(String.valueOf(zhiBanMap.get("policeCode")));
                    dianMingEntity.setShortPhone(String.valueOf(zhiBanMap.get("shortPhone")));
                    dianMingEntity.setDjj(String.valueOf(zhiBanMap.get("djj")));
                    dianMingEntity.setIsLeader(isLeader);
                    dianMingEntity.setReportPointId(String.valueOf(map.get("reportPointId")));

                    dianMingTempList.add(dianMingEntity);
                }

            }

            if (dianMingTempList.size() == 0) {
                emptyDMEntity = new DianMingEntity();
                emptyDMEntity.setDeptId(String.valueOf(map.get("deptId")));
                emptyDMEntity.setPolice("未报备");
                emptyDMEntity.setDeptName(DmConsts.XTJ_NAME.get(map.get("reportPointId")));
                emptyDMEntity.setReportPointId(String.valueOf(map.get("reportPointId")));

                dianMingList.add(emptyDMEntity);

                emptyDMEntity = new DianMingEntity();
                emptyDMEntity.setDeptId(String.valueOf(map.get("deptId")));
                emptyDMEntity.setPolice("未报备");
                emptyDMEntity.setDeptName(DmConsts.XTJ_NAME.get(map.get("reportPointId")));
                emptyDMEntity.setReportPointId(String.valueOf(map.get("reportPointId")));

                dianMingList.add(emptyDMEntity);

            } else if(dianMingTempList.size() <= count) {
                dianMingList.addAll(dianMingTempList);
            } else {
                Collections.shuffle(dianMingTempList);
                for (int i = 0; i < count; i++) {
                    dianMingList.add(dianMingTempList.get(i));
                }
            }

        }

        return dianMingList;

    }


}
