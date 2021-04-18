package io.renren.modules.jcj.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.*;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.utils.R;
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.dm.service.DeptService;
import io.renren.modules.jcj.entity.YdjcjEntity;
import io.renren.modules.jcj.service.YdjcjService;
import io.renren.modules.sys.controller.AbstractController;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author tomchen
 * @date 2020-04-13
 * 移动接出警
 */
@RestController
@RequestMapping("/ydjcj")
public class YdjcjController  extends AbstractController {

    @Autowired
    private YdjcjService ydjcjService;

    @Autowired
    private DeptService deptService;

    //移动接出警查询
    @ResponseBody
    @GetMapping("/queryByDate")
    public R queryByDate(String startDate,String endDate){
        logger.info("[queryByDate] startDate={},endDate={}", startDate, endDate);
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        List<YdjcjEntity> list = ydjcjService.queryByDate(params);

        return R.ok().put("data", list);
    }

    //根据日期查询未上传移动接出警数据的所有日期
    @ResponseBody
    @GetMapping("/queryNotUploadDateByDate")
    public R queryNotUploadDateByDate(String startDate,String endDate){
        logger.info("[queryNotUploadDateByDate] startDate={},endDate={}", startDate, endDate);
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        List<DateTime> dateTimeList = DateUtil.rangeToList(DateUtil.parse(startDate).toJdkDate(), DateUtil.parse(endDate).toJdkDate(), DateField.DAY_OF_MONTH);
        List<String> dateList = new ArrayList<>();
        dateTimeList.forEach(e -> {
            dateList.add(e.toString(DatePattern.NORM_DATE_FORMAT));
        });

        List<String> uploadDateList = ydjcjService.queryUploadDateList(params);
        dateList.removeAll(uploadDateList);

        return R.ok().put("data", dateList);
    }

    @ResponseBody
    @PostMapping("/upload")
    public R uploadYdjcj(@RequestParam("file") MultipartFile file,String date) throws Exception {

        if (StrUtil.isEmpty(date)) {
            return R.error("请选择上传日期");
        }

        ImportParams params = new ImportParams();

        List<YdjcjEntity> list = ExcelImportUtil.importExcel(file.getInputStream(), YdjcjEntity.class,params);

        ydjcjService.uploadYdjcj(list, getUserId(),date);


        return R.ok();

    }

    @ResponseBody
    @PostMapping("/uploadZd")
    public R uploadZd(@RequestParam("file") MultipartFile file,String date) throws Exception {

        if (StrUtil.isEmpty(date)) {
            return R.error("请选择上传日期");
        }
        ImportParams params = new ImportParams();

        List<YdjcjEntity> list = ExcelImportUtil.importExcel(file.getInputStream(), YdjcjEntity.class,params);

        ydjcjService.uploadYdjcjZd(list, getUserId(),date);


        return R.ok();

    }


    @GetMapping("/exportYdjcj")
    public void exportYdjcj(String startDate, String endDate, HttpServletRequest request, HttpServletResponse response) {

        logger.info("[exportYdjcj] startDate={}, endDate={}", startDate, endDate);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        List<YdjcjEntity> list = ydjcjService.queryByDate(params);

        list.forEach(e->{
            e.setXptjjlvExport(e.getXptjjlv() + "%");
            e.setXptcjlvExport(e.getXptcjlv() + "%");
            e.setYddcjlvExport(e.getYddcjlv() + "%");
        });



        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", list);

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/ydjcj.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = "移动接处警.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }



    public static void main(String[] args) {
        List<DateTime> dateTimes = DateUtil.rangeToList(DateUtil.parse("2020-04-10").toJdkDate(), DateUtil.parse("2020-05-25").toJdkDate(), DateField.DAY_OF_MONTH);
        System.out.println(dateTimes);

    }

}
