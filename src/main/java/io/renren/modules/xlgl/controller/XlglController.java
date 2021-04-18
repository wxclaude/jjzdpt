package io.renren.modules.xlgl.controller;

import java.net.URL;
import java.util.*;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.dm.entity.DianMingEntity;
import io.renren.modules.jcj.entity.YdjcjEntity;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.video.entity.JyVideoEntity;
import io.renren.modules.xlgl.entity.TdkEntity;
import io.renren.modules.xlgl.entity.TsbEntity;
import io.renren.modules.xlgl.entity.TxlEntity;
import io.renren.modules.xlgl.entity.TxlInfoEntity;
import io.renren.modules.xlgl.service.TdkService;
import io.renren.modules.xlgl.service.TsbService;
import io.renren.modules.xlgl.service.TxlService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-14 10:40:59
 */
@RestController
@RequestMapping("xlgl")
public class XlglController extends AbstractController {

    @Autowired
    private TsbService tsbService;
    @Autowired
    private TdkService tdkService;
    @Autowired
    private TxlService txlService;

    //根据设备编号分页查询设备信息
    @ResponseBody
    @GetMapping("/querySbByPage")
    public R querySbByPage(@RequestParam Map<String, Object> params){
        logger.info("[queryByDate] params={}", params);

        IPage<TsbEntity> page = tsbService.querySbByPage(params);


        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //根据设备编号分页查询设备信息
    @ResponseBody
    @GetMapping("/querySbByPageNew")
    public R querySbByPageNew(@RequestParam Map<String, Object> params){
        logger.info("[querySbByPageNew] params={}", params);

        IPage<Map<String,Object>> page = tsbService.querySbByPageNew(params);


        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //根据设备编号分页查询设备信息 视图
    @ResponseBody
    @GetMapping("/querySbByPageST")
    public R querySbByPageST(@RequestParam Map<String, Object> params){
        logger.info("[querySbByPageST] params={}", params);

        IPage<Map<String,Object>> page = tsbService.querySbByPageST(params);


        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //查询设备总数 正常数 报废数
    @ResponseBody
    @GetMapping("/querySbCount")
    public R querySbCount(@RequestParam Map<String, Object> params){
        logger.info("[querySbCount] params={}", params);

        Map<String,Object> map = tsbService.querySbCount(params);


        return R.ok().put("data", map);
    }

    @GetMapping("/exportSbST")
    public void exportSbST(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportSbST] params={}", params);
        //假装分页，获取全部
        params.put("page", 1);
        params.put("limit", 999999);

        IPage<Map<String,Object>> page = tsbService.querySbByPageST(params);


        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/sbst.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", page.getRecords());

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = "设备信息.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }

    //根据设备编号分页查询设备信息 的select查询条件
    @ResponseBody
    @GetMapping("/querySbSearchCondition")
    public R querySbSearchCondition(@RequestParam int type){
        //0单位 1设备类型  2网络类型 3设备状态
        logger.info("[querySbSearchCondition] type={}", type);

        List<Map<String,Object>> list = tsbService.querySbSearchCondition(type);


        return R.ok().put("data", list);
    }

    //新增设备
    @ResponseBody
    @PostMapping("/addSb")
    @SysLog("新增设备")
    public R addSb(@RequestBody TsbEntity tsbEntity ) {

        logger.info("[addSb] tsbEntity={}", tsbEntity);

        List<TsbEntity> exsitedTsbList = tsbService.list(new QueryWrapper<TsbEntity>().eq("sbbh", tsbEntity.getSbbh()));
        if (CollectionUtil.isNotEmpty(exsitedTsbList)) {
            throw new RRException("该设备编号已经存在");
        }
        tsbEntity.setCreateBy(getUser().getName());
        tsbService.save(tsbEntity);

        return R.ok();
    }

    //修改设备
    @ResponseBody
    @PostMapping("/updateSb")
    @SysLog("修改设备")
    public R updateSb(@RequestBody TsbEntity tsbEntity ) {

        logger.info("[addSb] tsbEntity={}", tsbEntity);
        List<TsbEntity> exsitedTsbList = tsbService.list(new QueryWrapper<TsbEntity>().eq("sbbh", tsbEntity.getSbbh()));
        if ((CollectionUtil.isNotEmpty(exsitedTsbList) && !exsitedTsbList.contains(tsbEntity))
                || (CollectionUtil.isNotEmpty(exsitedTsbList) && exsitedTsbList.size() > 1)) {
            throw new RRException("该设备编号已经存在");
        }
        tsbEntity.setCreateBy(getUser().getName());
        tsbService.updateById(tsbEntity);

        return R.ok();
    }

    @ResponseBody
    @PostMapping("/deleteSbBatch")
    @SysLog("删除设备")
    public R deleteSbBatch(@RequestBody List<TsbEntity> tsbEntityList){
        SysUserEntity user = getUser();

        logger.info("[deleteSbBatch] tsbEntityList={}, userName={}", tsbEntityList, user.getName());

        tsbService.deleteSbBatch(tsbEntityList);

        return R.ok();
    }

    //根据设备编号获取唯一设备
    @ResponseBody
    @GetMapping("/querySbBySbbh")
    public R querySbBySbbh(@RequestParam Map<String, Object> params){
        logger.info("[querySbBySbbh] params={}", params);

        TsbEntity tsbEntity = tsbService.getOne(new QueryWrapper<TsbEntity>().eq("sbbh", MapUtil.getStr(params, "sbbh")));


        return R.ok().put("data", tsbEntity);
    }


    /**
     * ***************************** 端口 *******************************
     */

    //根据端口编号分页查询设备信息
    @ResponseBody
    @GetMapping("/queryDkByPage")
    public R queryDkByPage(@RequestParam Map<String, Object> params){
        logger.info("[queryDkByPage] params={}", params);

        List<String> sbIdList = tsbService.querySbIdAll(params);

        if (CollectionUtil.isEmpty(sbIdList)) {
            return R.ok().put("data", new ArrayList<>()).put("count", 0);
        }


        IPage<TdkEntity> page = tdkService.queryDkByPage(params,sbIdList);

        page.getRecords().stream().forEach(e->{
            e.setSbMap(txlService.getSbById(e.getSbId()));
        });


        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //根据设备编号分页查询端口信息
    @ResponseBody
    @GetMapping("/queryDkDetailBySbIdPage")
    public R queryDkDetailBySbIdPage(@RequestParam Map<String, Object> params){
        logger.info("[queryDkDetailBySbIdPage] params={}", params);

        IPage<TdkEntity> page = tdkService.queryDkDetailBySbIdPage(params);

        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //根据设备编号查看该设备板卡号
    @ResponseBody
    @GetMapping("/queryDkBkhBySbId")
    public R queryDkBkhBySbId(@RequestParam Map<String, Object> params){
        logger.info("[queryDkBkhBySbId] params={}", params);

        List<Map<String,Object>> list = tdkService.queryDkBkhBySbId(params);

        return R.ok().put("data", list);
    }

    //设备新增页面  根据设备编号 查询设备已经端口信息
    @ResponseBody
    @GetMapping("/querySbDkDetailBySbId")
    public R querySbDkDetailBySbId(@RequestParam Map<String, Object> params){
        logger.info("[querySbDkDetailBySbId] params={}", params);

        List<Map<String,Object>> list = tdkService.querySbDkDetailBySbId(params);

        return R.ok().put("data", list);
    }


    //批量新增端口
    @ResponseBody
    @PostMapping("/addDkBatch")
    @SysLog("新增端口")
    public R addDkBatch(@RequestBody List<TdkEntity> list ) {

        logger.info("[addDkBatch] list={}", list);
        if (CollectionUtil.isEmpty(list)) {
            throw new RRException("请填写端口信息");
        }

        //判断是否存在相同板卡号的新增数据
        //for (int i = 0; i < list.size(); i++) {
        //    if(list.get(i).)
        //}

        List<TdkEntity> tdkEntityList = new ArrayList<>();

        list.forEach(e->{
            for (int i = e.getDkhqs(); i < e.getDks() + e.getDkhqs(); i++) {
                TdkEntity tdkEntity = new TdkEntity();
                tdkEntity.setCreateBy(getUser().getName());
                tdkEntity.setDklx(e.getDklx());
                tdkEntity.setBkh(e.getBkh());
                tdkEntity.setDkh(String.valueOf(i));
                tdkEntity.setSbId(e.getSbId());
                tdkEntityList.add(tdkEntity);
            }
        });


        tdkService.saveBatchCC(tdkEntityList);

        return R.ok();
    }

    //修改端口
    @ResponseBody
    @PostMapping("/updateDk")
    @SysLog("修改端口")
    public R updateDK(@RequestBody TdkEntity tdkEntity ) {

        logger.info("[updateDk] tdkEntity={}", tdkEntity);
        tdkEntity.setCreateBy(getUser().getName());
        tdkService.updateById(tdkEntity);

        return R.ok();
    }

    @ResponseBody
    @PostMapping("/deleteDkBatch")
    @SysLog("删除端口")
    public R deleteDkBatch(@RequestBody List<TdkEntity> tdkEntityList){
        SysUserEntity user = getUser();

        logger.info("[deleteSbBatch] tdkEntityList={}, userName={}", tdkEntityList, user.getName());

        tdkService.deleteDkBatch(tdkEntityList);

        return R.ok();
    }

    //根据设备Id获取所有端口
    @ResponseBody
    @GetMapping("/queryDkBySbId")
    public R queryDkBySbId(String sbId){
        logger.info("[queryDkBySbId] sbId={}", sbId);

        //List<TdkEntity> list = tdkService.list(new QueryWrapper<TdkEntity>().eq("sb_id", sbId));
        List<TdkEntity> list = tdkService.queryDkBySbId(sbId);

        return R.ok().put("data", list);
    }

    /**
     * ***************************** 线路 *********************************
     */

    //根据线路编号分页查询线路信息
    @ResponseBody
    @GetMapping("/queryXlByPage")
    public R queryXlByPage(@RequestParam Map<String, Object> params){
        logger.info("[queryXlByPage] params={}", params);

        List<String> sbIdList = tsbService.querySbIdAll(params);

        if (CollectionUtil.isEmpty(sbIdList)) {
            return R.ok().put("data", new ArrayList<>()).put("count", 0);
        }

        IPage<TxlEntity> page = txlService.queryXlByPage(params,sbIdList);


        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //新增线路
    @ResponseBody
    @PostMapping("/addXl")
    @SysLog("新增线路")
    public R addXl(@RequestBody TxlEntity txlEntity ) {

        logger.info("[addXl] txlEntity={}", txlEntity);

        //自动生成线路号 规则 X 年月日 4位随机数

        String xlbh = "X";
        xlbh += DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);

        //获取当天最大的编号
        List<TxlEntity> maxXlbhTodayEntityList = txlService.getListLikeXlbh(xlbh);

        TxlEntity maxXlbhTodayEntity = null;
        if (CollectionUtil.isNotEmpty(maxXlbhTodayEntityList)) {
            maxXlbhTodayEntity = maxXlbhTodayEntityList.get(0);
        }

        if (maxXlbhTodayEntity == null) {
            xlbh += "1001";
        } else {
            try {
                int nowNum = Integer.parseInt(StrUtil.subWithLength(maxXlbhTodayEntity.getXlbh(), 9, 4));
                nowNum++;
                xlbh += String.valueOf(nowNum);
            }catch (Exception e){
                throw new RRException("系统异常(NFE),请联系管理员");
            }
        }

        txlEntity.setXlbh(xlbh);

        List<TxlEntity> exsitedTxlList = txlService.getListByXlbh(txlEntity.getXlbh());
        if (CollectionUtil.isNotEmpty(exsitedTxlList)) {
            throw new RRException("该线路编号已经存在");
        }

        if (txlEntity.getXlDkId().equals(txlEntity.getSlDkId())) {
            throw new RRException("上联端口与下联端口不能相同");
        }

        //判断上下联端口是否已经被使用了
        List<TxlEntity> slList = txlService.listBySlDkIdOrXlDkId(txlEntity.getSlDkId());
        if (CollectionUtil.isNotEmpty(slList)){
            throw new RRException("该上联端口已经被使用");
        }

        List<TxlEntity> xlList = txlService.listBySlDkIdOrXlDkId(txlEntity.getXlDkId());
        if (CollectionUtil.isNotEmpty(xlList)){
            throw new RRException("该下联端口已经被使用");
        }

        //同一设备的两个端口不能当做统一线路的上下联
        if (txlEntity.getSlSbId().equals(txlEntity.getXlSbId())) {
            throw new RRException("线路的上下联端口不能是同一设备的");
        }

        //闭环判断
        //判断该线路（A）的上下联端口对应的上下联设备，是否存在在其它的线路中(B)上下联端口对应的设备刚好A的上下联端口对应的设备相反
        //TxlEntity bihuanTxlEntity = txlService.getOne(new QueryWrapper<TxlEntity>().and(i -> i.eq("sl_sb_id", xlSb.getId()).eq("xl_sb_id", slSb.getId())));
        //if (bihuanTxlEntity != null) {
        //    throw new RRException("线路闭环无法添加");
        //}

        txlEntity.setCreateBy(getUser().getName());
        txlService.saveCC(txlEntity);

        return R.ok();
    }

    //修改线路
    @ResponseBody
    @PostMapping("/updateXl")
    @SysLog("修改线路")
    public R updateXl(@RequestBody TxlEntity txlEntity ) {

        logger.info("[updateXl] txlEntity={}", txlEntity);

        List<TxlEntity> exsitedTxlList = txlService.getListByXlbh( txlEntity.getXlbh());
        if ((CollectionUtil.isNotEmpty(exsitedTxlList) && exsitedTxlList.size() > 1)
            ||(CollectionUtil.isNotEmpty(exsitedTxlList) && !exsitedTxlList.contains(txlEntity))) {

            throw new RRException("该线路编号已经存在");
        }

        //判断上下联端口是否已经被使用了
        List<TxlEntity> slList = txlService.listBySlDkIdOrXlDkId(txlEntity.getSlDkId());
        if ((CollectionUtil.isNotEmpty(slList) && slList.size() > 1)
            || (CollectionUtil.isNotEmpty(slList) && !slList.contains(txlEntity))){
            throw new RRException("该上联端口已经被使用");
        }

        List<TxlEntity> xlList = txlService.listBySlDkIdOrXlDkId(txlEntity.getXlDkId());
        if ((CollectionUtil.isNotEmpty(xlList) && xlList.size() > 1)
            || (CollectionUtil.isNotEmpty(xlList) && !xlList.contains(txlEntity))){
            throw new RRException("该下联端口已经被使用");
        }

        //同一设备的两个端口不能当做统一线路的上下联
        if (txlEntity.getSlSbId().equals(txlEntity.getXlSbId())) {
            throw new RRException("线路的上下联端口不能是同一设备的");
        }
        //闭环判断
        //判断该线路（A）的上下联端口对应的上下联设备，是否存在在其它的线路中(B)上下联端口对应的设备刚好A的上下联端口对应的设备相反
        //TxlEntity bihuanTxlEntity = txlService.getOne(new QueryWrapper<TxlEntity>().and(i -> i.eq("sl_sb_id", xlSb.getId()).eq("xl_sb_id", slSb.getId())));
        //if (bihuanTxlEntity != null) {
        //    throw new RRException("线路闭环无法添加");
        //}

        txlEntity.setCreateBy(getUser().getName());
        txlService.updateByIdCC(txlEntity);

        return R.ok();
    }

    @ResponseBody
    @PostMapping("/deleteXlBatch")
    @SysLog("删除线路")
    public R deleteXlBatch(@RequestBody List<TxlEntity> txlEntityList){
        SysUserEntity user = getUser();

        logger.info("[deleteXlBatch] txlEntityList={}, userName={}", txlEntityList, user.getName());

        txlService.deleteXlBatch(txlEntityList);

        return R.ok();
    }

    //根据线路编号查询线路上下接口/设备信息
    @ResponseBody
    @GetMapping("/queryXlInfo")
    public R queryXlInfo(@RequestParam Map<String, Object> params){
        logger.info("[queryXlInfo] params={}", params);

        List<TxlInfoEntity> list = txlService.queryXlInfo(params);

        list.stream().filter(e -> TxlInfoEntity.TYPE_SB.equals(e.getType())).forEach(e->{
            e.setSbMap(txlService.getSbById(e.getSbid()));
        });


        return R.ok().put("data", list);
    }

    @GetMapping("/exportXl")
    public void exportXl(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[exportXl] params={}", params);
        //假装分页，获取全部
        params.put("page", 1);
        params.put("limit", 999999);

        List<String> sbIdList = tsbService.querySbIdAll(params);

        List<TxlEntity> list = null;

        if (CollectionUtil.isNotEmpty(sbIdList)) {
            IPage<TxlEntity> page = txlService.queryXlByPage(params,sbIdList);
            list = page.getRecords();
        }

        //
        list.stream().forEach(e -> {
            e.setSlDkh(StrUtil.join("/", e.getSlDklx(), e.getSlBkh(), e.getSlDkh()));
            e.setXlDkh(StrUtil.join("/", e.getXlDklx(), e.getXlBkh(), e.getXlDkh()));
            e.setSlSbMap(txlService.getSbById(e.getSlSbId()));
            e.setXlSbMap(txlService.getSbById(e.getXlSbId()));
        });

        URL resource = Thread.currentThread().getContextClassLoader().getResource("template/xl.xlsx");
        TemplateExportParams templateExportParams = new TemplateExportParams(resource.getPath());

        Map<String, Object> excelMap = new HashMap<>();
        excelMap.put("list", list);

        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams, excelMap);

        String fileName = "线路.xlsx";
        if (workbook != null) {
            downLoadExcel(fileName, response, workbook);
        }

    }


    public static void main(String[] args) {
        int nowNum = Integer.parseInt(StrUtil.subWithLength("X202004181001", 9, 4));
        System.out.println(nowNum);
    }

}
