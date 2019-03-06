var system = require('system');
var page = require('webpage').create();
var url = system.args[1];
page.open(url,function(status){
	console.log("status："+status);
	if(status==="success"){
		console.log("网页标题为："+page.content);
	}else{
		console.log("网页打开出错了");
	}
	phantom.exit();
});