/**
 * 弹窗
 * @param {type} title      标题
 * @param {type} content    提示内容
 * @param {type} url        跳转链接
 * @returns {undefined}
 * @ico {type} icoState  图标显示--> 错误==error 正确==ok 疑问==question 警告==warning
 */
function artAlert(title, content, url, type){
    var	type_def    = ['error', 'ok', 'question', 'warning'];
    var	ico     = undefined;
    if (type === undefined && url !== undefined) {
        if ($.inArray(url,type_def)!== -1) {
            type    = url;
            url     = undefined;
        }
    }
    if($.inArray(type,type_def)!== -1){
        ico = 'ico-' + type;
    }
    var contentHtml = '<div class="custom-icon">';
    if (ico !== undefined) {
        contentHtml += '<em class="'+ico+'"></em>';
    }
    contentHtml += content +'</div>';
    var d = dialog({
            title: title,
            content: contentHtml,
            okValue: '确定',
            cancel: false,
            ok: function () {
                if (url !== undefined) {
                    if ('function' === typeof url) {
                        url();
                    } else if (url === 'reload') {
                        window.iframe.location.reload();
                    } else if (url === 'back') {
                        history.go(-1);
                    } else {
                        window.iframe.location  = url;
                    }
                }
                return true;
            }
        });
    d.width(400);
    d.showModal();
}

function artProgress(content) {
    if (content === '[close]') {
        top.$('div._mark').remove();
    } else {
        var cnt = top.$('div._mark');
        if (cnt.size() > 0) {
            cnt.find('span.dialog_cnt').text(content);
        } else {
            var html = '<div class="_mark"><p><img src="/jdvop/images/admin/loader.gif"/><br/><span class="dialog_cnt">' + content + '</span></p></div>';
            top.$('body').append(html);
        }
    }
}

/**
 * 
 * @param {type} title      标题
 * @param {type} content    提示内容
 * @param {type} fun        点击按钮执行函数
 * @returns {undefined}
 * @ico {type} icoState  图标显示--> 错误==error 正确==ok 疑问==question 警告==warning
 */
function artConfirm(title, content, fun, type){
    var	type_def    = ['error', 'ok', 'question', 'warning'];
    var	ico     = undefined;
    if($.inArray(type,type_def)!==-1){
         ico = 'ico-' + type;
    }
    var contentHtml = '<div class="custom-icon">';
    if (ico !== undefined) {
        contentHtml += '<em class="'+ico+'"></em>';
    }
    contentHtml += content +'</div>';
    var d = dialog({
        title: title,
        content:contentHtml,
        okValue: '确定',
        cancelValue: '取消',
        ok: function () {
            if(isFunction(fun)){
                fun(true);
            }
        },
        cancel: function () {
            if(isFunction(fun)){
                fun(false);
            }
        }
    });
    d.width(400);
    d.showModal();
}
/**
 * 悬浮提示
 * @param {type} 元素ID
 * @param {type} 提示内容
 * @param {type} 对齐方式 "top left", "top", "top right","right top","right","right bottom",
 * "bottom right","bottom","bottom left","left bottom","left", "left top"
 * @returns {undefined}
 */
function artMessage(id, content, align){
    var d = dialog({
        align: align,
        content: "<font color='red'>" + content + "</font>",
        quickClose: true
    });
    d.show(window.iframe.document.getElementById(id));
}

//**************************************************公用函数**************************************************//

function isFunction(fun){
    return fun != undefined && typeof(fun) === 'function';
}

function isNull(str){
    return str == undefined || str === '' || str.length <= 0;
}

function isNotNull(str){
    return !isNull(str);
}



