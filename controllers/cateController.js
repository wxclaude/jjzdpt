var dbConfig = require('../util/dbconfig');
var T9100Result,vehicleResult,resultDa;
//获取整合后的前端设备数据
getChannels=(req,res)=>{
    //获取表所需要的字段，所有数据
    //var sql1 = "SELECT b.DEVICE_CODE,b.CHANNEL_CODE,b.CHANNEL_NAME, a.channel_IP,DATE_FORMAT(a.build_time,'%Y-%m-%d') as build_time,c.status,a.build_company,a.channel_detail,a.program_version FROM `veh_new_channel_info` a JOIN vsl_dev_encoder_chn b ON a.CHANNEL_ID = b.CHANNEL_CODE join vsl_device_status c on b.CHANNEL_CODE=c.CODE;";
    //获取T9100库数据
    var sqlT9100 = "SELECT b.DEVICE_CODE,b.CHANNEL_CODE,b.CHANNEL_NAME,b.GPS_X,a.STATUS FROM `vsl_device_status` a JOIN vsl_dev_encoder_chn b ON a.CODE = b.CHANNEL_CODE";
    //var sqlT9100="SELECT COUNT(*) FROM veh_new_channel_info";
    var sqlVehicle = "SELECT device_id,channel_id,channel_IP,DATE_FORMAT(build_time,'%Y-%m-%d') as build_time,build_company,channel_detail,program_version FROM veh_new_channel_info";
    var sqlArr=[];
    
    var callBack=(err,data)=>{
        if(err){
            console.log('查询T9100连接出错');
        }else{
            T9100Result=data;
            dbConfig.sqlConnect(sqlVehicle,sqlArr,callBack1,'vehicle');
        }
    };
    var callBack1=(err,data)=>{
        if(err){
            console.log('查询车辆服务连接出错了');

        }else{
            //对T9100的结果和车辆服务的结果做合并处理
            vehicleResult = data;
             var resultPre = {};
            for(var i=0;i<T9100Result.length;i++){
                if(T9100Result[i]['GPS_X']){
                    resultPre[T9100Result[i]['CHANNEL_CODE']]= T9100Result[i];
                }
                
            };
            for(var j=0;j<vehicleResult.length;j++){
                var vehicleCode = vehicleResult[j]['channel_id'];
                if(resultPre.hasOwnProperty(vehicleCode)){
                    var curVehicleInnerKeyArr = Object.keys(vehicleResult[j]);
                    for(var k=0;k<curVehicleInnerKeyArr.length;k++){                  
                        resultPre[vehicleCode][curVehicleInnerKeyArr[k]]=vehicleResult[j][curVehicleInnerKeyArr[k]]; 
                    }
                }
            }
            
            //res.send(resultPre);
            var result = {
                'list':Object.values(resultPre),
                'category':{
                    'dianjing':[],
                    'kakou':[],
                    'qiuji':[],
                    'quanchen':[],
                    'quanjing':[],
                    'feiji':[],
                    'record':[],
                    'zaixian':[],
                    'lixian':[],
                    'butong':[],
                    'xiulu':[],
                    'weixiu':[],
                    'thisYearPoints':[]
                }
            }
            
            var i=0;
            for(var i=0;i<result.list.length;i++){
                var splitArr=[];
                //0.是否配置了对应的detail参数
                if(result.list[i].program_version){
                    splitArr= result.list[i].program_version.split('，');
                };
                
                    //赋初始值
                    result.list[i]['channel_detail_obj'] ={
                        'roadOrg':splitArr[0]?splitArr[0]:'未配置所属路口',
                        'channel_style':splitArr[1]?splitArr[1]:'未配置相机类型',
                        'channel_record':splitArr[2]?splitArr[2]:'录像未知',
                        'channel_status':splitArr[3]?splitArr[3]:'不通'
                    }

                //1.确定维保单位
                
                    switch(result.list[i]['build_company']){
                        case '国脉':
                            result.list[i]['build_company_tel']='国13813800000';
                            break;
                        case '创新':
                            result.list[i]['build_company_tel']='创13813800001';
                            break;
                        case '润通':
                            result.list[i]['build_company_tel']='润13813800002';
                            break;
                        default:
                            result.list[i]['build_company_tel']='未配置电话';
                            break;
                    };

                    //2.确定在离线状态
                    switch(result.list[i].STATUS){
                        case 0:
                            result.category.lixian.push(result.list[i]);//如果STATUS为0，那全放在离线里
                            //然后判断离线的原因，只有修路和待维修（忘了配置离线原因的，放在待维修里面）
                            if(splitArr[3]){
                                switch(splitArr[3]){
                                    // case '不通':
                                    //     result.category.butong.push(result.list[i]);
                                    //     result.category.weixiu.push(result.list[i]);
                                    //     break;
                                    case '修路':
                                        result.category.xiulu.push(result.list[i]);
                                        break;
                                    
                                    default:
                                        result.category.butong.push(result.list[i]);
                                        result.category.weixiu.push(result.list[i]);
                                        break;
                                }
                            }else{
                                result.category.butong.push(result.list[i]);
                                result.category.weixiu.push(result.list[i]);
                            }
                            
                            break;
                        case 1:
                            result.list[i]['channel_detail_obj']['channel_status'] = '在线';
                            result.category.zaixian.push(result.list[i]);
                            break;
                        default:
                            break;
                    }




                    if(result.list[i].build_time){
                        var todayDate = new Date(new Date().getFullYear()+'-01-01');
                        var listDate = new Date(result.list[i].build_time);
                        if(listDate.getTime() >todayDate.getTime()){
                            result.category.thisYearPoints.push(result.list[i]);
                        }
                    }
                
                    if(splitArr[1]){
                        switch(splitArr[1]){
                            case '电警':
                                result.category.dianjing.push(result.list[i]);
                                break;
                            case '卡警':
                                result.category.dianjing.push(result.list[i]);
                                break;
                            case '卡口':
                                result.category.dianjing.push(result.list[i]);
                                break;
                            case '纯卡口':
                                result.category.kakou.push(result.list[i]);
                                break;
                            case '球机':
                                result.category.qiuji.push(result.list[i]);
                                break;
                            case '全程监控':
                                result.category.quanchen.push(result.list[i]);
                                break;
                            case '全景':
                                result.category.quanjing.push(result.list[i]);
                                break;
                            case '非机动车道':
                                result.category.feiji.push(result.list[i]);
                                break;
                            case '非机':
                            result.category.feiji.push(result.list[i]);
                            break;
                            default:
                                break;
                        };
                    };
                    if(splitArr[2]){
                        switch(splitArr[2]){
                            case '1':
                                result.category.record.push(result.list[i]);
                                break;
                            default:
                                break;
                        }
                    }
                
                
                
            }
            
            res.send(result);
        }
    }
    //获取表所需要的字段，所有数据
    dbConfig.sqlConnect(sqlT9100,sqlArr,callBack,'T9100');
    
    
}

module.exports={getChannels};