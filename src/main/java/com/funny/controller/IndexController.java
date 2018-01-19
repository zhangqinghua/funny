package com.funny.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private TagService tagService;

    @RequestMapping({"/", "/index"})
    public String index(Model model,
                        @RequestParam(defaultValue = "1") int pno,
                        @RequestParam(defaultValue = "5") int psize) throws Exception {

        Page<Image> page = imageService.findAll((root, query, cb) -> {
            query.where(
                    cb.equal(root.get("status"), 2),
                    cb.isNotNull(root.get("url")),
                    cb.equal(root.get("suffix"), "gif"));
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
        model.addAttribute("pno", pno);
        model.addAttribute("totalRecords", page.getTotalElements());
        model.addAttribute("total", page.getTotalPages());
        model.addAttribute("hrefFormer", "/index");
        model.addAttribute("params", "1=1");

        return "index";
    }
}
