var http = require("http");
var urlUtil = require('url');
var file = require("file");
var querystring = require('querystring');
var HttpUtil = {
    //get�ύurl������html����
    get : function(url,success,error){
        http.get(url,function(res){
            var result = "";
            res.setEncoding("UTF-8");
            res.on("data",function(data){
                result += data;
            });
            res.on('error',error);
            res.on('end',function(){
                success(result);
            });
        }).on('error',this.requestError);
    },
    post : function(hostname,port,path,body,acceptType,contentType,success,error){
        var bodyString = "";
        if(body!=null && contentType == "application/json"){
            bodyString = JSON.stringify(body);
        }
        else if(body!=null && contentType == "application/x-www-form-urlencoded"){
            bodyString = querystring.stringify(body);
        }
        var opts = {
            hostname : hostname,
            port : port,
            path : path,
            method: 'post',
            headers : {
                'Accept':acceptType,
                'Content-Type':contentType,
                'Content-Length':bodyString.length
            }
        }
 
        var req = https.request(opts,function(res){
            
            var result = "";
            res.setEncoding("UTF-8");
            res.on("data",function(data){
                result += data;
            });
            res.on('error',error);
            res.on('end',function(){
                success(result);
            });
 
        });
        req.on('error',this.requestError);
        file.writeInFile(req);
        req.write(bodyString);
        req.end();
    },
    //�ύ����������������html����
    postAndReturnHtml : function(url,body,success,error){
        var urlConfig = urlUtil.parse(url);
        var contentType = "application/x-www-form-urlencoded";
        var acceptType = "text/html";
        this.post(urlConfig.hostname,urlConfig.port,urlConfig.path,body,acceptType,contentType,success,this.responseError);
    },
    //get�ύurl������������json����
    getAndReturnJson : function(url,success,error){
        this.get(url,function(data){
            var data = JSON.parse(data);
            success(data);
        },this.responseError(error));
    },
    //�ύjson������������json
    postAndReturnJson : function(url,body,success,error){
        var contentType = "application/json";
        var acceptType = "application/json";
        var urlConfig = urlUtil.parse(url);
        this.post(urlConfig.hostname,urlConfig.port,urlConfig.path,body,acceptType,contentType,function(data){
            var data = JSON.parse(data);
            success(data);
        },this.responseError(error));
    },
    requestError : function(error){
        console.log("����ʧ��--"+error.message);
    },
    responseError : function(error){
        return  error || function(e){
            console.log("��Ӧʧ��--"+e.message);
        };
    }
}
 var ABC = [
     {
         "metric":"clouddb.disk.capacity.total",
         "values":[
             {"value":2811735696,"timestamp":0,"tags":{}}
            ]
    },
    {
        "metric":"clouddb.disk.capacity.used",
        "values":[
            {"value":2167762840,"timestamp":0,"tags":{}}
        ]
    },
    {
        "metric":"clouddb.table.record.total",
        "values":[
            {"value":8560750,"timestamp":1634627619,"tags":{"tablename":"multi.P_DPRECORD"}},
            {"value":35456897,"timestamp":1634627619,"tags":{"tablename":"multi.P_DPRECORD_IMG"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"multi.P_OFFLINE_VEHICLE_RECORD"}},
            {"value":501204659,"timestamp":1634627619,"tags":{"tablename":"multi.P_PAS_ANALYZE"}},
            {"value":641854250,"timestamp":1634627619,"tags":{"tablename":"multi.P_PICRECORD"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"multi.P_PICRECORD_IMG"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"multi.P_SURVEY_ALARM"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"multi.P_VEHICLE_ALARM"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"multi.P_VEHICLE_RECORD"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"traffic.P_TRAFFIC_EVENT"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"traffic.P_TRAFFIC_EVENT_IMG"}},
            {"value":5,"timestamp":1634627619,"tags":{"tablename":"traffic.P_TRAFFIC_FLOW"}},
            {"value":1197,"timestamp":1634627619,"tags":{"tablename":"traffic.P_TRAFFIC_FLOW_RESULT"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"traffic.P_TRAFFIC_HIGH_ILLEGAL"}},
            {"value":0,"timestamp":1634627619,"tags":{"tablename":"traffic.P_TRAFFIC_HIGH_ILLEGAL_IMG"}}
        ]
    }
];

 module.exports = HttpUtil;