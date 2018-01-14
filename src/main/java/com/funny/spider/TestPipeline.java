package com.funny.spider;

import com.funny.entity.Image;
import com.funny.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

@Component
public class TestPipeline implements Pipeline {

    @Autowired
    private ImageService imageService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<String> urls = (List<String>) resultItems.get("urls");
        List<String> names = (List<String>) resultItems.get("names");


        for (int i = 0; i < urls.size(); i++) {
            try {
                Image image = new Image();
                image.setUrl(urls.get(i));
                image.setDescription(names.get(i));
                image.setStatus(1);
                image.setStar(2);
                imageService.save(image);
            } catch (Exception e) {
                System.out.println("保存图片失败：" + e.toString());
            }
        }
    }
}
