const mysql = require('mysql');
module.exports = {
    //数据库配置
    config1:{
        host:'32.140.42.66',
        port:'3306',
        user:'root',
        password:'dahuacloud',
        database:'nextdb2'
    },
    config2:{
        host:'32.140.42.69',
        port:'3306',
        user:'root',
        password:'dahuacloud',
        database:'vehicle'
    },
    //链接数据库，使用mysql连接池的方式
    //连接池对象
    sqlConnect:function(sql,sqlArr,callBack,config){
        
        var configInner = config==='T9100'?this.config1:this.config2;
        var pool = mysql.createPool(configInner);
        
        pool.getConnection((err,conn)=>{
            //console.log(configInner);
            if(err){
            
                console.log('连接失败！');
                console.log(err);
                return;
            }
            
            //事件驱动回调
            conn.query(sql,sqlArr,callBack);
        
            //释放连接
            conn.release();
           
        });
        
    }
}