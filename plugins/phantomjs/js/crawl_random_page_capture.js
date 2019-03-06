var system = require('system');
var url = system.args[1];
var page_name=system.args[2];
var page = require('webpage').create();
page.open(url,function(status){
	if(status==="success"){
		page.render(page_name);
	}else{
		console.log("error!");
	}
	phantom.exit();
});