package io.renren.modules.dm.consts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//邗江分局总值班表
//        头
//        分局总值班：总值班长 leader，副总值班长polices，值班长bzs1，值班驾驶员bzs2
//        邗江分局D0001
//        左侧2分局业务部门，6大队，5中队，合计13
//        机关部门（写死，如下），值班领导leader，值班民警polices
//        办公室D0055
//        指挥中心D0048
//        巡特警大队D0050
//        刑警大队D0018
//        治安大队D0011
//        交警大队D0032
//        督察大队D0057
//        法制大队D0047
//        交大汊河中队D0044
//        交大公道中队D0038
//        交大瓜洲中队D0037
//        交大甘泉中队D0039
//        交大方巷中队D0040
//        右侧16派出所
//        派出所（写死，如下），驻所领导bzs1，值班领导leader，值班人员polices
//        邗上D0059
//        汊河D0068
//        瓜洲D0078
//        蒋王D0086
//        甘泉D0245
//        杨庙D0095
//        新盛D00003
//        四季园D0181
//        槐泗D0102
//        方巷D0112
//        公道D0122
//        杨寿D0173
//        念四桥D0191
//        竹西D0209
//        西湖D0220
//        江阳D0238
public class DmConsts implements Serializable {

    //值班领导
    public final static int TYPE_LEADER = 1;
    //值班民警
    public final static int TYPE_ZBPOLICE = 0;
    //社区民警
    public final static int TYPE_SQPOLICE = -1;

    //社区民警
    public final static String ROLE_DM = "ROLE0021";

    //巡特警大队
    public static final String XTJDD = "D0050";



    public static List<String> pcs16DeptIdList = new ArrayList<>();
    public static List<String> zd5DeptIdList = new ArrayList<>();
    public static List<String> otherDeptList = new ArrayList<>();
    //巡特警邗巡3号线reportPointId
    public static List<String> xtj3List = new ArrayList<>();
    //巡特警骑警队reportPointId
    public static List<String> qjd2List = new ArrayList<>();
    public static final Map<String, String> XTJ_NAME = new HashMap<>();



    static {
        pcs16DeptIdList.add("D0059");
        pcs16DeptIdList.add("D0068");
        pcs16DeptIdList.add("D0078");
        pcs16DeptIdList.add("D0086");
        pcs16DeptIdList.add("D0245");
        pcs16DeptIdList.add("D0095");
        pcs16DeptIdList.add("D00003");
        pcs16DeptIdList.add("D0181");
        pcs16DeptIdList.add("D0102");
        pcs16DeptIdList.add("D0112");
        pcs16DeptIdList.add("D0122");
        pcs16DeptIdList.add("D0173");
        pcs16DeptIdList.add("D0191");
        pcs16DeptIdList.add("D0209");
        pcs16DeptIdList.add("D0220");
        pcs16DeptIdList.add("D0238");

        zd5DeptIdList.add("D0044");
        zd5DeptIdList.add("D0038");
        zd5DeptIdList.add("D0037");
        zd5DeptIdList.add("D0039");
        zd5DeptIdList.add("D0040");

        otherDeptList.add("D0001");
        otherDeptList.add("D0055");
        otherDeptList.add("D0048");
        otherDeptList.add("D0018");
        otherDeptList.add("D0011");
        otherDeptList.add("D0032");
        otherDeptList.add("D0057");
        otherDeptList.add("D0047");

        xtj3List.add("R60058");
        xtj3List.add("R60059");
        xtj3List.add("R60060");

        qjd2List.add("R60061");

        XTJ_NAME.put("R60058", "巡特警邗巡1号");
        XTJ_NAME.put("R60059", "巡特警邗巡2号");
        XTJ_NAME.put("R60060", "巡特警邗巡3号");
        XTJ_NAME.put("R60061", "巡特警骑警队");
    }



}
