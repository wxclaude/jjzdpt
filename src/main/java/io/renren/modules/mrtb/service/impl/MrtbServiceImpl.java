package io.renren.modules.mrtb.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.JsonObject;
import io.renren.common.exception.RRException;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.jcj.service.JjService;
import io.renren.modules.mrtb.consts.SysMrtbConsts;
import io.renren.modules.mrtb.dao.MrtbDao;
import io.renren.modules.mrtb.dao.MrtbDhDao;
import io.renren.modules.mrtb.dao.MrtbHisDao;
import io.renren.modules.mrtb.dao.MrtbXsjqDao;
import io.renren.modules.mrtb.entity.ZhMrtb;
import io.renren.modules.mrtb.entity.ZhMrtbDh;
import io.renren.modules.mrtb.entity.ZhMrtbHis;
import io.renren.modules.mrtb.entity.ZhMrtbXsjq;
import io.renren.modules.mrtb.service.MrtbService;
import io.renren.modules.sys.consts.SysModelConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpCookie;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MrtbServiceImpl extends ServiceImpl<MrtbDao, ZhMrtb> implements MrtbService {


    @Autowired
    private MrtbXsjqDao mrtbXsjqDao;
    @Autowired
    private MrtbDhDao mrtbDhDao;
    @Autowired
    private MrtbHisDao mrtbHisDao;


    @Autowired
    private JjService jjService;

    @Autowired
    private SysMrtbConsts sysMrtbConsts;

    @Override
    @DataSource("slave2")
    public Map<String, Object> getYesterday(Map<String, Object> params) {
        return baseMapper.getYesterday(params);

    }

    @Override
    public boolean saveCC(ZhMrtb zhMrtb, List<JcjCjxx> jcjcjxx) {
        boolean isSaved = baseMapper.saveCC(zhMrtb);
        if (!isSaved) {
            throw new RRException("生成每日通报失败");
        }


        jcjcjxx.forEach(e -> {
            e.setJjxx(jjService.queryByJjbh(e.getJjbh()));
            ZhMrtbXsjq zhMrtbXsjq = new ZhMrtbXsjq();
            zhMrtbXsjq.setTbid(zhMrtb.getId());
            zhMrtbXsjq.setJjsj(e.getJjrqsj());
            zhMrtbXsjq.setJjdw(e.getDeptsname());
            if (StrUtil.isNotEmpty(e.getDjdw())) {
                zhMrtbXsjq.setDw(StrUtil.subWithLength(e.getDjdw(), 0, 8));
            }
            zhMrtbXsjq.setJqlb(e.getCjlb());
            //zhMrtbXsjq.setJyaq(e.getCljgnr());
            zhMrtbXsjq.setJyaq(ReUtil.replaceAll(StrUtil.replace(StrUtil.replace(e.getCljgnr(), "（", "("), "）", ")"), "(\\([^\\)]*\\))", ""));
            zhMrtbXsjq.setJyaq(StrUtil.replace(zhMrtbXsjq.getJyaq(), "民警接报警后至江苏省扬州市邗江区。", e.getJjxx().getBjnr()));
            if (!zhMrtbXsjq.getJyaq().endsWith("。")) {
                zhMrtbXsjq.setJyaq(zhMrtbXsjq.getJyaq() + "。");
            }
            zhMrtbXsjq.setJjbh(e.getJjbh());
            zhMrtbXsjq.setCjbh(e.getCjbh());

            if (mrtbXsjqDao.insert(zhMrtbXsjq) <= 0) {
                throw new RRException("生成每日通报刑事警情失败");
            }

        });

        return isSaved;
    }

    @Override
    public void calMrtb(int tbid) {
        //根据tbid 获取当前的mrtb和mrtbxsjq,计算刑事警情和其它警情数量

        ZhMrtb zhMrtb = baseMapper.selectById(tbid);

        if (zhMrtb == null) {

            log.error("tbid {} not exsited", tbid);
            throw new RRException("通报数据不存在，请联系管理员");
        }

        List<ZhMrtbXsjq> zhMrtbXsjqList = mrtbXsjqDao.selectList(new QueryWrapper<ZhMrtbXsjq>().eq("tbid", zhMrtb.getId()));

        int xsjq = CollectionUtil.isNotEmpty(zhMrtbXsjqList) ? zhMrtbXsjqList.size() : 0;

        int mrtbXsjq = 0;
        int mrtbNum99 = 0;
        try {
            mrtbXsjq = Integer.parseInt(zhMrtb.getNum01());
            mrtbNum99 = Integer.parseInt(zhMrtb.getNum99());
        } catch (Exception e) {
            log.error("tbid {} num01 or num99 parse int error", tbid);
        }

        mrtbNum99 = mrtbNum99 + (mrtbXsjq - xsjq);

        zhMrtb.setNum01(String.valueOf(xsjq));
        zhMrtb.setNum99(String.valueOf(mrtbNum99));

        //修改各个派出所的刑事警情数据
        Map<String, List<ZhMrtbXsjq>> collect = zhMrtbXsjqList.stream().collect(Collectors.groupingBy(e -> e.getDw()));

        zhMrtb.setJd02(CollectionUtil.isNotEmpty(collect.get("32100317")) ? String.valueOf(collect.get("32100317").size()) : "0");
        zhMrtb.setHs02(CollectionUtil.isNotEmpty(collect.get("32100351")) ? String.valueOf(collect.get("32100351").size()) : "0");
        zhMrtb.setCh02(CollectionUtil.isNotEmpty(collect.get("32100352")) ? String.valueOf(collect.get("32100352").size()) : "0");
        zhMrtb.setGz02(CollectionUtil.isNotEmpty(collect.get("32100353")) ? String.valueOf(collect.get("32100353").size()) : "0");
        zhMrtb.setJw02(CollectionUtil.isNotEmpty(collect.get("32100354")) ? String.valueOf(collect.get("32100354").size()) : "0");
        zhMrtb.setYm02(CollectionUtil.isNotEmpty(collect.get("32100355")) ? String.valueOf(collect.get("32100355").size()) : "0");
        zhMrtb.setHss02(CollectionUtil.isNotEmpty(collect.get("32100356")) ? String.valueOf(collect.get("32100356").size()) : "0");
        zhMrtb.setFx02(CollectionUtil.isNotEmpty(collect.get("32100357")) ? String.valueOf(collect.get("32100357").size()) : "0");
        zhMrtb.setGd02(CollectionUtil.isNotEmpty(collect.get("32100358")) ? String.valueOf(collect.get("32100358").size()) : "0");
        zhMrtb.setYs02(CollectionUtil.isNotEmpty(collect.get("32100364")) ? String.valueOf(collect.get("32100364").size()) : "0");
        zhMrtb.setSjy02(CollectionUtil.isNotEmpty(collect.get("32100365")) ? String.valueOf(collect.get("32100365").size()) : "0");
        zhMrtb.setNsq02(CollectionUtil.isNotEmpty(collect.get("32100366")) ? String.valueOf(collect.get("32100366").size()) : "0");
        zhMrtb.setXs02(CollectionUtil.isNotEmpty(collect.get("32100373")) ? String.valueOf(collect.get("32100373").size()) : "0");
        zhMrtb.setZx02(CollectionUtil.isNotEmpty(collect.get("32100368")) ? String.valueOf(collect.get("32100368").size()) : "0");
        zhMrtb.setXh02(CollectionUtil.isNotEmpty(collect.get("32100369")) ? String.valueOf(collect.get("32100369").size()) : "0");
        zhMrtb.setJy02(CollectionUtil.isNotEmpty(collect.get("32100371")) ? String.valueOf(collect.get("32100371").size()) : "0");
        zhMrtb.setGq02(CollectionUtil.isNotEmpty(collect.get("32100372")) ? String.valueOf(collect.get("32100372").size()) : "0");

        zhMrtb.setXsjq01(0);
        zhMrtb.setXsjq02(0);
        zhMrtb.setXsjq03(0);
        zhMrtb.setXsjq99(0);

        //修改警情类型数据
        for (ZhMrtbXsjq zhMrtbXsjq : zhMrtbXsjqList) {
            switch (zhMrtbXsjq.getJqlb()) {
                //入室盗窃
                case "盗窃民宅":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;
                case "盗窃单位":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;
                case "盗窃商店":
                    zhMrtb.setXsjq01(zhMrtb.getXsjq01() != null ? zhMrtb.getXsjq01() + 1 : 1);
                    break;

                //诈骗
                case "诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "调包诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "设摊诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "假币假劵":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "信用卡诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "网络诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "短信诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "电话诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "拾物平分诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "碰瓷诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "使用假票据诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "贩卖假货":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "借打手机诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "招工征聘诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "宝物诈骗案":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "迷信诈骗案":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;
                case "其他诈骗":
                    zhMrtb.setXsjq02(zhMrtb.getXsjq02() != null ? zhMrtb.getXsjq02() + 1 : 1);
                    break;

                //盗窃非机动车
                case "盗窃自行车":
                    zhMrtb.setXsjq03(zhMrtb.getXsjq03() != null ? zhMrtb.getXsjq03() + 1 : 1);
                    break;
                case "盗窃电动自行车":
                    zhMrtb.setXsjq03(zhMrtb.getXsjq03() != null ? zhMrtb.getXsjq03() + 1 : 1);
                    break;

                //其它
                default:
                    zhMrtb.setXsjq99(zhMrtb.getXsjq99() != null ? zhMrtb.getXsjq99() + 1 : 1);
                    break;

            }
        }


        baseMapper.updateById(zhMrtb);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sentMrtb(int mrtbId, String msg, String ipAddr, String createBy) {

        ZhMrtb zhMrtb = this.getById(mrtbId);
        if (zhMrtb == null) {
            throw new RRException("发信短信失败，通报数据异常");
        }

        List<ZhMrtbDh> zhMrtbDhList = mrtbDhDao.selectList(new QueryWrapper<ZhMrtbDh>());
        if (CollectionUtil.isEmpty(zhMrtbDhList)) {
            throw new RRException("发信短信失败，发信的短信号码为空");
        }

        CharSequence separator = ",";
        StringJoiner sj = new StringJoiner(separator);
        String phones = "";
        for (ZhMrtbDh zhMrtbDh : zhMrtbDhList) {
            sj.add(zhMrtbDh.getPhone());
        }

        phones = sj.toString();

        //todo send msg
//        try {
//            Thread.sleep(10000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        int successCount = sendMsg(phones, msg);

        if (successCount <= 0) {
            log.error("sentMrtb error mrtbId={}, msg={}", mrtbId, msg);
            throw new RRException("发信短信失败，短信平台异常");
        }

        //save mrtb_his
        ZhMrtbHis zhMrtbHis = new ZhMrtbHis();
        zhMrtbHis.setPhones(phones);
        zhMrtbHis.setContent(msg);
        zhMrtbHis.setMrtbId(mrtbId);
        zhMrtbHis.setTbrq(zhMrtb.getTbrq());
        zhMrtbHis.setCreateBy(createBy);
        zhMrtbHis.setIp(ipAddr);
        zhMrtbHis.setSuccessCount(successCount);
        zhMrtbHis.setPhoneCount(zhMrtbDhList.size());

        if (mrtbHisDao.insert(zhMrtbHis) <= 0) {
            throw new RRException("发信短信失败，数据处理异常");
        }

        //update mrtb msg_count
        ZhMrtb newZhmrtb = new ZhMrtb();
        newZhmrtb.setId(zhMrtb.getId());
        newZhmrtb.setMsgCount(zhMrtb.getMsgCount() + 1);

        if (!this.updateById(newZhmrtb)) {
            throw new RRException("发信短信失败，数据处理异常");
        }

    }

    private int sendMsg(String phones, String msg) {

        int successCount = 0;
        String[] phoneArray = StrUtil.split(phones, ",");

        String host = "http://dxpt.yzh.js/";
        String sessionName = "ASP.NET_SessionId";
        String sessionValue = IdUtil.objectId();

        //1登录
        HttpRequest loginRequest = HttpUtil.createPost(host + "Login.aspx?action=login")
                .cookie(new HttpCookie(sessionName, sessionValue))
                .header(HttpHeaders.HOST, host)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8")
                .header(HttpHeaders.ORIGIN, host)
                .header("X-Requested-With", "XMLHttpRequest")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(HttpHeaders.REFERER, host)
                .header(HttpHeaders.COOKIE, sessionName + "=" + sessionValue)
                .form(MapUtil.builder(new HashMap<String, Object>())
                        .put("__VIEWSTATE",sysMrtbConsts.getVIEWSTATE())
                        .put("__EVENTVALIDATION",sysMrtbConsts.getEVENTVALIDATION())
                        .put("txtLoginType",1)
                        .put("txtAccountType",0)
                        .put("txtAccount",sysMrtbConsts.getACCOUNT())
                        .put("txtPassword",sysMrtbConsts.getPASSWORD())
                        .build());

        log.info("[login.aspx] request header: {}", loginRequest.headers());
        log.info("[login.aspx] request form: {}", loginRequest.form());

        HttpResponse loginResponse = loginRequest.execute();
        log.info("[login.aspx] response status: {}", loginResponse.getStatus());
        log.info("[login.aspx] response body: {}", loginResponse.body());
        if (loginResponse.getStatus() != 200) {
            throw new RRException("发送短信失败，登录异常");
        }
        JSONObject logibResponseBody = JSONUtil.parseObj(loginResponse.body());
        log.info("[login.aspx] logibResponseBody: {}", logibResponseBody);

        if (!"true".equals(String.valueOf(logibResponseBody.get("success")))) {
            throw new RRException("发送短信失败，登录异常");
        }

        //2获取所有用户信息
        HttpRequest allJYXXRequest = HttpUtil.createGet(host + "DXFS/GRMsg.aspx?action=getAllJYXX&_dc=" + DateUtil.current(false))
                .cookie(new HttpCookie(sessionName, sessionValue))
                .header(HttpHeaders.HOST, host)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8")
                .header(HttpHeaders.ORIGIN, host)
                .header("X-Requested-With", "XMLHttpRequest")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
//                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(HttpHeaders.REFERER, host + "DXFS/BMMsg.aspx")
                .header(HttpHeaders.COOKIE, sessionName + "=" + sessionValue);

        log.info("[GRMsg.aspx?action=getAllJYXX] request header: {} [request form] {}", allJYXXRequest.headers());
        log.info("[GRMsg.aspx?action=getAllJYXX] request form: {}", allJYXXRequest.form());

        HttpResponse allJYXXResponse = allJYXXRequest.execute();
        log.info("[GRMsg.aspx?action=getAllJYXX] response status: {}", allJYXXResponse.getStatus());
        log.info("[GRMsg.aspx?action=getAllJYXX] response body: {}", allJYXXResponse.body());
        if (allJYXXResponse.getStatus() != 200) {
            throw new RRException("发送短信失败，获取数据异常");
        }
        JSONArray allJYXXResponseBody = JSONUtil.parseArray(allJYXXResponse.body());
        if (allJYXXResponseBody.size() == 0) {
            throw new RRException("发送短信失败，获取数据异常");
        }

        //3 编辑短信
        HttpRequest saveRequest = HttpUtil.createPost(host + "DXFS/BMMsg.aspx?action=savebmdx")
                .cookie(new HttpCookie(sessionName, sessionValue))
                .header(HttpHeaders.HOST, host)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8")
                .header(HttpHeaders.ORIGIN, host)
                .header("X-Requested-With", "XMLHttpRequest")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
                .header(HttpHeaders.REFERER, host + "BMMsg.aspx")
                .header(HttpHeaders.COOKIE, sessionName + "=" + sessionValue)
                .form(MapUtil.builder(new HashMap<String, Object>())
                        .put("DXNR", msg + "(邗江分局指挥中心)")
                        .put("DXTS", phoneArray.length)
                        .put("YXJ", phoneArray.length)
                        .put("ZDQM", true)
                        .build());

        log.info("[DXFS/BMMsg.aspx?action=savebmdx] request header: {} [request form] {}", saveRequest.headers());
        log.info("[DXFS/BMMsg.aspx?action=savebmdx] request form: {}", saveRequest.form());
        HttpResponse saveResponse = saveRequest.execute();
        log.info("[DXFS/BMMsg.aspx?action=savebmdx] response status: {}", saveResponse.getStatus());
        log.info("[DXFS/BMMsg.aspx?action=savebmdx] response body: {}", saveResponse.body());
        if (saveResponse.getStatus() != 200) {
            throw new RRException("发送短信失败，保存数据异常");
        }
        JSONObject saveResponseBody = JSONUtil.parseObj(saveResponse.body());
        if (!"true".equals(String.valueOf(saveResponseBody.get("success")))) {
            throw new RRException("发送短信失败，保存数据异常");
        }

        String intXh = String.valueOf(saveResponseBody.get("msg"));
        if (StrUtil.isBlank(intXh)) {
            throw new RRException("发送短信失败，保存数据异常");
        }

        //4 循环发送
        HttpRequest sendRequest = HttpUtil.createPost(host + "DXFS/BMMsg.aspx?action=savebmdxmx")
                .cookie(new HttpCookie(sessionName, sessionValue))
                .header(HttpHeaders.HOST, host)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8")
                .header(HttpHeaders.ORIGIN, host)
                .header("X-Requested-With", "XMLHttpRequest")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
                .header(HttpHeaders.REFERER, host + "BMMsg.aspx")
                .header(HttpHeaders.COOKIE, sessionName + "=" + sessionValue);

        log.info("[DXFS/BMMsg.aspx?action=savebmdxmx] request header: {} [request form] {}", sendRequest.headers());

        Map params = null;
        JSONObject jyxx = null;
        HttpResponse sendResponse = null;
        for (String phone : phoneArray) {

            jyxx = null;
            sendResponse = null;
            params = new HashMap();
            params.put("YDDH", phone);
            params.put("intXh", intXh);
            params.put("DXTS", phoneArray.length);
            params.put("DXNR", msg + "(邗江分局指挥中心)");
            params.put("ZDQM", true);
            params.put("DMBM", "(邗江分局指挥中心)");
            //判断号码是否在allJYXXResponseBody中

            for (int i = 0; i < allJYXXResponseBody.size(); i++) {
                if (phone.equals(allJYXXResponseBody.getJSONObject(i).getStr("YDDH"))) {
                    jyxx = allJYXXResponseBody.getJSONObject(i);
                    break;
                }
            }

            if (jyxx != null) {
                params.put("XH", jyxx.getStr("XH"));
                params.put("JH", jyxx.getStr("JH"));
                params.put("XM", jyxx.getStr("XM"));
                params.put("BMMC", jyxx.getStr("BMMC"));
                params.put("YYS", jyxx.getStr("YYS"));
            } else {
                params.put("XH", "");
                params.put("JH", "");
                params.put("XM", "");
                params.put("BMMC", "");
                params.put("YYS", "");
            }

            sendRequest.form(params);
            log.info("[DXFS/BMMsg.aspx?action=savebmdxmx] request form: {} [request form] {}", sendRequest.form());

            sendResponse = sendRequest.execute();
            log.info("[DXFS/BMMsg.aspx?action=savebmdxmx] response status: {}", sendResponse.getStatus());
            log.info("[DXFS/BMMsg.aspx?action=savebmdxmx] response body: {}", sendResponse.body());
            if (sendResponse.getStatus() != 200) {
                log.info("[DXFS/BMMsg.aspx?action=savebmdxmx] send msg error");
            }
            JSONObject sendResponseBody = JSONUtil.parseObj(sendResponse.body());
            if (!"true".equals(String.valueOf(sendResponseBody.get("success")))) {
                log.info("[DXFS/BMMsg.aspx?action=savebmdxmx] send msg error, {}", sendResponseBody.get("msg"));
            } else {
                successCount++;
            }

        }

        //5 更新
        HttpRequest updateRequest = HttpUtil.createGet(host + "DXFS/BMMsg.aspx?action=updateZHFSSJ&_dc=" + DateUtil.current(false))
                .cookie(new HttpCookie(sessionName, sessionValue))
                .header(HttpHeaders.HOST, host)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8")
                .header(HttpHeaders.ORIGIN, host)
                .header("X-Requested-With", "XMLHttpRequest")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
//                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(HttpHeaders.REFERER, host + "DXFS/BMMsg.aspx")
                .header(HttpHeaders.COOKIE, sessionName + "=" + sessionValue);

        log.info("[BMMsg.aspx?action=updateZHFSSJ] request header: {} [request form] {}", updateRequest.headers());
        log.info("[BMMsg.aspx?action=updateZHFSSJ] request form: {}", updateRequest.form());

        HttpResponse updateResponse = updateRequest.execute();
        log.info("[BMMsg.aspx?action=updateZHFSSJ] response status: {}", updateResponse.getStatus());
        log.info("[BMMsg.aspx?action=updateZHFSSJ] response body: {}", updateResponse.body());
        if (updateResponse.getStatus() != 200) {
            log.info("[BMMsg.aspx?action=updateZHFSSJ] update ZHFSSJ error");
        }
        JSONObject updateResponseBody = JSONUtil.parseObj(updateResponse.body());
        if (!"true".equals(String.valueOf(updateResponseBody.get("success")))) {
            log.info("[DXFS/BMMsg.aspx?action=updateZHFSSJ] send msg error, {}", updateResponseBody.get("msg"));
        }

        return successCount;
    }

    public static void main(String[] args) {

        JSONObject saveResponseBody = JSONUtil.parseObj("{'success':true,'msg':'sss'}");

        System.out.println("true".equals(String.valueOf(saveResponseBody.get("success"))));
    }
}
