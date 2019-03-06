var page = require('webpage').create();
page.open('http://www.myhope365.com',function(status){
	console.log("status："+status);
	if(status==="success"){
		console.log("网页标题为："+page.title);
	}else{
		console.log("网页打开出错了");
	}
	phantom.exit();
});