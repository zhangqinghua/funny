package com.funny.service;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import com.funny.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class ImageService extends BaseService<Image> {
    @Override
    public <S extends Image> S save(S s) throws Exception {
        preSave(Collections.singletonList(s), true);
        return super.save(s);
    }

    @Override
    public <S extends Image> Iterable<S> save(Iterable<S> iterable) throws Exception {
        preSave((List<Image>) iterable, true);
        return super.save(iterable);
    }

    public void saveWithoutUpdateTime(Image image) throws Exception {
        preSave(Collections.singletonList(image), false);
        super.save(image);
    }


    private void preSave(List<Image> images, boolean updateTime) {
        for (Image image : images) {
            // 如果一个list的一个元素为null，则会报错，需要将其移除
            for (int i = 0; image.getTags() != null && i < image.getTags().size(); i++) {
                Tag tag = image.getTags().get(i);
                if (tag.getId() == null) {
                    image.getTags().remove(tag);
                    i--;
                }
            }

            // 图片大小
            if (image.getType() != Image.Type.JOKE) {
                image.setSize(Utils.imageSize(image.getUrl()));
            } else {
                image.setSize(image.getDescription().length());
            }

            // 是否要更新修改时间
            if (updateTime) {
                image.setUpdateTime(new Date());
            }
        }
    }

}
