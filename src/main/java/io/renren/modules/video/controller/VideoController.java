package io.renren.modules.video.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.utils.HttpContextUtils;
import io.renren.common.utils.IPUtils;
import io.renren.common.utils.MyUtils;
import io.renren.common.utils.R;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.video.consts.VideoConsts;
import io.renren.modules.video.entity.DwbzEntity;
import io.renren.modules.video.entity.JyVideoEntity;
import io.renren.modules.video.entity.XmConfigEntity;
import io.renren.modules.video.service.DwbzService;
import io.renren.modules.video.service.VideoService;
import io.renren.modules.video.service.XmConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Struct;
import java.text.DateFormat;
import java.util.*;

/**
 * @author tomchen
 * @date 2020-03-31
 */
@RestController
@RequestMapping("/video")
public class VideoController extends AbstractController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private DwbzService dwbzService;

    @Autowired
    private XmConfigService xmConfigService;

    //根据类型查询该类型单位最近10次巡检的日期
    @ResponseBody
    @GetMapping("/queryLastVideoDateByType")
    public R queryLastVideoDateByType(String type){
        logger.info("[queryLastVideoDateByType] type={}", type);
        List<XmConfigEntity> xmConfigEntityList = xmConfigService.list(new QueryWrapper<XmConfigEntity>().eq("dw", type));
        if (CollectionUtil.isEmpty(xmConfigEntityList)
                && !VideoConsts.TYPE_ZHZX.equals(type)
                && !VideoConsts.TYPE_PCSZJ.equals(type)
                && !VideoConsts.TYPE_JJ.equals(type)
                && !VideoConsts.TYPE_RL.equals(type)) {

            return R.ok().put("data", new ArrayList<>());
        }

        String xmIds = getXmIdStrFromList(xmConfigEntityList);
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("xmIds", MyUtils.formatSqlIn(xmIds));

        List<String> list = videoService.queryLastVideoDateByType(params);

        //String lastDate = MapUtil.getStr(list, "lastDate");
        //return R.ok().put("data", DateUtil.formatDate(DateUtil.parseDate(lastDate).toJdkDate()));
        return R.ok().put("data", list);
    }

    //根据类型和日期查询视频巡检记录
    @ResponseBody
    @GetMapping("/queryVideoDataByTypeAndDate")
    public R queryVideoDataByTypeAndDate(String date, String type){
        logger.info("[queryVideoDataByTypeAndDate] date={}, type={}", date, type);

        //获取txmconfig中单位对应的
        //String xmIds = xmConfigService.queryXmIdsByType(type);
        List<XmConfigEntity> xmConfigList = xmConfigService.list(new QueryWrapper<XmConfigEntity>().eq("dw", type).orderByAsc("sort"));
        if (CollectionUtil.isEmpty(xmConfigList)) {
            if (!type.equals(VideoConsts.TYPE_PCSZJ) && !type.equals(VideoConsts.TYPE_JJ) && !type.equals(VideoConsts.TYPE_RL)) {
                return R.ok().put("data", new ArrayList<>());
            }
        }

        List<Map<String, Object>> list = videoService.queryVideoDataByTypeAndDate(date, type, xmConfigList);


        return R.ok().put("data", list);
    }

    //巡检详情
    @ResponseBody
    @GetMapping("/queryVideoDataDetail")
    public R queryVideoDataDetail(@RequestParam Map<String, Object> params) {

        logger.info("[queryVideoDataDetail] params={}", params);
        IPage<Map<String,Object>> page = videoService.queryVideoDataDetailPage(params);
        page.getRecords().forEach(e -> {
            DwbzEntity dwbzEntity = videoService.getLastBzByBh(MapUtil.getStr(e, "监控点SN"));
            e.put("备注", dwbzEntity == null ? "" : dwbzEntity.getBz());
        });



        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //故障点位详情（新）根据配合id获取巡检详情
    @ResponseBody
    @GetMapping("/queryErrorVideoDataDetailByConfigId")
    public R queryVideoDataDetailByConfigId(@RequestParam Map<String, Object> params) {

        logger.info("[queryVideoDataDetailByConfigId] params={}", params);
        int configId = MapUtil.getInt(params, "configId");
        XmConfigEntity xmConfig = xmConfigService.getById(configId);
        params.put("xmConfig", xmConfig);

        List<Map<String,Object>> list = videoService.queryErrorVideoDataDetailByConfigId(params);
        list.forEach(e -> {
            DwbzEntity dwbzEntity = videoService.getLastBzByBh(MapUtil.getStr(e, "dwid"));
            e.put("bz", dwbzEntity == null ? "" : dwbzEntity.getBz());
        });



        return R.ok().put("data", list).put("count", 1);
    }

    //点位详情（新）根据配合id获取巡检详情
    @GetMapping("/queryDwVideoDataDetailByConfigId")
    public R queryDwVideoDataDetailByConfigId(@RequestParam Map<String, Object> params) {

        logger.info("[queryDwVideoDataDetailByConfigId] params={}", params);
        int configId = MapUtil.getInt(params, "configId");
        XmConfigEntity xmConfig = xmConfigService.getById(configId);
        params.put("xmConfig", xmConfig);

        List<Map<String,Object>> list = videoService.queryDwVideoDataDetailByConfigId(params);
        list.forEach(e -> {
            DwbzEntity dwbzEntity = videoService.getLastBzByBh(MapUtil.getStr(e, "dwid"));
            e.put("bz", dwbzEntity == null ? "" : dwbzEntity.getBz());
        });



        return R.ok().put("data", list).put("count", 1);
    }

    //正常点位详情（新）根据配置id获取巡检详情
    @ResponseBody
    @GetMapping("/queryNormalVideoDataDetailByConfigId")
    public R queryNormalVideoDataDetailByConfigId(@RequestParam Map<String, Object> params) {

        logger.info("[queryNormalVideoDataDetailByConfigId] params={}", params);
        int configId = MapUtil.getInt(params, "configId");
        XmConfigEntity xmConfig = xmConfigService.getById(configId);
        params.put("xmConfig", xmConfig);
        IPage<Map<String,Object>> page = videoService.queryNormalVideoDataDetailByConfigId(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //合同点位详情 根据配置id获取巡检详情
    @ResponseBody
    @GetMapping("/queryHtVideoDataDetailByConfigId")
    public R queryHtVideoDataDetailByConfigId(@RequestParam Map<String, Object> params) {

        logger.info("[queryHtVideoDataDetailByConfigId] params={}", params);
        int configId = MapUtil.getInt(params, "configId");
        XmConfigEntity xmConfig = xmConfigService.getById(configId);
        params.put("xmConfig", xmConfig);
        IPage<Map<String,Object>> page = videoService.queryHtVideoDataDetailByConfigId(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }

    //进入新增巡检页面时 根据类型获取上次巡检结果
    @ResponseBody
    @GetMapping("/queryLastVideoDataByType")
    public R queryLastVideoDataByType(@RequestParam Map<String, Object> params) {

        logger.info("[queryLastVideoDataByType] params={}", params);
        String type = MapUtil.getStr(params, "type");
        List<XmConfigEntity> xmConfigList = xmConfigService.list(new QueryWrapper<XmConfigEntity>().eq("dw", type).orderByAsc("sort"));
        params.put("xmConfigList", xmConfigList);
        if (CollectionUtil.isEmpty(xmConfigList)) {
            return R.ok().put("data", new ArrayList<>());
        }

        List<Map<String, Object>> list = videoService.queryLastVideoDataByType(params);
        String xjsj = "";
        if (CollectionUtil.isNotEmpty(list)) {
            xjsj = StrUtil.subBefore(String.valueOf(list.get(0).get("xjsj")), ".", true);
        }

        return R.ok().put("data", list).put("xjsj", xjsj);
    }


    //新增巡检
    @ResponseBody
    @PostMapping("/addVideoCheckBatch")
    @SysLog("新增巡检")
    public R addVideoCheckBatch(@RequestBody List<JyVideoEntity> list) {

        logger.info("[addVideoCheckBatch] list={}", list);
        videoService.addVideoForm(list);

        return R.ok();
    }


    //统计
    @ResponseBody
    @GetMapping("/queryVideoStatisticsByTypeAndDate")
    public R queryVideoStatisticsByTypeAndDate(@RequestParam Map<String, Object> params) {

        logger.info("[queryVideoStatisticsByTypeAndDate] params={}", params);
        String type = MapUtil.getStr(params, "type");
        List<XmConfigEntity> xmConfigList = xmConfigService.list(new QueryWrapper<XmConfigEntity>().eq("dw", type).orderByAsc("sort"));
        params.put("xmConfigList", xmConfigList);
        if (CollectionUtil.isEmpty(xmConfigList)) {
            if (!VideoConsts.TYPE_PCSZJ.equals(type) && !VideoConsts.TYPE_JJ.equals(type)) {

                return R.ok().put("data", new ArrayList<>());
            }
        }

        List<Map<String, Object>> list = videoService.queryVideoStatisticsByTypeAndDate(params);

        return R.ok().put("data", list);
    }

    //修改故障点位备注
    @ResponseBody
    @PostMapping("/updateVideoBz")
    @SysLog("修改故障点位备注")
    public R updateVideoBz(@RequestBody Map<String, Object> params) {

        logger.info("[updateVideoBz] params={}", params);

        params.put("userId", getUser().getName());

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        params.put("ip", IPUtils.getIpAddr(request));

        videoService.updateVideoBz(params);

        return R.ok();
    }

    //不登录 修改故障点位备注
    @ResponseBody
    @PostMapping("/queryUpdateVideoBz")
    public R queryUpdateVideoBz(@RequestBody Map<String, Object> params) {

        logger.info("[queryUpdateVideoBz] params={}", params);

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        params.put("ip", IPUtils.getIpAddr(request));

        videoService.updateVideoBz(params);

        return R.ok();
    }


    //根据bh(dwid)/日期 获取该点位的历史备注信息
    @ResponseBody
    @GetMapping("/queryVideoBzDataPage")
    public R queryVideoBzDataPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryVideoBzDataPage] params={}", params);
        IPage<DwbzEntity> page = videoService.queryVideoBzDataPage(params);

        page.getRecords().forEach(e -> {
            e.setXcr(videoService.getPoliceNameByPoliceCode(e.getUserId()));
        });
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());

    }

    //根据XMID 获取该点位(教育/小区/内部/其它)的历史备注信息
    @ResponseBody
    @GetMapping("/queryVideoBzDataByXMIDPage")
    public R queryVideoBzDataByXMIDPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryVideoBzDataByXMIDPage] params={}", params);
        IPage<Map<String, Object>> page = videoService.queryVideoBzDataByXMIDPage(params);
        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());

    }

    //根据bh(dwid) 获取该点位的历史巡检信息
    @ResponseBody
    @GetMapping("/queryHistoryDataPage")
    public R queryHistoryDataPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryHistoryDataPage] params={}", params);
        IPage<Map<String, Object>> page = videoService.queryHistoryDataPage(params);

        return R.ok().put("data", page.getRecords()).put("count", page.getTotal());
    }


    //根据dwid/日期 获取该点位该日期的具体监控点信息
    @ResponseBody
    @GetMapping("/queryDwDetailByDwidAndDate")
    public R queryDwDetailByDwidAndDate(@RequestParam Map<String, Object> params) {

        logger.info("[queryDwDetailByDwidAndDate] params={}", params);
        List<Map<String, Object>> list = videoService.queryDwDetailByDwidAndDate(params);

        return R.ok().put("data", list);
    }

    //获取国标编码或者名称不匹配的点位信息
    @ResponseBody
    @GetMapping("/queryCompareDataPage")
    public R queryCompareDataPage(@RequestParam Map<String, Object> params) {

        logger.info("[queryCompareDataPage] params={}", params);
        List<Map<String,Object>> list = videoService.queryCompareDataPage(params);

        return R.ok().put("data", list);
    }

    //获取所有项目
    @ResponseBody
    @GetMapping("/queryAllCompareXm")
    public R queryAllCompareXm(@RequestParam Map<String, Object> params) {

        logger.info("[queryAllCompareXm] params={}", params);

        List<Map<String,Object>> list = videoService.queryAllCompareXm(params);
        Set<Map<String, Object>> set = new LinkedHashSet<>();
        list.forEach(e -> {
            set.add(e);
        });

        set.forEach(e -> {
            e.put("所属单位", StrUtil.subBefore(String.valueOf(e.get("所属单位")), " ", false));
        });


        return R.ok().put("data", set);
    }

    /**
     * *********************************  项目配置  ***********************************
     */


    //获取所有项目
    @ResponseBody
    @GetMapping("/queryAllXm")
    public R queryAllXm(@RequestParam Map<String, Object> params) {

        logger.info("[queryAllXm] params={}", params);

        List<Map<String,Object>> list = xmConfigService.queryAllXm(params);

        return R.ok().put("data", list);
    }

    //获取所有配置
    @ResponseBody
    @GetMapping("/queryAllXmConfig")
    public R queryAllXmConfig(@RequestParam Map<String, Object> params) {

        logger.info("[queryAllXm] params={}", params);

        List<XmConfigEntity> list = xmConfigService.queryAllXmConfig(params);
        list.stream().forEach(e->{
            List<String> nameList = xmConfigService.getXmNameByIds(MyUtils.formatSqlIn(e.getXmid()));
            StringBuffer sb = new StringBuffer();
            for (String name : nameList) {
                sb.append(name).append(",");
            }
            e.setXmmcShow(StrUtil.subBefore(sb.toString(), ",", true));
        });

        return R.ok().put("data", list);
    }


    //新增配置
    @ResponseBody
    @PostMapping("/addXmConfig")
    @SysLog("新增配置")
    public R addXmConfig(@RequestBody XmConfigEntity xmConfigEntity) {

        logger.info("[addXmConfig] xmConfigEntity={}", xmConfigEntity);

        xmConfigEntity.setCreateBy(getUser().getName());

        boolean added = xmConfigService.save(xmConfigEntity);

        return R.ok();
    }

    //修改配置
    @ResponseBody
    @PostMapping("/updateXmConfig")
    @SysLog("修改配置")
    public R updateXmConfig(@RequestBody XmConfigEntity xmConfigEntity) {

        logger.info("[updateXmConfig] xmConfigEntity={}", xmConfigEntity);

        xmConfigService.updateById(xmConfigEntity);

        return R.ok();
    }

    @ResponseBody
    @PostMapping("/deleteXmConfig")
    @SysLog("删除配置")
    public R deleteXmConfig(@RequestBody List<XmConfigEntity> xmConfigList){
        SysUserEntity user = getUser();

        logger.info("[deleteSbBatch] xmConfigList={}, userName={}", xmConfigList, user.getName());

        for (XmConfigEntity xmConfig : xmConfigList) {
            xmConfigService.removeById(xmConfig.getId());
        }
        return R.ok();
    }


    //获取所有单次连续离线超过4天的天网点位
    @ResponseBody
    @GetMapping("/queryTwLxData")
    public R queryTwLxData(@RequestParam Map<String, Object> params) {

        logger.info("[queryTwLxData] params={}", params);

        List<Map<String,Object>> list = videoService.queryTwLxData(params);

        for (Map<String, Object> map : list) {

        }

        return R.ok().put("data", list);
    }

    //获取保修数据
    @ResponseBody
    @GetMapping("/queryBxData")
    public R queryBxData(@RequestParam Map<String, Object> params) {

        logger.info("[queryBxDataPage] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        String kind = String.valueOf(params.get("kind"));

        IPage<DwbzEntity> pageResult = null;
        pageResult = videoService.queryBxDataPage(params);

        //这里从sqlserver库里获取点位名称所属项目等信息
        StringBuffer bhSb = new StringBuffer();
        String bhs = "";
        pageResult.getRecords().stream().forEach(e->{
            bhSb.append(e.getBh()).append(",");
        });
        if (bhSb.toString().endsWith(",")) {
            bhs = StrUtil.subBefore(bhSb.toString(), ",", true);
        } else {
            bhs = bhSb.toString();
        }

        if (StrUtil.isNotBlank(bhs)) {
            params.put("bhs", MyUtils.formatSqlIn(bhs));

            if ("1".equals(kind)) {//点位故障
                List<Map<String, Object>> list = videoService.queryGZDWData(params);

                pageResult.getRecords().forEach(e -> {
                    list.forEach(l -> {
                        if (e.getBh().equals(MapUtil.getStr(l, "V_DWId"))) {
                            e.setS1(MapUtil.getStr(l,"pointName"));
                            e.setS2(MapUtil.getStr(l,"projectName"));
                        }
                    });
                    params.put("dwid", e.getBh());
                    params.put("islast", 0);
                    params.put("bzsj", DateUtil.format(e.getCreateTime(), DatePattern.NORM_DATE_FORMAT));
                    e.setS3(videoService.queryGZDWLastUpData(params));

                    //这里每次查询的时候，查询一下该通道或点位最新的巡检记录，若是成功的，则将该条保修备注记录已修理
                    //if (e.getState() == 0) {
                    //    params.put("islast", 1);
                    //    String repairTime = videoService.queryGZDWLastUpData(params);
                    //    if (StrUtil.isNotBlank(repairTime)) {
                    //        e.setState(1);
                    //        e.setRepairTime(repairTime);
                    //        dwbzService.updateById(e);
                    //        //若同一点位有多次保修，成功后修改第一次为成功，第一次后面的状态为0的修改为-1，不列入统计
                    //        dwbzService.update(DwbzEntity.builder().state(-1).build(),
                    //                new QueryWrapper<DwbzEntity>().eq("bh", e.getBh()).eq("state", 0).le("create_time", e.getCreateTime()));
                    //    }
                    //
                    //}

                });

            } else {
                List<Map<String, Object>> list = videoService.queryGZTDData(params);
                pageResult.getRecords().forEach(e -> {
                    list.forEach(l -> {
                        if (e.getBh().equals(MapUtil.getStr(l, "BZs1")) || e.getBh().equals(MapUtil.getStr(l, "bh"))) {
                            e.setS1(MapUtil.getStr(l, "devName"));
                            e.setS2(MapUtil.getStr(l, "projectName"));
                        }
                    });

                    if (StrUtil.isBlank(e.getS1())) {
                        e.setS1(videoService.getNameBySn(e.getBh()));
                    }

                    params.put("sn", e.getBh());
                    params.put("islast", 0);
                    params.put("bzsj", DateUtil.format(e.getCreateTime(), DatePattern.NORM_DATE_FORMAT));
                    e.setS3(videoService.queryGZTDastUpData(params));

                });
            }
        }


        return R.ok().put("data", pageResult.getRecords()).put("count", pageResult.getTotal());
    }

    //修改故障点位备注
    @ResponseBody
    @PostMapping("/updateBxById")
    @SysLog("更新报修备注")
    public R updateBxById(@RequestBody List<DwbzEntity> list) {

        logger.info("[updateBxById] list={}", list);

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        String ip2 = IPUtils.getIpAddr(request);

        for (DwbzEntity dwbzEntity : list) {
            dwbzEntity.setRepairTime(DateUtil.now());
            dwbzEntity.setIp2(ip2);
        }
        dwbzService.updateBatchByIdAndBefore(list);

        return R.ok();
    }

    //修改故障点位备注 无登录
    @ResponseBody
    @PostMapping("/queryUpdateBxById")
    @SysLog("更新报修备注")
    public R queryUpdateBxById(@RequestBody List<DwbzEntity> list) {

        logger.info("[queryUpdateBxById] list={}", list);

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        String ip2 = IPUtils.getIpAddr(request);

        for (DwbzEntity dwbzEntity : list) {
            dwbzEntity.setRepairTime(DateUtil.now());
            dwbzEntity.setIp2(ip2);
        }
        dwbzService.updateBatchByIdAndBefore(list);

        return R.ok();
    }


    //查询点位或者通道列表
    @ResponseBody
    @GetMapping("/queryDwOrTd")
    public R queryDwOrTd(@RequestParam Map<String, Object> params) {

        logger.info("[queryDwOrTd] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        String kind = String.valueOf(params.get("kind"));


        IPage<Map<String,Object>> pageResult = null;
        pageResult = videoService.queryDwOrTd(params);

        //获取IP地址
        String ip = IPUtils.getIpAddr(HttpContextUtils.getHttpServletRequest());

        //获取该ip上一次报修时所填的报修人和报修电话（如果存在）
        Map<String, Object> dwbzMap = dwbzService.getMap(new QueryWrapper<DwbzEntity>().eq("ip", ip).orderByDesc("create_time").last("limit 1"));

        pageResult.getRecords().forEach(e -> {
            //获取该通道当前的报修记录（如果存在）
            List<DwbzEntity> list = dwbzService.list(
                    new QueryWrapper<DwbzEntity>()
                            .eq("state", 0)
                            .and(w -> w.eq("bh", MapUtil.getStr(e, "bh")))
                            .orderByDesc("create_time")
                            .last("limit 1"));
            e.put("current", list);

            //获取该通道历史报修记录
            e.put("history",dwbzService.list(
                    new QueryWrapper<DwbzEntity>()
                            .and(w -> w.eq("bh", MapUtil.getStr(e, "bh")))
                            .orderByDesc("create_time")
                            .last("limit 5")
            ));

            e.put("lastBxr", MapUtil.getStr(dwbzMap,"user_id"));
            e.put("lastBxdh", MapUtil.getStr(dwbzMap,"dh"));

            //格式化所属项目
            e.put("projectName", StrUtil.subBefore(String.valueOf(e.get("projectName")), " ", false));

        });

        return R.ok().put("data", pageResult.getRecords()).put("count", pageResult.getTotal());
    }

    //根据ip查询点位或者通道列表
    @ResponseBody
    @GetMapping("/queryDwOrTdByIp")
    public R queryDwOrTdByIp(@RequestParam Map<String, Object> params) {

        logger.info("[queryDwOrTdByIp] params={}", params);

        params.put("kind", "0");
        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        params.put("ip", IPUtils.getIpAddr(request));

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        String kind = String.valueOf(params.get("kind"));

        IPage<DwbzEntity> pageResult = null;

        pageResult = videoService.queryBxDataPage(params);

        //这里从sqlserver库里获取点位名称所属项目等信息
        StringBuffer bhSb = new StringBuffer();
        String bhs = "";
        pageResult.getRecords().stream().forEach(e->{
            bhSb.append(e.getBh()).append(",");
        });
        if (bhSb.toString().endsWith(",")) {
            bhs = StrUtil.subBefore(bhSb.toString(), ",", true);
        } else {
            bhs = bhSb.toString();
        }

        if (StrUtil.isNotBlank(bhs)) {
            params.put("bhs", MyUtils.formatSqlIn(bhs));

            if ("1".equals(kind)) {//点位故障
                List<Map<String, Object>> list = videoService.queryGZDWData(params);

                pageResult.getRecords().forEach(e -> {
                    list.forEach(l -> {
                        if (e.getBh().equals(MapUtil.getStr(l, "V_DWId"))) {
                            e.setS1(MapUtil.getStr(l,"pointName"));
                            e.setS2(MapUtil.getStr(l,"projectName"));
                        }
                    });
                    params.put("dwid", e.getBh());
                    params.put("islast", 0);
                    params.put("bzsj", DateUtil.format(e.getCreateTime(), DatePattern.NORM_DATE_FORMAT));
                    e.setS3(videoService.queryGZDWLastUpData(params));

                    //这里每次查询的时候，查询一下该通道或点位最新的巡检记录，若是成功的，则将该条保修备注记录已修理
                    //if (e.getState() == 0) {
                    //    params.put("islast", 1);
                    //    String repairTime = videoService.queryGZDWLastUpData(params);
                    //    if (StrUtil.isNotBlank(repairTime)) {
                    //        e.setState(1);
                    //        e.setRepairTime(repairTime);
                    //        dwbzService.updateById(e);
                    //        //若同一点位有多次保修，成功后修改第一次为成功，第一次后面的状态为0的修改为-1，不列入统计
                    //        dwbzService.update(DwbzEntity.builder().state(-1).build(),
                    //                new QueryWrapper<DwbzEntity>().eq("bh", e.getBh()).eq("state", 0).le("create_time", e.getCreateTime()));
                    //    }
                    //
                    //}

                });

            } else {
                List<Map<String, Object>> list = videoService.queryGZTDData(params);
                pageResult.getRecords().forEach(e -> {
                    list.forEach(l -> {
                        if (e.getBh().equals(MapUtil.getStr(l, "BZs1"))) {
                            e.setS1(MapUtil.getStr(l, "devName"));
                            e.setS2(MapUtil.getStr(l,"projectName"));
                        }
                    });

                    if (StrUtil.isBlank(e.getS1())) {
                        e.setS1(videoService.getNameBySn(e.getBh()));
                    }

                    params.put("sn", e.getBh());
                    params.put("islast", 0);
                    params.put("bzsj", DateUtil.format(e.getCreateTime(), DatePattern.NORM_DATE_FORMAT));
                    e.setS3(videoService.queryGZTDastUpData(params));


                });
            }
        }


        return R.ok().put("data", pageResult.getRecords()).put("count", pageResult.getTotal());
    }

    //派出所 查看自建点位每日巡查情况
    @ResponseBody
    @GetMapping("/queryPcsZj")
    public R queryPcsZj(@RequestParam Map<String, Object> params) {

        logger.info("[queryPcsZj] params={}", params);

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        String kind = String.valueOf(params.get("kind"));


        IPage<Map<String,Object>> pageResult = null;
        pageResult = videoService.queryDwOrTd(params);


        return R.ok().put("data", pageResult.getRecords()).put("count", pageResult.getTotal());
    }


    private String getXmIdStrFromList(List<XmConfigEntity> xmConfigList) {
        StringBuffer sb = new StringBuffer();
        String xmIds = "";
        xmConfigList.stream().forEach(e->{
            sb.append(e.getXmid()).append(",");
        });
        if (sb.toString().endsWith(",")) {
            xmIds = StrUtil.subBefore(sb.toString(), ",", true);
        } else {
            xmIds = sb.toString();
        }
        return xmIds;
    }


}
