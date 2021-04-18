package io.renren.modules.mrtz.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.R;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.mrtz.service.MrtzService;
import io.renren.modules.sys.consts.SysModelConsts;
import io.renren.modules.sys.controller.AbstractController;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author tomchen
 * @date 2020-04-27
 */
@RestController
@RequestMapping("mrtz")
public class MrtzController extends AbstractController {

    @Autowired
    private MrtzService mrtzService;
    @Autowired
    private SysModelConsts sysModelConsts;

    //根据单位和时间查询每日台账
    @ResponseBody
    @GetMapping("/queryMrtzPage")
    public R queryMrtzPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryMrtzPage] params={}", params);

        IPage<ZhMrtzEntity> list = mrtzService.queryMrtz(params);

        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());
    }

    //生成每日台账
    @ResponseBody
    @PostMapping("/addMrtz")
    //@SysLog("生成每日台账")
    public R addMrtz(@RequestParam("file") MultipartFile file, String tzDate, String deptId, HttpServletRequest request) {

        logger.info("[addMrtz] tzDate={}, deptId={}", tzDate, deptId);

        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".doc") && !fileName.endsWith(".docx")) {
            throw new RRException("文件格式不正确");
        }

        if (StrUtil.isBlank(deptId)) {
            throw new RRException("单位异常，请联系管理员");
        }

        mrtzService.addMrtz(file, tzDate, deptId, IPUtils.getIpAddr(request));

        return R.ok();
    }

    @GetMapping("/exportMrtz")
    public void exportMrtz(int id, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportMrtz] id={}", id);

        ZhMrtzEntity mrtzEntity = mrtzService.getById(id);
        if (mrtzEntity == null) {
            throw new RRException("下载文件异常");
        }

        String fileUrl = mrtzEntity.getPath() + mrtzEntity.getDeptId() + File.separator + mrtzEntity.getUid() + File.separator + mrtzEntity.getFileName();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.setContentType("application/force-download");
            //设置编码，避免文件名中文乱码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(mrtzEntity.getFileName(), "UTF-8"));
            //response.setHeader("Content-Disposition", "attachment;filename=" + new String(mrtzEntity.getFileName().getBytes("gb2312"), "ISO8859-1") );
            outputStream.write(FileUtil.readBytes(fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RRException("下载文件异常");
        }finally {
            IoUtil.close(outputStream);
        }


    }

    //统计
    @ResponseBody
    @GetMapping("/queryMrtzStat")
    public R queryMrtzStat(@RequestParam Map<String, Object> params) {

        logger.info("[queryMrtzStat] params={}", params);

        List<Map<String, Object>> list = mrtzService.queryMrtzStat(params);

        return R.ok().put("data", list);
    }


    @GetMapping("/exportMrtzStat")
    public void queryMrtzStat(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportMrtzStat] params={}", params);
        String startDate = String.valueOf(params.get("startDate"));
        String endDate = String.valueOf(params.get("endDate"));

        List<Map<String, Object>> list = mrtzService.queryMrtzStat(params);


        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/mrtzStatistics.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("mapList", list);

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = startDate + "_" + endDate + "每日台账情况.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }


}
