var express = require('express');
const dbconfig = require('../util/dbconfig.js');
var router = express.Router();
var dbConfig = require('../util/dbconfig.js')
var cateController = require('../controllers/cateController');
var http = require('http');
var https = require('https');
var HttpsProxyAgentBbj = require("../util/HttpsProxyAgent.js");
var HttpsProxyAgent =HttpsProxyAgentBbj.HttpsProxyAgent;
var hex_md5 = require('../public/js/MD5.js');
var data2;
var YCKRES,YKTotal,YKUsed;




process.env.NODE_TLS_REJECT_UNAUTHORIZED='0';
/* GET home page. */

router.post('/pointsDetails', function(req, res) {
  console.log('进来请求了');
  cateController.getChannels(req,res);
});

router.post('/accessYCKInfo',function(req, res){
  YCKRES = res;
  authorizeToken(callBackFn);
  
  //getYCInfo(token);
  
});

function requestAgagin(data,realm,callbackFn){
  data = JSON.parse(data);
  console.log('加密前的：'+data);
  //对data中signature进行加密，加密后替换data中
  var userName = "admin";
  var password = "wangxiang110";
  var randomKey = data.randomKey;
  
  var signature = md5Password(userName,password,randomKey,realm);
  data.signature = signature;
  data = JSON.stringify(data); 
  console.log('第二次请求的：'+data);
  //加密完成
  var opt2 = {  
    host:'32.140.42.66',  
    port:'8069',  
    method:'POST',  
    path:'/phoenix-api/accounts/authorize',  
    body:data,
    json:true,
    headers:{  
        "Content-Type": 'application/json;',  
        "Content-Length": data.length,
        "Accept":'*/*'  
    }  
  };
    console.log('第二次请求的参数：'+JSON.stringify(opt2));
  var body2 = '';  
  var req2 = https.request(opt2, function(res) {  
              console.log("response2: " + res.statusCode);  
              res.setEncoding("UTF-8");
              res.on('data',function(res2Data){  
                console.log("第二次结果："+res2Data);
                var token = JSON.parse(res2Data).token;
                callbackFn(token);
                
              }).on('end', function(){  
                  console.log("responseData"+body2);
              });  
  }).on('error', function(e) {  
      console.log("error: " + e.message);  
  })  
  req2.write(data);  
  req2.end();  
}

function getYCInfo (token) {
  console.log("token:"+token);
  var data ={
    "service":"Efs",
    "clusterId":"49cdec39244a48ff8fc46337515de63e",
    "action":[
      "summary"
    ]
  };
  data = JSON.stringify(data); 
  var body='';
  var opt = {  
    host:'32.140.42.66',  
    port:'8069',  
    method:'POST',  
    path:'/phoenix-api/service/controller/command',  
    body:data,
    json:true,
    headers:{  
        "Content-Type": 'application/json;',  
        "Content-Length": data.length,
        "X-Subject-Token":token.toString()  
    }  
  };
  console.log('第三次请求参数：'+JSON.stringify(opt));
    var req = https.request(opt, function(res) {  
      console.log("response3: " + res.statusCode);  
      res.setEncoding("UTF-8");
      res.on('data',function(res2Data){  
      
        console.log('response3Data:'+JSON.stringify(res2Data));
        var finalResult = JSON.parse(res2Data);
        var totalNodes = parseInt(finalResult.totalNodes);//集群里节点数量
        var remainCapacity = parseInt(finalResult.remainCapacity);//云存储剩余容量
        var totalCapacity = parseInt(finalResult.totalCapacity);//云存储总容量
        var onlineNodes = totalNodes - finalResult.offlineNodes;//在线节点数量
        var YCRES = {
          'totalNodes':totalNodes,
          'onlineNodes':onlineNodes,
          'usedCapacity':totalCapacity - remainCapacity,
          'totalCapacity':totalCapacity
        };
        getYKInfo(token,YCRES);
        

      }).on('end', function(){  
          console.log("responseData"+body);
      });  
  }).on('error', function(e) {  
  console.log("error: " + e.message);  
  })  
  req.write(data);  
  req.end();  
}

function getYKInfo (token,YCRES) {
  console.log("获取云库时token:"+token);
  var data ={
    "queries":[
      {"metric":"clouddb.disk.capacity.total","aggFlag":"1"},
      {"metric":"clouddb.disk.capacity.used","aggFlag":"1"}
    ]
  };
  data = JSON.stringify(data); 
  var body='';
  var opt = {  
    host:'32.140.42.66',  
    port:'8069',  
    method:'POST',  
    path:'/phoenix-api/metrics/queries',  
    body:data,
    json:true,
    headers:{  
        "Content-Type": 'application/json;',  
        "Content-Length": data.length,
        "X-Subject-Token":token.toString()  
    }  
  };
  console.log('第四次请求参数：'+JSON.stringify(opt));
    var req = https.request(opt, function(res) {  
      console.log("response4: " + res.statusCode);  
      res.setEncoding("UTF-8");
      res.on('data',function(res2Data){  
      
        console.log('response4Data:'+JSON.stringify(res2Data));
        JSON.parse(res2Data).forEach(function(val,index){
          if(val['metric']==="clouddb.disk.capacity.total"){
            YKTotal = val["values"][0]['value'];
          }else if(val['metric']==="clouddb.disk.capacity.used"){
            YKUsed = val["values"][0]['value']
          }
        });
        YCRES["YKTotal"] = Math.round(parseInt(YKTotal)/10000);
        YCRES["YKUsed"] = Math.round(parseInt(YKUsed)/10000);
        YCRES["YKpercent"] = (parseInt(YKUsed)/parseInt(YKTotal)*100).toFixed(2);
        console.log(YCRES);
        YCKRES.send(YCRES);
        
        // var finalResult = JSON.parse(res2Data);
        // var totalNodes = parseInt(finalResult.totalNodes);//集群里节点数量
        // var remainCapacity = parseInt(finalResult.remainCapacity);//云存储剩余容量
        // var totalCapacity = parseInt(finalResult.totalCapacity);//云存储总容量
        // var onlineNodes = totalNodes - finalResult.offlineNodes;//在线节点数量
        // YCRES.send({
        //   'totalNodes':totalNodes,
        //   'onlineNodes':onlineNodes,
        //   'usedCapacity':totalCapacity - remainCapacity,
        //   'totalCapacity':totalCapacity
        // })

      }).on('end', function(){  
          console.log("responseData"+body);
      });  
  }).on('error', function(e) {  
  console.log("error: " + e.message);  
  })  
  req.write(data);  
  req.end();  
}


function md5Password(userName,password,randomKey,realm){
  var temp = hex_md5(encodeURIComponent(password));
  var passwdvalue = hex_md5(userName+temp);
  var passwdtmp = hex_md5(passwdvalue);
  var encryrttedPasswd = hex_md5(userName+":"+realm+":"+passwdtmp);
  return hex_md5(encryrttedPasswd+":"+ randomKey);
}
function authorizeToken(callbackFn){
  var data = {
    "userName":"admin",
    "clientType":"web",
    "ipAddress":"32.140.42.66"
  };  
  data = JSON.stringify(data);  
  data2 = {
    "userName":"admin",
    "signature":"",
    "randomKey":"",
    "encryptType":"",
    "clientType":"web",
    "ipAddress":"32.140.42.66"
  };
  
   
  var opt = {  
      host:'32.140.42.66',  
      port:'8069',  
      method:'POST',  
      body:data,
      
      path:'/phoenix-api/accounts/authorize',  
      //agent:agent,
      headers:{  
          "Content-Type": 'application/json',  
          "Content-Length": data.length  
      }  
  };  
  
  var body = '';  
  var req1 = https.request(opt, function(res1) {  
              
              res1.on('data',function(res1Data){  
                res1Data = JSON.parse(res1Data);
                console.log('第一次结果：'+JSON.stringify(res1Data));
                data2.randomKey = res1Data.randomKey;
                data2.encryptType=res1Data.encryptType;
             
                console.log('拼接后第二次的：'+JSON.stringify(data2));
                data2 = JSON.stringify(data2);
                requestAgagin(data2,res1Data.realm,callbackFn);
                
              }).on('end', function(){  
                  console.log("responseData"+body);
              });  
  }).on('error', function(e) {  
      console.log("error: " + e.message);  
  });
  
  req1.write(data);  
  req1.end();  
}
var callBackFn = function(token){
  getYCInfo(token);
}

module.exports = router;


