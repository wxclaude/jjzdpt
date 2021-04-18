package io.renren.modules.dm.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
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
import io.renren.modules.dm.entity.YjdmEntity;
import io.renren.modules.dm.service.DmService;
import io.renren.modules.dm.service.YjdmService;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

//应急点名
@RestController
@RequestMapping("/yjdm")
public class YjdmController extends AbstractController {

    @Autowired
    private YjdmService yjdmService;

    @ResponseBody
    @PostMapping("/addYjdmBatch")
    @SysLog("应急点名")
    public R addYjdmBatch(@RequestBody List<YjdmEntity> yjdmEntityList, HttpServletRequest request){
        SysUserEntity user = getUser();
        Date date = new Date();
        String remoteAddr = request.getRemoteAddr();
        logger.info("[addYjdmBatch] user={}, rem" + "oteAddr={}, data={}", user.getName(), remoteAddr, JSONUtil.toJsonStr(yjdmEntityList));

        yjdmEntityList.forEach( yjdmEntity -> {
            yjdmEntity.setDmPolice(user.getName());
            yjdmEntity.setDmTime(date);
            yjdmEntity.setIp(remoteAddr);
        });
        yjdmService.addYjdmBatch(yjdmEntityList);
        return R.ok();
    }

    @ResponseBody
    @PostMapping("/upateYjdmBatch")
    @SysLog("修改应急点名")
    public R upateYjdmBatch(@RequestBody List<YjdmEntity> yjdmEntityList){
        logger.info("[upateYjdmBatch] yjdmEntityList={}", JSONUtil.toJsonStr(yjdmEntityList));

        yjdmService.updateBatchById(yjdmEntityList);
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/queryYjdmByDmTime")
    public R queryYjdmById(@RequestParam Map<String, Object> params){
        logger.info("[queryYjdmById] params={}", params);
        String dmTime = MapUtil.getStr(params, "dmTime");
        List<YjdmEntity> yjdmEntityList = yjdmService.list(new QueryWrapper<YjdmEntity>().eq("dm_time", dmTime));
        return R.ok().put("data", yjdmEntityList);
    }

    @ResponseBody
    @GetMapping("/queryYjdmPage")
    public R queryYjdmPage(@RequestParam Map<String, Object> params){
        logger.info("[queryYjdmPage] params={}", params);
        Page page = yjdmService.queryYjdmPage(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    @GetMapping("/exportYjdmByDate")
    public void exportYjdmByDate(String dmTime, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportYjdmByDate] dmTime={}", dmTime);

        QueryWrapper<YjdmEntity> wrapper = new QueryWrapper<YjdmEntity>().eq("dm_time", dmTime);
        List<YjdmEntity> records = yjdmService.list(wrapper);
        Map<String, Object> map = records
                .stream().collect(Collectors.toMap(YjdmEntity::getDeptId, YjdmEntity::getYd));

        map.forEach((k, v) ->{
            if (Integer.parseInt(String.valueOf(v)) == 1) {
                map.put(k,"√");
            }else{
                map.put(k,"×");
            }
        });

        Date d = DateUtil.parse(dmTime);
        int monthInt = DateUtil.month(d) + 1;
        String dmTimeFormat = monthInt + "月" + DateUtil.dayOfMonth(d) + "日" + DateUtil.hour(d, true) + "时" + DateUtil.minute(d) + "分";
        Map<String, Object> excelMap = new HashMap<>();
        excelMap.putAll(map);
        excelMap.put("dmTimeFormat", dmTimeFormat);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/yjdm.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = dmTimeFormat +"应急点名结果.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }

    @ResponseBody
    @GetMapping("/queryYjdmReportDate")
    public R queryYjdmReportDate(@RequestParam Map<String, Object> params){
        logger.info("[queryYjdmReportDate] params={}", params);

        //先默认去当前月
        params.put("startDate", DateUtil.format(DateUtil.beginOfMonth(new Date()).toJdkDate(), DatePattern.NORM_DATE_FORMAT));
        params.put("endDate", DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));

        List<Map<String,Object>> list= yjdmService.queryYjdmReportDate(params);

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
        list.forEach(e -> {
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
    @GetMapping("/queryYjdmStatisticsByDatePage")
    public R queryYjdmStatisticsByDatePage(@RequestParam Map<String, Object> params){
        logger.info("[queryYjdmStatisticsByDatePage] params={}", params);
        IPage page = yjdmService.queryYjdmStatisticsByDatePage(params);
        List<Map<String, Object>> records = page.getRecords();


        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    @GetMapping("/exportYjdmStatisticsByDate")
    public void exportYjdmStatisticsByDate(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportYjdmStatisticsByDate] params={}", params);
        String startDate = String.valueOf(params.get("startDate"));
        String endDate = String.valueOf(params.get("endDate"));
        params.put("page", 1);
        params.put("limit", 9999);//这里设置9999，假装不分页
        IPage page = yjdmService.queryYjdmStatisticsByDatePage(params);

        List<Map<String, Object>> records = page.getRecords();

        //添加总计 邗江分局
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("dept_name", "邗江分局");
        totalMap.put("total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("total")))).sum());
        totalMap.put("yd1Total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("yd1Total")))).sum());
        totalMap.put("yd2Total", records.stream().mapToInt(e->Integer.parseInt(String.valueOf(e.get("yd2Total")))).sum());
        totalMap.put("yd1totalPrecent",  new BigDecimal(records.stream().mapToInt(e -> Integer.parseInt(String.valueOf(e.get("yd1totalPrecentInt")))).average().getAsDouble()).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");

        records.add(totalMap);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/yjdmStatistics.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("mapList", records);

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = startDate + "_" + endDate + "应急点名统计情况.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }



}
