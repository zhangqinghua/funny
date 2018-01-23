utils = {
    request: function (url, params, callback, isLocal) {
        $.ajax({
            type: 'POST',
            url: url,
            data: params,
            dataType: "json",
            success: function (data) {
                if (callback) {
                    callback(params, data);
                }

            },
            error: function (request) {
                if (callback) {
                    callback(params, request.responseText)
                }
            }
        });
    },

    /**
     * 加载模板
     */
    loadHtml: function (params, callbackData) {
        if (callbackData == null) {
            this.request(params.url, params, this.loadHtml, true)
            return
        }

        var target = document.getElementById(params.id);
        target.innerHTML = callbackData;

        // 查找出所有的js
        var scripts = callbackData.match(/<script([\S\s]*?)<\/script>/gi);
        for (var index in scripts) {
            var srcMatch = scripts[index].match(/src=[\'\"]?([^\'\"]*)[\'\"]?/i)
            if (srcMatch != null && srcMatch.length >= 2) {
                utils.loadJs(srcMatch[1])
            } else {
                var contentMatch = scripts[index].match(/<script>([\S\s]*?)<\/script>/i)
                if (contentMatch != null && contentMatch.length >= 2) {
                    utils.loadJsBySource(contentMatch[1])
                }
            }
        }
    },

    getParams: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    },
    getNum: function (text) {
        var value = text.replace(/[^0-9]/ig, "");
        return value;
    },

    /**
     * 纠正微信图片url不可显示问题
     * @param imgUrl http://mmbiz.qpic.cn/mmbiz_jpg/dwibhGiazgK4Kb15YdOjYTHhvfoYZSs0LMAf3ibE5RLgU1yLOAk0vFRsjW81SUOZnewpslYuErhwfFzuQ5ZWAu81g/0?wx_fmt=jpeg
     * @return http://mmbiz.qpic.cn/mmbiz_jpg/dwibhGiazgK4Kb15YdOjYTHhvfoYZSs0LMAf3ibE5RLgU1yLOAk0vFRsjW81SUOZnewpslYuErhwfFzuQ5ZWAu81g/0.jpeg
     */
    invalidWeixinImage: function (imgUrl) {
        if (!imgUrl) {
            return imgUrl;
        }
        var newImgUrl = imgUrl.substring(0, imgUrl.lastIndexOf("=") - 7) + "." + imgUrl.substring(imgUrl.lastIndexOf("=") + 1, imgUrl.length)
        // var newImgUrl = imgUrl.substring(0, imgUrl.lastIndexOf("=") - 7);
        return newImgUrl;
    },

    /**
     * 获得对象距离页面顶端的距离
     * @param obj 对象
     * @return {number} 距离屏幕的距离
     */
    getH: function (obj) {

        var h = 0;
        while (obj) {
            h += obj.offsetTop;
            obj = obj.offsetParent;
        }
        return h;
    }
};
