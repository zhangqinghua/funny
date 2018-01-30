/**
 * 修改图片标签
 * @param obj 标签节点
 */
var changeTag = function (obj) {
    if (obj.className.indexOf("active") >= 0) {
        $(obj).removeClass("active");
    } else {
        $(obj).addClass("active");
    }
    utils.request("/changeTag", {imageId: $(obj).attr("imageid"), tagId: $(obj).attr("tagid")})
};

/**
 * 动态加载图片
 */

window.onscroll = function () {
    var imgs = $(".content img");

    for (var i = 0, l = imgs.length; i < l; i++) {
        var img = imgs[i];
        if ($(img).attr("src")) {
            continue;
        }
        //检查oLi是否在可视区域
        var t = document.documentElement.clientHeight + (document.documentElement.scrollTop || document.body.scrollTop);
        var h = utils.getH(img);
        if (h < t) {
            $(img).attr("src", $(img).attr("data_src"));
            $(img).css("opacity", "0");
            $(img).animate({'opacity': 1}, 150);
        }
    }
};
window.onload = function () {
    window.onscroll();
};

var upvote = function (i, id, type) {
    utils.request("upvote", {imageId: id, type: type});
    var span = $(i).parent().find("span");
    $(span).text(parseInt(span.text()) + 1);
};

