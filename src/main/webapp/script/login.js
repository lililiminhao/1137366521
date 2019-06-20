$(function(){
    //注册按钮
    $(".js-register").on("click", function(){ 
        $(".js-dialogLogin").show();
        $(".js-loginWrap").stop(true, false).animate({top: "50%"}, 1200);
        $(".js-loginStep1").show();
    });
    //登录框 /弹出 ~关闭
    $(document).on("click",".js-btnDel",function(){//dialog-login关闭
         $(".js-loginWrap").stop(true, false).animate({top: "-50%"}, 800, function(){
            $(".js-dialogLogin").hide();
        });
    });
    /*登录/注册框tab*/
    $(document).on("click",".js-loginTab .list",function(){
        $(this).addClass("curr").siblings().removeClass("curr");
        $(".js-loginCon .js-Ncon").eq($(this).index()).removeClass('hide').siblings().addClass('hide');
    });
    //性别
    $(document).on("click",".js-tabGender .list",function(){
        $(this).addClass("curr").siblings().removeClass("curr");
    });
    //登录注册input -focus -blur 效果
    $(document).on("focus",".js-comInput",function(){
        $(this).addClass("focus");
        //fnShake(this,"left",30);
    }).on("blur",".js-comInput",function(){
        $(this).removeClass("focus");
    });
    //注册下一步
     $(document).on("click",".js-btnSubmitNext",function(){
        $(".js-loginStep1").hide();
        $('.js-perfectInfo').show();
    });
    //完善信息确认按钮
    $(document).on("click",".js-perfectInfoOk",function(){
        $(".js-loginStep1").hide();
        $('.js-perfectInfo').hide();
        $('.js-registerOk').show();
    });
    
    $("#loginForm").submit(function(e){
        e.preventDefault();
        var loginName = $(this).find('input[name="loginName"]');
        if(isEmpty(loginName.val())){
            loginName.addClass("error");
            loginName.siblings(".error-text").addClass("show");
            return false;
        }
        var password = $(this).find('input[name="password"]');
        if(isEmpty(password.val())){
            password.addClass("error");
            password.siblings(".error-text").addClass("show");
            return false;
        }
        $.post($(this).attr("action"), $(this).serialize(), function(data){
            if(data.isOk){
                window.location = data.message;
            }else{
                messageBox("error", "登录失败", data.message);
                return false;
            }
        }, "json");

    });
})
function isEmpty(temp){
    if(temp == '' || temp == 'undefined' || temp == 'null'){
        return true;
    }
    return false;
}

function fnShake(obj, attr, n, fn){
    if (obj.shake) { return;}/*防止重复抖动*/
    var iStart = css(obj, attr);//取初始位置
    var arr = [];
    var iNub = 0;
    for (var i = n; i >= 0; i -= 2){
        arr.push(-i, i);
    }
    arr.push(0);
    obj.shake = setInterval(function(){
        if (iNub >= arr.length) {
            clearInterval(obj.shake);
            obj.shake = 0;
            fn && fn();//定时器执行完毕之后去某件事
        }else{
            css(obj, attr, iStart + arr[iNub]);
            iNub++;
        }
    }, 30);
};
function css(obj, sAttr, val){
    if (arguments.length > 2){
        if (sAttr === "opacity"){
            obj.style.opacity = val / 100;
            obj.style.filter = "alpha(opacity=" + val + ")";
        }else {
            obj.style[sAttr] = val + "px";
        }
    }else{
        var iVal = obj.currentStyle ? obj.currentStyle[sAttr] : getComputedStyle(obj)[sAttr];
        if (sAttr === "opacity"){
            iVal = iVal * 100;
        }
        return parseInt(iVal);
    }
};
