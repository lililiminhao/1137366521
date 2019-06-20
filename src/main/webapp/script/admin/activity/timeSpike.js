var nextFrom_modify = {
    init: function (options) {
        $(".del-li").click(function () {
            var _this = $(this);
            top.artConfirm("警告", "您确定要删除该产品吗？", function (v) {
                if (v) {
                    $(_this).parents("li").remove();
                }
            });
        });
        $("#next_bu").click(function () {//点击下一步
            var valid = true;
            $(".edit-zhekou").each(
                    function (i) {
                        var fn = $("input", this);
                        var displayDiscountPrice = fn.eq(0).val();
                        var displayOriginalPrice = fn.eq(1).val();
                        var limitNum = fn.eq(2).val();
                        var saleNum = fn.eq(3).val();
                        if (validNull(displayDiscountPrice, "折扣价") ||
                                validNull(displayOriginalPrice, "展示原价") ||
                                validNull(limitNum, "限量总数")) {
                            valid = false;
                            return false;
                        } else if (validPositiveNumber(displayDiscountPrice, "折扣价") ||
                                validPositiveNumber(displayOriginalPrice, "展示原价") ||
                                validNumber(limitNum, "限量总数")) {
                            valid = false;
                            return false;
                        } else if (parseFloat(displayDiscountPrice) > parseFloat(displayOriginalPrice)) {
                            top.artAlert('温馨提示', '折扣价不能大于展示原价', 'error');
                            valid = false;
                            return false;
                        } else if (parseInt(limitNum) < parseInt(saleNum)) {//如果设置的数量小于 已售的数量 错：
                            top.artAlert('温馨提示', '限制数量不能小于已售数量', 'error');
                            valid = false;
                            return false;
                        }

                    }
            );
            if (!valid)
                return;
            var options = $.extend({}, options);
            nextFrom_modify.addActivity(options.content);
        });
    },
    addActivity: function (content) {
        dialog({
            width: 500,
            title: '创建秒杀活动',
            content: content,
            okValue: '确定',
            ok: function () {
                var activityName = $('input[name="activityName"]').val();
                if (activityName == ' ') {
                    top.artAlert('温馨提示', '请输入活动名称', 'error');
                    return false;
                }
                var startTime = $('input[name="startTime"]').val();

                if (startTime == '') {
                    top.artAlert('温馨提示', '请输入开始时间', 'error');
                    return false;
                }
                var endTime = $('input[name="endTime"]').val();

                if (endTime == '') {
                    top.artAlert('温馨提示', '请输入结束时间', 'error');
                    return false;
                }
                ;
                $.ajax({
                    type: "POST",
                    url: "/jdvop/admin/activity/timeSpike/createActivity.php",
                    contentType : "application/json",
                    data: nextFrom_modity.collectionData(),
                    async: false,
                    success: function (data) {
                        var temp = JSON.parse(data);
                        if (temp.isOk) {
                            top.artAlert('温馨提示', "数据保存成功！", "/jdvop/admin/activity/selectTimeSpike.php", 'ok');
                        } else {
                            top.artAlert('温馨提示', temp.message, "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                        }
                    },
                    error: function (data, xhr, type) {
                        top.artAlert('温馨提示', "系统异常", "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                        $(".btn-orange").attr("disabled", false);
                    }
                }, 'json');
            },
            cancelValue: '取消',
            cancel: function () {
            }

        }).showModal();
    }
    ,
    collectionData: function () {
        var startTime = $('input[name="startTime"]').val();
        var endTime = $('input[name="endTime"]').val();
        var data = {};
        var productNum;
        data.activityName = $('input[name="activityName"]').val();
        productNum = document.getElementsByName("totalCunt[]").length;
        data.productNum = productNum;
        data.isDelete = 0;
        data.startTime = new Date(startTime);
        var time = startTime.substring(0, startTime.indexOf(" "));
        data.endTime = new Date(time + " " + endTime + ":00");
        data.id = $('input[name="activityId"]').val(); //存储的活动id
        var p;
        var fn;
        var _arr = [];
        $(".edit-zhekou").each(
                function (i) {
                    p = {};
                    fn = $("input", this);
                    p["displayDiscountPrice"] = fn.eq(0).val(); //折扣价
                    p["displayOriginalPrice"] = fn.eq(1).val(); //原价
                    p["discountRate"] = fn.eq(0).val() / fn.eq(1).val(); //打几折
                    p["limitNum"] = fn.eq(2).val();
                    p["productId"] = fn.eq(4).val();
                    p["remaindAmount"] = parseInt(fn.eq(2).val()) - parseInt(fn.eq(3).val()); //产品剩余数量=限制数量-已售数量
                    _arr.push(p);
                }
        );
        data.productList = $.extend(true, [], _arr);
        return JSON.stringify(data);
    }
};
var nextFrom_add = {
    init: function () {
        $("#next_bu").click(function () {//点击下一步
            var valid = true;
            $(".edit-zhekou").each(
                    function (i) {
                        var fn = $("input", this);
                        var displayDiscountPrice = fn.eq(0).val();
                        var displayOriginalPrice = fn.eq(1).val();
                        var limitNum = fn.eq(2).val();
                        if (validNull(displayDiscountPrice, "折扣价") ||
                                validNull(displayOriginalPrice, "展示原价") ||
                                validNull(limitNum, "限量总数")) {
                            valid = false;
                            return false;
                        } else if (validPositiveNumber(displayDiscountPrice, "折扣价") ||
                                validPositiveNumber(displayOriginalPrice, "展示原价") ||
                                validNumber(limitNum, "限量总数")) {
                            valid = false;
                            return false;
                        } else if (parseFloat(displayDiscountPrice) > parseFloat(displayOriginalPrice)) {
                            top.artAlert('温馨提示', '折扣价不能大于展示原价', 'error');
                            valid = false;
                            return false;
                        }
                    }
            );
            if (!valid)
                return;
            nextFrom_add.addActivity();
        });
        $(".del-li").click(function () {
            var _this = $(this);
            top.artConfirm("警告", "您确定要删除该产品吗？", function (v) {
                if (v) {
                    $(_this).parents("li").remove();
                }
            });
        });
    },

    collectionData: function () {

        var data = {};
        var productNum;
        data.activityName = $('input[name="activityName"]').val();
        productNum = document.getElementsByName("totalCunt[]").length;
        data.productNum = productNum;
        data.isDelete = 0;
        data.startTime = new Date($('input[name="startTime"]').val());
        var time = $('input[name="startTime"]').val().substring(0, $('input[name="startTime"]').val().indexOf(" "));
        data.endTime = new Date(time + " " + $('input[name="endTime"]').val() + ":00");
        var p;
        var fn;
        var _arr = [];
        $(".edit-zhekou").each(
                function (i) {
                    p = {};
                    fn = $("input", this);
                    p["displayDiscountPrice"] = fn.eq(0).val(); //折扣价
                    p["displayOriginalPrice"] = fn.eq(1).val(); //原价
                    p["discountRate"] = fn.eq(0).val() / fn.eq(1).val(); //打几折
                    p["limitNum"] = fn.eq(2).val();
                    p["productId"] = fn.eq(4).val();
                    p["remaindAmount"] = parseInt(fn.eq(2).val()) - parseInt(fn.eq(3).val()); //产品剩余数量=限制数量-已售数量
                    _arr.push(p);
                }
        );
        data.productList = $.extend(true, [], _arr);
        return JSON.stringify(data);
    },
    addActivity: function () {
        dialog({
            width: 500,
            title: '创建秒杀活动',
            content: '<div class="con">'
                    + '<table>'
                    + '<colgroup>'
                    + '<col style="width: 100px;">'
                    + '<col>'
                    + '</colgroup>'
                    + '<tr><td class="tr"><i class="orange"> *</i>活动名称：</td><td><input type="text" name="activityName" class="common w180"/></td></tr>'
                    + '<tr><td class="tr"><i class="orange"> *</i>开始时间：'
                    + '</td><td><input type="text" name="startTime"  onfocus="WdatePicker({readOnly: true, skin: \'blueFresh\',minDate:\'%y-%M-%d\',dateFmt:\'yyyy-MM-dd 00:00:00\'});"  class="Wdate common h22 w180" /> </td></tr>'
                    + '<tr><td class="tr"><i class="orange"> *</i>开始时间点：'
                    + '</td><td><input type="text" name="endTime"  onfocus="WdatePicker({readOnly: true, skin: \'blueFresh\',dateFmt:\'HH:mm\'});"  class="Wdate common h22 w180" /> </td></tr>'
                    + '</table>'
                    + '</div>',
            okValue: '确定',
            ok: function () {
                var activityName = $('input[name="activityName"]').val();
                if (activityName == ' ') {
                    top.artAlert('温馨提示', '请输入活动名称', 'error');
                    return false;
                }
                var startTime = $('input[name="startTime"]').val();
                if (startTime == '') {
                    top.artAlert('温馨提示', '请输入开始时间', 'error');
                    return false;
                }
                var endTime = $('input[name="endTime"]').val();
                if (endTime == '') {
                    top.artAlert('温馨提示', '请输入结束时间', 'error');
                    return false;
                }
                ;
                $.ajax({
                    type: "POST",
                    url: "/jdvop/admin/activity/timeSpike/createActivity.php",
                    contentType : "application/json",
                    data: nextFrom_add.collectionData(),
                    async: false,
                    success: function (data) {
                        var temp = JSON.parse(data);
                        if (temp.isOk) {
                            top.artAlert('温馨提示', "数据保存成功！", "/jdvop/admin/activity/selectTimeSpike.php", 'ok');
                        } else {
                            top.artAlert('温馨提示', temp.message, "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                        }
                    },
                    error: function (data, xhr, type) {
                        top.artAlert('温馨提示', "系统异常", "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                        $(".btn-orange").attr("disabled", false);
                    }
                }, 'json');
            },
            cancelValue: '取消',
            cancel: function () {
            }

        }).showModal();

    }
}
var timeSpike_list = {
    init: function () {
        $("a[name='delete']").click(function () {
            var id = $(this).attr("ref_id");
            top.artConfirm('温馨提示', '您确定要删除吗?', function (v) {
                if (v) {
                    $.ajax({
                        url: "/jdvop/admin/activity/timeSpike/delete.php",
                        type: "POST",
                        dataType: "json",
                        data: {activityId: id},
                        success: function (data) {
                            if (data.isOk === true) {
                                top.artAlert('温馨提示', "操作成功!", "/jdvop/admin/activity/selectTimeSpike.php", 'ok');
                            } else {
                                top.artAlert('温馨提示', "操作失败!", "/jdvop/admin/activity/selectTimeSpike.php", 'ok');
                            }
                        }
                    });
                }
            });
        })
        $("a[name='updateActivityTime']").click(function () {
            var id = $(this).attr("ref_id");
            dialog({
                width: 500,
                title: '创建秒杀活动',
                content: '<div class="con">'
                        + '<table>'
                        + '<colgroup>'
                        + '<col style="width: 100px;">'
                        + '<col>'
                        + '</colgroup>'
                        + '<tr><td class="tr"><i class="orange"> *</i>开始时间：'
                        + '</td><td><input type="text" name="startTime" id="sTime" onfocus="WdatePicker({readOnly: true, skin: \'blueFresh\',dateFmt:\'yyyy-MM-dd 00:00:00\'});"  class="Wdate common h22 w180" /> </td></tr>'
                        + '<tr><td class="tr"><i class="orange"> *</i>开始时间点：'
                        + '</td><td><input type="text" name="endTime"  id="eTime" onfocus="WdatePicker({readOnly: true, skin: \'blueFresh\',dateFmt:\'HH:mm\'});"  class="Wdate common h22 w180" /> </td></tr>'
                        + '</table>'
                        + '</div>',
                okValue: '确定',
                ok: function () {
                    var startTime = $("#sTime").val();

                    if (startTime == '') {
                        top.artAlert('温馨提示', '请输入开始时间', 'error');
                        return false;
                    }
                    var endTime = $("#eTime").val();
                    if (endTime == '') {
                        top.artAlert('温馨提示', '请输入结束时间', 'error');
                        return false;
                    }
                    ;
                    $.ajax({
                        type: "POST",
                        url: "/jdvop/admin/activity/timeSpike/createActivity.php",
                        contentType : "application/json",
                        data: collectionData(id),
                        async: false,
                        success: function (data) {
                            var temp = JSON.parse(data);
                            if (temp.isOk) {
                                top.artAlert('温馨提示', "操作成功！", "/jdvop/admin/activity/selectTimeSpike.php", 'ok');
                            } else {
                                top.artAlert('温馨提示', temp.message);
                                $(".btn-orange").attr("disabled", false);
                            }
                        },
                        error: function (data, xhr, type) {
                            top.artAlert('温馨提示', "系统异常", "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                            $(".btn-orange").attr("disabled", false);
                        }
                    }, 'json');
                },
                cancelValue: '取消',
                cancel: function () {
                }

            }).showModal();
        })
        var collectionData = function (id) {
            var data = {};
            data.isDelete = 0;
            data.startTime = new Date($("#sTime").val());
            var time = $("#sTime").val().substring(0, $("#sTime").val().indexOf(" "));
            data.endTime = new Date(time + " " + $("#eTime").val() + ":00");
            data.id = id;
            //判断时间
            var startTimeActivity = new Date(time + " " + $("#eTime").val() + ":00").getTime();//活动开始时间
            var newDate = new Date().getTime();//当前时间
            if (startTimeActivity > newDate) {
                data.status = 'will_do';
            } else {
                data.status = 'be_doing';
            }
            return JSON.stringify(data);
        };

        /**
         * 批量删除
         */

        $(".batch-action").on("click", function (e) {
            var aa = [];
            $("input[name='activityId']:checkbox:checked").each(function () {
                aa.push($(this).val());
            });
            if (aa.length === 0) {
                top.artAlert("温馨提示", "您还没有选中哦!", "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                return false;
            } else {
                e.preventDefault();
                top.artConfirm("警告", "是否确认批量删除?", function (result) {
                    if (result) {
                        $.post("/jdvop/admin/activity/timeSpike/delete.php", $("#activity_from").serialize(), function (data) {
                            if (data.isOk) {
                                top.artAlert("温馨提示", data.message, "/jdvop/admin/activity/selectTimeSpike.php", 'ok');
                            } else {
                                top.artAlert("温馨提示", data.message, "/jdvop/admin/activity/selectTimeSpike.php", 'error');
                            }
                        }, "json");

                    }
                });
            }
        });
    }
}
var timeSpike_form = {
    animationShow: function () {
        if ($(".js-showImgList li").length <= 0) {
            $(".js-hideBox").animate({
                height: '0px'
            }, "1000");
            $(".js-hideBox .prompt").show();
        } else {
            $(".js-hideBox").animate({
                height: '421px'
            }, "1000");
            $(".js-hideBox .prompt").hide();
        }
        ;
    },
    init: function () {
        timeSpike_form.animationShow();
        $(document).on('click', '.js-selectbox li', function (e) {
            e.preventDefault();
            var thiz = $(this);
            var paramNum = $(".js-showImgList").find("li");     // 已选择的产品数量
            var img = thiz.find('img:first').attr('src');             // 产品图片路径
            var id = thiz.attr('rel');                          // 产品编号ID
            var title = thiz.find('label').attr('title');       // 获取每次点击的图片标题
            var name = thiz.find('p.product-title').text();
            if (thiz.hasClass('curr')) {
                //如果已经选中则取消,并且删除选中的产品
                thiz.removeClass('curr');
                $(".js-showImgList li").each(function () {
                    if ($(this).attr("rel") == id) {
                        $(this).remove();
                    }
                });
            } else {
                if (paramNum.size() >= $("#maxSize").val()) {
                    top.artAlert("温馨提示", "本栏目最多只能选择$maxSize款产品", 'error');
                    return false;
                }
                var html = "<li rel='" + id + "' class='js-dragsort'>";
                html += "<input type='hidden' name='id[]' value='" + id + "' />";
                html += '<a href="javascript:;" class="tooltip" title="' + name + '">';
                html += "<img src=\"" + img + "\"/>";
                html += "</a>";
                html += '<span class="ico-del js-del"></span>';
                html += '</li>';
                $(".js-showImgList").append(html);
                thiz.addClass('curr');
                timeSpike_form.animationShow()
            }
            ;
        });
        //清空已选产品
        $(document).on("click", ".js-clear-all", function (e) {
            e.preventDefault();
            $('li.js-dragsort').each(function () {
                var thisPicId = $(this).attr('rel');
                $('.js-selectbox li[rel="' + thisPicId + '"]').removeClass('curr');
                $(this).remove();
            });
        });

        //产品删除按钮
        $(document).on('click', '.js-dragsort .js-del', function (e) {
            e.preventDefault();
            var thisBox = $(this).parent('.js-dragsort');
            var thisPicId = $(thisBox).attr('rel');
            $('.js-selectbox li[rel="' + thisPicId + '"]').removeClass('curr');
            thisBox.remove();
            return false;
        });
        //模块拖动排序
        $('#dragsort').dragsort({
            dragSelector: "li.js-dragsort",
            dragEnd: function () { },
            dragBetween: 'false',
            dragSelectorExclude: 'span.ico-del',
            placeHolderTemplate: '<li class="a-dashed"></li>',
            scrollSpeed: 5
        });
        $('#query_form').on('submit', function (e) {
            e.preventDefault();
            var data = collectionData();
            top.artProgress('数据加载中，请稍后...');
            var url = '/jdvop/admin/website/block/loadProducts.php';
            if (data.length > 0) {
                url += '?';
                url += data.join('&');
            }
            $.get(url, function (data) {
                $('#candidate_box').html(data['contentHtml']);
                $('#page_box').html(data['pageHtml']);
                timeSpike_form.highlight();
                top.artProgress('[close]');
            }, 'json');
        });

        $('#do_save').on('click', function (e) {
            e.preventDefault();
            var selected;
            $('#dragsort li.js-dragsort').each(function () {
                selected += "," + $(this).attr('rel');
            });
            var newIds = selected.substring(selected.indexOf(",") + 1);
            if (selected.length < $("#minSize").val()) {
                top.artAlert('温馨提示', '您至少需要选择$minSize款产品', 'error');
                return false;
            }
            window.location.href = "/jdvop/admin/activity/timeSpike/nextfrom.php?pids=" + newIds + "&activityId=" + $("#activityId").val();
        });

        /*点击下一页*/
        $(document).on('click', 'ul.multipage a.link', function (e) {
            e.preventDefault();
            var href = $(this).attr('href');
            if (href !== undefined && href !== '#' && href !== '') {
                top.artProgress('数据加载中，请稍后...');
                $.get(href, function (data) {
                    $('#candidate_box').html(data['contentHtml']);
                    $('#page_box').html(data['pageHtml']);
                    timeSpike_form.highlight();
                    top.artProgress('[close]');
                }, 'json');
            }
        });

        $('button.js-sort-switch').click(function (e) {
            e.preventDefault();
            var url = location.pathname;
            url = '/jdvop/admin/website/block/loadProducts.php';
            var t = $(this);
            url = url + '?sort=' + t.attr('rel') + '.' + t.attr('js-sort-t');

            var data = collectionData();
            if (data.length > 0) {
                url += '&';
                url += data.join('&');
            }
            $.get(url, function (data) {
                $('#candidate_box').html(data['contentHtml']);
                $('#page_box').html(data['pageHtml']);
                timeSpike_form.highlight();
                top.artProgress('[close]');
            }, 'json');
            if (t.attr('js-sort-t') == 'desc')
            {
                t.find("i").html("↑");
                t.attr('js-sort-t', 'asc');
            } else if (t.attr('js-sort-t') == 'asc') {
                t.find("i").html("↓");
                t.attr('js-sort-t', 'desc');
            }
        });

        var collectionData = function () {
            var data = [];
            var cate = $('select[name="cate"]').val();
            if (cate !== '0') {
                data.push('cate=' + cate);
            }
            var price = parseFloat($.trim($('input[name="minPrice"]').val()));
            if (!isNaN(price)) {
                data.push('minPrice=' + price);
            }
            price = parseFloat($.trim($('input[name="maxPrice"]').val()));
            if (!isNaN(price)) {
                data.push('maxPrice=' + price);
            }
            var kwd = $.trim($('input[name="kw"]').val());
            if (kwd !== '') {
                data.push('kw=' + encodeURIComponent(kwd));
            }
            var productType = $('select[name="type"]').val();
            if (productType !== '') {
                data.push('type=' + productType);
            }
            var activityId = $('input[name="activityId"]').val();
            if (activityId !== '') {
                data.push('activityId=' + activityId);
            }
            return data;
        };
    },
    highlight: function () {
        var selected = [];
        $('#dragsort li.js-dragsort').each(function () {
            selected.push($(this).attr('rel'));
        });
        if (selected.length > 0) {
            $('#candidate_box li').each(function () {
                var li = $(this);
                var id = li.attr('rel');
                if ($.inArray(id, selected) !== -1) {
                    li.addClass('curr');
                }
            });
        }
    }
}
var activity_detail = {
    animationShow: function () {
        if ($(".js-showImgList li").length <= 0) {
            $(".js-hideBox").animate({
                height: '0px'
            }, "1000");
            $(".js-hideBox .prompt").show();
        } else {
            $(".js-hideBox").animate({
                height: '421px'
            }, "1000");
            $(".js-hideBox .prompt").hide();
        }
        ;
    },
    init: function () {
        activity_detail.animationShow();
        $(document).on('click', '.js-selectbox li', function (e) {
            e.preventDefault();
            var thiz = $(this);
            var paramNum = $(".js-showImgList").find("li");     // 已选择的产品数量
            var img = thiz.find('img:first').attr('src');             // 产品图片路径
            var id = thiz.attr('rel');                          // 产品编号ID
            var title = thiz.find('label').attr('title');       // 获取每次点击的图片标题
            var name = thiz.find('p.product-title').text();
            if (thiz.hasClass('curr')) {
                //如果已经选中则取消,并且删除选中的产品
                thiz.removeClass('curr');
                $(".js-showImgList li").each(function () {
                    if ($(this).attr("rel") == id) {
                        $(this).remove();
                    }
                });
            } else {
                var html = "<li rel='" + id + "' class='js-dragsort'>";
                html += "<input type='hidden' name='id[]' value='" + id + "' />";

                html += '<span class="ico-del js-del"></span>';
                html += '</li>';
                $(".js-showImgList").append(html);
                thiz.addClass('curr');
                activity_detail.animationShow()
            }
            ;
        });
        //清空已选产品
        $(document).on("click", ".js-clear-all", function (e) {
            e.preventDefault();
            $('li.js-dragsort').each(function () {
                var thisPicId = $(this).attr('rel');
                $('.js-selectbox li[rel="' + thisPicId + '"]').removeClass('curr');
                $(this).remove();
            });
        });

        //产品删除按钮
        $(document).on('click', '.js-dragsort .js-del', function (e) {
            e.preventDefault();
            var thisBox = $(this).parent('.js-dragsort');
            var thisPicId = $(thisBox).attr('rel');
            $('.js-selectbox li[rel="' + thisPicId + '"]').removeClass('curr');
            thisBox.remove();
            return false;
        });
        //模块拖动排序
        $('#dragsort').dragsort({
            dragSelector: "li.js-dragsort",
            dragEnd: function () {
            },
            dragBetween: 'false',
            dragSelectorExclude: 'span.ico-del',
            placeHolderTemplate: '<li class="a-dashed"></li>',
            scrollSpeed: 5
        });
    }
}
//校验是否为空
var validNull = function (fieldVal, msg) {
    return isNull(fieldVal, msg);
};
//校验数字正数
var validPositiveNumber = function (fieldVal, msg) {
    return isNegativeNumber(fieldVal, msg);
};
//校验数字整数
var validNumber = function (fieldVal, msg) {
    return isPositiveIntegerNumber(fieldVal, msg);
};