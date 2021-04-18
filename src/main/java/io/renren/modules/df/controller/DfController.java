package io.renren.modules.df.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.modules.df.consts.DfConsts;
import io.renren.modules.df.entity.*;
import io.renren.modules.df.service.*;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.xlgl.entity.TdkEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tomchen
 * @date 2020-04-28
 */
@RestController
@RequestMapping("/df")
public class DfController extends AbstractController {

    @Autowired
    private DfDetailService dfDetailService;

    @Autowired
    private DfRecordService dfRecordService;

    @Autowired
    private DfConfigService dfConfigService;

    @Autowired
    private DfRuleService dfRuleService;

    @Autowired
    private DfQbxxService dfQbxxService;
    @Autowired
    private DfSjfnService dfSjfnService;
    @Autowired
    private DfZhzhService dfZhzhService;
    @Autowired
    private DfJqzlkhService dfJqzlkhService;
    @Autowired
    private DfQbxsService dfQbxsService;
    @Autowired
    private DfZdmbService dfZdmbService;
    @Autowired
    private DfConsts dfConsts;


    //查看打分项
    @ResponseBody
    @GetMapping("/queryDfConfig")
    public R queryDfConfig(@RequestParam Map<String, Object> params){
        logger.info("[queryDfConfig] params={}", params);

        //IPage<DfConfig> page = dfConfigService.queryDfConfig(params);
        //page.getRecords().stream().forEach(e -> {
        //    DfConfig parentDfConfig = dfConfigService.getById(e.getParentId());
        //    if (parentDfConfig != null) {
        //        e.setParentName(parentDfConfig.getDfx());
        //    }
        //
        //});

        List<DfConfig> list = new ArrayList<>();

        //先查出父亲节点
        List<DfConfig> parentList = dfConfigService.list(new QueryWrapper<DfConfig>().eq("type", 0));
        for (DfConfig dfParentConfig : parentList) {
            list.add(dfParentConfig);
            list.addAll(dfConfigService.list(new QueryWrapper<DfConfig>().eq("parent_id",dfParentConfig.getId())));
        }
        list.stream().forEach(e -> {
            DfConfig parentDfConfig = dfConfigService.getById(e.getParentId());
            if (parentDfConfig != null) {
                e.setParentName(parentDfConfig.getDfx());
            }

        });


        return R.ok().put("data", list);
    }


    //新增打分项
    @ResponseBody
    @PostMapping("/addDfConfig")
    @SysLog("新增打分项")
    public R addDfConfig(@RequestBody DfConfig dfConfig ) {

        logger.info("[addDfConfig] dfConfig={}", dfConfig);

        dfConfig.setCreateBy(getUser().getName());
        dfConfigService.save(dfConfig);

        return R.ok();
    }

    /*
    //批量新增打分项
    @ResponseBody
    @PostMapping("/addDfConfig")
    @SysLog("新增打分项")
    public R addDfConfig(@RequestBody List<DfConfig> list ) {

        logger.info("[addDfConfig] list={}", list);

        dfConfigService.addDfConfig(list, getUser().getName());

        return R.ok();
    }
    */

    //修改打分项
    @ResponseBody
    @PostMapping("/updateDfConfig")
    @SysLog("修改打分项")
    public R updateDfConfig(@RequestBody DfConfig dfConfig ) {

        logger.info("[updateDfConfig] dfConfig={}", dfConfig);

        dfConfigService.updateById(dfConfig);

        return R.ok();
    }



    //新增打分详情前 获取所有打分项目
    @ResponseBody
    @GetMapping("/queryParentDfConfig")
    public R queryParentDfConfig(@RequestParam Map<String, Object> params){
        logger.info("[queryParentDfConfig] params={}", params);

        String type = MapUtil.getStr(params, "type");
        List<DfConfig> list = new ArrayList<>();
        if (StrUtil.isNotEmpty(type)) {
            list = dfConfigService.list(new QueryWrapper<DfConfig>().eq("type", type));
        } else {
            //先查出父亲节点
            List<DfConfig> parentList = dfConfigService.list(new QueryWrapper<DfConfig>().eq("type", 0));
            for (DfConfig dfParentConfig : parentList) {
                list.add(dfParentConfig);
                list.addAll(dfConfigService.list(new QueryWrapper<DfConfig>().eq("parent_id",dfParentConfig.getId())));
            }

        }


        list.stream().forEach(e -> {
            //将权重比拼入名称
            if (e.getType() == 1) {
                e.setDfx(e.getDfx() + "(" + e.getQzb() + "%)");
            }
        });


        return R.ok().put("data", list);
    }

    //配置打分规则前 获取所有单位
    @ResponseBody
    @GetMapping("/queryAllDept")
    public R queryAllDept(@RequestParam Map<String, Object> params){
        logger.info("[queryAllDept] params={}", params);

        List<Map<String,Object>> list = dfConfigService.queryAllDept(params);


        return R.ok().put("data", list);
    }

    //获取打分规则
    @ResponseBody
    @GetMapping("/queryDfRule")
    public R queryDfRule(@RequestParam Map<String, Object> params){
        logger.info("[queryDfRule] params={}", params);

        List<DfRule> dfRuleList = dfRuleService.list(new QueryWrapper<DfRule>());

        //DfRule dfRule = new DfRule();
        //if (CollectionUtil.isNotEmpty(dfRuleList)) {
        //    dfRule = dfRuleList.get(0);
        //}

        return R.ok().put("data", dfRuleList);
    }

    //根据id获取打分规则
    @ResponseBody
    @GetMapping("/queryDfRuleById")
    public R queryDfRuleById(@RequestParam Map<String, Object> params){
        logger.info("[queryDfRuleById] params={}", params);

        String id = MapUtil.getStr(params, "id");
        DfRule dfRule = dfRuleService.getById(id);

        return R.ok().put("data", dfRule);
    }




    //新增打分规则
    @ResponseBody
    @PostMapping("/addOrUpdateDfRule")
    @SysLog("新增或修改打分规则")
    public R addOrUpdateDfRule(@RequestBody DfRule dfRule) {

        logger.info("[addOrUpdateDfRule] dfRule={}", dfRule);

        if (dfRule.getId() != null && dfRule.getId() > 0) {
            dfRuleService.updateById(dfRule);
        } else {
            dfRule.setCreateBy(getUser().getName());
            dfRuleService.save(dfRule);
        }

        return R.ok();
    }



    //新增打分记录和详情
    @ResponseBody
    @PostMapping("/generateDfRecordAndDetail")
    @SysLog("新增打分记录和详情")
    public R generateDfRecordAndDetail(@RequestBody DfRecord dfRecord) {

        //这里先只有一个规则
        //dfRecord.setRuleId(1);

        logger.info("[generateDfRecordAndDetail] dfRecord={}", dfRecord);

        //判断当前月 该规则 是否已经有打分记录

        String dfMonth = dfRecord.getDfMonth();
        int ruleId = dfRecord.getRuleId();

        List<DfRecord> list = dfRecordService.list(new QueryWrapper<DfRecord>().eq("df_month", dfMonth).eq("rule_id", ruleId));
        if (CollectionUtil.isNotEmpty(list)) {
            throw new RRException("该打分规则当月已存在打分记录");
        }

        int recordId = dfDetailService.generateDfRecordAndDetail(dfRecord, getUser().getName());


        return R.ok().put("data", recordId);
    }

    //分页查看打分记录
    @ResponseBody
    @GetMapping("/queryDfRecord")
    public R queryDfRecord(@RequestParam Map<String, Object> params){
        logger.info("[queryDfRecord] params={}", params);

        IPage<DfRecord> page = dfRecordService.queryDfRecord(params);

        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }


    //进入打分详情页，获取打分详情
    @ResponseBody
    @GetMapping("/queryDfDetailByRecordId")
    public R queryDfDetailByRecordId(@RequestParam Map<String, Object> params){
        logger.info("[queryDfDetailByRecordId] params={}", params);

        DfRecord dfRecord = dfRecordService.queryDfDetailByRecordId(params);
        //获取单位名称
        dfRecord.getDfDetailList().forEach(e -> {

            Map<String, Object> deptMap = dfConfigService.getDeptByDeptId(MapUtil.getStr(e, "deptId"));

            e.put("deptName", MapUtil.getStr(deptMap, "deptSName"));

        });

        //获取子项打分头
        List<Map<String, Object>> configList = dfRecordService.queryDfTheadByRecordId(dfRecord.getId());
        dfRecord.setDfTheadList(configList);

        //获取父打分头
        List<Map<String, Object>> parentConfigList = dfRecordService.generateParentConfigList(configList);
        dfRecord.setDfParentTheadList(parentConfigList);

        return R.ok().put("data", dfRecord);
    }

    //保存打分详情
    @ResponseBody
    @PostMapping("/saveDfDetail")
    @SysLog("保存打分详情")
    public R saveDfDetail(@RequestBody List<DfDetailForm> dfDetailFormList, @RequestParam Map<String, Object> params) {

        logger.info("[saveDfDetail] dfDetailFormList={},params={}", dfDetailFormList, params);

        dfDetailService.saveDfDetail(dfDetailFormList, params);


        return R.ok();
    }

    //删除打分记录和详情
    @ResponseBody
    @GetMapping("/deleteDfRecordAndDetail")
    @SysLog("删除打分记录和详情")
    public R deleteDfRecordAndDetail(@RequestParam int recordId) {

        logger.info("[deleteDfRecordAndDetail] recordId={}", recordId);

        dfDetailService.deleteDfRecordAndDetail(recordId);


        return R.ok();
    }

    //根据deptId和年月获取当月该单位打分一级项目的分数
    @ResponseBody
    @GetMapping("/queryDfDetailByDeptIdAndDate")
    public R queryDfDetailByDeptIdAndDate(@RequestParam String dfMonth,@RequestParam String deptId) {

        logger.info("[queryDfDetailByDeptIdAndDate] dfMonth={}, deptId={}", dfMonth, deptId);

        List<Map<String, Object>> list = dfDetailService.queryDfDetailByDeptIdAndDate(dfMonth,deptId);


        return R.ok().put("data", list);
    }

    //根据年月和一级打分项id查看该一级打分项所有单位打分详情
    @ResponseBody
    @GetMapping("/queryDfDetailByDfMonthAndParentConfigId")
    public R queryDfDetailByDfMonthAndParentConfigId(@RequestParam Map<String, Object> params){
        logger.info("[queryDfDetailByDfMonthAndParentConfigId] params={}", params);
        String parentConfigId = MapUtil.getStr(params, "parentConfigId");

        DfRecord dfRecord = dfRecordService.queryDfDetailByDfMonthAndParentConfigId(params);
        //获取单位名称
        dfRecord.getDfDetailList().forEach(e -> {

            Map<String, Object> deptMap = dfConfigService.getDeptByDeptId(MapUtil.getStr(e, "deptId"));

            e.put("deptName", MapUtil.getStr(deptMap, "deptSName"));

        });

        //获取子项打分头
        List<Map<String, Object>> configList = dfRecordService.queryDfTheadByRecordId(dfRecord.getId());
        dfRecord.setDfTheadList(configList.stream().filter(e -> MapUtil.getStr(e, "parentId").equals(parentConfigId)).collect(Collectors.toList()));

        //获取父打分头
        List<Map<String, Object>> parentConfigList = dfRecordService.generateParentConfigList(configList);

        dfRecord.setDfParentTheadList(parentConfigList.stream().filter(e -> MapUtil.getStr(e, "id").equals(parentConfigId)).collect(Collectors.toList()));

        return R.ok().put("data", dfRecord);
    }


    //根据deptId，type和年月获取当月该单位打分一级项目的分数 new
    @ResponseBody
    @GetMapping("/queryDfDetailByDeptIdAndDateNew")
    public R queryDfDetailByDeptIdAndDateNew(@RequestParam String dfMonth,@RequestParam String deptId, @RequestParam String type) {


        logger.info("[queryDfDetailByDeptIdAndDateNew] dfMonth={}, deptId={}, type={}", dfMonth, deptId,type);

        List<Map<String, Object>> list = dfDetailService.queryDfDetailByDeptIdAndDateNew(dfMonth, deptId, type);


        return R.ok().put("data", list);
    }

    //根据年月和category/type查看该category打分项所有单位打分详情
    @ResponseBody
    @GetMapping("/queryDfDetailByDfMonthAndTypeAndCategory")
    public R queryDfDetailByDfMonthAndTypeAndCategory(@RequestParam String dfMonth, @RequestParam String category, @RequestParam String type) {

        logger.info("[queryDfDetailByDfMonthAndTypeAndCategory] dfMonth={}, category={}, type={}", dfMonth, category, type);

        //#0情报信息 1数据赋能 2综合指挥 3警情质量考核 4情报线索 5重点目标 6科技信息化
        if ("0".equals(category)) {
            List<DfQbxx> list = dfQbxxService.list(new QueryWrapper<DfQbxx>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));
            list.forEach(e -> {
                e.setS9(NumberUtil.mul(e.getS9(), dfConsts.getQBXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            });
            return R.ok().put("data", list);
        }else if("1".equals(category)){
            List<DfSjfn> list = dfSjfnService.list(new QueryWrapper<DfSjfn>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));
            list.forEach(e -> {
                e.setS2(NumberUtil.mul(e.getS2(), dfConsts.getSJFN()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS4(NumberUtil.mul(e.getS4(), dfConsts.getSJFN()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS6(NumberUtil.mul(e.getS6(), dfConsts.getSJFN()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS17(NumberUtil.mul(e.getS9(), dfConsts.getSJFN()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setTotal1(NumberUtil.add(e.getS2(), e.getS4(), e.getS6(), e.getS17()).toString());
            });
            return R.ok().put("data", list);
        }else if("2".equals(category)){
            List<DfZhzh> list = dfZhzhService.list(new QueryWrapper<DfZhzh>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));

            list.forEach(e -> {
                e.setS4(NumberUtil.mul(e.getS4(), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS7(NumberUtil.mul(e.getS7(), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS10(NumberUtil.mul(e.getS10(), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS13(NumberUtil.mul(e.getS13(), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS15(NumberUtil.mul(e.getS15(), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS17(NumberUtil.mul(e.getS17(), dfConsts.getZHZH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                e.setTotal(e.getS99());
            });

            return R.ok().put("data", list);
        }else if("3".equals(category)){
            List<DfJqzlkh> list = dfJqzlkhService.list(new QueryWrapper<DfJqzlkh>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));

            list.forEach(e -> {
                e.setS3(NumberUtil.mul(e.getS3(), dfConsts.getJQZLKH()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            });

            return R.ok().put("data",list );
        }else if("4".equals(category)){
            List<DfQbxs> list = dfQbxsService.list(new QueryWrapper<DfQbxs>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));

            list.forEach(e -> {
                e.setS1(NumberUtil.mul(e.getS1(), dfConsts.getQBXS()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS2(NumberUtil.mul(e.getS2(), dfConsts.getQBXS()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS3(NumberUtil.mul(e.getS3(), dfConsts.getQBXS()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setTotal(NumberUtil.sub(NumberUtil.add(e.getS1(), e.getS2()).toString(), e.getS3()).toString());
            });

            return R.ok().put("data",list );
        }else if("5".equals(category)){

            List<DfZdmb> list = dfZdmbService.list(new QueryWrapper<DfZdmb>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));

            list.forEach(e -> {
                e.setS1(NumberUtil.mul(e.getS1(), dfConsts.getZDMB()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            });

            return R.ok().put("data",list );

        }else if("6".equals(category)){//
            List<DfSjfn> list = dfSjfnService.list(new QueryWrapper<DfSjfn>().eq("df_month", dfMonth).eq("type", type).orderByAsc("length(dept_id)", "dept_id"));
            list.forEach(e -> {
                e.setS16(NumberUtil.mul(e.getS16(), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS18(NumberUtil.mul(e.getS18(), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS19(NumberUtil.mul(e.getS19(), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS20(NumberUtil.mul(e.getS20(), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS21(NumberUtil.mul(e.getS21(), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setS22(NumberUtil.mul(e.getS22(), dfConsts.getKJXX()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                e.setTotal2(NumberUtil.add(e.getS16(), e.getS18(), e.getS19(), e.getS20(), e.getS21(), e.getS22()).toString());
            });
            return R.ok().put("data", list);
        }

        return R.ok().put("data", CollectionUtil.newArrayList());


    }





}
