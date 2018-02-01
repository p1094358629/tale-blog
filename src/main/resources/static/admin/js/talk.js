var tale = new $.tale();
/**
 * 保存文章
 * @param status
 */
function subTalk(status) {
	console.log("coming in ");
    var contents = $('#talkForm input[name=contents]').val();
    if (contents == '') {
        tale.alertWarn('请输入内容');
        return;
    }
    $("#content-editor").val(contents);
    $("#talkForm #status").val(status);
    var params = $("#talkForm").serialize();
    console.log(params);
    var url = $('#talkForm #tid').val() != '' ? '/admin/talk/modify' : '/admin/talk/publish';
    tale.post({
        url: url,
        data: params,
        success: function (result) {
            if (result && result.success) {
                tale.alertOk({
                    text: '说说保存成功',
                    then: function () {
                        setTimeout(function () {
                            window.location.href = '/admin/talk';
                        }, 500);
                    }
                });
            } else {
                tale.alertError(result.msg || '说说失败');
            }
        }
    });
}
function delPost(tid) {
    tale.alertConfirm({
        title:'确定删除该条说说吗?',
        then: function () {
           tale.post({
               url : '/admin/talk/delete',
               data: {tid: tid},
               success: function (result) {
                   if(result && result.success){
                       tale.alertOkAndReload('说说删除成功');
                   } else {
                       tale.alertError(result.msg || '说说删除失败');
                   }
               }
           });
       }
    });
}