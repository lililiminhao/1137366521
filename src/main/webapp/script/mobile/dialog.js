(function ($) {
    $.fn.Dialog = function (options) {
        var defaults = {
            autoOpen: false,
            title: '标题',
            tipsText: "",       /* 提示内容 */
            model: 'twokey',   /* twokey确定取消弹窗，onekey单关闭按钮弹窗   */
            flag : true, 
            okEvent: function(){ 
                $.noop(); 
            }, 
            cancelEvent: function(){ 
                $.noop(); 
            }
        };
        var options = $.extend(defaults, options);
        var _move = false;
        var _x,_y;  //为移动使用
        this.each(function (){
            var win = $(window);
            var doc = $(document);
            var o = options;
            var model = o.model;
            var button;
            var html;
            if(options.flag){
            	html = '<section class="dialog js-dialog">';
            	html +='<p class="title">'+o.title+'</p>';
            	html +='<p class="text">'+o.tipsText+'</p>';
            	html +='<div class="ok webkitbox-h"></div>';
            	html +='</div>';
            }else{
            	html = '<section class="dialog 2">';
            	html +='<p class="title">'+o.title+'</p>';
            	html +='<p class="text">'+o.tipsText+'</p>';
            	html +='<div class="ok webkitbox-h"></div>';
            	html +='</div>';
            }
        	
            /*初始化弹窗*/
            _init = function(){
                var layout_mark = '<p class="layout-mask js-mark"></p>';
                $("body").append(layout_mark).append(html);
                /*弹窗按钮初始化*/
                if(model=="twokey"){
                    button = '<p class="flex1 js-ok">确定</p> <p class="flex1 js-close">取消</p>';
                }else if(model=="onekey"){
                    button = '<p class="flex1 js-ok">确定</p>';
                }
                $(".js-dialog .ok").append(button);
            };
           /*关闭事件*/
           function _close(){
                $(".js-dialog").detach();
                $(".js-mark").detach();
            }
            /*按钮事件绑定*/
            _init();
            $(".js-ok").on("click",o.okEvent);/*确定按钮*/
            $(".js-ok").on("click",_close);
            $(".js-close").on("click", o.cancelEvent); /*取消按钮*/
            $(".js-close").on("click", _close); 
        });
    };
})(jQuery);

