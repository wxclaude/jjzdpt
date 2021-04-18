package io.renren.modules.myh.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.exception.RRException;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.R;
import io.renren.modules.mrtz.entity.ZhMrtzEntity;
import io.renren.modules.mrtz.service.MrtzService;
import io.renren.modules.myh.entity.Tnews;
import io.renren.modules.myh.service.TnewsService;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author tomchen
 * @date 2020-04-27
 */
@RestController
@RequestMapping("myh")
public class MyhController extends AbstractController {

    @Autowired
    private TnewsService tnewsService;

    /********************************  新闻类开始   ************************************************/

    //分页查询 新闻列表
    @ResponseBody
    @GetMapping("/news/query")
    public R newsQuery(@RequestParam Map<String, Object> params) {
        logger.info("[newsQuery] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        Integer type = MapUtil.getInt(params, "type");
        //String frm = MapUtil.getStr(params, "frm");
        Page pageParams = new Page(page,limit);


        QueryWrapper<Tnews> queryWrapper = new QueryWrapper<Tnews>()
                .eq(type != null, "type", type)
                //.eq(frm != null && "1".equals(frm), "left(news_time,7)", StrUtil.subBefore(DateUtil.formatDate(new Date()), "-", true))
                .orderByDesc("news_time");

        IPage<Tnews> list = tnewsService.page(pageParams,queryWrapper);
        return R.ok().put("data", list.getRecords()).put("count", list.getTotal());

    }

    //新增新闻数据
    @ResponseBody
    @PostMapping("/news/add")
    public R newsAdd(@RequestBody Tnews tnews, HttpServletRequest request) {
        logger.info("[newsAdd] params={}", tnews);

//        tnews.setCreateBy(getUser().getName());
        if (!tnewsService.save(tnews)) {
            throw new RRException("新增新闻数据失败");
        }
        return R.ok();

    }

    //修改新闻数据
    @ResponseBody
    @PostMapping("/news/update")
    public R newsUpdate(@RequestBody Tnews tnews, HttpServletRequest request) {
        logger.info("[newsUpdate] params={}", tnews);

        if (!tnewsService.updateById(tnews)) {
            throw new RRException("修改新闻数据失败");
        }

        return R.ok();

    }

    //删除新闻数据
    @ResponseBody
    @GetMapping("/news/delete")
    public R newsDelete(@RequestParam String id) {
        logger.info("[newsDelete] id={}", id);

        tnewsService.removeById(id);
        return R.ok();
    }

    //上传新闻轮播图
    @ResponseBody
    @PostMapping("/news/upload")
    public R newsUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        logger.info("[newsUpload] ");


        String filePath = tnewsService.uploadNewsImage(file);
        List<String> list = new ArrayList<>();
        list.add(filePath);

        return R.ok().put("data", list).put("errno", 0);
    }

    //修改 新闻轮播图 前获取数据
    @ResponseBody
    @GetMapping("/news/queryById")
    public R queryNewsById(@RequestParam String id) {
        logger.info("[queryNewsById] id={}", id);

        Tnews tnews = tnewsService.getById(id);

        if (tnews == null) {
            throw new RRException("获取新闻数据异常");
        }


        return R.ok().put("data", tnews);

    }

    //首页 路面警情
    @ResponseBody
    @GetMapping("/news/queryStatic")
    public R queryNewsStatic() {
        logger.info("[queryNewsStatic]");
        //.eq(frm != null && "1".equals(frm), "left(news_time,7)", StrUtil.subBefore(DateUtil.formatDate(new Date()), "-", true))

        int monthCount = tnewsService.count(new QueryWrapper<Tnews>().eq("left(news_time,7)", StrUtil.subBefore(DateUtil.formatDate(new Date()), "-", true)));
        int totalCount = tnewsService.count(new QueryWrapper<Tnews>());

        return R.ok().put("monthCount", monthCount).put("totalCount", totalCount);

    }




    /********************************  新闻类结束  ************************************************/

}
