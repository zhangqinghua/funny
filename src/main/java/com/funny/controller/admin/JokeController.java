package com.funny.controller.admin;

import com.funny.entity.Joke;
import com.funny.entity.Tag;
import com.funny.service.JokeService;
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
@RequestMapping("admin/joke")
public class JokeController {
    @Autowired
    private JokeService jokeService;
    @Autowired
    private TagService tagService;

    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(defaultValue = "1") int pno,
                       @RequestParam(defaultValue = "10") int psize) throws Exception {

        Page<Joke> page = jokeService.findAll(new PageRequest(pno - 1, psize));
        model.addAttribute("jokes", page.getContent());
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

        return "admin/joke/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) throws Exception {
        List<Tag> tags = (List<Tag>) tagService.findAll();
        model.addAttribute("tags", tags);
        return "admin/joke/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(Joke joke) throws Exception {
        jokeService.save(joke);
        return "redirect:list";
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update(Model model, Long id) throws Exception {
        Joke joke = jokeService.findOne(id);
        model.addAttribute("joke", joke);
        List<Tag> tags = (List<Tag>) tagService.findAll();
        model.addAttribute("tags", tags);
        return "admin/joke/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(Joke joke) throws Exception {
        jokeService.save(joke);
        return "redirect:list";
    }
}
