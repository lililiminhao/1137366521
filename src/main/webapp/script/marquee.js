//**************************
var marquee_obj={
    page_textMove:function(){
         var winlist = function(){
            $.get("/jdvop/activity/lottery/winningList.php",{id: $("[name='id']").val()}, function(data){
                $("#boxMove ul li").remove();
               $.each(data.message, function (i, item) {
                   var winloginName = item.websiteUser.loginName;
                   if(winloginName.length>3){
                       winloginName =winloginName.substr(0,3)+'****'; 
                   }else{
                       winloginName = winloginName +'*****';
                   }
                   var h = "<li><p class='tl'>"+winloginName+"</p><p class='tr'>"+ item.awardName +"</p></li>";
                    $("#boxMove ul").append(h);
               });
            },'json');
        };
        setInterval(winlist, 8000);
        //中奖名单文字滚动
        var oBoxMove=document.getElementById("boxMove");
        var oUl=oBoxMove.getElementsByTagName("ul")[0];
        var oLi=oBoxMove.getElementsByTagName("li");
        var seep=-1;
        var _move= null;
        function textMove(){
            if(oUl.offsetTop<-oUl.offsetHeight/2){
                oUl.style.top=0;
            };
            if(oUl.offsetTop>0){
                oUl.style.top=-oUl.offsetHeight/2+'px';
            };
            oUl.style.top=oUl.offsetTop+seep+'px';
        };
        _move=setInterval(textMove,100);
        if(oLi.length<=4){ 
            clearInterval(_move);
        }else if(oLi.length>4){
            oUl.innerHTML+=oUl.innerHTML; 
            oUl.style.width=oLi[0].offsetHeight*oLi.length+24+"px";  
        };
    },
    page_marquee:function(){
        var beginD =Date.parse($("#beginTime").val().replace(/-/g, "/"));
        var expireD =Date.parse($("#expireTime").val().replace(/-/g, "/"));
        var sysD = Date.parse(new Date().format("yyyy-MM-dd hh:mm:ss"));
        //如果活动未开始 弹出提示
        if(sysD < beginD){
            $('.js-notStarted').show();
        };
         //如果活动已结束 弹出提示
        if(sysD > expireD){
            $('.js-actEnd').show();
        };
        var stopNum;
        var win;
        var message;
        var image;
        var awardType;
        var logId;
        var flag_starting = false;
        var index = 0, //当前亮区位置
            prevIndex = 0, //前一位置
            Speed = 300, //初始速度
            Time, //定义对象
            Time2,
            arr = GetSide(3, 3), //初始化数组 
            EndIndex = 0, //决定在哪一格变慢
            cycle = 0, //转动圈数  
            EndCycle = 0, //计算圈数
            flag = false, //结束转动标志 
            quick = 0; //加速
            $("#start_lottery_draw").click(function(){
                //如果活动未开始 弹出提示
                if(sysD < beginD){
                    $('.js-notStarted').show();
                    $('.js--act-mark').show();
                    return;
                }
                 //如果活动已结束 弹出提示
                if(sysD > expireD){
                    $('.js-actEnd').show();
                    $('.js--act-mark').show();
                    return;
                }
                //是否正在抽奖中
                if(flag_starting){
                    return false;
                }else{
                    flag_starting = true;
                }
                 //获取抽奖信息
                 $.post("/jdvop/activity/lottery.php", {id: $("[name='id']").val()}, function(data){
                     if(data.isOk){
                        message = data.message.MSG;
                        image = data.message.IMAGE;
                        stopNum = data.message.NUM;//最后将停在此数上
                        win     = data.message.WIN;//是否中奖
                        awardType = data.message.TYPE;
                        logId = data.message.LOGID;
                        cycle = 0;
                        flag = false;
                        EndIndex = Math.floor(Math.random() * 8);
                        EndCycle = 1;
                        Time = setInterval(Star, Speed);
                        $("#start_lottery_draw").addClass("disable");
                     }else{
                        if(data.message.nologin){
                            location = "/jdvop/login.php?retUrl="+data.message.retUrl;
                        }else{
                            if(data.message == 'noIntegral'){
                                 $('.js-noIntegral').show();
                                 $('.js--act-mark').show();
                            }
                            if(data.message == 'overlimit'){
                                $('.js-actOver').show();
                                $('.js--act-mark').show();
                            }
                            if(data.message == 'noExist'){
                                $('.js-actEnd').show();
                                $('.js--act-mark').show();
                            }
                            flag_starting = false;
                            return;
                        }
                     }
                 },'json');
            });
            function Star(){
                //改变色块
                var gameContentBox = $("#gameContent");
                var currentTd = $('#gameTable tr:eq('+ arr[index][0] +') td:nth-child('+ (arr[index][1]+1) +')');
                var point_x = $(currentTd).offset().left - gameContentBox.offset().left + 1;
                var point_y = $(currentTd).offset().top - gameContentBox.offset().top;
                $("#shade_box").css({"left":point_x+11, "top":point_y+9});
                if ((navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i))){
                    $("#shade_box").css({"left":point_x+5, "top":point_y+5});
                }else {
                    $("#shade_box").css({"left":point_x+11, "top":point_y+9});
                }
                if (index > 0) {
                    prevIndex = index - 1;
                } else {
                    prevIndex = arr.length - 1;
                };
                index++;
                quick++;
                if (index >= arr.length){
                    index = 0;
                    cycle++;
                };
                //跑马灯变速
                if (flag == false){
                    //走五格开始加速
                    if (quick == 5){
                        clearInterval(Time);
                        Speed = 50;
                        Time = setInterval(Star, Speed);
                    };
                    //跑N圈减速
                    if (cycle == EndCycle +2 && index == EndIndex){
                        clearInterval(Time);
                        Speed = 300;
                        flag = true;       //触发结束
                        Time = setInterval(Star, Speed);
                    };
                };
                if (flag == true && (index == stopNum || (stopNum == 8 && index ==0))) {
                    $("#start_lottery_draw").removeClass("disable");
                    quick = 0;
                    clearInterval(Time);
                    flag_starting = false;
                    if(win && win !== ''){
                        $(".contentOk p i").empty();
                        $(".contentOk p i").append(message);//在中奖弹出款显示中奖名称
                        $(".contentOk img").attr("src",image);//在中奖弹出框显示中奖图片
                        if(awardType == "CUSTOM" || awardType == "EXIST"){
                            $(".contentOk").remove("input");
                            $("#entityBtn").remove();
                            $(".contentOk").find(".text2").html("");
                            $(".contentOk").append("<input type='hidden' class='js-logid' value='"+logId+"'/><span  id='drawButton' class='btn-go'>领取礼物</span>");
                          
                        }
                        //中奖msg
                        $(".js-dialogOk").show();
                        $('.js--act-mark').show();
                    }else{
                        //没中奖msg
                        $('.js-dialogError').show();
                        $('.js--act-mark').show();
                    };
                };
            };
            //初始化数组
            function GetSide(m, n) {
                var arr = [];
                for (var i = 0; i < m; i++) {
                    arr.push([]);
                    for (var j = 0; j < n; j++) {
                        arr[i][j] = i * n + j;
                    };
                };
                //获取数组最外圈
                var resultArr = [];
                var tempX = 0,
                    tempY = 0,
                    direction = "Along",
                    count = 0;
                while (tempX >= 0 && tempX < n && tempY >= 0 && tempY < m && count < m * n) {
                    count++;
                    resultArr.push([tempY, tempX]);
                    if (direction == "Along") {
                        if (tempX == n - 1)
                            tempY++;
                        else
                            tempX++;
                        if (tempX == n - 1 && tempY == m - 1)
                            direction = "Inverse"
                    }
                    else {
                        if (tempX == 0)
                            tempY--;
                        else
                            tempX--;
                        if (tempX == 0 && tempY == 0)
                            break;
                    }
                }
                return resultArr;
            };
            //删除按钮
            $('.js-btnDel').click(function(){
                $(this).parents(".dialog-act").hide();
                $('.js--act-mark').hide();
            });
            $("#drawButton").live("click",function(){
                var logId = $(this).siblings(".js-logid").val();
                location = "/jdvop/activity/lottery/order.php?id="+logId;
            });
    }
};


