package com.funny.controller.admin;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import com.funny.service.ImageService;
import com.funny.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("admin/image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private TagService tagService;


    @RequestMapping("/list")
    public String list(Model model,
                        @RequestParam(defaultValue = "1") int pno,
                        @RequestParam(defaultValue = "10") int psize) throws Exception {
        Page<Image> page = imageService.findAll(new PageRequest(pno - 1, psize));
        model.addAttribute("images", page.getContent());

        /*
         * 分页参数
         * pno: 第几页
         * totalRecords: 总数
         * total: 总页数
         * hrefFormer: 分页链接
         * params: &type=image 附带参数
         */
        model.addAttribute("pno", pno);
        model.addAttribute("totalRecords", page.getTotalElements());
        model.addAttribute("total", page.getTotalPages());
        model.addAttribute("hrefFormer", "list");
        model.addAttribute("params", "");

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
        return "redirect:list";
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update(Model model, long id) throws Exception {

        Image image = imageService.findOne(id);
        List<Tag> tags = (List<Tag>) tagService.findAll();

        model.addAttribute("image", image);
        model.addAttribute("tags", tags);

        return "admin/image/update";
    }

    @RequestMapping("/update")
    public String update(Image image) throws Exception {

        imageService.save(image);
        return "redirect:list";
    }

    @RequestMapping("/delete")
    public String delete(Long id) throws Exception {
        imageService.delete(id);
        return "redirect:list";
    }

}
