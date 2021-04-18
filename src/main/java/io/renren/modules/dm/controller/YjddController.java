package io.renren.modules.dm.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.R;
import io.renren.modules.dm.entity.TyjddEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import io.renren.modules.dm.form.YjddForm;
import io.renren.modules.dm.service.YjddService;
import io.renren.modules.dm.service.YjdmService;
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

//一键点调
@RestController
@RequestMapping("/yjdd")
public class YjddController extends AbstractController {

    @Autowired
    private YjddService yjddService;

    @ResponseBody
    @PostMapping("/addYjdd")
    @SysLog("一键点调")
    public R addYjdd(@RequestBody YjddForm yjddForm, HttpServletRequest request){
        logger.info("[addYjdd] yjddForm={}", yjddForm);

        String ip = IPUtils.getIpAddr(request);
        yjddForm.getData().forEach(e->{
            e.setDdsj(yjddForm.getDmTime());
            e.setCreateBy(getUser().getName());
            e.setIp(ip);
        });

        if (!yjddService.saveBatch(yjddForm.getData())) {
            throw new RRException("一键点调失败，请联系管理员");
        }

        return R.ok();
    }

    @ResponseBody
    @PostMapping("/upateYjddBatch")
    @SysLog("修改一键点调")
    public R upateYjddBatch(@RequestBody YjddForm yjddForm){
        logger.info("[upateYjddBatch] yjddForm={}", yjddForm);

        yjddForm.getData().forEach(e->{
            e.setDdsj(yjddForm.getDmTime());
        });
        yjddService.updateBatchById(yjddForm.getData());
        return R.ok();
    }

    @ResponseBody
    @GetMapping("/queryYjddByDmTime")
    public R queryYjddByDmTime(@RequestParam Map<String, Object> params){
        logger.info("[queryYjddByDmTime] params={}", params);
        String ddsj = MapUtil.getStr(params, "ddsj");
        List<TyjddEntity> yjdmEntityList = yjddService.list(new QueryWrapper<TyjddEntity>().eq("ddsj", ddsj));
        return R.ok().put("data", yjdmEntityList);
    }

    @ResponseBody
    @GetMapping("/queryYjddPage")
    public R queryYjddPage(@RequestParam Map<String, Object> params){
        logger.info("[queryYjddPage] params={}", params);
        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        IPage pageParams = new Page(page, limit);

        IPage pageResult = yjddService.pageMaps(pageParams, new QueryWrapper<TyjddEntity>().select("ddsj", "sum(yd) as ydTotal").groupBy("ddsj").orderByDesc("create_time"));
        return R.ok().put("data", pageResult.getRecords()).put("count", pageResult.getTotal());
    }

    //@GetMapping("/exportYjdmByDate")
    //public void exportYjdmByDate(String dmTime, HttpServletRequest request, HttpServletResponse response) {
    //    logger.info("[exportYjdmByDate] dmTime={}", dmTime);
    //
    //    QueryWrapper<YjdmEntity> wrapper = new QueryWrapper<YjdmEntity>().eq("dm_time", dmTime);
    //    List<YjdmEntity> records = yjdmService.list(wrapper);
    //    Map<String, Object> map = records
    //            .stream().collect(Collectors.toMap(YjdmEntity::getDeptId, YjdmEntity::getYd));
    //
    //    map.forEach((k, v) ->{
    //        if (Integer.parseInt(String.valueOf(v)) == 1) {
    //            map.put(k,"√");
    //        }else{
    //            map.put(k,"×");
    //        }
    //    });
    //
    //    Date d = DateUtil.parse(dmTime);
    //    int monthInt = DateUtil.month(d) + 1;
    //    String dmTimeFormat = monthInt + "月" + DateUtil.dayOfMonth(d) + "日" + DateUtil.hour(d, true) + "时" + DateUtil.minute(d) + "分";
    //    Map<String, Object> excelMap = new HashMap<>();
    //    excelMap.putAll(map);
    //    excelMap.put("dmTimeFormat", dmTimeFormat);
    //
    //    URL resource = Thread.currentThread().getContextClassLoader().getResource("template/yjdm.xlsx");
    //    TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());
    //    Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);
    //
    //    String fileName = dmTimeFormat +"应急点名结果.xlsx";
    //    if (workbook != null) {
    //        downLoadExcel(fileName, response, workbook);
    //    }
    //
    //}


}
