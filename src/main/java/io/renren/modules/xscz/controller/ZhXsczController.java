package io.renren.modules.xscz.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.MyUtils;
import io.renren.common.utils.R;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.mrtz.service.MrtzService;
import io.renren.modules.sys.consts.SysModelConsts;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.xscz.entity.ZhXsczEntity;
import io.renren.modules.xscz.entity.ZhXsczFjEntity;
import io.renren.modules.xscz.service.ZhXsczFjService;
import io.renren.modules.xscz.service.ZhXsczService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-27
 */
@RestController
@RequestMapping("xscz")
public class ZhXsczController extends AbstractController {

    @Autowired
    private ZhXsczService zhXsczService;
    @Autowired
    private ZhXsczFjService zhXsczFjService;
    @Autowired
    private SysModelConsts sysModelConsts;

    //根据单位和时间查询线索处置
    @ResponseBody
    @GetMapping("/queryZhXsczPage")
    public R queryZhXsczPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryZhXsczPage] params={}", params);

        IPage<ZhXsczEntity> list = zhXsczService.queryZhXsczPage(params);

        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());
    }

    //生成线索处置
    @ResponseBody
    @PostMapping("/addZhXscz")
    @SysLog("生成线索处置")
    public R addZhXscz(@RequestBody ZhXsczEntity zhXsczEntity, HttpServletRequest request) {

        logger.info("[addZhXscz] zhXsczEntity={}", zhXsczEntity);

        zhXsczEntity.setId(MyUtils.getUUID());
        zhXsczEntity.setIp1(IPUtils.getIpAddr(request));
        zhXsczEntity.setCreateBy(getUser().getName());
        zhXsczEntity.setXfsj(new Date());

        zhXsczService.saveWithFj(zhXsczEntity);

        return R.ok();
    }

    //签收线索处置
    @ResponseBody
    @PostMapping("/qsZhXscz")
    @SysLog("签收线索处置")
    public R qsZhXscz(@RequestBody ZhXsczEntity zhXsczEntity, HttpServletRequest request) {

        logger.info("[qsZhXscz] zhXsczEntity={}", zhXsczEntity);

        ZhXsczEntity exsit = zhXsczService.getById(zhXsczEntity.getId());
        if (exsit == null) {
            throw new RRException("数据异常，请联系管理员");
        }

        if (zhXsczEntity.getCzzt() == 2) {
            zhXsczEntity.setQssj(new Date());
        } else if (zhXsczEntity.getCzzt() == 3) {
            zhXsczEntity.setZhzxqssj(new Date());
        }

        zhXsczService.updateById(zhXsczEntity);

        return R.ok();
    }

    //反馈线索处置
    @ResponseBody
    @PostMapping("/updateZhXscz")
    @SysLog("反馈线索处置")
    public R updateZhXscz(@RequestBody ZhXsczEntity zhXsczEntity, HttpServletRequest request) {

        logger.info("[updateZhXscz] zhXsczEntity={}", zhXsczEntity);

        ZhXsczEntity exsit = zhXsczService.getById(zhXsczEntity.getId());
        if (exsit == null) {
            throw new RRException("数据异常，请联系管理员");
        }


        zhXsczEntity.setIp2(IPUtils.getIpAddr(request));
        zhXsczEntity.setFksj(new Date());
        zhXsczEntity.setCzzt(1);
        //判断是否超时
        if (zhXsczEntity.getFksj().getTime() >= exsit.getCzsj().getTime()) {
            zhXsczEntity.setSfcs(1);
            zhXsczEntity.setCssj((zhXsczEntity.getFksj().getTime() - exsit.getCzsj().getTime()) / 1000 / 60 / 60);
        }

        zhXsczService.updateWithFj(zhXsczEntity);

        return R.ok();
    }

    //修改线索处置
    @ResponseBody
    @PostMapping("/modifyZhXscz")
    @SysLog("修改线索处置")
    public R modifyZhXscz(@RequestBody ZhXsczEntity zhXsczEntity, HttpServletRequest request) {

        logger.info("[modifyZhXscz] zhXsczEntity={}", zhXsczEntity);

        ZhXsczEntity exsit = zhXsczService.getById(zhXsczEntity.getId());
        if (exsit == null) {
            throw new RRException("数据异常，请联系管理员");
        }

        zhXsczService.modifyWithFj(zhXsczEntity);

        return R.ok();
    }

    //删除线索处置
    @ResponseBody
    @GetMapping("/deleteZhXscz")
    @SysLog("删除线索处置")
    public R deleteZhXscz(String id){
        logger.info("[deleteZhXscz] id={}", id);

        zhXsczService.removeById(id);
        return R.ok();
    }



    //上传线索处置附件
    @ResponseBody
    @PostMapping("/uploadZhXsczFj")
    public R uploadZhXsczFj(@RequestParam("file") MultipartFile file, @RequestParam int type, HttpServletRequest request) {

        logger.info("[uploadZhXsczFj] type={}", type);


        String fjId = zhXsczFjService.uploadZhXsczFj(file, type, getUser().getName(), IPUtils.getIpAddr(request));

        return R.ok().put("data", MapUtil.builder().put("fjId",fjId).build());
    }

    //下载线索处置附件
    @GetMapping("/exportZhXscz")
    public void exportZhXscz(String id, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[ZhXscz] id={}", id);

        ZhXsczFjEntity zhXsczFjEntity = zhXsczFjService.getById(id);
        if (zhXsczFjEntity == null) {
            throw new RRException("下载文件异常");
        }

        String fileUrl = zhXsczFjEntity.getPath() + zhXsczFjEntity.getId() + File.separator + zhXsczFjEntity.getFilename();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.setContentType("application/force-download");
            //设置编码，避免文件名中文乱码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zhXsczFjEntity.getFilename(), "UTF-8"));
            outputStream.write(FileUtil.readBytes(fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RRException("下载文件异常");
        }finally {
            IoUtil.close(outputStream);
        }


    }

    //下载线索处置word
    @GetMapping("/exportWord")
    public void exportWord(String id, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportWord] id={}", id);

        ZhXsczEntity zhXsczEntity = zhXsczService.getById(id);
        if (zhXsczEntity == null) {
            throw new RRException("下载文件异常");
        }

        try {
            String typeName = zhXsczEntity.getType() == 0 ? "污染防治_" : "城管_";
            String fileName = typeName + DateUtil.formatDate(zhXsczEntity.getXfsj()) + "_" + zhXsczEntity.getCzsd() + "（" + zhXsczEntity.getSjhdd() + "）" + ".docx";
            URL resource = Thread.currentThread().getContextClassLoader().getResource(zhXsczEntity.getType() == 0 ? "template/wrfz.docx" : "template/cg.docx");

            Map<String, Object> map = BeanUtil.beanToMap(zhXsczEntity);
            if (zhXsczEntity.getSffxsxhjwfxw() == null) {
                map.put("sffxsxhjwfxwStr", "");
            } else {
                map.put("sffxsxhjwfxwStr", zhXsczEntity.getSffxsxhjwfxw() == 0 ? "否" : "是");
            }

            if (zhXsczEntity.getSfydzf() == null) {
                map.put("sfydzfStr", "");

            } else {
                map.put("sfydzfStr", zhXsczEntity.getSfydzf() == 0 ? "否" : "是");
            }

            if (zhXsczEntity.getSfsyzfjyl() == null) {
                map.put("sfsyzfjylStr", "");
            } else {
                map.put("sfsyzfjylStr", zhXsczEntity.getSfsyzfjyl() == 0 ? "否" : "是");
            }

            if (zhXsczEntity.getJcjg() == null) {
                map.put("jcjgStr", "");
            } else {
                switch (zhXsczEntity.getJcjg()) {
                    case 0:
                        map.put("jcjgStr", "不属实");
                        break;
                    case 1:
                        map.put("jcjgStr", "已解决");
                        break;
                    case 2:
                        map.put("jcjgStr", "受理行政案件");
                        break;
                    default:
                        map.put("jcjgStr", "");
                        break;
                }
            }

            if (zhXsczEntity.getFwdxsfmy() == null) {
                map.put("fwdxsfmyStr", "");
            } else {
                switch (zhXsczEntity.getFwdxsfmy()) {
                    case 0:
                        map.put("fwdxsfmyStr", "不满意");
                        break;
                    case 1:
                        map.put("fwdxsfmyStr", "满意");
                        break;
                    case 2:
                        map.put("fwdxsfmyStr", "一般");
                        break;
                    case 3:
                        map.put("fwdxsfmyStr", "未接通");
                        break;
                    default:
                        map.put("fwdxsfmyStr", "");
                        break;
                }
            }

            XWPFDocument doc = WordExportUtil.exportWord07(resource.getPath(), map);
            if (doc != null) {
                downLoadWord(fileName, response, doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //统计
    @ResponseBody
    @GetMapping("/queryZhXsczStat")
    public R queryZhXsczStat(@RequestParam Map<String, Object> params) {

        logger.info("[queryZhXsczStat] params={}", params);

        List<Map<String, Object>> list = zhXsczService.queryZhXsczStat(params);

        return R.ok().put("data", list);
    }

    //统计详情
    @ResponseBody
    @GetMapping("/queryZhXsczStatDetail")
    public R queryZhXsczStatDetail(@RequestParam Map<String, Object> params) {

        logger.info("[queryZhXsczStatDetail] params={}", params);

        List<Map<String, Object>> list = zhXsczService.queryZhXsczStatDetail(params);

        return R.ok().put("data", list);
    }

    //统计派出所未签收
    @ResponseBody
    @GetMapping("/queryCount")
    public R queryCount(@RequestParam Map<String, Object> params) {

        logger.info("[queryCount] params={}", params);

        int count0 = zhXsczService.count(new QueryWrapper<ZhXsczEntity>().eq("dept_id", params.get("deptId")).eq("czzt", 0).eq("type", 0));
        int count1 = zhXsczService.count(new QueryWrapper<ZhXsczEntity>().eq("dept_id", params.get("deptId")).eq("czzt", 0).eq("type", 1));

        Map<String, Object> result = new HashMap<>();
        result.put("count0", count0);
        result.put("count1", count1);

        return R.ok().put("data", result);
    }



}
