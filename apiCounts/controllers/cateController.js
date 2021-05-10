var dbConfig = require('../util/dbconfig');

//获取整合后的前端设备数据
getChannels=(req,res)=>{
    //获取表所需要的字段，所有数据
    var sql1 = "SELECT b.DEVICE_CODE,b.CHANNEL_CODE,b.CHANNEL_NAME, a.channel_IP,a.build_time,b.IS_ONLINE,a.build_company,a.channel_detail FROM `veh_new_channel_info` a JOIN vsl_dev_encoder_chn b ON a.CHANNEL_ID = b.CHANNEL_CODE;";
    var sqlArr=[];
    function utc2beijing(utc_datetime) {
        // 转为正常的时间格式 年-月-日 时:分:秒
        var T_pos = utc_datetime.indexOf('T');
        var Z_pos = utc_datetime.indexOf('Z');
        var year_month_day = utc_datetime.substr(0,T_pos);
        var hour_minute_second = utc_datetime.substr(T_pos+1,Z_pos-T_pos-1);
        var new_datetime = year_month_day+" "+hour_minute_second; // 2017-03-31 08:02:06
    
        // 处理成为时间戳
        timestamp = new Date(Date.parse(new_datetime));
        timestamp = timestamp.getTime();
        timestamp = timestamp/1000;
    
        // 增加8个小时，北京时间比utc时间多八个时区
        var timestamp = timestamp+8*60*60;
    
        // 时间戳转为时间
        var beijing_datetime = new Date(parseInt(timestamp) * 1000).toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
        return beijing_datetime; // 2017-03-31 16:02:06
    } 
    
    var callBack=(err,data)=>{
        if(err){
            console.log('连接出错了');

        }else{
            var result = {
                'list':data,
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
            console.log(data);
            var i=0,dataList=result.list;
            for(var i=0;i<dataList.length;i++){
                dataList[i].build_time = utc2beijing(dataList[i].build_time).substr(0,10);
                var splitArr = dataList[i].channel_detail.split('，');
                var channelProp = dataList[i];
                channelProp['channel_detail_obj'] ={
                    'roadOrg':splitArr[0]?splitArr[0]:'未配置所属路口',
                    'channel_style':splitArr[1]?splitArr[1]:'未配置相机类型',
                    'channle_record':splitArr[2]?splitArr[2]:'录像未知',
                    'channel_status':splitArr[3]?splitArr[3]:'未配置在离线状态'
                }
                switch(splitArr[1]){
                    case '电警':
                        result.category.dianjing.push(channelProp);
                        break;
                    case '卡口':
                        result.category.kakou.push(channelProp);
                        break;
                    case '球机':
                        result.category.qiuji.push(channelProp);
                        break;
                    case '全程监控':
                        result.category.quanchen.push(channelProp);
                        break;
                    default:
                        break;
                };
                switch(splitArr[2]){
                    case '1':
                        result.category.record.push(channelProp);
                        break;
                    default:
                        break;
                }
                switch(dataList[i].IS_ONLINE){
                    case 0:
                        result.category.lixian.push(channelProp);
                        break;
                    default:
                        break;
                }
                switch(splitArr[3]){
                    case '不通':
                        result.category.butong.push(channelProp);
                        break;
                    case '修路':
                        result.category.xiulu.push(channelProp);
                        break;
                    case '维修中':
                        result.category.weixiu.push(channelProp);
                        break;
                    default:
                        break;
                }
            }
            
            res.send(result);
        }
    }
    //获取表所需要的字段，所有数据
    dbConfig.sqlConnect(sql1,sqlArr,callBack);
    
    
}

module.exports={getChannels};