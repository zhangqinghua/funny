package com.funny.controller;

import com.funny.entity.Image;
import com.funny.entity.Status;
import com.funny.entity.Tag;
import com.funny.entity.Type;
import com.funny.service.ImageService;
import com.funny.service.TagService;
import com.funny.utils.Utils;
import com.funny.vo.ErrorInfo;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private TagService tagService;

    /**
     * @param model       数据模型
     * @param description 图片名称，模糊查询
     * @param tagId       标签ID
     * @param type        图片类型
     * @param pno         第几页
     * @param psize       每页大小
     * @return 首页
     * @throws Exception 查询失败
     */
    @RequestMapping({"/", "/index"})
    public String index(Model model,
                        String description,
                        String tagId,
                        @RequestParam(defaultValue = "GIF") Type type,
                        @RequestParam(defaultValue = "1") int pno,
                        @RequestParam(defaultValue = "5") int psize) throws Exception {

        Page<Image> page = imageService.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!Utils.isEmpty(description)) {
                predicates.add(cb.like(root.get("description"), "%" + description + "%"));
            }
            if (!Utils.isEmpty(tagId)) {
                predicates.add(cb.equal(root.joinList("tags").get("id"), tagId));
            }
            if (type != Type.JOKE) {
                predicates.add(cb.isNotNull(root.get("url")));
            }

            predicates.add(cb.equal(root.get("type"), type));
            predicates.add(cb.equal(root.get("status"), Status.ONLINE));

            query.where(predicates.toArray(new Predicate[predicates.size()]));
            query.orderBy(cb.desc(root.get("updateTime")));
            return null;
        }, new PageRequest(pno - 1, psize));


        model.addAttribute("images", page.getContent());

        List<Tag> tags = (List<Tag>) tagService.findAll();
        model.addAttribute("tags", tags);
        /*
         * 分页参数
         * pno: 第几页
         * totalRecords: 总数
         * total: 总页数
         * hrefFormer: 分页链接
         * params: &type=image 附带参数
         */
        model.addAttribute("totalRecords", page.getTotalElements());
        model.addAttribute("total", page.getTotalPages());
        model.addAttribute("hrefFormer", "/index");
        model.addAttribute("params", String.format("&description=%s&tagId=%s&type=%s", description, tagId, type));
        model.addAttribute("title", type == Type.GIF ? "GIF趣图" : type == Type.IMAGE ? "今日囧图" : "内涵段子");

        model.addAttribute("description", description);
        model.addAttribute("tagId", tagId);
        model.addAttribute("type", type);
        model.addAttribute("pno", pno);
        model.addAttribute("psize", psize);

        return "index/index";
    }

    @RequestMapping(value = "/changeTag")
    @ResponseBody
    public ErrorInfo<JSONObject> changeTag(Long imageId, Long tagId) {
        ErrorInfo<JSONObject> errorInfo = new ErrorInfo<>();
        try {
            if (imageId == null || tagId == null) {
                throw new Exception(String.format("参数错误，imageId(%s)，tagId(%s)", imageId, tagId));
            }

            Image image = imageService.findOne(imageId);
            if (image == null) {
                throw new Exception("不存在的图片：" + imageId);
            }
            Tag tag = tagService.findOne(tagId);
            if (tag == null) {
                throw new Exception("不存在的标签：" + imageId);
            }

            if (image.getTags().contains(tag)) {
                image.getTags().remove(tag);
            } else {
                image.getTags().add(tag);
            }

            imageService.save(image);
            errorInfo.setStatus(true);
            errorInfo.setMsg("修改标签成功");
        } catch (Exception e) {
            errorInfo.setMsg("修改标签失败，" + e.toString());
        }
        return errorInfo;
    }

    @RequestMapping(value = "/upvote")
    @ResponseBody
    public ErrorInfo<JSONObject> upvote(Long imageId, @RequestParam(defaultValue = "upvote") String type) {
        ErrorInfo<JSONObject> errorInfo = new ErrorInfo<>();
        try {
            Image image = imageService.findOne(imageId);
            if (image == null) {
                throw new Exception(String.format("图片（%s）不存在", imageId + ""));
            }

            if ("upvote".equals(type)) {
                image.setUpvote(image.getUpvote() == null ? 1 : image.getUpvote() + 1);
            } else {
                image.setDownvote(image.getDownvote() == null ? 1 : image.getDownvote() + 1);
            }

            imageService.saveWithoutUpdateTime(image);

            errorInfo.setStatus(true);
            errorInfo.setMsg("点赞成功");
        } catch (Exception e) {
            errorInfo.setMsg("点赞失败，" + e.toString());
        }
        return errorInfo;
    }
}
