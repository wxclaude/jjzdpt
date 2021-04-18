package io.renren.modules.jcj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.jcj.dao.CjDao;
import io.renren.modules.jcj.dao.JjDao;
import io.renren.modules.jcj.dao.JqCjBhgDao;
import io.renren.modules.jcj.entity.JcjCjxx;
import io.renren.modules.jcj.entity.JcjJjxx;
import io.renren.modules.jcj.entity.JqCjBhgEntity;
import io.renren.modules.jcj.service.CjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CjServiceImpl extends ServiceImpl<CjDao, JcjCjxx> implements CjService {

    @Autowired
    private CjDao cjDao;

    @Autowired
    private JjDao jjDao;
    @Autowired
    private JqCjBhgDao jqCjBhgDao;


    @Override
    @DataSource("slave2")
    public IPage<JcjCjxx> queryCjList(Map<String, Object> params) {

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));
        Page<JcjCjxx> pageParams = new Page(page,limit);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("condition", String.valueOf(params.get("condition")));
        params2.put("beforeDate", String.valueOf(params.get("beforeDate")).replace("-",""));
        params2.put("afterDate", String.valueOf(params.get("afterDate")).replace("-",""));
        params2.put("dw", String.valueOf(params.get("dw")));
        params2.put("jjbh", String.valueOf(params.get("jjbh")));

        IPage<JcjCjxx> pageResult = cjDao.queryCjList(pageParams, params2);
        for (JcjCjxx jcjCjxx:pageResult.getRecords()) {
            transformCjxx(jcjCjxx);
        }
        return pageResult;
    }

    @Override
    @DataSource("slave2")
    public List<JcjCjxx> queryCjListIn3days(Map<String, Object> params) {
        if(!String.valueOf(params.get("beforeDate")).isEmpty()||String.valueOf(params.get("beforeDate"))!=null){
            params.replace("beforeDate",String.valueOf(params.get("beforeDate")).replace("-",""));
            params.replace("afterDate",String.valueOf(params.get("afterDate")).replace("-",""));
        }

        List<JcjCjxx> jcjCjxxList = cjDao.queryCjListIn3days(params);

        //jcjCjxxList.forEach(e -> {
        //    e.setJjxx(jjDao.selectById(e.getJjbh()));
        //});
        return jcjCjxxList;
    }

    @Override
    @DataSource("slave2")
    public List<JcjCjxx> queryCjxx(Map<String, Object> params) {
        return cjDao.queryCjxx(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOf8HourNoPoliceDeal(Map<String, Object> params) {
        return cjDao.countOf8HourNoPoliceDeal(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOf24HourNoApproval(Map<String, Object> params) {
        return cjDao.countOf24HourNoApproval(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfUpOrLowLimitIsEmpty(Map<String, Object> params) {
        return cjDao.countOfUpOrLowLimitIsEmpty(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfWeatherConditionIsEmptyWhileTraffic(Map<String, Object> params) {
        return cjDao.countOfWeatherConditionIsEmptyWhileTraffic(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfAddressIsEmpty(Map<String, Object> params) {
        return cjDao.countOfAddressIsEmpty(params);
    }

    @Override
    public JcjCjxx transformCjxx(JcjCjxx jcjCjxx) {


        //警情备注，为普通警情/重大警情，新冠、债务、恶劣天气
        if(jcjCjxx.getBjlx()!=null&&!jcjCjxx.getBjlx().isEmpty()&&jcjCjxx.getBjlx().length()>=4) {
            if(jcjCjxx.getBjlx().startsWith("2001")
                    ||jcjCjxx.getBjlx().startsWith("2002")
                    ||jcjCjxx.getBjlx().startsWith("2003")
                    ||jcjCjxx.getBjlx().startsWith("2004")
                    ||jcjCjxx.getBjlx().startsWith("2007")
                    ||jcjCjxx.getBjlx().startsWith("2011")
                    ||jcjCjxx.getBjlx().startsWith("2015")
                    ||jcjCjxx.getBjlx().startsWith("2032")){
                jcjCjxx.setRemarks("重大警情");
            }
            if(jcjCjxx.getCljgnr().contains("暴雨")||jcjCjxx.getCljgnr().contains("大雪")
                    ||jcjCjxx.getCljgnr().contains("冰冻")||jcjCjxx.getCljgnr().contains("台风")){
                jcjCjxx.setRemarks("".equals(jcjCjxx.getRemarks())?"恶劣天气":jcjCjxx.getRemarks()+",恶劣天气");
            }
            if(jcjCjxx.getCljgnr().contains("新冠")){
                jcjCjxx.setRemarks("".equals(jcjCjxx.getRemarks())?"新冠":jcjCjxx.getRemarks()+",新冠");
            }
            if(jcjCjxx.getCljgnr().contains("套路贷")){
                jcjCjxx.setRemarks("".equals(jcjCjxx.getRemarks())?"套路贷":jcjCjxx.getRemarks()+",套路贷");
            }
            if(jcjCjxx.getCljgnr().contains("债务")
                    ||jcjCjxx.getCljgnr().contains("借")
                    ||jcjCjxx.getCljgnr().contains("欠")){
                jcjCjxx.setRemarks("".equals(jcjCjxx.getRemarks())?"债务":jcjCjxx.getRemarks()+",债务");
            }
            if(jcjCjxx.getCljgnr().contains("勒索病毒")){
                jcjCjxx.setRemarks("".equals(jcjCjxx.getRemarks())?"勒索病毒":jcjCjxx.getRemarks()+",勒索病毒");
            }
            if(jcjCjxx.getCljgnr().contains("家暴")){
                jcjCjxx.setRemarks("".equals(jcjCjxx.getRemarks())?"家暴":jcjCjxx.getRemarks()+",家暴");
            }
        }

        //移送报警，移送接受单位/人/时间为空，p0
        if(("17").equals(jcjCjxx.getBjxs())&&
                    (jcjCjxx.getCjysjssj().isEmpty()||jcjCjxx.getCjysjssj()==null
                    ||jcjCjxx.getCjysjsdw().isEmpty()||jcjCjxx.getCjysjsdw()==null
                    ||jcjCjxx.getCjysjsr().isEmpty()||jcjCjxx.getCjysjsr()==null)){
            jcjCjxx.setP0("1");
        }
        //事发场所未具体，==990，p1
        if("990".equals(jcjCjxx.getSfcs())||"".equals(jcjCjxx.getSfcs())||jcjCjxx.getSfcs().isEmpty()){
            jcjCjxx.setP1("1");
        }
        //处警类别为其他警情or处警类别未填到字典项最底层，P2
        if(jcjCjxx.getCjlb()!=null&&!jcjCjxx.getCjlb().isEmpty()&&jcjCjxx.getCjlb().length()>=6){
            if(jcjCjxx.getCjlb().endsWith("00")||jcjCjxx.getCjlb().startsWith("10")){
                jcjCjxx.setP2("1");
            }
        }
        //处警统计信息未填写，P3，机动车/出动警力/救助群众/伤员/死亡五要素必填，P3
        if("0".equals(jcjCjxx.getCdjdc())
                &&"0".equals(jcjCjxx.getCdjl())
                &&"0".equals(jcjCjxx.getJzsy())
                &&"0".equals(jcjCjxx.getJzqz())
                &&"0".equals(jcjCjxx.getRysws())){
            jcjCjxx.setP3("1");
        }

        //关联损失情况警情类型，损失情况未填写，P4
        if((jcjCjxx.getSsxxqk()==null&&jcjCjxx.getSsxxqk().isEmpty())
                &&("011700".equals(jcjCjxx.getBjlx())
                ||"011600".equals(jcjCjxx.getBjlx())
                ||"011800".equals(jcjCjxx.getBjlx())
                ||"011900".equals(jcjCjxx.getBjlx())
                ||"020200".equals(jcjCjxx.getBjlx())
                ||"020300".equals(jcjCjxx.getBjlx())
                ||"020400".equals(jcjCjxx.getBjlx())
                ||"020700".equals(jcjCjxx.getBjlx())
                ||"020800".equals(jcjCjxx.getBjlx()))){
            jcjCjxx.setP4("1");
        }

        //处警类别为网络诈骗但属性未标注涉网，P5
        if(jcjCjxx.getJqsx()!=null&&!jcjCjxx.getJqsx().isEmpty()&&jcjCjxx.getJqsx().length()>=16){
            if("011805".equals(jcjCjxx.getBjlx())&&jcjCjxx.getJqsx().substring(1, 2).equals("0")){
                jcjCjxx.setP5("1");
            }
        }

        //处警超时，登记时间-接警时间>24h，P6
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
//        Date nowdate = c.getTime();
        long now =c.getTime().getTime();
        if(jcjCjxx.getJjrqsj()!=null&&!jcjCjxx.getJjrqsj().isEmpty()){
            if(jcjCjxx.getDjsj()!=null&&!jcjCjxx.getDjsj().isEmpty()){
                try {
                    Date jjrqsj = formatter.parse(jcjCjxx.getJjrqsj());
                    Date djsj = formatter.parse(jcjCjxx.getDjsj());
                    long t1 = (djsj.getTime() - jjrqsj.getTime()) / 1000 / 3600;
                    if (t1 > 24) {
                        jcjCjxx.setP6("1");
                    }
                } catch (ParseException e) {
//                    e.printStackTrace();
                }catch (NullPointerException n){
//                    n.printStackTrace();
                }
            }else{
                //当前时间超过接警时间24h
                try {
                    Date jjrqsj = formatter.parse(jcjCjxx.getJjrqsj());
                    long t1 = (now - jjrqsj.getTime()) / 1000 / 3600;
                    if (t1 > 24) {
                        jcjCjxx.setP6("1");
                    }
                } catch (ParseException e) {
//                    e.printStackTrace();
                }catch (NullPointerException n){
//                    n.printStackTrace();
                }
            }
        }
        //审批超时，审批时间-登记时间>24h，P7
//        if(jcjCjxx.getDjsj()!=null&&!jcjCjxx.getDjsj().isEmpty()){
//            if(jcjCjxx.getSpsj()!=null&&!jcjCjxx.getSpsj().isEmpty()){
//                try {
//                    Date djsj = formatter.parse(jcjCjxx.getDjsj());
//                    Date spsj = formatter.parse(jcjCjxx.getSpsj());
//                    long t2 = (spsj.getTime()-djsj.getTime())/1000/3600;
//                    if (t2>24){
//                        jcjCjxx.setP7("1");
//                    }
//                } catch (ParseException e) {
////                    e.printStackTrace();
//                }catch (NullPointerException n){
////                    n.printStackTrace();
//                }
//            }else{
//                //当前时间超过接警时间24h
//                try {
//                    Date djsj = formatter.parse(jcjCjxx.getJjrqsj());
//                    long t2 = (now - djsj.getTime()) / 1000 / 3600;
//                    if (t2>24){
//                        jcjCjxx.setP7("1");
//                    }
//                } catch (ParseException e) {
////                    e.printStackTrace();
//                }catch (NullPointerException n){
////                    n.printStackTrace();
//                }
//            }
//        }
        //重大警情，处警时间-接警时间>2h，P8
        if(jcjCjxx.getRemarks().contains("重大警情")){
            if(jcjCjxx.getJjrqsj()!=null&&!jcjCjxx.getJjrqsj().isEmpty()){
                if(jcjCjxx.getCjsj()!=null&&!jcjCjxx.getCjsj().isEmpty()){
                    try {
                        Date jjrqsj = formatter.parse(jcjCjxx.getJjrqsj());
                        Date cjsj = formatter.parse(jcjCjxx.getCjsj());
                        long t3 = (cjsj.getTime()-jjrqsj.getTime())/1000/3600;
                        if(t3>2){
                            jcjCjxx.setP8("1");
                        }
                    } catch (ParseException e) {
//                    e.printStackTrace();
                    }catch (NullPointerException n){
//                    n.printStackTrace();
                    }
                }else{
                    //当前时间超过接警时间24h
                    try {
                        Date jjrqsj = formatter.parse(jcjCjxx.getJjrqsj());
                        long t3 = (now-jjrqsj.getTime())/1000/3600;
                        if(t3>2){
                            jcjCjxx.setP8("1");
                        }
                    } catch (ParseException e) {
//                    e.printStackTrace();
                    }catch (NullPointerException n){
//                    n.printStackTrace();
                    }
                }
            }
        }

        //自接警情，填写不完善，P9
        if("05".equals(jcjCjxx.getBjxs())&&
                (jcjCjxx.getBjlx()==null||jcjCjxx.getBjlx().isEmpty()
                        ||jcjCjxx.getBjr()==null||jcjCjxx.getBjr().isEmpty()
                        ||jcjCjxx.getBjrxb()==null||jcjCjxx.getBjrxb().isEmpty()
                        ||jcjCjxx.getSfdd()==null||jcjCjxx.getSfdd().isEmpty())){
            jcjCjxx.setP9("1");
        }
        //事发时间上限、下限为空，P10
        if(jcjCjxx.getSfsjsx()==null||jcjCjxx.getSfsjsx().isEmpty()||jcjCjxx.getSfsjxx()==null||jcjCjxx.getSfsjxx().isEmpty()){
            jcjCjxx.setP10("1");
        }
        //天气情况为空的交通类警情，P11
        if(jcjCjxx.getCjlb().substring(0, 2).equals("03")&&(jcjCjxx.getTqqk()==null||jcjCjxx.getTqqk().isEmpty())){
            jcjCjxx.setP11("1");
        }
        //报警形式
        if(jcjCjxx.getBjxs()!=null&&!jcjCjxx.getBjxs().isEmpty()){
            switch (jcjCjxx.getBjxs()){
                case "01":
                    jcjCjxx.setBjxs("110报警");
                    break;
                case "02":
                    jcjCjxx.setBjxs("112报警");
                    break;
                case "05":
                    jcjCjxx.setBjxs("其他电话报警");
                    break;
                case "08":
                    jcjCjxx.setBjxs("器材报警");
                    break;
                case "17":
                    jcjCjxx.setBjxs("其他部门移送");
                    break;
            }
        }
        //报警类型
        if(jcjCjxx.getBjlx()!=null&&!jcjCjxx.getBjlx().isEmpty()&&jcjCjxx.getBjlx().length()>=2) {
            switch (jcjCjxx.getCjlb().substring(0, 2)) {
                case "20":
                    jcjCjxx.setCjlb("违法犯罪");
                    break;
                case "21":
                    jcjCjxx.setCjlb("交通");
                    break;
                case "22":
                    jcjCjxx.setCjlb("火灾事故");
                    break;
                case "23":
                    jcjCjxx.setCjlb("群众求助");
                    break;
                case "24":
                    jcjCjxx.setCjlb("举报投诉");
                    break;
                case "25":
                    jcjCjxx.setCjlb("群体事件");
                    break;
                case "26":
                    jcjCjxx.setCjlb("纠纷");
                    break;
                case "27":
                    jcjCjxx.setCjlb("灾害事故");
                    break;
                case "28":
                    jcjCjxx.setCjlb("扬言");
                    break;
                case "29":
                    jcjCjxx.setCjlb("关注对象");
                    break;
                case "30":
                    jcjCjxx.setCjlb("警情备案");
                    break;
                case "90":
                    jcjCjxx.setCjlb("其他警情");
                    break;
                case "97":
                    jcjCjxx.setCjlb("涉林警情");
                    break;
            }
        }
        //派出所名称
        if(jcjCjxx.getDeptsname()!=null&&!jcjCjxx.getDeptsname().isEmpty()){
            jcjCjxx.setDeptsname(jcjCjxx.getDeptsname().replace("派出所",""));
        }
        //处警类别
        if(jcjCjxx.getCjlb()!=null&&!jcjCjxx.getCjlb().isEmpty()&&jcjCjxx.getCjlb().length()>=2) {
            switch (jcjCjxx.getCjlb().substring(0, 2)) {
                case "01":
                    jcjCjxx.setCjlb("刑事警情");
                    break;
                case "02":
                    jcjCjxx.setCjlb("治安警情");
                    break;
                case "03":
                    jcjCjxx.setCjlb("交通警情");
                    break;
                case "04":
                    jcjCjxx.setCjlb("火灾");
                    break;
                case "05":
                    jcjCjxx.setCjlb("群众求助");
                    break;
                case "06":
                    jcjCjxx.setCjlb("举报投诉");
                    break;
                case "07":
                    jcjCjxx.setCjlb("事件");
                    break;
                case "08":
                    jcjCjxx.setCjlb("纠纷");
                    break;
                case "09":
                    jcjCjxx.setCjlb("灾害事故");
                    break;
                case "10":
                    jcjCjxx.setCjlb("其他行政违法");
                    break;
            }
        }
        //各种时间
        if(jcjCjxx.getJjrqsj()!=null&&!jcjCjxx.getJjrqsj().isEmpty()){
            jcjCjxx.setJjrqsj(jcjCjxx.getJjrqsj().substring(0,4)+"/"+jcjCjxx.getJjrqsj().substring(4,6)+"/"+jcjCjxx.getJjrqsj().substring(6,8)+" "+jcjCjxx.getJjrqsj().substring(8,10)+":"+jcjCjxx.getJjrqsj().substring(10,12));
        }
        if(jcjCjxx.getCjsj()!=null&&!jcjCjxx.getCjsj().isEmpty()){
            jcjCjxx.setCjsj(jcjCjxx.getCjsj().substring(0,4)+"/"+jcjCjxx.getCjsj().substring(4,6)+"/"+jcjCjxx.getCjsj().substring(6,8)+" "+jcjCjxx.getCjsj().substring(8,10)+":"+jcjCjxx.getCjsj().substring(10,12));
        }
        if(jcjCjxx.getDjsj()!=null&&!jcjCjxx.getDjsj().isEmpty()){
            jcjCjxx.setDjsj(jcjCjxx.getDjsj().substring(0,4)+"/"+jcjCjxx.getDjsj().substring(4,6)+"/"+jcjCjxx.getDjsj().substring(6,8)+" "+jcjCjxx.getDjsj().substring(8,10)+":"+jcjCjxx.getDjsj().substring(10,12));
        }
        if(jcjCjxx.getDdxcsj()!=null&&!jcjCjxx.getDdxcsj().isEmpty()){
            jcjCjxx.setDdxcsj(jcjCjxx.getDdxcsj().substring(0,4)+"/"+jcjCjxx.getDdxcsj().substring(4,6)+"/"+jcjCjxx.getDdxcsj().substring(6,8)+" "+jcjCjxx.getDdxcsj().substring(8,10)+":"+jcjCjxx.getDdxcsj().substring(10,12));
        }
        if(jcjCjxx.getSfsjsx()!=null&&!jcjCjxx.getSfsjsx().isEmpty()){
            jcjCjxx.setSfsjsx(jcjCjxx.getSfsjsx().substring(0,4)+"/"+jcjCjxx.getSfsjsx().substring(4,6)+"/"+jcjCjxx.getSfsjsx().substring(6,8)+" "+jcjCjxx.getSfsjsx().substring(8,10)+":"+jcjCjxx.getSfsjsx().substring(10,12));
        }
        if(jcjCjxx.getSfsjxx()!=null&&!jcjCjxx.getSfsjxx().isEmpty()){
            jcjCjxx.setSfsjxx(jcjCjxx.getSfsjxx().substring(0,4)+"/"+jcjCjxx.getSfsjxx().substring(4,6)+"/"+jcjCjxx.getSfsjxx().substring(6,8)+" "+jcjCjxx.getSfsjxx().substring(8,10)+":"+jcjCjxx.getSfsjxx().substring(10,12));
        }
        if(jcjCjxx.getCjfksj()!=null&&!jcjCjxx.getCjfksj().isEmpty()){
            jcjCjxx.setCjfksj(jcjCjxx.getCjfksj().substring(0,4)+"/"+jcjCjxx.getCjfksj().substring(4,6)+"/"+jcjCjxx.getCjfksj().substring(6,8)+" "+jcjCjxx.getCjfksj().substring(8,10)+":"+jcjCjxx.getCjfksj().substring(10,12));
        }
        if(jcjCjxx.getLdclsj()!=null&&!jcjCjxx.getLdclsj().isEmpty()){
            jcjCjxx.setLdclsj(jcjCjxx.getLdclsj().substring(0,4)+"/"+jcjCjxx.getLdclsj().substring(4,6)+"/"+jcjCjxx.getLdclsj().substring(6,8)+" "+jcjCjxx.getLdclsj().substring(8,10)+":"+jcjCjxx.getLdclsj().substring(10,12));
        }
        if(jcjCjxx.getSpsj()!=null&&!jcjCjxx.getSpsj().isEmpty()){
            jcjCjxx.setSpsj(jcjCjxx.getSpsj().substring(0,4)+"/"+jcjCjxx.getSpsj().substring(4,6)+"/"+jcjCjxx.getSpsj().substring(6,8)+" "+jcjCjxx.getSpsj().substring(8,10)+":"+jcjCjxx.getSpsj().substring(10,12));
        }
        if(jcjCjxx.getXgsj()!=null&&!jcjCjxx.getXgsj().isEmpty()){
            jcjCjxx.setXgsj(jcjCjxx.getXgsj().substring(0,4)+"/"+jcjCjxx.getXgsj().substring(4,6)+"/"+jcjCjxx.getXgsj().substring(6,8)+" "+jcjCjxx.getXgsj().substring(8,10)+":"+jcjCjxx.getXgsj().substring(10,12));
        }
        if(jcjCjxx.getSpxgsj()!=null&&!jcjCjxx.getSpxgsj().isEmpty()){
            jcjCjxx.setSpxgsj(jcjCjxx.getSpxgsj().substring(0,4)+"/"+jcjCjxx.getSpxgsj().substring(4,6)+"/"+jcjCjxx.getSpxgsj().substring(6,8)+" "+jcjCjxx.getSpxgsj().substring(8,10)+":"+jcjCjxx.getSpxgsj().substring(10,12));
        }
        if(jcjCjxx.getCjysjssj()!=null&&!jcjCjxx.getCjysjssj().isEmpty()){
            jcjCjxx.setCjysjssj(jcjCjxx.getCjysjssj().substring(0,4)+"/"+jcjCjxx.getCjysjssj().substring(4,6)+"/"+jcjCjxx.getCjysjssj().substring(6,8)+" "+jcjCjxx.getCjysjssj().substring(8,10)+":"+jcjCjxx.getCjysjssj().substring(10,12));
        }

        //警情属性
        if(jcjCjxx.getJqsx()!=null&&!jcjCjxx.getJqsx().isEmpty()&&jcjCjxx.getJqsx().length()>=16){
            if (jcjCjxx.getJqsx().endsWith("1")) {
                jcjCjxx.setJqsx("其他");
            }else {
                String jqsx = "";
                if (jcjCjxx.getJqsx().substring(0, 1).equals("1")) {
                    jqsx = "涉黑恶";
                }
                if (jcjCjxx.getJqsx().substring(1, 2).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉网";
                    } else {
                        jqsx = jqsx + ",涉网";
                    }
                }
                if (jcjCjxx.getJqsx().substring(2, 3).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="团伙";
                    } else {
                        jqsx = jqsx + ",团伙";
                    }
                }
                if (jcjCjxx.getJqsx().substring(3, 4).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉外";
                    } else {
                        jqsx = jqsx + ",涉外";
                    }
                }
                if (jcjCjxx.getJqsx().substring(4, 5).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉黄";
                    } else {
                        jqsx = jqsx + ",涉黄";
                    }
                }
                if (jcjCjxx.getJqsx().substring(5, 6).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉毒";
                    } else {
                        jqsx = jqsx + ",涉毒";
                    }
                }
                if (jcjCjxx.getJqsx().substring(6, 7).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉赌";
                    } else {
                        jqsx = jqsx + ",涉赌";
                    }
                }
                if (jcjCjxx.getJqsx().substring(7, 8).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉枪";
                    } else {
                        jqsx = jqsx + ",涉枪";
                    }
                }
                if (jcjCjxx.getJqsx().substring(8, 9).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="恐怖";
                    } else {
                        jqsx = jqsx + ",恐怖";
                    }
                }
                if (jcjCjxx.getJqsx().substring(9, 10).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="命案";
                    } else {
                        jqsx = jqsx + ",命案";
                    }
                }
                if (jcjCjxx.getJqsx().substring(10, 11).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉邪";
                    } else {
                        jqsx = jqsx + ",涉邪";
                    }
                }
                if (jcjCjxx.getJqsx().substring(11, 12).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="经侦";
                    } else {
                        jqsx = jqsx + ",经侦";
                    }
                }
                if (jcjCjxx.getJqsx().substring(12, 13).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉娼";
                    } else {
                        jqsx = jqsx + ",涉娼";
                    }
                }
                if (jcjCjxx.getJqsx().substring(13, 14).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉酒";
                    } else {
                        jqsx = jqsx + ",涉酒";
                    }
                }
                if (jcjCjxx.getJqsx().substring(14, 15).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉犬";
                    } else {
                        jqsx = jqsx + ",涉犬";
                    }
                }
                jcjCjxx.setJqsx(jqsx);
            }
        }

        return jcjCjxx;
    }

    @Override
    public JcjCjxx transformCjxxNew(JcjCjxx jcjCjxx) {

        //******************************** 不合格警情原因开始 *************************************

        //a1 1、处警类别为6位编码分三级从左往右排列(大类2位、中类2位、小类2位)，目前实际情况有只选大类，和只选大类及中类的情况，要求处警类别必须要选到小类，不符合的情况需要筛选出来。
        if (StrUtil.isNotEmpty(jcjCjxx.getCjlb()) && jcjCjxx.getCjlb().length() >= 6) {
            if("00".equals(jcjCjxx.getCjlb().substring(0, 2)) || "00".equals(jcjCjxx.getCjlb().substring(2, 4))){
                jcjCjxx.setA1(1);
            }
        }

        //a2 2、处警人必须>=2人，只有1人则需要筛选出来。
        String[] cjrArr = jcjCjxx.getCjr().split(",");
        if (cjrArr == null || cjrArr.length < 2) {
            jcjCjxx.setA2(1);
        }

        //a3 3、处警人的联系方式不能为110，必须是电话号码，非110即可。
        String cjrlxfs = jcjCjxx.getCjrlxfs();
        if (StrUtil.isBlank(cjrlxfs)) {
            jcjCjxx.setA3(1);
        }
        if (StrUtil.isNotBlank(cjrlxfs) && "110".equals(StrUtil.trim(cjrlxfs))) {
            jcjCjxx.setA3(1);
        }

        //a4 4、"简要警情及处理结果”:不能少于18个字。
        String cljgnr = jcjCjxx.getCljgnr();
        if (StrUtil.isBlank(cljgnr) || cljgnr.length() < 18) {
            jcjCjxx.setA4(1);
        }

        //a5 5、当处警类别为:“刑事警情中”的“盗窃”、“诈骗”、“抢劫”、
        //“抢夺”，“行政(治安)警情”中的“盗窃”、“诈骗”、“哄抢"、
        //“敲诈勒索”、“损毁公私财物”时，则“损失情况”为必填，为
        //空则筛选出来。(此条件均为报警类别中的中类，应包含该项下全部小类)
        //todo 统计处警类别 刑事警情和行政(治安)警情所有中类、小类


        //a6 6、涉警人员为空，则须根据警情编号筛选列出。todo 涉警人员字段待确定

        //a7 7、涉警人员的联系电话不能为连续相同的数字，可以为“无”。不符合的情况需筛选列出。todo 涉警人员、涉警人员电话字段待确定

        //a8 8、当处警类别为“纠纷”时，则“涉警人员”的记录数要>=2,否则需要筛选列出。todo 涉警人员字段待确定

        //a9 9、当处警类别为“走失人员"时，则“走失人员”的记录数要>=1,否则需要筛选列出。todo 走失人员字段待确定

        //a10 10、当处警类别为“招领人员”时，则“招领人员”的记录数要>=1, 否则需要筛选列出。todo 招领人员字段待确定

        //a11 11、当处警类别为:“刑事警情中”的“盗窃”、“诈骗”、“抢劫”、
        //“抢夺”，“行政(治安)警情”中的“盗窃”、“诈骗”、“哄抢”、
        //“敲诈勒索”、“损毁公私财物":“群众救助”的“物品信息”时，
        //则“涉警物品”的记录>=1，否则需要筛选出来。(此条件均为报警类别中的中类，应包含该项下全部小类)todo 涉警物品字段待确定

        //a12 12、处警类别为“盗窃单位”，“扰乱企事业单位秩序”则"涉警单位”记录数>=1,否则需要筛选出来。todo 涉警单位字段待确定

        //a13 13、报警类型为“放火”、“杀人”、“爆炸”、 “强奸猥亵”、“绑
        //架”、“两抢”、“抢劫”、“抢夺”、“投放危险物质"时，当接警时
        //间在1个半小时后，该接警的处警标识为“未处警”则短信通知
        //“接警单位”的负责人。(要有界面维护接警单位负责人的电话、
        //姓名)当“处警信息中的“登记时间”接警信息中的“接警时
        //间”>2小时”时，则需要筛选出来。(此条件均为报警类别中的
        //中类，应包含该项下全部小类)todo 统计报警类型 所有中项目小项

        //a14 14、接警编号的处警标识为“未处警”:当“ 当前时间-接警时
        //间>12小时、22 小时”时，则短信通知“接警单位”的负责人。
        //当“处警信息中”的“登记时间”一“接警信息中”的“接警时
        //间”>24小时”时，则需要筛选出来。
        //cjbs 处警标识	1未处警，2已处警，3移交，4重复警单

        JcjJjxx jjxx = jcjCjxx.getJjxx();
        if ("1".equals(jjxx.getCjbs())) {
            if (NumberUtil.mul(NumberUtil.sub(jcjCjxx.getDjsj(), jjxx.getJjrqsj()), 1000).longValue() >= DateUnit.DAY.getMillis()) {
                jcjCjxx.setA14(1);
            }
        }

        //a15 15、接警编号的处警标识为“已处警”，当“ 处警信息中的“登
        //记时间" -“接警信息”中的“接警时间”>24小时”时，则需要
        //筛选出来。
        if ("2".equals(jjxx.getCjbs())) {
            if (NumberUtil.mul(NumberUtil.sub(jcjCjxx.getDjsj(), jjxx.getJjrqsj()), 1000).longValue() >= DateUnit.DAY.getMillis()) {
                jcjCjxx.setA15(1);
            }
        }


        //a16 16、处警单的处理结果为“未审批”:当“当前时间登记时间>12
        //小时、22小时”时，则短信通知“接警单位”的负责人。当“处
        //警信息中的“领导处理时间”.“处警信息中的“登记时间”>24
        //小时”时，则需要筛选出来。todo 处理结果字段待确定

        //a17 17、处警单的处理结果为非“未审批”时:当“ 处警信息中的
        //“领导处理时间”。“处警信息中的“登记时间”>24小时”时，
        //则需要筛选出来。todo 处理结果字段待确定

        //a18 18、处警类别为“010403、011607、 011608、011609、011610、
        //011704、011705、011706、020210" 时，则“机动车”信息>=1，
        //否则需要筛选出来。todo 机动车字段待确定

        //a19 19、处警类别为“110601、110602、 110604、 110605、011803、
        //021890、080700、 080790” 时，则“假币”信息>=1，否则需要
        //筛选出来。todo 假币字段待确定

        //a20 20、当处警类别为“ 损毁公私财物”(020800及以下三级选项、
        //080106)时，处警内容中有“车”字时，涉警物品信息中“机动
        //车”信息>=1，否则报错。todo 涉警物品字段待确定

        //a21 21、报警类型为“021700及以下”或“012100及以下”或“060101”
        //或“202100及以下”或“240101"时;且“报警内容”或“事
        //发地址”中包含“棋牌室”、“馆”、“店”、“网吧”、“游戏”、“麻
        //将室”、“茶楼”;则“涉警单位”>=1，否则报错。todo 涉警单位字段待确定

        //a22 22、报警类型为“012403”或“021500及以下”或“202404”
        //或“202405”或“202411”或“060102”或“240102”时，且“报警内容”或“事发地址”中包含“会所”、“馆”、“足”、“浴”、
        //“酒店”、“KTV"、“舞厅”、“温泉”、“休闲”:则“涉警单位”>=1，
        //否则报错。todo 涉警单位字段待确定

        //a23 23、报警内容中有“拆迁公司”，则“涉警单位”>=1，否则报错。todo 涉警单位字段待确定

        //a24 24、处警内容中有“假币”时，处警类别应为:“110601”.“110602”、
        //“110604”、“110605”、“011803”、“021890"、“080700"、“080790"，否则报错。
        if (jcjCjxx.getCljgnr().contains("假币")) {
            if (!"110601".equals(jcjCjxx.getCjlb())
                    && !"110602".equals(jcjCjxx.getCjlb())
                    && !"110604".equals(jcjCjxx.getCjlb())
                    && !"110605".equals(jcjCjxx.getCjlb())
                    && !"011803".equals(jcjCjxx.getCjlb())
                    && !"021890".equals(jcjCjxx.getCjlb())
                    && !"080700".equals(jcjCjxx.getCjlb())
                    && !"080790".equals(jcjCjxx.getCjlb())) {

                jcjCjxx.setA24(1);

            }
        }

        //B、警综接警类
        //25、接警类型必须到小类，列出只有大类或只有大类和中类的情况。
        //26、报警内容不能少于18个字，否则列出。
        //27、报警方式、报警类型、来话类别不能为空，否则列出。
        //28、事发地址(不能少于4个字)、联系电话不能改为空，否则列出。
        //29、报警人性别不能为空，否则列出。
        //30、报警人姓名不能为空，否则列出。

        //******************************** 不合格警情原因结束 *************************************


        //报警形式
        if(jcjCjxx.getBjxs()!=null&&!jcjCjxx.getBjxs().isEmpty()){
            switch (jcjCjxx.getBjxs()){
                case "01":
                    jcjCjxx.setBjxs("110报警");
                    break;
                case "02":
                    jcjCjxx.setBjxs("112报警");
                    break;
                case "05":
                    jcjCjxx.setBjxs("其他电话报警");
                    break;
                case "08":
                    jcjCjxx.setBjxs("器材报警");
                    break;
                case "17":
                    jcjCjxx.setBjxs("其他部门移送");
                    break;
            }
        }
        //报警类型
        if(jcjCjxx.getBjlx()!=null&&!jcjCjxx.getBjlx().isEmpty()&&jcjCjxx.getBjlx().length()>=2) {
            switch (jcjCjxx.getBjlx().substring(0, 2)) {
                case "20":
                    jcjCjxx.setBjlx("违法犯罪");
                    break;
                case "21":
                    jcjCjxx.setBjlx("交通");
                    break;
                case "22":
                    jcjCjxx.setBjlx("火灾事故");
                    break;
                case "23":
                    jcjCjxx.setBjlx("群众求助");
                    break;
                case "24":
                    jcjCjxx.setBjlx("举报投诉");
                    break;
                case "25":
                    jcjCjxx.setBjlx("群体事件");
                    break;
                case "26":
                    jcjCjxx.setBjlx("纠纷");
                    break;
                case "27":
                    jcjCjxx.setBjlx("灾害事故");
                    break;
                case "28":
                    jcjCjxx.setBjlx("扬言");
                    break;
                case "29":
                    jcjCjxx.setBjlx("关注对象");
                    break;
                case "30":
                    jcjCjxx.setBjlx("警情备案");
                    break;
                case "90":
                    jcjCjxx.setBjlx("其他警情");
                    break;
                case "97":
                    jcjCjxx.setBjlx("涉林警情");
                    break;
            }
        }
        //派出所名称
        if(jcjCjxx.getDeptsname()!=null&&!jcjCjxx.getDeptsname().isEmpty()){
            jcjCjxx.setDeptsname(jcjCjxx.getDeptsname().replace("派出所",""));
        }
        //处警类别
        if(jcjCjxx.getCjlb()!=null&&!jcjCjxx.getCjlb().isEmpty()&&jcjCjxx.getCjlb().length()>=2) {
            switch (jcjCjxx.getCjlb().substring(0, 2)) {
                case "01":
                    jcjCjxx.setCjlb("刑事警情");
                    break;
                case "02":
                    jcjCjxx.setCjlb("治安警情");
                    break;
                case "03":
                    jcjCjxx.setCjlb("交通警情");
                    break;
                case "04":
                    jcjCjxx.setCjlb("火灾");
                    break;
                case "05":
                    jcjCjxx.setCjlb("群众求助");
                    break;
                case "06":
                    jcjCjxx.setCjlb("举报投诉");
                    break;
                case "07":
                    jcjCjxx.setCjlb("事件");
                    break;
                case "08":
                    jcjCjxx.setCjlb("纠纷");
                    break;
                case "09":
                    jcjCjxx.setCjlb("灾害事故");
                    break;
                case "10":
                    jcjCjxx.setCjlb("其他行政违法");
                    break;
            }
        }
        //各种时间
        if(jcjCjxx.getJjrqsj()!=null&&!jcjCjxx.getJjrqsj().isEmpty()){
            jcjCjxx.setJjrqsj(jcjCjxx.getJjrqsj().substring(0,4)+"/"+jcjCjxx.getJjrqsj().substring(4,6)+"/"+jcjCjxx.getJjrqsj().substring(6,8)+" "+jcjCjxx.getJjrqsj().substring(8,10)+":"+jcjCjxx.getJjrqsj().substring(10,12));
        }
        if(jcjCjxx.getCjsj()!=null&&!jcjCjxx.getCjsj().isEmpty()){
            jcjCjxx.setCjsj(jcjCjxx.getCjsj().substring(0,4)+"/"+jcjCjxx.getCjsj().substring(4,6)+"/"+jcjCjxx.getCjsj().substring(6,8)+" "+jcjCjxx.getCjsj().substring(8,10)+":"+jcjCjxx.getCjsj().substring(10,12));
        }
        if(jcjCjxx.getDjsj()!=null&&!jcjCjxx.getDjsj().isEmpty()){
            jcjCjxx.setDjsj(jcjCjxx.getDjsj().substring(0,4)+"/"+jcjCjxx.getDjsj().substring(4,6)+"/"+jcjCjxx.getDjsj().substring(6,8)+" "+jcjCjxx.getDjsj().substring(8,10)+":"+jcjCjxx.getDjsj().substring(10,12));
        }
        if(jcjCjxx.getDdxcsj()!=null&&!jcjCjxx.getDdxcsj().isEmpty()){
            jcjCjxx.setDdxcsj(jcjCjxx.getDdxcsj().substring(0,4)+"/"+jcjCjxx.getDdxcsj().substring(4,6)+"/"+jcjCjxx.getDdxcsj().substring(6,8)+" "+jcjCjxx.getDdxcsj().substring(8,10)+":"+jcjCjxx.getDdxcsj().substring(10,12));
        }
        if(jcjCjxx.getSfsjsx()!=null&&!jcjCjxx.getSfsjsx().isEmpty()){
            jcjCjxx.setSfsjsx(jcjCjxx.getSfsjsx().substring(0,4)+"/"+jcjCjxx.getSfsjsx().substring(4,6)+"/"+jcjCjxx.getSfsjsx().substring(6,8)+" "+jcjCjxx.getSfsjsx().substring(8,10)+":"+jcjCjxx.getSfsjsx().substring(10,12));
        }
        if(jcjCjxx.getSfsjxx()!=null&&!jcjCjxx.getSfsjxx().isEmpty()){
            jcjCjxx.setSfsjxx(jcjCjxx.getSfsjxx().substring(0,4)+"/"+jcjCjxx.getSfsjxx().substring(4,6)+"/"+jcjCjxx.getSfsjxx().substring(6,8)+" "+jcjCjxx.getSfsjxx().substring(8,10)+":"+jcjCjxx.getSfsjxx().substring(10,12));
        }
        if(jcjCjxx.getCjfksj()!=null&&!jcjCjxx.getCjfksj().isEmpty()){
            jcjCjxx.setCjfksj(jcjCjxx.getCjfksj().substring(0,4)+"/"+jcjCjxx.getCjfksj().substring(4,6)+"/"+jcjCjxx.getCjfksj().substring(6,8)+" "+jcjCjxx.getCjfksj().substring(8,10)+":"+jcjCjxx.getCjfksj().substring(10,12));
        }
        if(jcjCjxx.getLdclsj()!=null&&!jcjCjxx.getLdclsj().isEmpty()){
            jcjCjxx.setLdclsj(jcjCjxx.getLdclsj().substring(0,4)+"/"+jcjCjxx.getLdclsj().substring(4,6)+"/"+jcjCjxx.getLdclsj().substring(6,8)+" "+jcjCjxx.getLdclsj().substring(8,10)+":"+jcjCjxx.getLdclsj().substring(10,12));
        }
        if(jcjCjxx.getSpsj()!=null&&!jcjCjxx.getSpsj().isEmpty()){
            jcjCjxx.setSpsj(jcjCjxx.getSpsj().substring(0,4)+"/"+jcjCjxx.getSpsj().substring(4,6)+"/"+jcjCjxx.getSpsj().substring(6,8)+" "+jcjCjxx.getSpsj().substring(8,10)+":"+jcjCjxx.getSpsj().substring(10,12));
        }
        if(jcjCjxx.getXgsj()!=null&&!jcjCjxx.getXgsj().isEmpty()){
            jcjCjxx.setXgsj(jcjCjxx.getXgsj().substring(0,4)+"/"+jcjCjxx.getXgsj().substring(4,6)+"/"+jcjCjxx.getXgsj().substring(6,8)+" "+jcjCjxx.getXgsj().substring(8,10)+":"+jcjCjxx.getXgsj().substring(10,12));
        }
        if(jcjCjxx.getSpxgsj()!=null&&!jcjCjxx.getSpxgsj().isEmpty()){
            jcjCjxx.setSpxgsj(jcjCjxx.getSpxgsj().substring(0,4)+"/"+jcjCjxx.getSpxgsj().substring(4,6)+"/"+jcjCjxx.getSpxgsj().substring(6,8)+" "+jcjCjxx.getSpxgsj().substring(8,10)+":"+jcjCjxx.getSpxgsj().substring(10,12));
        }
        if(jcjCjxx.getCjysjssj()!=null&&!jcjCjxx.getCjysjssj().isEmpty()){
            jcjCjxx.setCjysjssj(jcjCjxx.getCjysjssj().substring(0,4)+"/"+jcjCjxx.getCjysjssj().substring(4,6)+"/"+jcjCjxx.getCjysjssj().substring(6,8)+" "+jcjCjxx.getCjysjssj().substring(8,10)+":"+jcjCjxx.getCjysjssj().substring(10,12));
        }

        //警情属性
        if(jcjCjxx.getJqsx()!=null&&!jcjCjxx.getJqsx().isEmpty()&&jcjCjxx.getJqsx().length()>=16){
            if (jcjCjxx.getJqsx().endsWith("1")) {
                jcjCjxx.setJqsx("其他");
            }else {
                String jqsx = "";
                if (jcjCjxx.getJqsx().substring(0, 1).equals("1")) {
                    jqsx = "涉黑恶";
                }
                if (jcjCjxx.getJqsx().substring(1, 2).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉网";
                    } else {
                        jqsx = jqsx + ",涉网";
                    }
                }
                if (jcjCjxx.getJqsx().substring(2, 3).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="团伙";
                    } else {
                        jqsx = jqsx + ",团伙";
                    }
                }
                if (jcjCjxx.getJqsx().substring(3, 4).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉外";
                    } else {
                        jqsx = jqsx + ",涉外";
                    }
                }
                if (jcjCjxx.getJqsx().substring(4, 5).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉黄";
                    } else {
                        jqsx = jqsx + ",涉黄";
                    }
                }
                if (jcjCjxx.getJqsx().substring(5, 6).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉毒";
                    } else {
                        jqsx = jqsx + ",涉毒";
                    }
                }
                if (jcjCjxx.getJqsx().substring(6, 7).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉赌";
                    } else {
                        jqsx = jqsx + ",涉赌";
                    }
                }
                if (jcjCjxx.getJqsx().substring(7, 8).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉枪";
                    } else {
                        jqsx = jqsx + ",涉枪";
                    }
                }
                if (jcjCjxx.getJqsx().substring(8, 9).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="恐怖";
                    } else {
                        jqsx = jqsx + ",恐怖";
                    }
                }
                if (jcjCjxx.getJqsx().substring(9, 10).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="命案";
                    } else {
                        jqsx = jqsx + ",命案";
                    }
                }
                if (jcjCjxx.getJqsx().substring(10, 11).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉邪";
                    } else {
                        jqsx = jqsx + ",涉邪";
                    }
                }
                if (jcjCjxx.getJqsx().substring(11, 12).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="经侦";
                    } else {
                        jqsx = jqsx + ",经侦";
                    }
                }
                if (jcjCjxx.getJqsx().substring(12, 13).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉娼";
                    } else {
                        jqsx = jqsx + ",涉娼";
                    }
                }
                if (jcjCjxx.getJqsx().substring(13, 14).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉酒";
                    } else {
                        jqsx = jqsx + ",涉酒";
                    }
                }
                if (jcjCjxx.getJqsx().substring(14, 15).equals("1")) {
                    if (jqsx.equals("")) {
                        jqsx ="涉犬";
                    } else {
                        jqsx = jqsx + ",涉犬";
                    }
                }
                jcjCjxx.setJqsx(jqsx);
            }
        }

        return jcjCjxx;
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfMonthlyJj(Map<String, Object> params) {
        return cjDao.countOfMonthlyJj(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfDailyJj(Map<String, Object> params) {
        return cjDao.countOfDailyJj(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfMonthlyCj(Map<String, Object> params) {
        return cjDao.countOfMonthlyCj(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfDailyCj(Map<String, Object> params) {
        return cjDao.countOfDailyCj(params);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfEverydayJj(String jjrqsj) {
        return cjDao.countOfEverydayJj(jjrqsj);
    }

    @Override
    @DataSource("slave2")
    public List<Map<String, Object>> countOfMonthlyCjlbAnalysis() {
        return cjDao.countOfMonthlyCjlbAnalysis();
    }

    @Override
    @DataSource("slave2")
    public List<JcjCjxx> queryYesterdayXSJQ(Map<String, Object> params) {
        //j.JJRQSJ,d.DEPTSNAME,c.CJLB,c.CLJGNR
        List<JcjCjxx> xSJQ = cjDao.queryYesterdayXSJQ(params);
        for (JcjCjxx jcjCjxx: xSJQ) {
            if(jcjCjxx.getJjrqsj()!=null&&!jcjCjxx.getJjrqsj().isEmpty()){
                jcjCjxx.setJjrqsj(jcjCjxx.getJjrqsj().substring(0,4)+"年"
                                    +jcjCjxx.getJjrqsj().substring(4,6)+"月"
                                    +jcjCjxx.getJjrqsj().substring(6,8)+"日"
                                    +jcjCjxx.getJjrqsj().substring(8,10)+"时"
                                    +jcjCjxx.getJjrqsj().substring(10,12)+"分");
            }
            jcjCjxx.setDeptsname(jcjCjxx.getDeptsname().replace("派出所",""));

            //警情类别
            switch(jcjCjxx.getCjlb().substring(2,6)){
                case "0000":
                    jcjCjxx.setCjlb("刑事");
                    break;
                case "0100":
                    jcjCjxx.setCjlb("纵火");
                    break;
                case "0190":
                    jcjCjxx.setCjlb("其他纵火");
                    break;
                case "0200":
                    jcjCjxx.setCjlb("爆炸");
                    break;
                case "0201":
                    jcjCjxx.setCjlb("自杀式爆炸");
                    break;
                case "0202":
                    jcjCjxx.setCjlb("投掷式爆炸");
                    break;
                case "0203":
                    jcjCjxx.setCjlb("预埋式爆炸");
                    break;
                case "0204":
                    jcjCjxx.setCjlb("邮寄式爆炸");
                    break;
                case "0290":
                    jcjCjxx.setCjlb("其他爆炸");
                    break;
                case "0300":
                    jcjCjxx.setCjlb("投放危险物质");
                    break;
                case "0390":
                    jcjCjxx.setCjlb("其他投放危险物质");
                    break;
                case "0400":
                    jcjCjxx.setCjlb("劫持");
                    break;
                case "0401":
                    jcjCjxx.setCjlb("劫持航空器");
                    break;
                case "0402":
                    jcjCjxx.setCjlb("劫持船只");
                    break;
                case "0403":
                    jcjCjxx.setCjlb("劫持汽车");
                    break;
                case "0404":
                    jcjCjxx.setCjlb("劫持人质");
                    break;
                case "0490":
                    jcjCjxx.setCjlb("其他劫持");
                    break;
                case "0500":
                    jcjCjxx.setCjlb("非法制售枪支弹药");
                    break;
                case "0590":
                    jcjCjxx.setCjlb("其他非法制售枪支弹药");
                    break;
                case "0600":
                    jcjCjxx.setCjlb("非法制售爆炸物品");
                    break;
                case "0690":
                    jcjCjxx.setCjlb("其他非法制售爆炸物品");
                    break;
                case "0700":
                    jcjCjxx.setCjlb("走私");
                    break;
                case "0790":
                    jcjCjxx.setCjlb("其他走私");
                    break;
                case "0800":
                    jcjCjxx.setCjlb("杀人");
                    break;
                case "0801":
                    jcjCjxx.setCjlb("抢劫杀人");
                    break;
                case "0802":
                    jcjCjxx.setCjlb("绑架杀人");
                    break;
                case "0803":
                    jcjCjxx.setCjlb("嫖赌纠纷杀人");
                    break;
                case "0804":
                    jcjCjxx.setCjlb("婚恋纠纷杀人");
                    break;
                case "0805":
                    jcjCjxx.setCjlb("家庭（家族）矛盾杀人");
                    break;
                case "0806":
                    jcjCjxx.setCjlb("邻里纠纷杀人");
                    break;
                case "0807":
                    jcjCjxx.setCjlb("其他纠纷杀人");
                    break;
                case "0808":
                    jcjCjxx.setCjlb("精神病人杀人");
                    break;
                case "0809":
                    jcjCjxx.setCjlb("故意杀人");
                    break;
                case "0810":
                    jcjCjxx.setCjlb("过失致人死亡");
                    break;
                case "0890":
                    jcjCjxx.setCjlb("其他原因杀人");
                    break;
                case "0900":
                    jcjCjxx.setCjlb("伤害");
                    break;
                case "0901":
                    jcjCjxx.setCjlb("故意伤害");
                    break;
                case "0902":
                    jcjCjxx.setCjlb("伤害致死");
                    break;
                case "0990":
                    jcjCjxx.setCjlb("其他伤害");
                    break;
                case "1000":
                    jcjCjxx.setCjlb("强奸");
                    break;
                case "1001":
                    jcjCjxx.setCjlb("轮奸");
                    break;
                case "1002":
                    jcjCjxx.setCjlb("强奸弱智或精神病人");
                    break;
                case "1003":
                    jcjCjxx.setCjlb("奸淫幼女");
                    break;
                case "1004":
                    jcjCjxx.setCjlb("猥亵儿童");
                    break;
                case "1005":
                    jcjCjxx.setCjlb("猥亵侮辱妇女");
                    break;
                case "1090":
                    jcjCjxx.setCjlb("其他强奸");
                    break;
                case "1100":
                    jcjCjxx.setCjlb("非法拘禁");
                    break;
                case "1190":
                    jcjCjxx.setCjlb("其他非法拘禁");
                    break;
                case "1200":
                    jcjCjxx.setCjlb("绑架");
                    break;
                case "1201":
                    jcjCjxx.setCjlb("绑架儿童中小学生");
                    break;
                case "1202":
                    jcjCjxx.setCjlb("绑架商人、企业主");
                    break;
                case "1290":
                    jcjCjxx.setCjlb("绑架其他人员");
                    break;
                case "1300":
                    jcjCjxx.setCjlb("拐卖妇女儿童");
                    break;
                case "1390":
                    jcjCjxx.setCjlb("其他拐卖妇女儿童");
                    break;
                case "1400":
                    jcjCjxx.setCjlb("强迫职工劳动");
                    break;
                case "1490":
                    jcjCjxx.setCjlb("其他强迫职工劳动");
                    break;
                case "1500":
                    jcjCjxx.setCjlb("非法侵入住宅");
                    break;
                case "1590":
                    jcjCjxx.setCjlb("其他非法侵入住宅");
                    break;
                case "1600":
                    jcjCjxx.setCjlb("抢劫");
                    break;
                case "1601":
                    jcjCjxx.setCjlb("入户抢劫");
                    break;
                case "1602":
                    jcjCjxx.setCjlb("拦路抢劫");
                    break;
                case "1603":
                    jcjCjxx.setCjlb("在公共交通工具上抢劫");
                    break;
                case "1604":
                    jcjCjxx.setCjlb("抢劫银行或其他金融机构");
                    break;
                case "1605":
                    jcjCjxx.setCjlb("抢劫珠宝店");
                    break;
                case "1606":
                    jcjCjxx.setCjlb("抢劫提（送）款员");
                    break;
                case "1607":
                    jcjCjxx.setCjlb("抢劫运钞车");
                    break;
                case "1608":
                    jcjCjxx.setCjlb("抢劫出租汽车");
                    break;
                case "1609":
                    jcjCjxx.setCjlb("抢劫汽车");
                    break;
                case "1610":
                    jcjCjxx.setCjlb("抢劫摩托车");
                    break;
                case "1611":
                    jcjCjxx.setCjlb("抢劫牲畜");
                    break;
                case "1612":
                    jcjCjxx.setCjlb("驾汽车抢劫");
                    break;
                case "1613":
                    jcjCjxx.setCjlb("驾摩托车抢劫");
                    break;
                case "1614":
                    jcjCjxx.setCjlb("驾助力车抢劫");
                    break;
                case "1615":
                    jcjCjxx.setCjlb("驾自行车抢劫");
                    break;
                case "1616":
                    jcjCjxx.setCjlb("徒步抢劫");
                    break;
                case "1617":
                    jcjCjxx.setCjlb("色情引诱抢劫");
                    break;
                case "1618":
                    jcjCjxx.setCjlb("拉人上车抢劫");
                    break;
                case "1619":
                    jcjCjxx.setCjlb("麻醉抢劫");
                    break;
                case "1620":
                    jcjCjxx.setCjlb("网络聊天见面抢劫");
                    break;
                case "1621":
                    jcjCjxx.setCjlb("冒充军警抢劫");
                    break;
                case "1622":
                    jcjCjxx.setCjlb("持枪抢劫");
                    break;
                case "1690":
                    jcjCjxx.setCjlb("其他抢劫");
                    break;
                case "1700":
                    jcjCjxx.setCjlb("盗窃");
                    break;
                case "1701":
                    jcjCjxx.setCjlb("盗窃民宅");
                    break;
                case "1702":
                    jcjCjxx.setCjlb("盗窃单位");
                    break;
                case "1703":
                    jcjCjxx.setCjlb("盗窃商店");
                    break;
                case "1704":
                    jcjCjxx.setCjlb("盗窃汽车");
                    break;
                case "1705":
                    jcjCjxx.setCjlb("盗窃车内财物");
                    break;
                case "1706":
                    jcjCjxx.setCjlb("盗窃摩托车");
                    break;
                case "1707":
                    jcjCjxx.setCjlb("盗窃燃油助力车");
                    break;
                case "1708":
                    jcjCjxx.setCjlb("盗窃自行车");
                    break;
                case "1709":
                    jcjCjxx.setCjlb("盗窃电动自行车");
                    break;
                case "1710":
                    jcjCjxx.setCjlb("盗窃通信线路");
                    break;
                case "1711":
                    jcjCjxx.setCjlb("盗窃牲畜");
                    break;
                case "1712":
                    jcjCjxx.setCjlb("盗窃保险柜");
                    break;
                case "1713":
                    jcjCjxx.setCjlb("扒窃");
                    break;
                case "1714":
                    jcjCjxx.setCjlb("盗窃工业原材料");
                    break;
                case "1715":
                    jcjCjxx.setCjlb("色情引诱盗窃");
                    break;
                case "1716":
                    jcjCjxx.setCjlb("盗窃养殖物");
                    break;
                case "1717":
                    jcjCjxx.setCjlb("盗窃农作物");
                    break;
                case "1718":
                    jcjCjxx.setCjlb("盗窃电脑零件");
                    break;
                case "1719":
                    jcjCjxx.setCjlb("拎包");
                    break;
                case "1790":
                    jcjCjxx.setCjlb("其他刑事类盗窃");
                    break;
                case "1800":
                    jcjCjxx.setCjlb("诈骗");
                    break;
                case "1801":
                    jcjCjxx.setCjlb("调包诈骗");
                    break;
                case "1802":
                    jcjCjxx.setCjlb("设摊诈骗");
                    break;
                case "1803":
                    jcjCjxx.setCjlb("假币假劵");
                    break;
                case "1804":
                    jcjCjxx.setCjlb("信用卡诈骗");
                    break;
                case "1805":
                    jcjCjxx.setCjlb("网络诈骗");
                    break;
                case "1806":
                    jcjCjxx.setCjlb("短信诈骗");
                    break;
                case "1807":
                    jcjCjxx.setCjlb("电话诈骗");
                    break;
                case "1808":
                    jcjCjxx.setCjlb("拾物平分诈骗");
                    break;
                case "1809":
                    jcjCjxx.setCjlb("碰瓷诈骗");
                    break;
                case "1810":
                    jcjCjxx.setCjlb("使用假票据诈骗");
                    break;
                case "1811":
                    jcjCjxx.setCjlb("贩卖假货");
                    break;
                case "1812":
                    jcjCjxx.setCjlb("借打手机诈骗");
                    break;
                case "1813":
                    jcjCjxx.setCjlb("招工征聘诈骗");
                    break;
                case "1814":
                    jcjCjxx.setCjlb("宝物诈骗案");
                    break;
                case "1815":
                    jcjCjxx.setCjlb("迷信诈骗案");
                    break;
                case "1890":
                    jcjCjxx.setCjlb("其他诈骗");
                    break;
                case "1900":
                    jcjCjxx.setCjlb("抢夺");
                    break;
                case "1901":
                    jcjCjxx.setCjlb("驾汽车抢夺");
                    break;
                case "1902":
                    jcjCjxx.setCjlb("驾摩托车抢夺");
                    break;
                case "1903":
                    jcjCjxx.setCjlb("驾助力车抢夺");
                    break;
                case "1904":
                    jcjCjxx.setCjlb("驾自行车抢夺");
                    break;
                case "1905":
                    jcjCjxx.setCjlb("徒步抢夺");
                    break;
                case "1906":
                    jcjCjxx.setCjlb("入店抢夺");
                    break;
                case "1907":
                    jcjCjxx.setCjlb("拉车门抢夺");
                    break;
                case "1908":
                    jcjCjxx.setCjlb("异物阻挡抢夺");
                    break;
                case "1990":
                    jcjCjxx.setCjlb("其他抢夺");
                    break;
                case "2000":
                    jcjCjxx.setCjlb("敲诈");
                    break;
                case "2001":
                    jcjCjxx.setCjlb("投毒敲诈");
                    break;
                case "2002":
                    jcjCjxx.setCjlb("隐私敲诈");
                    break;
                case "2003":
                    jcjCjxx.setCjlb("爆炸敲诈");
                    break;
                case "2004":
                    jcjCjxx.setCjlb("暴力敲诈");
                    break;
                case "2090":
                    jcjCjxx.setCjlb("其他敲诈勒索");
                    break;
                case "2100":
                    jcjCjxx.setCjlb("赌博");
                    break;
                case "2101":
                    jcjCjxx.setCjlb("六合彩");
                    break;
                case "2102":
                    jcjCjxx.setCjlb("聚众赌博");
                    break;
                case "2103":
                    jcjCjxx.setCjlb("赌博机");
                    break;
                case "2104":
                    jcjCjxx.setCjlb("网络赌博");
                    break;
                case "2190":
                    jcjCjxx.setCjlb("其他赌博");
                    break;
                case "2200":
                    jcjCjxx.setCjlb("聚众斗殴");
                    break;
                case "2290":
                    jcjCjxx.setCjlb("其他聚众斗殴");
                    break;
                case "2300":
                    jcjCjxx.setCjlb("寻衅滋事");
                    break;
                case "2390":
                    jcjCjxx.setCjlb("其他寻衅滋事");
                    break;
                case "2400":
                    jcjCjxx.setCjlb("妨害（扰乱）社会管理秩序");
                    break;
                case "2401":
                    jcjCjxx.setCjlb("走私毒品");
                    break;
                case "2402":
                    jcjCjxx.setCjlb("贩毒");
                    break;
                case "2403":
                    jcjCjxx.setCjlb("组织、强迫、容留卖淫");
                    break;
                case "2490":
                    jcjCjxx.setCjlb("其他妨害（扰乱）社会管理秩序");
                    break;
                case "9000":
                    jcjCjxx.setCjlb("其他刑事警情");
                    break;
                default:
                    jcjCjxx.setCjlb("其他刑事警情");
                    break;
            }
        }

        return xSJQ;
    }

    @Override
    @DataSource("slave2")
    public List<JcjCjxx> queryYesterdaySHSE(Map<String, Object> params) {
        //d.DeptSName ,c.JJBH,j.BJNR ,c.CLJGNR,c.BCCLJG,'涉黑恶' as JQSX,c.CJRHZXS,c.ZBLDXM
        List<JcjCjxx> sHSE =cjDao.queryYesterdaySHSE(params);
        for (JcjCjxx jcjCjxx: sHSE) {
            jcjCjxx.setDeptsname(jcjCjxx.getDeptsname().replace("派出所",""));
        }
        return sHSE;
    }

    @Override
    public List<JcjCjxx> queryCjSd(Map<String, Object> params) {
        return null;
    }

    @Override
    @DataSource("slave2")
    public int countOfLdcj8Djsj(Map<String, Object> params) {
        return cjDao.countOfLdcj8Djsj(params);

    }


}
