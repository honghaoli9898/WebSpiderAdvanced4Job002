var system=require('system');
var page=require('webpage').create();
var url = system.args[1];
var injectFlag=phantom.injectJs("library/jquery-3.3.1.js");
console.log('injectFlag='+injectFlag);
page.onAlert=function(msg){
	console.log('ALERT:'+msg);
};
page.open(url,function(status){
	if(status==="success"){
		var titleValue=page.evaluate(function(){
			window.alert("2222");
			return $("div#u1").text();
		});
		console.log(titleValue);
	}else{
		console.log("error!");
	}
	phantom.exit();
});
