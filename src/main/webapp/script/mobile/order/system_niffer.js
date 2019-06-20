var apply_niffer = {
    init: function () {
        $("textarea[name='remark']").on('keyup', function () {
            var remark = $(this).val();
            var maxLen = $(this).attr("maxlength");
            if (remark.length >= maxLen) {
                $(this).val(remark.substring(0, maxLen));
            }
        });
        $(document).on('touchstart', '.js-checked', function (e) {
            e.preventDefault();
            var thiz = $(this);
            if (thiz.hasClass('curr')) {
                thiz.removeClass('curr');
                thiz.parents('li').find('input[name="selected[]"]').attr('value', 'false');
            } else {
                thiz.addClass('curr');
                thiz.parents('li').find('input[name="selected[]"]').attr('value', 'true');
            }
        });
        //增加数量
        var product_count;
        $('.js-add').on('touchstart', function () {
            product_count = $(this).attr('rev');
            var txtVal = $(this).siblings('.js-fm-txt');
            var maxAmount = parseInt(txtVal.attr('max'));
            var amount = parseInt(txtVal.val()) + 1;
            if (amount >= maxAmount) {
                txtVal.val(maxAmount);
                $(this).parent().find(".js-reduce").removeClass('disabled');
            } else {
                txtVal.val(amount);
            }
        });
        //减少数量
        $('.js-reduce').on('touchstart', function () {
            var txtVal = $(this).siblings('.js-fm-txt');
            var amount = parseInt(txtVal.val());
            if (parseInt(txtVal.val()) === 1) {
                return false;
            }
            txtVal.val(amount - 1);
            if (parseInt(txtVal.val()) === 1) {
                $(this).addClass('disabled');
            }
        });
        //数量修改
        $(".js-fm-txt").on("change", function () {
            var number = $(this).val();
            number = isNaN(number) ? 1 : number;
            var maxAmount = Number($(this).attr("max"))
            if(number>maxAmount){
                $(this).val(maxAmount);
            }
        });

        $(".but-orange").click(function (e) {
            e.preventDefault();
            if (checkForm()) {
                $.post($('#dataForm').attr('action'), $('#dataForm').serialize(), function (result) {
                    var data = eval('(' + result + ')');
                    if (data.isOk) {
                        location = '/jdvop/mobile/order/nifferResult.php?id=' + data.message;
                    } else {
                        mui.alert(data.message, '温馨提示');
                    }
                }, 'application/json');
            }
        });
        function checkForm() {
            if ($("input[name='consumerName']").val() === '') {
                mui.alert('请填写联系人姓名！', '温馨提示');
                return false;
            }
            if ($("input[name='consumerMobile']").val() === '') {
                mui.alert('请填写您的联系方式，方便客服人员联系到您！', '温馨提示');
                return false;
            }
            return true;
        }
        /* 切换售后类型 */
        $(".js-btn-service").change(function () {
            var value = $(".js-btn-service").val();
            if (value === 'niffer') {
                $('input[name="afterSaleType"]').val(value);
                $('select.js-niffer-reason').attr('name', 'reason').removeClass('hide');
                $('select.js-return-reason').attr('name', 'reason_hide').addClass('hide');
                $("#pageTitle").html("换货商品");
                $("#consumerInfo").show();
            } else if (value === 'returned') {
                $('input[name="afterSaleType"]').val(value);
                $('select.js-return-reason').attr('name', 'reason').removeClass('hide');
                $('select.js-niffer-reason').attr('name', 'reason_hide').addClass('hide');
                $("#pageTitle").html("退货商品");
                $("#consumerInfo").hide();
            }
        });
    }
}
