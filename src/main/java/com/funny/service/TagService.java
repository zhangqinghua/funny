package com.funny.service;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagService extends BaseService<Tag> {

    @Autowired
    private ImageService imageService;

    @Override
    public void delete(Long id) throws Exception {
        Tag tag = super.findOne(id);

        for (Image image : tag.getImages()) {
            image.getTags().remove(tag);
        }

        imageService.save(tag.getImages());

        super.delete(id);
    }
}
