// 获取当前时间日日日日
	var date = new Date();
	Y = date.getFullYear() + '-';
	M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
	D = (date.getDate()< 10 ? '0'+(date.getDate()) : date.getDate());
	var nowdate = Y+M+D;
	// console.log(nowdate);
	// 获取部门code
	// http://192.168.32.4:8888        
	// 定义地址
	
	// 定义农历时间
	var CalendarData=new Array(20);
	var madd=new Array(12);
	var TheDate=new Date();
	var tgString="甲乙丙丁戊己庚辛壬癸";
	var dzString="子丑寅卯辰巳午未申酉戌亥";
	var numString="一二三四五六七八九十";
	var monString="正二三四五六七八九十冬腊";
	var weekString="日一二三四五六";
	var sx="鼠牛虎兔龙蛇马羊猴鸡狗猪";
	var cYear;
	var cMonth;
	var cDay;
	var cHour;
	var cDateString;
	var DateString;
	var Browser=navigator.appName;

	function init()
	{
		CalendarData[0]=0x41A95;
		CalendarData[1]=0xD4A;
		CalendarData[2]=0xDA5;
		CalendarData[3]=0x20B55;
		CalendarData[4]=0x56A;
		CalendarData[5]=0x7155B;
		CalendarData[6]=0x25D;
		CalendarData[7]=0x92D;
		CalendarData[8]=0x5192B;
		CalendarData[9]=0xA95;
		CalendarData[10]=0xB4A;
		CalendarData[11]=0x416AA;
		CalendarData[12]=0xAD5;
		CalendarData[13]=0x90AB5;
		CalendarData[14]=0x4BA;
		CalendarData[15]=0xA5B;
		CalendarData[16]=0x60A57;
		CalendarData[17]=0x52B;
		CalendarData[18]=0xA93;
		CalendarData[19]=0x40E95;
		madd[0]=0;
		madd[1]=31;
		madd[2]=59;
		madd[3]=90;
		madd[4]=120;
		madd[5]=151;
		madd[6]=181;
		madd[7]=212;
		madd[8]=243;
		madd[9]=273;
		madd[10]=304;
		madd[11]=334;
	}

	function GetBit(m,n)
	{
		return (m>>n)&1;
	}

	function e2c()
	{
		var total,m,n,k;
		var isEnd=false;
		var tmp=TheDate.getYear();
		if (tmp<1900) tmp+=1900;
		total=(tmp-2001)*365
			+Math.floor((tmp-2001)/4)
			+madd[TheDate.getMonth()]
			+TheDate.getDate()
			-23;
		if (TheDate.getYear()%4==0&&TheDate.getMonth()>1)
			total++;
		for(m=0;;m++)
		{
			k=(CalendarData[m]<0xfff)?11:12;
			for(n=k;n>=0;n--)
			{
				if(total<=29+GetBit(CalendarData[m],n))
				{
					isEnd=true;
					break;
				}
				total=total-29-GetBit(CalendarData[m],n);
			}
			if(isEnd)break;
		}
		cYear=2001 + m;
		cMonth=k-n+1;
		cDay=total;
		if(k==12)
		{
			if(cMonth==Math.floor(CalendarData[m]/0x10000)+1)
				cMonth=1-cMonth;
			if(cMonth>Math.floor(CalendarData[m]/0x10000)+1)
				cMonth--;
		}
		cHour=Math.floor((TheDate.getHours()+3)/2);
	}

	function GetcDateString()
	{ var tmp="";
		tmp+=tgString.charAt((cYear-4)%10); //年干
		tmp+=dzString.charAt((cYear-4)%12); //年支
		tmp+="年(";
		tmp+=sx.charAt((cYear-4)%12);
		tmp+=") ";
		if(cMonth<1)
		{
			tmp+="闰";
			tmp+=monString.charAt(-cMonth-1);
		}
		else
			tmp+=monString.charAt(cMonth-1);
		tmp+="月";
		tmp+=(cDay<11)?"初":((cDay<20)?"十":((cDay<30)?"廿":"卅"));
		if(cDay%10!=0||cDay==10)
			tmp+=numString.charAt((cDay-1)%10);
		tmp+=" ";
		cDateString=tmp;
		return tmp;
	}

	function GetDateString()
	{
		var tmp="";
		var t1=TheDate.getYear();
		if (t1<1900)t1+=1900;
		tmp+=t1
			+"年"
			+(TheDate.getMonth()+1)+"月"
			+TheDate.getDate()+"日 "
			+" 星期"+weekString.charAt(TheDate.getDay());
		DateString=tmp;
		return tmp;
	}

	init();
	e2c();
	GetDateString();
	GetcDateString();
	$("#nongli").html(DateString+"[农历]"+cDateString);
	
	
	
	
	
	
	
	
	// 退出
	// var instorage = window.localstorage;
	var instorage = window.sessionStorage;
	var token = instorage.token;
	var roles = instorage.roles;
	var deptId = instorage.deptId;
	var deptCode = instorage.deptCode.substr(0,8);
	
	$("#yonghuming").html(instorage.loginUserName);
	$("#out").click(function(){
		console.log(token);
		$.ajax({
		    url:baseUrl+"/sys/logout",
		    type:"post",
		    cache:false,
			beforeSend: function (XMLHttpRequest) {
			   XMLHttpRequest.setRequestHeader("token", token);
			},
		    success:function(data) {
				if(data.code==0){
					instorage.clear();
					window.location = 'login.html';
				}else {
				    layer.msg(data.msg);
				}
		    },
		    error : function (res) {
		        instorage.clear();
		        window.location = 'login.html';
		    }
		})
		
	})
	

	
	
	
	
	// 检查token
	
	function testToken(){
		var token = instorage.getItem("token");
		if(token==""||token==undefined||token==null){
			layer.msg('您还没有登陆',{time:2000}, function () {
				window.location = 'login.html';
			});
		}
	}
	testToken();
	
	
	
	// 获取字符串
	function getQueryString(name) {
	    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
	    var r = window.location.search.substr(1).match(reg);
	    if (r != null) {
	        return unescape(r[2]);
	    }
	    return null;
	}
	
	
	
	
	// 判断非空
	var form = layui.form;
	form.verify({
	    otherReq: function(value,item){
	      var $ = layui.$;
	      var verifyName=$(item).attr('name')
	      , verifyType=$(item).attr('type')
	      ,formElem=$(item).parents('.layui-form')//获取当前所在的form元素，如果存在的话
			,verifyElem=formElem.find('input[name='+verifyName+']')//获取需要校验的元素
			,isTrue= verifyElem.is(':checked')//是否命中校验
			,focusElem = verifyElem.next().find('i.layui-icon');//焦点元素
			if(!isTrue || !value){
	        //定位焦点
	        focusElem.css(verifyType=='radio'?{"color":"#FF5722"}:{"border-color":"#FF5722"});
	        //对非输入框设置焦点
	        focusElem.first().attr("tabIndex","1").css("outline","0").blur(function() {
	            focusElem.css(verifyType=='radio'?{"color":""}:{"border-color":""});
	         }).focus();
	        return '必填项不能为空';
	}
	    }
	  });


	var Role0026 = 'ROLE0026';//网络线路管理
	var Role0027 = 'ROLE0027';//网络线路查看
	
	
	function checkRole(roles,targetRole) {

        if (roles == null || roles == undefined || roles=='') {
            return false;
        }
		
		var roleArray = roles.split(",");
        var result = false;

		for(var i = 0;i<roleArray.length;i++){
			if(roleArray[i] == targetRole){
				result = true;
				break;
			}
		}
		// console.log("result=" + result);
		return result;

	}

		// 弹窗方法开始
		var isinter;
		var millisec = 25;//定时器间隔执行时间/毫秒
		var xflo = 0; //漂浮层x坐标
		var yflo = 0; //漂浮层y坐标
		var yistop = false;
		var xisleft = true;
		function floatadfun(){
			kwidth = $(window).width();//可视区域宽度
			kheight = $(window).height();//可视区域高度
 
			divwidth = $('#floatdivids').width();//div漂浮层宽度
			divheight = $('#floatdivids').height();//div漂浮层高度
 
			hstop = jQuery(window).scrollTop();//滚动条距离顶部的高度
			wsleft = jQuery(window).scrollLeft();//滚动条距离最左边的距离
 
			offwidth = (kwidth-divwidth);//div偏移的宽度
			offheight = (kheight-divheight);//div偏移的高度
 
			if (!yistop) {
				yflo++;
				if (yflo >= offheight) {
					yistop = true;
				}
			} else {
				yflo--;
				if (yflo <= 0) {
					yistop = false;
				}
			}
 
			if (xisleft) {
				xflo++;
				if (xflo >= offwidth) {
					xisleft = false;
				}
			} else {
				xflo--;
				if (xflo <= 0) {
					xisleft = true;
				}
			}
 
			$('#floatdivids').css({'top':yflo+hstop,'left':xflo+wsleft}); /* 如果使用固定定位，请把加上滚动条的距离去掉即可 */
		}
		
		$(function () {
			isinter = setInterval("floatadfun()",millisec);
			$('#floatdivids').mouseover(function () {
				clearInterval(isinter);
			}).mouseout(function () {
				isinter = setInterval("floatadfun()",millisec);
			});
			$('#ClickRemoveFlo').click(function () {
				$(this).parents("#floatdivids").slideUp(500,function(){
					clearInterval(isinter);
					$(this).remove();
				});
			});
		});
		
		// 弹窗方法结束
		
		
		// 获取浏览器字符串
		 function getQueryStringRegExp(name)
		{
		 var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i"); 
		 if (reg.test(location.href)) return unescape(RegExp.$2.replace(/\+/g, " ")); return "";
		}