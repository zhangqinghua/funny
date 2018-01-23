package com.funny.controller.admin;

import com.funny.service.ImageService;
import com.funny.service.WeixinService;
import com.funny.utils.Utils;
import com.funny.vo.ErrorInfo;
import com.funny.vo.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;
    @Autowired
    private ImageService imageService;


    @RequestMapping("/getMaterialcount")
    @ResponseBody
    public ErrorInfo<JSON> getMaterialcount() throws Exception {
        ErrorInfo<JSON> errorInfo = new ErrorInfo<>();

        JSON result = weixinService.getMaterialcount();
        if (Utils.isEmpty(result.getStr("errcode"))) {
            errorInfo.setStatus(true);
            errorInfo.setMsg("获取素材总数成功！");
            errorInfo.setData(result);
        } else {
            errorInfo.setMsg("获取素材总数失败！" + result.getStr("errmsg"));
        }
        return errorInfo;
    }

    /**
     * 获取素材列表
     *
     * @param type  素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
     * @param pno   第几页，
     * @param psize 每页大小
     * @return 素材管理页面
     * @throws Exception 查询素材失败，token_access失败
     */
    @RequestMapping("/batchgetMaterial")
    public String batchgetMaterial(Model model,
                                   @RequestParam(defaultValue = "news") String type,
                                   @RequestParam(defaultValue = "1") int pno,
                                   @RequestParam(defaultValue = "10") int psize) throws Exception {


        // 获取素材数据
        JSON result = weixinService.batchgetMaterial(type, pno, psize);

        // 素材类型 news：图文素材 image：图片素材
        model.addAttribute("type", type);
        // 素材集合
        model.addAttribute("items", result.getList("item"));


        /*
         * 分页参数
         * pno: 第几页
         * totalRecords: 总数
         * total: 总页数
         * hrefFormer: 分页链接
         * params: &type=image 附带参数
         */
        model.addAttribute("pno", pno);
        model.addAttribute("totalRecords", result.getStr("total_count"));
        model.addAttribute("total", Integer.parseInt(result.getStr("total_count")) / psize + (Integer.parseInt(result.getStr("total_count")) / psize > 0 ? 1 : 0));
        model.addAttribute("hrefFormer", "batchgetMaterial");
        model.addAttribute("params", "&type=image");


        return "admin/weixin/batchgetMaterial";
    }

    /**
     * 新增永久图文素材
     */
    @RequestMapping(value = "/addNews", method = RequestMethod.GET)
    public String addNews() throws Exception {
        return "admin/weixin/addNews";
    }

    @RequestMapping(value = "/addNews", method = RequestMethod.POST)
    public String addNews(HttpServletRequest request) throws Exception {
        JSON article = new JSON();
        article.put("thumb_media_id", request.getParameter("thumb_media_id")); // 图片素材id
        article.put("title", request.getParameter("title")); // 标题
        article.put("author", request.getParameter("author")); // 作者
        article.put("digest", request.getParameter("digest")); // 图文消息的摘要
        article.put("show_cover_pic", "0"); // 不显示封面
        article.put("content", request.getParameter("content")); // 图文消息的具体内容
        article.put("content_source_url", "1"); // 图文消息的原文地址，即点击“阅读原文”后的URL

        JSON articles = new JSON();
        articles.put("articles[0]", article.getObj());

        weixinService.addNews(articles);

        return "redirect:addNews";
    }

    @RequestMapping("/updateNews")
    @ResponseBody
    public String updateNews(String media_id, int index) throws Exception {
        JSON result = weixinService.updateNews(media_id, index);
        return result.toString();
    }

    /**
     * 新增其他类型永久素材
     */
    @RequestMapping(value = "/addMaterial", method = RequestMethod.POST)
    public String addMaterial(@RequestParam(defaultValue = "image") String type, MultipartFile media) throws Exception {
        weixinService.addMaterial(type, media);
        return "redirect:batchgetMaterial?type=" + type;
    }

    /**
     * 删除永久素材
     */
    @RequestMapping("/delMaterial")
    public String delMaterial(@RequestParam(defaultValue = "image") String type, String mediaId) throws Exception {
        JSON result = weixinService.delMaterial(mediaId);
        if (!result.getStr("errcode").equals("0")) {
            throw new Exception("删除永久素材失败：" + result.getStr("errmsg"));
        }
        return "redirect:batchgetMaterial?type=" + type;
    }


}
