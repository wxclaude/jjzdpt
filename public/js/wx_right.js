
var url = 'http://32.140.42.66:4000/pointsDetails';

function zbmjAll() {
    $(".zbmjAlert").show();
}

function clearInterval(){
    
}
        // // 将一个sheet转成最终的excel文件的blob对象，然后利用URL.createObjectURL下载
        function sheet2blob(sheet, sheetName) {
            sheetName = sheetName || 'sheet1';
            var workbook = {
                SheetNames: [sheetName],
                Sheets: {}
            };
            workbook.Sheets[sheetName] = sheet;
            // 生成excel的配置项
            var wopts = {
                bookType: 'xlsx', // 要生成的文件类型
                bookSST: false, // 是否生成Shared String Table，官方解释是，如果开启生成速度会下降，但在低版本IOS设备上有更好的兼容性
                type: 'binary'
            };
            var wbout = XLSX.write(workbook, wopts);
            var blob = new Blob([s2ab(wbout)], {
                type: "application/octet-stream"
            });
            // 字符串转ArrayBuffer
            function s2ab(s) {
                var buf = new ArrayBuffer(s.length);
                var view = new Uint8Array(buf);
                for (var i = 0; i != s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
                return buf;
            }
            return blob;
        }
//**
        //  * 通用的打开下载对话框方法，没有测试过具体兼容性
        //  * @param url 下载地址，也可以是一个blob对象，必选
        //  * @param NameStr 保存文件名，可选
        //  */
        function openDownloadDialog(url, NameStr) {
            if (typeof url == 'object' && url instanceof Blob) {
                url = URL.createObjectURL(url); // 创建blob地址
            }
            var todayStr = new Date().getFullYear()+"-"+(new Date().getMonth()+1)+'-'+new Date().getDate();
            var saveName;
            switch(NameStr){
                case 'total':     
                    saveName='全部点位汇总';
                    break;
                case 'zaixian':
                    saveName='在线点位汇总';
                    break;
                case 'dianjing':
                    saveName='卡警点位汇总';
                    break;
                case 'qiuji':
                    saveName='球机点位汇总';
                    break;
                case 'kakou':
                    saveName='纯卡口点位汇总';
                    break;
                case 'quanchen':
                    saveName='全程监控点位汇总';
                    break;
                case 'record':
                    saveName='已录像点位汇总';
                    break;
                case 'lixian':
                    saveName='离线点位汇总';
                    break;
                case 'butong':
                    saveName='不通点位汇总';
                    break;
                case 'xiulu':
                    saveName='修路中的点位汇总';
                    break;
                case 'weixiu':
                    saveName='维修中的点位汇总';
                    break;
                default:
                    break;
            }



            var aLink = document.createElement('a');
            aLink.href = url;
            aLink.download = (todayStr+saveName+'.xlsx') || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
            var event;
            if (window.MouseEvent) event = new MouseEvent('click');
            else {
                event = document.createEvent('MouseEvents');
                event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            }
            aLink.dispatchEvent(event);
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


//
(function(){
    $.post(url,'',function(result){
        console.log('接口返回数据：');
        console.log(result);
        //赋值右上角今年新增相机数量
        console.log(result);
        $('.ptAccess .newDeviceCount').html(result.category.thisYearPoints.length);
        // var result = {
        //     'list':[],
        //     'category':{
        //         'dianjing':[],
        //         'kakou':[],
        //         'qiuji':[],
        //         'quanchen':[],
        //         'record':[],
        //         'lixian':[],
        //         'butong':[],
        //         'xiulu':[],
        //         'weixiu':[]
        //     }
        // }
//平台点位情况
            //接入平台数量情况展示
            //丢一种类型的数据进函数，返回路口数量
            var dianjingRoads = getRoadsNum(result.category.dianjing);
            var kakouRoads = getRoadsNum(result.category.kakou);
            var qiujiRoads = getRoadsNum(result.category.qiuji);
            var quanchenRoads = getRoadsNum(result.category.quanchen);
            var jieruPointsData = {
                'key':['卡警路口数量：','卡口路口数量：','球机路口数量：','道路监控路口数量：'],
                'value':[dianjingRoads,kakouRoads,qiujiRoads,quanchenRoads],
                'channel_value':[result.category.dianjing,result.category.kakou,result.category.qiuji,result.category.quanchen],
                'target':['dianjing','kakou','qiuji','quanchen','record']
            };
            var jieruPointsHtml='';

            for(var i=0;i<jieruPointsData.key.length;i++){
                
                jieruPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">'+jieruPointsData.key[i]+'</div><div class="pointsInfoBody" data-target="'+jieruPointsData.target[i]+'"><span class="roadNumber">'+jieruPointsData.value[i]+'</span> (相机数量：<span class="pointsNum cameraNumber">'+jieruPointsData.channel_value[i].length+'</span>)</div></div>';
            };
            //补充录像数量
            jieruPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">平台录像相机数量：</div><div class="pointsInfoBody" data-target="luxiang"><span class="pointsNum">'+result.category.record.length+'</span></div></div>';
            $('.jieru-points .points-body').html(jieruPointsHtml);
            //平台点位状态情况展示
            var onlineC = result.list.length - result.category.lixian.length;
            
            var statusPointsData = {
                'key':['平台相机总数：',     '在线相机数量：',               '离线相机数量：','离线的包括：',              '修路状态相机数量：',          '维修中相机数量：'],
                'value':[result.list.length,result.category.zaixian.length,result.category.lixian.length,'',result.category.xiulu.length,result.category.weixiu.length],
                'target':['total','zaixian','lixian','lixianTitle','xiulu','weixiu']
            };
            var statusPointsHtml = '';
            for(var i=0;i<statusPointsData.key.length;i++){
                    statusPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">'+statusPointsData.key[i]+'</div><div class="pointsInfoBody" data-target="'+statusPointsData.target[i]+'"><span class="cameraNumber">'+statusPointsData.value[i]+'</span></div></div>';
            };
            $('.status-points .points-body').html(statusPointsHtml);
            //<--------------平台右下角情况展示
            //录像情况
            // var recordPointsData = {
            //     'key':['平台录像相机数量：'],
            //     'value':[result.category.record.length],
            //     'target':['record']
            // };
            // var recordPointsHtml='';
            // for(var i=0;i<recordPointsData.key.length;i++){
            //     recordPointsHtml += '<div class="pointsInfo"><div class="pointsInfoTitle">'+recordPointsData.key[i]+'</div><div class="pointsInfoBody" data-target="'+recordPointsData.target[i]+'"><span>'+recordPointsData.value[i]+'</span></div></div>';
            // };
            // $('.others-points .points-body').html(recordPointsHtml);
            //修改为展示过车量对比数据

            //------------------>右下角展示模块结束
            $('body').on('click','.pointsInfo .pointsInfoBody span.cameraNumber',function(){
                var targetStr = $(this).parent().data('target');
                
                var targetAlertHtml='<h2>设备详细情况<span style="float:right;margin-right:50px;cursor:pointer;">导出</span><i class="closeBtn"></i></h2><div class="listContent"><div id="'+targetStr+'Table"></div></div>',targetTableData;
                switch(targetStr){
                    case 'total':     
                        targetTableData=result.list;
                        break;
                    case 'zaixian':
                        targetTableData=result.category.zaixian;
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
                    case 'luxiang':
                        targetTableData=result.category.record;
                        break;
                    default:
                        break;
                }
                
                $('.pointsTableAlert').html(targetAlertHtml);
                $('.pointsTableAlert').show();
            
                
                $('.pointsTableAlert h2 span').off('click').on('click',function(){
                    
                    //1.处理数组json成为二维数组
                    var aoa =[
                        ['设备名称','设备IP','相机类型','所属路口','是否录像','在离线状态','加入时间','建设单位','建设单位联系人']
                    ];
                    targetTableData.map(function(item,index,arr){
                        var ifRec=item.channel_detail_obj.channel_record?'是':'否';
                        
                        aoa.push([item.CHANNEL_NAME,item.channel_IP,item.channel_detail_obj.channel_style,item.channel_detail_obj.roadOrg,ifRec,item.channel_detail_obj.channel_status,item.build_time,item.build_company,item.build_company_tel])
                    })
                    
                    var sheet = XLSX.utils.aoa_to_sheet(aoa);
                    openDownloadDialog(sheet2blob(sheet), targetStr);
                })
             
                console.log('总点位如下：');
                console.log(result);
                console.log('点击的类别的点位如下：');
                console.log(targetTableData);
                
                //解析成展示表格所需数据
                var FinaltableData = [];
               
                for(var i in targetTableData){
                    
                    FinaltableData.push({
                        'CHANNEL_NAME':targetTableData[i]['CHANNEL_NAME'],
                        'channel_IP':targetTableData[i]['channel_IP'],
                        'CHANNEL_CODE':targetTableData[i]['CHANNEL_CODE'],
                        'build_company':targetTableData[i]['build_company'],
                        'build_company_tel':targetTableData[i]['build_company_tel'],
                        //'build_time':targetTableData[i]['build_time'],
                        'channel_record':targetTableData[i]['channel_detail_obj'].channel_record==='1'?'是':'否',
                        'roadOrg':targetTableData[i]['channel_detail_obj']['roadOrg'],
                        'channel_style':targetTableData[i]['channel_detail_obj']['channel_style'],
                        'channel_status':targetTableData[i]['channel_detail_obj']['channel_status']
                    })
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
                            ,{field: 'CHANNEL_CODE', title: '设备编码'}
                            ,{field: 'build_company', title: '建设单位',width:90}
                            ,{field: 'build_company_tel', title: '联系方式',width:150}
                            //,{field: 'build_time', title: '加入时间'}
                            ,{field:'channel_record',title:'是否录像',width:90}
                            ,{field:'roadOrg',title:'所属路口'}
                            ,{field:'channel_style',title:'相机类型',width:90}
                            ,{field:'channel_status',title:'在离线状态',width:120}
                        ]]
                        ,data: FinaltableData
                        ,page: true //开启分页
                        //,skin: 'line' //表格风格
                        ,even: true
                        //,page: true //是否显示分页
                        //,limits: [5, 7, 10]
                        //,limit: 5 //每页默认显示的数量
                    });
                    
                });
                
            }).on('click','.newDeviceCount',function(){
                var targetStr = $(this).data('target');
                var targetAlertHtml='<h2>设备详细情况<span style="float:right;margin-right:50px;cursor:pointer;">导出</span><i class="closeBtn"></i></h2><div class="listContent"><div id="'+targetStr+'Table"></div></div>',targetTableData;
                var targetTableData = result.category.thisYearPoints;
                $('.pointsTableAlert').html(targetAlertHtml);
                $('.pointsTableAlert').show();

                $('.newDeviceCount h2 span').off('click').on('click',function(){
                    
                    //1.处理数组json成为二维数组
                    var aoa =[
                        ['设备名称','设备IP','相机类型','所属路口','是否录像','在离线状态','加入时间','建设单位','建设单位联系人']
                    ];
                    targetTableData.map(function(item,index,arr){
                        var ifRec=item.channel_detail_obj.channel_record?'是':'否';
                        
                        aoa.push([item.CHANNEL_NAME,item.channel_IP,item.channel_detail_obj.channel_style,item.channel_detail_obj.roadOrg,ifRec,item.channel_detail_obj.channel_status,item.build_time,item.build_company,item.build_company_tel])
                    })
                    
                    var sheet = XLSX.utils.aoa_to_sheet(aoa);
                    openDownloadDialog(sheet2blob(sheet), targetStr);
                })
                //解析成展示表格所需数据
                var FinalnewDeviceTableData = [];
                for(var i in targetTableData){
                    FinalnewDeviceTableData.push({
                        'CHANNEL_NAME':targetTableData[i]['CHANNEL_NAME'],
                        'channel_IP':targetTableData[i]['channel_IP'],
                        'build_company':targetTableData[i]['build_company'],
                        'build_company_tel':targetTableData[i]['build_company_tel'],
                        //'build_time':targetTableData[i]['build_time'],
                        'channel_record':targetTableData[i]['channel_detail_obj'].channel_record==='1'?'是':'否',
                        'roadOrg':targetTableData[i]['channel_detail_obj']['roadOrg'],
                        'channel_style':targetTableData[i]['channel_detail_obj']['channel_style'],
                        'channel_status':targetTableData[i]['channel_detail_obj']['channel_status']
                    })
                };
                
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
                            ,{field: 'build_company_tel', title: '联系方式'}
                            //,{field: 'build_time', title: '加入时间'}
                            ,{field:'channel_record',title:'是否录像'}
                            ,{field:'roadOrg',title:'所属路口'}
                            ,{field:'channel_style',title:'相机类型'}
                            ,{field:'channel_status',title:'在离线状态'}
                        ]]
                        ,data: FinalnewDeviceTableData
                        ,page: true //开启分页
                        //,skin: 'line' //表格风格
                        ,even: true
                        //,page: true //是否显示分页
                        //,limits: [5, 7, 10]
                        //,limit: 5 //每页默认显示的数量
                    });
                    
                });
            })

    })
})();

//绑定点击事件
(function(){
    $('body').on('click','.newDeviceT',function(){
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