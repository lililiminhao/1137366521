var shopping_order = {
    //确认收货
    confirm_receipt: function() {
        $(document).on('click', '.js_confirm_receipt', function() {
            mui.confirm('是否确认收货？', '温馨提示', function(e){
                if (e.index == 1) {
                    var url = $('.js_confirm_receipt').attr('data-url');
                    $.post(url, {}, function(data){
                        if(data.isOk){
                            location.reload();
                        } 
                    }, "json");
                }
            });
        });
    }
}

