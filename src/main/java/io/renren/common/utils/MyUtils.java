package io.renren.common.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tomchen
 * @date 2020-04-18
 */
public class MyUtils {

    /**
     * 将逗号分割的字符串 重新拼接 加入单引号  使得查询条件可用
     * @param arg
     * @return
     */
    public static String formatSqlIn(String arg){
        StringBuilder sb=new StringBuilder();
        if(StrUtil.isNotEmpty(arg)){
            String[] temp=arg.split(",");
            for(int i=0;i<temp.length;i++){

                if(i!=(temp.length-1)){
                    sb.append("'").append(temp[i].trim()).append("'").append(",");
                }else{
                    sb.append("'").append(temp[i].trim()).append("'");
                }
            }

        }

        return sb.toString();
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    //private static int table1(String tabelStr){
    //    Document doc = Jsoup.parse(tabelStr);
    //}


    public static void main(String[] args) {
        //MyUtils.test1();
        //MyUtils.test2();
        String replaceAll = ReUtil.replaceAll("123[1]23(456)789(000)---", "(\\([^\\)]*\\))", "");
        System.out.println(replaceAll);
        String s1 = "123[1]23（4）（5）789(000)---";
        String replaceAll2 = ReUtil.replaceAll(StrUtil.replace(StrUtil.replace(s1, "（", "("), "）", ")"), "(\\([^\\)]*\\))", "");
        System.out.println(replaceAll2);


    }

    public static void test1() {
        String text = "&nbsp;&nbsp;&nbsp;&nbsp;2020年05月10日7时至2020年05月11日7时，全区共接各类警情268起（交警大队31起、邗上63起、西湖27起、四季园24起、新盛20起、汊河16起、甘泉14起、蒋王13起、槐泗11起、念四桥11起、江阳11起、瓜洲8起、竹西6起、公道4起、杨寿4起、杨庙3起、方巷2起；处警未反馈警情72起，其中交警大队11起、西湖16起、邗上13起、四季园11起、汊河8起、江阳4起、槐泗3起、念四桥3起、蒋王2起、杨寿1起），<strong>其中刑事警情<a target='_blank'  href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01>8</a>\n" +
                "\t\t\t\t起（邗上<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100351>2</a>起、汊河<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100352>1</a>起、杨庙<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100355>1</a>起、槐泗<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100356>1</a>起、方巷<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100357>1</a>起、西湖<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100369>1</a>起、甘泉<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100372>1</a>起。其中八类案件：<a target='_blank'  href=CaseDetail8.aspx?b=20200510070000&e=20200511070000 >0起</a>，入室盗窃：<a target='_blank'  href=CaseDetailRS.aspx?b=20200510070000&e=20200511070000 >0起</a>，盗窃摩托车：<a target='_blank'  href=CaseDetailMTC.aspx?b=20200510070000&e=20200511070000 >0起</a>，盗窃非机动车：<a target='_blank'  href=CaseDetailFJDC.aspx?b=20200510070000&e=20200511070000 >1起</a>，扒窃：<a target='_blank'  href=CaseDetailPQ.aspx?b=20200510070000&e=20200511070000 >0起</a>，诈骗：<a target='_blank'  href=CaseDetailZP.aspx?b=20200510070000&e=20200511070000 >6起</a>，其他：<a target='_blank'  href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01 >1起</a>）</strong>，治安警情23起，交通事故27起，火灾事故0起，举报投诉17起，群众求助59起，纠纷68起，经济案件0起，其他警情66起。\n" +
                "\t\n" +
                "\t\n" +
                "\t\t\t\t<table style=\"display:none\" width=100% border=1 cellspacing=0 cellpadding=0 align=center style='font-size:11px;text-align:center' ><tr>\n" +
                "<th>八类案件</th><th>入室盗窃</th><th>盗窃摩托车</th><th>盗窃非机动车</th><th>扒窃</th><th>诈骗</th><th>其他</th></tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td><a target='_blank'  href=CaseDetail8.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailRS.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailMTC.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailFJDC.aspx?b=20200510070000&e=20200511070000 >1起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailPQ.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailZP.aspx?b=20200510070000&e=20200511070000 >6起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01 >1起</a></td></tr>\n" +
                "</table>\n" +
                "\t\t\t\t\t\t\t\t<table width=720px bgcolor=\"#cccccc\" cellpadding=0 cellspacing=1 border=0 style=\"font-size:8pt; text-align:center\" >\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t<th bgcolor=white width=60px>类别</th>\n" +
                "\t\t\t\t<th bgcolor=white>分局</th>\n" +
                "\t\t\t\t<th bgcolor=white>交大</th>\n" +
                "\t\t\t\t<th bgcolor=white>邗上</th>\n" +
                "\t\t\t\t<th bgcolor=white>汊河</th>\n" +
                "\t\t\t\t<th bgcolor=white>瓜洲</th>\n" +
                "\t\t\t\t<th bgcolor=white>蒋王</th>\n" +
                "\t\t\t\t<th bgcolor=white>杨庙</th>\n" +
                "\t\t\t\t<th bgcolor=white>槐泗</th>\n" +
                "\t\t\t\t<th bgcolor=white>方巷</th>\n" +
                "\t\t\t\t<th bgcolor=white>公道</th>\n" +
                "\t\t\t\t<th bgcolor=white>杨寿</th>\n" +
                "\t\t\t\t<th bgcolor=white>四季园</th>\n" +
                "\t\t\t\t<th bgcolor=white>念四桥</th>\n" +
                "\t\t\t\t<th bgcolor=white>新盛</th>\n" +
                "\t\t\t\t<th bgcolor=white>竹西</th>\n" +
                "\t\t\t\t<th bgcolor=white>西湖</th>\n" +
                "\t\t\t\t<th bgcolor=white>江阳</th>\n" +
                "\t\t\t\t<th bgcolor=white>甘泉</th>\n" +
                "<th bgcolor=white>汽车站</th>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>接警总数</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>268</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=17&b=20200510070000&e=20200511070000' target='_Blank'>31</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=51&b=20200510070000&e=20200511070000' target='_Blank'>63</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=52&b=20200510070000&e=20200511070000' target='_Blank'>16</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=53&b=20200510070000&e=20200511070000' target='_Blank'>8</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=54&b=20200510070000&e=20200511070000' target='_Blank'>13</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=55&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=56&b=20200510070000&e=20200511070000' target='_Blank'>11</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=57&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=58&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=64&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=65&b=20200510070000&e=20200511070000' target='_Blank'>24</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=66&b=20200510070000&e=20200511070000' target='_Blank'>11</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=73&b=20200510070000&e=20200511070000' target='_Blank'>20</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=68&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=69&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=71&b=20200510070000&e=20200511070000' target='_Blank'>11</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=72&b=20200510070000&e=20200511070000' target='_Blank'>14</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>刑事警情</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>8</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=55&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=57&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>治安警情</td>\n" +
                "\t\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>23</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=53&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=54&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=58&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>交通事故</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=03&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=03&dw=17&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>火灾事故</td>\n" +
                "\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=04&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>0</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>举报投诉</td>\n" +
                "\t\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>17</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=58&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=64&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=65&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=71&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>群众求助</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>59</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=53&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=54&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=55&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=57&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=65&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>10</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=68&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=71&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>纠纷</td>\n" +
                "\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>68</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>9</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=53&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=54&b=20200510070000&e=20200511070000' target='_Blank'>5</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=55&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=58&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=64&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=65&b=20200510070000&e=20200511070000' target='_Blank'>8</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>7</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=68&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=71&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>经济案件</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=11&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>0</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>其他警情</td>\n" +
                "\t\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>66</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\n" +
                "\t\t\t\t</table>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "<img src=GetChart.aspx?zr=20200510 width=718px height=210px /><br />\n" +
                "　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　统计时间：05月11日09时30分";
        text = StrUtil.subBefore(text, "<table", false);
        text = StrUtil.replace(HtmlUtil.cleanHtmlTag(text), "&nbsp;", "");
        text = StrUtil.replace(text, "\t", "");
        text = StrUtil.replace(text, "\n", "");
        System.out.println(text);
    }

    public static void test2() {
        String text = "&nbsp;&nbsp;&nbsp;&nbsp;2020年05月10日7时至2020年05月11日7时，全区共接各类警情268起（交警大队31起、邗上63起、西湖27起、四季园24起、新盛20起、汊河16起、甘泉14起、蒋王13起、槐泗11起、念四桥11起、江阳11起、瓜洲8起、竹西6起、公道4起、杨寿4起、杨庙3起、方巷2起；处警未反馈警情72起，其中交警大队11起、西湖16起、邗上13起、四季园11起、汊河8起、江阳4起、槐泗3起、念四桥3起、蒋王2起、杨寿1起），<strong>其中刑事警情<a target='_blank'  href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01>8</a>\n" +
                "\t\t\t\t起（邗上<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100351>2</a>起、汊河<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100352>1</a>起、杨庙<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100355>1</a>起、槐泗<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100356>1</a>起、方巷<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100357>1</a>起、西湖<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100369>1</a>起、甘泉<a   href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01&dw=32100372>1</a>起。其中八类案件：<a target='_blank'  href=CaseDetail8.aspx?b=20200510070000&e=20200511070000 >0起</a>，入室盗窃：<a target='_blank'  href=CaseDetailRS.aspx?b=20200510070000&e=20200511070000 >0起</a>，盗窃摩托车：<a target='_blank'  href=CaseDetailMTC.aspx?b=20200510070000&e=20200511070000 >0起</a>，盗窃非机动车：<a target='_blank'  href=CaseDetailFJDC.aspx?b=20200510070000&e=20200511070000 >1起</a>，扒窃：<a target='_blank'  href=CaseDetailPQ.aspx?b=20200510070000&e=20200511070000 >0起</a>，诈骗：<a target='_blank'  href=CaseDetailZP.aspx?b=20200510070000&e=20200511070000 >6起</a>，其他：<a target='_blank'  href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01 >1起</a>）</strong>，治安警情23起，交通事故27起，火灾事故0起，举报投诉17起，群众求助59起，纠纷68起，经济案件0起，其他警情66起。\n" +
                "\t\n" +
                "\t\n" +
                "\t\t\t\t<table style=\"display:none\" width=100% border=1 cellspacing=0 cellpadding=0 align=center style='font-size:11px;text-align:center' ><tr>\n" +
                "<th>八类案件</th><th>入室盗窃</th><th>盗窃摩托车</th><th>盗窃非机动车</th><th>扒窃</th><th>诈骗</th><th>其他</th></tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td><a target='_blank'  href=CaseDetail8.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailRS.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailMTC.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailFJDC.aspx?b=20200510070000&e=20200511070000 >1起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailPQ.aspx?b=20200510070000&e=20200511070000 >0起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetailZP.aspx?b=20200510070000&e=20200511070000 >6起</a></td>\n" +
                "<td><a target='_blank'  href=CaseDetail.aspx?b=20200510070000&e=20200511070000&lb=01 >1起</a></td></tr>\n" +
                "</table>\n" +
                "\t\t\t\t\t\t\t\t<table width=720px bgcolor=\"#cccccc\" cellpadding=0 cellspacing=1 border=0 style=\"font-size:8pt; text-align:center\" >\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t<th bgcolor=white width=60px>类别</th>\n" +
                "\t\t\t\t<th bgcolor=white>分局</th>\n" +
                "\t\t\t\t<th bgcolor=white>交大</th>\n" +
                "\t\t\t\t<th bgcolor=white>邗上</th>\n" +
                "\t\t\t\t<th bgcolor=white>汊河</th>\n" +
                "\t\t\t\t<th bgcolor=white>瓜洲</th>\n" +
                "\t\t\t\t<th bgcolor=white>蒋王</th>\n" +
                "\t\t\t\t<th bgcolor=white>杨庙</th>\n" +
                "\t\t\t\t<th bgcolor=white>槐泗</th>\n" +
                "\t\t\t\t<th bgcolor=white>方巷</th>\n" +
                "\t\t\t\t<th bgcolor=white>公道</th>\n" +
                "\t\t\t\t<th bgcolor=white>杨寿</th>\n" +
                "\t\t\t\t<th bgcolor=white>四季园</th>\n" +
                "\t\t\t\t<th bgcolor=white>念四桥</th>\n" +
                "\t\t\t\t<th bgcolor=white>新盛</th>\n" +
                "\t\t\t\t<th bgcolor=white>竹西</th>\n" +
                "\t\t\t\t<th bgcolor=white>西湖</th>\n" +
                "\t\t\t\t<th bgcolor=white>江阳</th>\n" +
                "\t\t\t\t<th bgcolor=white>甘泉</th>\n" +
                "<th bgcolor=white>汽车站</th>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>接警总数</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>268</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=17&b=20200510070000&e=20200511070000' target='_Blank'>31</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=51&b=20200510070000&e=20200511070000' target='_Blank'>63</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=52&b=20200510070000&e=20200511070000' target='_Blank'>16</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=53&b=20200510070000&e=20200511070000' target='_Blank'>8</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=54&b=20200510070000&e=20200511070000' target='_Blank'>13</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=55&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=56&b=20200510070000&e=20200511070000' target='_Blank'>11</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=57&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=58&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=64&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=65&b=20200510070000&e=20200511070000' target='_Blank'>24</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=66&b=20200510070000&e=20200511070000' target='_Blank'>11</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=73&b=20200510070000&e=20200511070000' target='_Blank'>20</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=68&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=69&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=71&b=20200510070000&e=20200511070000' target='_Blank'>11</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?dw=72&b=20200510070000&e=20200511070000' target='_Blank'>14</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>刑事警情</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>8</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=55&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=57&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=01&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>治安警情</td>\n" +
                "\t\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>23</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=53&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=54&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=58&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=02&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>交通事故</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=03&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=03&dw=17&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>火灾事故</td>\n" +
                "\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=04&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>0</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>举报投诉</td>\n" +
                "\t\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>17</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=58&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=64&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=65&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=71&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=06&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>群众求助</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>59</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>27</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=53&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=54&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=55&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=57&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=65&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>10</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=68&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=71&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=05&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>纠纷</td>\n" +
                "\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>68</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>9</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=52&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=53&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=54&b=20200510070000&e=20200511070000' target='_Blank'>5</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=55&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=56&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=58&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=64&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=65&b=20200510070000&e=20200511070000' target='_Blank'>8</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=73&b=20200510070000&e=20200511070000' target='_Blank'>7</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=68&b=20200510070000&e=20200511070000' target='_Blank'>3</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=69&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=71&b=20200510070000&e=20200511070000' target='_Blank'>4</a></td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=08&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>6</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>经济案件</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=11&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>0</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td bgcolor=white width=60px>其他警情</td>\n" +
                "\t\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=321003&b=20200510070000&e=20200511070000' target='_Blank'>66</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=51&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=66&b=20200510070000&e=20200511070000' target='_Blank'>2</a></td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white>0</td>\n" +
                "\t\t\t\t<td bgcolor=white><a href='CaseDetail2.aspx?lb=99&dw=72&b=20200510070000&e=20200511070000' target='_Blank'>1</a></td><td bgcolor=white>0</td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\n" +
                "\t\t\t\t</table>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "<img src=GetChart.aspx?zr=20200510 width=718px height=210px /><br />\n" +
                "　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　统计时间：05月11日09时30分";

        text = StrUtil.subAfter(text, "</table>", false);
        text = "</table>" + text;
        text = HtmlUtil.removeAllHtmlAttr(text, "table", "th", "tr", "td", "a");
        text = HtmlUtil.removeHtmlTag(text, "img");
        text = StrUtil.replace(text, "&nbsp;", "");
        text = StrUtil.replace(text, "\t", "");
        text = StrUtil.replace(text, "\n", "");
        text = StrUtil.subBefore(text, "<br", true);

        //String table1 = StrUtil.subBefore(text, "</table>", false) + "</table>";
        //String table2 = StrUtil.subAfter(text, "</table>", false);

        System.out.println(text);
        //System.out.println(table1);
        //System.out.println(table2);

        Document doc = Jsoup.parse(text);
        Elements rows = doc.select("table").get(0).select("tr");

        if (rows.size() == 1) {
            System.out.println("没有结果");
        }else {
            for(int i=1;i<4;i++)
            {
                System.out.println("-----------------------------------------------------------------");
                Element row = rows.get(i);
                System.out.println(row.select("td").get(0).text() + "—" + row.select("td").get(1).text() + "-" + row.select("td").get(2).text());
                System.out.println("-----------------------------------------------------------------");
            }
        }

    }
}
