/**
 * Created by colorful on 2018/10/19.
 */
var _currentElement;    //当前点击元素
var _currentProduct;
var _currentImage;
var _currentSlider;
var _fileuploadURL;
var _addElementId;      //要添加内容的ID
var _system_image_host = "https://cdnweb05.96225.com/jdvop/opt/data/image"; //系统路径前缀
$(function(){
    /*------------------------------ 模板内容 ------------------------------------*/
    //公告栏
    $(document).on('click', 'li.js-add-notice', function() {
        serial_num ++;
        var data;
        var specialActivityId = $('input[name="specialActivityId"]').val();
        if(specialActivityId == null){
            data = {bloKey: 'NOTICE_BLOCK'};
        }else{
            data = {bloKey: 'NOTICE_BLOCK', specialActivityId : specialActivityId};
        }
        $.get('/jdvop/admin/custom/fenxiao/modifyText.php?type=create', data, function(data){
            if(data.isOk) {
                var html = '<dd class="js-dragsort" id="notice_'+ serial_num +'">';
                html += '<input type="hidden" name="blockId[]" value="'+ data.message.blockId +'"/>';
                html += '<div class="custom-notice">';
                html += '<div class="text js-autoRoll"><i>公告：</i>';
                html += '<div class="con">';
                html += '<input type="hidden" name="noticeContent" value="请填写内容，如果文字过长则将会自动滚动。"/>'
                html += '<p id="textMove">请填写内容，如果文字过长则将会自动滚动。</p>';
                html += '</div>';
                html += '</div></div>';
                html += '<div class="actions-wrap">';
                html += '<span class="js-btnEdit" key="notice" rev="'+ serial_num +'">编辑</span>';
                html += '<span class="js-addContent">加内容</span><span class="js-btnDel">删除</span></div>';
                html += '</dd>';
                if(_addElementId !== '' && typeof(_addElementId) !== 'undefined') {
                    $('#' + _addElementId).after(html);
                    _addElementId = '';
                } else {
                    $('.js-temp').before(html);
                }
                textMove();
                $('#notice_' + serial_num).find('span.js-btnEdit').click();
            }
        }, 'json');
    });
    //图片广告
    $(document).on('click', 'li.js-add-slider', function() {
        var data;
        var specialActivityId = $('input[name="specialActivityId"]').val();
        if(specialActivityId == null){
            data ={bloKey: 'SLIDER_BLOCK'};
        }else{
            data ={bloKey: 'SLIDER_BLOCK', specialActivityId : specialActivityId};
        }
        serial_num ++;
        $.get('/jdvop/admin/custom/fenxiao/modifySlider.php?type=create', data, function(data){
            if(data.isOk) {
                var html = '<dd class="js-dragsort" id="slider_'+ serial_num +'">';
                html += '<input type="hidden" name="blockId[]" value="'+ data.message.blockId +'"/>';
                html += '<div class="main-banner js-main-banner">';
                html += '<ul class="swiper-wrapper js-slider-ul">';
                html += '<li class="swiper-slide"><img src="/jdvop/images/admin/wx/morenimg1.jpg"></li>';
                html += '<li class="swiper-slide"><img src="/jdvop/images/admin/wx/morenimg1.jpg"></li>';
                html += '<li class="swiper-slide"><img src="/jdvop/images/admin/wx/morenimg1.jpg"></li>';
                html += '</ul>';
                html += '<div class="swiper-pagination"></div>';
                html += '</div>';
                html += '<div class="actions-wrap">';
                html += '<span class="js-btnEdit" key="slider" rev="'+ serial_num +'">编辑</span><span class="js-addContent">加内容</span><span class="js-btnDel">删除</span>';
                html += '</div>';
                html += '</dd>';
                if(_addElementId !== '' && typeof(_addElementId) !== 'undefined') {
                    $('#' + _addElementId).after(html);
                    _addElementId = '';
                } else {
                    $('.js-temp').before(html);
                }
                $('#slider_' + serial_num).find('span.js-btnEdit').click();
            }
        }, 'json');
    });
    //导航按钮
    $(document).on('click', 'li.js-add-nav', function() {
        serial_num ++;
        var data;
        var specialActivityId = $('input[name="specialActivityId"]').val();
        if(specialActivityId == null){
            data = {bloKey: 'NAVIGATION_BLOCK'};
        }else{
            data = {bloKey: 'NAVIGATION_BLOCK', specialActivityId : specialActivityId};
        }
        $.get('/jdvop/admin/custom/fenxiao/modifySlider.php?type=create', data, function(data){
            if(data.isOk) {
                var html = '<dd class="js-dragsort" id="nav_'+ serial_num +'">';
                html += '<input type="hidden" name="blockId[]" value="'+ data.message.blockId +'"/>';
                html += '<div class="a-module-1 webkitbox-h js-nav-div">';
                html += '<div class="flex1 list"><img src="/jdvop/images/admin/wx/test-img.jpg"/></div>';
                html += '<div class="flex1 list"><img src="/jdvop/images/admin/wx/test-img.jpg"/></div>';
                html += '</div>';
                html += '<div class="actions-wrap">';
                html += '<span class="js-btnEdit" key="navigation" rev="'+ serial_num +'">编辑</span>';
                html += '<span class="js-addContent">加内容</span><span class="js-btnDel">删除</span></div>';
                html += '</dd>';
                if(_addElementId !== '' && typeof(_addElementId) !== 'undefined') {
                    $('#' + _addElementId).after(html);
                    _addElementId = '';
                } else {
                    $('.js-temp').before(html);
                }
                $('#nav_' + serial_num).find('span.js-btnEdit').click();
            }
        }, 'json');
    });
    //产品搜索
    $(document).on('click', 'li.js-add-search', function() {
        serial_num ++;
        var data ;
        var specialActivityId = $('input[name="specialActivityId"]').val();
        if(specialActivityId == null){
            data = {bloKey: 'PRODUCT_SEARCH_BLOCK'};
        }else{
            data = {bloKey: 'PRODUCT_SEARCH_BLOCK', specialActivityId : specialActivityId};
        }
        $.get('/jdvop/admin/custom/fenxiao/modifySearchBox.php', data, function(data){
            if(data.isOk) {
                $('input[name=blockId]').val(data.message.blockId);
                var html = '<dd class="js-dragsort" id="nav_'+ serial_num +'">';
                html += '<input type="hidden" name="blockId[]" value="'+ data.message.blockId +'"/>';
                html += '<div class="a-search-wrap search-wrap">';
                html += '<div class="search-frm">';
                html += '<input type="search" class="search" id="search" autocomplete="off" placeholder="搜索全部商品"/>';
                html += '<a class="clear hide js-clearSearch" href="javascript:;" >x</a>';
                html += '</div>';
                html += '<div class="btn-wrap">';
                html += '<input type="submit" value="搜索" class="hide"/>';
                html += '<a href="javascript:;" id="searchBtn" class=" hd_search_btn_blue">搜索</a>';
                html += '</div></div>';
                html += '<div class="actions-wrap"><span class="js-btnEdit" key="search">编辑</span><span class="js-addContent">加内容</span>';
                html += '<span class="js-btnDel">删除</span></div>';
                html += '</dd>';
                if(_addElementId !== '' && typeof(_addElementId) !== 'undefined') {
                    $('#' + _addElementId).after(html);
                    _addElementId = '';
                } else {
                    $('.js-temp').before(html);
                }
            }
        }, 'json');
    });
    //产品列表
    $(document).on('click', 'li.js-add-product', function() {
        serial_num ++;
        var data;
        var specialActivityId = $('input[name="specialActivityId"]').val()||null;
        if(specialActivityId == null){
            data = {bloKey: 'PRODUCT_BLOCK_2'};
        }else{
            data = {bloKey: 'PRODUCT_BLOCK_2', specialActivityId : specialActivityId};
        }
        $.get('/jdvop/admin/custom/fenxiao/modifyProductBox.php', data, function(data){
            if(data.isOk) {
                var html = '<dd class="js-dragsort" id="product_'+ serial_num +'">';
                html += '<input type="hidden" name="blockId[]" value="'+ data.message.blockId +'"/>';
                html += '<div class="a-module-3 layout-grid2 clearfix" id="product-module" style="background:#fdedee;margin:0;padding:2% 0;">';
                html += '<p class=\"a-title tc\" style="padding:0;margin:0 15px">~标题~</p>';
                html += '<div id=\"quan_list\">'
                html += '<div class=\"quan_wrapper\">'
                html += '<div class=\"quan_item\">'
                html += '<div class=\"q_l\"><p>￥<span>10</span></p><p>满100可用</p></div>'
                html += '<div class=\"q_r\">立即领取</div>'
                html += '</div>'
                html += '<div class=\"quan_item\">'
                html += '<div class=\"q_l\"><p>￥<span>10</span></p><p>满100可用</p></div>'
                html += '<div class=\"q_r\">立即领取</div>'
                html += '</div>'
                html += '<div class=\"quan_item\">'
                html += '<div class=\"q_l\"><p>￥<span>10</span></p><p>满100可用</p></div>'
                html += '<div class=\"q_r\">立即领取</div>'
                html += '</div>'
                html += '</div>'
                html += '</div>'
                html += '<ul class="js-product-ul" style="box-sizing:border-box;padding:0 15px;display:flex;flex-wrap:wrap;justify-content:space-between">';
                html += '<li><p class="img"><img src="/jdvop/images/admin/wx/test-img.jpg"/></p>';
                html += '<p class="text"><span class="name js-name">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>价格：<em class="price red js-price">6690.00</em></p></li>';
                html += '<li><p class="img"><img src="/jdvop/images/admin/wx/test-img.jpg"  alt="产品名称"/></p>';
                html += '<p class="text"><span class="name js-name">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>价格：<em class="price red js-price">6690.00</em></p></li>';
                html += '<li><p class="img"><img src="/jdvop/images/admin/wx/test-img.jpg"  alt="产品名称"/></p>';
                html += '<p class="text"><span class="name js-name">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>价格：<em class="price red js-price">6690.00</em></p></li>';
                html += '<li><p class="img"><img src="/jdvop/images/admin/wx/test-img.jpg"  alt="产品名称"/></p>';
                html += '<p class="text"><span class="name js-name">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>价格：<em class="price red js-price">6690.00</em></p></li>';
                html += '</ul>';
                html += '</div>';
                html += '<div class="actions-wrap">';
                html += '<span class="js-btnEdit" key="product" rev="'+ serial_num +'" rel="'+ data.message.bloKey +'" snum="6">编辑</span>';
                html += '<span class="js-addContent">加内容</span><span class="js-btnDel">删除</span></div>';
                html += '</dd>';
                if(_addElementId !== '' && typeof(_addElementId) !== 'undefined') {
                    $('#' + _addElementId).after(html);
                    _addElementId = '';
                } else {
                    $('.js-temp').before(html);
                }
                $('#product_' + serial_num).find('span.js-btnEdit').click();
            }
        }, 'json');
    });

    /*========================================================== 模板内容end ==================================================================*/
    //编辑模块
    var dd_count;
    var image_count;
    var cates;
    $.get("/jdvop/admin/custom/fenxiao/cates.php", {}, function(data) {
        if (typeof data === 'object')
        {
            cates = data;
        }
    }, 'json');
    $(document).on("click", 'span.js-btnEdit', function(){
        var topOff = $(this).parents(".js-dragsort").offset().top-90;
        $('html,body').animate({scrollTop:topOff}, 800);
        var key = $(this).attr('key');
        dd_count = $(this).attr('rev');
        if(key === "notice") {
            var text = $('#notice_' + dd_count).find('input[name=noticeContent]').val();
            var blockId = $('#notice_' + dd_count).find('input[name="blockId[]"]').val();
            var noticeHTML = '<img src="/jdvop/images/admin/ico-arrow2.png" class="img-arrow2"/>';
            noticeHTML += '<div class="app-sidebar-inner clearfix">';
            noticeHTML += '<div class="app-mob app-custom-notice">';
            noticeHTML += '<table>';
            noticeHTML += '<colgroup><col style="width: 80px;"/><col/></colgroup>';
            noticeHTML += '<tr>';
            noticeHTML += '<td valign="top" class="tr">公告：</td>';
            noticeHTML += '<td><textarea class="com" name="text" id="notice_content" placeholder="请在这里填写内容...">'+ text +'</textarea>';
            noticeHTML += '<div class="btn-wrap">';
            noticeHTML += '<span class="btn-orange js-save-btn" rev="text">确定</span><span class="btn-grey js-del-btn">取消</span>';
            noticeHTML += '</div></td></tr>';
            noticeHTML += '</table></div></div>';
            $('input[name=bloKey]').val('NOTICE_BLOCK');
            $('input[name=blockId]').val(blockId);
            $('#js-edit-block').html(noticeHTML);
        } else if(key === "slider" || key === "image") {
            var blockId = $('#slider_' + dd_count).find('input[name="blockId[]"]').val();
            var sliderHTML = '<img src="/jdvop/images/admin/ico-arrow2.png" class="img-arrow2"/>';
            sliderHTML += '<div class="app-sidebar-inner clearfix">';
            sliderHTML += '<div class="app-banner">';
            sliderHTML += '<div class="control-group pb10">';
            sliderHTML += '<label class="control-label">显示方式：</label>';
            sliderHTML += '<div class="controls">';
            if(key === "image") {
                $('input[name=bloKey]').val('IMAGE_BLOCK');
                sliderHTML += '<label class="radio inline pr20 cup"><input type="radio" name="showType" value="slider">轮播广告</label>';
                sliderHTML += '<label class="radio inline cup"><input type="radio" name="showType" value="image" checked="checked"/>图片广告</label>';
                sliderHTML += '</div></div>';
            } else {
                $('input[name=bloKey]').val('SLIDER_BLOCK');
                sliderHTML += '<label class="radio inline pr20 cup"><input type="radio" name="showType" value="slider" checked="checked">轮播广告</label>';
                sliderHTML += '<label class="radio inline cup"><input type="radio" name="showType" value="image"/>图片广告</label>';
                sliderHTML += '</div></div>';
            }
            sliderHTML += '<ul class="clearfix js-add-img">';
            sliderHTML += '<li class="btn-add js-btnAdd" rev="slider">';
            sliderHTML += '<a href="javascript:;" class="tooltip" title="为了更好的展示，选中轮播方式显示，请传图片大小尺寸为640*300px,分开显示则传宽度为640px，高度不限"> +添加一个广告</a>';
            sliderHTML += '</li>';
            sliderHTML += '</ul>';
            sliderHTML += '<div class="btn-wrap">';
            if(key === "slider") {
                sliderHTML += '<span class="btn-orange js-save-btn" rev="slider">确定</span>';
            } else {
                sliderHTML += '<span class="btn-orange js-save-btn" rev="image">确定</span>';
            }
            sliderHTML += '<span class="btn-grey js-del-btn">取消</span></div>';
            sliderHTML += '</div>';
            sliderHTML += '</div>';
            if(key === "slider") {
                $.get("/jdvop/admin/custom/fenxiao/getCustomBlocks.php", {id: blockId}, function(d) {
                    if (typeof d === 'object')
                    {
                        var appendHTML = '';
                        if(typeof(d.length) !== 'undefined' && d.length > 0) {
                            image_count = d.length;
                            _currentSlider = d;
                            for (i = 0; i < d.length; ++i)
                            {
                                appendHTML += '<li class="clearfix js-li" id="li_'+ i +'">';
                                appendHTML += '<span class="btn-del js-img-btnDel" title="删除"></span>';
                                appendHTML += '<table><tr><td valign="top">';
                                appendHTML += '<div class="img">';
                                appendHTML += '<input type="hidden" name="data[]" value="'+ d[i].data +'"/>';
                                appendHTML += '<img src="'+ _system_image_host + d[i].data +'" class="js-upload" width="190" height="100" rev="slider"/>';
                                appendHTML += '<div class="again-up js-upload" rev="slider">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                                appendHTML += '</div></td>';
                                appendHTML += '<td class="pl30">';
                                appendHTML += '<div>链接：';
                                appendHTML += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                                if(d[i].selectFlag === 'product') {
                                    appendHTML += '<option value="product" selected>产品中心</option>';
                                    appendHTML += '<option value="cate">商品分类</option>';
                                    appendHTML += '<option value="custom">自定义</option></select>';
                                    appendHTML += '</div> ';
                                    appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w240 h30" name="linkTo[]"/></div>';
                                } else if(d[i].selectFlag === 'cate') {
                                    appendHTML += '<option value="product">产品中心</option>';
                                    appendHTML += '<option value="cate" selected>商品分类</option>';
                                    appendHTML += '<option value="custom">自定义</option></select></div>';
                                    appendHTML += '<div class="mt10 js-cate">分类：';
                                    appendHTML += '<select class="common h32 w120 js-child-cates" name="partner_cate[]">';
                                    appendHTML += '<option value="-">--请选择--</option>';
                                    for(j = 0; j < cates.length; ++j) {
                                        if(cates[j].partnerId === 0) {
                                            if(d[i].partner_cate === cates[j].id) {
                                                appendHTML += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                            } else {
                                                appendHTML += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                            }
                                        }
                                    }
                                    appendHTML += '</select>';
                                    appendHTML += '&nbsp;<select class="common h32 w140" name="child_cate[]">';
                                    appendHTML += '<option value="-">--请选择--</option>';
                                    for(var j = 0; j < cates.length; ++j) {
                                        if(cates[j].partnerId !== 0 && d[i].partner_cate === cates[j].partnerId) {
                                            if(d[i].child_cate === cates[j].id) {
                                                appendHTML += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                            } else {
                                                appendHTML += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                            }
                                        }
                                    }
                                    appendHTML += '</select></div>';
                                    appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w240 h30" name="linkTo[]"/></div>';
                                } else {
                                    appendHTML += '<option value="product">产品中心</option>';
                                    appendHTML += '<option value="cate">商品分类</option>';
                                    appendHTML += '<option value="custom" selected>自定义</option></select>';
                                    appendHTML += '</div> ';
                                    appendHTML += '<div class="mt10 js-customLink">路径：<input type="text" class="common w240 h30" name="linkTo[]" value="'+ d[i].linkTo +'"/></div>';
                                }
                                appendHTML += '<div class="mt10">备注：<input type="text" name="description[]" class="common w240 h30" value="'+ d[i].description +'"/></div>';
                                appendHTML += '</td></tr></table>';
                                appendHTML += '</li>';
                            }
                        } else {
                            image_count = 2;
                            for (i = 0; i < 2; ++i)
                            {
                                appendHTML += '<li class="clearfix js-li">';
                                appendHTML += '<span class="btn-del js-img-btnDel" title="删除"></span>';
                                appendHTML += '<table><tr><td valign="top">';
                                appendHTML += '<div class="img">';
                                appendHTML += '<input type="hidden" name="data[]" />';
                                appendHTML += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="100" rev="slider"/>';
                                appendHTML += '<div class="again-up js-upload" rev="slider">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                                appendHTML += '</div>';
                                appendHTML += '</td>';
                                appendHTML += '<td class="pl30">';
                                appendHTML += '<div>链接：';
                                appendHTML += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                                appendHTML += '<option value="product">产品中心</option>';
                                appendHTML += '<option value="cate">商品分类</option>';
                                appendHTML += '<option value="custom">自定义</option></select>';
                                appendHTML += '</div> ';
                                appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w240 h30" name="linkTo[]"/></div>';
                                appendHTML += '<div class="mt10">备注：<input type="text" name="description[]" class="common w260 h30"/></div>';
                                appendHTML += '</td></tr></table>';
                                appendHTML += '</li>';
                            }
                        }
                        $('li.js-btnAdd').before(appendHTML);
                    }
                }, 'json');
            } else {
                $.get("/jdvop/admin/custom/fenxiao/getCustomBlock.php", {id: blockId}, function(d) {
                    if (typeof d === 'object')
                    {
                        var appendHTML = '<span>请上传尺寸宽度为&nbsp;<i class="red">640px</i>&nbsp;的广告图片，高度不限</span>';
                        if(d.data !== '') {
                            _currentImage = d;
                            appendHTML += '<li class="clearfix js-li">';
                            appendHTML += '<table><tr><td valign="top">';
                            appendHTML += '<div class="img">';
                            appendHTML += '<input type="hidden" name="data[]" value="'+ d.data +'"/>';
                            appendHTML += '<img src="'+ _system_image_host + d.data +'" class="js-upload" width="190" height="100" rev="image"/>';
                            appendHTML += '<div class="again-up js-upload" rev="image">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                            appendHTML += '</div>';
                            appendHTML += '</td>';
                            appendHTML += '<td class="pl30">';
                            appendHTML += '<div>链接：';
                            appendHTML += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                            if(d.selectFlag === 'product') {
                                appendHTML += '<option value="product" selected>产品中心</option>';
                                appendHTML += '<option value="cate">商品分类</option>';
                                appendHTML += '<option value="custom">自定义</option></select>';
                                appendHTML += '</div> ';
                                appendHTML += '<div class="mt10 js-customLink hide">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                            } else if(d.selectFlag === 'cate') {
                                appendHTML += '<option value="product">产品中心</option>';
                                appendHTML += '<option value="cate" selected>商品分类</option>';
                                appendHTML += '<option value="custom">自定义</option></select></div>';
                                appendHTML += '<div class="mt10 js-cate">分类：';
                                appendHTML += '<select class="common h32 w120 js-child-cates" name="partner_cate[]">';
                                appendHTML += '<option value="-">--请选择--</option>';
                                for(j = 0; j < cates.length; ++j) {
                                    if(cates[j].partnerId === 0) {
                                        if(d.partner_cate === cates[j].id) {
                                            appendHTML += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                        } else {
                                            appendHTML += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                        }
                                    }
                                }
                                appendHTML += '</select>';
                                appendHTML += '&nbsp;<select class="common h32 w140" name="child_cate[]">';
                                appendHTML += '<option value="-">--请选择--</option>';
                                for(var j = 0; j < cates.length; ++j) {
                                    if(cates[j].partnerId !== 0 && d.partner_cate === cates[j].partnerId) {
                                        if(d.child_cate === cates[j].id) {
                                            appendHTML += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                        } else {
                                            appendHTML += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                        }
                                    }
                                }
                                appendHTML += '</select></div>';
                                appendHTML += '<div class="mt10 js-customLink hide">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                            } else {
                                appendHTML += '<option value="product">产品中心</option>';
                                appendHTML += '<option value="cate">商品分类</option>';
                                appendHTML += '<option value="custom" selected>自定义</option></select>';
                                appendHTML += '</div> ';
                                appendHTML += '<div class="mt10 js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]" value="'+ d.linkTo +'"/></div>';
                            }
                            appendHTML += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30" value="'+ d.description +'"/></div>';
                            appendHTML += '</td></tr></table>';
                            appendHTML += '</li>';
                        } else {
                            appendHTML += '<li class="clearfix js-li">';
                            appendHTML += '<table><tr><td valign="top">';
                            appendHTML += '<div class="img">';
                            appendHTML += '<input type="hidden" name="data[]" />';
                            appendHTML += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="90" rev="image"/>';
                            appendHTML += '<div class="again-up js-upload" rev="image">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                            appendHTML += '</div>';
                            appendHTML += '</td>';
                            appendHTML += '<td class="pl30">';
                            appendHTML += '<div>链接：';
                            appendHTML += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                            appendHTML += '<option value="product">产品中心</option>';
                            appendHTML += '<option value="cate">商品分类</option>';
                            appendHTML += '<option value="custom">自定义</option></select>';
                            appendHTML += '</div> ';
                            appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w240 h30" name="linkTo[]" value="'+ d.linkTo +'"/></div>';
                            appendHTML += '<div class="mt10">备注：<input type="text" name="description[]" class="common w240 h30" /></div>';
                            appendHTML += '</td></tr></table>';
                            appendHTML += '</li>';
                        }
                        $('ul.js-add-img').html(appendHTML);
                    }
                }, 'json');
            }
            $('input[name=blockId]').val(blockId);
            $('#js-edit-block').html(sliderHTML);
        } else if(key === "navigation") {
            var blockId = $('#nav_' + dd_count).find('input[name="blockId[]"]').val();
            var nvaHTML = '<img src="/jdvop/images/admin/ico-arrow2.png" class="img-arrow2"/>';
            nvaHTML += '<div class="app-sidebar-inner clearfix">';
            nvaHTML += '<div class="app-banner">';
            nvaHTML += '<ul class="clearfix js-add-img">';
            nvaHTML += '<li class="btn-add js-btnAdd" rev="navigation"><a href="javascript:;" class="tooltip" title="为了更好的展示，请至少添加两个广告"> +添加一个广告</a></li>';
            nvaHTML += '</ul>';
            nvaHTML += '<div class="btn-wrap">';
            nvaHTML += '<span class="btn-orange js-save-btn" rev="navigation">确定</span>';
            nvaHTML += '<span class="btn-grey js-del-btn">取消</span></div>';
            nvaHTML += '</div>';
            nvaHTML += '</div>';
            $.get("/jdvop/admin/custom/fenxiao/getCustomBlocks.php", {id: blockId}, function(d) {
                if (typeof d === 'object')
                {
                    var appendHTML = '<span>建议广告图片尺寸&nbsp;<i class="red">160px * 160px</i></span>';
                    if(typeof(d.length) !== 'undefined' && d.length > 0) {
                        image_count = d.length;
                        for(i = 0; i < d.length; ++i) {
                            appendHTML += '<li class="clearfix js-li" id="li_'+ i +'">';
                            appendHTML += '<span class="btn-del js-img-btnDel" title="删除"></span>';
                            appendHTML += '<table><tr><td valign="top">';
                            appendHTML += '<div class="img">';
                            appendHTML += '<input type="hidden" name="data[]" value="'+ d[i].data +'"/>';
                            appendHTML += '<img src="'+ _system_image_host + d[i].data +'" class="js-upload" width="190" height="100" rev="navigation"/>';
                            appendHTML += '<div class="again-up js-upload" rev="navigation">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                            appendHTML += '</div></td>';
                            appendHTML += '<td class="pl30">';
                            appendHTML += '<div>链接：';
                            appendHTML += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                            if(d[i].selectFlag === 'product') {
                                appendHTML += '<option value="product" selected>产品中心</option>';
                                appendHTML += '<option value="cate">商品分类</option>';
                                appendHTML += '<option value="custom">自定义</option></select></div>';
                                appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                            } else if(d[i].selectFlag === 'cate') {
                                appendHTML += '<option value="product">产品中心</option>';
                                appendHTML += '<option value="cate" selected>商品分类</option>';
                                appendHTML += '<option value="custom">自定义</option></select></div>';
                                appendHTML += '<div class="mt10 js-cate">分类：';
                                appendHTML += '<select class="common h32 w120 js-child-cates" name="partner_cate[]">';
                                appendHTML += '<option value="-">--请选择--</option>';
                                for(j = 0; j < cates.length; ++j) {
                                    if(cates[j].partnerId === 0) {
                                        if(d[i].partner_cate === cates[j].id) {
                                            appendHTML += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                        } else {
                                            appendHTML += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                        }
                                    }
                                }
                                appendHTML += '</select>';
                                appendHTML += '&nbsp;<select class="common h32 w140" name="child_cate[]">';
                                appendHTML += '<option value="-">--请选择--</option>';
                                for(var j = 0; j < cates.length; ++j) {
                                    if(cates[j].partnerId !== 0 && d[i].partner_cate === Number(cates[j].partnerId)) {
                                        if(d[i].child_cate === cates[j].id) {
                                            appendHTML += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                        } else {
                                            appendHTML += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                        }
                                    }
                                }
                                appendHTML += '</select>';
                                appendHTML += '</div> ';
                                appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                            } else {
                                appendHTML += '<option value="product">产品中心</option>';
                                appendHTML += '<option value="cate">商品分类</option>';
                                appendHTML += '<option value="custom" selected>自定义</option></select>';
                                appendHTML += '</div> ';
                                appendHTML += '<div class="mt10 js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]" value="'+ d[i].linkTo +'"/></div>';
                            }
                            appendHTML += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30" value="'+ d[i].description +'"/></div>';
                            appendHTML += '</td></tr></table>';
                            appendHTML += '</li>';
                        }
                    } else {
                        image_count = 2;
                        for(var i=0;i < 2; i++) {
                            appendHTML += '<li class="clearfix js-li">';
                            appendHTML += '<span class="btn-del js-img-btnDel" title="删除"></span>';
                            appendHTML += '<table><tr><td valign="top">';
                            appendHTML += '<div class="img">';
                            appendHTML += '<input type="hidden" name="data[]" />';
                            appendHTML += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="100" rev="navigation"/>';
                            appendHTML += '<div class="again-up js-upload" rev="navigation">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                            appendHTML += '</div></td>';
                            appendHTML += '<td class="pl30">';
                            appendHTML += '<div>链接：';
                            appendHTML += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                            appendHTML += '<option value="product">产品中心</option>';
                            appendHTML += '<option value="cate">商品分类</option>';
                            appendHTML += '<option value="custom">自定义</option>';
                            appendHTML += '</select></div> ';
                            appendHTML += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                            appendHTML += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30"/></div>';
                            appendHTML += '</td></tr></table>';
                            appendHTML += '</li>';
                        }
                    }
                    $('li.js-btnAdd').before(appendHTML);
                }
            }, 'json');
            $('input[name=bloKey]').val('NAVIGATION_BLOCK');
            $('input[name=blockId]').val(blockId);
            $('#js-edit-block').html(nvaHTML);
        } else if(key === "product") {
            var couponCode = $('#product_' + dd_count).find('#product-module').attr('couponcode')||'';
            var model_title = $('#product_' + dd_count).find('.a-title').text();
            var blockId = $('#product_' + dd_count).find('input[name="blockId[]"]').val();
            var bloKey = $(this).attr('rel');
            var proLen = $(this).attr('snum');
            var proHTML = '<img src="/jdvop/images/admin/ico-arrow2.png" class="img-arrow2"/>';
            proHTML += '<div class="app-sidebar-inner clearfix">';
            proHTML += '<div class="add-commodity-list">';
            proHTML += '<div class="control-group">';
            proHTML += '<label class="control-label">&emsp;&emsp;&emsp;标题：</label>';
            proHTML += '<input class="common w200 h30" type="text" value="'+ model_title +'" name="model_title" />';
            proHTML += '</div>';
            proHTML += '<div class="control-group couponCode">';
            proHTML += '<label class="control-label">优惠券编码：</label>';
            proHTML += '<input class="common w200 h30" type="text" value="'+couponCode+'" name="couponCodes" placeholder="此处黏贴编码，多个优惠券编码逗号分隔" />';
            proHTML += '</div>';
            proHTML += '<div class="control-group uploadBgImage">';
            proHTML += '<label class="control-label">列表背景图：</label>';
            proHTML += '<label class="upload-wrapper"><img src="" /><span class="bg"></span><input  type="file" accept="image/jpeg image/png" name="bgImg" /></label>';
            proHTML += '</div>';
            proHTML += '<div class="control-group mt10">';
            proHTML += '<label class="control-label">&emsp;显示个数：</label>';
            proHTML += '<div class="controls">';
            if(proLen === '12') {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="1"/>1</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="2"/>2</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="6"/>6</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="10"/>10</label>'
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="12" checked/>12</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="18">18</label>';
            } else if(proLen === '18') {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="1"/>1</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="2"/>2</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="6"/>6</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="10"/>10</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="12"/>12</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="18" checked/>18</label>';
            }else if(proLen === '10') {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="1"/>1</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="2"/>2</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="6"/>6</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="10" checked/>10</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="12"/>12</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="18" />18</label>';
            }else if(proLen === '2') {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="1"/>1</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="2" checked/>2</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="6"/>6</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="10"/>10</label>'
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="12"/>12</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="18" />18</label>';
            } else if(proLen === '1') {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="1" checked/>1</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="2" />2</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="6"/>6</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="10"/>10</label>'
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="12"/>12</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="18" />18</label>';
            } else{
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="1"/>1</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="2"/>2</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="6" checked/>6</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="10"/>10</label>'
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="12"/>12</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_num" value="18"/>18</label>';
            }
            proHTML += '</div>';
            proHTML += '</div>';

            proHTML += '<ul class="up_img add-img-wrap clearfix js-showImgList mt20 clearfix">';
            proHTML += '<li class="btn-add tc templet-js-product">';
            proHTML += '<a href="javascript:;" style="display: block; width: 100%; height: 100%;  overflow: hidden;" class="tooltip" title="点击添加商品"><span class="bg"></span></a>';
            proHTML += '</li>';
            proHTML += '</ul>';
            proHTML += '<div class="control-group mt10">';
            proHTML += '<label class="control-label">&emsp;显示方式：</label>';
            proHTML += '<div class="controls">';
            if(bloKey === "PRODUCT_BLOCK_1") {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_check" value="big"/>大图</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_check" value="small" checked="checked">详细列表</label>';
                proHTML += '<label class="radio inline  cup"><input type="radio" name="product_check" value="list">小图</label>';
            } else if(bloKey === "PRODUCT_BLOCK_2") {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_check" value="big" />大图</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_check" value="small">详细列表</label>';
                proHTML += '<label class="radio inline  cup"><input type="radio" name="product_check" value="list" checked="checked">小图</label>';
            } else {
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_check" value="big" checked="checked"/>大图</label>';
                proHTML += '<label class="radio inline pr10 cup"><input type="radio" name="product_check" value="small">详细列表</label>';
                proHTML += '<label class="radio inline  cup"><input type="radio" name="product_check" value="list">小图</label>';
            }
            proHTML += '</div></div>';
            proHTML += '<div class="btn-wrap mt10">&emsp;';
            proHTML += '<span class="btn-orange js-save-btn" rev="products">确定</span>';
            proHTML += '<span class="btn-grey js-del-btn">取消</span>';
            proHTML += '</div>';
            proHTML += '</div></div>';
            $.get("/jdvop/admin/custom/fenxiao/getCustomBlocks.php", {id: blockId}, function(d) {
                if (typeof d === 'object')
                {
                    var appendHTML = '';
                    if(typeof(d.length) !== 'undefined' && d.length > 0) {
                        _currentProduct = d;
                        for (i = 0; i < d.length; ++i)
                        {
                            appendHTML += '<li class="img js-box">';
                            appendHTML += '<input type="hidden" name="product_id[]" value="'+ d[i].id +'" />';
                            if(d[i].ownerType == "jingdong" || d[i].ownerType == "system")
                            {
                                if(d[i].ownerType == "jingdong")
                                {
                                    appendHTML += '<span class=\"ico-jdsm\"> <!--京东icon--> </span>';
                                }
                                appendHTML += '<img src="'+ d[i].thumbnailImage +'" width="90" height="90" alt="'+ d[i].name +'"/>';
                            } else {
                                appendHTML += '<img src="'+ _system_image_host +'/middle'+ d[i].thumbnailImage +'" width="90" height="90" alt="'+ d[i].name +'"/>';
                            }
                            appendHTML += '</li>';
                        }
                    }
                    $('li.templet-js-product').before(appendHTML);
                }
            }, 'json');
            $('input[name=bloKey]').val(bloKey);
            $('input[name=blockId]').val(blockId);
            $('#js-edit-block').html(proHTML);

            //上传背景图片
            $('.upload-wrapper input').on('change',function(e){
                e = e||window.event;
                var file = e.target.files[0];
                var _self = $(this);
                var form = new FormData();
                form.append('imageFile',file);
                $.ajax({
                    url:'/jdvop//admin/lottery/image-upload.php',
                    type:'post',
                    data:form,
                    processData:false,
                    contentType:false,
                    success:function(res){
                        res = JSON.parse(res);
                        if(res.isOk){
                            $('.uploadBgImage').attr('picUrl',res.message);
                            var src = window.URL.createObjectURL(file);
                            $('.upload-wrapper img').attr('src',src).show();
                        }
                    }
                })
            })
        }
        $('#js-edit-block').css({"margin-top":topOff+"px"});
    });

    /*========================================================== 编辑模板end ==================================================================*/
    //添加内容
    $(document).on("click", 'span.js-addContent', function(){
        dd_count = $(this).parents().find('span.js-btnEdit').attr('rev');
        var topOff=$(this).parents(".js-dragsort").offset().top-120;
        var html = '<img src="/jdvop/images/admin/ico-arrow2.png" class="img-arrow2"/>';
        html += '<div class="app-sidebar-inner clearfix">';
        html += '<h2 class="title">添加内容</h2>';
        html += '<div class="app-mob">';
        html += '<p>点击添加内容：</p>';
        html += '<ul class="clearfix">';
        html += '<li class="js-add-slider">图片广告</li>';
        html += '<li class="js-add-search">商品搜索</li>';
        html += '<li class="js-add-notice">公告栏</li>';
        html += '<li class="js-add-product">商品列表</li>';
        html += '<li class="js-add-nav">导航按钮</li>';
        //html += '<li>富文本</li>';
        html += '</ul>';
        html += '</div>';
        html += '</div>';
        _addElementId = $(this).parents('dd').attr('id');
        $('#js-edit-block').html(html);
        $('#js-edit-block').css({"margin-top":topOff+"px"});
    });



    //删除模块
    $(document).on("click", 'span.js-btnDel', function(){
        var blockId = $(this).parents().find('input[name="blockId[]"]').val();
        if(blockId !== "") {
            $.get("/jdvop/admin/custom/fenxiao/delete.php", {id: blockId}, function(d) {
                if (!d.isOk)  {
                    top.artAlert('操作失败', d.message);
                }
            }, 'json');
        }
        image_count = 2;
        $(this).parents("dd").remove();
        var id = $(this).parents().find('span.js-btnEdit').attr('rev');
        if(dd_count !== '' && typeof(dd_count) !== 'undefined') {
            if(dd_count === id) {
                $('#js-edit-block').html('');
            }
        } else {
            $('#js-edit-block').html('');
        }
    });
    //产品列表模板切换
    $(document).on("click", 'input[name="product_check"]', function() {
        var thiz = $(this);
        var len = $('#product_' + dd_count).find('.js-product-ul li').length;
        var html = '';
        html += '<li><p class="img"><img src="/jdvop/images/admin/wx/test-img.jpg" /></p>';
        html += '<p class="text"><span class="name js-name">Dibea/地贝 智能扫地机器人家用智能吸尘器 </span>单价：<em class="price red js-price">6690</em></p></li>';
        if(thiz.val() === "big") {
            $('#product_' + dd_count).find('.js-btnEdit').attr('rel', 'PRODUCT_BLOCK_4');
            $('#form2').find('input[name="bloKey"]').val('PRODUCT_BLOCK_4');
            $('#product_' + dd_count).find('#product-module').removeClass().addClass('a-module-5 layout-grid2 clearfix');
        } else if(thiz.val() === "small") {
            $('#product_' + dd_count).find('.js-btnEdit').attr('rel', 'PRODUCT_BLOCK_1');
            $('#form2').find('input[name="bloKey"]').val('PRODUCT_BLOCK_1');
            $('#product_' + dd_count).find('#product-module').removeClass().addClass('a-module-2 layout-list');
        } else {
            $('#product_' + dd_count).find('.js-btnEdit').attr('rel', 'PRODUCT_BLOCK_2');
            $('#form2').find('input[name="bloKey"]').val('PRODUCT_BLOCK_2');
            $('#product_' + dd_count).find('#product-module').removeClass().addClass('a-module-3 layout-grid2 clearfix');
            if(len < 4) {
                $('#product_' + dd_count).find('.js-product-ul li:last-child').after(html);
            }
        }
        tHeightImg();
    });
    /*-----------------------------------轮播单个图片切换star---------------------------------------------*/
    $(document).on('click', 'input[name="showType"]', function(){
        var html = "";
        if($(this).val() === "image") {
            $('.js-slider-size').addClass('hide');
            html += '<span>请上传尺寸宽度为&nbsp;<i class="red">640px</i>&nbsp;的广告图片，高度不限</span>';
            if(_currentImage !== '' && typeof(_currentImage) !== 'undefined') {
                html += '<li class="clearfix js-li">';
                html += '<table><tr><td valign="top">';
                html += '<div class="img">';
                html += '<input type="hidden" name="data[]" value="'+ _currentImage.data +'"/>';
                html += '<img src="'+ _system_image_host + _currentImage.data +'" class="js-upload" width="190" height="100" rev="image"/>';
                html += '<div class="again-up js-upload" rev="image">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                html += '</div>';
                html += '</td>';
                html += '<td class="pl30">';
                html += '<div>链接：';
                html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                if(_currentImage.selectFlag === 'product') {
                    html += '<option value="product" selected>产品中心</option>';
                    html += '<option value="cate">商品分类</option>';
                    html += '<option value="custom">自定义</option></select>';
                    html += '</div> ';
                    html += '<div class="mt10 js-customLink hide">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                } else if(_currentImage.selectFlag === 'cate') {
                    html += '<option value="product">产品中心</option>';
                    html += '<option value="cate" selected>商品分类</option>';
                    html += '<option value="custom">自定义</option>';
                    html += '</select></div>';
                    html += '<div class="mt10 js-cate">分类：';
                    html += '<select class="common h32 w120 js-child-cates" name="partner_cate[]">';
                    for(j = 0; j < cates.length; ++j) {
                        if(cates[j].partnerId === 0) {
                            if(_currentImage.child_cate === cates[j].id) {
                                html += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                            } else {
                                html += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                            }
                        }
                    }
                    html += '</select>';
                    html += '&nbsp;<select class="common h32 w140" name="child_cate[]">';
                    html += '<option value="-">--请选择--</option>';
                    for(var j = 0; j < cates.length; ++j) {
                        if(cates[j].partnerId !== 0 && _currentImage.partner_cate === cates[j].partnerId) {
                            if(_currentImage.child_cate === cates[j].id) {
                                html += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                            } else {
                                html += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                            }
                        }
                    }
                    html += '</select></div> ';
                    html += '<div class="mt10 js-customLink hide">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                } else {
                    html += '<option value="product">产品中心</option>';
                    html += '<option value="cate">商品分类</option>';
                    html += '<option value="custom" selected>自定义</option></select>';
                    html += '</div> ';
                    html += '<div class="mt10 js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]" value="'+ _currentImage.linkTo +'"/></div>';
                }
                html += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30" value="'+ _currentImage.description +'"/></div>';
                html += '</td></tr></table>';
                html += '</li>';
            } else {
                html += '<li class="clearfix js-li">';
                html += '<table><tr><td valign="top">';
                html += '<div class="img">';
                html += '<input type="hidden" name="data[]"/>';
                html += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="100" rev="image"/>';
                html += '<div class="again-up js-upload" rev="image">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                html += '</div></td>';
                html += '<td class="pl30">';
                html += '<div>链接：';
                html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                html += '<option value="product">产品中心</option>';
                html += '<option value="cate">商品分类</option>';
                html += '<option value="custom">自定义</option>';
                html += '</select></div> ';
                html += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w260 h30" name="linkTo[]"/></div>';
                html += '<div class="mt10">备注：<input type="text" name="description[]" class="common w260 h30"/></div>';
                html += '</td></tr></table>';
                html += '</li>';
            }
            $('input[name=bloKey]').val('IMAGE_BLOCK');
            $('#slider_' + dd_count).find('span.js-btnEdit').attr('key', 'image');
            $(this).parents().find('ul.js-add-img').html(html);
            $(this).parents().find('span.js-save-btn').attr('rev', 'image');
            $('#form2').find('ul.js-add-img li:last-child').attr('rev', 'image');
        } else {
            $('.js-slider-size').removeClass('hide');
            if(_currentSlider !== '' && typeof(_currentSlider) !== 'undefined') {
                for (var i = 0; i < _currentSlider.length; i++)
                {
                    html += '<li class="clearfix js-li">';
                    html += '<span class="btn-del js-img-btnDel" title="删除"></span>';
                    html += '<table><tr><td valign="top">';
                    html += '<div class="img">';
                    html += '<input type="hidden" name="data[]" value="'+ _currentSlider[i].data +'"/>';
                    html += '<img src="'+ _system_image_host + _currentSlider[i].data +'" class="js-upload" width="190" height="100" rev="slider"/>';
                    html += '<div class="again-up js-upload" rev="slider">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                    html += '</div></td>';
                    html += '<td class="pl30">';
                    if(_currentSlider[i].selectFlag === 'product') {
                        html += '<div>链接：';
                        html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                        html += '<option value="product" selected>产品中心</option>';
                        html += '<option value="cate">商品分类</option>';
                        html += '<option value="custom">自定义</option></select>';
                        html += '</div> ';
                        html += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                    } else if(_currentSlider[i].selectFlag === 'cate') {
                        html += '<div>链接：';
                        html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                        html += '<option value="product">产品中心</option>';
                        html += '<option value="cate" selected>商品分类</option>';
                        html += '<option value="custom">自定义</option>';
                        html += '</select></div>';
                        html += '<div class="mt10 js-cate">分类：';
                        html += '<select class="common h32 w120 js-child-cates" name="partner_cate[]">';
                        html += '<option value="-">--请选择--</option>';
                        for(j = 0; j < cates.length; ++j) {
                            if(cates[j].partnerId === 0) {
                                if(_currentSlider[i].partner_cate === cates[j].id) {
                                    html += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                } else {
                                    html += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                }
                            }
                        }
                        html += '</select>';
                        html += '&nbsp;<select class="common h32 w140" name="child_cate[]">';
                        html += '<option value="-">--请选择--</option>';
                        if(_currentSlider[i].child_cate > 0) {
                            for(var j = 0; j < cates.length; ++j) {
                                if(cates[j].partnerId !== 0 && _currentSlider[i].partner_cate === cates[j].partnerId) {
                                    if(_currentSlider[i].child_cate === cates[j].id) {
                                        html += '<option value="'+ cates[j].id +'" selected>'+ cates[j].name + '</option>';
                                    } else {
                                        html += '<option value="'+ cates[j].id +'">'+ cates[j].name + '</option>';
                                    }
                                }
                            }
                        }
                        html += '</select>';
                        html += '</div> ';
                        html += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                    } else {
                        html += '<div>链接：';
                        html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                        html += '<option value="product">产品中心</option>';
                        html += '<option value="cate">商品分类</option>';
                        html += '<option value="custom" selected>自定义</option></select>';
                        html += '</div>';
                        html += '<div class="mt10 js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]" value="'+ _currentSlider[i].linkTo +'"/></div>';
                    }
                    html += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30" value="'+ _currentSlider[i].description +'"/></div>';
                    html += '</td></tr></table>';
                    html += '</li>';
                }
            } else {
                for(i = 0; i < 2; ++i) {
                    html += '<li class="clearfix js-li">';
                    html += '<span class="btn-del js-img-btnDel" title="删除"></span>';
                    html += '<table><tr><td valign="top">';
                    html += '<div class="img">';
                    html += '<input type="hidden" name="data[]"/>';
                    html += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="100" rev="slider"/>';
                    html += '<div class="again-up js-upload" rev="slider">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
                    html += '</div></td>';
                    html += '<td class="pl30">';
                    html += '<div>链接：';
                    html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
                    html += '<option value="product">产品中心</option>';
                    html += '<option value="cate">商品分类</option>';
                    html += '<option value="custom">自定义</option>';
                    html += '</select></div> ';
                    html += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
                    html += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30"/></div>';
                    html += '</td></tr></table>';
                    html += '</li>';
                }
            }
            $('input[name=bloKey]').val('SLIDER_BLOCK');
            $('#slider_' + dd_count).find('span.js-btnEdit').attr('key', 'slider');
            $(this).parents().find('span.js-save-btn').attr('rev', 'slider');

            html += '<li class="btn-add js-btnAdd" rev="slider">';
            html += '<a href="javascript:;" class="tooltip" title="为了更好的展示，选中轮播方式显示，请传图片大小尺寸为640*300px,分开显示则传宽度为640px，高度不限,"> +添加一个广告</a>';
            html += '</li>';
            image_count = 2;
            $(this).parents().find('ul.js-add-img').html(html);
        }
    });
    /*-----------------------------------轮播单个图片切换end---------------------------------------------*/
    //通栏广告 轮播图最多允许添加5个
    $(document).on("click", '.js-btnAdd', function() {
        var key = $(this).attr('rev');
        var html = '';
        html += '<li class="clearfix js-li">';
        html += '<span class="btn-del js-img-btnDel" title="删除"></span>';
        html += '<table><tr><td valign="top">';
        html += '<div class="img">';
        html += '<input type="hidden" name="data[]"/>';
        if(key === "slider") {
            html += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="100" rev="slider"/>';
            html += '<div class="again-up js-upload" rev="slider">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
        } else {
            html += '<img src="/jdvop/images/admin/wx/morenimg1.jpg" class="js-upload" width="190" height="100" rev="navigation"/>';
            html += '<div class="again-up js-upload" rev="navigation">上传图片</div><input type="file" name="Filedata" id="file_upload" class="hide"/>';
        }
        html += '</div></td>';
        html += '<td class="pl30">';
        html += '<div>链接：';
        html += '<select class="common h32 w120" id="js-imgLink" name="cateType[]">';
        html += '<option value="product">产品中心</option>';
        html += '<option value="cate">商品分类</option>';
        html += '<option value="custom">自定义链接</option>';
        html += '</select></div>';
        html += '<div class="mt10 hide js-customLink">路径：<input type="text" class="common w280 h30" name="linkTo[]"/></div>';
        html += '<div class="mt10">备注：<input type="text" name="description[]" class="common w280 h30"/></div>';
        html += '</td></tr></table>';
        html += '</li>';
        if(key === "slider") {
            if(image_count < 10) {
                image_count ++;
                $(this).before(html);
            } else {
                top.artAlert("温馨提示", "您最多可以添加10个图片广告。", 'error');
            }
        } else {
            if(image_count < 4) {
                image_count ++;
                $(this).before(html);
            } else {
                top.artAlert("温馨提示", "您最多可以添加4个图片广告。", 'error');
            }
        }
    });
    /*---------------------------------图片链接切换star-----------------------------------------------*/
    $(document).on("change", "#js-imgLink", function() {
        var thiz = $(this);
        _currentElement = thiz.parents('li');
        if(thiz.val() === "cate") {
            _currentElement.find('.js-customLink').addClass('hide');
            var len = _currentElement.find('.js-cate').size();
            if(len === 0) {
                $.get("/jdvop/admin/custom/fenxiao/cates.php", {}, function(d) {
                    if (typeof d === 'object')
                    {
                        var appendHTML = '<div class="mt10 js-cate">分类：';
                        appendHTML += '<select class="common h32 w120 js-child-cates" name="partner_cate[]">';
                        appendHTML += '<option value="-">--请选择--</option>';
                        for (i = 0; i < d.length; ++i)
                        {
                            if(d[i].partnerId === 0) {
                                appendHTML += '<option value="'+ d[i].id +'">'+ d[i].name + '</option>';
                            }
                        }
                        appendHTML += '</select>';
                        appendHTML += '&nbsp;<select class="common h32 w140" name="child_cate[]">';
                        appendHTML += '<option value="-">--请选择--</option>';
                        appendHTML += '</select>';
                        appendHTML += '</div>';
                        thiz.after(appendHTML);
                    }
                }, 'json');
            } else {
                _currentElement.find('.js-cate').removeClass('hide');
            }
        } else if(thiz.val() === "custom") {
            _currentElement.find('.js-customLink').removeClass('hide');
            if(typeof(_currentElement.find('.js-cate')) !== 'undefined') {
                _currentElement.find('.js-cate').addClass('hide');
            }
        } else {
            _currentElement.find('.js-customLink').addClass('hide');
            if(typeof(_currentElement.find('.js-cate')) !== 'undefined') {
                _currentElement.find('.js-cate').addClass('hide');
            }
        }
    });
    /*---------------------------------图片链接切换end-----------------------------------------------*/
    $(document).on("change", ".js-child-cates", function(){
        var cateId = $(this).val();
        if(_currentElement === '' || typeof(_currentElement) === 'undefined') {
            _currentElement = $(this).parents('li');
        }
        if(cateId > 0) {
            $.get("/jdvop/admin/custom/fenxiao/childCates.php", {cateId: cateId}, function(data) {
                if (typeof data === 'object')
                {
                    var len = data.length;
                    _currentElement.find('select[name="child_cate[]"]').empty();
                    var option = '<option value="-">--请选择--</option>';
                    if(len > 0){
                        for (var i = 0; i < len; ++i) {
                            option += '<option value="' + data[i].id + '">' + data[i].name + "</option>";
                        }
                    }
                    _currentElement.find('select[name="child_cate[]"]').append(option);
                    _currentElement = '';
                }
            }, 'json');
        }
    });
    $(document).on("click", ".js-img-btnDel", function() {//删除图片
        if(image_count <= 2) {
            top.artAlert("温馨提示", "至少配置两条广告", 'error');
            return false;
        }
        image_count --;
        $(this).parent("li").remove();
    });
    $(document).on("click", ".js-upload", function() {//上传图片
        _currentElement = $(this);
        var    key = $(this).attr('rev');
        var height = 300;
        _fileuploadURL = "/jdvop/admin/custom/fenxiao/upload.php?width=640&height=" + height + "&type=" + key;
        $('#file_upload').click();
    });
    $(document).on("click", "#file_upload", function() {
        $('#file_upload').fileupload({
            url: _fileuploadURL,
            dataType: 'json',
            autoUpload: true,
            done: function(e, data) {
                if (!data.result.isOk) {
                    top.artAlert("数据错误", data.result.message, 'error');
                } else {
                    var p = _currentElement.parents('li');
                    p.find('img.js-upload').attr('src', _system_image_host + data.result.message);
                    p.find('input[name="data[]"]').val(data.result.message);
                }
            }
        });
    });
    //取消模板
    $(document).on('click', '.js-del-btn', function(){
        $('#js-edit-block').html('');
    });
    //保存模板配置
    $(document).on("click", '.js-save-btn', function(){
        var blockType = $(this).attr('rev');
        if (blockType === "text") {
            var text = $('#notice_content').val();
            if(text === '') {
                top.artAlert("错误提示", "您还有编辑公告栏信息！", 'error');
                return false;
            }
            $.get('/jdvop/admin/custom/fenxiao/modifyText.php?type=edit', $('#form2').serialize(), function(data){
                if(data.isOk) {
                    $('#notice_' + dd_count).find('#textMove').text(text + text);
                    $('#notice_' + dd_count).find('input[name=noticeContent]').attr('value', text);
                    $('#notice_' + dd_count).find('input[name="blockId[]"]').val(data.message.blockId);
                    top.artAlert("温馨提示", "已保存配置！", 'ok');
                }
            }, 'json');
        } else if(blockType === "navigation") {
            var _navigationElement = '';
            var thiz = $('#js-edit-block');
            var isOk = false;
            thiz.find('input[name="data[]"]').each(function() {
                if($(this).val() === '') {
                    isOk = false;
                } else {
                    _navigationElement += '<div class="flex1 list"><img src="'+ _system_image_host + $(this).val() +'"/></div>';
                    isOk = true;
                }
            });
            if(isOk) {
                $.get('/jdvop/admin/custom/fenxiao/modifySlider.php?type=edit', $('#form2').serialize(), function(data){
                    if(data.isOk) {
                        $('#nav_' + dd_count).find('input[name="blockId[]"]').val(data.message.blockId);
                        $('#nav_' + dd_count).find('.js-nav-div').html(_navigationElement);
                        _navigationElement = '';
                        top.artAlert("温馨提示", "已保存配置！", 'ok');
                    }
                }, 'json');
            } else {
                top.artAlert("错误提示", "您还有未上传的图片，请上传后提交", 'error');
            }
        } else if(blockType === "slider") {
            var _sliderElement = '';
            var thiz = $('#js-edit-block');
            var isOk = false;
            if($('#slider_' + dd_count).find('.js-slider-ul').size() === 0) {
                _sliderElement += '<div class="main-banner js-main-banner">';
                _sliderElement += '<ul class="swiper-wrapper js-slider-ul">';
                thiz.find('input[name="data[]"]').each(function() {
                    if($(this).val() === '') {
                        isOk = false;
                    } else {
                        _sliderElement += '<li class="swiper-slide"><img src="'+ _system_image_host + $(this).val() +'"></li>';
                        isOk = true;
                    }
                });
                _sliderElement += '</ul>';
                _sliderElement += '<div class=\"swiper-pagination\"></div>';
                _sliderElement += '</div>';
            } else {
                thiz.find('input[name="data[]"]').each(function() {
                    if($(this).val() === '') {
                        isOk = false;
                    } else {
                        _sliderElement += '<li class="swiper-slide"><img src="'+ _system_image_host + $(this).val() +'"></li>';
                        isOk = true;
                    }
                });
            }
            if(isOk) {
                $.get('/jdvop/admin/custom/fenxiao/modifySlider.php?type=edit', $('#form2').serialize(), function(data){
                    if(data.isOk) {
                        $('#slider_' + dd_count).find('div.swiper-pagination').removeClass('hide');
                        $('#slider_' + dd_count).find('input[name="blockId[]"]').val(data.message.blockId);
                        if($('#slider_' + dd_count).find('.js-slider-ul').size() === 0) {
                            $('#slider_' + dd_count).find('.alone-img').remove();
                            $('#slider_' + dd_count).find('.actions-wrap').before(_sliderElement);
                        } else {
                            $('#slider_' + dd_count).find('.js-slider-ul').html(_sliderElement);
                        }
                        swiper1();
                        _sliderElement = '';
                        top.artAlert("温馨提示", "已保存配置！", 'ok');
                    }
                }, 'json');
            } else {
                top.artAlert("错误提示", "您还有未上传的图片，请上传后提交", 'error');
            }
        } else if(blockType === "products") {
            var quan_code = $('.couponCode input').val();
            var bgImage = $('.uploadBgImage').attr('picUrl')||'';
            var model_title = $('#form2').find('input[name=model_title]').val();
            var spacialActivityId = $('#form1').find('input[name="specialActivityId"]').val()||null;

            //取出勾选的显示个数
            var product_num = $('input[name=product_num]:checked').val();
            if(model_title == '')
            {
                top.artAlert("错误提示", "请为模板编辑标题！", 'error');
                return false;
            }
            var product = _currentProduct;
            var appendHTML = '';
            if(_currentProduct !== '' && typeof(_currentProduct) !== 'undefined') {
                for (var i = 0; i < product.length; ++i) {
                    appendHTML += '<li>';
                    appendHTML += '<p class="img">';
                    if(product[i].ownerType == "jingdong" || product[i].ownerType == "system")
                    {
                        if(product[i].ownerType == "jingdong")
                        {
                            appendHTML += '<span class=\"ico-jdsm\"> <!--京东icon--> </span>';
                        }
                        appendHTML += '<img src="'+ product[i].thumbnailImage +'" class="js-img"/>';
                    } else {
                        appendHTML += '<img src="'+ _system_image_host +'/middle'+ product[i].thumbnailImage +'" class="js-img"/>';
                    }
                    appendHTML += '</p>';
                    appendHTML += '<p class="text"><span class="name js-name">'+ product[i].name +' </span><em class="price red js-price">&yen;'+ toDecimal(product[i].retailPrice) +'</em></p></li>';
                }
            } else {
                top.artAlert("错误提示", "请为模板添加产品！", 'error');
                return false;
            }
            //判断勾选的显示个数跟添加的产品数量是否一致  不一致的话 给出提示
            if(product.length != product_num){
                top.artAlert("错误提示","勾选的显示个数和产品个数不一样！",'error');
                return false;
            }
            $.get('/jdvop/admin/custom/fenxiao/modifyProductBox.php?type=edit&backImag='+bgImage+(spacialActivityId?('&specialActivityId='+spacialActivityId):''), $('#form2').serialize(), function(data){
                if(data.isOk) {
                    var len = $('input[name=product_num]:checked').val();
                    $('#product_' + dd_count).find('.a-title').text(model_title);
                    $('#product_' + dd_count).find('.js-btnEdit').attr('snum', len);
                    $('#product_' + dd_count).find('input[name="blockId[]"]').val(data.message.blockId);
                    $('#product_' + dd_count).find('ul.js-product-ul').html(appendHTML);
                    tHeightImg();
                    top.artAlert("温馨提示", "已保存配置！", 'ok');
                }else{
                    top.artAlert('温馨提示', data.message, 'error');
                }
            }, 'json');
        } else if(blockType === "image") {
            var _imageElement = '';
            var d = $('#js-edit-block').find('input[name="data[]"]').val();
            if(d === '') {
                top.artAlert("数据错误", "您还未上传图片，请上传后提交", 'error');
                return false;
            } else {
                if($('#slider_' + dd_count).find('.js-slider-ul').size() > 0) {
                    $('#slider_' + dd_count).find('.js-main-banner').remove();
                }
                if($('#slider_' + dd_count).find('.alone-img').size() > 0) {
                    $('#slider_' + dd_count).find('.alone-img').remove();
                }
                _imageElement = '<div class="alone-img"><img src="'+ _system_image_host + d +'"></div>';
            }
            $.get('/jdvop/admin/custom/fenxiao/modifyImage.php?type=edit', $('#form2').serialize(), function(data){
                if(data.isOk) {
                    $('#slider_' + dd_count).find('div.swiper-pagination').addClass('hide');
                    $('#slider_' + dd_count).find('input[name="blockId[]"]').val(data.message.blockId);
                    $('#slider_' + dd_count).find('.actions-wrap').before(_imageElement);

                    top.artAlert("温馨提示", "已保存配置！", 'ok');
                }
            }, 'json');
        }
    });

    function tHeightImg() {
        $('.js-img').each(function() {
            /*产品图片的高度*/
            var tHeight = $(this).width();
            $(this).height(tHeight);
        });
    }
    //产品广告选择
    $(document).on('click', '.templet-js-product', function(e){
        e.preventDefault();
        _currentElement = $(this);
        var _chooseProductURL = "/jdvop/admin/website/block/chooseFenxiaoProducts.php?";
        var size = _currentElement.parents().find('input[name="product_num"]:checked').val();
        _chooseProductURL += "&minSize=" + size;
        _chooseProductURL += "&maxSize=" + size;
        //专题活动ID
        var specialId = _currentElement.parents().find('input[name="specialActivityId"]').val();
        if(specialId != undefined || specialId != null || specialId > 0){
            _chooseProductURL += "&specialId="+specialId;
        }
        var pids = "";
        _currentElement.parents('ul.js-showImgList').find("li.js-box").each(function(){
            var pid = $(this).find('input[name="product_id[]"]').val();
            if(!isNaN(pid) && pid > 0){
                pids += "&id[]=" + pid;
            }
        });
        _chooseProductURL += pids;
        window.open(_chooseProductURL, 'chooseWindow', 'height=760, width=1100, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no');
    });

    //保存配置信息
    $(document).on("click", '.js-submit-btn', function(e){
        e.preventDefault();
        $.post($('#form1').attr('action'), $('#form1').serialize(), function(data){
            if(data.isOk) {
                var specialActivityId = $('input[name="specialActivityId"]').val();
                //如果是首页保存的  则跳转到首页位置，如果是专题活动保存的 则跳转到编辑页面
                var url = $('#form1').attr('action');
                if(url === "/jdvop/admin/custom/fenxiao/save.php"){
                    top.artAlert("温馨提示", "您的配置保存成功！", 'ok');
                    location = "/jdvop/admin/fenxiaotemplate.php"; //首页保存跳转路径
                }else if(url === "/jdvop/admin/special/save.php"){
                    location = "/jdvop/admin/specialList.php"; //专题活动保存跳转到专题活动列表
                }else{
                    top.artAlert("温馨提示", "您的配置保存成功！", 'ok');
                    //专题活动编辑 跳转到编辑活动的页面
                    location = "/jdvop/admin/special/edit.php?specialActivityId="+specialActivityId;
                }
            } else {
                top.artAlert("数据错误", data.message, 'error');
            }
        }, 'json');
    });
});
// ***********************************************回调函数***************************************************
function callBackFunForProducts(data){
    var html = '';
    _currentProduct = data;
    for (i = 0; i < data.length; ++i) {
        var product = _currentProduct[i];
        html += '<li class="img js-box">';
        html += '<input type="hidden" name="product_id[]" value="'+ product.id +'" />';
        if(product.ownerType == "jingdong" || product.ownerType == "system")
        {
            if(product.ownerType == "jingdong")
            {
                html += '<span class=\"ico-jdsm\"> <!--京东icon--> </span>';
            }
            html += '<img src="'+ product.thumbnailImage +'" width="90" height="90" alt="'+ product.name +'"/>';
        } else {
            html += '<img src="'+ _system_image_host +'/middle'+ product.thumbnailImage +'" width="90" height="90" alt="'+ product.name +'"/>';
        }
        html += '</li>';
    }
    _currentElement.parents().find('li.js-box').remove();
    _currentElement.before(html);
}
//
//保留两位小数 （保留2位小数，如：2，会在2后面补上00.即2.00 ）
function toDecimal(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return false;
    }
    var f = Math.round(x*100)/100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + 2) {
        s += '0';
    }
    return s;
}