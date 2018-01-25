package com.funny.controller;

import com.funny.entity.Image;
import com.funny.entity.Joke;
import com.funny.entity.Tag;
import com.funny.service.ImageService;
import com.funny.service.JokeService;
import com.funny.service.TagService;
import com.funny.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private JokeService jokeService;
    @Autowired
    private TagService tagService;

    @RequestMapping({"/", "/index"})
    public String index(Model model,
                        @RequestParam(defaultValue = "gif") String type,
                        @RequestParam(defaultValue = "1") int pno,
                        @RequestParam(defaultValue = "5") int psize) throws Exception {

        Page<Image> page = imageService.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if ("image".equals(type)) {
                predicates.add(cb.notEqual(root.get("suffix"), "gif"));
            } else {
                predicates.add(cb.equal(root.get("suffix"), "gif"));
            }



            predicates.add(cb.equal(root.get("status"), 2));
            predicates.add(cb.isNotNull(root.get("url")));

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
        model.addAttribute("pno", pno);
        model.addAttribute("totalRecords", page.getTotalElements());
        model.addAttribute("total", page.getTotalPages());
        model.addAttribute("hrefFormer", "/index");
        model.addAttribute("params", "&type=" + type);
        model.addAttribute("type", type);

        return "index/index";
    }

    @RequestMapping("/joke")
    public String joke(Model model,
                       @RequestParam(defaultValue = "1") int pno,
                       @RequestParam(defaultValue = "5") int psize) throws Exception {
        Page<Joke> page = jokeService.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), 2));
            query.where(predicates.toArray(new Predicate[predicates.size()]));

            query.orderBy(cb.desc(root.get("updateTime")));
            return null;
        }, new PageRequest(pno - 1, psize));
        model.addAttribute("jokes", page.getContent());

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
        model.addAttribute("hrefFormer", "/joke");
        model.addAttribute("params", "");

        return "index/joke";
    }
}
