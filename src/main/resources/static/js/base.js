utils = {
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
    }

};
