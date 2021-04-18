package io.renren.modules.jcj.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.HttpContextUtils;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.MyUtils;
import io.renren.common.utils.R;
import io.renren.modules.jcj.entity.*;
import io.renren.modules.jcj.service.*;
import io.renren.modules.mrtb.entity.ZhMrtbXsjq;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Struct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/*
接警信息
 */
@RestController
@RequestMapping("/cj")
public class CjController extends AbstractController {

    @Autowired
    private CjService cjService;
    @Autowired
    private JqCjViewService jqCjViewService;
    @Autowired
    private JqCjBhgService jqCjBhgService;
    @Autowired
    private JjService jjService;
    @Autowired
    private JqCjXcrService jqCjXcrService;

    private Object lock = new Object();


    //处警信息查询,page=1&limit=10&beforeDate=2020-06-08&afterDate=2020-06-08&dw=32100317
    @ResponseBody
    @GetMapping("/queryCjList")
    public R queryCjList(@RequestParam Map<String, Object> params) {
        logger.info("[queryCjList] params={}", params);
        IPage<JcjCjxx> page = cjService.queryCjList(params);
        String frm = MapUtil.getStr(params, "frm");

        //todo 获取检查信息
        page.getRecords().forEach(e -> {
            e.setJqCjBhgEntity(jqCjBhgService.getOne(new QueryWrapper<JqCjBhgEntity>()
                    .eq(StrUtil.isNotBlank(frm), "frm", frm)
                    .eq("jjbh", e.getJjbh())
                    .and(wrapper -> wrapper.eq("cjbh", e.getCjbh()).or().eq("cjbh", "").or().isNull("cjbh"))
                    .orderByDesc("create_time").last("limit 1")));

            if (e.getJqCjBhgEntity() != null) {
                e.setType(e.getJqCjBhgEntity().getType());
                e.setJcr(e.getJqCjBhgEntity().getJcr());
            }
        });

        String jclv = "";
        if ("1".equals(frm)) {
            long total = page.getTotal();
            long jcs = jqCjBhgService.queryCj(params).stream().mapToLong(e-> (long) e.get("yjcs")).sum();
            if (total == 0) {
                jclv = "0.00%";
            } else {

                jclv = NumberUtil.mul(String.valueOf(NumberUtil.div(jcs, total)), "100").setScale(2, RoundingMode.HALF_UP) + "%";
            }

        }

        return R.ok().put("data", page.getRecords()).put("count", page.getTotal()).put("jclv", jclv);
    }

    //不合格处警信息查询,beforeDate=2020-06-08&afterDate=2020-06-08&dw=32100317
    @ResponseBody
    @GetMapping("/queryIllegalCjxx")
    public R queryIllegalCjxx(@RequestParam Map<String, Object> params) {
        logger.info("[queryIllegalCjxx] params={}", params);
        //不传默认只显示前天的
        if(params.get("beforeDate")==null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//注意月份是MM
            String theDayBeforeTodayDate = simpleDateFormat.format(calendar.getTime());
            params.put("beforeDate",theDayBeforeTodayDate);
            params.put("afterDate",theDayBeforeTodayDate);
        }

        List<JcjCjxx> jcjcjxx = cjService.queryCjListIn3days(params);

        for (int i=jcjcjxx.size()-1;i>=0;i--) {
            JcjCjxx c= cjService.transformCjxx(jcjcjxx.get(i));
            if ("0".equals(c.getP0())
                    &&"0".equals(c.getP1())
                    &&"0".equals(c.getP2())
                    &&"0".equals(c.getP3())
                    &&"0".equals(c.getP4())
                    &&"0".equals(c.getP5())
                    &&"0".equals(c.getP6())
                    &&"0".equals(c.getP7())
                    &&"0".equals(c.getP8())
                    &&"0".equals(c.getP9())
                    &&"0".equals(c.getP10())){
                jcjcjxx.remove(i);
            }
        }

        //手动标注为不合格的jcxx
        //String deptCode = MapUtil.getStr(params, "dw");
        //List<JqCjBhgEntity> jqCjBhgEntityList = jqCjBhgService.list(new QueryWrapper<JqCjBhgEntity>().eq("state", 0).eq(StrUtil.isNotBlank(deptCode), "dept_code", deptCode));
        //StringBuffer sb = new StringBuffer();
        //
        //jqCjBhgEntityList.forEach(e -> {
        //    sb.append(e.getCjbh()).append(",");
        //});
        //
        //params.put("cjbhs", MyUtils.formatSqlIn(sb.toString()));
        //List<JcjCjxx> bhgList = new ArrayList<>();
        //if (StrUtil.isNotBlank(sb)) {
        //    bhgList =  cjService.queryCjListIn3days(params);
        //}
        //
        //for (int i=bhgList.size()-1;i>=0;i--) {
        //    cjService.transformCjxx(bhgList.get(i));
        //    bhgList.get(i).setP99("1");
        //
        //    for (JqCjBhgEntity jqCjBhgEntity : jqCjBhgEntityList) {
        //        if (bhgList.get(i).getCjbh().equals(jqCjBhgEntity.getCjbh())) {
        //            bhgList.get(i).setJqCjBhgEntity(jqCjBhgEntity);
        //        }
        //    }
        //}
        //
        //
        //jcjcjxx.addAll(bhgList);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        int size = jcjcjxx.size();
        List<JcjCjxx> newjcjcjxx =new ArrayList<>();
        if(size<limit){
            newjcjcjxx =jcjcjxx;
        }else{
            if(page<size/limit){
                for(int i=(page-1)*limit;i<page*limit;i++){
                    newjcjcjxx.add(jcjcjxx.get(i));
                }
            }else{
                for(int i=(page-1)*limit;i<size;i++){
                    newjcjcjxx.add(jcjcjxx.get(i));
                }
            }
        }
        return R.ok().put("data", jcjcjxx).put("count",jcjcjxx.size()).put("data",newjcjcjxx);
    }

    //单条处警信息查询
    @ResponseBody
    @GetMapping("/queryCjxx")
    public R queryCjxx(@RequestParam Map<String, Object> params) {
        logger.info("[queryCjxx] params={}", params);
        List<JcjCjxx> jcjcjxx = cjService.queryCjxx(params);
        return R.ok().put("data", cjService.transformCjxx(jcjcjxx.get(0)));
    }

    //接处警提示信息查询
    @ResponseBody
    @GetMapping("/queryHintCount")
    public R queryHintCount(@RequestParam Map<String, Object> params) {
        logger.info("[queryHintCount] params={}", params);
        List<Map<String, Object>> countOf8HourNoPoliceDeal = cjService.countOf8HourNoPoliceDeal(params);
//        List<Map<String, Object>> countOf24HourNoApproval = cjService.countOf24HourNoApproval(params);
        List<Map<String, Object>> countOfUpOrLowLimitIsEmpty = cjService.countOfUpOrLowLimitIsEmpty(params);
        List<Map<String, Object>> countOfWeatherConditionIsEmptyWhileTraffic = cjService.countOfWeatherConditionIsEmptyWhileTraffic(params);
        List<Map<String, Object>> countOfAddressIsEmpty = cjService.countOfAddressIsEmpty(params);
        int countOfLdcj8Djsj = cjService.countOfLdcj8Djsj(params);

        return R.ok().put("countOf8HourNoPoliceDeal", countOf8HourNoPoliceDeal.get(0).get("countOf8HourNoPoliceDeal"))
//                .put("countOf24HourNoApproval", countOf24HourNoApproval.get(0).get("countOf24HourNoApproval"))
                .put("countOfUpOrLowLimitIsEmpty", countOfUpOrLowLimitIsEmpty.get(0).get("countOfUpOrLowLimitIsEmpty"))
                .put("countOfWeatherConditionIsEmptyWhileTraffic", countOfWeatherConditionIsEmptyWhileTraffic.get(0).get("countOfWeatherConditionIsEmptyWhileTraffic"))
                .put("countOfAddressIsEmpty", countOfAddressIsEmpty.get(0).get("countOfAddressIsEmpty"))
                .put("countOfLdcj8Djsj", countOfLdcj8Djsj);
    }

    //接处警首页信息查询
    @ResponseBody
    @GetMapping("/queryIndexInfo")
    public R queryIndexInfo(@RequestParam Map<String, Object> params) {
        logger.info("[queryIndexInfo] params={}", params);
        List<Map<String, Object>> countOfMonthlyJj = cjService.countOfMonthlyJj(params);
        List<Map<String, Object>> countOfDailyJj = cjService.countOfDailyJj(params);
        List<Map<String, Object>> countOfMonthlyCj = cjService.countOfMonthlyCj(params);
        List<Map<String, Object>> countOfDailyCj = cjService.countOfDailyCj(params);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//注意月份是MM
        String theDayBeforeTodayDate = simpleDateFormat.format(calendar.getTime());

        params.put("beforeDate",theDayBeforeTodayDate);
        params.put("afterDate",theDayBeforeTodayDate);
        List<JcjCjxx> jcjcjxx = cjService.queryCjListIn3days(params);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date1 = calendar.getTime();

        calendar.setTime(new Date());
//        calendar.add(Calendar.DATE, -1);
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        calendar.set(Calendar.HOUR_OF_DAY, 12);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
        Date date2 = calendar.getTime();

        List<DateTime> range = DateUtil.rangeToList(date1,date2, DateField.DAY_OF_MONTH);
        range.remove(range.size()-1);
        List<String> xAxis1 =new ArrayList<>();
        List<String> yAxis1 =new ArrayList<>();
        for (DateTime d: range) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String jjrqsj = formatter.format(d);
            List<Map<String, Object>> countOfEverydayJj = cjService.countOfEverydayJj(jjrqsj);
            xAxis1.add(jjrqsj.startsWith("0")?jjrqsj.substring(6,8).replace("0","")+"号":jjrqsj.substring(6,8)+"号");
            yAxis1.add(String.valueOf(countOfEverydayJj.get(0).get("countOfEverydayJj")));
        }

        List<Map<String, Object>> countOfMonthlyCjlbAnalysis = cjService.countOfMonthlyCjlbAnalysis();
        //01刑事警情，02治安警情，03交通警情，04火灾，05群众求助，06举报投诉，07事件，08纠纷，09灾害事故，10其他行政违法
        List<String> xAxis2 =new ArrayList<>();
        List<Map> yAxis2 =new ArrayList<>();
        xAxis2.add("刑事警情");
        xAxis2.add("治安警情");
        xAxis2.add("交通警情");
        xAxis2.add("火灾");
        xAxis2.add("群众求助");
        xAxis2.add("举报投诉");
        xAxis2.add("事件");
        xAxis2.add("纠纷");
        xAxis2.add("灾害事故");
        xAxis2.add("其他行政违法");

        String symbol="";
        for (Map<String, Object> m:countOfMonthlyCjlbAnalysis) {
            HashMap<String, Object> map = new HashMap<>();
            switch (String.valueOf(m.get("a2"))){
                case "01":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","刑事警情");
                    yAxis2.add(map);
                    symbol+="+01";
                    break;
                case  "02":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","治安警情");
                    yAxis2.add(map);
                    symbol+="+02";
                    break;
                case  "03":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","交通警情");
                    yAxis2.add(map);
                    symbol+="+03";
                    break;
                case  "04":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","火灾");
                    yAxis2.add(map);
                    symbol+="+04";
                    break;
                case  "05":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","群众求助");
                    yAxis2.add(map);
                    symbol+="+05";
                    break;
                case  "06":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","举报投诉");
                    yAxis2.add(map);
                    symbol+="+06";
                    break;
                case  "07":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","事件");
                    yAxis2.add(map);
                    symbol+="+07";
                    break;
                case  "08":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","纠纷");
                    yAxis2.add(map);
                    symbol+="+08";
                    break;
                case  "09":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","灾害事故");
                    yAxis2.add(map);
                    symbol+="+09";
                    break;
                case  "10":
                    map.put("value",Integer.parseInt(String.valueOf(m.get("a1"))));
                    map.put("name","其他行政违法");
                    yAxis2.add(map);
                    symbol+="+10";
                    break;
            }
        }
        if(!symbol.contains("+01")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","刑事警情");
            yAxis2.add(map);
        }
        if(!symbol.contains("+02")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","治安警情");
            yAxis2.add(map);
        }
        if(!symbol.contains("+03")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","交通警情");
            yAxis2.add(map);
        }
        if(!symbol.contains("+04")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","火灾");
            yAxis2.add(map);
        }
        if(!symbol.contains("+05")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","群众求助");
            yAxis2.add(map);
        }
        if(!symbol.contains("+06")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","举报投诉");
            yAxis2.add(map);
        }
        if(!symbol.contains("+07")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","事件");
            yAxis2.add(map);
        }
        if(!symbol.contains("+08")){
            HashMap<String, Object> map = new HashMap<>();

            map.put("value",0);
            map.put("name","纠纷");
            yAxis2.add(map);
        }
        if(!symbol.contains("+09")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","灾害事故");
            yAxis2.add(map);
        }
        if(!symbol.contains("+10")){
            HashMap<String, Object> map = new HashMap<>();
            map.put("value",0);
            map.put("name","其他行政违法");
            yAxis2.add(map);
        }

        for (int i=jcjcjxx.size()-1;i>=0;i--) {
            JcjCjxx c= cjService.transformCjxx(jcjcjxx.get(i));
            if ("0".equals(c.getP0())
                    &&"0".equals(c.getP1())
                    &&"0".equals(c.getP2())
                    &&"0".equals(c.getP3())
                    &&"0".equals(c.getP4())
                    &&"0".equals(c.getP5())
                    &&"0".equals(c.getP6())
                    &&"0".equals(c.getP7())
                    &&"0".equals(c.getP8())
                    &&"0".equals(c.getP9())
                    &&"0".equals(c.getP10())){
                jcjcjxx.remove(i);
            }
        }
        return R.ok().put("countOfIllegalCjxx",jcjcjxx.size())
                .put("countOfMonthlyJj", countOfMonthlyJj.get(0).get("countOfMonthlyJj"))
                .put("countOfDailyJj", countOfDailyJj.get(0).get("countOfDailyJj"))
                .put("countOfMonthlyCj", countOfMonthlyCj.get(0).get("countOfMonthlyCj"))
                .put("countOfDailyCj", countOfDailyCj.get(0).get("countOfDailyCj"))
                .put("xAxis1",xAxis1)
                .put("yAxis1",yAxis1)
                .put("xAxis2",xAxis2)
                .put("yAxis2",yAxis2);
//                .put("countOfMonthlyCjlbAnalysis",countOfMonthlyCjlbAnalysis);
    }

    //每日110刑事警情查询,rq="2020-06-04"
    @ResponseBody
    @GetMapping("/queryYesterdayXSJQ")
    public R queryYesterdayXSJQ(@RequestParam Map<String, Object> params) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//注意月份是MM
            Date date = simpleDateFormat.parse(String.valueOf(params.get("rq")));
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH,1);
            Date afterDate = c.getTime();
            params.put("beforeDate", sdf.format(date) + "07");
            params.put("date", sdf.format(afterDate) + "07");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<JcjCjxx> jcjcjxx = cjService.queryYesterdayXSJQ(params);

        return R.ok().put("data", jcjcjxx);
    }
    //每日110涉黑恶警情查询，rq="2020-06-04"
    @ResponseBody
    @GetMapping("/queryYesterdaySHSE")
    public R queryYesterdaySHSE(@RequestParam Map<String, Object> params) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//注意月份是MM
            Date date = simpleDateFormat.parse(String.valueOf(params.get("rq")));
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH,1);
            Date afterDate = c.getTime();
            params.put("beforeDate",sdf.format(date)+"07");
            params.put("date",sdf.format(afterDate)+"07");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<JcjCjxx> jcjcjxx = cjService.queryYesterdaySHSE(params);
        return R.ok().put("data", jcjcjxx);
    }


    //派出所 不合格处警 记录该处警已查看
    @ResponseBody
    @GetMapping("/addJqCjView")
    public R addJqCjView(@RequestParam String cjbh, @RequestParam String deptId, @RequestParam String deptCode) {

        //JqCjViewEntity exsitedJqCj = jqCjViewService.getOne(new QueryWrapper<JqCjViewEntity>().eq("cjbh", cjbh).eq("dept_id", deptId).last("limit 1"));
        //if (exsitedJqCj == null) {
        //    //获取request
        //    HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //    //设置IP地址
        //    jqCjViewService.save(JqCjViewEntity.builder().cjbh(cjbh).deptId(deptId).deptCode(deptCode).ip(IPUtils.getIpAddr(request)).build());
        //}

        return R.ok();
    }


    //查看派出所当天未查看的不合格处警数量
    @ResponseBody
    @GetMapping("/queryJqCjView")
    public R queryJqCjView(@RequestParam String deptId,@RequestParam String deptCode) {

        return R.ok().put("data", 0);
    }


    //703 手动添加不合格警情
    @ResponseBody
    @PostMapping("/addJqCjBhg")
    @SysLog("手动添加不合格警情")
    public R addJqCjBhg(@RequestBody JqCjBhgEntity jqCjBhgEntity) {


        return R.ok();

    }

    //派出所 手动更新不合格警情
    @ResponseBody
    @GetMapping("/updateJqCjBhg")
    @SysLog("派出所手动更新不合格警情")
    public R updateJqCjBhg(@RequestParam String id) {

        JqCjBhgEntity jqCjBhgEntity = jqCjBhgService.getById(id);
        if (jqCjBhgEntity == null) {
            throw new RRException("数据异常，请离线管理员");
        }

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        jqCjBhgEntity.setUpdateBy(getUser().getName());
        jqCjBhgEntity.setUpdateTime(new Date());
        jqCjBhgEntity.setState(1);
        jqCjBhgEntity.setIp(IPUtils.getIpAddr(request));

        jqCjBhgService.updateById(jqCjBhgEntity);

        return R.ok();

    }

    //############################################################################

    //703 手动添加不合格警情new
    @ResponseBody
    @PostMapping("/addJqCjBhgNew")
    @SysLog("手动添加不合格警情")
    public synchronized R addJqCjBhgNew(@RequestBody JqCjBhgEntity jqCjBhgEntity) {

        synchronized (lock) {
//            try {
//                Thread.sleep(10000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //检查jjbh和frm的数据是否重复，避免重复提交
            int ifexsited = jqCjBhgService.count(
                    new QueryWrapper<JqCjBhgEntity>()
                            .eq("jjbh", jqCjBhgEntity.getJjbh())
//                            .eq("cjbh", jqCjBhgEntity.getCjbh())
                            .eq("frm", jqCjBhgEntity.getFrm())
            );

            //检查处警编号不同，但接警编号相同的，确保一条接警编号多条处警编号的所有数据都可以提交
            int ifexsitedOneJjAndTwoCj = jqCjBhgService.count(
                    new QueryWrapper<JqCjBhgEntity>()
                            .eq("jjbh", jqCjBhgEntity.getJjbh())
                            .eq("cjbh", jqCjBhgEntity.getCjbh())
                            .eq("frm", jqCjBhgEntity.getFrm())
            );

            if (ifexsited > 0 && ifexsitedOneJjAndTwoCj > 0) {
                logger.info("[addJqCjBhgNew] repeated jjbh and cjbh, data: {}", jqCjBhgEntity);
                throw new RRException("请勿重复添加");
            }

            List<String> typeList = jqCjBhgEntity.getTypeList();
            if (CollectionUtil.isEmpty(typeList)) {
                jqCjBhgEntity.setType("-1");//-1正常
            } else if (typeList.size() == 1 && "-1".equals(typeList.get(0))) {
                jqCjBhgEntity.setType("-1");//-1正常
            } else {
                StringBuffer typeSb = new StringBuffer();
                typeList.forEach(e -> {
                    typeSb.append(e).append(",");
                });
                jqCjBhgEntity.setType(StrUtil.subBefore(typeSb, ",", true));
            }

            jqCjBhgEntity.setCreateway(0);
            jqCjBhgEntity.setIp(getIp());
            jqCjBhgEntity.setCreateBy(getUser().getName());
            jqCjBhgEntity.setJjsj(DateUtil.format(DateUtil.parse(jqCjBhgEntity.getJjsj(), "yyyy/MM/dd HH:mm"), "yyyy-MM-dd HH:mm:ss"));

            int doubleCheck = 0;

            if (!"-1".equals(jqCjBhgEntity.getType())) {
                //todo 这里根据jjbh获取
                List<JqCjBhgEntity> jqCjBhgEntityList = jqCjBhgService.list(new QueryWrapper<JqCjBhgEntity>().eq("jjbh", jqCjBhgEntity.getJjbh()));

                if (jqCjBhgEntity.getFrm() == 0) {
                    for (JqCjBhgEntity e : jqCjBhgEntityList) {
                        if (e.getFrm() == 1 && !"-1".equals(e.getType())) {
                            doubleCheck = 1;
                            e.setDoubleCheck(1);
                            jqCjBhgService.updateById(e);
                        }
                    }
                }

                if (jqCjBhgEntity.getFrm() == 1) {
                    for (JqCjBhgEntity e : jqCjBhgEntityList) {
                        if (e.getFrm() == 0 && !"-1".equals(e.getType())) {
                            doubleCheck = 1;
                            e.setDoubleCheck(1);
                            jqCjBhgService.updateById(e);
                        }
                    }
                }
            }

            jqCjBhgEntity.setDoubleCheck(doubleCheck);
            if (!jqCjBhgService.save(jqCjBhgEntity)) {
                throw new RRException("新增不合格警情失败");
            }
        }

        return R.ok();

    }

    //703 手动添加不合格警情new
    @ResponseBody
    @PostMapping("/addJqCjBhgNewWithUpdate")
    @SysLog("手动添加不合格警情")
    public R addJqCjBhgNewWithUpdate(@RequestBody JqCjBhgEntity jqCjBhgEntity) {

//        try {
//            Thread.sleep(3000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<String> typeList = jqCjBhgEntity.getTypeList();
        if (CollectionUtil.isEmpty(typeList)) {
            jqCjBhgEntity.setType("-1");//-1正常
        } else if (typeList.size() == 1 && "-1".equals(typeList.get(0))) {
            jqCjBhgEntity.setType("-1");//-1正常
        } else {
            StringBuffer typeSb = new StringBuffer();
            typeList.forEach(e -> {
                typeSb.append(e).append(",");
            });
            jqCjBhgEntity.setType(StrUtil.subBefore(typeSb, ",", true));
        }

        int doubleCheck = 0;

        if (!"-1".equals(jqCjBhgEntity.getType())) {
            //todo 这里根据jjbh获取
            List<JqCjBhgEntity> jqCjBhgEntityList = jqCjBhgService.list(new QueryWrapper<JqCjBhgEntity>().eq("jjbh", jqCjBhgEntity.getJjbh()));

            if (jqCjBhgEntity.getFrm() == 0) {
                for (JqCjBhgEntity e : jqCjBhgEntityList) {
                    if (e.getFrm() == 1 && !"-1".equals(e.getType())) {
                        doubleCheck = 1;
                        e.setDoubleCheck(1);
                        jqCjBhgService.updateById(e);
                    }
                }
            }

            if (jqCjBhgEntity.getFrm() == 1) {
                for (JqCjBhgEntity e : jqCjBhgEntityList) {
                    if (e.getFrm() == 0 && !"-1".equals(e.getType())) {
                        doubleCheck = 1;
                        e.setDoubleCheck(1);
                        jqCjBhgService.updateById(e);
                    }
                }
            }
        }

        jqCjBhgEntity.setDoubleCheck(doubleCheck);
        if (!jqCjBhgService.updateById(jqCjBhgEntity)) {
            throw new RRException("修改不合格警情失败");
        }

        return R.ok();

    }


    //不合格处警信息查询new, startDate=2020-04-02&endDate=2020-04-12&dw=32100317
    @ResponseBody
    @GetMapping("/queryIllegalCjxxNew")
    public R queryIllegalCjxxNew(@RequestParam Map<String, Object> params) {
        logger.info("[queryIllegalCjxxNew] params={}", params);
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        String type = MapUtil.getStr(params, "type");
        String startDate = MapUtil.getStr(params, "startDate");
        String endDate = MapUtil.getStr(params, "endDate");
//        if (StrUtil.isNotBlank(startDate)) {
//            startDate = DateUtil.format(DateUtil.parseDate(startDate),"yyyy/MM/dd");
//        }
//        if (StrUtil.isNotBlank(startDate)) {
//            endDate = DateUtil.format(DateUtil.parseDate(endDate), "yyyy/MM/dd");
//        }

        StringBuffer likeStr = new StringBuffer();

        if (StrUtil.isNotBlank(type)) {
            String[] typeArr = StrUtil.split(type, ",");
            if (ArrayUtil.isNotEmpty(typeArr)) {
                for (int i = 0; i < typeArr.length; i++) {
                    if (i == 0) {
                        likeStr.append(typeArr[i]);
                    } else {
                        likeStr.append("%").append(typeArr[i]);
                    }
                }
            }
        }

        logger.info("likeStr:{}", likeStr);

        IPage<JqCjBhgEntity> pageParams = new Page<>(page, limit);
        IPage<JqCjBhgEntity> pageResult = jqCjBhgService.page(pageParams,
                new QueryWrapper<JqCjBhgEntity>()
                        .eq(StrUtil.isNotBlank(MapUtil.getStr(params, "frm")),"frm", params.get("frm"))
                        .eq(StrUtil.isNotBlank(MapUtil.getStr(params, "deptCode")), "dept_code", params.get("deptCode"))
                        .eq(StrUtil.isNotBlank(MapUtil.getStr(params, "jjbh")), "jjbh", params.get("jjbh"))
                        .ge(StrUtil.isNotBlank(startDate), "left(jjsj,10)", startDate)
                        .le(StrUtil.isNotBlank(endDate), "left(jjsj,10)", endDate)
                        .like(StrUtil.isNotBlank(likeStr), "type", likeStr)
                        .orderByDesc("type")
                        .orderByDesc("dw")
                        .orderByDesc("jjsj")
        );

        return R.ok().put("data", pageResult.getRecords()).put("count",pageResult.getTotal());
    }


    //派出所/703 手动整改不合格警情
    @ResponseBody
    @GetMapping("/updateJqCjBhgNew")
    @SysLog("派出所手动更新不合格警情")
    public R updateJqCjBhgNew(@RequestParam String id, @RequestParam int state) {

        JqCjBhgEntity jqCjBhgEntity = jqCjBhgService.getById(id);
        if (jqCjBhgEntity == null) {
            throw new RRException("数据异常，请离线管理员");
        }

        jqCjBhgEntity.setState(state);
        if (state == 1) {
            jqCjBhgEntity.setUpdateBy(getUser().getName());
            jqCjBhgEntity.setUpdateTime(new Date());
            jqCjBhgEntity.setIp2(getIp());
        }

        if (!jqCjBhgService.updateById(jqCjBhgEntity)) {
            throw new RRException("修改不合格警情失败");
        }

        return R.ok();

    }

    //当日、当月、当年抽查情况汇总
    @ResponseBody
    @GetMapping("/queryCjStatic")
    public R queryCjStatic(@RequestParam Map<String, Object> params) {

        List<Map<String, Object>> list = jqCjBhgService.queryCjStatic(params, true);

        int ccsTotal = list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("ccs")))).sum();
        int bhgsTotal = list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("bhgs")))).sum();
        double bhglvtal = list.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("bhglv")))).average().getAsDouble();
        int wzgsTotal = list.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("wzgs")))).sum();

        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("ccs", ccsTotal);
        totalMap.put("bhgs", bhgsTotal);
        totalMap.put("bhglv", new BigDecimal(bhglvtal).setScale(2,BigDecimal.ROUND_HALF_UP));
        totalMap.put("wzgs", wzgsTotal);
        totalMap.put("deptname", "合计");
        list.add(totalMap);

        return R.ok().put("data", list);
    }

    //派出所 703异常警情提醒
    @ResponseBody
    @GetMapping("/queryCjNotice")
    public R queryCjNotice(@RequestParam Map<String, Object> params) {

        int state = MapUtil.getInt(params, "state");
        String deptCode = MapUtil.getStr(params, "deptCode");
        List<JqCjBhgEntity> list = jqCjBhgService.list(new QueryWrapper<JqCjBhgEntity>()
                .ne("type", -1)
                .eq("state", state)
                .eq("frm", 0)
                .eq(StrUtil.isNotEmpty(deptCode), "dept_code", deptCode));

        return R.ok().put("data", list.size()).put("list", list);
    }

    //删除抽查警情
    @ResponseBody
    @GetMapping("/deleteJqCjBhgNew")
    @SysLog("删除不合格警情")
    public R deleteJqCjBhgNew(@RequestParam String id) {

        if (!jqCjBhgService.removeById(id)) {
            throw new RRException("删除失败");
        }
        return R.ok();

    }

    @GetMapping("/exportJqkf")
    public void exportJqkf(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportJqkf] params={}", params);

        List<Map<String, Object>> list = jqCjBhgService.queryCjStatic(params, false);
        //计算分数
        list.forEach(e -> {
            // 整改数 / 抽查数 * 15
            if (MapUtil.getInt(e, "ccs") != 0) {
                e.put("zgskf", NumberUtil.mul(NumberUtil.div(MapUtil.getInt(e, "zgs"), MapUtil.getInt(e, "ccs")), 15).setScale(6, BigDecimal.ROUND_HALF_UP));
                e.put("wzgskf", NumberUtil.mul(NumberUtil.div(MapUtil.getInt(e, "wzgs"), MapUtil.getInt(e, "ccs")), 30).setScale(6, BigDecimal.ROUND_HALF_UP));
            } else {
                e.put("zgskf", 0);
                e.put("wzgskf", 0);
            }
            e.put("hjkf", NumberUtil.add(MapUtil.getStr(e, "zgskf"), MapUtil.getStr(e, "wzgskf")).setScale(6, BigDecimal.ROUND_HALF_UP));
            e.put("df", NumberUtil.sub("15", MapUtil.getStr(e, "hjkf")).setScale(6, BigDecimal.ROUND_HALF_UP));
            e.put("empty", "");
        });

        //格式化分数
        list.forEach(e -> {
            if (MapUtil.getStr(e, "zgskf").endsWith("000000")) {
                e.put("zgskf", StrUtil.subBefore(MapUtil.getStr(e, "zgskf"), ".", true));
            }
            if (MapUtil.getStr(e, "wzgskf").endsWith("000000")) {
                e.put("wzgskf", StrUtil.subBefore(MapUtil.getStr(e, "wzgskf"), ".", true));
            }
            if (MapUtil.getStr(e, "hjkf").endsWith("000000")) {
                e.put("hjkf", StrUtil.subBefore(MapUtil.getStr(e, "hjkf"), ".", true));
            }
            if (MapUtil.getStr(e, "df").endsWith("000000")) {
                e.put("df", StrUtil.subBefore(MapUtil.getStr(e, "df"), ".", true));
            }
        });


        int month = DateUtil.month(DateUtil.parseDate((String) params.get("startDate"))) + 1;

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/jq_kf.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());


        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", list);
        excelMap.put("month", month);

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = month +"月警情质量考核通报.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }


    //导出每月警情质量通报
    @GetMapping("/exportJqtb")
    public void exportJqtb(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportJqtb] params={}", params);

        //根据date计算起始日期

        DateTime startDate = DateUtil.parseDate((String) params.get("startDate"));
        DateTime endDate = DateUtil.parseDate((String) params.get("endDate"));
        String tbrqStart = DateUtil.year(startDate) + "年" + (DateUtil.month(startDate) + 1) + "月" + DateUtil.dayOfMonth(startDate) + "日";
        String tbrqEnd = DateUtil.year(endDate) + "年" + (DateUtil.month(endDate) + 1) + "月" + DateUtil.dayOfMonth(endDate) + "日";

        List<JqCjBhgEntity> list = jqCjBhgService.list(new QueryWrapper<JqCjBhgEntity>()
            .eq("frm", 0)
            .ge(StrUtil.isNotBlank(MapUtil.getStr(params, "startDate")), "left(jjsj,10)", params.get("startDate"))
            .le(StrUtil.isNotBlank(MapUtil.getStr(params, "endDate")), "left(jjsj,10)", params.get("endDate"))
        );


        int month = DateUtil.month(DateUtil.parse((String) params.get("startDate"), "yyyy-MM")) + 1;
        int total = list.size();
        int bhgsj = list.stream().filter(e -> !"-1".equals(e.getType())).collect(Collectors.toList()).size();
        int bhgzb = 0;
        if (total == 0) {
            bhgzb = 0;
        } else {
            bhgzb = (int) NumberUtil.mul(NumberUtil.div(bhgsj, total), 100);
        }

        Map<String, List<JqCjBhgEntity>> mapGroupByDw = list.stream().collect(Collectors.groupingBy(e -> e.getDw()));
        List<Map<String, Object>> listStaticDw = new ArrayList<>();

        Map<String, Object> mapStaticDw = null;
        for (Map.Entry<String, List<JqCjBhgEntity>> entry : mapGroupByDw.entrySet()) {

            mapStaticDw = new HashMap<>();
            mapStaticDw.put("dw", entry.getKey());
            mapStaticDw.put("total", entry.getValue() == null ? 0 : entry.getValue().size());
            mapStaticDw.put("hgs", entry.getValue().stream().filter(e -> "-1".equals(e.getType())).count());
            if (MapUtil.getInt(mapStaticDw, "total") != 0) {
                mapStaticDw.put("hglv", NumberUtil.div(MapUtil.getInt(mapStaticDw, "hgs"), MapUtil.getInt(mapStaticDw, "total")));
            } else {
                mapStaticDw.put("hglv", 0);
            }

            listStaticDw.add(mapStaticDw);

        }

        StringBuffer hglv100Dw = new StringBuffer();
        StringBuffer hglhglv90Dwv100Dw = new StringBuffer();
        listStaticDw.stream().filter(e -> MapUtil.getInt(e, "hglv") == 1).collect(Collectors.toList()).forEach(e -> {
            hglv100Dw.append(e.get("dw")).append("、");
        });
        listStaticDw.stream().filter(e -> MapUtil.getDouble(e, "hglv") < 0.9).collect(Collectors.toList()).forEach(e -> {
            hglhglv90Dwv100Dw.append(e.get("dw")).append("、");
        });

        int wzg = 0;
        int wzg1 = 0;
        int wzg2 = 0;
        StringBuffer wzg1SB = new StringBuffer();
        StringBuffer wzg2SB = new StringBuffer();

        List<JqCjBhgEntity> wzgList = list.stream().filter(e -> !"-1".equals(e.getType()) && e.getState() != 2).collect(Collectors.toList());
        List<JqCjBhgEntity> wzg1List = list.stream().filter(e -> !"-1".equals(e.getType()) && (e.getState() == 0 || e.getState() == 3)).collect(Collectors.toList());
        List<JqCjBhgEntity> wzg2List = list.stream().filter(e -> !"-1".equals(e.getType()) && e.getState() == 4).collect(Collectors.toList());
        wzg = wzgList.size();
        wzg1 = wzg1List.size();
        wzg2 = wzg2List.size();

        Map<String, Long> wzg1GroupMap = wzg1List.stream().collect(Collectors.groupingBy(e -> e.getDw(), Collectors.counting()));
        Map<String, Long> wzg2GroupMap = wzg2List.stream().collect(Collectors.groupingBy(e -> e.getDw(), Collectors.counting()));

        for (Map.Entry<String, Long> entry : MapUtil.sort(wzg1GroupMap, (a, b) -> b.compareTo(a)).entrySet()) {
            wzg1SB.append(entry.getKey()).append(entry.getValue()).append("起，");
        }

        for (Map.Entry<String, Long> entry : MapUtil.sort(wzg2GroupMap, (a, b) -> b.compareTo(a)).entrySet()) {
            wzg2SB.append(entry.getKey()).append(entry.getValue()).append("起，");
        }

        //本月出警不合格数据项目中
        StringBuffer data1 = new StringBuffer();//一旦生成即无法整改的数据
        StringBuffer data2 = new StringBuffer();//超过三天无法整改的数据
        StringBuffer data3 = new StringBuffer();//其它问题数据
        StringBuffer data4 = new StringBuffer();//超过24小时无法整改的数据

        Map<String, List<JqCjBhgEntity>> typeListMap = new LinkedHashMap<>();

        List<Map<String, Object>> typeList = new ArrayList<>();
        typeList.add(MapUtil.of("处警类型定性不准确","01"));
        typeList.add(MapUtil.of("处警人填写不规范","02"));
        typeList.add(MapUtil.of("处警人联系方式填写不规范","03"));
        typeList.add(MapUtil.of("发生地点填写不规范","04"));
        typeList.add(MapUtil.of("警情属性定性不准确","05"));
        typeList.add(MapUtil.of("简要警情及处理结果要素填写不全","06"));
        typeList.add(MapUtil.of("简要警情及处理结果填写存在错误","07"));
        typeList.add(MapUtil.of("处警统计信息填写不规范","08"));
        typeList.add(MapUtil.of("损失情况填写不规范","09"));
        typeList.add(MapUtil.of("涉警人员填写不规范","10"));
        typeList.add(MapUtil.of("涉警单位填写不规范","11"));
        typeList.add(MapUtil.of("涉警物品填写不规范","12"));
        typeList.add(MapUtil.of("移送接收单位未填","13"));
        typeList.add(MapUtil.of("移送接收人未填","14"));
        typeList.add(MapUtil.of("移送接收时间未填","15"));
        typeList.add(MapUtil.of("发生原因选择不规范","16"));
        typeList.add(MapUtil.of("事发场所选择不规范","17"));
        typeList.add(MapUtil.of("事发时间上限下限填写错误","18"));
        typeList.add(MapUtil.of("两抢等警情未在两小时内反馈","19"));
        typeList.add(MapUtil.of("违规升格,降格处理警情","20"));
        typeList.add(MapUtil.of("超24小时登记","21"));
        typeList.add(MapUtil.of("超24小时审批","22"));
        typeList.add(MapUtil.of("未处警","23"));
        typeList.add(MapUtil.of("自接警情登记不规范","24"));
        typeList.add(MapUtil.of("警情标注标签输入不规范","25"));
        typeList.add(MapUtil.of("处警反馈时间未填","26"));
        typeList.add(MapUtil.of("天气情况未填写","27"));


        typeList.forEach(e -> {
            String key = IterUtil.getFirst(e.keySet());
            String value = (String) e.values().toArray()[0];
            typeListMap.put(key, list.stream().filter(v -> v.getType().contains(value)).collect(Collectors.toList()));
        });

        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> temp = null;
        Map<String, Long> dwCountMap = null;
        int count1 = 1;
        int count2 = 1;
        int count3 = 1;
        int count4 = 1;
        for (Map.Entry<String, List<JqCjBhgEntity>> entry : typeListMap.entrySet()) {
            if (CollectionUtil.isNotEmpty(entry.getValue())) {

                //一旦生成即无法整改的数据 data1
                //移送接收单位未填 移送接收人未填 移送接收时间未填

                //超过24小时无法整改的数据 data4
                //两抢等警情未在两小时内反馈 超24小时登记 超24小时审批

                //超过三天无法整改的数据 data2
                //损失情况填写不规范 警情标注标签输入不规范 简要警情及处理结果要素填写不全 简要警情及处理结果填写存在错误 处警类型定性不准确

                //其他 data3

                if ("两抢等警情未在两小时内反馈".equals(entry.getKey())
                        || "超24小时登记".equals(entry.getKey())
                        || "超24小时审批".equals(entry.getKey())) {
                    data4.append(count4).append("、").append("\"").append(entry.getKey()).append("\"").append("问题数据").append(entry.getValue().size()).append("起：");
                } else if ("损失情况填写不规范".equals(entry.getKey())
                        || "警情标注标签输入不规范".equals(entry.getKey())
                        || "简要警情及处理结果要素填写不全".equals(entry.getKey())
                        || "简要警情及处理结果填写存在错误".equals(entry.getKey())
                        || "处警类型定性不准确".equals(entry.getKey())) {
                    data2.append(count2).append("、").append("\"").append(entry.getKey()).append("\"").append("问题数据").append(entry.getValue().size()).append("起：");
                } else if ("移送接收单位未填".equals(entry.getKey())
                        || "移送接收人未填".equals(entry.getKey())
                        || "移送接收时间未填".equals(entry.getKey())){
                    data1.append(count1).append("、").append("\"").append(entry.getKey()).append("\"").append("问题数据").append(entry.getValue().size()).append("起：");
                } else {
                    data3.append(count3).append("、").append("\"").append(entry.getKey()).append("\"").append("问题数据").append(entry.getValue().size()).append("起：");
                }

                temp = new HashMap<>();
                dwCountMap = null;

                temp.put("name", entry.getKey());
                temp.put("count", entry.getValue().size());

                dwCountMap = entry.getValue().stream().collect(Collectors.groupingBy(e -> e.getDw(), Collectors.counting()));
                for (Map.Entry<String, Long> entry2 : MapUtil.sort(dwCountMap, (a, b) -> b.compareTo(a)).entrySet()) {


                    if ("两抢等警情未在两小时内反馈".equals(entry.getKey())
                            || "超24小时登记".equals(entry.getKey())
                            || "超24小时审批".equals(entry.getKey())) {
                        data4.append(entry2.getKey()).append(entry2.getValue()).append("起，");
                    } else if ("损失情况填写不规范".equals(entry.getKey())
                            || "警情标注标签输入不规范".equals(entry.getKey())
                            || "简要警情及处理结果要素填写不全".equals(entry.getKey())
                            || "简要警情及处理结果填写存在错误".equals(entry.getKey())
                            || "处警类型定性不准确".equals(entry.getKey())) {
                        data2.append(entry2.getKey()).append(entry2.getValue()).append("起，");
                    } else if ("移送接收单位未填".equals(entry.getKey())
                            || "移送接收人未填".equals(entry.getKey())
                            || "移送接收时间未填".equals(entry.getKey())){
                        data1.append(entry2.getKey()).append(entry2.getValue()).append("起，");
                    } else {
                        data3.append(entry2.getKey()).append(entry2.getValue()).append("起，");
                    }
                }

                if ("两抢等警情未在两小时内反馈".equals(entry.getKey())
                        || "超24小时登记".equals(entry.getKey())
                        || "超24小时审批".equals(entry.getKey())) {
                    data4 = new StringBuffer(StrUtil.subBefore(data4, "，", true)).append(";\r\n");
                    count4++;
                } else if ("损失情况填写不规范".equals(entry.getKey())
                        || "警情标注标签输入不规范".equals(entry.getKey())
                        || "简要警情及处理结果要素填写不全".equals(entry.getKey())
                        || "简要警情及处理结果填写存在错误".equals(entry.getKey())
                        || "处警类型定性不准确".equals(entry.getKey())) {
                    data2 = new StringBuffer(StrUtil.subBefore(data2, "，", true)).append(";\r\n");
                    count2++;
                } else if ("移送接收单位未填".equals(entry.getKey())
                        || "移送接收人未填".equals(entry.getKey())
                        || "移送接收时间未填".equals(entry.getKey())){
                    data1 = new StringBuffer(StrUtil.subBefore(data1, "，", true)).append(";\r\n");
                    count1++;
                } else {
                    data3 = new StringBuffer(StrUtil.subBefore(data3, "，", true)).append(";\r\n");
                    count3++;
                }
                dataList.add(temp);

            }
        }

        //问题数据整改情况表格
        List<Map<String, Object>> wtsjList = jqCjBhgService.queryCjStatic(params, true);

        int ccsTotal = wtsjList.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("ccs")))).sum();
        int bhgsTotal = wtsjList.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("bhgs")))).sum();
        double bhglvtal = wtsjList.stream().mapToDouble(e->Double.parseDouble(String.valueOf(e.get("bhglv")))).average().getAsDouble();
        int wzgsTotal = wtsjList.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("wzgs")))).sum();

        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("ccs", ccsTotal);
        totalMap.put("bhgs", bhgsTotal);
        totalMap.put("bhglv", new BigDecimal(bhglvtal).setScale(2, BigDecimal.ROUND_HALF_UP));
        totalMap.put("wzgs", wzgsTotal);
        totalMap.put("deptname", "合计");
        wtsjList.add(totalMap);

        wtsjList.forEach(e -> {
            e.put("bhglv", e.get("bhglv") + "%");
        });


        Map<String, Object> wordMap = new HashMap<>();
        wordMap.put("month", month);
        wordMap.put("dateRange", tbrqStart + "-" + tbrqEnd);
        wordMap.put("total", list.size());//警情总是
        wordMap.put("bhgsj", bhgsj);//不合格数
        wordMap.put("bhgzb", bhgzb);//不合格占比
        wordMap.put("hglv100Dw", StrUtil.subBefore(hglv100Dw, "、", true));//合格率100的单位
        wordMap.put("hglhglv90Dwv100Dw", StrUtil.subBefore(hglhglv90Dwv100Dw, "、", true));//合格率90的单位
        wordMap.put("wzg", wzg);//未整改
        wordMap.put("wzg1", wzg1);//能整改而未整改
        wordMap.put("wzg2", wzg2);//不能整改
        wordMap.put("data1", StrUtil.subBefore(data1, ";", true) + "。");//一旦生成即无法整改的数据
        wordMap.put("data2", StrUtil.subBefore(data2, ";", true) + "。");//超过三天无法整改的数据
        wordMap.put("data3", StrUtil.subBefore(data3, ";", true) + "。");//气他问题数据
        wordMap.put("data4", StrUtil.subBefore(data4, ";", true) + "。");//超过24小时无法整改的数据
        wordMap.put("wtsjList", wtsjList);//问题数据整改情况表格
        wordMap.put("wzg1DW", StrUtil.subBefore(wzg1SB, "，", true));//能整改而未整改单位
        wordMap.put("wzg2DW", StrUtil.subBefore(wzg2SB, "，", true));//不能整改单位


        String fileName = month + "月全区警情数据质量通报.docx";
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("template/jq_tb.docx");
            XWPFDocument doc = WordExportUtil.exportWord07(resource.getPath(), wordMap);
            if (doc != null) {
                downLoadWord(fileName, response, doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @ResponseBody
    @PostMapping("/upload")
    public R uploadYdjcj(@RequestParam("file") MultipartFile file) throws Exception {

        ImportParams params = new ImportParams();

        //List<JcjCjxxImport> list = ExcelImportUtil.importExcel(file.getInputStream(), JcjCjxxImport.class,params);

        List<Map<String, Object>> list = ExcelImportUtil.importExcel(file.getInputStream(), Map.class,params);

        List<JqCjBhgEntity> jqCjBhgEntityList = new ArrayList<>();
        JqCjBhgEntity jqCjBhgEntity = null;
        StringBuffer typeSb = null;

        for (Map<String, Object> map : list) {

            if (StrUtil.isNotEmpty(MapUtil.getStr(map, "接警编号")) && !"null".equals(MapUtil.getStr(map, "接警编号"))) {

                jqCjBhgEntity = new JqCjBhgEntity();
                jqCjBhgEntity.setJjbh(MapUtil.getStr(map, "接警编号"));
                jqCjBhgEntity.setDw(MapUtil.getStr(map, "分局"));
                jqCjBhgEntity.setRemark(MapUtil.getStr(map, "备注"));
                jqCjBhgEntity.setCreateBy(getUser().getName());
                jqCjBhgEntity.setIp(getIp());
                jqCjBhgEntity.setFrm(0);
                jqCjBhgEntity.setCreateway(1);
                jqCjBhgEntity.setDeptCode(StrUtil.subWithLength(MapUtil.getStr(map, "接警编号"),1,8));

                JcjJjxx jjxx = jjService.queryByJjbh(MapUtil.getStr(map, "接警编号"));
                if (jjxx != null && StrUtil.isNotEmpty(jjxx.getJjrqsj())) {
                    jqCjBhgEntity.setJjsj(DateUtil.format(DateUtil.parse(jjxx.getJjrqsj(), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
                }

                typeSb = new StringBuffer();


                if ("1".equals(MapUtil.getStr(map, "无问题"))) {
                    typeSb.append("-1,");
                } else {

                    if("1".equals(MapUtil.getStr(map, "处警类别定性不准确"))){
                        typeSb.append("01,");
                    }
                    if("1".equals(MapUtil.getStr(map, "处警人填写不规范"))){
                        typeSb.append("02,");
                    }
                    if("1".equals(MapUtil.getStr(map, "处警人联系方式填写不规范"))){
                        typeSb.append("03,");
                    }
                    if("1".equals(MapUtil.getStr(map, "发生地点填写不规范"))){
                        typeSb.append("04,");
                    }
                    if("1".equals(MapUtil.getStr(map, "警情属性定性不准确"))){
                        typeSb.append("05,");
                    }
                    if("1".equals(MapUtil.getStr(map, "简要警情及处理结果要素填写不全"))){
                        typeSb.append("06,");
                    }
                    if("1".equals(MapUtil.getStr(map, "简要警情及处理结果填写存在错误"))){
                        typeSb.append("07,");
                    }
                    if("1".equals(MapUtil.getStr(map, "处警统计信息填写不规范"))){
                        typeSb.append("08,");
                    }
                    if("1".equals(MapUtil.getStr(map, "损失情况填写不规范"))){
                        typeSb.append("09,");
                    }
                    if("1".equals(MapUtil.getStr(map, "涉警人员填写不规范"))){
                        typeSb.append("10,");
                    }
                    if("1".equals(MapUtil.getStr(map, "涉警单位填写不规范"))){
                        typeSb.append("11,");
                    }
                    if("1".equals(MapUtil.getStr(map, "涉警物品填写不规范"))){
                        typeSb.append("12,");
                    }
                    if("1".equals(MapUtil.getStr(map, "移送接收单位未填"))){
                        typeSb.append("13,");
                    }
                    if("1".equals(MapUtil.getStr(map, "移送接收人未填"))){
                        typeSb.append("14,");
                    }
                    if("1".equals(MapUtil.getStr(map, "移送接收时间未填"))){
                        typeSb.append("15,");
                    }
                    if("1".equals(MapUtil.getStr(map, "发生原因选择不规范"))){
                        typeSb.append("16,");
                    }
                    if("1".equals(MapUtil.getStr(map, "事发场所选择不规范"))){
                        typeSb.append("17,");
                    }
                    if("1".equals(MapUtil.getStr(map, "事发时间上限下限填写错误"))){
                        typeSb.append("18,");
                    }
                    if("1".equals(MapUtil.getStr(map, "两抢等重大警情未在两小时内反馈"))){
                        typeSb.append("19,");
                    }
                    if("1".equals(MapUtil.getStr(map, "违规升格、降格处理警情"))){
                        typeSb.append("20,");
                    }
                    if("1".equals(MapUtil.getStr(map, "超24小时登记"))){
                        typeSb.append("21,");
                    }
                    if("1".equals(MapUtil.getStr(map, "超24小时审批"))){
                        typeSb.append("22,");
                    }
                    if("1".equals(MapUtil.getStr(map, "未  处  警"))){
                        typeSb.append("23,");
                    }
                    if("1".equals(MapUtil.getStr(map, "自接警情登记不规范"))){
                        typeSb.append("24,");
                    }
                    if("1".equals(MapUtil.getStr(map, "警情标注标签输入不规范"))){
                        typeSb.append("25,");
                    }
                    if("1".equals(MapUtil.getStr(map, "处警反馈时间未填"))){
                        typeSb.append("26,");
                    }
                    if("1".equals(MapUtil.getStr(map, "天气情况未填写"))){
                        typeSb.append("27,");
                    }

                }

                jqCjBhgEntity.setType(StrUtil.subBefore(typeSb, ",", true));

                jqCjBhgEntityList.add(jqCjBhgEntity);
            }

        }

        for (JqCjBhgEntity cjBhgEntity : jqCjBhgEntityList) {
            int doubleCheck = 0;

            if (!"-1".equals(cjBhgEntity.getType())) {
                //todo 这里根据jjbh获取
                List<JqCjBhgEntity> jqCjBhgEntityList2 = jqCjBhgService.list(new QueryWrapper<JqCjBhgEntity>().eq("jjbh", cjBhgEntity.getJjbh()));

                for (JqCjBhgEntity e : jqCjBhgEntityList2) {
                    if (e.getFrm() == 1 && !"-1".equals(e.getType())) {
                        doubleCheck = 1;
                        e.setDoubleCheck(1);
                        jqCjBhgService.updateById(e);
                    }
                }
            }
            cjBhgEntity.setDoubleCheck(doubleCheck);
        }

        if (!jqCjBhgService.saveBatch(jqCjBhgEntityList)) {
            throw new RRException("导入失败");
        }

        return R.ok();

    }


    //分页 巡查人列表
    @ResponseBody
    @GetMapping("/queryXcr")
    public R queryXcr(@RequestParam Map<String, Object> params) {
        logger.info("[queryXcr] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        String deptCode = MapUtil.getStr(params, "deptCode");

        Page pageParams = new Page(page,limit);

        QueryWrapper<JqCjXcrEntity> queryWrapper = new QueryWrapper<JqCjXcrEntity>().eq("dept_code", deptCode).orderByDesc("id");
        IPage<JqCjXcrEntity> list = jqCjXcrService.page(pageParams,queryWrapper);
        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());

    }

    //新增 巡查人
    @ResponseBody
    @PostMapping("/addXcr")
    public R addXcr(@RequestBody JqCjXcrEntity jqCjXcrEntity, HttpServletRequest request) {
        logger.info("[addXcr] params={}", jqCjXcrEntity);

        jqCjXcrEntity.setCreateBy(getUser().getName());
        if (!jqCjXcrService.save(jqCjXcrEntity)) {
            throw new RRException("新增电动车数据失败");
        }
        return R.ok();

    }

    //删除巡查人
    @ResponseBody
    @GetMapping("/deleteXcr")
    public R deleteXcr(@RequestParam String id) {
        logger.info("[deleteXcr] id={}", id);

        jqCjXcrService.removeById(id);
        return R.ok();
    }

    //导出警情检查率统计
    @GetMapping("/exportCjjcByDate")
    public void exportCjjcByDate(String startDate, String endDate, String dw,HttpServletRequest request, HttpServletResponse response) {

        logger.info("[exportCjjcByDate] startDate={},endDate={}", startDate, endDate);

        Map<String, Object> params = new HashMap<>();
        params.put("beforeDate", startDate);
        params.put("afterDate", endDate);
        params.put("dw", dw);
        params.put("page", 1);
        params.put("limit", 99999);

        //cj list
        List<JcjCjxx> cjList = cjService.queryCjList(params).getRecords();

        List<Map<String, Object>> cjBhgList = jqCjBhgService.queryCj(params);

        cjBhgList.forEach(e -> {
            e.put("zjqs", cjList.stream().filter(f -> e.get("deptCode").equals(f.getDjdw().substring(0, 8))).count());
            if ("0".equals(String.valueOf(e.get("zjqs")))) {
                e.put("jclv", "0.00%");
            } else {
                e.put("jclv", NumberUtil.mul(String.valueOf(NumberUtil.div(String.valueOf(e.get("yjcs")), String.valueOf(e.get("zjqs")))), "100").setScale(2, RoundingMode.HALF_DOWN) + "%");
            }
        });

        CollectionUtil.sort(cjBhgList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return String.valueOf(o2.get("jclv")).compareTo(String.valueOf(o1.get("jclv")));
            }
        });

        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("deptName", "合计");
        totalMap.put("zjqs", cjBhgList.stream().mapToLong(e -> (long) e.get("zjqs")).sum());
        totalMap.put("yjcs", cjBhgList.stream().mapToLong(e -> (long) e.get("yjcs")).sum());
        if ("0".equals(String.valueOf(totalMap.get("zjqs")))) {
            totalMap.put("jclv", "0.00%");
        } else {
            totalMap.put("jclv", NumberUtil.mul(String.valueOf(NumberUtil.div(String.valueOf(totalMap.get("yjcs")), String.valueOf(totalMap.get("zjqs")))), "100").setScale(2, RoundingMode.HALF_DOWN) + "%");
        }

        cjBhgList.add(totalMap);

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", cjBhgList);
        excelMap.put("startDate", startDate);
        excelMap.put("endDate", endDate);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/cjStatistics.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = startDate + "至" + endDate + "警情检查统计.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }


}
