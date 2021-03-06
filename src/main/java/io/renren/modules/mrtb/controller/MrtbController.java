package io.renren.modules.mrtb.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.*;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.R;
import io.renren.modules.dm.consts.DmConsts;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.dm.service.YjdmService;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.jcj.entity.YdjcjEntity;
import io.renren.modules.jcj.service.CjService;
import io.renren.modules.jcj.service.JjService;
import io.renren.modules.jcj.service.YdjcjService;
import io.renren.modules.mrtb.entity.*;
import io.renren.modules.mrtb.service.*;
import io.renren.modules.mrtb.util.WordUtil;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.service.SysConfigService;
import io.renren.modules.xlgl.entity.TxlEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020-04-27
 */
@RestController
@RequestMapping("mrtb")
public class MrtbController extends AbstractController {

    @Autowired
    private MrtbService mrtbService;

    @Autowired
    private DmService dmService;

    @Autowired
    private YjdmService yjdmService;

    @Autowired
    private YdjcjService ydjcjService;

    @Autowired
    private CjService cjService;

    @Autowired
    private MrtbXsjqService mrtbXsjqService;

    @Autowired
    private MrtbViewService mrtbViewService;

    @Autowired
    private JjService jjService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MrtbDhService mrtbDhService;

    @Autowired
    private MrtbHisService mrtbHisService;
    @Autowired
    private TXxService tXxService;


    //??????????????????
    @ResponseBody
    @GetMapping("/exportMrxj")
    //@SysLog("??????????????????")
    public void exportMrxj(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {

        logger.info("[exportMrxj] params={}", params);

        //????????????
        String fbrqParam = MapUtil.getStr(params, "fbrq");
        Date fbrqDate = DateUtil.parse(fbrqParam);
        String fbrq = DateUtil.year(fbrqDate) + "???" + (DateUtil.month(fbrqDate) + 1) + "???" + DateUtil.dayOfMonth(fbrqDate) + "???";

        //??????????????????
        String tbrqParam = MapUtil.getStr(params, "tbrq");
        Date tbrqDate = DateUtil.parse(tbrqParam);
        String tbrq = DateUtil.year(tbrqDate) + "???" + (DateUtil.month(tbrqDate) + 1) + "???" + DateUtil.dayOfMonth(tbrqDate) + "???";
        //??????????????????
        String tbrqMonthAndDay = (DateUtil.month(tbrqDate) + 1) + "???" + DateUtil.dayOfMonth(tbrqDate) + "???";

        DateTime tbrqDateEnd = DateUtil.offset(tbrqDate, DateField.DAY_OF_MONTH, 1);
        //????????????????????????
        String tbrqEnd = DateUtil.year(tbrqDateEnd) + "???" + (DateUtil.month(tbrqDateEnd) + 1) + "???" + DateUtil.dayOfMonth(tbrqDateEnd) + "???";

        //??????
        String qs = MapUtil.getStr(params, "qs");

        //?????????????????????????????????????????????????????????
        String dmTime = dmService.getDmTimeByTbrq(tbrqParam);
        params.put("dmTime", dmTime);
        List<DianMingEntity> entityList = new ArrayList<>();
        if (StrUtil.isNotEmpty(dmTime)) {
            entityList = dmService.queryDmDataByCondition(params);
        }

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
                    yd = "???";
                } else if (e.getYd() == 2) {
                    yd = "??";
                } else if (e.getYd() == 3) {
                    yd = "???";
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
                        yd = "???";
                    } else if (e.getYd() == 2) {
                        yd = "??";
                    } else if (e.getYd() == 3) {
                        yd = "???";
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
                        yd = "???";
                    } else if (e.getYd() == 2) {
                        yd = "??";
                    } else if (e.getYd() == 3) {
                        yd = "???";
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
                        yd = "???";
                    } else if (e.getYd() == 2) {
                        yd = "??";
                    } else if (e.getYd() == 3) {
                        yd = "???";
                    }
                    r.put("police", e.getPolice());
                    r.put("yd", yd);
                }
            });

        });

        otherResultList.addAll(zd5ResultList);

        Date d = DateUtil.parse(dmTime);
        String dmTimeFormat = "";
        if (d != null) {
            int monthInt = DateUtil.month(d) + 1;
            dmTimeFormat = monthInt + "???" + DateUtil.dayOfMonth(d) + "???" + DateUtil.hour(d, true) + "???" + DateUtil.minute(d) + "???";

        }

        //????????????
        QueryWrapper<YjdmEntity> wrapper = new QueryWrapper<YjdmEntity>().like("dm_time", tbrqParam);
        List<YjdmEntity> records = yjdmService.list(wrapper);
        Map<String, Object> yjdmMap = records
                .stream().collect(Collectors.toMap(YjdmEntity::getDeptId, YjdmEntity::getYd));

        yjdmMap.forEach((k, v) ->{
            if (Integer.parseInt(String.valueOf(v)) == 1) {
                yjdmMap.put(k,"???");
            }else{
                yjdmMap.put(k,"??");
            }
        });
        String yjDmTimeFormat = "";
        if (CollectionUtil.isNotEmpty(records)) {
            Date yjDmTime = records.get(0).getDmTime();
            yjDmTimeFormat = (DateUtil.month(yjDmTime) + 1) + "???" + DateUtil.dayOfMonth(yjDmTime) + "???" + DateUtil.hour(yjDmTime, true) + "???" + DateUtil.minute(yjDmTime) + "???";
        }

        //???????????????
        params.put("startDate",tbrqParam);
        List<YdjcjEntity> ydjcjList = ydjcjService.queryByDate(params);

        //????????????
        Map<String, Object> map = new HashMap<>();
        map.put("qs", qs);
        map.put("fbrq", fbrq);
        map.put("tbrqStart", tbrq);
        map.put("tbrqEnd", tbrqEnd);
        map.put("tbrqmd", tbrqMonthAndDay);
        map.put("pcs16List", pcs16ResultList);
        map.put("zd5List", otherResultList);
        map.put("xtjList", xtjResultList);
        map.put("dmtime", dmTimeFormat);
        map.putAll(yjdmMap);
        map.put("yjdmtime", yjDmTimeFormat);
        map.put("ydjcjList", ydjcjList);

        String fileName = fbrq + "??????.docx";
        //try {
        //    //URL resource = Thread.currentThread().getContextClassLoader().getResource("template/tongbao.docx");
        //
        //    XWPFDocument doc = WordExportUtil.exportWord07(resource.getPath(), map);
        //    if (doc != null) {
        //        downLoadWord(fileName, response, doc);
        //    }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}

        //URL resource = Thread.currentThread().getContextClassLoader().getResource("template/tongbao.ftl");
        WordUtil.createWord(map, "tongbao.ftl", fileName, response);

    }

    //??????????????????110??????
    @ResponseBody
    @GetMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
        logger.info("[queryPage] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page pageParams = new Page(page,limit);

        IPage<ZhMrtb> list = mrtbService.page(pageParams, new QueryWrapper<ZhMrtb>().orderByDesc("tbrq"));

        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());
    }

    //??????????????????

    @ResponseBody
    @GetMapping("/addMrtb")
    @SysLog("??????????????????")
    public R addMrtb(@RequestParam Map<String, Object> params) {
        logger.info("[addMrtb] params={}", params);
        String tbrqParam = MapUtil.getStr(params, "tbrq");

        //????????????????????????????????????
        ZhMrtb exsitedMrtb = mrtbService.getOne(new QueryWrapper<ZhMrtb>().eq("tbrq", tbrqParam).last("limit 1"));
        if (exsitedMrtb != null) {
            return R.error("?????????????????????????????????");
        }

        //???yesterday??????????????????
        params.put("yesterdayId", StrUtil.replace(tbrqParam, "-", ""));
        Map<String, Object> yesterdayMap = mrtbService.getYesterday(params);

        if (MapUtil.isEmpty(yesterdayMap)) {
            return R.error("???????????????????????????????????????");
        }

        String yesterdayStr = MapUtil.getStr(yesterdayMap, "CaseDesc");
        if (StrUtil.isEmpty(yesterdayStr)) {
            return R.error("???????????????????????????????????????");
        }

        ZhMrtb zhMrtb = new ZhMrtb();
        zhMrtb.setCreateBy(getUser().getName());
        zhMrtb.setFbrq(DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));

        //?????????????????????????????????????????????
        String firstDateOfYear = DateUtil.format(DateUtil.beginOfYear(DateUtil.parseDate(tbrqParam)), DatePattern.NORM_DATE_FORMAT);
        if (tbrqParam.equals(firstDateOfYear)) {
            zhMrtb.setQs("1");
        } else {
            //?????????????????????
            ZhMrtb lastMrtb = mrtbService.getOne(new QueryWrapper<ZhMrtb>().eq("tbrq", DateUtil.offsetDay(DateUtil.parseDate(tbrqParam).toJdkDate(), -1)).last("limit 1"));
            String lastQs = "";
            try {
                if (lastMrtb != null) {
                    lastQs = String.valueOf(Integer.valueOf(lastMrtb.getQs()).intValue() + 1);
                }

            } catch (Exception e) {
                //????????????????????????????????????

            }

            zhMrtb.setQs(lastQs);
        }

        zhMrtb.setTbrq(tbrqParam);
        zhMrtb.setTotalnum(MapUtil.getStr(yesterdayMap,"TotalNum"));
        zhMrtb.setNum01(MapUtil.getStr(yesterdayMap,"Num01"));
        zhMrtb.setNum02(MapUtil.getStr(yesterdayMap,"Num02"));
        zhMrtb.setNum03(MapUtil.getStr(yesterdayMap,"Num03"));
        zhMrtb.setNum04(MapUtil.getStr(yesterdayMap,"Num04"));
        zhMrtb.setNum05(MapUtil.getStr(yesterdayMap,"Num05"));
        zhMrtb.setNum06(MapUtil.getStr(yesterdayMap,"Num06"));
        zhMrtb.setNum08(MapUtil.getStr(yesterdayMap,"Num08"));
        zhMrtb.setNum11(MapUtil.getStr(yesterdayMap,"Num11"));
        zhMrtb.setNum99(MapUtil.getStr(yesterdayMap,"Num99"));

        String tableStr = "";

        tableStr = StrUtil.subAfter(yesterdayStr, "</table>", false);
        tableStr = "</table>" + tableStr;
        tableStr = HtmlUtil.removeAllHtmlAttr(tableStr, "table", "th", "tr", "td", "a");
        tableStr = HtmlUtil.removeHtmlTag(tableStr, "img");
        tableStr = StrUtil.replace(tableStr, "&nbsp;", "");
        tableStr = StrUtil.replace(tableStr, "\t", "");
        tableStr = StrUtil.replace(tableStr, "\n", "");
        tableStr = StrUtil.subBefore(tableStr, "<br", true);

        Document doc = Jsoup.parse(tableStr);
        Elements rows = doc.select("table").get(0).select("tr");


        for (int i = 1; i < 4; i++) {
            System.out.println("-----------------------------------------------------------------");
            Element row = rows.get(i);
            if (i == 1) {
                zhMrtb.setJd01(row.select("td").get(2).text());
                zhMrtb.setHs01(row.select("td").get(3).text());
                zhMrtb.setCh01(row.select("td").get(4).text());
                zhMrtb.setGz01(row.select("td").get(5).text());
                zhMrtb.setJw01(row.select("td").get(6).text());
                zhMrtb.setYm01(row.select("td").get(7).text());
                zhMrtb.setHss01(row.select("td").get(8).text());
                zhMrtb.setFx01(row.select("td").get(9).text());
                zhMrtb.setGd01(row.select("td").get(10).text());
                zhMrtb.setYs01(row.select("td").get(11).text());
                zhMrtb.setSjy01(row.select("td").get(12).text());
                zhMrtb.setNsq01(row.select("td").get(13).text());
                zhMrtb.setXs01(row.select("td").get(14).text());
                zhMrtb.setZx01(row.select("td").get(15).text());
                zhMrtb.setXh01(row.select("td").get(16).text());
                zhMrtb.setJy01(row.select("td").get(17).text());
                zhMrtb.setGq01(row.select("td").get(18).text());
            } else if (i == 2) {
                zhMrtb.setJd02(row.select("td").get(2).text());
                zhMrtb.setHs02(row.select("td").get(3).text());
                zhMrtb.setCh02(row.select("td").get(4).text());
                zhMrtb.setGz02(row.select("td").get(5).text());
                zhMrtb.setJw02(row.select("td").get(6).text());
                zhMrtb.setYm02(row.select("td").get(7).text());
                zhMrtb.setHss02(row.select("td").get(8).text());
                zhMrtb.setFx02(row.select("td").get(9).text());
                zhMrtb.setGd02(row.select("td").get(10).text());
                zhMrtb.setYs02(row.select("td").get(11).text());
                zhMrtb.setSjy02(row.select("td").get(12).text());
                zhMrtb.setNsq02(row.select("td").get(13).text());
                zhMrtb.setXs02(row.select("td").get(14).text());
                zhMrtb.setZx02(row.select("td").get(15).text());
                zhMrtb.setXh02(row.select("td").get(16).text());
                zhMrtb.setJy02(row.select("td").get(17).text());
                zhMrtb.setGq02(row.select("td").get(18).text());

            } else if (i == 3) {
                zhMrtb.setJd03(row.select("td").get(2).text());
                zhMrtb.setHs03(row.select("td").get(3).text());
                zhMrtb.setCh03(row.select("td").get(4).text());
                zhMrtb.setGz03(row.select("td").get(5).text());
                zhMrtb.setJw03(row.select("td").get(6).text());
                zhMrtb.setYm03(row.select("td").get(7).text());
                zhMrtb.setHss03(row.select("td").get(8).text());
                zhMrtb.setFx03(row.select("td").get(9).text());
                zhMrtb.setGd03(row.select("td").get(10).text());
                zhMrtb.setYs03(row.select("td").get(11).text());
                zhMrtb.setSjy03(row.select("td").get(12).text());
                zhMrtb.setNsq03(row.select("td").get(13).text());
                zhMrtb.setXs03(row.select("td").get(14).text());
                zhMrtb.setZx03(row.select("td").get(15).text());
                zhMrtb.setXh03(row.select("td").get(16).text());
                zhMrtb.setJy03(row.select("td").get(17).text());
                zhMrtb.setGq03(row.select("td").get(18).text());

            }
            
            System.out.println("-----------------------------------------------------------------");
        }

        //????????????
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//???????????????MM
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//???????????????MM
            Date date = simpleDateFormat.parse(tbrqParam);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH,1);
            Date afterDate = c.getTime();
            params.put("beforeDate",sdf.format(date)+"07");
            params.put("date",sdf.format(afterDate)+"07");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<JcjCjxx> jcjcjxxList = cjService.queryYesterdayXSJQ(params);

        //????????????
        for (JcjCjxx jcjCjxx : jcjcjxxList) {
            switch(jcjCjxx.getCjlb()){
                //????????????
                case "????????????":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;

                //??????
                case "??????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "???????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "??????????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "?????????????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "??????????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "??????????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "???????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "???????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "????????????":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;

                //??????????????????
                case "???????????????":
                    zhMrtb.setXsjq03(zhMrtb.getXsjq03() != null ? zhMrtb.getXsjq03() + 1 : 1);
                    break;
                case "?????????????????????":
                    zhMrtb.setXsjq03(zhMrtb.getXsjq03() != null ? zhMrtb.getXsjq03() + 1 : 1);
                    break;

                //??????
                default:
                    zhMrtb.setXsjq99(zhMrtb.getXsjq99() != null ? zhMrtb.getXsjq99() + 1 : 1);
                    break;

            }
        }

        //sys_config?????????????????????
        String comment3 = sysConfigService.getValue("gzts");
        zhMrtb.setComment3(comment3);

        boolean issaved = mrtbService.saveCC(zhMrtb, jcjcjxxList);

        if (issaved) {
            return R.ok().put("data", zhMrtb.getId());
        } else {
            return R.error("?????????????????????????????????");
        }
    }

    //????????????110??????????????????
    @ResponseBody
    @GetMapping("/queryMrtbXsjqByTbid")
    public R queryMrtbXsjqByTbid(@RequestParam String id){
        logger.info("[queryMrtbXsjqByTbid] tbid={}", id);


        List<ZhMrtbXsjq> zhMrtbXsjqList = mrtbXsjqService.list(new QueryWrapper<ZhMrtbXsjq>().eq("tbid", id).orderByAsc("dw"));

        zhMrtbXsjqList.forEach(e -> {
            e.setJcjJjxx(jjService.queryByJjbh(e.getJjbh()));
        });


        return R.ok().put("data", zhMrtbXsjqList);
    }

    //??????????????????
    @ResponseBody
    @PostMapping("/updateMrtb")
    @SysLog("??????????????????")
    public R updateMrtb(@RequestBody ZhMrtb zhMrtb) {
        logger.info("[updateMrtb] zhMrtb={}", zhMrtb);
        if (StrUtil.isEmpty(zhMrtb.getFbrq())) {
            zhMrtb.setFbrq(null);
        }

        sysConfigService.updateValueByKey("gzts", zhMrtb.getComment3());
        boolean isupdated = mrtbService.updateById(zhMrtb);

        if (isupdated) {
            return R.ok();
        } else {
            return R.error("?????????????????????????????????");
        }
    }

    //??????????????????
    @ResponseBody
    @GetMapping("/deleteMrtb")
    @SysLog("??????????????????")
    public R deleteMrtb(@RequestParam String id) {
        logger.info("[deleteMrtb] id={}", id);
        mrtbService.removeById(id);
        mrtbXsjqService.remove(new QueryWrapper<ZhMrtbXsjq>().eq("tbid", id));
        return R.ok();
    }


    //??????id??????110????????????
    @ResponseBody
    @GetMapping("/queryById")
    public R queryById(@RequestParam String id, String deptId) {
        logger.info("[queryById] id={}, deptId={}", id, deptId);
        Map<String, Object> params = new HashMap<>();

        ZhMrtb zhMrtb = mrtbService.getById(id);

        //????????????
        String fbrqParam = zhMrtb.getFbrq();
        String fbrq = null;
        if (StrUtil.isNotEmpty(fbrqParam)) {
            Date fbrqDate = DateUtil.parse(fbrqParam);
            fbrq = DateUtil.year(fbrqDate) + "???" + (DateUtil.month(fbrqDate) + 1) + "???" + DateUtil.dayOfMonth(fbrqDate) + "???";
        }

        //??????????????????
        String tbrqParam = zhMrtb.getTbrq();
        Date tbrqDate = DateUtil.parse(tbrqParam);
        String tbrq = DateUtil.year(tbrqDate) + "???" + (DateUtil.month(tbrqDate) + 1) + "???" + DateUtil.dayOfMonth(tbrqDate) + "???";
        //??????????????????
        String tbrqMonthAndDay = (DateUtil.month(tbrqDate) + 1) + "???" + DateUtil.dayOfMonth(tbrqDate) + "???";

        DateTime tbrqDateEnd = DateUtil.offset(tbrqDate, DateField.DAY_OF_MONTH, 1);
        //????????????????????????
        String tbrqEnd = DateUtil.year(tbrqDateEnd) + "???" + (DateUtil.month(tbrqDateEnd) + 1) + "???" + DateUtil.dayOfMonth(tbrqDateEnd) + "???";

        //??????
        String qs = zhMrtb.getQs();

        //?????????????????????????????????????????????????????????
        String dmTime = dmService.getDmTimeByTbrq(tbrqParam);
        params.put("dmTime", dmTime);
        List<DianMingEntity> entityList = new ArrayList<>();
        if (StrUtil.isNotEmpty(dmTime)) {
            entityList = dmService.queryDmDataByCondition(params);
        }

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
            Map<String, Object> map = new HashMap<>();
            map.put("deptName", e);
            pcs16ResultList.add(map);
        });

        zd5DeptNameSet.forEach(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("deptName", e);
            zd5ResultList.add(map);
        });

        xtjDeptNameSet.forEach(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("deptName", e);
            xtjResultList.add(map);
        });
        otherDeptNameSet.forEach(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("deptName", e);
            otherResultList.add(map);
        });


        entityList.stream().forEach(e -> {
            pcs16ResultList.forEach(r -> {
                String deptName = String.valueOf(r.get("deptName"));
                String yd = "";
                if (e.getYd() == 1) {
                    yd = "???";
                } else if (e.getYd() == 2) {
                    yd = "??";
                } else if (e.getYd() == 3) {
                    yd = "???";
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
                        yd = "???";
                    } else if (e.getYd() == 2) {
                        yd = "??";
                    } else if (e.getYd() == 3) {
                        yd = "???";
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
                        yd = "???";
                    } else if (e.getYd() == 2) {
                        yd = "??";
                    } else if (e.getYd() == 3) {
                        yd = "???";
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
                        yd = "???";
                    } else if (e.getYd() == 2) {
                        yd = "??";
                    } else if (e.getYd() == 3) {
                        yd = "???";
                    }
                    r.put("police", e.getPolice());
                    r.put("yd", yd);
                }
            });

        });

        otherResultList.addAll(zd5ResultList);

        Date d = DateUtil.parse(dmTime);
        String dmTimeFormat = "";
        if (d != null) {
            int monthInt = DateUtil.month(d) + 1;
            dmTimeFormat = monthInt + "???" + DateUtil.dayOfMonth(d) + "???" + DateUtil.hour(d, true) + "???" + DateUtil.minute(d) + "???";

        }

        //????????????
        QueryWrapper<YjdmEntity> wrapper = new QueryWrapper<YjdmEntity>().like("dm_time", tbrqParam);
        List<YjdmEntity> records = yjdmService.list(wrapper);
        Map<String, Object> yjdmMap = records
                .stream().collect(Collectors.toMap(YjdmEntity::getDeptId, YjdmEntity::getYd, (oldValue, newValue) -> oldValue));

        yjdmMap.forEach((k, v) -> {
            if (Integer.parseInt(String.valueOf(v)) == 1) {
                yjdmMap.put(k, "???");
            } else {
                yjdmMap.put(k, "??");
            }
        });
        String yjDmTimeFormat = "";
        if (CollectionUtil.isNotEmpty(records)) {
            Date yjDmTime = records.get(0).getDmTime();
            yjDmTimeFormat = (DateUtil.month(yjDmTime) + 1) + "???" + DateUtil.dayOfMonth(yjDmTime) + "???" + DateUtil.hour(yjDmTime, true) + "???" + DateUtil.minute(yjDmTime) + "???";
        }

        //???????????????
        params.put("startDate", tbrqParam);
        params.put("endDate", tbrqParam);
        List<YdjcjEntity> ydjcjList = ydjcjService.queryByDate(params);

        //?????????
        params.put("yesterdayId", StrUtil.replace(tbrqParam, "-", ""));
        Map<String, Object> yesterdayMap = mrtbService.getYesterday(params);

        String yesterdayStr = MapUtil.getStr(yesterdayMap, "CaseDesc");
        String jqStr = "";
        if (StrUtil.isNotEmpty(yesterdayStr)) {
            jqStr = StrUtil.subBefore(yesterdayStr, "<table", false);
            jqStr = StrUtil.replace(HtmlUtil.cleanHtmlTag(jqStr), "&nbsp;", "");
            jqStr = StrUtil.replace(jqStr, "\t", "");
            jqStr = StrUtil.replace(jqStr, "\n", "");
        }

        //????????????????????????
        String[] jqxAxis;
        String[] jqseries;
        if (MapUtil.isNotEmpty(yesterdayMap)) {
            jqxAxis = new String[]{"????????????", "????????????", "????????????", "????????????", "????????????", "????????????", "????????????", "??????", "????????????", "????????????"};
            jqseries = new String[]{
                    /**
                    MapUtil.getStr(yesterdayMap, "TotalNum"),
                    MapUtil.getStr(yesterdayMap, "Num01"),
                    MapUtil.getStr(yesterdayMap, "Num02"),
                    MapUtil.getStr(yesterdayMap, "Num03"),
                    MapUtil.getStr(yesterdayMap, "Num04"),
                    MapUtil.getStr(yesterdayMap, "Num06"),
                    MapUtil.getStr(yesterdayMap, "Num05"),
                    MapUtil.getStr(yesterdayMap, "Num08"),
                    MapUtil.getStr(yesterdayMap, "Num11"),
                    MapUtil.getStr(yesterdayMap, "Num99")
                     **/

                    zhMrtb.getTotalnum(),//????????????
                    zhMrtb.getNum01(),//????????????
                    zhMrtb.getNum02(),//????????????
                    zhMrtb.getNum03(),//????????????
                    zhMrtb.getNum04(),//????????????
                    zhMrtb.getNum06(),//????????????
                    zhMrtb.getNum05(),//????????????
                    zhMrtb.getNum08(),//??????
                    zhMrtb.getNum11(),//????????????
                    zhMrtb.getNum99()//????????????
            };
        } else {
            jqxAxis = new String[]{};
            jqseries = new String[]{};
        }

        //??????????????????????????????
        //['product', '2015', '2016', '2017'],
        //['Matcha Latte', '43.3', '85.8', '93.7'],
        //['Milk Tea', 83.1, 73.4, 55.1],
        //['Cheese Cocoa', 86.4, 65.2, 82.5],
        //['Walnut Brownie', 72.4, 53.9, 39.1]

        List<List<String>> jqDwArr = new ArrayList<>();
        if (StrUtil.isNotEmpty(yesterdayStr)) {

            List<String> rowList = CollectionUtil.newArrayList("product", "????????????", "????????????", "????????????");
            List<String> jdList = CollectionUtil.newArrayList("??????", zhMrtb.getJd01(), zhMrtb.getJd02(), zhMrtb.getJd03());
            List<String> hsList = CollectionUtil.newArrayList("??????", zhMrtb.getHs01(), zhMrtb.getHs02(), zhMrtb.getHs03());
            List<String> chList = CollectionUtil.newArrayList("??????", zhMrtb.getCh01(), zhMrtb.getHs02(), zhMrtb.getCh03());
            List<String> gzList = CollectionUtil.newArrayList("??????", zhMrtb.getGz01(), zhMrtb.getGz02(), zhMrtb.getGz03());
            List<String> jwList = CollectionUtil.newArrayList("??????", zhMrtb.getJw01(), zhMrtb.getJw02(), zhMrtb.getJw03());
            List<String> ymList = CollectionUtil.newArrayList("??????", zhMrtb.getYm01(), zhMrtb.getYm02(), zhMrtb.getYm03());
            List<String> hssList = CollectionUtil.newArrayList("??????", zhMrtb.getHss01(), zhMrtb.getHss02(), zhMrtb.getHss03());
            List<String> fxList = CollectionUtil.newArrayList("??????", zhMrtb.getFx01(), zhMrtb.getFx02(), zhMrtb.getFx03());
            List<String> gdList = CollectionUtil.newArrayList("??????", zhMrtb.getGd01(), zhMrtb.getGd02(), zhMrtb.getGd03());
            List<String> ysList = CollectionUtil.newArrayList("??????", zhMrtb.getYs01(), zhMrtb.getYs02(), zhMrtb.getYs03());
            List<String> sjyList = CollectionUtil.newArrayList("?????????", zhMrtb.getSjy01(), zhMrtb.getSjy02(), zhMrtb.getSjy03());
            List<String> nsqList = CollectionUtil.newArrayList("?????????", zhMrtb.getNsq01(), zhMrtb.getNsq02(), zhMrtb.getNsq03());
            List<String> xsList = CollectionUtil.newArrayList("??????", zhMrtb.getXs01(), zhMrtb.getXs02(), zhMrtb.getXs03());
            List<String> zxList = CollectionUtil.newArrayList("??????", zhMrtb.getZx01(), zhMrtb.getZx02(), zhMrtb.getZx03());
            List<String> xhList = CollectionUtil.newArrayList("??????", zhMrtb.getXh01(), zhMrtb.getXh02(), zhMrtb.getXh03());
            List<String> jyList = CollectionUtil.newArrayList("??????", zhMrtb.getJy01(), zhMrtb.getJy02(), zhMrtb.getJy03());
            List<String> gqList = CollectionUtil.newArrayList("??????", zhMrtb.getGq01(), zhMrtb.getGq02(), zhMrtb.getGq03());

            /*
            String tableStr = "";

            tableStr = StrUtil.subAfter(yesterdayStr, "</table>", false);
            tableStr = "</table>" + tableStr;
            tableStr = HtmlUtil.removeAllHtmlAttr(tableStr, "table", "th", "tr", "td", "a");
            tableStr = HtmlUtil.removeHtmlTag(tableStr, "img");
            tableStr = StrUtil.replace(tableStr, "&nbsp;", "");
            tableStr = StrUtil.replace(tableStr, "\t", "");
            tableStr = StrUtil.replace(tableStr, "\n", "");
            tableStr = StrUtil.subBefore(tableStr, "<br", true);

            Document doc = Jsoup.parse(tableStr);
            Elements rows = doc.select("table").get(0).select("tr");


            for (int i = 1; i < 4; i++) {
                System.out.println("-----------------------------------------------------------------");
                Element row = rows.get(i);
                jdList.add(row.select("td").get(2).text());
                hsList.add(row.select("td").get(3).text());
                chList.add(row.select("td").get(4).text());
                gzList.add(row.select("td").get(5).text());
                jwList.add(row.select("td").get(6).text());
                ymList.add(row.select("td").get(7).text());
                hssList.add(row.select("td").get(8).text());
                fxList.add(row.select("td").get(9).text());
                gdList.add(row.select("td").get(10).text());
                ysList.add(row.select("td").get(11).text());
                sjyList.add(row.select("td").get(12).text());
                nsqList.add(row.select("td").get(13).text());
                xsList.add(row.select("td").get(14).text());
                zxList.add(row.select("td").get(15).text());
                xhList.add(row.select("td").get(16).text());
                jyList.add(row.select("td").get(17).text());
                gqList.add(row.select("td").get(18).text());
                System.out.println("-----------------------------------------------------------------");
            }
            */

            jqDwArr.add(rowList);
            jqDwArr.add(jdList);
            jqDwArr.add(hsList);
            jqDwArr.add(chList);
            jqDwArr.add(gzList);
            jqDwArr.add(jwList);
            jqDwArr.add(ymList);
            jqDwArr.add(hssList);
            jqDwArr.add(fxList);
            jqDwArr.add(gdList);
            jqDwArr.add(ysList);
            jqDwArr.add(sjyList);
            jqDwArr.add(nsqList);
            jqDwArr.add(xsList);
            jqDwArr.add(zxList);
            jqDwArr.add(xhList);
            jqDwArr.add(jyList);
            jqDwArr.add(gqList);
        }

        //????????????
        //List<ZhMrtbXsjq> zhMrtbXsjqList = mrtbXsjqService.list(new QueryWrapper<ZhMrtbXsjq>().eq("tbid", zhMrtb.getId()).orderByAsc("dw"));


        //????????????????????????jilu
        if (StrUtil.isNotBlank(deptId)) {
            try {
                //todo 11

                ZhMrtbView zhMrtbView = mrtbViewService.getOne(new QueryWrapper<ZhMrtbView>()
                        .eq("tbrq", zhMrtb.getTbrq())
                        .eq("dept_id", deptId)
                        .last("limit 1"));

                if (zhMrtbView == null) {
                    mrtbViewService.save(ZhMrtbView.builder().deptId(deptId).tbrq(zhMrtb.getTbrq()).build());
                }

            } catch (Exception e) {

                logger.error("error occus when zh_mrtb_view , msg={}", e.getMessage());
            }
        }

        //????????????
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("qs", qs);//??????
        map.put("fbrq", fbrq);//????????????
        map.put("tbrqStart", tbrq);//?????????????????? 2020???5???20???7???
        map.put("tbrqEnd", tbrqEnd);//?????????????????? 2020???5???21???7???
        map.put("tbrqmd", tbrqMonthAndDay);//?????????????????????  5???12???
        map.put("pcs16List", pcs16ResultList);//16??????????????????
        map.put("zd5List", otherResultList);//5???????????????
        map.put("xtjList", xtjResultList);//???????????????
        map.put("dmtime", dmTimeFormat);//????????????
        map.put("yjdm", yjdmMap);//????????????
        map.put("yjdmtime", yjDmTimeFormat);//??????????????????
        map.put("ydjcjList", ydjcjList);//???????????????
        map.put("jqStr", jqStr);//????????????
        map.put("jqxAxis", jqxAxis);//??????????????????
        map.put("jqseries", jqseries);//???????????????
        map.put("jqDwArr", jqDwArr);//????????????????????????
        map.put("mrtb", zhMrtb);//?????????????????????
        //map.put("zhMrtbXsjqList", zhMrtbXsjqList);//??????????????????

        return R.ok().put("data", map);
    }

    //??????????????????????????????
    @ResponseBody
    @PostMapping("/updateMrtbXsjq")
    @SysLog("??????????????????????????????")
    public R updateMrtbXsjq(@RequestBody ZhMrtbXsjq zhMrtbXsjq) {
        logger.info("[updateMrtbXsjq] zhMrtbXsjq={}", zhMrtbXsjq);
        boolean isupdated = mrtbXsjqService.updateById(zhMrtbXsjq);

        //todo ??????mrtb????????????????????????
        //mrtbService.calMrtb(zhMrtbXsjq.getTbid());

        if (isupdated) {
            return R.ok();
        } else {
            return R.error("?????????????????????????????????");
        }
    }

    //?????????????????????????????????
    @ResponseBody
    @GetMapping("/deleteMrtbXsjq")
    @SysLog("?????????????????????????????????")
    public R deleteMrtbXsjq(@RequestParam String id,@RequestParam int tbid) {
        logger.info("[deleteMrtbXsjq] id={}", id);
        mrtbXsjqService.removeById(id);
        //todo ??????mrtb????????????????????????
        mrtbService.calMrtb(tbid);
        return R.ok();
    }

    //??????????????????????????????
    @ResponseBody
    @PostMapping("/addMrtbXsjq")
    @SysLog("??????????????????????????????")
    public R addMrtbXsjq(@RequestBody ZhMrtbXsjq zhMrtbXsjq) {
        logger.info("[addMrtbXsjq] zhMrtbXsjq={}", zhMrtbXsjq);
        boolean isupdated = mrtbXsjqService.save(zhMrtbXsjq);

        //todo ??????mrtb????????????????????????
        mrtbService.calMrtb(zhMrtbXsjq.getTbid());

        if (isupdated) {
            return R.ok();
        } else {
            return R.error("?????????????????????????????????");
        }
    }

    //??????deptId??????????????????????????????????????????
    @ResponseBody
    @GetMapping("/queryMrtbView")
    public R queryMrtbView(String deptId) {
        logger.info("[queryMrtbView] deptId={}", deptId);
        Map<String, Object> result = new HashMap<>();

        //?????????????????????

        String yesterday = DateUtil.format(DateUtil.yesterday().toJdkDate(), DatePattern.NORM_DATE_FORMAT);

        //?????????????????????
        ZhMrtb zhMrtb = mrtbService.getOne(new QueryWrapper<ZhMrtb>().eq("tbrq", yesterday).last("limit 1"));
        if (zhMrtb != null) {
            List<ZhMrtbView> list = mrtbViewService.list(new QueryWrapper<ZhMrtbView>().eq("tbrq", yesterday).eq("dept_id", deptId));
            if (CollectionUtil.isEmpty(list)) {

                result.put("tbrq", zhMrtb.getTbrq());
                result.put("id", zhMrtb.getId());

            }
        }

        return R.ok().put("result", result);
    }

    //??????deptId??????????????????????????????????????????
    @ResponseBody
    @GetMapping("/queryMrtbDhPage")
    public R queryMrtbDhPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryMrtbDhPage] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page pageParams = new Page(page,limit);
        String phone = MapUtil.getStr(params, "phone");

        IPage<ZhMrtbDh> list = mrtbDhService.page(pageParams, new QueryWrapper<ZhMrtbDh>().like(StrUtil.isNotBlank(phone), "phone", phone)
                .or().like(StrUtil.isNotBlank(phone), "name", phone));

        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());

    }

    @ResponseBody
    @PostMapping("/uploadMrtbDh")
    public R uploadMrtbDh(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {

//        Thread.sleep(3000L);
        ImportParams params = new ImportParams();

        List<ZhMrtbDh> list = ExcelImportUtil.importExcel(file.getInputStream(), ZhMrtbDh.class, params);
        list.stream().forEach(e -> {
            if (mrtbDhService.count(new QueryWrapper<ZhMrtbDh>().eq("phone", e.getPhone())) <= 0) {
                //??????IP??????
                e.setIp(IPUtils.getIpAddr(request));
                e.setCreateBy(getUser().getName());
                mrtbDhService.save(e);
            }
        });
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/downloadMrtbDh")
    public void downloadMrtbDh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/mrtb_dh.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
        downLoaFile("??????????????????.xlsx", response, new File(resource.getPath()));
    }

    //??????????????????????????????
    @ResponseBody
    @PostMapping("/sentMrtb")
    public R sentMrtb(@RequestBody MrtbMsgForm mrtbMsgForm, HttpServletRequest request) {
        logger.info("[sentMrtb] mrtbMsgForm={}", mrtbMsgForm);

        mrtbService.sentMrtb(mrtbMsgForm.getMrtbId(), mrtbMsgForm.getMsg(), IPUtils.getIpAddr(request), getUser().getName());
        return R.ok();
    }

    public static void main(String[] args) {
        Date now = new Date();
        //System.out.println(String.valueOf(DateUtil.betweenDay(DateUtil.beginOfYear(now), now , true)));
        String ee ="091";
        int ii = Integer.valueOf(ee).intValue();
        System.out.println(ii);
    }

    //????????????????????????????????????
    @ResponseBody
    @GetMapping("/queryMrtbHisPage")
    public R queryMrtbHisPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryMrtbHisPage] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page pageParams = new Page(page,limit);

        IPage<ZhMrtbHis> list = mrtbHisService.page(pageParams, new QueryWrapper<ZhMrtbHis>().orderByDesc("create_time"));

        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());

    }

    //??????????????????
    @ResponseBody
    @GetMapping("/deleteMrtbDh")
    @SysLog("????????????????????????")
    public R deleteMrtbDh(@RequestParam String id) {
        logger.info("[deleteMrtbDh] id={}", id);
        mrtbDhService.removeById(id);
        return R.ok();
    }

    //??????????????????
    @ResponseBody
    @PostMapping("/deleteAllMrtbDh")
    @SysLog("??????????????????????????????")
    public R deleteAllMrtbDh(@RequestBody List<ZhMrtbDh> zhMrtbDhList) {
        logger.info("[deleteAllMrtbDh params={}]", zhMrtbDhList);

        if (!mrtbDhService.removeByIds(zhMrtbDhList.stream().map(e -> e.getId()).collect(Collectors.toList()))) {
            throw new RRException("????????????????????????");
        }
        return R.ok();
    }

    //??????????????????
    @ResponseBody
    @PostMapping("/publishMrtb")
    public R publishMrtb(@RequestBody MrtbPublishForm mrtbPublishForm, HttpServletRequest request) {
        logger.info("[publishMrtb] mrtbPublishForm={}", mrtbPublishForm);

        IPUtils.getIpAddr(request);

        ZhMrtb zhMrtb = mrtbService.getById(mrtbPublishForm.getMrtbId());
        if (zhMrtb == null) {
            throw new RRException("??????????????????");
        }

        TXx exsitedTxx = tXxService.getByIdCC(zhMrtb.getXxId());


        //??????Txx
        if (zhMrtb.getXxId() == null || exsitedTxx == null) {
            //??????t_xx
            TXx tXx = new TXx();
            tXx.setLmid(57);
            tXx.setLbid(225);
            tXx.setFbdw(",,");
            tXx.setBt("????????????????????????????????????" + zhMrtb.getQs() + "??????");
            tXx.setNr(mrtbPublishForm.getPublishContent());
            tXx.setKeys("");
            tXx.setFbrq(new Date());
            tXx.setHits(0);
            tXx.setPlsx(999);
            tXx.setUid("zhzx");
            tXx.setBmid(43);
            tXx.setPicUrl("");
            tXx.setFujian("");
            tXx.setBmi("0");
            tXx.setZhyw("0");
            tXx.setCanComment("0");
            tXx.setReqAccept(",,");
            tXx.setIsPicNews("0");
            tXx.setIsImport("0");
            tXx.setLinkUrl("");
            tXx.setJrdd("0");
            tXx.setTcxx("0");
            tXx.setDtime(null);
            tXx.setTcw("");
            tXx.setTch("");
            tXx.setTcl("");
            tXx.setTct("");
            tXx.setCanps("0");
            tXx.setBtcolor("");
            tXx.setSffb("1");
            tXx.setSfbtj("1");
            tXx.setStcy("0");
            tXx.setYdjw("0");

            logger.info("[publishMrtb] txx: {}", tXx);

            if (!tXxService.saveCC(tXx)) {
                throw new RRException("????????????????????????");
            }

            zhMrtb.setXxId(tXx.getId());


        } else {
            //??????t_xx
            exsitedTxx.setBt("????????????????????????????????????" + zhMrtb.getQs() + "??????");
            exsitedTxx.setNr(mrtbPublishForm.getPublishContent());
            zhMrtb.setXxId(exsitedTxx.getId());

            logger.info("[publishMrtb] txx: {}", exsitedTxx);

            if (!tXxService.updateByIdCC(exsitedTxx)) {
                throw new RRException("????????????????????????");
            }
        }

        //??????mrtb
        zhMrtb.setPublishTime(new Date());
        zhMrtb.setPublishIp(IPUtils.getIpAddr(request));
        zhMrtb.setPublished(1);
        zhMrtb.setPublishContent(mrtbPublishForm.getPublishContent());

        logger.info("[publishMrtb] mrtb: {}", zhMrtb);

        mrtbService.updateById(zhMrtb);

        return R.ok();
    }

}
