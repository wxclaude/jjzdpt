var dbConfig = require('../util/dbconfig');

//获取整合后的前端设备数据
getChannels=(req,res)=>{
    //获取表所需要的字段，所有数据
    var sql1 = "SELECT b.DEVICE_CODE,b.CHANNEL_CODE,b.CHANNEL_NAME, a.channel_IP,a.build_time,b.IS_ONLINE,a.build_company,a.channel_detail FROM `veh_new_channel_info` a JOIN vsl_dev_encoder_chn b ON a.CHANNEL_ID = b.CHANNEL_CODE;";
    var sqlArr=[];
    
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
            var i=0,data=result.list;
            for(var i=0;i<data.length;i++){
                var splitArr = data[i].channel_detail.split('，');
                switch(splitArr[0]){
                    case '电警':
                        result.category.dianjing.push(data[i]);
                        break;
                    case '卡口':
                        result.category.kakou.push(data[i]);
                        break;
                    case '球机':
                        result.category.qiuji.push(data[i]);
                        break;
                    case '全程监控':
                        result.category.quanchen.push(data[i]);
                        break;
                    default:
                        break;
                };
                switch(splitArr[1]){
                    case '录像':
                        result.category.record.push(data[i]);
                        break;
                    default:
                        break;
                }
                switch(data[i].IS_ONLINE){
                    case 0:
                        result.category.lixian.push(data[i]);
                        break;
                    default:
                        break;
                }
                switch(splitArr[2]){
                    case '不通':
                        result.category.butong.push(data[i]);
                        break;
                    case '修路':
                        result.category.xiulu.push(data[i]);
                        break;
                    case '维修中':
                        result.category.weixiu.push(data[i]);
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