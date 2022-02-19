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
var HttpUtil = require('../public/js/HttpUtil.js');

process.env.NODE_TLS_REJECT_UNAUTHORIZED='0';
/* GET home page. */

router.post('/pointsDetails', function(req, res) {
  console.log('进来请求了');
  cateController.getChannels(req,res);
});

router.post('/accessYCInfo',function(req, res){
  var param1 = {
    "userName":"admin",
    "clientType":"web",
    "ipAddress":"32.140.42.66"
  };  
  HttpUtil.post("32.140.42.66","8069","/phoenix-api/accounts/authorize",param1,'*/*',"application/json",successA,errorA);
  function successA(result){
      console.log(JSON.stringify(result));
  }
  data2 = {
    "userName":"admin",
    "signature":"",
    "randomKey":"",
    "encryptType":"",
    "clientType":"web",
    "ipAddress":"32.140.42.66"
  };

  
});

function md5Password(userName,password,randomKey,realm){
  var temp = hex_md5(encodeURIComponent(password));
  var passwdvalue = hex_md5(userName+temp);
  var passwdtmp = hex_md5(passwdvalue);
  var encryrttedPasswd = hex_md5(userName+":"+realm+":"+passwdtmp);
  return hex_md5(encryrttedPasswd+":"+ randomKey);
}



module.exports = router;


