var _activity_id;
var _image_url;
var _integral;
var _user_id;
var _title;
var _link;
var _desc;
var _data_box;
$(function(){
    //立即转发按钮
    $("span.js-forward").on("touchstart",function(e) {
        e.preventDefault();
        var thiz = $(this);
        var isWechat = $('input[name="isWechat"]').val();
        if(isWechat === "true") {
            _data_box    = thiz.attr("rel");
            _activity_id = $("#" + _data_box).find('input[name="activityId"]').val();
            _image_url   = $("#" + _data_box).find('input[name="imageUrl"]').val();
            _integral    = $("#" + _data_box).find('input[name="integral"]').val();
            _user_id     = $("#" + _data_box).find('input[name="userId"]').val();
            _title       = $("#" + _data_box).find('input[name="title"]').val();
            _link        = $("#" + _data_box).find('input[name="link"]').val();
            _desc        = $("#" + _data_box).find('input[name="desc"]').val();
            $('.js-markLayout').show();

            //获取“分享到朋友圈”按钮点击状态及自定义分享内容接口
            wx.onMenuShareTimeline({
                title: _title,              // 分享标题
                link: _link,                // 分享链接
                imgUrl: _image_url,         // 分享图标
                success: function () { 
                    $.post("/jdvop/forward/success.php", {aid: _activity_id, uid: _user_id}, function(data) {
                        if (data.isOk) {
                            msgBox("温馨提示", "恭喜你获得" + "<i class='red'>" + _integral + "</i>积分", false, function(){
                                window.location.reload();
                            });
                        } else {
                            msgBox("温馨提示", data.message, false, function(){
                                $('.js-markLayout').hide();
                            });
                            return false;
                        }
                    }, "json");
                },
                cancel: function () {
                    $('.js-markLayout').hide();
                    alert("分享已取消");
                }
            });

            //获取“分享给朋友”按钮点击状态及自定义分享内容接口
            wx.onMenuShareAppMessage({
                title: _title,             // 分享标题
                desc: _desc,               // 分享描述
                link: _link,               // 分享链接
                imgUrl: _image_url,        // 分享图标
                success: function () { 
                   $.post("/jdvop/forward/success.php", {aid: _activity_id, uid: _user_id}, function(data) {
                        if (data.isOk) {
                            msgBox("温馨提示", "恭喜你获得" + "<i class='red'>" + _integral + "</i>积分", false, function(){
                                window.location.reload();
                            });
                        } else {
                            msgBox("温馨提示", data.message, false, function(){
                                $('.js-markLayout').hide();
                            });
                            return false;
                        }
                    }, "json");
                },
                cancel: function () {
                    $('.js-markLayout').hide();
                    alert("分享已取消");
                }
            });

            //获取“分享到QQ”按钮点击状态及自定义分享内容接口
            wx.onMenuShareQQ({
                title: _title,             // 分享标题
                desc: _desc,               // 分享描述
                link: _link,               // 分享链接
                imgUrl: _image_url,        // 分享图标
                success: function () { 
                   $.post("/jdvop/forward/success.php", {aid: _activity_id, uid: _user_id}, function(data) {
                        if (data.isOk) {
                            msgBox("温馨提示", "恭喜你获得" + "<i class='red'>" + _integral + "</i>积分", false, function(){
                                window.location.reload();
                            });
                        } else {
                            msgBox("温馨提示", data.message, false, function(){
                                $('.js-markLayout').hide();
                            });
                            return false;
                        }
                    }, "json");
                },
                cancel: function () { 
                    $('.js-markLayout').hide();
                    alert("分享已取消");
                }
            });

            //获取“分享到腾讯微博”按钮点击状态及自定义分享内容接口
            wx.onMenuShareWeibo({
                title: _title,             // 分享标题
                desc: _desc,               // 分享描述
                link: _link,               // 分享链接
                imgUrl: _image_url,        // 分享图标
                success: function () { 
                    $.post("/jdvop/forward/success.php", {aid: _activity_id, uid: _user_id}, function(data) {
                        if (data.isOk) {
                            msgBox("温馨提示", "恭喜你获得" + "<i class='red'>" + _integral + "</i>积分", false, function(){
                                window.location.reload();
                            });
                        } else {
                            msgBox("温馨提示", data.message, false, function(){
                                $('.js-markLayout').hide();
                            });
                            return false;
                        }
                    }, "json");
                },
                cancel: function () { 
                    $('.js-markLayout').hide();
                    alert("分享已取消");
                }
            });

            //获取“分享到QQ空间”按钮点击状态及自定义分享内容接口
            wx.onMenuShareQZone({
                title: _title,             // 分享标题
                desc: _desc,               // 分享描述
                link: _link,               // 分享链接
                imgUrl: _image_url,        // 分享图标
                success: function () { 
                    $.post("/jdvop/forward/success.php", {aid: _activity_id, uid: _user_id}, function(data) {
                        if (data.isOk) {
                            msgBox("温馨提示", "恭喜你获得" + "<i class='red'>" + _integral + "</i>积分", false, function(){
                                window.location.reload();
                            });
                        } else {
                            msgBox("温馨提示", data.message, false, function(){
                                $('.js-markLayout').hide();
                            });
                            return false;
                        }
                    }, "json");
                },
                cancel: function () { 
                    $('.js-markLayout').hide();
                    alert("分享已取消");
                }
            });
        } else {
            alert("请使用微信浏览器进行转发");
        }
    });
    
    $('.js-markLayout').on("touchstart",function(){
        $(this).hide();
    });
});