var _fileuploadURL;
var _currentElement;    //当前点击元素
var _currentImage;      //当前上传IMG

$(function(){
    //轮播图片上传
    $(".templet-js-slide").bind("click", function(e){
        e.preventDefault();
        $("div.data-box").remove();
        _currentElement = $(this);
        var thiz = $(this);
        var elementKey = thiz.attr("key");
        var maxSize = thiz.attr("max");
        var width   = thiz.attr("width");
        var height  = thiz.attr("height");
        _fileuploadURL = "/jdvop/admin/website/img/upload.php?";
        _fileuploadURL += "&width=" +  width + "&height=" + height;
        var oldImgs     = []; 
        var oldImgLinks = [];
        var i = 0; var j = 0;
        //收集旧数据
        $(document).find("input[delete='"+ elementKey +"']").each(function(index, element){
            var _elv = $(element).val().replace(elementKey + "#", "");
            if($(element).attr("name") === "image[]"){
                oldImgs[i] = _elv;
                i++;
            }else{
                oldImgLinks[j] = _elv;
                j++;
            } 
        });
        
        var suggestSize = width + " * " + height + "px";
        var dataHtml = '<div class="dialog-b2c-configure dialog-b2c-configure-'+elementKey+'">';
        dataHtml += '<div class="up-banner"><ul>'; 
        for(var i = 0; i < maxSize; i++){
            dataHtml += '<li class="clearfix"><table><colgroup><col style="width: 80px;"/><col/></colgroup>';
            dataHtml += '<tr><td class="tr">图片('+ (i + 1) +')：</td>';
            if(oldImgs.length > 0 && oldImgs.length > i){
                dataHtml += '<td><p class="img"><img src="'+ oldImgs[i] +'" eq="'+ i +'" width="160" height="41" class="image_input_trigger"/><em class="ico-del"></em></p>';
            }else{
                dataHtml += '<td><p class="img"><img src="" eq="'+ i +'" width="160" height="41"/><em class="ico-del"></em></p>';
            }
            dataHtml += '<span class="btn-grey2 mt10 image_input_trigger">上传图片</span>';
            dataHtml += '<div class="topTip-wrap ml6">';
            dataHtml += '<em class="help-title js-topTip-title">图片建议尺寸'+ suggestSize +'</em>';
            dataHtml += '<i class="topTip js-topTip"></i>';
            dataHtml += '</div></td></tr>';
            dataHtml += '<tr><td  class="tr">链接页面：</td>';
            if(oldImgs.length > 0 && oldImgs.length > i){
                dataHtml += '<td><input type="text" name="imgLink" value="'+ oldImgLinks[i]  +'" eq="'+ i +'" class="common w260" placeholder="链接地址"/></td>';
            }else{
                dataHtml += '<td><input type="text" name="imgLink" eq="'+ i +'" class="common w260" placeholder="链接地址"/></td>';
            }
            dataHtml += '</tr></table></li>';
        }
        dataHtml += '</ul></div></div>';
        dialog({
            title: "广告配置(图片建议尺寸："+ suggestSize +")",
            width: 400,
            id: elementKey,
            content: dataHtml,
            okValue: '提交保存',
            cancelValue: '取消',
            ok: function (){
                js_slider_trigger(elementKey);
                this.close();
                return false;
            },
            cancel: function () {
                this.close();// 隐藏
                return false;
            }
        }).showModal();
    });
    
    //轮播图提交保存
    function js_slider_trigger(key){
        var imgUrls  = [];
        var imgLinks = [];
        var dialogBox  = $("div.dialog-b2c-configure-"+key);
        dialogBox.find("img").each(function(index, element){
            imgUrls[index] = $(element).attr("src");
        });
        dialogBox.find("input[name='imgLink']").each(function(index, element){
            imgLinks[index] = $(element).val();
        });
        
        //筛选并校验图片数据
        var initImgUrls = [];
        var initImgLinks = [];
        var flag = 0;
        for(var i = 0; i < imgUrls.length; i++){
            var imgUrl = imgUrls[i];
            if(imgUrl != '' && imgUrl.length > 5){
                var imgLink = imgLinks[i];
                if(imgLink == '' || imgUrl.length <= 0){
                    top.artAlert("数据错误", "您还没有填写图片链接地址");
                    return false;
                }
                initImgUrls[flag]  = imgUrl;
                initImgLinks[flag] = imgLink;
                flag++;
            }
        }
        
        var elementKey = _currentElement.attr("key");
        var maxSize = _currentElement.attr("max");
        var minSize = _currentElement.attr("min");
        if(initImgUrls.length < minSize){
            top.artAlert("数据错误", "此处图片个数不得少于" + minSize + "张");
            return;
        }
        if(initImgUrls.length > maxSize){
            top.artAlert("数据错误", "此处图片个数不得大于" + maxSize + "张");
            return;
        }
        
        //填充数据
        var imgData  = "";
        var linkData = "";
        for(var i = 0; i < initImgUrls.length; i++){
            imgData  += "<input type='hidden' name='image[]' value='" + elementKey + "#" + initImgUrls[i] + "' delete='"+ elementKey +"'/>";
            linkData += "<input type='hidden' name='imageLink[]' value='" + elementKey + "#" + initImgLinks[i] + "' delete='"+ elementKey +"'/>";
        }
        var deleteFlag = "input[delete='"+ elementKey +"']";
        $("#js-form-data").find(deleteFlag).remove();   //清除旧数据
        $("#js-form-data").append(imgData + linkData);
    }
    
    
    //单张广告图上传
    $(".templet-js-image").bind("click", function(e){
        e.preventDefault();
        $("div.data-box").remove();
        _currentElement = $(this);
        var thiz = $(this);
        var elementKey = thiz.attr("key");
        var width   = thiz.attr("width");
        var height  = thiz.attr("height");
        _fileuploadURL = "/jdvop/admin/website/setting/img/upload.php?";
        _fileuploadURL += "&width=" +  width + "&height=" + height;
        
        var oldImg      = ""; 
        var oldImgLink  = "";
        //收集旧数据
        $(document).find("input[delete='"+ elementKey +"']").each(function(index, element){
            var _elv = $(element).val().replace(elementKey + "#", "");
            if($(element).attr("name") === "image[]"){
                oldImg = _elv;
            }else{
                oldImgLink = _elv;
            }
        });
        var suggestSize = width + " * " + height + "px";
        var dataHtml = '<div class="dialog-b2c-configure">';
        dataHtml += '<div class="up-banner"><ul><li class="clearfix">'; 
        dataHtml += '<table><colgroup><col style="width: 80px;"/><col/></colgroup>';
        dataHtml += '<tr><td class="tr">图片(1)：</td>';
        dataHtml += '<td><p class="img"><img src="'+ oldImg +'" width="160" height="41"/><em class="ico-del"></em></p>';
        dataHtml += '<span class="btn-grey2 mt10 image_input_trigger">上传图片</span>';
        dataHtml += '<div class="topTip-wrap ml6">';
        dataHtml += '<em class="help-title js-topTip-title">图片建议尺寸'+ suggestSize +'</em>';
        dataHtml += '<i class="topTip js-topTip"></i>';
        dataHtml += '</div></td></tr>';
        dataHtml += '<tr><td  class="tr">链接页面：</td>';
        dataHtml += '<td><input type="text" name="imgLink" value="'+ oldImgLink  +'" class="common w260" placeholder="链接地址"/></td>';
        dataHtml += '</tr></table></li></ul></div></div>';
        dialog({
            title: "广告配置(图片建议尺寸："+ suggestSize +")",
            width: 400,
            id: elementKey,
            content: dataHtml,
            okValue: '提交保存',
            cancelValue: '取消',
            ok: function (){
                js_image_trigger();
                this.close();
                return false;
            },
            cancel: function () {
                this.close();// 隐藏
                return false;
            }
        }).showModal();
    });
    
    //移除图片
    $(document).on("click", ".dialog-b2c-configure .ico-del", function(){
        $(this).siblings("img").attr("src", "");
        //$(this).parents("li.clearfix").find("input[name='imgLink']").val('');
        var elementKey = _currentElement.attr("key");
        var deleteFlag = "input[delete='"+ elementKey +"']";
        $("#js-form-data").find(deleteFlag).remove();   //清除数据
    });
    
    //提交保存
    function js_image_trigger()
    {
        var dialogBox  = $("div.dialog-b2c-configure");
        var imgUrl  = dialogBox.find("img").attr("src");
        var imgLink = dialogBox.find("input[name='imgLink']").val();
        if(imgUrl == '' || imgUrl.length <= 5){
            top.artAlert("数据错误", "您还没有上传图片");
            return;
        }
        if(imgLink == '' || imgUrl.length <= 0){
            top.artAlert("数据错误", "您还没有填写图片链接地址");
            return;
        }
        var elementKey = _currentElement.attr("key");
        var deleteFlag = "input[delete='"+ elementKey +"']";
        var imgData = "<input type='hidden' name='image[]' value='" + elementKey + "#" + imgUrl + "' delete='"+ elementKey +"'/>";
        var linkData = "<input type='hidden' name='imageLink[]' value='" + elementKey + "#" + imgLink + "' delete='"+ elementKey +"'/>";
        $("#js-form-data").find(deleteFlag).remove();   //清除旧数据
        $("#js-form-data").append(imgData + linkData);
    }
    
    
    //文本内容编辑
    $(".templet-js-text").bind("click", function(e){
        e.preventDefault();
        $("div.data-box").remove();
        _currentElement = $(this);
        var thiz = $(this);
        var elementKey = thiz.attr("key");
        var noLink = thiz.attr("noLink");
        var oldtext     = ""; 
        var oldTextLink = "";
        //收集旧数据
        $(document).find("input[delete='"+ elementKey +"']").each(function(index, element){
            var _elv = $(element).val().replace(elementKey + "#", "");
            if($(element).attr("name") === "text[]"){
                oldtext = _elv;
            }else{
                oldTextLink = _elv;
            }
        });
        var dataHtml = "<div class='data-box'>";
        dataHtml += "<div style='width: 260px;' class='pt10 pb10 mb4'>";
        dataHtml += "<p class='ml6 fwb'>标题内容:<input type='text' name='title' value='"+ oldtext +"' class='common w180' placeholder='请输入标题'/></p>";
        if(noLink === "false"){
            dataHtml += "<p class='ml6 pt10 fwb'>链接地址:<input type='text' name='link' value='"+ oldTextLink +"'  class='common w180' placeholder='请输入链接地址'/></p>";
        }
        dataHtml += "<p class='tc'><span class='btn-blue2 mt10 mr10 js-text-trigger'>确定</span><span class='btn-blue2 mt10 ml10 js-cancel-trigger'>取消</span></p></div></div>";
        var _appendData = $("#append-data");
        _appendData.html(dataHtml);
        _appendData.css({top : window.event.clientY, left : window.event.clientX});
    });
    
    
    //产品广告选择
    $(".templet-js-product").bind("click", function(e){
        e.preventDefault();
        $("div.data-box").remove();
        _currentElement = $(this);
        var thiz = $(this);
        var elementKey = thiz.attr("key");
        var _chooseProductURL = "/jdvop/admin/website/setting/chooseProducts.php?";
        var minSize = thiz.attr("minSize");
        var maxSize = thiz.attr("maxSize");
        _chooseProductURL += "&minSize=" + minSize;
        _chooseProductURL += "&maxSize=" + maxSize;
        var pids = "";
        $("#js-product-data").find("input").each(function(index, element){
            var pid = $(element).val().replace(elementKey + "#", "");
            if(!isNaN(pid) && pid > 0){
                pids += "&id[]=" + pid;
            }
        });
        _chooseProductURL += pids;
        window.open(_chooseProductURL, 'chooseWindow', 'height=760, width=1100, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');  
    });
    
    
    //礼品卡册广告选择
    $(".templet-js-booklet").bind("click", function(e){
         e.preventDefault();
        $("div.data-box").remove();
         _currentElement = $(this);
        var thiz = $(this);
        var elementKey = thiz.attr("key");
        var _chooseBookletURL = "/website/setting/chooseBooklets.php?";
        var websiteId = $("input[name='websiteId']").val();
        var minSize = thiz.attr("minSize");
        var maxSize = thiz.attr("maxSize");
        _chooseBookletURL += "&websiteId=" + websiteId;
        _chooseBookletURL += "&minSize=" + minSize;
        _chooseBookletURL += "&maxSize=" + maxSize;
        var bids = "";
        $("#js-booklet-data").find("input").each(function(index, element){
            var bid = $(element).val().replace(elementKey + "#", "");
            if(!isNaN(bid) && bid > 0){
                bids += "&id[]=" + bid;
            }
        });
        _chooseBookletURL += bids;
        window.open(_chooseBookletURL, 'chooseWindow', 'height=760, width=1100, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');  
    });
    
    
    // ***********************************************common***************************************************
    //关闭数据填充窗口
    $(document).on("click", ".js-cancel-trigger", function(){
        $("div.data-box").remove();
    });
    
    //文本编辑,确定按钮点击事件
    $(document).on("click", ".js-text-trigger", function(){
        var noLink = _currentElement.attr("noLink");
        var elementKey = _currentElement.attr("key");
        var inputData = "";
        var title = $(document).find("input[name='title']").val();
        if(title =='' || title.length <= 0){
            top.artAlert("数据错误", "您还没有填写任何文字");
            return;
        }
        inputData += "<input type='hidden' name='text[]' value='" + elementKey + "#" + title + "' delete='"+ elementKey +"'/>";
        if(noLink === "false"){
            var link  = $(document).find("input[name='link']").val();
            if(link =='' || link.length <= 0){
                top.artAlert("数据错误", "您还没有填写文字链接");
                return;
            }
            inputData += "<input type='hidden' name='textLink[]' value='" + elementKey + "#" + link + "' delete='"+ elementKey +"'/>";
        }
        var deleteFlag = "input[delete='"+ elementKey +"']";
        $("#js-text-data").find(deleteFlag).remove();   //清除旧数据
        $("#js-text-data").append(inputData);
        _currentElement.html(title);
        $("div.data-box").remove();
    });
    
    
    //点击上传图片事件
    $(document).on("click", ".image_input_trigger", function(){
        _currentImage = $(this).siblings("p.img").find("img");
        $('#image_input').click();
        $('#image_input').fileupload({
            url        : _fileuploadURL,
            dataType   : 'json',
            autoUpload : true,
            singleFileUploads  : false,
            done        : function(e, data) {
                if (data.result.isOk)
                {
                    var imgUrl = data.result.message;
                    _currentImage.attr("src", imgUrl);
                }else{
                    top.artAlert("数据错误", data.result.message);
                    return false;
                }
            }
        });
    });
    
    
    //单张图片上传,确定按钮事件
    $(document).on("click", ".js-image-trigger", function()
    {
        var imgUrl  = $(document).find(".image_input_trigger").attr("src");
        var imgLink = $(document).find("input[name='imgLink']").val();
        if(imgUrl == '' || imgUrl.length <= 5){
            top.artAlert("数据错误", "您还没有上传图片");
            return;
        }
        if(imgLink == '' || imgUrl.length <= 0){
            top.artAlert("数据错误", "您还没有填写图片链接地址");
            return;
        }
        var elementKey = _currentElement.attr("key");
        var deleteFlag = "input[delete='"+ elementKey +"']";
        var imgData = "<input type='hidden' name='image[]' value='" + elementKey + "#" + imgUrl + "' delete='"+ elementKey +"'/>";
        var linkData = "<input type='hidden' name='imageLink[]' value='" + elementKey + "#" + imgLink + "' delete='"+ elementKey +"'/>";
        $("#js-form-data").find(deleteFlag).remove();   //清除旧数据
        $("#js-form-data").append(imgData + linkData);
        $("div.data-box").remove();
    });
    
    
    //slider图片上传,确定按钮事件
    $(document).on("click", ".js-slider-trigger", function()
    {
        var imgUrls  = [];
        var imgLinks = [];
        $(document).find(".image_input_trigger").each(function(index, element){
            imgUrls[index] = $(element).attr("src");
        });
        $(document).find("input[name='imgLink']").each(function(index, element){
            imgLinks[index] = $(element).val();
        });
        
        //筛选并校验图片数据
        var initImgUrls = [];
        var initImgLinks = [];
        var flag = 0;
        for(var i = 0; i < imgUrls.length; i++){
            var imgUrl = imgUrls[i];
            if(imgUrl != '' && imgUrl.length > 5){
                var imgLink = imgLinks[i];
                if(imgLink == '' || imgUrl.length <= 0){
                    top.artAlert("数据错误", "您还没有填写图片链接地址");
                    return false;
                }
                initImgUrls[flag]  = imgUrl;
                initImgLinks[flag] = imgLink;
                flag++;
            }
        }
        
        var elementKey = _currentElement.attr("key");
        var maxSize = _currentElement.attr("max");
        var minSize = _currentElement.attr("min");
        if(initImgUrls.length < minSize){
            top.artAlert("数据错误", "此处图片个数不得少于" + minSize + "张");
            return;
        }
        if(initImgUrls.length > maxSize){
            top.artAlert("数据错误", "此处图片个数不得大于" + maxSize + "张");
            return;
        }
        
        //填充数据
        var imgData  = "";
        var linkData = "";
        for(var i = 0; i < initImgUrls.length; i++){
            imgData  += "<input type='hidden' name='image[]' value='" + elementKey + "#" + initImgUrls[i] + "' delete='"+ elementKey +"'/>";
            linkData += "<input type='hidden' name='imageLink[]' value='" + elementKey + "#" + initImgLinks[i] + "' delete='"+ elementKey +"'/>";
        }
        var deleteFlag = "input[delete='"+ elementKey +"']";
        $("#js-form-data").find(deleteFlag).remove();   //清除旧数据
        $("#js-form-data").append(imgData + linkData);
        $("div.data-box").remove();
    });
});

// ***********************************************回调函数***************************************************
function callBackFunForProducts(data){
    var elementKey = _currentElement.attr("key");
    var ulBox = _currentElement.parents("ul");
    var inputData = "";
    ulBox.find(".templet-js-product").each(function(index, element){
        if(index === data.length){
            return;
        }
        var product = data[index];
        var _element = $(element);
        _element.attr("pid", product.id);
        _element.find("a").attr("href", "/product.php?id=" + product.id);
        _element.find(".js-name").text(product.name);
        _element.find(".js-feature").text(product.features);
        _element.find(".js-image").attr("src", "/thumbnail" + product.thumbnailImage);
        _element.find(".js-price").text("¥" + product.retailPrice);
        inputData += "<input type='hidden' name='entry[]' value='" + elementKey + "#" + product.id + "' delete='"+ elementKey +"'/>";
    });
    var deleteFlag = "input[delete='"+ elementKey +"']";
    $("#js-product-data").find(deleteFlag).remove();   //清除旧数据
    $("#js-product-data").append(inputData);
}

function callBackFunForBooklets(data){
    var elementKey = _currentElement.attr("key");
    var ulBox = _currentElement.parents("ul");
    var inputData = "";
    ulBox.find(".templet-js-booklet").each(function(index, element){
        var booklet = data[index];
        var _element = $(element);
        _element.attr("bid", booklet.id);
        _element.find("a").attr("href", "/jdvop/booklet/detail.php?bookletId=" + booklet.id);
        _element.find(".js-name").text(booklet.name);
        _element.find(".js-image").attr("src", booklet.faceImage);
        _element.find(".js-price").text("¥" + booklet.price);
        inputData += "<input type='hidden' name='entry[]' value='" + elementKey + "#" + booklet.id + "' delete='"+ elementKey +"'/>";
    });
    var deleteFlag = "input[delete='"+ elementKey +"']";
    $("#js-booklet-data").find(deleteFlag).remove();   //清除旧数据
    $("#js-booklet-data").append(inputData);
}
