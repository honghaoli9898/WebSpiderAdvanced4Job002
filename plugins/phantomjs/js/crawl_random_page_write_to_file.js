var system = require('system');
var url = system.args[1];
var fs = require('fs');
var saveFileName = system.args[2];
var page = require('webpage').create();
page.open(url,function(status){
	if(status==="success"){
		console.log("status="+status);
		fs.write(saveFileName,page.content,'w');
	}else{
		console.log("error!");
	}
	phantom.exit();
});
