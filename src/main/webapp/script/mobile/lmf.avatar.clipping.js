clipping_obj={
    file_upload:function(){
        $('input.js-image-upload').fileupload({
            url        : '/jdvop/mobile/user/image/uploead.php',
            dataType   : 'json',
            autoUpload : true,
            singleFileUploads  : true,
            done        : function(e, data) {
                if (!data.result.isOk)
                {
                    mui.alert(data.result.message, '提示', function() {});  
                    return false;
                }else{
                    $("#js-face-image").attr("src", data.result.message);
                }
            }
        });
    },
    cropper_img_fun:function(){
        (function(factory) {
            if (typeof define === 'function' && define.amd) {
                define(['jquery'], factory);
            } else if (typeof exports === 'object') {
                factory(require('jquery'));
            } else {
                factory(jQuery);
            }
        })(function($) {
            function cropAvatar($element) {
                this.$cont = $element;
                this.$btnUpload = this.$cont.find('.js-image-upload'); //上传按钮
                this.$maxImg = this.$cont.find('.js-max-img'); // 上传大图显示
                this.$avatarForm = this.$cont.find('.js-avatar-form'); //form
                this.init();
            }
            cropAvatar.prototype = {
                support: {
                    fileList: !!$('<input type="file">').prop('files'),
                    blobURLs: !!window.URL && URL.createObjectURL,
                    formData: !!window.FormData
                },
                init: function() {
                    this.support.datauri = this.support.fileList && this.support.blobURLs;
                    if (!this.support.formData) {
                        this.initIframe();
                    }
                    this.addListener();
                },
                addListener: function() {
                    this.$btnUpload.on('change', $.proxy(this.change, this));
                    this.$avatarForm.on('submit', $.proxy(this.submit, this)); // 提交表单-18
                },
                change: function(){
                    $(".js-mobile-cropper").removeClass("hide");
                    $(".js-box-wrap").hide();
                    var files;
                    var file;
                    if (this.support.datauri) {
                        files = this.$btnUpload.prop('files');
                        if (files.length > 0) {
                            file = files[0];
                            if (this.isImageFile(file)) {
                                if (this.url) {
                                    URL.revokeObjectURL(this.url); // 撤销旧的
                                }
                                this.url = URL.createObjectURL(file);
                                this.startCropper();
                            };
                        };
                    } else {
                        file = this.$btnUpload.val();
                        if (this.isImageFile(file)) {
                            this.syncUpload();
                        }
                    }
                },
                submit: function (e) { // 提交表单-18
                    e.preventDefault();
                    var uploadimg =$("#uploadImg").val();
                    if(uploadimg===undefined||uploadimg===""){
                        mui.alert('请选择图片', '温馨提示', function() {}); 
                        return false;
                    }
                    
                    var cropperImg= $("#showImg"); //要截取的图片
                    var cutData=cropperImg.cropper("getData"); //获取图片截取后的数据
                    var x=Math.round(cutData.x),y=Math.round(cutData.y), width=Math.round(cutData.width),height=Math.round(cutData.height);
                    $(".js-avatar-x").val(x);
                    $(".js-avatar-y").val(y);
                    $(".js-avatar-width").val(width);
                    $(".js-avatar-height").val(height);
                    $(".js-upload-img").addClass("disabled").html("截图中...");
                     $('.js-avatar-form').ajaxSubmit({
                          type:"post",
                          url:"/jdvop/user/cutHeadImage.php",
                          success: function(data){  
                              var objData =eval('(' + data + ')');
                                if(objData.isOk){
                                    $("#js-face-image").attr("src", objData.message);
                                    $(".js-mobile-cropper").addClass("hide");
                                    $(".js-box-wrap").show();
                                    $(".js-upload-img").removeClass("disabled").html("确定");
                                }else{
                                    mui.alert(objData.message, '温馨提示', function() {}); 
                                    $(".js-upload-img").removeClass("disabled").html("确定");
                                    return false;
                                }
                             $("#uploadImg").val("");
                          },  
                          error: function(XmlHttpRequest, textStatus, errorThrown){  
                               mui.alert('图片上传失败', '温馨提示', function() {}); 
                               $(".js-upload-img").removeClass("disabled").html("确定");
                              $("#uploadImg").val("");
                              return false;
                          } 
                    });
                },
                isImageFile: function(file) {
                    if (file.type) {
                        return /^image\/\w+$/.test(file.type);
                    } else {
                        return /\.(jpg|jpeg|png|gif)$/.test(file);
                    }
                },
                startCropper: function(){
                    var _this = this;
                    if (this.active){
                        this.$img.cropper('replace', this.url);
                    } else {
                        this.$img = $('<img src="' + this.url + '" id="showImg">');
                        this.$maxImg.empty().html(this.$img);
                        this.$img.cropper({
                            viewMode:1,
                            aspectRatio: 1,
                            //movable:false,
                            dragCrop:false,
                            minCropBoxWidth:120,
                            minCropBoxHeight:120,
                            autoCropArea:0.7,
                            cropBoxMovable:false,
                            dragMode:'move',
                            cropBoxResizable:false, //禁用拖动截图区域大小
                            crop: function(e){
                                var json = [
                                    '{"x":' + e.x,
                                    '"y":' + e.y,
                                    '"height":' + e.height,
                                    '"width":' + e.width,
                                    '"rotate":' + e.rotate + '}'
                                ].join();
                                //   _this.$avatarData.val(json);
                                
                            }
                        });
                        this.active = true;
                    }
                },
                stopCropper: function(){
                    if (this.active) {
                        this.$img.cropper('destroy');
                        this.$img.remove();
                        this.active = false;
                    }
                },
                ajaxUpload:function(){
                }
            };
            $(function() {
                return new cropAvatar($('.js-crop-avatar'));
            });
        });
        $(".js-btn-return").on("touchend",function(){
             $(".js-mobile-cropper").addClass("hide");
            $(".js-box-wrap").show();
            $(".js-upload-img").removeClass("disabled").html("确定");
        });
        
        $(".js-upload-img").on("click",function(){
             $(".js-avatar-form").submit();
        });
       
        
        
        
    }
};