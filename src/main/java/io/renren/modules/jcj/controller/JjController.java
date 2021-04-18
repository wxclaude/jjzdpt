package io.renren.modules.jcj.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.utils.R;
import io.renren.modules.dm.service.DeptService;
import io.renren.modules.jcj.entity.YdjcjEntity;
import io.renren.modules.jcj.service.JjService;
import io.renren.modules.jcj.service.YdjcjService;
import io.renren.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
接警信息
 */
@RestController
@RequestMapping("/jj")
public class JjController extends AbstractController {

    @Autowired
    private JjService jjService;

    //接警信息查询
    @ResponseBody
    @GetMapping("/queryJjList")
    public R queryJjList(@RequestParam Map<String, Object> params){
        logger.info("[queryJjList] params={}", params);
        IPage page = jjService.queryJjList(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

}
