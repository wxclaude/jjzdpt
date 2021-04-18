var layer = layui.layer;
var baseUrl = "http://192.168.32.103:8080";
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



(function() {
    $('body').on('click', '#headTitle', function() {
        $('.myhsqjjbox .myhsqjj').html('').append(
            `
	  <div class="myhcontent">
		<p style="text-indent:40px">明月湖警务工作站成立于2020年9月1日，隶属于扬州市公安局邗江分局巡特警大队。
		在区委区政府、市局、分局的关心和支持下，2020年5月在京华城东停车场始建，
		于2020年9月1日开始实体化运行。配备民警8人、勤务辅警22人，警用汽车1辆，
		警用摩托车11辆。
		</p>
		</br>
		<p style="text-indent:40px">明月湖警务工作站位于京华城综合体东侧，
		所属环明月湖综合体有京华城、R-MALL购物广场、五彩世界购物广场、昌建广场、
		星耀天地等五个商业综合体及双博馆、国展中心、会议中心、明月湖酒店等大型活动场馆，
		中小学校、幼儿园共7所学校，辐射区域约5平方公里。警务站24小时值守，实行警力叠加型勤务模式，
		采取车巡、摩巡有机结合,动中备勤，对巡逻中发现的警情予以先期处置。
		</p>
		</br>
		<p style="text-indent:40px">明月湖警务工作站位于京华城综合体东侧，
		警务站建筑面积239平方米，为满足接处警、破案打击、服务群众等工作需要，功能分区全面优化，
		含接待大厅、调解室、视频会议室、
		视频监控室、民警办公室、站长室、询问室、装备室、更衣室、备勤室、生活间、卫生间等十二个功能。
		</p>
	  </div>
	  
	  `);


        $('.myhsqjjbox').show();
    })
})();

// 云存储系统情况
// 云库情况
var myChart1 = echarts.init(document.getElementById('echarts1'));
var yk_v1 = 200000;//云库总容量
var yk_v2 = 100000;//云库已使用容量
var yk_v3 = yk_v1 - yk_v2;//云库未使用容量
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
            value: yk_v2,
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
            value: yk_v3,
            name: '云库未使用容量',
            label: {
                normal: {
                    formatter: function(){
                        return yk_v3 + 'w'; 
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

//云存储空间容量
var myChart3 = echarts.init(document.getElementById('echarts3'));
var ycc_v1 = 1500;//云存储总容量
var ycc_v2 = 500;//云存储已使用容量
var ycc_v3 = ycc_v1 - ycc_v2;//云存储剩余空间
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
                        return Math.round(ycc_v2 / ycc_v1 * 100) + '%'
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


var myChart2 = echarts.init(document.getElementById('echarts2'));
var jd_1 = 11;//云存储节总数
var jd_2 = 11;//云存储在线节点数量
var jd_3 = jd_1 - jd_2;//云存储离线节点数量
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
    myChart1.setOption(option1);
    myChart2.setOption(option2);
    myChart3.setOption(option3);
}, 500);

//在途量表格
(function(){
    //根据当天计算最近7天
    var nowDate = new Date();

    var day1str = new Date(nowDate - 7*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 7*24*3600*1000).getDate(),
        day2str = new Date(nowDate - 6*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 6*24*3600*1000).getDate(),
        day3str = new Date(nowDate - 5*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 5*24*3600*1000).getDate(),
        day4str = new Date(nowDate - 4*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 4*24*3600*1000).getDate(),
        day5str = new Date(nowDate - 3*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 3*24*3600*1000).getDate(),
        day6str = new Date(nowDate - 2*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 2*24*3600*1000).getDate(),
        day7str = new Date(nowDate - 1*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 1*24*3600*1000).getDate();

    var zaituData = [
        {
            'firstCol':'早高峰',
            'day1':'570000',
            'day2':'650000',
            'day3':'490000',
            'day4':'510000',
            'day5':'590000',
            'day6':'690000',
            'day7':'600000'
        },
        {
            'firstCol':'晚高峰',
            'day1':'570000',
            'day2':'650000',
            'day3':'490000',
            'day4':'510000',
            'day5':'590000',
            'day6':'690000',
            'day7':'600000'
        },
        {
            'firstCol':'全天',
            'day1':'570000',
            'day2':'650000',
            'day3':'490000',
            'day4':'510000',
            'day5':'590000',
            'day6':'690000',
            'day7':'600000'
        },
    ];



    layui.use('table', function(){
        var table = layui.table;
        
        //展示已知数据
        table.render({
          elem: '#zaitu-table'
          ,cols: [[ //标题栏
            {field: 'firstCol', title: '', width: 75}
            ,{field: 'day1', title: day1str, width: 80}
            ,{field: 'day2', title: day2str, width: 80}
            ,{field: 'day3', title: day3str, width:80}
            ,{field: 'day4', title: day4str, width:80}
            ,{field: 'day5', title: day5str, width: 80}
            ,{field: 'day6', title: day6str, width: 80}
            ,{field: 'day7', title: day7str, width: 80}
          ]]
          ,data: zaituData
          //,skin: 'line' //表格风格
          ,even: true
          //,page: true //是否显示分页
          //,limits: [5, 7, 10]
          //,limit: 5 //每页默认显示的数量
        });
      });
})();

// 在途量走势图
// 柱状图模块1
(function() {
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
            data: ["3-25", "3-26", "3-27", "3-28", "3-29", "3-30", "3-31"],
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
                data: ["960.6", "929.7", "1159.7", "1162.0", "1371.2", "1689.6", "1107.6", "1150.6", "1005.4", "1700.9", "1600.3"],
            },

        ]
    };

    // 3. 把配置给实例对象
    myChart.setOption(option);
    // 4. 让图表跟随屏幕自动的去适应
    window.addEventListener("resize", function() {
        myChart.resize();
    });
})();

// 违法量走势图
// 柱状图模块1
(function() {
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
            data: ["3-25", "3-26", "3-27", "3-28", "3-29", "3-30", "3-31"],
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
                data: ["960.6", "929.7", "1159.7", "1162.0", "1371.2", "1689.6", "1107.6", "1150.6", "1005.4", "1700.9", "1600.3"],
            },

        ]
    };

    // 3. 把配置给实例对象
    myChart.setOption(option);
    // 4. 让图表跟随屏幕自动的去适应
    window.addEventListener("resize", function() {
        myChart.resize();
    });
})();

//违法量表格
(function(){
    //根据当天计算最近7天
    var nowDate = new Date();

    var day1str = new Date(nowDate - 7*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 7*24*3600*1000).getDate(),
        day2str = new Date(nowDate - 6*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 6*24*3600*1000).getDate(),
        day3str = new Date(nowDate - 5*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 5*24*3600*1000).getDate(),
        day4str = new Date(nowDate - 4*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 4*24*3600*1000).getDate(),
        day5str = new Date(nowDate - 3*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 3*24*3600*1000).getDate(),
        day6str = new Date(nowDate - 2*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 2*24*3600*1000).getDate(),
        day7str = new Date(nowDate - 1*24*3600*1000).getMonth()+1 + '-' + new Date(nowDate - 1*24*3600*1000).getDate();

    var weifaData = [
        {
            'firstCol':'早高峰',
            'day1':'30000',
            'day2':'40000',
            'day3':'25000',
            'day4':'29000',
            'day5':'31000',
            'day6':'32000',
            'day7':'33000'
        },
        {
            'firstCol':'晚高峰',
            'day1':'30000',
            'day2':'40000',
            'day3':'25000',
            'day4':'29000',
            'day5':'31000',
            'day6':'32000',
            'day7':'33000'
        },
        {
            'firstCol':'全天',
            'day1':'30000',
            'day2':'40000',
            'day3':'25000',
            'day4':'29000',
            'day5':'31000',
            'day6':'32000',
            'day7':'33000'
        },
    ];



    layui.use('table', function(){
        var table = layui.table;
        
        //展示已知数据
        table.render({
          elem: '#weifa-table'
          ,cols: [[ //标题栏
            {field: 'firstCol', title: '', width: 175}
            ,{field: 'day1', title: day1str, width: 170}
            ,{field: 'day2', title: day2str, width: 70}
            ,{field: 'day3', title: day3str, minWidth: 70}
            ,{field: 'day4', title: day4str, minWidth: 70}
            ,{field: 'day5', title: day5str, width: 70}
            ,{field: 'day6', title: day6str, width: 70}
            ,{field: 'day7', title: day7str, width: 70}
          ]]
          ,data: weifaData
          //,skin: 'line' //表格风格
          ,even: true
          //,page: true //是否显示分页
          //,limits: [5, 7, 10]
          //,limit: 5 //每页默认显示的数量
        });
      });
})();