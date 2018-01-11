package com.funny.service;

import com.funny.entity.Image;
import com.funny.entity.Tag;
import com.funny.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class ImageService extends BaseService<Image> {



    @Override
    public Image save(Image image) throws Exception {
        // 如果一个list的一个元素为null，则会报错，需要将其移除
        for (int i = 0; image.getTags() != null && i < image.getTags().size(); i++) {
            Tag tag = image.getTags().get(i);
            if (tag.getId() == null) {
                image.getTags().remove(tag);
                i--;
            }
        }

        // 图片后缀名
        image.setSuffix(Utils.getSuffix(image.getUrl()));

        // 图片大小
        image.setSize(Utils.imageSize(image.getUrl()));





        return super.save(image);
    }


}
