$(function () {
    $('.js-comfirm-refund').click(function (e) {
        e.preventDefault();
        var $this = $(this);
        var exceptionRefund = $this.attr("exception-refund");
        var msg="";
        if("true"==exceptionRefund){
            msg+="该退款单曾退款失败，";
        }
        msg += "是否确认退款?";
        top.artConfirm("温馨提示", msg, function (result) {
            if (result) {
                top.artProgress('正在提交数据，请稍后...');
                $.post($this.attr("data-url"), {}, function (data) {
                    top.artProgress('[close]');
                    if (data.isOk) {
                        window.location.href="/jdvop/admin/order/niffers.php?isSystem="+$("input[name=isSystem]").val();
                    } else {
                        top.artAlert("温馨提示", data.message, 'error');
                    }
                }, "json");
            }
        }, 'question');
    });

    $(".js-refuse").click(function (e) {
        e.preventDefault();
        var thiz = $(this);
        top.dialog({
            width: 450,
            title: '填写备注信息',
            content: '<div class="con">'
                    + '<table>'
                    + '<colgroup>'
                    + '<col style="width: 100px;">'
                    + '<col />'
                    + '</colgroup>'
                    + '<tr><td class="tr"><i class="orange">*</i>拒绝原因：</td><td><textarea name="remark" cols="54" rows="6" placeholder="请填写拒绝原因！"></textarea></td></tr>'
                    + '</table>'
                    + '</div>',
            okValue: '确定',
            ok: function () {
                var remark = top.$('textarea[name="remark"]').val();
                if (remark == '') {
                    top.artAlert('温馨提示', '请输入拒绝原因！', 'error');
                    return false;
                }
                var id = thiz.attr("rel");
                $.post('/jdvop/admin/order/niffer/refuse.php', {id: id, remark: remark}, function (data) {
                    if (data.isOk) {
                       window.location.href="/jdvop/admin/order/niffers.php?isSystem="+$("input[name=isSystem]").val();
                    } else {
                        top.artAlert('温馨提示', data.message, 'error');
                        return;
                    }
                }, 'json');
            },
            cancelValue: '取消',
            cancel: function () {
            },

        }).showModal();
    });
});