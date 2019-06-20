$(function(){
    $(".js-nav-show").hover(function(){
        $(this).children(".product-categories").show();
    },function(){
         $(this).children(".product-categories").hide();
    });
});
var main_obj = {
    page_nav: function() {
        $(".j-nav-list li").hover(function() {
            var autoHeight = $(this).find('.j-two-level dd').length * 37 + "px";
            $(this).children(".line").animate({left: '0', width: '100%'}, 200);
            $(this).find('.j-two-level').animate({height: autoHeight}, 400);
        }, function() {
            $(this).children(".line").stop().animate({left: '50%', width: '0'}, 200);
            $(this).find('.j-two-level').stop().animate({height: 0}, 200);
        });
    },
    page_wow: function() {
        if (!(/msie [6|7|8|9]/i.test(navigator.userAgent))) {
            //可以加入 data-wow-duration（动画持续时间）和 data-wow-delay（动画延迟时间）属性，如：
            wow = new WOW({
                animateClass: 'animated',
                offset: 200
            });
            wow.init();
        }
        ;
    },
    yzh_slider: function() {
        $('.js-yzh-slider').flexslider({
            animation: "fade",
            slideshowSpeed: 5000,
            directionNav: false,
            pauseOnAction: false
        });
    },
    bds_info: function() {
        window._bd_share_config = {"common": {"bdSnsKey": {}, "bdText": "", "bdMini": "2", "bdMiniList": false, "bdPic": "", "bdStyle": "0", "bdSize": "32"}, "share": {}};
        with (document)
            0[(getElementsByTagName('head')[0] || body).appendChild(createElement('script')).src = 'http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion=' + ~(-new Date() / 36e5)];
    },
    about_info: function() {
        $(".js-tab-btn .btn").click(function() {
            $(this).addClass("curr").siblings().removeClass('curr');
            var _index = $(this).index();
            $(".right-cont .js-tab-cont").eq(_index).show().siblings(".js-tab-cont").hide();
        });
        //创建和初始化地图函数：
        function initMap() {
            createMap();//创建地图
            setMapEvent();//设置地图事件
            addMapControl();//向地图添加控件
            addMarker();//向地图中添加marker
            addRemark();//向地图中添加文字标注
        }
        //创建地图函数：
        function createMap() {
            var map = new BMap.Map("dituContent");//在百度地图容器中创建一个地图
            var point = new BMap.Point(113.953356, 22.559301);//定义一个中心点坐标
            map.centerAndZoom(point, 18);//设定地图的中心点和坐标并将地图显示在地图容器中
            window.map = map;//将map变量存储在全局
        }

        //地图事件设置函数：
        function setMapEvent() {
            map.enableDragging();//启用地图拖拽事件，默认启用(可不写)
            map.enableScrollWheelZoom();//启用地图滚轮放大缩小
            map.enableDoubleClickZoom();//启用鼠标双击放大，默认启用(可不写)
            map.enableKeyboard();//启用键盘上下左右键移动地图
        }

        //地图控件添加函数：
        function addMapControl() {
            //向地图中添加缩放控件
            var ctrl_nav = new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_LEFT, type: BMAP_NAVIGATION_CONTROL_LARGE});
            map.addControl(ctrl_nav);
            //向地图中添加缩略图控件
            var ctrl_ove = new BMap.OverviewMapControl({anchor: BMAP_ANCHOR_BOTTOM_RIGHT, isOpen: 1});
            map.addControl(ctrl_ove);
            //向地图中添加比例尺控件
            var ctrl_sca = new BMap.ScaleControl({anchor: BMAP_ANCHOR_BOTTOM_LEFT});
            map.addControl(ctrl_sca);
        }

        //标注点数组
        var markerArr = [{title: "深圳市南山区科技园北区北环大道科苑路北侧清华信息港科研楼206-207", content: " &nbsp;", point: "113.954061|22.559585", isOpen: 1, icon: {w: 21, h: 21, l: 0, t: 0, x: 6, lb: 5}}];
        //创建marker
        function addMarker() {
            for (var i = 0; i < markerArr.length; i++) {
                var json = markerArr[i];
                var p0 = json.point.split("|")[0];
                var p1 = json.point.split("|")[1];
                var point = new BMap.Point(p0, p1);
                var iconImg = createIcon(json.icon);
                var marker = new BMap.Marker(point, {icon: iconImg});
                var iw = createInfoWindow(i);
                var label = new BMap.Label(json.title, {"offset": new BMap.Size(json.icon.lb - json.icon.x + 10, -20)});
                marker.setLabel(label);
                map.addOverlay(marker);
                label.setStyle({
                    borderColor: "#808080",
                    color: "#333",
                    cursor: "pointer"
                });
                (function() {
                    var index = i;
                    var _iw = createInfoWindow(i);
                    var _marker = marker;
                    _marker.addEventListener("click", function() {
                        this.openInfoWindow(_iw);
                    });
                    _iw.addEventListener("open", function() {
                        _marker.getLabel().hide();
                    })
                    _iw.addEventListener("close", function() {
                        _marker.getLabel().show();
                    })
                    label.addEventListener("click", function() {
                        _marker.openInfoWindow(_iw);
                    })
                    if (!!json.isOpen) {
                        label.hide();
                        _marker.openInfoWindow(_iw);
                    }
                })()
            }
        }
        //创建InfoWindow
        function createInfoWindow(i) {
            var json = markerArr[i];
            var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='" + json.title + "'>" + json.title + "</b><div class='iw_poi_content'>" + json.content + "</div>");
            return iw;
        }
        //创建一个Icon
        function createIcon(json) {
            var icon = new BMap.Icon("http://app.baidu.com/map/jdvop/images/us_mk_icon.png", new BMap.Size(json.w, json.h), {imageOffset: new BMap.Size(-json.l, -json.t), infoWindowOffset: new BMap.Size(json.lb + 5, 1), offset: new BMap.Size(json.x, json.h)})
            return icon;
        }
        //文字标注数组
        var lbPoints = [{point: "114.052961|22.54718", content: "我的标记"}
        ];
        //向地图中添加文字标注函数
        function addRemark() {
            for (var i = 0; i < lbPoints.length; i++) {
                var json = lbPoints[i];
                var p1 = json.point.split("|")[0];
                var p2 = json.point.split("|")[1];
                var label = new BMap.Label("<div style='padding:2px;'>" + json.content + "</div>", {point: new BMap.Point(p1, p2), offset: new BMap.Size(3, -6)});
                map.addOverlay(label);
                label.setStyle({borderColor: "#999"});
            }
        }
        initMap();//创建和初始化地图
    },
    about_com: function() {
        var strurl = window.location.href;
        var flag = strurl.substring(strurl.indexOf("flag=") + 5, strurl.length);
        var box = $(".js-nav-bar .js-tab").eq(flag);
        box.addClass("curr").siblings().removeClass("curr");
        var _index = box.index(".js-tab");
        $(".js-right-cont .js-yzhculture").eq(_index).show().siblings().hide();
        $(".js-nav-bar .js-tab").click(function() {
            $(this).addClass("curr").siblings(".js-tab").removeClass("curr");
            var _index = $(this).index(".js-tab");
            $(".js-right-cont .js-yzhculture").eq(_index).show().siblings(".js-yzhculture").hide();
        });
    },
    news_list: function() {
        /*文字移动*/
        $(".js-text-moving").hover(function() {
            $(this).stop().animate({left: '18px'}, 300)
        }, function() {
            $(this).stop().animate({left: '0px'}, 300)
        });
    },
    yzh_jqzoom: function() {
        $(".jqzoom").jqueryzoom({
            xzoom: 398,
            yzoom: 398,
            offset: 10,
            position: "right",
            preload: 1,
            lens: 1
        });
        $("#spec-list").jdMarquee({
            deriction: "left",
            width: 356,
            height: 80,
            step: 2,
            speed: 4,
            delay: 10,
            control: true,
            _front: "#spec-right",
            _back: "#spec-left"
        });
        $("#spec-list img").on("mouseover", function(){
            var src = $(this).attr("src");
            $("#spec-n1 img").eq(0).attr({
                src: src.replace("\/n5\/", "\/n1\/"),
                jqimg: src.replace("\/n5\/", "\/n0\/")
            });
        });
        
        $("#spec-list img").bind("mouseover", function() {
            var src = $(this).attr("src");
            $("#spec-n1 img").eq(0).attr({
                    src : src.replace("\/n5\/", "\/n1\/"),
                    jqimg : src.replace("\/n5\/", "\/n0\/")
            });
            $(this).parents('li').addClass("curr").siblings().removeClass("curr");
        });
    },
    yzh_topics: function(){
        $('.js-new-slider').flexslider({
            animation: "fade",
            slideshowSpeed: 10000,
            directionNav: false,
            pauseOnAction: false
        });
        $(".js-new2-slider").flexslider({
            animation: "slide",
            direction: "vertical",
            animationLoop: false,
            minItems: 2,
            maxItems: 4
        });
    }
};
main_obj.page_nav();
main_obj.bds_info();