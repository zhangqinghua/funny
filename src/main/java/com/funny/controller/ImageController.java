package com.funny.controller;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import com.funny.service.ImageService;
import com.funny.service.TagService;
import com.funny.vo.ErrorInfo;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private TagService tagService;


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
}
