
var ysy_percent = 0; //云存储使用百分比
var yk_percent = 0;//云库已使用百分比
var yckInfoUrl = 'http://32.140.42.66:4000/accessYCKInfo';

var layer = layui.layer;

var date = new Date();
Y = date.getFullYear() + '-';
M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
var nowdate = Y + M + D;
// // 获取值班人员
// $.get(baseUrl + '/dm/queryZhiBanAllByDate?date=' + nowdate, function(res) {
//     console.log(res.data[28]);
//     $("#zbld").html("<span class='singlePolice' style='cursor:pointer;' data-policeCode=" + res.data[28].leaderList[0].policeCode + " data-name=" + res.data[28].leaderList[0].police + " data-shortPhone=" + res.data[28].leaderList[0].shortPhone + ">" + res.data[28].leaderList[0].police + "</span>");
//     var npoliceList = res.data[28].policeList;
//     var mj = "";
//     for (var i in npoliceList) {
//         if (npoliceList[i].policeCode != null && npoliceList[i].policeCode != '' && npoliceList[i].policeCode != undefined) {
//             mj += "<span class='singlePolice' style='cursor:pointer;' data-policeCode=" + npoliceList[i].policeCode + " data-name=" + npoliceList[i].police + " data-shortPhone=" + npoliceList[i].shortPhone + ">" + npoliceList[i].police + "</span>，"
//         }
//     }

//     $("#zbmj").html(mj.substr(0, mj.length - 1));
// });

//根据当天计算最近7天
var nowDate = new Date();
var day1str = new Date(nowDate - 7*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 7*24*3600*1000).getDate(),
day1Full =new Date(new Date() - 7*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 7*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 7*24*3600*1000).getDate()),
day2str = new Date(nowDate - 6*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 6*24*3600*1000).getDate(),
day2Full = new Date(new Date() - 6*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 6*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 6*24*3600*1000).getDate()),
day3str = new Date(nowDate - 5*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 5*24*3600*1000).getDate(),
day3Full = new Date(new Date() - 5*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 5*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 5*24*3600*1000).getDate()),
day4str = new Date(nowDate - 4*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 4*24*3600*1000).getDate(),
day4Full = new Date(new Date() - 4*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 4*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 4*24*3600*1000).getDate()),
day5str = new Date(nowDate - 3*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 3*24*3600*1000).getDate(),
day5Full = new Date(new Date() - 3*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 3*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 3*24*3600*1000).getDate()),
day6str = new Date(nowDate - 2*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 2*24*3600*1000).getDate(),
day6Full = new Date(new Date() - 2*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 2*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 2*24*3600*1000).getDate()),
day7str = new Date(nowDate - 1*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 1*24*3600*1000).getDate(),
day7Full = new Date(new Date() - 1*24*3600*1000).getFullYear()+'-'+(new Date(new Date() - 1*24*3600*1000).getMonth()+1 + '-' + new Date(new Date() - 1*24*3600*1000).getDate());

// 云存储系统情况
// 云库情况
var YKecharts = echarts.init(document.getElementById('echarts1'));
//获取日期
function getDate(){
    return new Date(nowDate).getMonth()+1 + '-' + new Date(nowDate).getDate(); //今天日期，如：3-21
}

var yk_v1 = 250000;//云库总容量
var yk_v2 = yk_v1*yk_percent;//云库已使用容量

function updateYK(YKInfo){
    var YKTotal = YKInfo.YKTotal;
    var YKUsed = YKInfo.YKUsed;
    var YKpercent = YKInfo.YKpercent;
    YKecharts.setOption({
        series: [{
            type: 'pie',
            radius: ['70%', '80%'],
            color: '#0088cc',
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [{
                value: YKUsed/YKTotal,
                name: '云库已使用容量',
                label: {
                    normal: {
                        formatter: '已使用:',
                        textStyle: {
                            fontSize: 10,
                            color: '#fff',
                        }
                    }
                }
            },{
                value: (YKTotal-YKUsed)/YKTotal,
                name: '云库未使用容量',
                label: {
                    normal: {
                        formatter: function(){
                            return YKpercent+ '%'; 
                        },
                        textStyle: {
                            fontSize: 10,
                            color: '#fff',
                        }
                    }
                },
                itemStyle: {
                    normal: {
                        color: 'rgba(255,255,255,.2)'
                    },
                    emphasis: {
                        color: '#fff'
                    }
                }
            } ]
        }]
    });
    $('.echarts1Detail').html(YKUsed+'W/'+YKTotal+'W');//图表下面显示的具体数据值
}

var yk_v3 = yk_v1 - yk_v2;//云库未使用容量
var yk_date = getDate();
$('.echarts1Detail').html('0W / 0W');//图表下面显示的具体数据值
var option1 = {
    series: [{
        type: 'pie',
        radius: ['70%', '80%'],
        color: '#0088cc',
        label: {
            normal: {
                position: 'center'
            }
        },
        data: [{
            value: yk_percent,
            name: '云库已使用容量',
            label: {
                normal: {
                    formatter: '已使用:',
                    textStyle: {
                        fontSize: 10,
                        color: '#fff',
                    }
                }
            }
        },{
            value: yk_v3/yk_v1,
            name: '云库未使用容量',
            label: {
                normal: {
                    formatter: function(){
                        return yk_percent*100+ '%'; 
                    },
                    textStyle: {
                        fontSize: 10,
                        color: '#fff',
                    }
                }
            },
            itemStyle: {
                normal: {
                    color: 'rgba(255,255,255,.2)'
                },
                emphasis: {
                    color: '#fff'
                }
            }
        } ]
    }]
};


(function(){
    console.log("云存云库开始了")
    $.post(yckInfoUrl,function(res){
        console.log('云存查询结果如下：');
        console.log(res);
        updateYC(res);
        updateYK(res);
    })
})();
function updateYC(YCdata){
    var ycUsed = (YCdata.usedCapacity/1024).toFixed(2);
    var totalCapacity = (YCdata.totalCapacity*0.8/1024).toFixed(2);
    var ycRemaind = totalCapacity - ycUsed;
    var onlineNodes = YCdata.onlineNodes;
    var totalNodes = YCdata.totalNodes;
    var offlineNodes = totalNodes - onlineNodes;
    YCecharts.setOption({

        series: [{
            type: 'pie',
            radius: ['70%', '80%'],
            color: '#fccb00',
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [{
                value: ycUsed,
                name: '云存储已使用',
                label: {
                    normal: {
                        formatter: '已使用',
                        textStyle: {
                            
                            color: '#fff',
                        }
                    }
                }
            },{
                value: ycRemaind,
                name: '云存储剩余空间',
                label: {
                    normal: {
                        formatter: function(params) {
                            return Math.round(ycUsed / totalCapacity *10000)/100 + '%'
                        },
                        textStyle: {
                            
                            color: '#fff',
                        }
                    }
                },
                itemStyle: {
                    normal: {
                        color: 'rgba(255,255,255,.2)'
                    },
                    emphasis: {
                        color: '#fff'
                    }
                }
            } ]
        }]
    });
    $('.echarts2Detail').html(ycUsed+'T/'+totalCapacity+'T');//修改云存图表下面显示的具体数据值

    Nodesecharts.setOption({
        series: [{
            type: 'pie',
            radius: ['70%', '80%'],
            color: '#62b62f',
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [{
                value: offlineNodes,
                name: '离线节点数量',
                label: {
                    normal: {
                        formatter: onlineNodes + '',
                        textStyle: {
                            fontSize: 20,
                            color: '#fff',
                        }
                    }
                },
                itemStyle: {
                    normal: {
                        color: 'rgba(255,255,255,.2)'
                    },
                    emphasis: {
                        color: '#fff'
                    }
                },
            }, {
                value: onlineNodes,
                name: '在线节点数量',
                label: {
                    normal: {
                        formatter: function(params) {
                            return '占比' + Math.round(onlineNodes / totalNodes * 100) + '%'
                        },
                        textStyle: {
                            color: '#aaa',
                            fontSize: 12
                        }
                    }
                }
            }]
        }]
    });
    $('.echarts3Detail').html(onlineNodes+' / '+totalNodes);//图表下面显示的具体数据值
}
//云存储空间容量
var YCecharts = echarts.init(document.getElementById('echarts2'));

var ycc_v1 = 3000;//云存储总容量
var ycc_v2 = ycc_v1*ysy_percent;//云存储已使用容量
var ycc_v3 = ycc_v1 - ycc_v2;//云存储剩余空间
$('.echarts2Detail').html(ycc_v2+'T/'+ycc_v1+'T');//图表下面显示的具体数据值
var option2 = {

    series: [{
        type: 'pie',
        radius: ['70%', '80%'],
        color: '#fccb00',
        label: {
            normal: {
                position: 'center'
            }
        },
        data: [{
            value: ycc_v2,
            name: '云存储已使用',
            label: {
                normal: {
                    formatter: '已使用',
                    textStyle: {
                        
                        color: '#fff',
                    }
                }
            }
        },{
            value: ycc_v3,
            name: '云存储剩余空间',
            label: {
                normal: {
                    formatter: function(params) {
                        return Math.round(ycc_v2 / ycc_v1 *10000)/100 + '%'
                    },
                    textStyle: {
                        
                        color: '#fff',
                    }
                }
            },
            itemStyle: {
                normal: {
                    color: 'rgba(255,255,255,.2)'
                },
                emphasis: {
                    color: '#fff'
                }
            }
        } ]
    }]
};


var Nodesecharts = echarts.init(document.getElementById('echarts3'));
var jd_1 = 0;//云存储节总数
var jd_2 = 0;//云存储在线节点数量
var jd_3 = jd_1 - jd_2;//云存储离线节点数量
$('.echarts3Detail').html(jd_2+' / '+jd_1);//图表下面显示的具体数据值

var option3 = {
    series: [{

        type: 'pie',
        radius: ['70%', '80%'],
        color: '#62b62f',
        label: {
            normal: {
                position: 'center'
            }
        },
        data: [{
            value: jd_3,
            name: '离线节点数量',
            label: {
                normal: {
                    formatter: jd_2 + '',
                    textStyle: {
                        fontSize: 20,
                        color: '#fff',
                    }
                }
            },
            itemStyle: {
                normal: {
                    color: 'rgba(255,255,255,.2)'
                },
                emphasis: {
                    color: '#fff'
                }
            },
        }, {
            value: jd_2,
            name: '在线节点数量',
            label: {
                normal: {
                    formatter: function(params) {
                        return '占比' + Math.round(jd_2 / jd_1 * 100) + '%'
                    },
                    textStyle: {
                        color: '#aaa',
                        fontSize: 12
                    }
                }
            }
        }]
    }]
};
setTimeout(function() {
    YKecharts.setOption(option1);
    YCecharts.setOption(option2);
    Nodesecharts.setOption(option3);
}, 500);

//给在途量或违法量看表格按钮加点击时间
function addTableClick(btnStyle,chartTableData){
    var btn2click = btnStyle === 'weifa'?'.weifaTableBtn':'.zaituTableBtn';
    $('body').on('click',btn2click,function(){
        var curChart = $(this).data('charttable');
        var tableCols = [[ //标题栏
            {field: 'time', title: '日期'}
            ,{field: 'count', title: '数量'}
            ]];
        if(curChart === 'zaitu'){
            //展示在途量表格
            var chartTableHtml='<h2>在途数据量<i class="closeBtn"></i></h2><div class="listContent"><div id="chartTable"></div></div>'
        }else{
            //展示违法量表格
            var chartTableHtml='<h2>违法数据量<i class="closeBtn"></i></h2><div class="listContent"><div id="chartTable"></div></div>'
        };
        $(".chartTableAlert").html(chartTableHtml);
        $(".chartTableAlert").show();
        layui.use('table', function(){
            var table = layui.table;
            //展示已知数据
            table.render({
                elem: '#chartTable'
                ,cols: tableCols
                ,data: chartTableData
                //,skin: 'line' //表格风格
                ,even: true
                //,page: true //是否显示分页
                //,limits: [5, 7, 10]
                //,limit: 5 //每页默认显示的数量
            });
        });
    })
}
// 在途量走势图
// 柱状图模块1
(function() {
    //0获取在途量数据
    //在途量接口
    var zaituUrl = 'http://32.140.42.66:8314/dahuawaijie/vehicle/statistics';
    //获取最近7天的开始时间和结束时间
    var zaituOption =JSON.stringify({
        "startTimeStr": day1Full+" 00:00:00",
        "endTimeStr": day7Full+" 23:59:59"
    })
    $.ajax({
        url:zaituUrl,
        type:'POST',
        dataType:'json',
        crossDomain:true,
        contentType:'application/json;charset=UTF-8',
        data:zaituOption,
        success:function(result){
            var numResult = result.results;
            console.log(result);
            var zaituChartVal = [];
            var zaituChartKey = [];
            var chartTableData = [];
            for(var i=0;i<numResult.length;i++){
                zaituChartVal.push(numResult[i].cnt);
                zaituChartKey.push(numResult[i].dayTime.substr(5));
                chartTableData.push({
                    'time':numResult[i].dayTime,
                    'count':numResult[i].cnt,
                })
            };
            addTableClick('zaitu',chartTableData);
                // 1实例化对象
            // var myChart = echarts.init(document.querySelector(".bar2 .chart"));
            var myChart = echarts.init(document.querySelector(".zaitu-line .chart"));
            // 2. 指定配置项和数据
            // 2.指定配置
            var option = {
                // 通过这个color修改两条线的颜色
                color: ["#ed3f35"],
                tooltip: {
                    trigger: "axis"
                },
                legend: {
                    // 如果series 对象有name 值，则 legend可以不用写data
                    // 修改图例组件 文字颜色
                    textStyle: {
                        color: "#4c9bfd"
                    },
                    // 这个10% 必须加引号
                    right: "10%"
                },
                grid: {
                    top: "20%",
                    left: "3%",
                    right: "4%",
                    bottom: "3%",
                    show: true, // 显示边框
                    borderColor: "#012f4a", // 边框颜色
                    containLabel: true // 包含刻度文字在内
                },

                xAxis: {
                    type: "category",
                    boundaryGap: false,
                    data:zaituChartKey,
                    axisTick: {
                        show: false // 去除刻度线
                    },
                    axisLabel: {
                        color: "#4c9bfd" // 文本颜色
                    },
                    axisLine: {
                        show: false // 去除轴线
                    }
                },
                yAxis: {
                    type: "value",
                    axisTick: {
                        show: false // 去除刻度线
                    },
                    axisLabel: {
                        color: "#4c9bfd" // 文本颜色
                    },
                    axisLine: {
                        show: false // 去除轴线
                    },
                    splitLine: {
                        lineStyle: {
                            color: "#012f4a" // 分割线颜色
                        }
                    }
                },
                series: [{
                        name: "在途量",
                        type: "line",
                        // true 可以让我们的折线显示带有弧度
                        smooth: true,
                        itemStyle:{normal:{label:{show:true}}},
                        data:zaituChartVal,
                    },

                ]
            };

            // 3. 把配置给实例对象
            myChart.setOption(option);
            // 4. 让图表跟随屏幕自动的去适应
            window.addEventListener("resize", function() {
                myChart.resize();
            });


            
        
            

        }

    })




})();

// 违法量走势图
// 柱状图模块1
(function() {
    //0获取在途量数据
    //在途量接口
    var zaituUrl = 'http://32.140.42.66:8314/dahuawaijie/illegal/statistics';
    //获取最近7天的开始时间和结束时间
    var zaituOption =JSON.stringify({
        "startTimeStr": day1Full+" 00:00:00",
        "endTimeStr": day7Full+" 23:59:59"
    })

    $.ajax({
        url:zaituUrl,
        type:'POST',
        dataType:'json',
        crossDomain:true,
        contentType:'application/json;charset=UTF-8',
        data:zaituOption,
        success:function(result){
            var numResult = result.results;
            console.log(result);
            var weifaChartVal = [];
            var weifaChartKey = [];
            var chartTableData = [];
            for(var i=0;i<numResult.length;i++){
                weifaChartVal.push(numResult[i].cnt);
                weifaChartKey.push(numResult[i].dayTime.substr(5));
                chartTableData.push({
                    'time':numResult[i].dayTime,
                    'count':numResult[i].cnt,
                })
            };
            addTableClick('weifa',chartTableData);
            // 1实例化对象
            // var myChart = echarts.init(document.querySelector(".bar2 .chart"));
            var myChart = echarts.init(document.querySelector(".weifa-line .chart"));
            // 2. 指定配置项和数据
            // 2.指定配置
            var option = {
                // 通过这个color修改两条线的颜色
                color: ["#ed3f35"],
                tooltip: {
                    trigger: "axis"
                },
                legend: {
                    // 如果series 对象有name 值，则 legend可以不用写data
                    // 修改图例组件 文字颜色
                    textStyle: {
                        color: "#4c9bfd"
                    },
                    // 这个10% 必须加引号
                    right: "10%"
                },
                grid: {
                    top: "20%",
                    left: "3%",
                    right: "4%",
                    bottom: "3%",
                    show: true, // 显示边框
                    borderColor: "#012f4a", // 边框颜色
                    containLabel: true // 包含刻度文字在内
                },

                xAxis: {
                    type: "category",
                    boundaryGap: false,
                    data: weifaChartKey,
                    axisTick: {
                        show: false // 去除刻度线
                    },
                    axisLabel: {
                        color: "#4c9bfd" // 文本颜色
                    },
                    axisLine: {
                        show: false // 去除轴线
                    }
                },
                yAxis: {
                    type: "value",
                    axisTick: {
                        show: false // 去除刻度线
                    },
                    axisLabel: {
                        color: "#4c9bfd" // 文本颜色
                    },
                    axisLine: {
                        show: false // 去除轴线
                    },
                    splitLine: {
                        lineStyle: {
                            color: "#012f4a" // 分割线颜色
                        }
                    }
                },
                series: [{
                        name: "违法量",
                        type: "line",
                        // true 可以让我们的折线显示带有弧度
                        smooth: true,
                        itemStyle:{normal:{label:{show:true}}},
                        data:weifaChartVal,
                    },

                ]
            };
            // 3. 把配置给实例对象
            myChart.setOption(option);
            // 4. 让图表跟随屏幕自动的去适应
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        }

    })
    
})();