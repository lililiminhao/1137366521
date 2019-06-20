var main_obj={
    page_init:function(){
        /*计算宽高*/
        $('#menuCotent').height($(window).height() - 60);
        $('#layoutRight .a-iframe').height($(window).height()-60);
        main_obj.page_scroll_init();
    },
    page_scroll_init:function(){
        $('#menuCotent').jScrollPane();
    }
};
$(function(){
    $(document).on("click","#menuCotent li .js-title",function(){
        if($(this).siblings("ol").length>0){
            if($(this).siblings("ol").css("display") == 'none'){	
                main_obj.page_scroll_init();
                $(this).siblings("ol").slideDown();
                $(this).parent(".level-1").addClass('curr').siblings().removeClass('curr').find("ol").slideUp();
            }else{  
                main_obj.page_scroll_init();
                $(this).parent(".level-1").removeClass('curr');
                $(this).siblings("ol").slideUp();
            }
            main_obj.page_scroll_init();
        }else{
            main_obj.page_scroll_init();
            $(this).parent(".level-1").addClass('curr').siblings().removeClass('curr').find("ol").slideUp();
        }
    });
//    main_obj.page_scroll_init();
    //系统设置
    $(".js-selectbox1 .s-inp").click(function(e){
       if(!$(this).hasClass("curr")){
           $(this).siblings().show();
           $(this).addClass("curr");
       }else{
          $(this).siblings().hide();
          $(this).removeClass("curr"); 
       }
       return false;
    });
    $(".js-selectbox1 dd").click(function(){
       $(".js-selectbox1 .js-sOption").hide();
       $(".js-selectbox1 .s-inp").removeClass("curr"); 
    }); 
    $(".js-selectbox1").mouseleave(function(){
        $(".js-selectbox1 .js-sOption").hide();
        $(".js-selectbox1 .s-inp").removeClass("curr");
    });
    
    //模拟下拉框
    $(".js-dropDown").click(function() {
        var x = ($(this).offset().left);
        var y = ($(this).offset().top + 26);
        if (!$(this).hasClass("curr")){
            $(this).addClass("curr");
            $(this).next(".js-sOption").show(0, function(){
                $(this).css({
                    "top": y + "px",
                    "left": x + "px"
                });
            });
        } else {
            $(this).removeClass("curr");
            $(this).next(".js-sOption").hide();
        }
    });
    $(".js-sOption").parents(".js-option-parents").mouseleave(function(){
        $(this).find(".js-dropDown").removeClass("curr");
        $(this).find(".js-sOption").hide();
    });
    $(".js-sOption").click(function(){
        $(this).hide();
    });
    $(window).scroll(function() {
        var vtop = $(document).scrollTop();
        if (vtop > 200) {
            $(".mainNav").addClass("curr");
        } else if (vtop === 0) {
            $(".mainNav").removeClass("curr");
        }
    });
   //提示帮助
    $(document).on("mouseenter", ".js-topTip", function() {
        var y = $(this).siblings(".js-topTip-title").outerHeight();
        $(this).siblings(".js-topTip-title").css({
            "visibility": "visible",
            "top": -y - 4 + "px"
        });
    });
    $(document).on("mouseleave", ".js-topTip", function() {
        $(this).siblings(".js-topTip-title").css({
            "visibility": "hidden"
        });
    });
    /* 超链接文字提示 */
    var x = 10;
    var y = 20;
    $(document).on("mouseover", "a.tooltip", function(e) {
        this.myTitle = this.title;
        this.title = "";
        var tooltip = "<div id='tooltip'>" + this.myTitle + "</div>"; 
        $("body").append(tooltip);	
        $("#tooltip").css({
            "top": (e.pageY + y) + "px",
            "left": (e.pageX + x) + "px"
        }).show("fast");	  //设置x坐标和y坐标，并且显示
    }); 
     $(document).on("mouseout", "a.tooltip", function(){
        this.title = this.myTitle;
        $("#tooltip").remove();   //移除 
     });
    $(document).on("mousemove", "a.tooltip", function(e){
         $("#tooltip").css({
            "top": (e.pageY + y) + "px",
            "left": (e.pageX + x) + "px"
        });
     });
    //工具下载
    $(document).on("click",".js-lmfTool",function(){
        alert(1);
    });
    //全选    
    $('#select_all').on('click', function(e){
        var thiz = $(this);
        if (thiz.is(':checked')) {  
            $('input[type="checkbox"].select-item').attr('checked', 'checked');
            //$('input[type="checkbox"].select-item').parents("tr").addClass("bgCurr");
        } else {
            $('input[type="checkbox"].select-item').removeAttr('checked');
            //$('input[type="checkbox"].select-item').parents("tr").removeClass("bgCurr");
        }
    });
    $('input[type="checkbox"].select-item').on('click', function(e){
        var thiz = $(this);
        if (!thiz.is(':checked')) {
            $('#select_all').removeAttr('checked');
        }
    });
    //SEO     
    var FIRST_SEG_FUNC = function(str){
        var pos = str.indexOf(',');
        if (pos === -1) {
            pos = str.indexOf('、');
        }
        if (pos === -1) {
            pos = str.indexOf('，');
        }
        if (pos === -1) {
            pos = str.indexOf('。');
        }
        if (pos === -1) {
            pos = str.indexOf('.');
        }
        if (pos === -1) {
            pos = str.indexOf('/');
        }
        return pos;
    };
    var INPUT_HANDLER = function(){
        var thiz = $(this);
        var v = $.trim(thiz.val());
        var objName = thiz.attr('object-name');
        while (true) {
            var pos = FIRST_SEG_FUNC(v);
            if (pos === -1) {
                break;
            } else {
                var seg = v.substring(0, pos);
                if (seg !== '') {
                    var html = "<label class='multi-word-item mr10 pr20'><input type='hidden' name='" + objName + "' value='" + seg + "' />" + seg + "<span class='ico-del js-del' title='删除'></span></label>";
                    thiz.before(html);
                }
                v   = $.trim(v.substring(pos + 1));
                thiz.val(v);
            }
        }
    };
    if ($.browser.msie) {
        $('input.js-muliti-word-input').on('propertychange', INPUT_HANDLER);
    } else {
        $('input.js-muliti-word-input').on('input', INPUT_HANDLER);
    }
    $('input.js-muliti-word-input').on('blur', function(){
        var thiz = $(this);
        var v = $.trim(thiz.val());
        if (v !== '') {
            var objName = thiz.attr('object-name');
            var html = "<label class='multi-word-item mr10 pr20'><input type='hidden' name='" + objName + "' value='" + v + "' />" + v + "<span class='ico-del js-del' title='删除'></span></label>";
            thiz.before(html);
            thiz.val('');
        }
    });
    $(document).on("click", '.multi-word-item .js-del', function() {
         $(this).parent(".multi-word-item").remove();
    });
    $(document).on('click', '.ajax-request', function(e){
        e.preventDefault();
        var thiz = $(this);
        var queryMethod = $.post;
        var mtdCfg = thiz.attr('method');
        if (mtdCfg !== undefined && $.trim(mtdCfg).toUpperCase() === 'GET') {
            queryMethod = $.get;
        }
        var okMsg = thiz.attr('ok-message');
        var requestHandler = function() {
            queryMethod(thiz.attr('href'), function(d){
                if (d.isOk||(d.code&&d.code==1)){
                    if (okMsg === 'reload') {
                        location.reload();
                    } else if (okMsg === 'back') {
                        history.go(-1);
                    } else if (okMsg !== undefined) {
                        top.artAlert('操作成功', okMsg, 'ok');
                    }
                } else {
                    top.artAlert('操作失败', d.message, 'error');
                }
            }, 'json');
        };
        var cfm = thiz.attr('cfm-message');
        if (cfm !== undefined) {
            top.artConfirm('警告', cfm, function(v){
                if (v) {
                    requestHandler();
                }
            }, 'question');
        } else {
            requestHandler();
        }
    });
    var isCtrl = function(code){
        return code == 88 || code == 8 || code == 0 || code == 13 || code == 17;
    };
    var isNumber = function(code) {
        if(code < 48) return false;
        if(code > 57) return false;
        return true;
    };
    var isLower = function(code){
        if(code < 97)	return false;
        if(code > 122)	return false;
        return true;
    };
    var isAlpha = function(code) {
	if(code < 65) return false;
	if(code > 122) return false;
	if(code > 90 && code < 97) return false;
	return true;
    };
    var isAlphanumber = function(code) {
	if(isAlpha(code))	return true;
	if(isNumber(code))	return true;
	return false;
    };
    $(document).on('keypress', 'input.filter-input', function(e){
        var thiz = $(this);
        var code = e.which;
        if (isCtrl(code))   return;
        var rule = thiz.attr('filter-rule').toLowerCase();
        if (rule === 'isalphanumber' || rule === 'is_alphanumber') {
            if (isAlphanumber(code))    return;
        } else if (rule === 'isnumber' || rule === 'is_number') {
            if (isNumber(code)) return;
        } else if (rule === 'isalpha' || rule === 'is_alpha') {
            if (isAlpha(code))  return;
        } else if (rule === 'islower' || rule === 'is_lower') {
            if (isLower(code))  return;
        }
        var except = thiz.attr('filter-except');
        if (except !== undefined) {
            for (var i = 0; i < except.length; ++ i) {
                if (code === except.charCodeAt(i))  return;
            }
        }
        e.preventDefault();
    });
    $(document).on('click', '.submit-filter-get', function(e){
        var fm = $(this).parents('form');
        var data = [];
        fm.find('input').each(function(){
            var t = $(this);
            if (t.attr('type') === 'checkbox' || t.attr('type') === 'radio') {
                if (!t.is(':checked'))  return;
            }
            var n = t.attr('name');
            var v = t.val();
            var rule = t.attr('valid-rule');
            if (rule === undefined || rule === '') {
                data.push(n + '=' + v);
            } else if (rule === 'number') {
                var vv = parseFloat(v);
                if (!isNaN(vv)) {
                    data.push(n + '=' + vv);
                }
            } else if (rule === 'noempty') {
                var vv = $.trim(v);
                if (vv !== '') {
                    data.push(n + '=' + vv);
                }
            } else {
                console.log('不支持的验证类型' + rule);
                data.push(n + '=' + v);
            }
        });
        fm.find('select').each(function(){
            var t = $(this);
            var n = t.attr('name');
            var v = t.val();
            if (v !== '_') {
                data.push(n + '=' + v);
            }
        });
        var action = fm.attr('action');
        if (data.length == 0) {
            location    = action;
        } else {
            location    = action + '?' + data.join('&');
        }
    });
});
window.onresize = function() {
    $(".js-dropDown").each(function() {
        var x = ($(this).offset().left);
        var y = ($(this).offset().top + 26);
        $(this).next(".js-sOption").css({
            "top": y + "px",
            "left": x + "px"
        });
    });
};

