var global_obj = {
    check_form: function(obj) {
        var flag = false;
        obj.each(function() {
            if ($(this).val() == '') {
                $(this).css('border', '1px solid red');
                flag == false && ($(this).focus());
                flag = true;
            } else {
                $(this).removeAttr('style');
            }
        });
        return flag;
    },
    map_init: function() {
        //一键导航
        var myAddress = $('input[name=address]').val();
        var destPoint = new BMap.Point($('input[name=primaryLng]').val(), $('input[name=primaryLat]').val());
        var map = new BMap.Map('map');
        map.centerAndZoom(new BMap.Point(destPoint.lng, destPoint.lat), 20);
        map.enableScrollWheelZoom();
        map.addControl(new BMap.NavigationControl());
        var marker = new BMap.Marker(destPoint);
        map.addOverlay(marker);
        map.addEventListener('click', function(e) {
            destPoint = e.point;
            set_primary_input();
            map.clearOverlays();
            map.addOverlay(new BMap.Marker(destPoint));
        });
        var ac = new BMap.Autocomplete({'input': 'address', 'location': map});
        ac.addEventListener('onhighlight', function(e) {
            ac.setInputValue(e.toitem.value.business);
        });
        ac.setInputValue(myAddress);
        ac.addEventListener('onconfirm', function(e) {//鼠标点击下拉列表后的事件
            var _value = e.item.value;
            myAddress = _value.business;
            ac.setInputValue(myAddress);
            map.clearOverlays();    //清除地图上所有覆盖物
            local = new BMap.LocalSearch(map, {renderOptions: {map: map}}); //智能搜索
            local.setMarkersSetCallback(markersCallback);
            local.search(myAddress);
        });
        var markersCallback = function(posi) {
            $('#Primary').attr('disabled', false);
            if (posi.length == 0) {
                alert('定位失败，请重新输入详细地址或直接点击地图选择地点！');
                return false;
            }
            for (var i = 0; i < posi.length; i++) {
                if (i == 0) {
                    destPoint = posi[0].point;
                    set_primary_input();
                }
                posi[i].marker.addEventListener('click', function(data) {
                    destPoint = data.target.getPosition(0);
                });
            }
        }

        var set_primary_input = function() {
            $('input').filter('[name=primaryLng]').val(destPoint.lng).end().filter('[name=primaryLat]').val(destPoint.lat);
        };
        $('input[name=address]').keyup(function(event) {
            if (event.which == 13) {
                $('#Primary').click();
            }
        });
        $('#Primary').click(function() {
            if (global_obj.check_form($('input[name=address]'))) {
                return false;
            };
            $(this).attr('disabled', true);
            local = new BMap.LocalSearch(map, {renderOptions: {map: map}}); //智能搜索
            local.setMarkersSetCallback(markersCallback);
            local.search($('input[name=address]').val());
            return false;
        });
    },
    lbs_init: function(){
        //一键导航上传图片
        global_obj.map_init();
        $('#lbs_form').submit(function() {
            if (global_obj.check_form($('*[notnull]'))) {
                return false;
            };
            $('#lbs_form input:submit').attr('disabled', true);
            return true;
        });
    }
};