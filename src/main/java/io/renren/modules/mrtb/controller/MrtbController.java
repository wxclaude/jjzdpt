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


    //导出每日通报
    @ResponseBody
    @GetMapping("/exportMrxj")
    //@SysLog("导出每日通报")
    public void exportMrxj(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {

        logger.info("[exportMrxj] params={}", params);

        //发布日期
        String fbrqParam = MapUtil.getStr(params, "fbrq");
        Date fbrqDate = DateUtil.parse(fbrqParam);
        String fbrq = DateUtil.year(fbrqDate) + "年" + (DateUtil.month(fbrqDate) + 1) + "月" + DateUtil.dayOfMonth(fbrqDate) + "日";

        //通报数据日期
        String tbrqParam = MapUtil.getStr(params, "tbrq");
        Date tbrqDate = DateUtil.parse(tbrqParam);
        String tbrq = DateUtil.year(tbrqDate) + "年" + (DateUtil.month(tbrqDate) + 1) + "月" + DateUtil.dayOfMonth(tbrqDate) + "日";
        //通报日期无年
        String tbrqMonthAndDay = (DateUtil.month(tbrqDate) + 1) + "月" + DateUtil.dayOfMonth(tbrqDate) + "日";

        DateTime tbrqDateEnd = DateUtil.offset(tbrqDate, DateField.DAY_OF_MONTH, 1);
        //通报数据日期截止
        String tbrqEnd = DateUtil.year(tbrqDateEnd) + "年" + (DateUtil.month(tbrqDateEnd) + 1) + "月" + DateUtil.dayOfMonth(tbrqDateEnd) + "日";

        //期数
        String qs = MapUtil.getStr(params, "qs");

        //根据抽取日期获取当天最近一次的巡检日期
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

        Date d = DateUtil.parse(dmTime);
        String dmTimeFormat = "";
        if (d != null) {
            int monthInt = DateUtil.month(d) + 1;
            dmTimeFormat = monthInt + "月" + DateUtil.dayOfMonth(d) + "日" + DateUtil.hour(d, true) + "时" + DateUtil.minute(d) + "分";

        }

        //应急点名
        QueryWrapper<YjdmEntity> wrapper = new QueryWrapper<YjdmEntity>().like("dm_time", tbrqParam);
        List<YjdmEntity> records = yjdmService.list(wrapper);
        Map<String, Object> yjdmMap = records
                .stream().collect(Collectors.toMap(YjdmEntity::getDeptId, YjdmEntity::getYd));

        yjdmMap.forEach((k, v) ->{
            if (Integer.parseInt(String.valueOf(v)) == 1) {
                yjdmMap.put(k,"√");
            }else{
                yjdmMap.put(k,"×");
            }
        });
        String yjDmTimeFormat = "";
        if (CollectionUtil.isNotEmpty(records)) {
            Date yjDmTime = records.get(0).getDmTime();
            yjDmTimeFormat = (DateUtil.month(yjDmTime) + 1) + "月" + DateUtil.dayOfMonth(yjDmTime) + "日" + DateUtil.hour(yjDmTime, true) + "时" + DateUtil.minute(yjDmTime) + "分";
        }

        //移动接处警
        params.put("startDate",tbrqParam);
        List<YdjcjEntity> ydjcjList = ydjcjService.queryByDate(params);

        //设置参数
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

        String fileName = fbrq + "通报.docx";
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

    //分页获取每日110数据
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

    //新增每日通报

    @ResponseBody
    @GetMapping("/addMrtb")
    @SysLog("生成每日通报")
    public R addMrtb(@RequestParam Map<String, Object> params) {
        logger.info("[addMrtb] params={}", params);
        String tbrqParam = MapUtil.getStr(params, "tbrq");

        //判断该日期是否已经有通报
        ZhMrtb exsitedMrtb = mrtbService.getOne(new QueryWrapper<ZhMrtb>().eq("tbrq", tbrqParam).last("limit 1"));
        if (exsitedMrtb != null) {
            return R.error("该通报日期已经存在记录");
        }

        //从yesterday获取警情数据
        params.put("yesterdayId", StrUtil.replace(tbrqParam, "-", ""));
        Map<String, Object> yesterdayMap = mrtbService.getYesterday(params);

        if (MapUtil.isEmpty(yesterdayMap)) {
            return R.error("该通报日期警情数据存在异常");
        }

        String yesterdayStr = MapUtil.getStr(yesterdayMap, "CaseDesc");
        if (StrUtil.isEmpty(yesterdayStr)) {
            return R.error("该通报日期警情数据存在异常");
        }

        ZhMrtb zhMrtb = new ZhMrtb();
        zhMrtb.setCreateBy(getUser().getName());
        zhMrtb.setFbrq(DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));

        //判断通报日期是否是每年的第一天
        String firstDateOfYear = DateUtil.format(DateUtil.beginOfYear(DateUtil.parseDate(tbrqParam)), DatePattern.NORM_DATE_FORMAT);
        if (tbrqParam.equals(firstDateOfYear)) {
            zhMrtb.setQs("1");
        } else {
            //去上一期的日期
            ZhMrtb lastMrtb = mrtbService.getOne(new QueryWrapper<ZhMrtb>().eq("tbrq", DateUtil.offsetDay(DateUtil.parseDate(tbrqParam).toJdkDate(), -1)).last("limit 1"));
            String lastQs = "";
            try {
                if (lastMrtb != null) {
                    lastQs = String.valueOf(Integer.valueOf(lastMrtb.getQs()).intValue() + 1);
                }

            } catch (Exception e) {
                //获取当前日期在今年的天数

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

        //刑事警情
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//注意月份是MM
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

        //判断类别
        for (JcjCjxx jcjCjxx : jcjcjxxList) {
            switch(jcjCjxx.getCjlb()){
                //入室盗窃
                case "盗窃民宅":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;
                case "盗窃单位":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;
                case "盗窃商店":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;

                //诈骗
                case "诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "调包诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "设摊诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "假币假劵":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "信用卡诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "网络诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "短信诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "电话诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "拾物平分诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "碰瓷诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "使用假票据诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "贩卖假货":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "借打手机诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "招工征聘诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "宝物诈骗案":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "迷信诈骗案":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "其他诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;

                //盗窃非机动车
                case "盗窃自行车":
                    zhMrtb.setXsjq03(zhMrtb.getXsjq03() != null ? zhMrtb.getXsjq03() + 1 : 1);
                    break;
                case "盗窃电动自行车":
                    zhMrtb.setXsjq03(zhMrtb.getXsjq03() != null ? zhMrtb.getXsjq03() + 1 : 1);
                    break;

                //其它
                default:
                    zhMrtb.setXsjq99(zhMrtb.getXsjq99() != null ? zhMrtb.getXsjq99() + 1 : 1);
                    break;

            }
        }

        //sys_config表中的工作提示
        String comment3 = sysConfigService.getValue("gzts");
        zhMrtb.setComment3(comment3);

        boolean issaved = mrtbService.saveCC(zhMrtb, jcjcjxxList);

        if (issaved) {
            return R.ok().put("data", zhMrtb.getId());
        } else {
            return R.error("添加失败，请联系管理员");
        }
    }

    //获取每日110刑事警情数据
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

    //更新每日通报
    @ResponseBody
    @PostMapping("/updateMrtb")
    @SysLog("更新每日通报")
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
            return R.error("修改失败，请联系管理员");
        }
    }

    //删除每日通报
    @ResponseBody
    @GetMapping("/deleteMrtb")
    @SysLog("删除每日通报")
    public R deleteMrtb(@RequestParam String id) {
        logger.info("[deleteMrtb] id={}", id);
        mrtbService.removeById(id);
        mrtbXsjqService.remove(new QueryWrapper<ZhMrtbXsjq>().eq("tbid", id));
        return R.ok();
    }


    //根据id获取110数据详情
    @ResponseBody
    @GetMapping("/queryById")
    public R queryById(@RequestParam String id, String deptId) {
        logger.info("[queryById] id={}, deptId={}", id, deptId);
        Map<String, Object> params = new HashMap<>();

        ZhMrtb zhMrtb = mrtbService.getById(id);

        //发布日期
        String fbrqParam = zhMrtb.getFbrq();
        String fbrq = null;
        if (StrUtil.isNotEmpty(fbrqParam)) {
            Date fbrqDate = DateUtil.parse(fbrqParam);
            fbrq = DateUtil.year(fbrqDate) + "年" + (DateUtil.month(fbrqDate) + 1) + "月" + DateUtil.dayOfMonth(fbrqDate) + "日";
        }

        //通报数据日期
        String tbrqParam = zhMrtb.getTbrq();
        Date tbrqDate = DateUtil.parse(tbrqParam);
        String tbrq = DateUtil.year(tbrqDate) + "年" + (DateUtil.month(tbrqDate) + 1) + "月" + DateUtil.dayOfMonth(tbrqDate) + "日";
        //通报日期无年
        String tbrqMonthAndDay = (DateUtil.month(tbrqDate) + 1) + "月" + DateUtil.dayOfMonth(tbrqDate) + "日";

        DateTime tbrqDateEnd = DateUtil.offset(tbrqDate, DateField.DAY_OF_MONTH, 1);
        //通报数据日期截止
        String tbrqEnd = DateUtil.year(tbrqDateEnd) + "年" + (DateUtil.month(tbrqDateEnd) + 1) + "月" + DateUtil.dayOfMonth(tbrqDateEnd) + "日";

        //期数
        String qs = zhMrtb.getQs();

        //根据抽取日期获取当天最近一次的巡检日期
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

        Date d = DateUtil.parse(dmTime);
        String dmTimeFormat = "";
        if (d != null) {
            int monthInt = DateUtil.month(d) + 1;
            dmTimeFormat = monthInt + "月" + DateUtil.dayOfMonth(d) + "日" + DateUtil.hour(d, true) + "时" + DateUtil.minute(d) + "分";

        }

        //应急点名
        QueryWrapper<YjdmEntity> wrapper = new QueryWrapper<YjdmEntity>().like("dm_time", tbrqParam);
        List<YjdmEntity> records = yjdmService.list(wrapper);
        Map<String, Object> yjdmMap = records
                .stream().collect(Collectors.toMap(YjdmEntity::getDeptId, YjdmEntity::getYd, (oldValue, newValue) -> oldValue));

        yjdmMap.forEach((k, v) -> {
            if (Integer.parseInt(String.valueOf(v)) == 1) {
                yjdmMap.put(k, "√");
            } else {
                yjdmMap.put(k, "×");
            }
        });
        String yjDmTimeFormat = "";
        if (CollectionUtil.isNotEmpty(records)) {
            Date yjDmTime = records.get(0).getDmTime();
            yjDmTimeFormat = (DateUtil.month(yjDmTime) + 1) + "月" + DateUtil.dayOfMonth(yjDmTime) + "日" + DateUtil.hour(yjDmTime, true) + "时" + DateUtil.minute(yjDmTime) + "分";
        }

        //移动接处警
        params.put("startDate", tbrqParam);
        params.put("endDate", tbrqParam);
        List<YdjcjEntity> ydjcjList = ydjcjService.queryByDate(params);

        //接处警
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

        //警情分类统计报表
        String[] jqxAxis;
        String[] jqseries;
        if (MapUtil.isNotEmpty(yesterdayMap)) {
            jqxAxis = new String[]{"接警总数", "刑事警情", "治安警情", "交通事故", "火灾事故", "举报投诉", "群众求助", "纠纷", "经济案件", "其他警情"};
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

                    zhMrtb.getTotalnum(),//接警总数
                    zhMrtb.getNum01(),//刑事警情
                    zhMrtb.getNum02(),//治安警情
                    zhMrtb.getNum03(),//交通事故
                    zhMrtb.getNum04(),//火灾事故
                    zhMrtb.getNum06(),//举报投诉
                    zhMrtb.getNum05(),//群众求助
                    zhMrtb.getNum08(),//纠纷
                    zhMrtb.getNum11(),//经济案件
                    zhMrtb.getNum99()//其他警情
            };
        } else {
            jqxAxis = new String[]{};
            jqseries = new String[]{};
        }

        //警情分派出所统计报表
        //['product', '2015', '2016', '2017'],
        //['Matcha Latte', '43.3', '85.8', '93.7'],
        //['Milk Tea', 83.1, 73.4, 55.1],
        //['Cheese Cocoa', 86.4, 65.2, 82.5],
        //['Walnut Brownie', 72.4, 53.9, 39.1]

        List<List<String>> jqDwArr = new ArrayList<>();
        if (StrUtil.isNotEmpty(yesterdayStr)) {

            List<String> rowList = CollectionUtil.newArrayList("product", "接警总数", "刑事警情", "治安警情");
            List<String> jdList = CollectionUtil.newArrayList("交大", zhMrtb.getJd01(), zhMrtb.getJd02(), zhMrtb.getJd03());
            List<String> hsList = CollectionUtil.newArrayList("邗上", zhMrtb.getHs01(), zhMrtb.getHs02(), zhMrtb.getHs03());
            List<String> chList = CollectionUtil.newArrayList("汊河", zhMrtb.getCh01(), zhMrtb.getHs02(), zhMrtb.getCh03());
            List<String> gzList = CollectionUtil.newArrayList("瓜洲", zhMrtb.getGz01(), zhMrtb.getGz02(), zhMrtb.getGz03());
            List<String> jwList = CollectionUtil.newArrayList("蒋王", zhMrtb.getJw01(), zhMrtb.getJw02(), zhMrtb.getJw03());
            List<String> ymList = CollectionUtil.newArrayList("杨庙", zhMrtb.getYm01(), zhMrtb.getYm02(), zhMrtb.getYm03());
            List<String> hssList = CollectionUtil.newArrayList("槐泗", zhMrtb.getHss01(), zhMrtb.getHss02(), zhMrtb.getHss03());
            List<String> fxList = CollectionUtil.newArrayList("方巷", zhMrtb.getFx01(), zhMrtb.getFx02(), zhMrtb.getFx03());
            List<String> gdList = CollectionUtil.newArrayList("公道", zhMrtb.getGd01(), zhMrtb.getGd02(), zhMrtb.getGd03());
            List<String> ysList = CollectionUtil.newArrayList("杨寿", zhMrtb.getYs01(), zhMrtb.getYs02(), zhMrtb.getYs03());
            List<String> sjyList = CollectionUtil.newArrayList("四季园", zhMrtb.getSjy01(), zhMrtb.getSjy02(), zhMrtb.getSjy03());
            List<String> nsqList = CollectionUtil.newArrayList("念四桥", zhMrtb.getNsq01(), zhMrtb.getNsq02(), zhMrtb.getNsq03());
            List<String> xsList = CollectionUtil.newArrayList("新盛", zhMrtb.getXs01(), zhMrtb.getXs02(), zhMrtb.getXs03());
            List<String> zxList = CollectionUtil.newArrayList("竹西", zhMrtb.getZx01(), zhMrtb.getZx02(), zhMrtb.getZx03());
            List<String> xhList = CollectionUtil.newArrayList("西湖", zhMrtb.getXh01(), zhMrtb.getXh02(), zhMrtb.getXh03());
            List<String> jyList = CollectionUtil.newArrayList("江阳", zhMrtb.getJy01(), zhMrtb.getJy02(), zhMrtb.getJy03());
            List<String> gqList = CollectionUtil.newArrayList("甘泉", zhMrtb.getGq01(), zhMrtb.getGq02(), zhMrtb.getGq03());

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

        //刑事警情
        //List<ZhMrtbXsjq> zhMrtbXsjqList = mrtbXsjqService.list(new QueryWrapper<ZhMrtbXsjq>().eq("tbid", zhMrtb.getId()).orderByAsc("dw"));


        //派出所登录时点击jilu
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

        //设置参数
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("qs", qs);//期数
        map.put("fbrq", fbrq);//发布日期
        map.put("tbrqStart", tbrq);//通报日期开始 2020年5月20号7点
        map.put("tbrqEnd", tbrqEnd);//通报日期结束 2020年5月21号7点
        map.put("tbrqmd", tbrqMonthAndDay);//通报日期月和日  5月12号
        map.put("pcs16List", pcs16ResultList);//16个派出所点名
        map.put("zd5List", otherResultList);//5个中队点名
        map.put("xtjList", xtjResultList);//巡特警点名
        map.put("dmtime", dmTimeFormat);//点名时间
        map.put("yjdm", yjdmMap);//应急点名
        map.put("yjdmtime", yjDmTimeFormat);//应急点名时间
        map.put("ydjcjList", ydjcjList);//移动接处警
        map.put("jqStr", jqStr);//警情描述
        map.put("jqxAxis", jqxAxis);//分类警情图标
        map.put("jqseries", jqseries);//分警情图标
        map.put("jqDwArr", jqDwArr);//分派出所警情图标
        map.put("mrtb", zhMrtb);//通报的其它信息
        //map.put("zhMrtbXsjqList", zhMrtbXsjqList);//刑事警情列表

        return R.ok().put("data", map);
    }

    //更新每日通报刑事警情
    @ResponseBody
    @PostMapping("/updateMrtbXsjq")
    @SysLog("更新每日通报刑事警情")
    public R updateMrtbXsjq(@RequestBody ZhMrtbXsjq zhMrtbXsjq) {
        logger.info("[updateMrtbXsjq] zhMrtbXsjq={}", zhMrtbXsjq);
        boolean isupdated = mrtbXsjqService.updateById(zhMrtbXsjq);

        //todo 修改mrtb里的相关警情数据
        //mrtbService.calMrtb(zhMrtbXsjq.getTbid());

        if (isupdated) {
            return R.ok();
        } else {
            return R.error("修改失败，请联系管理员");
        }
    }

    //删除每日通报报刑事警情
    @ResponseBody
    @GetMapping("/deleteMrtbXsjq")
    @SysLog("删除每日通报报刑事警情")
    public R deleteMrtbXsjq(@RequestParam String id,@RequestParam int tbid) {
        logger.info("[deleteMrtbXsjq] id={}", id);
        mrtbXsjqService.removeById(id);
        //todo 修改mrtb里的相关警情数据
        mrtbService.calMrtb(tbid);
        return R.ok();
    }

    //添加每日通报刑事警情
    @ResponseBody
    @PostMapping("/addMrtbXsjq")
    @SysLog("添加每日通报刑事警情")
    public R addMrtbXsjq(@RequestBody ZhMrtbXsjq zhMrtbXsjq) {
        logger.info("[addMrtbXsjq] zhMrtbXsjq={}", zhMrtbXsjq);
        boolean isupdated = mrtbXsjqService.save(zhMrtbXsjq);

        //todo 修改mrtb里的相关警情数据
        mrtbService.calMrtb(zhMrtbXsjq.getTbid());

        if (isupdated) {
            return R.ok();
        } else {
            return R.error("添加失败，请联系管理员");
        }
    }

    //根据deptId查看该单位是否查看昨日的通报
    @ResponseBody
    @GetMapping("/queryMrtbView")
    public R queryMrtbView(String deptId) {
        logger.info("[queryMrtbView] deptId={}", deptId);
        Map<String, Object> result = new HashMap<>();

        //生成昨天的日期

        String yesterday = DateUtil.format(DateUtil.yesterday().toJdkDate(), DatePattern.NORM_DATE_FORMAT);

        //先判断有无警情
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

    //根据deptId查看该单位是否查看昨日的通报
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
                //设置IP地址
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
        downLoaFile("短信电话模板.xlsx", response, new File(resource.getPath()));
    }

    //每日通报发送短信通知
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

    //分页查看短信发送历史记录
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

    //删除每日通报
    @ResponseBody
    @GetMapping("/deleteMrtbDh")
    @SysLog("删除每日通报电话")
    public R deleteMrtbDh(@RequestParam String id) {
        logger.info("[deleteMrtbDh] id={}", id);
        mrtbDhService.removeById(id);
        return R.ok();
    }

    //删除每日通报
    @ResponseBody
    @PostMapping("/deleteAllMrtbDh")
    @SysLog("删除所有每日通报电话")
    public R deleteAllMrtbDh(@RequestBody List<ZhMrtbDh> zhMrtbDhList) {
        logger.info("[deleteAllMrtbDh params={}]", zhMrtbDhList);

        if (!mrtbDhService.removeByIds(zhMrtbDhList.stream().map(e -> e.getId()).collect(Collectors.toList()))) {
            throw new RRException("删除电话号码失败");
        }
        return R.ok();
    }

    //每日通报发布
    @ResponseBody
    @PostMapping("/publishMrtb")
    public R publishMrtb(@RequestBody MrtbPublishForm mrtbPublishForm, HttpServletRequest request) {
        logger.info("[publishMrtb] mrtbPublishForm={}", mrtbPublishForm);

        IPUtils.getIpAddr(request);

        ZhMrtb zhMrtb = mrtbService.getById(mrtbPublishForm.getMrtbId());
        if (zhMrtb == null) {
            throw new RRException("通报数据异常");
        }

        TXx exsitedTxx = tXxService.getByIdCC(zhMrtb.getXxId());


        //获取Txx
        if (zhMrtb.getXxId() == null || exsitedTxx == null) {
            //插入t_xx
            TXx tXx = new TXx();
            tXx.setLmid(57);
            tXx.setLbid(225);
            tXx.setFbdw(",,");
            tXx.setBt("指挥中心每日情况通报（第" + zhMrtb.getQs() + "期）");
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
                throw new RRException("新增发布通报失败");
            }

            zhMrtb.setXxId(tXx.getId());


        } else {
            //修改t_xx
            exsitedTxx.setBt("指挥中心每日情况通报（第" + zhMrtb.getQs() + "期）");
            exsitedTxx.setNr(mrtbPublishForm.getPublishContent());
            zhMrtb.setXxId(exsitedTxx.getId());

            logger.info("[publishMrtb] txx: {}", exsitedTxx);

            if (!tXxService.updateByIdCC(exsitedTxx)) {
                throw new RRException("修改发布通报失败");
            }
        }

        //修改mrtb
        zhMrtb.setPublishTime(new Date());
        zhMrtb.setPublishIp(IPUtils.getIpAddr(request));
        zhMrtb.setPublished(1);
        zhMrtb.setPublishContent(mrtbPublishForm.getPublishContent());

        logger.info("[publishMrtb] mrtb: {}", zhMrtb);

        mrtbService.updateById(zhMrtb);

        return R.ok();
    }

}
