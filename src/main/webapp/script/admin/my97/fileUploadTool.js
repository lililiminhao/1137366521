/*
 * @auto Super
 * @data 2014/2/13 
 * 图片上传工具
 */


/**
 * 该方法不可覆盖原图
 * @param {type} callBackFunction  上传成功后执行的回调函数
 * @returns {undefined}
 */
function fileUploadInit(callBackFunction) {
    //初始化上传文件的控件
    $('input[name="FileUpload"]').each(function() {
        var thiz = $(this);
        thiz.omFileUpload({
            action: '/image/uploadImage.php',
            swf: '/operamask/swf/om-fileupload.swf',
            fileExt: '*.jpg;*.png;*.gif;*.jpeg;*.bmp',
            fileDesc: 'Image Files',
            autoUpload: true,
            sizeLimit: 500 * 1024,
            onComplete: function(ID, fileObj, response, data, event)
            {
                var d = $.parseJSON(response);
                if (d.isOk)
                {
                    //上传成功执行回调函数
                    callBackFunction(d);
                } else {
                    $.omMessageBox.alert({
                        type: 'error',
                        title: '错误',
                        content: d.message
                    });
                }
            }
        });
    });
}



