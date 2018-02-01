/*获取所有文章数之和*/
/*var total = document.getElementsByName("category");
console.log("total:"+total.length);
for(var i=0;i<total.length;i++){
	console.log(total[i].value);
}*/
var total=0;
var one = document.getElementsByName("category");
for(var i=0;i<one.length;i++){
	total+=parseInt(one[i].value);
}
$('.getTotalArts').html('('+total+")");