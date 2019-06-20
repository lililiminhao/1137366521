var     _func;
var     _whithCancel = true; 
/**
 * 消息弹窗
 * @param {type} title          标题
 * @param {type} content        内容
 * @param {type} whithCancel    是否显示取消按钮 默认=true 
 * @param {type} func           点击确定时执行的函数
 */
function msgBox( title, content, whithCancel, func){
   // alert(arguments.length)
    if($(".js-layout-mask").size() > 0){return false;};
    this._func = func;
    if(hasParam(whithCancel)){
        _whithCancel = whithCancel;
    };
var msgH = '<p class="layout-mask js-layoutMask"></p>';
    msgH += '<section class="dialog-j js-dialog">';
    msgH += '<p class="title">'+title+'</p>';
    msgH += '<p class="text">'+content+'</p>';
    msgH += '<div class="ok">';
    msgH += '<i class="js-btnOk btn">确定</i>';
    if(_whithCancel){
        msgH += '<i class="js-btncancel btn">取消</i>';
    };
    msgH += '</div>';
    msgH += '</section>';
    $('body').append(msgH);
};
$(document).on('touchstart','.js-dialog .js-btncancel',function(e){ //取消按钮
     e.preventDefault();
     remove();
});
$(document).on('touchstart','.js-dialog .js-btnOk',function(e){ //确定按钮
    e.preventDefault();
    remove();
    if(hasParam(_func)){
        _func();
    }
});
function remove(){
    $(".js-layoutMask,.js-dialog").remove();
}
function hasParam(temp){
    if(temp !== null && temp !== undefined && temp !== ""){
        return true;
    };
    return false;
};