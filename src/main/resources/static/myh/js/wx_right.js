//添加弹窗
// (function () {
//     if(!$('body .fykdAlertWindow')){
//         $('body').append('<article class="fykdAlertWindow" onclick="closedList()"></article>')
//     }
//
//     if(!$('body .sqsjAlertWindow')){
//         $('body').append('<article class="sqsjAlertWindow" onclick="closedList()"></article>')
//     }
//     $('#fykd>h2').html('防疫卡口<a href="javascript:;" class="fr"  onclick="infomationTip()">查看全部</a>');
//     $('#sqsj>h2').html('商圈事件<a href="javascript:;" class="fr"  onclick="infomationTip()">查看全部</a>');
// })()
function zbmjAll() {
    $(".zbmjAlert").show();
}

function getRoadsNum(arr) {
    var obj = {};
    for (var i in arr ){
        var roadskey = arr[i].channel_detail_obj.roadOrg;
        if(!obj[roadskey]){
            obj[roadskey] = 1;
        }else{
            obj[roadskey]++;
        }
    }
    return Object.keys(obj).length;
}
var url = 'http://localhost:4000/';
//
(function(){
    //$.get(url,'',function(result){
        var result = {
            'list':[],
            'category':{
                'dianjing':[],
                'kakou':[],
                'qiuji':[],
                'quanchen':[],
                'record':[],
                'lixian':[],
                'butong':[],
                'xiulu':[],
                'weixiu':[]
            }
        }
//平台点位情况
            //接入平台数量情况展示
            //丢一种类型的数据进函数，返回路口数量
            var dianjingRoads = getRoadsNum(result.category.dianjing);
            var kakouRoads = getRoadsNum(result.category.kakou);
            var qiujiRoads = getRoadsNum(result.category.qiuji);
            var quanchenRoads = getRoadsNum(result.category.quanchen);
            var jieruPointsData = {
                'key':['电警路口数量：','球机路口数量：','卡口路口数量：','全程监控路口数量：'],
                'value':[dianjingRoads,qiujiRoads,kakouRoads,quanchenRoads],
                'channel_value':[result.category.dianjing,result.category.qiuji,result.category.kakou,result.category.quanchen],
                'target':['dianjing','qiuji','kakou','quanchen']
            };
            var jieruPointsHtml='';


            for(var i=0;i<jieruPointsData.key.length;i++){
                jieruPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">'+jieruPointsData.key[i]+'</div><div class="pointsInfoBody" data-target="'+jieruPointsData.target[i]+'">'+jieruPointsData.value[i]+' (相机数量：<span class="pointsNum">'+jieruPointsData.channel_value[i].length+'</span>)</div></div>';
            };
            $('.jieru-points .points-body').html(jieruPointsHtml);
            //平台点位状态情况展示
            var onlineC = result.list.length - result.category.lixian.length;
            var statusPointsData = {
                'key':['平台相机总数：','在线相机数量：','离线相机数量：','网络不通相机数量：','修路状态相机数量：','维修中相机数量：'],
                'value':[result.list.length,onlineC,result.category.lixian.length,result.category.butong.length,result.category.xiulu.length,result.category.weixiu.length],
                'target':['total','zaixian','lixian','butong','xiulu','weixiu']
            };
            var statusPointsHtml = '';
            for(var i=0;i<statusPointsData.key.length;i++){
                statusPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">'+statusPointsData.key[i]+'</div><div class="pointsInfoBody" data-target="'+statusPointsData.target[i]+'"><span>'+statusPointsData.value[i]+'</span></div></div>';
            };
            $('.status-points .points-body').html(statusPointsHtml);
            //平台右下角情况展示
            //录像情况
            var recordPointsData = {
                'key':['平台录像相机数量：'],
                'value':[result.category.record.length],
                'target':['record']
            };
            var recordPointsHtml='';
            for(var i=0;i<recordPointsData.key.length;i++){
                recordPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">'+recordPointsData.key[i]+'</div><div class="pointsInfoBody" data-target="'+recordPointsData.target[i]+'"><span>'+recordPointsData.value[i]+'</span></div></div>';
            };
            $('.others-points .points-body').html(recordPointsHtml);

            $('body').on('click','.pointsInfo .pointsInfoBody span',function(){
                var targetStr = $(this).data('target');
                var targetAlertHtml='<h2>设备详细情况<i class="closeBtn"></i></h2><div class="listContent"><div id="'+targetStr+'Table"></div></div>',targetTableData;
                switch(targetStr){
                    case 'total':     
                        targetTableData=result.list;
                        break;
                    case 'dianjing':
                        targetTableData=result.category.dianjing;
                        break;
                    case 'qiuji':
                        targetTableData=result.category.qiuji;
                        break;
                    case 'kakou':
                        targetTableData=result.category.kakou;
                        break;
                    case 'quanchen':
                        targetTableData=result.category.quanchen;
                        break;
                    case 'record':
                        targetTableData=result.category.record;
                        break;
                    case 'lixian':
                        targetTableData=result.category.lixian;
                        break;
                    case 'butong':
                        targetTableData=result.category.butong;
                        break;
                    case 'xiulu':
                        targetTableData=result.category.xiulu;
                        break;
                    case 'weixiu':
                        targetTableData=result.category.weixiu;
                        break;
                    default:
                        break;
                }
                
                $('.pointsTableAlert').html(targetAlertHtml);
                $('.pointsTableAlert').show();
                var dataA=[
                    {   'CHANNEL_NAME':'东-文昌中路与泰州路-电警1',
                        'channel_IP':'192.168.100.111',
                        'build_company':'国脉',
                        'IS_ONLINE':1,
                        'build_time':'2000-01-01',
                        'program_version':'',
                        'channel_detail_obj':{
                            'roadOrg':'未配置所属路口',
                            'channel_style':'未配置相机类型',
                            'channel_record':'录像未知',
                            'channel_status':'未配置在线状态'
                        }
                    }
                ];
                var dataArr = targetTableData;
                for(var i in dataArr){
                    dataArr[i]['roadOrg'] = dataArr[i].channel_detail_obj.roadOrg;
                    dataArr[i]['channel_style'] = dataArr[i].channel_detail_obj.channel_style;
                    dataArr[i]['channel_record'] = dataArr[i].channel_detail_obj.channel_record;
                    dataArr[i]['channel_status'] = dataArr[i].channel_detail_obj.channel_status;
                }
                layui.use('table', function(){
                    var table = layui.table;
                    
                    //展示已知数据
                    table.render({
                        elem: '#'+targetStr+'Table'
                        ,cellMinWidth: 80
                        ,cols: [[ //标题栏
                            {field: 'CHANNEL_NAME', title: '设备名称',width:250},
                            {field: 'channel_IP', title: '设备IP'}
                            ,{field: 'build_company', title: '建设单位'}
                            ,{field: 'build_time', title: '加入时间'}
                            ,{field:'channel_record',title:'是否录像'}
                            ,{field:'roadOrg',title:'所属路口'}
                            ,{field:'channel_style',title:'相机类型'}
                            ,{field:'channel_status',title:'在离线状态'}
                        ]]
                        ,data: dataA//targetTableData
                        ,page: true //开启分页
                        //,skin: 'line' //表格风格
                        ,even: true
                        //,page: true //是否显示分页
                        //,limits: [5, 7, 10]
                        //,limit: 5 //每页默认显示的数量
                    });
                    
                });
                
            })

    //})
})();

//绑定点击事件
(function(){
    $('body').on('click','.chartModel .chartBtn',function(){
        var curChart = $(this).data('charttable');
        var tableCols = [[ //标题栏
            {field: 'col1', title: '列1', width: 75}
            ,{field: 'day1', title: '列2', width: 70}
            ,{field: 'day2', title: '列3', width: 70}
            ,{field: 'day3', title: '列4', minWidth: 70}
            ,{field: 'day4', title: '列5', minWidth: 70}
            ,{field: 'day5', title: '列6', width: 70}
            ,{field: 'day6', title: '列7', width: 70}
            ,{field: 'day7', title: '列8', width: 70}
            ]];
        var chartTableData = [
            {
                'day1':'30000',
                'day2':'40000',
                'day3':'25000',
                'day4':'29000',
                'day5':'31000',
                'day6':'32000',
                'day7':'33000'
            }
        ];
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
    }).on('click','.newDeviceT',function(){
        $(".newDeviceTAlert").show();
    }).on('click','.closeBtn',function(){
        $(this).parent().parent().hide();
        $(this).parent().parent().find('.listContent');   
    })

})();

    
    
    

//最近登录用户
(function() {
    var review_box = $('#review_box');
    var jsonData = [{
            'name': '用户1',
            'time': '2021-02-01 18:01:00',
         },
        {
            'name': '用户2',
            'time': '2021-01-01 18:01:00',
         }, {
            'name': '用户3',
            'time': '2020-12-02 18:01:00',
         }, {
            'name': '用户4',
            'time': '2020-12-24 18:01:00',
         }
    ]
    var htmlStr = '';
    jsonData.forEach(function(val, index, arr) {
        var item = '<li class="clearfix"><span class="pull_left">' + val.name + '</span> <span class="pull_right">' + val.time + '</span> </li>'
        htmlStr += item;
    })


    review_box.html('<ul id="comment1">' + htmlStr + '</ul><ul id="comment2"></ul>');

    // 滚动注释
    // function scrollTop(t) {
    //     var ul1 = document.getElementById("comment1");
    //     var ul2 = document.getElementById("comment2");
    //     var ulbox = document.getElementById("review_box");
    //     ul2.innerHTML = ul1.innerHTML;
    //     ulbox.scrollTop = 0; // 开始无滚动时设为0
    //     var timer = setInterval(rollStart, t); // 设置定时器，参数t用在这为间隔时间（单位毫秒），参数t越小，滚动速度越快
    //     // 鼠标移入div时暂停滚动
    //     ulbox.onmouseover = function () {
    //      clearInterval(timer);
    //     }
    //     // 鼠标移出div后继续滚动
    //     ulbox.onmouseout = function () {
    //      timer = setInterval(rollStart, t);
    //     }
    //    }
    //    // 开始滚动函数
    //    function rollStart() {
    //     // 上面声明的DOM对象为局部对象需要再次声明
    //     var ul1 = document.getElementById("comment1");
    //     var ul2 = document.getElementById("comment2");
    //     var ulbox = document.getElementById("review_box");
    //     // 正常滚动不断给scrollTop的值+1,当滚动高度大于列表内容高度时恢复为0
    //     if (ulbox.scrollTop >= ul1.scrollHeight) {
    //      ulbox.scrollTop = 0;
    //     } else {
    //      ulbox.scrollTop++;
    //     }
    //    }
    //     scrollTop(100);
    // 滚动注释end
    // $("body").on('click', '#sqsj li', function() {

    //     var indexT = $('#sqsj li').index($(this));

    //     var htmlStrB = "<div class='thingHeader'><div class='thingName '><span>标题： </span><span>" + jsonData[indexT].name + "</span></div><div class='thingTime'><span>时间：</span><span>" + jsonData[indexT].time + "</span></div><div class='thingOwn'><span>民警：</span><span>" + jsonData[indexT].personOwn + "</span></div><div class='thingPerson'><span>商圈负责人： </span><span>" + jsonData[indexT].person + "</span></div><div class='thingNum'><span>电话： </span><span>" + jsonData[indexT].tel + "</span></div><div class='thingConten' style='overflow:hidden;'><span style='float:left'>事件内容： </span><p style='line-height:28px;width:calc(100% - 150px);float:right;padding-right:30px'>" + jsonData[indexT].content + "</p></div></div>"

    //     $('.sqsjAlert .listContent').html("").append(htmlStrB);
    //     $(".sqsjAlert").show();


    // }).on('click', '.rightAlert h2>i', function() {
    //     $(this).parent().parent().hide()
    // })

})()