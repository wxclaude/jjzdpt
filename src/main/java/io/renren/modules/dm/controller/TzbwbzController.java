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
import io.renren.modules.dm.entity.DeptEntity;
import io.renren.modules.dm.entity.TzbwbzEntity;
import io.renren.modules.dm.entity.YjdmEntity;
import io.renren.modules.dm.service.DeptService;
import io.renren.modules.dm.service.TzbwbzService;
import io.renren.modules.dm.service.YjdmService;
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

@RestController
@RequestMapping("/tzbwbz")
public class TzbwbzController extends AbstractController {

    @Autowired
    private TzbwbzService tzbwbzService;
    @Autowired
    private DeptService deptService;

    @ResponseBody
    @PostMapping("/addTzbwbz")
    @SysLog("添加值班点名未报备")
    public R add(@RequestBody TzbwbzEntity tzbwbzEntity){
        SysUserEntity user = getUser();
        Date date = new Date();
        logger.info("[addTzbwbz] tzbwbzEntity={}", tzbwbzEntity);

        String deptIds = tzbwbzEntity.getDeptIds();
        String[] deptIdsArr = StrUtil.split(deptIds, ",");
        for (String deptId : deptIdsArr) {
            TzbwbzEntity t = new TzbwbzEntity();
            t.setDeptId(deptId);
            t.setZbdate(tzbwbzEntity.getZbdate());
            t.setCreateBy(user.getName());
            tzbwbzService.save(t);
        }

        return R.ok();
    }

    //根据日期统计未报备值班数据
    @ResponseBody
    @GetMapping("/queryTzbwbzSta")
    public R queryTzbwbzSta(@RequestParam Map<String, Object> params){
        logger.info("[queryTzbwbzSta] params={}", params);

        List<Map<String, Object>> list = tzbwbzService.queryTzbwbzSta(params);
        return R.ok().put("data", list);
    }

    //根据日期和deptId查看该单位未报备详情
    @ResponseBody
    @GetMapping("/queryTzbwbzByDateAndDept")
    public R queryTzbwbzByDateAndDept(@RequestParam Map<String, Object> params){
        logger.info("[queryTzbwbzByDateAndDept] params={}", params);
        String startDate = MapUtil.getStr(params, "startDate");
        String endDate = MapUtil.getStr(params, "endDate");
        String deptId = MapUtil.getStr(params, "deptId");

        List<TzbwbzEntity> list = tzbwbzService.list(new QueryWrapper<TzbwbzEntity>().eq("dept_id", deptId).ge("zbdate", startDate).le("zbdate", endDate));
        list.forEach(e->{
            e.setDeptName(deptService.getOne(new QueryWrapper<DeptEntity>().eq("dept_id", e.getDeptId()).last("limit 1")).getDeptName());
        });
        return R.ok().put("data", list);
    }



}
