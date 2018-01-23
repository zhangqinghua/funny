package com.funny.controller.admin;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import com.funny.service.ImageService;
import com.funny.service.TagService;
import com.funny.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
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
                       String status,
                       @RequestParam(defaultValue = "1") int pno,
                       @RequestParam(defaultValue = "5") int psize) throws Exception {
        Page<Image> page = imageService.findAll((root, query, cb) -> {
            /*
             * 连接查询条件, 不定参数，可以连接0..N个查询条件
             */
            List<Predicate> predicates = new ArrayList<>();


            if (!Utils.isEmpty(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            query.where(predicates.toArray(new Predicate[predicates.size()]));
            query.orderBy(cb.desc(root.get("createTime"))); // 根据更新时间排序，最旧的最优选

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
        model.addAttribute("pno", pno);
        model.addAttribute("totalRecords", page.getTotalElements());
        model.addAttribute("total", page.getTotalPages());
        model.addAttribute("hrefFormer", "list");
        model.addAttribute("params", "status=" + status);
        model.addAttribute("status", status);

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
    public String update(Image image) throws Exception {
        imageService.save(image);
        return "forward:list";
    }

    @RequestMapping("/delete")
    public String delete(Long id) throws Exception {
        imageService.delete(id);
        return "forward:list";
    }

    /**
     * 修改为未审核状态
     */
    @RequestMapping("/unCheck")
    public String unCheck(Long id) throws Exception {
        // 将图片修改为未审核状态
        Image image = imageService.findOne(id);
        image.setStatus(0);
        imageService.save(image);
        return "forward:list";
    }

    /**
     * 修改为上架状态
     */
    @RequestMapping("/onLine")
    public String onLine(Long id) throws Exception {
        // 将图片修改为未审核状态
        Image image = imageService.findOne(id);
        image.setStatus(2);
        imageService.save(image);
        return "forward:list";
    }

    /**
     * 修改为下架状态
     */
    @RequestMapping("/offLine")
    public String offLine(Long id) throws Exception {
        // 将图片修改为未审核状态
        Image image = imageService.findOne(id);
        image.setStatus(1);
        imageService.save(image);
        return "forward:list";
    }
}
