/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
(function(){
    var sw = window.screen.width;
    var sh = window.screen.height;
    var cd = window.screen.colorDepth;
    
    var website = undefined;
    var pageType = undefined;
    var pageId = undefined;
    var userType = undefined;
    var userId = undefined;
    
    var metas = document.getElementsByTagName('meta');
    for (var i = 0; i < metas.length; ++ i)
    {
        var nm = metas[i].getAttribute('name');
        var ct = metas[i].getAttribute('content');
        if (nm !== undefined && nm !== null)
        {
            nm  = nm.toLowerCase();
            if (nm === 'website')
            {
                var tmp = parseInt(ct);
                if (!isNaN(tmp))
                {
                    website = tmp;
                }
            } else if (nm === 'pagetype') {
                pageType    = ct;
            } else if (nm === 'pageid') {
                var tmp = parseInt(ct);
                if (!isNaN(tmp))
                {
                    pageId  = tmp;
                }
            } else if (nm === 'usertype') {
                userType    = ct;
            } else if (nm === 'userid') {
                var tmp = parseInt(ct);
                if (!isNaN(tmp))
                {
                    userId  = tmp;
                }
            }
        }
    }
    if (pageType === undefined || pageType === '') {
        pageType    = 'other';
    }
    var url = 'http://tj.limofang.cn/log.php?sw=' + sw 
            + '&sh=' + sh + '&cd=' + cd + '&pt=' + pageType;
    if (website !== undefined)
    {
        url += '&wid=' + website;
    }
    if (pageId !== undefined)
    {
        url += '&pid=' + pageId;
    }
    if (userType !== undefined)
    {
        url += '&ut=' + userType;
    }
    if (userId !== undefined)
    {
        url += '&uid=' + userId;
    }
    url += '&rdm=' + Math.random();
    var img = new Image;
    img.src = url;
})();