// var baseUrl = "http://192.168.32.4:8888";
// var baseUrl = "";
var baseUrl = "http://192.168.32.103:8080";

	
	
function showlogin(){
	if(token==""||token==undefined||token==null){
		$("#yidenglu").html("<a href='login.html' style='color:white'>登录</a>");
	}else{
		$("#yonghuming").html(instorage.loginUserName);
	}
}
	
	
	// 获取浏览器字符串
	 function getQueryStringRegExp(name)
	{
	 var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i"); 
	 if (reg.test(location.href)) return unescape(RegExp.$2.replace(/\+/g, " ")); return "";
	} 
	
	// 获取日期时间,负数为前几天,
	function FunGetDateStr(p_count) {
			 var dd = new Date();
			 dd.setDate(dd.getDate() + p_count);//获取p_count天后的日期
			 var y = dd.getFullYear();
			 var m = dd.getMonth() + 1;//获取当前月份的日期
			 if( m <10){
				 m = '0'+m;
			 }
			 var d = dd.getDate();
			 if( d <10){
				 d = '0'+d;
			 }
			 return y + "-" + m + "-" + d;
	}
	