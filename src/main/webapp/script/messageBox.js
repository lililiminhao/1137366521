var     _func;
var     _whithCancel = true; 

/**
 * 消息弹窗
 * @param {type} type           ico:success, error, warning, question
 * @param {type} title          标题
 * @param {type} content        内容
 * @param {type} whithCancel    是否显示取消按钮
 * @param {type} func           点击确定时执行的函数
 * @returns {messageBox}
 */
function messageBox(type, title, content, whithCancel, func)
{
    if($("#mark_close_btfy").size() > 0){
        return false;
    }
    
    this._func = func;
    if(hasParam(whithCancel))
    {
        _whithCancel = whithCancel;
    }
    
    var messageHtml  = '<div class="mark_error_box" id="mark_close_btfy"></div>';
        messageHtml += '<div class="landing" id="message_box_btfy">';
        messageHtml += '<div class="header">';
        messageHtml += '<a href="#" class="close message_close_btfy" title="关闭">×</a>';
        messageHtml += '<h3>'+ title +'</h3>';
        messageHtml += '</div>';
        messageHtml += '<div class="minddle-landing">';
        if(type === "error")
        {
           messageHtml += '<i class="error"></i>'; 
        } else if(type === "success"){
           messageHtml += '<i class="success"></i>'; 
        } else if(type === "warning"){
           messageHtml += '<i class="warning"></i>';  
        } else if(type === "question"){
           messageHtml += '<i class="question"></i>'; 
        }else{
           messageHtml += '<i class="success"></i>';  
        }
        messageHtml += '<em>'+ content +'</em>';
        messageHtml += '<div class="landing-btn">';
        messageHtml += '<span class="but_ok" id="message_ok_btfy">ok</span>';
        if(_whithCancel)
        {
            messageHtml += '<span class="now-bg message_close_btfy">返回</span>';
        }
        messageHtml += '</div>';
        messageHtml += '</div>';
        messageHtml += '<div class="foot"></div>';
        messageHtml += '</div>';
        $('body').append(messageHtml);
}

$(".message_close_btfy").live("click", function(e){
    e.preventDefault();
    remove();
});

$("#message_ok_btfy").live("click",function(e){
    e.preventDefault();
    remove();
    if(hasParam(_func)){
        _func();
    }
});

function remove(){
    $("#mark_close_btfy").remove();
    $("#message_box_btfy").remove();
}

function hasParam(temp){
    if(temp !== null && temp !== undefined && temp !== ""){
        return true;
    }
    return false;
}