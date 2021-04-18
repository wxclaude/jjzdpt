package io.renren.modules.dm.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import io.renren.modules.dm.form.HydmForm;
import io.renren.modules.dm.service.HydmService;
import io.renren.modules.dm.service.YjdmService;
import io.renren.modules.jcj.entity.YdjcjEntity;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

//会议点名
@RestController
@RequestMapping("/hydm")
public class HydmController extends AbstractController {

    @Autowired
    private HydmService hydmService;

    @ResponseBody
    @PostMapping("/addHydmBatch")
    @SysLog("会议点名")
    public R addHydmBatch(@RequestBody HydmForm hydmForm, HttpServletRequest request){
        SysUserEntity user = getUser();
        String remoteAddr = request.getRemoteAddr();
        logger.info("[addHydmBatch] user={}, oteAddr={}, hydmForm={}", user.getName(), remoteAddr, JSONUtil.toJsonStr(hydmForm));

        hydmForm.getData().forEach( hydmEntity -> {
            hydmEntity.setTitle(hydmForm.getTitle());
            hydmEntity.setDmPolice(user.getName());
            hydmEntity.setDmTime(hydmForm.getDmTime());
            hydmEntity.setIp(remoteAddr);

        });

        int count = hydmService.count(new QueryWrapper<HydmEntity>().eq("dm_time", hydmForm.getDmTime()));
        if (count > 0) {
            throw new RRException("新增失败，该时间已存在会议点名记录");
        }

        hydmService.addHydmBatch(hydmForm.getData());
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/queryHydmPage")
    public R queryHydmPage(@RequestParam Map<String, Object> params){
        logger.info("[queryHydmPage] params={}", params);
        Page page = hydmService.queryHydmPage(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    @GetMapping("/exportHydmByDate")
    public void exportHydmByDate(String dmTime, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportHydmByDate] dmTime={}", dmTime);

        QueryWrapper<HydmEntity> wrapper = new QueryWrapper<HydmEntity>().eq("dm_time", dmTime).orderByAsc("length(dept_id)").orderByAsc("dept_id");
        List<HydmEntity> records = hydmService.list(wrapper);

        String title = "";

        if (records != null && records.size() > 0) {
            title = records.get(0).getTitle();
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        records.forEach(e -> {
            mapList.add(BeanUtil.beanToMap(e));
        });

        mapList.forEach(e ->{
            if (Integer.parseInt(String.valueOf(e.get("yd"))) == 1) {
                e.put("yd","√");
            }else{
                e.put("yd","×");
            }
            if (Integer.parseInt(String.valueOf(e.get("xg"))) == 1) {
                e.put("xg","良好");
            }else{
                e.put("xg","异常");
            }

        });



        Date d = DateUtil.parse(dmTime);
        int monthInt = DateUtil.month(d) + 1;
        String dmTimeFormat = monthInt + "月" + DateUtil.dayOfMonth(d) + "日" + DateUtil.hour(d, true) + "时" + DateUtil.minute(d) + "分";
        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", mapList);
        excelMap.put("dmTimeFormat", dmTimeFormat);
        excelMap.put("title", title);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/hydm.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = dmTimeFormat +"会议点名结果.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }

    //导出会议点名统计
    @GetMapping("/exportHydmStatisticsByDate")
    public void exportHydmStatisticsByDate(String startDate, String endDate, HttpServletRequest request, HttpServletResponse response) {

        logger.info("[exportHydmStatisticsByDate] startDate={},endDate={}", startDate, endDate);

//        List<Map<String, Object>> mapList = hydmService.queryHydmStatisticsByDate(startDate,endDate);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        List<Map<String, Object>> list = hydmService.queryHydmReportDate(params);


        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", list);
        excelMap.put("startDate", startDate);
        excelMap.put("endDate", endDate);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/hydmStatistics.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = startDate + "-" + endDate + "会议点名统计.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }

    @ResponseBody
    @GetMapping("/queryHydmReportDate")
    public R queryHydmReportDate(@RequestParam Map<String, Object> params){
        logger.info("[queryHydmReportDate] params={}", params);

        //先默认去当前月
        params.put("startDate", DateUtil.format(DateUtil.beginOfMonth(new Date()).toJdkDate(), DatePattern.NORM_DATE_FORMAT));
        params.put("endDate", DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));

        List<Map<String, Object>> list = hydmService.queryHydmReportDate(params);

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
    @PostMapping("/updateHydmBatch")
    @SysLog("修改会议点名")
    public R updateHydmBatch(@RequestBody HydmForm hydmForm, HttpServletRequest request){
        SysUserEntity user = getUser();
        String remoteAddr = request.getRemoteAddr();
        logger.info("[updateHydmBatch] user={}, oteAddr={}, hydmForm={}", user.getName(), remoteAddr, JSONUtil.toJsonStr(hydmForm));

        hydmForm.getData().forEach( hydmEntity -> {
            hydmEntity.setTitle(hydmForm.getTitle());
            hydmEntity.setDmPolice(user.getName());
            hydmEntity.setDmTime(hydmForm.getDmTime());
            hydmEntity.setIp(remoteAddr);
        });

        int count = hydmService.count(new QueryWrapper<HydmEntity>().eq("dm_time", hydmForm.getDmTime()).ne("title", hydmForm.getTitle()));
        if (count > 0) {
            throw new RRException("修改失败，该时间已存在会议点名记录");
        }

        hydmService.updateBatchById(hydmForm.getData());
        return R.ok();
    }

    //会议查询
    @ResponseBody
    @GetMapping("/queryByDate")
    public R queryByDate(@RequestParam String dmTime){
        logger.info("[queryByDate] dmTime={}", dmTime);

        List<HydmEntity> list = hydmService.list(new QueryWrapper<HydmEntity>().eq("dm_time", dmTime));

        return R.ok().put("data", list);
    }



}
