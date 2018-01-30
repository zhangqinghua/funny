package com.funny.controller.admin;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.Predicate;
import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("admin/image")
public class AdminImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private TagService tagService;

    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(defaultValue = "GIF") Type type,
                       @RequestParam(defaultValue = "ONLINE") Status status,
                       @RequestParam(defaultValue = "1") int pno,
                       @RequestParam(defaultValue = "5") int psize) throws Exception {
        Page<Image> page = imageService.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!Utils.isEmpty(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            predicates.add(cb.equal(root.get("type"), type));
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            query.orderBy(cb.desc(root.get("updateTime")));
            return null;
        }, new PageRequest(pno - 1, psize));


        model.addAttribute("images", page.getContent());

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
        model.addAttribute("hrefFormer", "list");
        model.addAttribute("params", String.format("&type=%s&status=%s", type, status));
        model.addAttribute("title", type == Type.GIF ? "GIF趣图" : type == Type.IMAGE ? "今日囧图" : "内涵段子");

        model.addAttribute("status", status);
        model.addAttribute("type", type);
        model.addAttribute("pno", pno);
        model.addAttribute("psize", psize);
        return "admin/image/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) throws Exception {
        List<Tag> tags = (List<Tag>) tagService.findAll();

        model.addAttribute("tags", tags);

        return "admin/image/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(Image image) throws Exception {


        imageService.save(image);
        return "forward:list";
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update(Model model, long id) throws Exception {

        Image image = imageService.findOne(id);
        List<Tag> tags = (List<Tag>) tagService.findAll();

        model.addAttribute("image", image);
        model.addAttribute("tags", tags);

        return "admin/image/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(Image updateImage) throws Exception {
        Image image = imageService.findOne(updateImage.getId());
        image.setUrl(updateImage.getUrl());
        image.setDescription(updateImage.getDescription());
        imageService.save(image);
        return "forward:list";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ErrorInfo<JSONObject> delete(Long id) throws Exception {
        ErrorInfo<JSONObject> errorInfo = new ErrorInfo<>();
        try {
            imageService.delete(id);
            errorInfo.setStatus(true);
            errorInfo.setMsg("删除成功");
        } catch (Exception e) {
            errorInfo.setMsg("删除失败，" + e.toString());
        }
        imageService.delete(id);
        return errorInfo;
    }

    /**
     * 修改图片状态
     */
    @RequestMapping("/changeStatus")
    @ResponseBody
    public ErrorInfo<JSONObject> changeStatus(Long id, Status status) {
        ErrorInfo<JSONObject> errorInfo = new ErrorInfo<>();
        try {
            Image image = imageService.findOne(id);
            image.setStatus(status);
            imageService.save(image);
            errorInfo.setStatus(true);
            errorInfo.setMsg("修改状态成功");
        } catch (Exception e) {
            errorInfo.setMsg("修改状态失败，" + e.toString());
        }
        return errorInfo;
    }
}
