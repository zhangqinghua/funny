package com.funny.controller.admin;


import com.funny.entity.Tag;
import com.funny.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("admin/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @RequestMapping("/list")
    public String list(Model model) throws Exception {
        List<Tag> tags = (List<Tag>) tagService.findAll();
        model.addAttribute("tags", tags);
        return "admin/tag/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        return "admin/tag/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(Tag tag) throws Exception {
        tagService.save(tag);
        return "redirect:list";
    }

    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update(Model model, Long id) throws Exception {
        Tag tag = tagService.findOne(id);

        model.addAttribute("tag", tag);

        return "admin/tag/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(Tag tag) throws Exception {
        tagService.save(tag);
        return "redirect:list";
    }

    @RequestMapping("/delete")
    public String delete(Long id) throws Exception {
        tagService.delete(id);
        return "redirect:list";
    }
}
