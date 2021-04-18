package io.renren.modules.report.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.utils.R;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.dm.service.YjdmService;
import io.renren.modules.report.service.ReportService;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.video.consts.VideoConsts;
import io.renren.modules.video.entity.XmConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author tomchen
 * @date 2020-05-06
 */
@RestController
@RequestMapping("report")
public class ReportController extends AbstractController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private DmService dmService;

    @Autowired
    private YjdmService yjdmService;

    //报表查询天网巡检数据
    @ResponseBody
    @GetMapping("/queryTwData")
    public R queryTwData(){
        logger.info("[queryTwData] ");

        //获取当前年月
        String date = DateUtil.format(new Date(), "yyyy-MM");

        Map<String,Object> reportMap = reportService.queryTwData(date);

        return R.ok().put("data", reportMap);
    }

    //报表查询自建巡检数据
    @ResponseBody
    @GetMapping("/queryZjData")
    public R queryZjData(@RequestParam Map<String, Object> params){
        logger.info("[queryZjData] params={}", params);

        //获取当前年月
        //String date = DateUtil.format(new Date(), "yyyy-MM");
        String date = MapUtil.getStr(params, "date");

        Map<String,Object> reportMap = reportService.queryZjData(date);

        return R.ok().put("data", reportMap);
    }

    //报表查询值班点名和应急点名
    @ResponseBody
    @GetMapping("/queryDmData")
    public R queryDmData(){
        logger.info("[queryDmData] ");


        //先默认去当前月
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", DateUtil.format(DateUtil.beginOfMonth(new Date()).toJdkDate(), DatePattern.NORM_DATE_FORMAT));
        params.put("endDate", DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));
        params.put("page", 1);
        params.put("limit", 100);//mock page

        List<Map<String,Object>> dmList= dmService.queryDMByDatePage(params).getRecords();
        List<Map<String,Object>> yjdmList= yjdmService.queryYjdmReportDate(params);

        //根据应答率倒序排序
        Collections.sort(dmList,new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double yd1totalPrecent1 = Double.parseDouble(String.valueOf(o1.get("yd4totalPrecent")));
                Double yd1totalPrecent2 = Double.parseDouble(String.valueOf(o2.get("yd4totalPrecent")));

                return yd1totalPrecent2.compareTo(yd1totalPrecent1);
            }
        });

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap;
        int yjdmPre;
        DecimalFormat df = new DecimalFormat("0.##");
        for (Map<String, Object> dmMap : dmList) {
            resultMap = new LinkedHashMap<>();
            resultMap.put("pcs", MapUtil.getStr(dmMap, "shortDeptName"));
            resultMap.put("值班点名", NumberUtil.mul(MapUtil.getStr(dmMap, "yd4totalPrecent"), "100").intValue());
            yjdmPre = 0;
            for (Map<String, Object> yjMap : yjdmList) {
                if (MapUtil.getStr(dmMap, "shortDeptName").equals(MapUtil.getStr(yjMap, "shortDeptName"))) {
                    yjdmPre = NumberUtil.mul(MapUtil.getStr(yjMap, "yd1totalPrecent"), "100").intValue();
                }
            }

            resultMap.put("应急点名", yjdmPre);

            resultList.add(resultMap);
        }


        return R.ok().put("data", resultList);
    }

    //报表查询移动接处警三率数据
    @ResponseBody
    @GetMapping("/queryYdjcjData")
    public R queryYdjcjData(){
        logger.info("[queryYdjcjData] ");

        //获取当前年月
        String date = DateUtil.format(new Date(), "yyyy-MM");

        Map<String,Object> reportMap = reportService.queryYdjcjData(date);

        return R.ok().put("xptjjlv", reportMap.get("xptjjlv"))
                .put("xptcjlv", reportMap.get("xptcjlv"))
                .put("yddcjlv", reportMap.get("yddcjlv"))
                .put("pcs", reportMap.get("pcs"));
    }

    //报表查询移动接处警三率数据
    @ResponseBody
    @GetMapping("/queryYdjcjDataNew")
    public R queryYdjcjDataNew(){
        logger.info("[queryYdjcjDataNew] ");

        //获取当前年月
        String date = DateUtil.format(new Date(), "yyyy-MM");

        List<Map<String,Object>> list = reportService.queryYdjcjDataNew(date);

        return R.ok().put("data", list);
    }

    //绩效打分报表
    @ResponseBody
    @GetMapping("/queryDfData")
    public R queryDfData(@RequestParam Map<String, Object> params){
        logger.info("[queryDfData] params={}", params);

        Map<String, Object> result = reportService.queryDfData(params);

        return R.ok().put("data", result);
    }

    //绩效打分报表递归
    @ResponseBody
    @GetMapping("/queryDfDataNew")
    public R queryDfDataNew(@RequestParam Map<String, Object> params){
        logger.info("[queryDfDataNew] params={}", params);

        Map<String, Object> result = reportService.queryDfDataNew(params);

        return R.ok().put("data", result);
    }

    //绩效打分报表
    @ResponseBody
    @GetMapping("/queryDfDataNull")
    public R queryDfDataNull(@RequestParam Map<String, Object> params){
        logger.info("[queryDfDataNull] params={}", params);

        Map<String, Object> result = reportService.queryDfDataNull(params);

        return R.ok().put("data", result);
    }

    //报表查询情报信息
    @ResponseBody
    @GetMapping("/queryQbxxData")
    public R queryQbxxData(@RequestParam Map<String, Object> params){
        logger.info("[queryQbxxData] params={}", params);

        Map<String,Object> reportMap = reportService.queryQbxxData(params);

        return R.ok().put("s3", reportMap.get("s3"))
                .put("s5", reportMap.get("s5"))
                .put("s7", reportMap.get("s7"))
                .put("pcs", reportMap.get("pcs"));
    }

    //报表查询情报信息null
    @ResponseBody
    @GetMapping("/queryQbxxDataNull")
    public R queryQbxxDataNull(@RequestParam Map<String, Object> params){
        logger.info("[queryQbxxDataNull] params={}", params);

        Map<String,Object> reportMap = reportService.queryQbxxDataNull(params);

        return R.ok().put("s3", reportMap.get("s3"))
                .put("s5", reportMap.get("s5"))
                .put("s7", reportMap.get("s7"))
                .put("pcs", reportMap.get("pcs"));
    }

    //报表查询每日台账
    @ResponseBody
    @GetMapping("/queryMrtzData")
    public R queryMrtzData(@RequestParam Map<String, Object> params){
        logger.info("[queryMrtzData] params={}", params);

        Map<String, Object> result = reportService.queryMrtzData(params);

        return R.ok().put("data", result);
    }

}
